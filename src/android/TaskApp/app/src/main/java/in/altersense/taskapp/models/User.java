package in.altersense.taskapp.models;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;

import java.util.Random;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.database.UserDbHelper;
import in.altersense.taskapp.requests.RegisterUserRequest;

/**
 * Created by mahesmohan on 1/31/15.
 */
public class User {

    public String getPassword() {
        return password;
    }

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
    private String password;

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
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2)
        );
    }

    public User(String userUUID, Activity activity) {
        UserDbHelper userDbHelper = new UserDbHelper(activity.getApplicationContext());
        User newUser = userDbHelper.getUserByUUID(userUUID);
        this.id = newUser.getId();
        this.uuid = newUser.getUuid();
        this.email = newUser.getEmail();
        this.name = newUser.getEmail();
        this.password = "";
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
            String uuid,
            String email,
            String name
            ) {
        this.uuid = uuid;
        this.email = email;
        this.name = name;
        this.password = "";
    }

    public User() {
        this.uuid = "";
    }

   public void registerUser(
           String name,
           String email,
           String password,
           Activity activity
   ) {
       // Set up the user object with name, email, password.
       this.name = name;
       this.email = email;
       this.password = password;
       // Call the register user API.
       RegisterUserRequest registerUserRequest = new RegisterUserRequest(
               this,
               activity
       );
       registerUserRequest.execute();
   }

    /*
   public boolean loginUser(String email, String password) {
       // Call the login user API
       // If login success.
        //refresh user and task database
        // make user owner
        // sync tasks.
        // sync collaborators.
        // return true
       // If login fails.
        // display message
        // return false
  }
*/
    public User(String name, String email) {
        Random rand = new Random(1000);
        int uuid = rand.nextInt();

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

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
