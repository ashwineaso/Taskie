package in.altersense.taskapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
            Collaborator.KEYS.USER_UUID.getName() + " " + Collaborator.KEYS.USER_UUID.getType() + " , " +
            Collaborator.KEYS.STATUS.getName() + " " + Collaborator.KEYS.STATUS.getType() + ");";
    private Context context;


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
        this.context = context;
    }

    public boolean addCollaborator(Task task, Collaborator user) {
        String TAG = CLASS_TAG+"addCollaborator";
        Log.d(TAG, "Task: "+task.toString());
        Log.d(TAG, "User: "+user.getString());
        // Open a writable database.
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        Log.d(TAG, "Opened a writable database");
        // Prepare the query.
        ContentValues values = new ContentValues();
        values.put(Collaborator.KEYS.TASK_UUID.getName(),task.getUuid());
        values.put(Collaborator.KEYS.TASK_ROWID.getName(),task.getId());
        values.put(Collaborator.KEYS.USER_UUID.getName(),user.getUuid());
        values.put(Collaborator.KEYS.USER_ROWID.getName(),user.getId());
        values.put(Collaborator.KEYS.STATUS.getName(),user.getStatus());
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
                "TASK_ROWID =? AND " + "USER_ROWID =?",
                new String[] {task.getId()+"", user.getId()+""}
        );
        Log.d(TAG, "Rows affected: "+result);
        // Close database.
        writableDb.close();
        // Return action status.
        return (result>0);
    }

    /**
     * Fetches all the collaborators of the task.
     * @param task The seed task.
     * @return List of Collaborator
     */
    public List<Collaborator> getAllCollaborators(Task task) {
        String TAG = CLASS_TAG+"getAllCollaborators";
        // Open readable db
        SQLiteDatabase readableDb = this.getReadableDatabase();
        Log.d(TAG, "Readable db opened.");
        // Prepare query
        String[] columns = Collaborator.getAllColumns();
        // Execute query
        Cursor result = readableDb.query(
                Collaborator.TABLE_NAME,
                columns,
                Collaborator.KEYS.TASK_ROWID+"=?",
                new String[] {task.getId()+""},
                null,
                null,
                null
        );
        Log.d(TAG, "Returned "+result.getCount()+" rows.");
        result.moveToFirst();
        List<Collaborator> collaboratorList = new ArrayList<Collaborator>();
        UserDbHelper userDbHelper = new UserDbHelper(this.context);
        try {
            do {
                // Fetch collaborator from db
                Log.d(TAG, "Fetch collaborator from db.");
                Collaborator collaborator = new Collaborator(userDbHelper.getUserByRowId(result.getLong(2)));
                // Set collaborator status
                Log.d(TAG, "Set collaborator status.");
                collaborator.setStatus(result.getInt(4));
                // Make list
                collaboratorList.add(collaborator);
            }while (result.moveToNext());
            // Close db
            readableDb.close();
            // Return list
            return collaboratorList;
        } catch (CursorIndexOutOfBoundsException e) {
            return collaboratorList;
        }
    }

    public boolean updateStatus(Task task, Collaborator collaborator) {
        String TAG = CLASS_TAG+"updateStatus";
        // Open writable database.
        SQLiteDatabase writableDb = this.getWritableDatabase();
        // Make query.
        ContentValues values = new ContentValues();
        values.put(Collaborator.KEYS.STATUS.getName(), collaborator.getStatus());
        // Execute query
        int affectedRows = writableDb.update(
                Collaborator.TABLE_NAME,
                values,
                Collaborator.KEYS.TASK_ROWID.getName()+" =? " +
                        Collaborator.KEYS.USER_ROWID.getName()+" =?",
                new String[] {
                        task.getId()+"",
                        collaborator.getId()+""
                }
        );
        // Close db.
        writableDb.close();
        // Return status.
        return (affectedRows>0);
    }
}