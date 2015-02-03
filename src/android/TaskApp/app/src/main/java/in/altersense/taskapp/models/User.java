package in.altersense.taskapp.models;

import android.content.Context;
import android.database.Cursor;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.database.UserDbHelper;

/**
 * Created by mahesmohan on 1/31/15.
 */
public class User {
    public int getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public boolean isDeviceOwner() {
        return isDeviceOwner;
    }

    private int id;
    private String uuid;
    private String email;
    private String name;
    private boolean isDeviceOwner;

    /**
     * Table name for Users
     */
    public static String TABLE_NAME = "UserTable";

    /**
     * Contructor with cursor.
     * @param cursor Cursor with User data.
     */
    public User(Cursor cursor) {
        this(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3)
        );
    }

    public User(String userUUID, Context context) {
        UserDbHelper userDbHelper = new UserDbHelper(context);
        User newUser = userDbHelper.getUserByUUID(userUUID);
        this.id = newUser.getId();
        this.uuid = newUser.getUuid();
        this.email = newUser.getEmail();
        this.name = newUser.getEmail();
        this.isDeviceOwner = newUser.isDeviceOwner();
    }

    /**
     * Table structure for Users
     */
    public static enum KEYS {
        UUID("uuid", "TEXT"),
        EMAIL("email", "TEXT"),
        NAME("name", "TEXT");

        public String getName() {
            return name;
        }

        private final String name;

        public String getType() {
            return type;
        }

        private final String type;

        private KEYS(
                String name,
                String type
        ) {
            this.name = name;
            this.type = type;
        }

    }

    public User(
            int id,
            String uuid,
            String email,
            String name
            ) {
        this.id = id;
        this.uuid = uuid;
        this.email = email;
        this.name = name;
    }

    /**
     * Makes the user the device Owner.
     * @param context Current context to change the SharedPreferences.
     * @return Returns an instance of the current user.
     */
    public User makeDeviceOwner(Context context) {
        // Check if this owner is owner
        if(!this.isDeviceOwner) {
            // If not check if a another deviceOwner exists
            String devOwnerId = AltEngine.readStringFromSharedPref(
                    context,
                    Config.SHARED_PREF_KEYS.OWNER_ID.getKey(),
                    ""
            );
            // If device owner exist compare UUIDs
            if(!devOwnerId.equals(this.uuid)) {
                // If not same uuid remove owner privileges of the user
                AltEngine.writeStringToSharedPref(
                        context,
                        Config.SHARED_PREF_KEYS.OWNER_ID.getKey(),
                        this.uuid
                );
                AltEngine.writeStringToSharedPref(
                        context,
                        Config.SHARED_PREF_KEYS.OWNER_NAME.getKey(),
                        this.name
                );
            }
            // set this user as device owner.
            this.isDeviceOwner =true;
        }
        return this;
    }

    public static User getDeviceOwner(Context context) {
        UserDbHelper userDbHelper = new UserDbHelper(context);
        String deviceOwnerUUID = AltEngine.readStringFromSharedPref(
                context,
                Config.SHARED_PREF_KEYS.OWNER_ID.getKey(),
                ""
        );
        User deviceOwner = userDbHelper.getUserByUUID(deviceOwnerUUID);
        deviceOwner.isDeviceOwner = true;
        return deviceOwner;
    }

}
