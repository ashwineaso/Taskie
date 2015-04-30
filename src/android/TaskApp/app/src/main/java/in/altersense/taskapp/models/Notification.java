package in.altersense.taskapp.models;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.R;
import in.altersense.taskapp.database.TaskDbHelper;

/**
 * Created by ashwineaso on 4/10/15.
 */
public class Notification {

    /**
     * Table name for Notification
     */
    public static final String TABLE_NAME = "Notification";
    private String message, taskUuid, type;
    private Context context;
    private Task task;
    private boolean seen;
    private long taskRowId;
    private Long dateTime;
    private long id;

    /**
     * KEYS for the Notification table.
     */
    public static enum KEYS {

        TASK_ROW_ID("task_row_id", "INTEGER"),
        TASK_UUID("task_uuid", "TEXT"),
        TYPE("type", "TEXT"),
        MESSAGE("message","TEXT"),
        DATE_TIME("dateTime", "INTEGER"),
        SEEN("seen","INTEGER");

        private final String name;
        private final String type;

        KEYS(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {return name;}

        public String getType() {return type;}

    }

    /**
     * Constructor with task,
     * @param task Task whose notification is being added,
     * @param context the current context
     * @param type
     * @param message the message for notification
     * @param dateTime
     */
    public Notification(Task task,
                        Context context,
                        String type,
                        String message,
                        Long dateTime) {
        this.task = task;
        this.context = context;
        this.type = type;
        this.message = message;
        this.dateTime = dateTime;
        //Manually set seen as false
        this.seen = false;
        this.id = 0;
        //retrieve the uuid and row_id from the task
        this.taskRowId = this.task.getId();
        this.taskUuid = this.task.getUuid();
    }

    /**
     * Constructor with cursor and activity
     * @param cursor Cursor containing notification data
     * @param context Current context
     */
    public Notification(Cursor cursor, Context context) {
        TaskDbHelper taskDbHelper = new TaskDbHelper(context);
        this.taskRowId = cursor.getLong(0);
        this.taskUuid = cursor.getString(1);
        this.type = cursor.getString(2);
        this.message = cursor.getString(3);
        this.dateTime = cursor.getLong(4);
        this.setSeen(cursor.getInt(5));
        this.task = taskDbHelper.getTaskByRowId(this.taskRowId);

    }

    /**
     * List all column for making SQLite queries
     * @return String array of columns including ROWID
     */
    public static String[] getAllColumns() {
        List<String> columnList = new ArrayList<>();
        for(KEYS columns:KEYS.values()) {
            columnList.add(columns.getName());
        }
        columnList.add("ROWID");
        String[] columns = new String[columnList.size()];
        columns = columnList.toArray(columns);
        return columns;
    }


    public long getTaskRowId() {return taskRowId;}

    public String getTaskUuid() {return taskUuid;}

    public String getType() {return type;}

    public String getMessage() {return message;}

    public long getDateTime() {return dateTime;}

    public boolean getSeen() {return seen;}

    public  int getSeenAsInt() {return getSeen() == true? 1: 0;}

    public void setSeen(boolean seen) {this.seen = seen;}

    public void setSeen(int seen) {this.seen = seen ==1;}

    public void setId(long id) {this.id = id;}

    public long getId() {return id;}

    public void setTaskRowId() {this.taskRowId = taskRowId;}

    public void setMessage() {this.message = message;}

    public int getSymbol(String type) {
        switch (type) {
            case "newTask" : return R.drawable.ic_notif_new_task;
            case "taskUpdate" : return R.drawable.ic_notif_update;
            case "taskStatusChange" : return R.drawable.ic_notif_complete;
            case "collStatusChange" : return R.drawable.ic_notif_complete;
            case "taskDeletion" : return  R.drawable.ic_notif_delete;
            case "collAddition" : return R.drawable.ic_notif_coll_add;
            case "collDeletion" : return R.drawable.ic_notif_coll_rem;
            default: return R.drawable.ic_notif_update;
        }
    }


}
