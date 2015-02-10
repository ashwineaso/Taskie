package in.altersense.taskapp.common;

/**
 * Holds most configuration of the app.
 * Created by mahesmohan on 1/29/15.
 */
public class Config {

    public static int DATABASE_VERSION = 1;
    public static int REQUEST_MAXOUT = 3;
    public static String SERVER_ADDRESS = "172.16.11.31:8080";
    public static String RESPONSE_STATUS_FAILED = "failed";
    public static String RESPONSE_STATUS_SUCCESS = "success";
    public static String TOKEN_EXPIRED_ERROR = "Access Token Invalid";
    public static String APP_SECRET = "AppSecret";
    public static String APP_KEY = "AppKey";
    public static String REQUEST_TIMED_OUT_ERROR = "Request Timed Out";
    public static int CONNECTION_TIMEOUT = 10000;

    public static enum MESSAGES {

        REGISTRATION_REQUEST("Registering user. Please wait."),
        LOGIN_REQUEST("Signing in.");

        private String messaage;

        public String getMessage() {
            return messaage;
        }

        private MESSAGES(String messaage) {
            this.messaage = messaage;
        }
    }

    /**
     * Shared preference keys for the app.
     */
    public static enum SHARED_PREF_KEYS {
        ACCESS_TOKEN("access_token"),
        REFRESH_TOKEN("refresh_token"),
        OWNER_ID("ownerID"), // UUID of the owner.
        OWNER_NAME("ownerName"); // Name to be displayed for owner.

        private String key;

        public String getKey() {
            return key;
        }

        private SHARED_PREF_KEYS(String key) {
            this.key = key;
        }

    }

    public static enum REQUEST_RESPONSE_KEYS {

        EMAIL("email"),
        NAME("name"),
        PASSWORD("password"),
        STATUS("status"),
        REFRESH_TOKEN("refresh_token"),
        ACCESS_TOKEN("acess_token");

        private String key;

        public String getKey() {
            return key;
        }

        private REQUEST_RESPONSE_KEYS(String key) {
            this.key = key;
        }
    }

}
