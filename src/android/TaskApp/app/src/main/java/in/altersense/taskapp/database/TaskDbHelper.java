package in.altersense.taskapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.models.Buzz;
import in.altersense.taskapp.models.Notification;
import in.altersense.taskapp.models.Task;

/**
 * Created by mahesmohan on 2/1/15.
 */
public class TaskDbHelper extends SQLiteOpenHelper {

    private static final String CLASS_TAG = "TaskDbHelper ";

    private final Context context;

    private static String CREATION_STATEMENT_TASK = "CREATE TABLE " + Task.TABLE_NAME + " ( " +
            Task.KEYS.UUID.getName() + " " + Task.KEYS.UUID.getType() + ", " +
            Task.KEYS.OWNER_UUID.getName() + " " + Task.KEYS.OWNER_UUID.getType() + ", " +
            Task.KEYS.NAME.getName() + " " + Task.KEYS.NAME.getType() + ", " +
            Task.KEYS.DESCRIPTION.getName() + " " + Task.KEYS.DESCRIPTION.getType() + ", " +
            Task.KEYS.PRIORITY.getName() + " " + Task.KEYS.PRIORITY.getType() + ", " +
            Task.KEYS.DUE_DATE_TIME.getName() + " " + Task.KEYS.DUE_DATE_TIME.getType() + ", " +
            Task.KEYS.STATUS.getName() + " " + Task.KEYS.STATUS.getType() + ", " +
            Task.KEYS.IS_GROUP.getName() + " " + Task.KEYS.IS_GROUP.getType() + ", " +
            Task.KEYS.GROUP_UUID.getName() + " " + Task.KEYS.GROUP_UUID.getType() +", "+
            Task.KEYS.USER_PARTICIPATION_STATUS.getName() + " " + Task.KEYS.USER_PARTICIPATION_STATUS.getType() +", "+
            Task.KEYS.SYNC_STATUS.getName() + " "+ Task.KEYS.SYNC_STATUS.getType() + ");";

    private static String CREATION_STATEMENT_BUZZ = "CREATE TABLE " + Buzz.TABLE_NAME + " ( " +
            Buzz.KEYS.TASK_ID.getName() + " " + Buzz.KEYS.TASK_ID.getType() + ", " +
            Buzz.KEYS.TASK_UUID.getName() + " " + Buzz.KEYS.TASK_UUID.getType() + ");";

    private static String CREATION_STATEMENT_NOTIFICATION = "CREATE TABLE " + Notification.TABLE_NAME + " ( " +
            Notification.KEYS.TASK_ROW_ID.getName() + " " + Notification.KEYS.TASK_ROW_ID.getType() + ", " +
            Notification.KEYS.MESSAGE.getName() + " " + Notification.KEYS.MESSAGE.getType() + ", " +
            Notification.KEYS.SEEN.getName() + " " + Notification.KEYS.SEEN.getType() + ");";

    public TaskDbHelper(Context context) {
        super(context, Task.TABLE_NAME, null, Config.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                CREATION_STATEMENT_TASK
        );
        db.execSQL(
                CREATION_STATEMENT_BUZZ
        );
        db.execSQL(
                CREATION_STATEMENT_NOTIFICATION
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL(
                        "ALTER TABLE "+ Task.TABLE_NAME+
                                " ADD "+Task.KEYS.SYNC_STATUS.getName()+" "+
                                Task.KEYS.SYNC_STATUS.getType()+";");
                db.execSQL("DROP TABLE IF EXISTS "+Buzz.TABLE_NAME);
                db.execSQL(
                        CREATION_STATEMENT_BUZZ
                );
            case 2:
                db.execSQL(
                        "ALTER TABLE "+ Task.TABLE_NAME+
                                " ADD "+Task.KEYS.USER_PARTICIPATION_STATUS.getName()+" "+
                                Task.KEYS.USER_PARTICIPATION_STATUS.getType()+";");
        }
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
        values.put(Task.KEYS.SYNC_STATUS.getName(), task.getSyncStatusAsInt());
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

    public Task createTask(Task newTask) {
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
        values.put(Task.KEYS.SYNC_STATUS.getName(), newTask.getSyncStatusAsInt());
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
        Log.d(TAG, "Query run task inserted to row "+rowId+".");
        database.close();
        newTask.setId(rowId);
        return newTask;
    }

    public Task getTaskByRowId(long rowId) {
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
            cursorString+=selfCursor.getColumnName(i)+":"+selfCursor.getString(i)+", ";
        }
        Log.d(TAG, "Cursor: "+cursorString);
        selfCursor.moveToFirst();
        Task task = new Task(
                selfCursor,
                this.context
        );
        selfCursor.close();
        readableDb.close();
        return task;
    }

