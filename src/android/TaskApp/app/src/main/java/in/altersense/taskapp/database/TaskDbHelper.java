package in.altersense.taskapp.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.CreateTaskActivity;
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.models.Task;

/**
 * Created by mahesmohan on 2/1/15.
 */
public class TaskDbHelper extends SQLiteOpenHelper {

    private static final String CLASS_TAG = "TaskDbHelper ";
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

    public void updateUUID(Task task) {
        String TAG = CLASS_TAG+"updateUUID";
        Log.d(TAG, "Setting UUID for task: "+task.toString());
        // Open a writable database.
        SQLiteDatabase writableDb = this.getWritableDatabase();
        Log.d(TAG, "Writable database opened.");
        // Setup data to be updated.
        ContentValues values = new ContentValues();
        values.put(Task.KEYS.UUID.getName(), task.getUuid());
        // Update the record.
        writableDb.update(
                Task.TABLE_NAME,
                values,
                "ROWID ="+task.getId(),
                null
        );
        Log.d(TAG, "Row updated with uuid "+task.getUuid());
        // close the database.
        writableDb.close();
    }

    public Task createTask(Task newTask, Activity activity) {
        String TAG = CLASS_TAG+"createTask";
        // Open a writable database
        Log.d(TAG, "Writable database opened.");
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
        Log.d(TAG, "Content values set. "+ values.toString());
        // Insert into database
        long rowId = database.insert(
                Task.TABLE_NAME,
                null,
                values
        );
        Log.d(TAG, "Query run db inserted to row "+rowId+".");
        database.close();
        Task task = getTaskByRowId(rowId, activity);
        task.setId(rowId);
        return task;
    }

    private Task getTaskByRowId(long rowId, Activity activity) {
        String TAG = CLASS_TAG+"getTaskByRowId";
        // Open database.
        Log.d(TAG, "Readable database opened.");
        SQLiteDatabase readableDb = this.getReadableDatabase();
        // Setup columns
        ArrayList<String> columnList = Task.getAllColumns();
        String[] columns = new String[columnList.size()];
        columns = columnList.toArray(columns);
        // Fetch Task with matching row Id.
        Cursor selfCursor = readableDb.query(
                Task.TABLE_NAME,
                columns,
                "ROWID =?",
                new String[] {
                        rowId+""
                },
                null,
                null,
                null
        );
        selfCursor.moveToFirst();
        String cursorString = new String();
        for(int i=0;i<selfCursor.getColumnCount();i++) {
            cursorString+=i+":"+selfCursor.getString(i)+", ";
        }
        Log.d(TAG, "Cursor: "+cursorString);
        Task task = new Task(
                selfCursor,
                activity
        );
        task.setId(rowId);
        readableDb.close();
        return task;
    }

    /**
     * Gets a list of all non group tasks.
     * @param activity The current activity.
     * @return A list of Task objects.
     */
    public List<Task> getAllNonGroupTasks(Activity activity) {
        String TAG = CLASS_TAG+"getAllNonGroupTasks";
        // Open database.
        Log.d(TAG, "Readable database opened.");
        SQLiteDatabase readableDb = this.getReadableDatabase();
        // Create a list of tasks.
        List<Task> taskList = new ArrayList<Task>();
        // List all the non group tasks.
        // Setup columns
        ArrayList<String> columnList = Task.getAllColumns();
        String[] columns = new String[columnList.size()];
        columns = columnList.toArray(columns);
        Cursor resultCursor = readableDb.query(
                Task.TABLE_NAME,
                columns,
                Task.KEYS.IS_GROUP.getName()+"=?",
                new String[] {"0"},
                null,
                null,
                null
        );
        Log.d(TAG, "Returned "+resultCursor.getCount()+" rows.");
        if(resultCursor.moveToFirst()) {
            do {
                taskList.add(new Task(resultCursor, activity));
                Log.d(TAG, "Added task to list.");
            } while(resultCursor.moveToNext());
        }
        // Close database
        readableDb.close();
        // Return the list.
        return taskList;
    }

    public Task getTaskByUUID(String taskUUID, Activity activity) {
        String TAG = CLASS_TAG+"getTaskByUUID";
        // Open readable database.
        SQLiteDatabase readableDb = this.getReadableDatabase();
        Log.d(TAG, "Readable db opened.");
        // Prepare columns list.
        ArrayList<String> columnList = Task.getAllColumns();
        String[] columns = new String[columnList.size()];
        columns = columnList.toArray(columns);
        Log.d(TAG, "Columns prepped..");
        // Prepare and execute query.
        Cursor cursor = readableDb.query(
                Task.TABLE_NAME,
                columns,
                Task.KEYS.UUID.getName()+"=?",
                new String[] { taskUUID },
                null,
                null,
                null
        );
        Log.d(TAG, "Returned "+cursor.getCount()+" rows.");
        // Close database.
        readableDb.close();
        // Fetch task from the cursor
        cursor.moveToFirst();
        Log.d(TAG, "Creating new task from the cursor.");
        Task task = new Task(cursor, activity);
        // Return the task.
        return task;
    }

    public void updateTask(Task task) {
        String TAG = CLASS_TAG+"updateTask";
        // Open writable database.
        SQLiteDatabase writableDb = this.getWritableDatabase();
        Log.d(TAG, "Writable database opened.");
        // Set up updation content value.
        ContentValues values = new ContentValues();
        values.put(Task.KEYS.UUID.getName(), task.getUuid());
        values.put(Task.KEYS.NAME.getName(), task.getName());
        values.put(Task.KEYS.DESCRIPTION.getName(), task.getDescription());
        values.put(Task.KEYS.OWNER_UUID.getName(), task.getOwner().getUuid());
        values.put(Task.KEYS.PRIORITY.getName(), task.getPriority());
        values.put(Task.KEYS.DUE_DATE_TIME.getName(), task.getDueDateTime());
        values.put(Task.KEYS.STATUS.getName(), task.getStatus());
        values.put(Task.KEYS.IS_GROUP.getName(), task.getIntIsGroup());
        if(task.isGroup()) {
            values.put(Task.KEYS.GROUP_UUID.getName(), task.getGroup().getUuid());
        }
        Log.d(TAG, "Content values set. "+ values.toString());
        // Call update query.
        int affected = writableDb.update(
                Task.TABLE_NAME,
                values,
                "ROWID =?",
                new String[] { task.getId()+"" }
        );
        Log.d(TAG, "Update called and affected "+affected+" row.");
        // Close database.
        writableDb.close();
        Log.d(TAG, "Db closed.");
    }
}
