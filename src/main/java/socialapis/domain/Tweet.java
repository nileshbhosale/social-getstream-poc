package socialapis.domain;

import lombok.Data;

/**
 * Created by Nilesh Bhosale
 */
@Data
public class Tweet {
    private String id;

    private String tweet;

    private User user;

}