    /**
     * Gets a list of all non group tasks.
     * @return A list of Task objects.
     */
    public List<Task> getAllNonGroupTasks() {
        String TAG = CLASS_TAG+"getAllNonGroupTasks";
        Cursor resultCursor = getAllNonGroupTasksAsCursor();
        // List all the non group tasks.
        List<Task> taskList = new ArrayList<Task>();
        if(resultCursor.moveToFirst()) {
            do {
                // Creates a task object to check status.
                Task task = new Task(resultCursor, this.context);
                int taskStatus = task.getStatus(this.context);
                // Ignores tasks marked as declined or completed by the user.
                if(taskStatus>Config.COLLABORATOR_STATUS.DECLINED.getStatus() &&
                        taskStatus<Config.COLLABORATOR_STATUS.COMPLETED.getStatus()) {
                    taskList.add(task);
                }
            } while(resultCursor.moveToNext());
        }
        // Close cursor.
        resultCursor.close();
        // Return the list.
        return taskList;
    }

    public Task getTaskByUUID(String taskUUID) {
        String TAG = CLASS_TAG+"getTaskByUUID";
        // Open readable database.
        SQLiteDatabase readableDb = this.getReadableDatabase();
        Log.d(TAG, "Readable db opened. Searching for id = " + taskUUID);
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
        if(cursor.moveToFirst()) {
            do {
                Log.d(TAG, "Tasks with same UUID("+cursor.getString(0)+"): "+cursor.getString(2));
            } while(cursor.moveToNext());
        }
        // Fetch task from the cursor
        cursor.moveToFirst();
        if(cursor.getCount()==0) {
            return null;
        }
        Log.d(TAG, "Creating new task from the cursor.");
        Task task = new Task(cursor, this.context);
        // Return the task.
        cursor.close();
        // Close database.
        readableDb.close();
        return task;
    }

