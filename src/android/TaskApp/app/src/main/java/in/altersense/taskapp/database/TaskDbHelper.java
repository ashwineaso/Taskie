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
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.models.Buzz;
import in.altersense.taskapp.models.Collaborator;
import in.altersense.taskapp.models.Notification;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.models.User;

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

    private static String CREATION_STATEMENT_COLLABORATOR = "CREATE TABLE " + Collaborator.TABLE_NAME + " ( " +
            Collaborator.KEYS.TASK_ROWID.getName() + " " + Collaborator.KEYS.TASK_ROWID.getType() + ", " +
            Collaborator.KEYS.TASK_UUID.getName() + " " + Collaborator.KEYS.TASK_UUID.getType() + ", " +
            Collaborator.KEYS.USER_ROWID.getName() + " " + Collaborator.KEYS.USER_ROWID.getType() + ", " +
            Collaborator.KEYS.USER_UUID.getName() + " " + Collaborator.KEYS.USER_UUID.getType() + " , " +
            Collaborator.KEYS.STATUS.getName() + " " + Collaborator.KEYS.STATUS.getType() + " , " +
            Collaborator.KEYS.SYNC_STATUS.getName() + " " + Collaborator.KEYS.SYNC_STATUS.getType() + ");";

    private static String CREATION_STATEMENT_BUZZ = "CREATE TABLE " + Buzz.TABLE_NAME + " ( " +
            Buzz.KEYS.TASK_ID.getName() + " " + Buzz.KEYS.TASK_ID.getType() + ", " +
            Buzz.KEYS.TASK_UUID.getName() + " " + Buzz.KEYS.TASK_UUID.getType() + ");";

    private static String CREATION_STATEMENT_NOTIFICATION = "CREATE TABLE " + Notification.TABLE_NAME + " ( " +
            Notification.KEYS.TASK_ROW_ID.getName() + " " + Notification.KEYS.TASK_ROW_ID.getType() + ", " +
            Notification.KEYS.TASK_UUID.getName() + " " + Notification.KEYS.TASK_UUID.getType() + ", " +
            Notification.KEYS.OWNER_NAME.getName() + " " + Notification.KEYS.OWNER_NAME.getType() + ", " +
            Notification.KEYS.MESSAGE.getName() + " " + Notification.KEYS.MESSAGE.getType() + ", " +
            Notification.KEYS.DATE_TIME.getName() + " " + Notification.KEYS.DATE_TIME.getType() + ", " +
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
        db.execSQL(
                CREATION_STATEMENT_COLLABORATOR
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
            case 3:
                db.execSQL(
                        CREATION_STATEMENT_COLLABORATOR
                );
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
        String columnsFromTask = "";
        for(int ctr=0; ctr<columns.length; ctr++) {
            String col = columns[ctr];
            columnsFromTask = columnsFromTask+"A."+col;
            if(ctr!=columns.length-1) {
                columnsFromTask+=", ";
            } else {
                columnsFromTask+=" ";
            }
        }
        String deviceOwnerUUID = AltEngine.readStringFromSharedPref(
                this.context,
                Config.SHARED_PREF_KEYS.OWNER_ID.getKey(),
                ""
        );
        String query = "SELECT DISTINCT "+columnsFromTask + " FROM " + Task.TABLE_NAME + " A LEFT JOIN "+
                Collaborator.TABLE_NAME + " B ON A.ROWID = B." + Collaborator.KEYS.TASK_ROWID.getName() +
                " WHERE ( A." +Task.KEYS.OWNER_UUID.getName()+" LIKE \""+deviceOwnerUUID+"\" AND " +
                "A."+Task.KEYS.STATUS.getName()+
                " LIKE " + Config.TASK_STATUS.INCOMPLETE.getStatus() +
                ") OR ( B." + Collaborator.KEYS.USER_UUID + " LIKE \"" + deviceOwnerUUID + "\"" +
                " AND B." + Collaborator.KEYS.STATUS + " BETWEEN " + Config.COLLABORATOR_STATUS.PENDING.getStatus() +
                " AND " + Config.COLLABORATOR_STATUS.ACCEPTED.getStatus() + ");";
        Cursor resultCursor = readableDb.rawQuery(query, null);
        Log.d(TAG, "Returned "+resultCursor.getCount()+" rows.");
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

    // Collaborators dal
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

    public boolean removeCollaborator(Task task, Collaborator user) {
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

        result.moveToFirst();
        List<Collaborator> collaboratorList = new ArrayList<Collaborator>();
        UserDbHelper userDbHelper = new UserDbHelper(this.context);
        do {
            try {
                String cursorString = "";
                for(int i=0; i<result.getColumnCount();i++) {
                    try{
                        cursorString+=result.getColumnName(i)+"="+result.getString(i)+", ";
                    } catch (CursorIndexOutOfBoundsException e) {
                        Log.d(TAG, "No collaborator to display");
                        return collaboratorList;
                    }
                }
                Log.d(TAG, "Collaborator: "+cursorString);
                // Fetch collaborator from db
                Log.d(TAG, "Fetching collaborator from db.");
                User user = userDbHelper.getUserByRowId(result.getLong(2));
                Collaborator collaborator = new Collaborator(user);
                // Set collaborator status
                Log.d(TAG, "Setting collaborator status.");
                collaborator.setStatus(result.getInt(4));
                // Make list
                collaboratorList.add(collaborator);
            } catch (CursorIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }while (result.moveToNext());
        // Close db
        readableDb.close();
        // Return list
        return collaboratorList;
    }

    public Collaborator getCollaborator(Task task, Collaborator collaborator) {
        // Open a readable db
        SQLiteDatabase readableDb = this.getReadableDatabase();
        // Exectute query
        Cursor result = readableDb.query(
                Collaborator.TABLE_NAME,
                Collaborator.getAllColumns(),
                Collaborator.KEYS.TASK_ROWID.getName()+"=? AND "+
                        Collaborator.KEYS.USER_ROWID.getName()+"=?",
                new String[] {
                        task.getId()+"",
                        collaborator.getId()+""
                },
                null,
                null,
                null
        );
        // Set collaborator status.
        result.moveToFirst();
        collaborator.setStatus(
                result.getInt(result.getColumnIndex(Collaborator.KEYS.STATUS.getName()))
        );
        // Return collaborator
        return collaborator;
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
                Collaborator.KEYS.TASK_ROWID.getName()+" =? AND " +
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

    public void listAllCollaborators() {
        String TAG = CLASS_TAG+"listAllCollaborators";
        // Open readable database.
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        // Query the database.
        Cursor result = readableDatabase.query(
                Collaborator.TABLE_NAME,
                Collaborator.getAllColumns(),
                null,
                null,
                null,
                null,
                null
        );
        Log.d(TAG, "Returned "+result.getCount()+" rows.");
        // Print each result in log.
        result.moveToFirst();
        try {
            do {
                String cursorString = "";
                for (int i = 0; i < result.getColumnCount(); i++) {
                    cursorString += i + "=" + result.getString(i) + ", ";
                }
                Log.d(TAG, "Collaborator: " + cursorString);

            } while (result.moveToNext());
            // Close database.
            result.close();
            readableDatabase.close();
        }
        catch (CursorIndexOutOfBoundsException e) {
            Log.d(TAG, "No Collaborators to display");
        }
    }

    public boolean isCollaborator(Task task, Collaborator collaborator) {
        String TAG = CLASS_TAG+"isCollaborator";
        boolean state = false;
        // Open readable database
        SQLiteDatabase readableDb = getReadableDatabase();
        Log.d(TAG, "Opened readable db.");
        // Search for entry in db with task and collaborator.
        Cursor result = readableDb.query(
                Collaborator.TABLE_NAME,
                Collaborator.getAllColumns(),
                Collaborator.KEYS.TASK_ROWID.getName()+"=? AND "+
                        Collaborator.KEYS.USER_ROWID.getName()+"=?",
                new String[] {
                        task.getId()+"",
                        collaborator.getId()+""
                },
                null,
                null,
                null
        );
        result.moveToFirst();
        if(result.getCount()!=0) {
            // If present and status
            state = true;
        } else {
            // else false.
            state = false;
        }
        // Close db and cursor
        result.close();
        readableDb.close();
        return state;
    }

    public boolean deleteCollaborator(Task task) {
        String TAG = CLASS_TAG+"delete(Task)";
        if(task.getId()==0) {
            Log.d(TAG, "Task ROWID is zero so aborting delete.");
            return false;
        }
        // Open database.
        SQLiteDatabase writableDb = this.getWritableDatabase();
        // Delete all collaborators of the task
        int affectedRows = writableDb.delete(
                Collaborator.TABLE_NAME,
                Collaborator.KEYS.TASK_ROWID.getName()+" =?",
                new String[] { task.getId()+"" }
        );
        // Close database
        writableDb.close();
        // Return action status.
        Log.d(TAG, "Cleared "+affectedRows+" rows.");
        return affectedRows>0;
    }
}
