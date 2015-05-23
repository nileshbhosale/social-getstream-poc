package socialapis.service;

import antlr.StringUtils;
import client.StreamClientImpl;
import io.getstream.client.StreamClient;
import io.getstream.client.config.ClientConfiguration;
import io.getstream.client.config.StreamRegion;
import io.getstream.client.exception.StreamClientException;
import io.getstream.client.model.activities.AggregatedActivity;
import io.getstream.client.model.activities.SimpleActivity;
import io.getstream.client.model.feeds.Feed;
import io.getstream.client.model.filters.FeedFilter;
import io.getstream.client.service.AggregatedActivityServiceImpl;
import io.getstream.client.service.FlatActivityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import socialapis.domain.Connection;
import socialapis.domain.Tweet;
import socialapis.domain.User;
import socialapis.util.Constants;

import java.io.IOException;
import java.util.List;

/**
 * Created by Nilesh Bhosale
 */
@Component("getStream")
public class GetStream {


    private StreamClient streamClient;

    /**
     * GetStream Constructor
     * Set the APIKEY, APISECRET and StreamRegion
     * Create a new client for Stream using the configuration
     */
    @Autowired
    public GetStream(@Value("${getstream.key}") String apiKey,
                     @Value("${getstream.secret}") String apiSecret ) {
        ClientConfiguration streamConfig = new ClientConfiguration();
        streamConfig.setRegion(StreamRegion.AP_SOUTH_EAST);
        streamClient = new StreamClientImpl(streamConfig, apiKey, apiSecret);
    }



    public void addToPublicList(User user) throws IOException, StreamClientException {
        Feed publicFeed= streamClient.newFeed("user",Constants.PUBLICFEEDNAME );
        Feed userFeed= streamClient.newFeed("user",String.valueOf(user.getId()));
        publicFeed.follow("user",String.valueOf(user.getId()) );



    }

    public Tweet tweetPublic(Tweet tweet) throws IOException, StreamClientException {
        // Instantiate a feed object
        Feed feed = streamClient.newFeed("user", String.valueOf(tweet.getUser().getId()));
        tweet.setId(addActivity(feed,tweet).getId());
        return tweet;
    }

    public Tweet tweetPrivate(Tweet tweet) throws IOException, StreamClientException {
        // Instantiate a feed object
        Feed feed = streamClient.newFeed("flat", String.valueOf(tweet.getUser().getId()));
        tweet.setId(addActivity(feed,tweet).getId());
        return tweet;
    }

    private SimpleActivity addActivity( Feed feed,Tweet tweet) throws IOException, StreamClientException {

        // Add an activity to the feed, where actor, object and target are references to objects (`Alessandro`, `Fuerteventura`, `Places to Visit`)
        SimpleActivity activity = new SimpleActivity();
        activity.setActor("User:"+tweet.getUser().getId());
        activity.setObject(tweet.getTweet());
        activity.setVerb("tweet");
        activity.setTarget("Profile");
        FlatActivityServiceImpl<SimpleActivity> flatActivityService =
                feed.newFlatActivityService(SimpleActivity.class);

        SimpleActivity response = flatActivityService.addActivity(activity);
        return response;
    }

    public void follow(Connection connection) throws IOException, StreamClientException {
        Feed feed = streamClient.newFeed("flat", String.valueOf(connection.getFollowerId()));
        Feed aggregated = streamClient.newFeed("aggregated", String.valueOf(connection.getFollowerId()));
        Feed notification = streamClient.newFeed("notification", String.valueOf(connection.getFollowerId()));


        feed.follow("flat",String.valueOf(connection.getFolloweeId()));
        aggregated.follow("flat",String.valueOf(connection.getFolloweeId()));
        notification.follow("flat",String.valueOf(connection.getFolloweeId()));
    }

    public void unFollow(Connection connection) throws IOException, StreamClientException {
        Feed feed = streamClient.newFeed("flat", String.valueOf(connection.getFollowerId()));
        Feed aggregated = streamClient.newFeed("aggregated", String.valueOf(connection.getFollowerId()));
        Feed notification = streamClient.newFeed("notification", String.valueOf(connection.getFollowerId()));


        feed.unfollow("flat",String.valueOf(connection.getFolloweeId()));
        aggregated.unfollow("flat",String.valueOf(connection.getFolloweeId()));
        notification.unfollow("flat",String.valueOf(connection.getFolloweeId()));
    }


    public List<SimpleActivity> getPublicFeeds() throws IOException, StreamClientException {
        // Get activities from 5 to 10 (using offset pagination)
        FeedFilter filter = new FeedFilter.Builder().withLimit(5).withOffset(0).build();
        Feed feed = streamClient.newFeed("user", Constants.PUBLICFEEDNAME);
        feed.getToken();
        // Create an activity service
        FlatActivityServiceImpl<SimpleActivity> flatActivityService =
                feed.newFlatActivityService(SimpleActivity.class);

        List<SimpleActivity> activities = flatActivityService.getActivities(filter).getResults();
        return activities;
  }

    //Currently not working. Posted an email to support team at getstreamio
  /*  public List<AggregatedActivity<SimpleActivity>> getAggregatedFeeds(User user)  throws IOException, StreamClientException{
        // Get activities from 5 to 10 (using offset pagination)
        FeedFilter filter = new FeedFilter.Builder().withLimit(5).withOffset(0).build();
        Feed feed = streamClient.newFeed("flat", String.valueOf(user.getId()));

        // Create an activity service
        AggregatedActivityServiceImpl<SimpleActivity> aggregatedActivityService =
                feed.newAggregatedActivityService(SimpleActivity.class);

        List<AggregatedActivity<SimpleActivity>> activities = aggregatedActivityService.getActivities(filter).getResults();
        return activities;
    }*/

    public List<SimpleActivity> getFlatFeeds(User user)  throws IOException, StreamClientException{
        // Get activities from 5 to 10 (using offset pagination)
        FeedFilter filter = new FeedFilter.Builder().withLimit(5).withOffset(0).build();
        Feed feed = streamClient.newFeed("flat", String.valueOf(user.getId()));

        // Create an activity service
        FlatActivityServiceImpl<SimpleActivity> flatActivityService =
                feed.newFlatActivityService(SimpleActivity.class);

        List<SimpleActivity> activities = flatActivityService.getActivities(filter).getResults();
        return activities;
    }


}
