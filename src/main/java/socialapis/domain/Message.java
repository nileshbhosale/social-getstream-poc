package socialapis.domain;

import io.getstream.client.model.activities.BaseActivity;
import lombok.Data;

/**
 * Created by Nilesh Bhosale on 13/6/15.
 */
@Data
public class Message extends BaseActivity{
    private Long toUser;
    private Long fromUser;
    private String message;
}
