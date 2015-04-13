package in.altersense.taskapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.models.User;

/**
 * Created by mahesmohan on 1/31/15.
 */
public class UserDbHelper extends SQLiteOpenHelper {

    private final Context context;

    private static String CREATION_STATEMENT = "CREATE TABLE " + User.TABLE_NAME + " ( " +
            User.KEYS.UUID.getName() + " " + User.KEYS.UUID.getType() + ", " +
            User.KEYS.EMAIL.getName() + " " + User.KEYS.EMAIL.getType() + ", " +
            User.KEYS.NAME.getName() + " " + User.KEYS.NAME.getType() + ", "+
            User.KEYS.SYNC_STATUS.getName() + " " + User.KEYS.SYNC_STATUS.getType() + ");";
    private static String CLASS_TAG = "UserDbHelper ";

    public UserDbHelper(Context context) {
        super(context, User.TABLE_NAME, null, Config.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                CREATION_STATEMENT
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
            case 2:
                db.execSQL(
                        "ALTER TABLE "+ User.TABLE_NAME+
                                " ADD "+User.KEYS.SYNC_STATUS.getName()+" "+
                                User.KEYS.SYNC_STATUS.getType()+";");
        }
    }

    /**
     * Fetches a User object with the supplied UUID
     * @param uuid The UUID of the user to be fetched.
     * @return An instance of User
     */
    public User getUserByUUID(String uuid) {
        String TAG = CLASS_TAG+"getUserByUUID";
        // Open database.
        Log.d(TAG, "Set up a readable database");
        SQLiteDatabase readableDb = this.getReadableDatabase();
        // Fetch user with matching uuid.
        String whereClause = User.KEYS.UUID.getName()+"=?";
        String[] whereArgs = new String[] {
                uuid
        };
        Log.d(TAG, "Query set up with uuid: "+uuid);
        // Fetch the result of the query.
        Cursor selfCursor = readableDb.query(
                User.TABLE_NAME,
                User.getAllColumns(),
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        Log.d(TAG, "Query executed. "+selfCursor.getCount()+" rows returned.");
        if(selfCursor.moveToFirst()) {
            do {
                Log.d(TAG, "Users with same UUID("+selfCursor.getString(0)+"): "+selfCursor.getString(2));
                String cursorString = "";
                for(int i=0;i<selfCursor.getColumnCount();i++) {
                    cursorString+=i+": "+selfCursor.getString(i)+" ";
                }
                Log.d(TAG, "User: "+cursorString);
            } while(selfCursor.moveToNext());
        }
        selfCursor.moveToFirst();
        if(selfCursor.getCount()==0) {
            return null;
        }
        // Create a User object from the cursor
        User user = new User(selfCursor);
        // Close database
        readableDb.close();
        return user;
    }

    /**
     * Fetches a User object with the supplied Email
     * @param email The Email of the user to be fetched.
     * @return An instance of User
     */
    public User retrieve(String email) {
        String TAG = CLASS_TAG+"retrieve";
        // Open database.
        Log.d(TAG, "Set up a readable database");
        SQLiteDatabase readableDb = this.getReadableDatabase();
        // Fetch user with matching uuid.
        String whereClause = User.KEYS.EMAIL.getName()+"=?";
        String[] whereArgs = new String[] {
                email
        };
        Log.d(TAG, "Query set up with retrieve: "+email);
        // Fetch the result of the query.
        Cursor selfCursor = readableDb.query(
                User.TABLE_NAME,
                User.getAllColumns(),
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        Log.d(TAG, "Query executed. "+selfCursor.getCount()+" rows returned.");
        if(selfCursor.moveToFirst()) {
            do {
                Log.d(TAG, "Users with same UUID("+selfCursor.getString(0)+"): "+selfCursor.getString(2));
                String cursorString = "";
                for(int i=0;i<selfCursor.getColumnCount();i++) {
                    cursorString+=i+": "+selfCursor.getString(i)+" ";
                }
                Log.d(TAG, "User: "+cursorString);
            } while(selfCursor.moveToNext());
        }
        selfCursor.moveToFirst();
        if(selfCursor.getCount()==0) {
            return null;
        }
        // Create a User object from the cursor
        User user = new User(selfCursor);
        // Close database
        readableDb.close();
        return user;
    }

    /**
     * Lists all users for collaborator addition purposes.
     * Purposely omits the device owner to be removed from the list.
     * @return
     */
    public List<User> listAllUsers() {
        String TAG = CLASS_TAG+"listAllUsers";
        // Open readable database.
        SQLiteDatabase readableDb = this.getReadableDatabase();
        Log.d(TAG, "Readable database opened");
        // Create array list
        List<User> userList = new ArrayList<User>();
        // Setup columns
        String[] columns = User.getAllColumns();
        // Execute query
        Cursor cursor = readableDb.query(
                User.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );
        Log.d(TAG, "Query returned "+cursor.getCount()+" rows");
        cursor.moveToFirst();
        int UUIDColNum = cursor.getColumnIndex(
                User.KEYS.UUID.getName()
        );
        String ownerUUID = AltEngine.readStringFromSharedPref(
                this.context,
                Config.SHARED_PREF_KEYS.OWNER_ID.getKey(),
                "");
        do {
            if(!cursor.getString(UUIDColNum).equals(ownerUUID)) {
                // Add each user to the list if user is not the owner.
                userList.add(new User(cursor));
            }
        } while (cursor.moveToNext());
        Log.d(TAG, "Readable db closed.");
        readableDb.close();
        return userList;
    }

    /**
     * Checks whether a user exist by the name or email and returns the user.
     * @param nameEmail Name or email of the user.
     * @return Instance of user object or null if no matching user found
     */
    public User getUserByNameEmail(String nameEmail) {
        String TAG = CLASS_TAG+"getUserByNameEmail";
        User user = null;
        // Open a readable database.
        SQLiteDatabase readableDb = this.getReadableDatabase();
        // Setup columns.
        String[] columns = User.getAllColumns();
        // Make query with name
        Cursor cursor = readableDb.query(
                User.TABLE_NAME,
                columns,
                User.KEYS.NAME.getName()+"=?",
                new String[] { nameEmail+"%" },
                null,
                null,
                null
        );
        // Check cursor size if less than 1
        if(cursor.getCount()<1) {
            // Make query with email
            cursor.moveToFirst();
            cursor = readableDb.query(
                    User.TABLE_NAME,
                    columns,
                    User.KEYS.EMAIL.getName()+"=?",
                    new String[] { nameEmail+"%" },
                    null,
                    null,
                    null
            );
        }
        if(cursor.getCount()>0) {
            // Create user from the cursor
            cursor.moveToFirst();
            user = new User(cursor);
        }
        // Close db
        readableDb.close();
        // Return user
        return user;
    }

    /**
     * Creates a new User in database and returns it.
     * @param newUser The userObject to be created.
     * @return An instance of User
     */
    public User createUser(User newUser) {
        String TAG = CLASS_TAG+"createUser";
        Log.d(TAG, "User: "+newUser.toString());
        // Open a writable database
        SQLiteDatabase database = this.getWritableDatabase();
        Log.d(TAG, "Set up a readable database");
        // Setup data to be written
        ContentValues values = new ContentValues();
        values.put(User.KEYS.UUID.getName(), newUser.getUuid());
        values.put(User.KEYS.NAME.getName(), newUser.getName());
        values.put(User.KEYS.EMAIL.getName(), newUser.getEmail());
        values.put(User.KEYS.SYNC_STATUS.getName(), newUser.getSyncStatusAsInt());
        Log.d(TAG, "Set up a content values.");
        // Insert into database
        long rowId = database.insert(
                User.TABLE_NAME,
                null,
                values
        );
        Log.d(TAG, "Inserted to row: "+rowId);
        database.close();
        newUser.setId(rowId);
        return newUser;
    }

    /**
     * Fetches a user by RowId from the database
     * @param rowId The row Id in the database
     * @return An instance of User type.
     */
    public User getUserByRowId(long rowId) {
        String TAG = CLASS_TAG+"getUserByRowId";
        // Open database.
        Log.d(TAG, "Set up a readable database");
        SQLiteDatabase readableDb = this.getReadableDatabase();
        // Fetch user with matching row Id.
        Cursor selfCursor = readableDb.query(
                User.TABLE_NAME,
                User.getAllColumns(),
                "ROWID =?",
                new String[] {rowId+""},
                null,
                null,
                null
        );
        selfCursor.moveToFirst();
        User user = new User(selfCursor);
        Log.d(TAG, "Fetched user: "+user.getString());
        readableDb.close();
        return user;
    }


    public boolean updateUser(User user) {
        String TAG = CLASS_TAG+"updateUser";
        // Open writable db
        Log.d(TAG, "Writable db opened.");
        SQLiteDatabase writableDb = this.getWritableDatabase();
        // Make query
        ContentValues values = new ContentValues();
        values.put(User.KEYS.NAME.getName(), user.getName());
        values.put(User.KEYS.UUID.getName(), user.getUuid());
        values.put(User.KEYS.SYNC_STATUS.getName(), user.getSyncStatusAsInt());
        Log.d(TAG, "Content values: " + values.toString());
        // Execute query
        int affectedRows = writableDb.update(
                User.TABLE_NAME,
                values,
                User.KEYS.EMAIL.getName()+" =?",
                new String[] {
                        user.getEmail()
                }
        );
        Log.d(TAG, "Query executed and affected "+affectedRows+" row.");
        // Close db.
        writableDb.close();
        // Return status.
        return affectedRows>0;
    }
}
