package com.callisto;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterBuilder {
	private static TwitterBuilder instance = null;
	private Twitter twitter = null;
	
	protected TwitterBuilder() 
	{

	}

	public static TwitterBuilder getInstance() 
	{
		if (instance == null) 
		{
			instance = new TwitterBuilder();
			instance.twitter = initTwitter();
		}
		return instance;
	}
	
	public static TwitterBuilder getNewInstance() 
	{
		instance = new TwitterBuilder();
		instance.twitter = initTwitter();
		return instance;
	}
	
	public Twitter getTwitter()
	{
		return twitter;
	}
	
	private static Twitter initTwitter()
	{
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("xxxxx")
		  .setOAuthConsumerSecret("xxxx")
		  .setOAuthAccessToken("xxxx")
		  .setOAuthAccessTokenSecret("xxxx");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		return twitter;
	}
}
