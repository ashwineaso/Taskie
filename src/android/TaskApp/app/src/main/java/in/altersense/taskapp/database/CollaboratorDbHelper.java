package in.altersense.taskapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.models.Collaborator;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.models.User;

/**
 * Created by mahesmohan on 2/17/15.
 */
public class CollaboratorDbHelper extends SQLiteOpenHelper {

    private static String CLASS_TAG = "CollaboratorDbHelper ";

    private static String CREATION_STATEMENT = "CREATE TABLE " + Collaborator.TABLE_NAME + " ( " +
            Collaborator.KEYS.TASK_ROWID.getName() + " " + Collaborator.KEYS.TASK_ROWID.getType() + ", " +
            Collaborator.KEYS.TASK_UUID.getName() + " " + Collaborator.KEYS.TASK_UUID.getType() + ", " +
            Collaborator.KEYS.USER_ROWID.getName() + " " + Collaborator.KEYS.USER_ROWID.getType() + ", " +
            Collaborator.KEYS.USER_UUID.getName() + " " + Collaborator.KEYS.USER_UUID.getType() + ");";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATION_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: Move to a better database upgradation logic.
        db.execSQL("DROP TABLE IF EXISTS " + Collaborator.TABLE_NAME);
        db.execSQL(CREATION_STATEMENT);
    }

    public CollaboratorDbHelper(Context context) {
        super(context, Collaborator.TABLE_NAME, null, Config.DATABASE_VERSION);
    }

    public boolean addCollaborator(Task task, User user) {
        String TAG = CLASS_TAG+"addCollaborator";
        // Open a writable database.
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        Log.d(TAG, "Opened a writable database");
        // Prepare the query.
        ContentValues values = new ContentValues();
        values.put(Collaborator.KEYS.TASK_UUID.getName(),task.getUuid());
        values.put(Collaborator.KEYS.TASK_ROWID.getName(),task.getId());
        values.put(Collaborator.KEYS.USER_UUID.getName(),user.getUuid());
        values.put(Collaborator.KEYS.USER_ROWID.getName(),user.getId());
        Log.d(TAG, "Content values to set :"+values);
        // Execute query.
        long result = writableDatabase.insert(
                Collaborator.TABLE_NAME,
                null,
                values
        );
        Log.d(TAG, "Inserted to row "+result);
        // Close db
        writableDatabase.close();
        // return query execution status.
        return (result!=-1);
    }

    public boolean removeCollaborator(Task task, User user) {
        String TAG = CLASS_TAG+"removeCollaborator";
        // Open a writable database.
        SQLiteDatabase writableDb = this.getWritableDatabase();
        Log.d(TAG, "Opened writable db");
        // Delete row.
        int result = writableDb.delete(
                Collaborator.TABLE_NAME,
                "TASK_ROWID =? AND" + "USER_ROWID =?",
                new String[] {task.getId()+"", user.getId()+""}
        );
        Log.d(TAG, "Rows affected: "+result);
        // Close database.
        writableDb.close();
        // Return action status.
        return (result>0);
    }
}
