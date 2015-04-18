package in.altersense.taskapp.models;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.database.TaskDbHelper;

/**
 * Created by ashwineaso on 4/10/15.
 */
public class Notification {

    /**
     * Table name for Notification
     */
    public static final String TABLE_NAME = "Notification";
    private String ownerName, message, taskUuid;
    private Context context;
    private Task task;
    private boolean seen;
    private long taskRowId, dateTime;
    private long id;

    /**
     * KEYS for the Notification table.
     */
    public static enum KEYS {

        TASK_ROW_ID("task_row_id", "INTEGER"),
        TASK_UUID("task_uuid", "TEXT"),
        TYPE("type", "TEXT"),
        OWNER_NAME("owner_name", "TEXT"),
        MESSAGE("message","TEXT"),
        DATE_TIME("message", "INTEGER"),
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
     * @param message the message for notification
     */
    public Notification(Task task,
                        Context context,
                        String message,
                        String ownerName,
                        long dateTime) {
        this.task = task;
        this.context = context;
        this.message = message;
        this.ownerName = ownerName;
        this.dateTime = dateTime;
        this.seen = false;
        this.id = 0;
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
        this.ownerName = cursor.getString(2);
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

    public String getMessage() {return message;}

    public boolean getSeen() {return seen;}
    public  int getSeenAsInt() {return getSeen() == true? 1: 0;}

    public void setSeen(boolean seen) {this.seen = seen;}
    public void setSeen(int seen) {this.seen = seen ==1;}

    public void setId(long id) {this.id = id;}

    public long getId() {return id;}

    public void setTaskRowId() {this.taskRowId = taskRowId;}

    public void setMessage() {this.message = message;}


}
