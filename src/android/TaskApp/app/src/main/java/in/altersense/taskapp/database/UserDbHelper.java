package in.altersense.taskapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.models.User;

/**
 * Created by mahesmohan on 1/31/15.
 */
public class UserDbHelper extends SQLiteOpenHelper {

    private static String CREATION_STATEMENT = "CREATE TABLE " + User.TABLE_NAME + " ( " +
            User.KEYS.UUID.getName() + " " + User.KEYS.UUID.getType() + ", " +
            User.KEYS.EMAIL.getName() + " " + User.KEYS.EMAIL.getType() + ", " +
            User.KEYS.NAME.getName() + " " + User.KEYS.NAME.getType() + ");";

    public UserDbHelper(Context context) {
        super(context, User.TABLE_NAME, null, Config.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                CREATION_STATEMENT
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: Move to a better database upgradation logic.
        db.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME);
        db.execSQL(CREATION_STATEMENT);
    }

    /**
     * Fetches a User object with the supplied UUID
     * @param uuid The UUID of the user to be fetched.
     * @return An instance of User
     */
    public User getUserByUUID(String uuid) {
        // Open database.
        SQLiteDatabase readableDb = this.getReadableDatabase();
        // Fetch image with matching uuid.
        String[] columns = new String[] {
                "*"
        };
        String whereClause = User.KEYS.UUID.getName()+"=?";
        String[] whereArgs = new String[] {
                uuid
        };
        // Fetch the result of the query.
        Cursor selfCursor = readableDb.query(
                User.TABLE_NAME,
                columns,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        selfCursor.moveToFirst();
        // Create a User object from the cursor
        User user = new User(selfCursor);
        // Close database
        readableDb.close();
        return user;
    }

    /**
     * Creates a new User in database and returns it.
     * @param newUser The userObject to be created.
     * @return An instance of User
     */
    public User createUser(User newUser) {
        // Open a writable database
        SQLiteDatabase database = this.getWritableDatabase();
        // Setup data to be written
        ContentValues values = new ContentValues();
        values.put(User.KEYS.UUID.getName(), newUser.getUuid());
        values.put(User.KEYS.NAME.getName(), newUser.getName());
        values.put(User.KEYS.EMAIL.getName(), newUser.getEmail());
        // Insert into database
        long rowId = database.insert(
                User.TABLE_NAME,
                null,
                values
        );
        database.close();
        User user = getUserByRowId(rowId);
        return user;
    }

    /**
     * Fetches a user by RowId from the database
     * @param rowId The row Id in the database
     * @return An instance of User type.
     */
    public User getUserByRowId(long rowId) {
        // Open database.
        SQLiteDatabase readableDb = this.getReadableDatabase();
        // Fetch user with matching row Id.
        String query = "SELECT * FROM "+User.TABLE_NAME+" WHERE ROWID = "+rowId+";";
        Cursor selfCursor = readableDb.rawQuery(query, null);
        selfCursor.moveToFirst();
        User user = new User(selfCursor);
        readableDb.close();
        return user;
    }
}
