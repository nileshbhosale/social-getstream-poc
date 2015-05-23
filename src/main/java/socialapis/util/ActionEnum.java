package socialapis.util;

/**
 * Created by Nilesh Bhosale
 */
public enum ActionEnum {
    PUBLIC("public"),   AGGREGATED("aggregated"),  FLAT("flat"), PRIVATE("private");

    private String action;

    ActionEnum(String action) {
        this.action = action;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
