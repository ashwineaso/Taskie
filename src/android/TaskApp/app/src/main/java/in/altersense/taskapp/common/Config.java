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
    public static String RESPONSE_STATUS_SUCCESS = "success";
    public static String TOKEN_EXPIRED_ERROR = "Access Token Invalid";
    public static String REQUEST_TIMED_OUT_ERROR = "Request Timed Out";
    public static int CONNECTION_TIMEOUT = 10000;

    public static enum MESSAGES {

        REGISTRATION_REQUEST("Registering user. Please wait."),
        LOGIN_REQUEST("Signing in."),
        LOGIN_ERROR_TITLE("Oops");

        private String message;

        public String getMessage() {
            return message;
        }

        private MESSAGES(String message) {
            this.message = message;
        }
    }

    /**
     * Shared preference keys for the app.
     */
    public static enum SHARED_PREF_KEYS {
        ACCESS_TOKEN("access_token"),
        REFRESH_TOKEN("refresh_token"),
        OWNER_ID("ownerID"), // UUID of the owner.
        OWNER_NAME("ownerName"),// Name to be displayed for owner.
        APP_SECRET("devOwnerPassword"),
        APP_KEY("devOwnerEmail");

        private String key;

        public String getKey() {
            return key;
        }

        private SHARED_PREF_KEYS(String key) {
            this.key = key;
        }

    }

    public static enum REQUEST_ERROR_CODES {
        TOKEN_NOT_FOUND(3001),
        ACCESS_TOKEN_INVALID(3002),
        REFRESH_TOKEN_INVALID(3003),
        ACCESS_TOKEN_EXPIRED(3004);

        public int getCode() {
            return code;
        }

        private int code;

        private REQUEST_ERROR_CODES(int errorCode) {
            this.code = errorCode;
        }
    }

    public static enum REQUEST_RESPONSE_KEYS {

        EMAIL("email"),
        NAME("name"),
        PASSWORD("password"),
        STATUS("status"),
        REFRESH_TOKEN("refresh_token"),
        ACCESS_TOKEN("access_token"),
        DATA("data"),
        MESSAGE("message"),
        UUID("id"),
        OWNER("owner"),
        TASK_NAME("name"),
        TASK_COLLABOATORS("collaborators");

        private String key;

        public String getKey() {
            return key;
        }

        private REQUEST_RESPONSE_KEYS(String key) {
            this.key = key;
        }
    }

}
