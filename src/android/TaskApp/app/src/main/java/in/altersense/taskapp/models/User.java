package in.altersense.taskapp.models;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.database.UserDbHelper;
import in.altersense.taskapp.requests.RegisterUserRequest;

/**
 * Created by mahesmohan on 1/31/15.
 */
public class User {

    private static String CLASS_TAG = "User";

    public User(String uuid, String email, String name, int id) {
        this(uuid,email,name);
        this.id = id;
    }

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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    private boolean syncStatus;

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
        this.setSyncStatus(cursor.getInt(3));
        this.id = cursor.getInt(4);
    }

    public User(String userUUID, Context context) {
        String TAG = CLASS_TAG+" Constructor(uuid,activity)";
        Log.d(TAG, "uuid: "+ userUUID);
        UserDbHelper userDbHelper = new UserDbHelper(context);
        User newUser = userDbHelper.getUserByUUID(userUUID);
        Log.d(TAG, "Fetched user.");
        this.id = newUser.getId();
        this.uuid = newUser.getUuid();
        this.email = newUser.getEmail();
        this.name = newUser.getName();
        this.password = "";
        this.isDeviceOwner = newUser.isDeviceOwner();
        this.syncStatus = newUser.getSyncStatus();
        Log.d(TAG, "User setup complete.");
    }

    public static String[] getAllColumns() {
        ArrayList<String> columnList = new ArrayList<String>();
        for(KEYS key: KEYS.values()) {
            columnList.add(key.getName());
        }
        columnList.add("ROWID");
        String[] columns = new String[columnList.size()];
        columns = columnList.toArray(columns);
        return columns;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Table structure for Users
     */
    public static enum KEYS {
        UUID("uuid", "TEXT"),
        EMAIL("email", "TEXT"),
        NAME("name", "TEXT"),
        SYNC_STATUS("sync_status", "INTEGER");

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
        if(name==null) {
            this.name = email;
        } else {
            this.name = name;
        }
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

    /**
     * For logging in user with email and password.
     * @param email String email of the user.
     * @param password String password of the user.
     */
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * For addding unknown user as a collaborator.
     * @param email String Email of the user.
     */
    public User(String email) {
        this.email = email;
        this.name = email;
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

    public boolean getSyncStatus() {
        if(syncStatus) return true;
        return false;
    }

    public int getSyncStatusAsInt() {
        if(syncStatus) return 1;
        return 0;
    }

    public void setSyncStatus(boolean syncStatus) {
        this.syncStatus = syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus==1 ? true : false;
    }

    @Override
    public String toString() {
        return this.email;
    }

    public String getString() {
        String string = "";
        string+=" id="+this.id;
        string+=" name="+this.name;
        string+=" email="+this.email;
        string+=" uuid="+this.uuid;
        return string;
    }

    public String getInitials() {
        try{
            String[] nameTerms = this.getName().split(" ");
            String initials = "";
            initials+=nameTerms[0].substring(0,1);
            if(nameTerms.length>1) {
                initials+=nameTerms[nameTerms.length-1].substring(0,1);
            }
            return initials.toUpperCase();
        } catch (NullPointerException e) {
            return "??";
        }
    }

    /**
     * Checks for a particular user in the list and removes it from the list.
     * @param list List of users to be checked
     * @param userToBeCheckedForRemoval The user to be removed
     * @return The updated list.
     */
    public static List<User> removeUserFromList(List<User> list, User userToBeCheckedForRemoval) {
        String TAG = CLASS_TAG+"removeUserFromList";
        int positionToBeRemoved=-1;
        for(int ctr=0;ctr<list.size();ctr++) {
            User userToBeChecked = list.get(ctr);
            if(userToBeChecked.getEmail().equals(userToBeCheckedForRemoval.getEmail())) {
                positionToBeRemoved=ctr;
            }
        }
        if(positionToBeRemoved!=-1) {
            list.remove(positionToBeRemoved);
            Log.d(TAG, "Removed user: "+userToBeCheckedForRemoval.toString());
        }
        return list;
    }
}
