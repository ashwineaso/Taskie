package in.altersense.taskapp.common;

import android.util.Log;

import java.util.List;

/**
 * Holds most configuration of the app.
 * Created by mahesmohan on 1/29/15.
 */
public class Config {

    public static int DATABASE_VERSION = 1;
    public static int REQUEST_MAXOUT = 3;
    public static String SERVER_ADDRESS = "192.168.0.102:8080";
    public static String RESPONSE_STATUS_FAILED = "failed";
    public static String RESPONSE_STATUS_SUCCESS = "success";
    public static String TOKEN_EXPIRED_ERROR = "Access Token Invalid";
    public static String REQUEST_TIMED_OUT_ERROR = "Request Timed Out";
    public static int CONNECTION_TIMEOUT = 10000;

    public static final int MIN_STATUS = -1;  // Minimum status a task can have.
    public static final int MAX_STATUS = 2;  // Maximum status a task can have.

    public static enum TASK_STATUS {

        DECLINED(-1,"Declined"),
        PENDING(0,"Pending"),
        ACCEPTED(1,"Accepted"),
        COMPLETED(2,"Completed");

        public String getStatusText() {
            return statusText;
        }

        public int getStatus() {
            return status;
        }

        private int status;
        private String statusText;

        private TASK_STATUS(int status, String statusText) {
            this.status = status;
            this.statusText = statusText;
        }
    }

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
        TASK_COLLABOATORS("collaborators"),
        DESCRIPTION("description"),
        DUE_DATE_TIME("dueDateTime"),
        PRIORITY("priority");

        private String key;

        public String getKey() {
            return key;
        }

        private REQUEST_RESPONSE_KEYS(String key) {
            this.key = key;
        }
    }

    public static enum PRIORITY {

        LOW(0,"Low"),
        MEDIUM(1,"Medium"),
        HIGH(2,"High");

        public String getText() {
            return text;
        }

        public static String getText(int value) {
            String TAG = "PRIORITY getText";
            Log.d(TAG, "Value: "+value);
            for(PRIORITY priority:PRIORITY.values()) {
                Log.d(TAG, "Checking "+priority.getText()+"("+priority.getValue()+")");
                if(priority.getValue()==value) {
                    Log.d(TAG,"Found");
                    return priority.getText();
                }
            }
            return "";
        }

        public int getValue() {
            return value;
        }

        private int value;
        private String text;

        private PRIORITY(int value, String text) {
            this.value = value;
            this.text = text;
        }
    }
}
