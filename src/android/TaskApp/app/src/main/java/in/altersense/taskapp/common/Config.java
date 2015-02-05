package in.altersense.taskapp.common;

/**
 * Holds most configuration of the app.
 * Created by mahesmohan on 1/29/15.
 */
public class Config {

    public static int DATABASE_VERSION = 1;

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
