package in.altersense.taskapp.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.models.Task;

/**
 * Created by mahesmohan on 2/1/15.
 */
public class TaskDbHelper extends SQLiteOpenHelper {

    private static String CREATION_STATEMENT = "CREATE TABLE " + Task.TABLE_NAME + " ( " +
            Task.KEYS.UUID.getName() + " " + Task.KEYS.UUID.getType() + ", " +
            Task.KEYS.OWNER_UUID.getName() + " " + Task.KEYS.OWNER_UUID.getType() + ", " +
            Task.KEYS.NAME.getName() + " " + Task.KEYS.NAME.getType() + ", " +
            Task.KEYS.DESCRIPTION.getName() + " " + Task.KEYS.DESCRIPTION.getType() + ", " +
            Task.KEYS.PRIORITY.getName() + " " + Task.KEYS.PRIORITY.getType() + ", " +
            Task.KEYS.DUE_DATE_TIME.getName() + " " + Task.KEYS.DUE_DATE_TIME.getType() + ", " +
            Task.KEYS.STATUS.getName() + " " + Task.KEYS.STATUS.getType() + ", " +
            Task.KEYS.IS_GROUP.getName() + " " + Task.KEYS.IS_GROUP.getType() + ", " +
            Task.KEYS.GROUP_UUID.getName() + " " + Task.KEYS.GROUP_UUID.getType() + ");";

    public TaskDbHelper(Context context) {
        super(context, Task.TABLE_NAME, null, Config.DATABASE_VERSION);
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
        db.execSQL("DROP TABLE IF EXISTS " + Task.TABLE_NAME);
        db.execSQL(CREATION_STATEMENT);
    }

    public Task createTask(Task newTask, Activity activity) {
        // Open a writable database
        SQLiteDatabase database = this.getWritableDatabase();
        // Setup data to be written
        ContentValues values = new ContentValues();
        values.put(Task.KEYS.UUID.getName(), newTask.getUuid());
        values.put(Task.KEYS.NAME.getName(), newTask.getName());
        values.put(Task.KEYS.DESCRIPTION.getName(), newTask.getDescription());
        values.put(Task.KEYS.OWNER_UUID.getName(), newTask.getOwner().getUuid());
        values.put(Task.KEYS.PRIORITY.getName(), newTask.getPriority());
        values.put(Task.KEYS.DUE_DATE_TIME.getName(), newTask.getDueDateTime());
        values.put(Task.KEYS.STATUS.getName(), newTask.getStatus());
        values.put(Task.KEYS.IS_GROUP.getName(), newTask.getIntIsGroup());
        if(newTask.isGroup()) {
            values.put(Task.KEYS.GROUP_UUID.getName(), newTask.getGroup().getUuid());
        }
        // Insert into database
        long rowId = database.insert(
                Task.TABLE_NAME,
                null,
                values
        );
        database.close();
        Task Task = getTaskByRowId(rowId, activity);
        return Task;
    }

    private Task getTaskByRowId(long rowId, Activity activity) {
        // Open database.
        SQLiteDatabase readableDb = this.getReadableDatabase();
        // Fetch Task with matching row Id.
        String query = "SELECT * FROM "+ Task.TABLE_NAME+" WHERE ROWID = "+rowId+";";
        Cursor selfCursor = readableDb.rawQuery(query, null);
        selfCursor.moveToFirst();
        Task task = new Task(
                selfCursor,
                activity
        );
        readableDb.close();
        return task;
    }

    /**
     * Gets a list of all non group tasks.
     * @param activity The current activity.
     * @return A list of Task objects.
     */
    public List<Task> getAllNonGroupTasks(Activity activity) {
        // Open database.
        SQLiteDatabase readableDb = this.getReadableDatabase();
        // Create a list of tasks.
        List<Task> taskList = new ArrayList<Task>();
        // List all the non group tasks.
        String query = "SELECT * FROM "+ Task.TABLE_NAME+
                " WHERE "+ Task.KEYS.IS_GROUP.getName()
                +" = 0;";
        Cursor resultCursor = readableDb.rawQuery(query, null);
        if(resultCursor.moveToFirst()) {
            do {
                taskList.add(new Task(resultCursor, activity));
            } while(resultCursor.moveToNext());
        }
        // Close database
        readableDb.close();
        // Return the list.
        return taskList;
    }
}
