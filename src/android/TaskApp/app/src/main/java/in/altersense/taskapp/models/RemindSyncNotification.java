package in.altersense.taskapp.models;

import android.content.Context;
import android.database.Cursor;

import in.altersense.taskapp.database.TaskDbHelper;

/**
 * Created by mahesmohan on 4/26/15.
 */
public class RemindSyncNotification {

    public static final String TABLE_NAME = "RemindCollaboratorTable";
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
    }
}
