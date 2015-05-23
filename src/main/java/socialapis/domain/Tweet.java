package socialapis.domain;

/**
 * Created by Nilesh Bhosale
 */
public class Tweet {
    private String id;

    private String tweet;

    private User user;

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