    public void updateTask(Task task) {
        String TAG = CLASS_TAG+"updateTask";
        // Open writable database.
        SQLiteDatabase writableDb = this.getWritableDatabase();
        Log.d(TAG, "Writable database opened.");
        // Set up updation content value.
        ContentValues values = new ContentValues();
        values.put(Task.KEYS.NAME.getName(), task.getName());
        values.put(Task.KEYS.UUID.getName(), task.getUuid());
        values.put(Task.KEYS.DESCRIPTION.getName(), task.getDescription());
        values.put(Task.KEYS.OWNER_UUID.getName(), task.getOwner().getUuid());
        values.put(Task.KEYS.PRIORITY.getName(), task.getPriority());
        values.put(Task.KEYS.DUE_DATE_TIME.getName(), task.getDueDateTimeAsLong());
        values.put(Task.KEYS.STATUS.getName(), task.getStatus());
        values.put(Task.KEYS.IS_GROUP.getName(), task.getIntIsGroup());
        values.put(Task.KEYS.SYNC_STATUS.getName(), task.getSyncStatusAsInt());
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

    public boolean updateStatus(Task task, int status) {
        String TAG = CLASS_TAG+"updateStatus";
        // Open a writable database.
        SQLiteDatabase writableDb = this.getWritableDatabase();
        // make query
        ContentValues values = new ContentValues();
        values.put(Task.KEYS.STATUS.getName(),status);
        values.put(Task.KEYS.SYNC_STATUS.getName(),task.getSyncStatusAsInt());
        // execute update
        int affectedRows = writableDb.update(
                Task.TABLE_NAME,
                values,
                "ROWID =?",
                new String[] { task.getId()+"" }
        );
        // close db
        writableDb.close();
        // return status
        return (affectedRows>0);
    }

    public Cursor getAllNonGroupTasksAsCursor() {
        String TAG = CLASS_TAG+"getAllNonGroupTasksAsCursor";
        // Open database.
        SQLiteDatabase readableDb = this.getReadableDatabase();
        // Create a list of tasks.
        // Setup columns
        ArrayList<String> columnList = Task.getAllColumns();
        String[] columns = new String[columnList.size()];
        columns = columnList.toArray(columns);
        // Queries for tasks which are still marked incomplete.
        Cursor resultCursor = readableDb.query(
                Task.TABLE_NAME,
                columns,
                Task.KEYS.IS_GROUP.getName()+"=? AND "+
                Task.KEYS.STATUS.getName()+"=?",
                new String[] {
                        "0",
                        Config.TASK_STATUS.INCOMPLETE.getStatus() + ""
                },
                null,
                null,
                null
        );
        // Close db.
        readableDb.close();
        return resultCursor;
    }

    public boolean delete(Task task) {
        String TAG = CLASS_TAG+"delete";
        Log.d(TAG, "About to delete task "+task.toString()+ " from db.");
        // Open writable database.
        SQLiteDatabase writableDb = this.getWritableDatabase();
        Log.d(TAG, "Db opened.");
        // Delete row.
        int affectedRows = writableDb.delete(
                Task.TABLE_NAME,
                "ROWID =?",
                new String[] { task.getId()+"" }
        );
        Log.d(TAG,"Deleted "+affectedRows+" cols.");
        // Close database.
        writableDb.close();
        Log.d(TAG, "Db closed.");
        if(affectedRows>0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean delete(String uuid) {
        String TAG = CLASS_TAG+"delete(uuid)";
        // Open writable database
        SQLiteDatabase writableDatabse = getWritableDatabase();
        // Delete row
        int affectedRows = writableDatabse.delete(
                Task.TABLE_NAME,
                Task.KEYS.UUID.getName()+"=?",
                new String[] { uuid }
        );
        // close db
        writableDatabse.close();
        return affectedRows>0;
    }

    public Buzz createBuzz(Buzz buzz) {
        String TAG = CLASS_TAG+"createBuzz";
        // Open writable datanase.
        SQLiteDatabase writableDb = this.getWritableDatabase();
        // Setup values
        ContentValues values = new ContentValues();
        values.put(Buzz.KEYS.TASK_ID.getName(),buzz.getTaskId());
        values.put(Buzz.KEYS.TASK_UUID.getName(),buzz.getTaskUuid());
        // Insert row
        long id = writableDb.insert(
                Buzz.TABLE_NAME,
                null,
                values
        );
        // ceate new buzz from the fetched row.
        buzz.setId(id);
        // close writable db
        writableDb.close();
        // return new buzz
        return buzz;
    }

    public List<Buzz> retrieveBuzz() {
        String TAG = CLASS_TAG+"retrieveBuzz()";
        List<Buzz> buzzList = new ArrayList<>();
        // Open readable database
        SQLiteDatabase readableDb = this.getReadableDatabase();
        // Execute query
        Cursor result = readableDb.query(
                Buzz.TABLE_NAME,
                Buzz.getAllColumns(),
                null,
                null,
                null,
                null,
                null
        );
        result.moveToFirst();
        // loop through each cursor and add new buzz to a lisr
        do {
            Buzz buzz = new Buzz(result, this.context);
            buzzList.add(buzz);
        } while (result.moveToNext());
        // close the db, closse the cursor
        readableDb.close();
        result.close();
        // return list of all buzz
        return buzzList;
    }

    public Buzz retrieveBuzz(Task task) {
        String TAG = CLASS_TAG+"retrieveBuzz(Task,Activity)";
        // Open readable database
        SQLiteDatabase readableDb = this.getReadableDatabase();
        // Make query
        Cursor result = readableDb.query(
                Buzz.TABLE_NAME,
                Buzz.getAllColumns(),
                Buzz.KEYS.TASK_ID+"=? OR "+Buzz.KEYS.TASK_UUID+"=?",
                new String[]{
                        task.getUuid()+"",
                        task.getId()+""
                },
                null,
                null,
                null
        );
        result.moveToFirst();
        // Create buzz from cursor
        Buzz buzz;
        if(result.getCount()!=0) {
            buzz = new Buzz(result, this.context);
        } else {
            buzz = null;
        }
        // close cursor, db
        result.close();
        readableDb.close();
        // return buzz
        return buzz;
    }

    public boolean updateBuzz(Buzz buzz) {
        String TAG = CLASS_TAG+"updateBuzz";
        // Open writable database.
        SQLiteDatabase writableDb = this.getWritableDatabase();
        // Make querry
        ContentValues values = new ContentValues();
        values.put(Buzz.KEYS.TASK_ID.getName(), buzz.getTaskId());
        values.put(Buzz.KEYS.TASK_UUID.getName(), buzz.getTaskUuid());
        // Execute query
        // Check affected rows
        int affectedRows = writableDb.update(
                Buzz.TABLE_NAME,
                values,
                "ROWID =?",
                new String[] { buzz.getId()+"" }
        );
        // Close db
        writableDb.close();
        // return true if affecctedRows > 0
        return affectedRows > 0;
    }

    public boolean deleteBuzz(Buzz buzz) {
        String TAG = CLASS_TAG+"deleteBuzz(Buzz)";
        // Open writable database.
        SQLiteDatabase writableDb = this.getWritableDatabase();
        // execute delete query
        // find affected row count
        int affectedRows = writableDb.delete(
                Buzz.TABLE_NAME,
                "ROWID =?",
                new String[] { buzz.getId()+"" }
        );
        // close db
        writableDb.close();
        // if affected row greater than 0 return true
        return affectedRows>0;
    }

    public List<Task> retrieveAllUnsyncedTask() {
        String TAG = CLASS_TAG+"getAllUnsyncedTask";
        // Open readable database.
        SQLiteDatabase readableDb = this.getReadableDatabase();
        // make query
        String[] columns = new String[Task.getAllColumns().size()];
        columns = Task.getAllColumns().toArray(columns);
        // execute query
        Cursor unsyncedTasksCursor = readableDb.query(
                Task.TABLE_NAME,
                columns,
                Task.KEYS.SYNC_STATUS.getName()+" <>?",
                new String[] { 1 + "" },
                null,
                null,
                null
        );
        unsyncedTasksCursor.moveToFirst();
        if(unsyncedTasksCursor.getCount()==0) {
            return new ArrayList<>();
        }
        // create an empty list
        List<Task> unSyncedTaskList = new ArrayList<>();
        // loop through the result cursor
        do {
            Task unSyncedTask = new Task(unsyncedTasksCursor, this.context);
            // add tasks to the list
            unSyncedTaskList.add(unSyncedTask);
        } while (unsyncedTasksCursor.moveToNext());
        // close db
        readableDb.close();
        // close cursor
        unsyncedTasksCursor.close();
        // return tasks list
        return unSyncedTaskList;
    }

    public boolean updateParticipationStatus(Task task) {
        String TAG = CLASS_TAG+"updateParticipationStatus";
        // Open writable databse
        SQLiteDatabase writableDb = this.getWritableDatabase();
        // Setup content values.
        ContentValues values = new ContentValues();
        values.put(Task.KEYS.USER_PARTICIPATION_STATUS.getName(),task.getUserParticipationStatus());
        // Run query
        int affectedRows = writableDb.update(
                Task.TABLE_NAME,
                values,
                "ROWID =?",
                new String[] { task.getId()+"" }
        );
        // close Db
        writableDb.close();
        // return true if affectedRows > 0
        return affectedRows>0;
    }

    public Notification createNotification(Notification notification) {
        String TAG = CLASS_TAG+" creteNotification";
        // Open writable database.
        SQLiteDatabase writableDb = this.getWritableDatabase();
        // Setup values
        ContentValues values = new ContentValues();
        values.put(Notification.KEYS.TASK_ROW_ID.getName(), notification.getTaskRowId());
        values.put(Notification.KEYS.MESSAGE.getName(), notification.getMessage());
        // Insert row
        long id = writableDb.insert(
                Notification.TABLE_NAME,
                null,
                values
        );
        // create new notification from the fetched row
        notification.getId();
        // close writable db
        writableDb.close();
        // return new buzz
        return notification;
    }

    public List<Notification> retrieveNotification() {
        String TAG = CLASS_TAG + "retrieveNotification()";
        List<Notification> notificationList = new ArrayList<>();
        //Open readable database
        SQLiteDatabase readableDb = this.getReadableDatabase();
        // Execute query
        Cursor result = readableDb.query(
                Notification.TABLE_NAME,
                Notification.getAllColumns(),
                null,
                null,
                null,
                null,
                null
        );
        result.moveToFirst();
        //loop through each cursor and add new notification to a list
        do {
            Notification notification = new Notification(result, this.context);
            notificationList.add(notification);
        } while (result.moveToNext());
        //close the db, close the cursor
        readableDb.close();
        result.close();
        //return list of all notification
        return notificationList;
    }

    public Notification retrieveNotiifcation(Task task) {
        String TAG = CLASS_TAG + "retrieveNotification (Task, Activity)";
        //Open readable database
        SQLiteDatabase readableDb = this.getReadableDatabase();
        //Make query
        Cursor result = readableDb.query(
                Notification.TABLE_NAME,
                Notification.getAllColumns(),
                Notification.KEYS.TASK_ROW_ID + "=?",
                new String[] {
                        task.getId()+""
                },
                null,
                null,
                null
        );
        result.moveToFirst();
        // Create notification from cursor
        Notification notification;
        if (result.getCount()!=0) {
            notification = new Notification(result, this.context);
        } else {
            notification = null;
        }
        result.close();
        readableDb.close();
        // return notification
        return notification;
    }

    public boolean deleteNotification(Notification notification) {
        String TAG = CLASS_TAG + "deleteNotification";
        //Open  writable database.
        SQLiteDatabase writeableDb = this.getWritableDatabase();
        // execute delete query
        // find affected row count
        int affectedRows = writeableDb.delete(
                Notification.TABLE_NAME,
                "ROWID =?",
                new String[] { notification.getId()+"" }
        );
        // close db
        writeableDb.close();
        // if affected row greater than 0 return true
        return affectedRows > 0;
    }
}
