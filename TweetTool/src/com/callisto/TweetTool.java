package com.callisto;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Paging;
import twitter4j.Status;

public class TweetTool 
{
	private boolean filterRetweets;
	private boolean filterConversations;
	private boolean filterLinks;
	private String userName;
	private int statusLimit;
	private int statusPage = 100;
	
	private String linksPattern = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	private String retweetPattern = "(^|\\s)RT\\b";
	private String conversationFilter = "(^|\\s)@\\w+";
	
	public TweetTool(boolean filterRetweets, boolean filterConversations, boolean filterLinks, String userName, int statusLimit)
	{
		this.filterRetweets = filterRetweets;
		this.filterConversations = filterConversations;
		this.filterLinks = filterLinks;
		this.userName = userName;
		this.statusLimit = statusLimit;
	}
	
	public TweetTool(boolean filterRetweets, boolean filterConversations, boolean filterLinks,String userName)
	{
		this(filterRetweets, filterConversations,filterLinks,userName,3200);
	}
	
	public void getTweets() throws Exception
	{
		List<Status> statuses = getStatusList();
		if(this.filterRetweets)
		{
			filterTweets(statuses);
		}
		outputStatusList(statuses);
	}
	
	public List<Status> getStatusList()
	{
		List<Status> statuses = new ArrayList<Status>();
		for(int i = 1; i < (statusLimit/statusPage); i++)
		{
			System.out.println("Fetching statusus " + ((i-1) * 100) + " through " + (((i-1) * 100) +100) + "\n");
			try 
			{
				statuses.addAll(TwitterBuilder.getInstance().getTwitter().getUserTimeline(this.userName, new Paging(i, statusPage)));
			} 
			catch (Exception e) 
			{
				System.out.println("Error getting tweets: " + e.getMessage());
			}
			
		}
		return statuses;
	}
	
	public void filterTweets(List<Status> statusList) 
	{
		ListIterator<Status> statusIterator = statusList.listIterator();
		int filteredRetweets = 0;
		int filteredConversations = 0;
		int filteredLinks = 0;
		while (statusIterator.hasNext())
		{
			Status status = statusIterator.next();
			String tweetText = status.getText().toLowerCase();
			if(this.filterRetweets)
			{
				if (status.isRetweet()) 
				{
					statusIterator.remove();
					filteredRetweets++;
					continue;
				} 
				else 
				{
					Matcher m = Pattern.compile(this.retweetPattern, Pattern.CASE_INSENSITIVE).matcher(tweetText);;
					if (m.find()) 
					{
						statusIterator.remove();
						filteredRetweets++;
						continue;
					}
				}
			}
			
			if(this.filterConversations)
			{
				Matcher m = Pattern.compile(this.conversationFilter, Pattern.CASE_INSENSITIVE).matcher(tweetText);
				if (m.find())
				{
					statusIterator.remove();
					filteredConversations++;
					continue;
				}
			}
			
			if(this.filterLinks)
			{
				Matcher m = Pattern.compile(this.linksPattern,Pattern.CASE_INSENSITIVE).matcher(tweetText);
				if (m.find()) 
				{
					statusIterator.remove();
					filteredLinks++;
				}
			}
		}
		System.out.println("Removed:  " + filteredRetweets + " retweets, "+ filteredLinks + " links, and " + filteredConversations + " conversations\n");
	}
	
	public void outputStatusList(List<Status> statuses) throws IOException 
	{
		if (!statuses.isEmpty()) 
		{
			File tempFile = File.createTempFile(this.userName, ".txt");
			BufferedWriter outputFileWriter;
			try 
			{
				outputFileWriter = new BufferedWriter(new FileWriter(tempFile));
			} 
			catch (Exception e1) 
			{
				System.out.println("Error creating file: " + e1.getMessage());
				return;
			}
			for (Status status : statuses)
			{
				try 
				{
					String statusText = status.getText();
					if (statusText != null && !statusText.isEmpty()) 
					{
						statusText = statusText.replaceAll("\\r\\n|\\r|\\n", "");
						outputFileWriter.append(status.getId() + ",");
						if(!this.filterConversations)
						{
							outputFileWriter.append(status.getInReplyToScreenName() + ",");	
						}
						outputFileWriter.append(statusText + ",");
						outputFileWriter.newLine();
						outputFileWriter.flush();
					}
				}
				catch (Exception e) 
				{
					System.out.println("Error writing tweet: " + e.getMessage());
				}
			}
			
			try 
			{
				System.out.println("File Location: " + tempFile.getAbsolutePath());
				outputFileWriter.flush();
				outputFileWriter.close();
				
			} 
			catch (Exception e1) 
			{
				System.out.println("Error closing Stream: "  + e1.getMessage());
			}
			try 
			{
				Desktop.getDesktop().edit(tempFile);
			}
			catch (Exception e) 
			{
				System.out.println("Error opening in notepad file: " + e.getMessage());
			}
		}
	}
	
	public boolean isFilterRetweets() {
		return filterRetweets;
	}

	public void setFilterRetweets(boolean filterRetweets) {
		this.filterRetweets = filterRetweets;
	}

	public boolean isFilterConversations() {
		return filterConversations;
	}

	public void setFilterConversations(boolean filterConversations) {
		this.filterConversations = filterConversations;
	}

	public boolean isFilterLinks() {
		return filterLinks;
	}

	public void setFilterLinks(boolean filterLinks) {
		this.filterLinks = filterLinks;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
