package socialapis.controller;

import io.getstream.client.exception.StreamClientException;
import io.getstream.client.model.activities.AggregatedActivity;
import io.getstream.client.model.activities.SimpleActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import socialapis.dao.ConnectionDao;
import socialapis.dao.UserDao;
import socialapis.domain.Connection;
import socialapis.domain.Message;
import socialapis.domain.Tweet;
import socialapis.domain.User;
import socialapis.service.GetStream;
import socialapis.util.ActionEnum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nilesh Bhosale
 */
@RestController()
@RequestMapping("/socialapis/v1/")
public class SocialController {
    @Autowired
    private GetStream getStream;

    @Autowired
    private UserDao userDao;


    @Autowired
    private ConnectionDao connectionDao;

    /*
    * Create a new user
    * */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    @ResponseBody
    public User createUser(@RequestBody User user) throws IOException, StreamClientException {
        user =userDao.save(user);
        getStream.addToPublicList(user);
        return user;

    }

    /*
  * Create a new user
  * */
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public List<User> getUserList() throws IOException, StreamClientException {
        return (List<User>) userDao.findAll();

    }



    /**
     * Follow another user
     * @param connection
     * @return
     */
    @RequestMapping(value = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public Connection followUser(@RequestBody Connection connection) throws IOException, StreamClientException {
        connection=  connectionDao.save(connection);
        getStream.follow(connection);
        return connection;
    }

    /**
     * Follow another user
     * @param connection
     * @return
     */
    @RequestMapping(value = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public Connection unFollowUser(@RequestBody Connection connection) throws IOException, StreamClientException {
        connection=connectionDao.getOneByFollowerIdAndFolloweeId(connection.getFollowerId(), connection.getFolloweeId());
     connectionDao.delete(connection.getId());
        getStream.unFollow(connection);
        return connection;
    }


    /**
     * Follow another user
     * @param tweet
     * @return
     */
    @RequestMapping(value = "/tweet", method = RequestMethod.POST)
    @ResponseBody
    public Tweet tweet(@RequestBody Tweet tweet,@RequestParam ActionEnum action) throws IOException, StreamClientException {

        switch(action){
            case PUBLIC:    getStream.tweetPublic(tweet);break;
            case PRIVATE:    getStream.tweetPrivate(tweet);break;
        }

            return tweet;
    }

    /**
     * Get feeds
     * @param userid
     * @param action
     * @return
     */
    @RequestMapping(value = "/feeds/{userid}", method = RequestMethod.GET)
    @ResponseBody
    public List<Tweet> getPublicFeeds(@PathVariable Long userid,@RequestParam ActionEnum action) throws IOException, StreamClientException {
        List tweetList=null;
        User user=userDao.findOne(userid);
        switch (action){
        /*    case AGGREGATED:
                tweetList = getAggregatedTweets(getStream.getAggregatedFeeds(user));break;
         */
            case FLAT:
                tweetList= getTweets(getStream.getFlatFeeds(user));break;
        }
        return tweetList;
    }

    /**
     * Get feeds
     * @param action
     * @return
     */
    @RequestMapping(value = "/feeds", method = RequestMethod.GET)
    @ResponseBody
    public List<Tweet> getPublicFeeds() throws IOException, StreamClientException {
        List tweetList=  getTweets(getStream.getPublicFeeds());
         return tweetList;
    }

    private List<Tweet> getTweets(List<SimpleActivity> publicFeeds) {
        List<Tweet> tweets=new ArrayList<>();
        for(SimpleActivity simpleActivity:publicFeeds){
            Tweet tweet=new Tweet();
            tweet.setTweet(simpleActivity.getObject());
            tweet.setId(simpleActivity.getId());
            tweet.setUser(userDao.findOne(Long.parseLong(
                    simpleActivity.getActor().substring(5))));
            tweets.add(tweet);

        }
        return tweets;
    }
    private List getAggregatedTweets( List<AggregatedActivity<SimpleActivity>> publicFeeds) {
        List activities=new ArrayList<>();


        for(AggregatedActivity<SimpleActivity> activityAggregatedActivity:publicFeeds){
            List<Tweet> tweets=new ArrayList<>();
            for(SimpleActivity simpleActivity:activityAggregatedActivity.getActivities()){
                Tweet tweet=new Tweet();
                tweet.setTweet(simpleActivity.getObject());
                tweet.setId(simpleActivity.getId());
                tweet.setUser(userDao.findOne(Long.parseLong(
                        simpleActivity.getActor().substring(5))));
                tweets.add(tweet);

            }
            activities.add(tweets);
        }
        return activities;
    }

    /**
     * Get public feeds token
     * @return
     */
    @RequestMapping(value = "/feed/token", method = RequestMethod.GET)
    @ResponseBody
    public String getPublicFeedToken() throws IOException, StreamClientException {
        return getStream.getPublicFeedToken();
    }

    /**
     * Sends a message
     * @return
     */
    @RequestMapping(value = "/message/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendMessage(@RequestBody Message message) throws IOException, StreamClientException {

        return getStream.sendMessage(message);
    }

}
