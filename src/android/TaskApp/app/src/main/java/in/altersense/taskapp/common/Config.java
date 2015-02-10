package in.altersense.taskapp.common;

/**
 * Holds most configuration of the app.
 * Created by mahesmohan on 1/29/15.
 */
public class Config {

    public static int DATABASE_VERSION = 1;
    public static int REQUEST_MAXOUT = 3;
    public static String SERVER_ADDRESS = "192.168.1.5:8080";
    public static String RESPONSE_STATUS_FAILED = "failed";
    public static String TOKEN_EXPIRED_ERROR = "Access Token Invalid";
    public static String APP_SECRET = "AppSecret";
    public static String APP_KEY = "AppKey";
    public static String REQUEST_TIMED_OUT_ERROR = "Request Timed Out";
    public static int CONNECTION_TIMEOUT = 10000;

    /**
     * Shared preference keys for the app.
     */
    public static enum SHARED_PREF_KEYS {
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

    public static enum REQUEST_KEYS {

        EMAIL("email"),
        NAME("name"),
        PASSWORD("password");

        private String key;

        public String getKey() {
            return key;
        }

        private REQUEST_KEYS(String key) {
            this.key = key;
        }
    }

}
