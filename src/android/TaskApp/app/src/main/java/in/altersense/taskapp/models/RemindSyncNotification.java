package in.altersense.taskapp.models;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import in.altersense.taskapp.database.TaskDbHelper;

/**
 * Created by mahesmohan on 4/26/15.
 */
public class RemindSyncNotification {

    public static final String TABLE_NAME = "RemindSyncNotificationTable";
    /**
     * Table Structure for Task
     */
    public static enum KEYS {

        TASK_ID("task_id", "TEXT"),
        HIDE_NOTIF("hide_notification", "INTEGER");

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

    private long taskId;
    private boolean hideNotification;
    private Context context;
    private TaskDbHelper taskDbHelper;
    private Task task;
    private long id;

    public RemindSyncNotification(long taskId, boolean hideNotification, Context context) {
        this.context = context;
        this.taskId = taskId;
        this.hideNotification = hideNotification;
        this.taskDbHelper = new TaskDbHelper(this.context);
        this.task = this.taskDbHelper.getTaskByRowId(this.taskId);
    }

    public RemindSyncNotification(Cursor cursor, Context context) {
        this(
                cursor.getLong(cursor.getColumnIndex(KEYS.TASK_ID.getName())),
                cursor.getInt(cursor.getColumnIndex(KEYS.HIDE_NOTIF.getName()))==1,
                context
        );
        this.id = cursor.getLong(cursor.getColumnIndex("ROWID"));
    }

    public long getTaskId() {
        return taskId;
    }

    public boolean isHideNotification() {
        return hideNotification;
    }

    public int getHideNotification() {
        return hideNotification == true ? 1 : 0;
    }

    public Task getTask() {
        return task;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static String[] getAllColumns() {
        ArrayList<String> columnsList = new ArrayList<String>();
        for(KEYS key: KEYS.values()) {
            columnsList.add(key.getName());
        }
        // Add row id to list of columns.
        columnsList.add("ROWID as _id");
        String[] columns = new String[columnsList.size()];
        columns = columnsList.toArray(columns);
        return columns;
    }
}
