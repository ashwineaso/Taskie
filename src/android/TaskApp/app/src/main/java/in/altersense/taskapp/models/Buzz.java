package in.altersense.taskapp.models;

import android.app.Activity;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.database.TaskDbHelper;

/**
 * Created by mahesmohan on 3/16/15.
 */
public class Buzz {

    /**
     * Table name for Buzz
     */
    public static final String TABLE_NAME = "BuzzList";
    private String taskUuid;
    private long taskId;
    private long id;
    private Activity activity;
    private Task task;

    /**
     * KEYS for the BuzzList table.
     */
    public static enum KEYS {

        TASK_ID("task_id", "INTEGER"),
        TASK_UUID("task_uuid", "TEXT");

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

    /**
     * Constructor with task.
     * @param task Task which needs to be bzzed.
     * @param activity Current activity.
     */
    public Buzz(Task task, Activity activity) {
        this.task = task;
        this.taskId = task.getId();
        this.taskUuid = task.getUuid();
        this.activity = activity;
        this.id = 0;
    }

    /**
     * Constructor with a cursor and activity
     * @param cursor Cursor containing Buzz data
     * @param activity Current activity.
     */
    public Buzz(Cursor cursor, Activity activity) {
        TaskDbHelper taskDbHelper = new TaskDbHelper(activity.getApplicationContext());
        this.taskId = cursor.getLong(0);
        this.taskUuid = cursor.getString(1);
        this.id = cursor.getLong(2);
        this.activity = activity;
        this.task = taskDbHelper.getTaskByRowId(this.taskId, this.activity);
    }

    /**
     * Lists all columns for making SQLite queries.
     * @return String array of columns including ROWID.
     */
    public static String[] getAllColumns() {
        List<String> columnList = new ArrayList<>();
        for(KEYS column:KEYS.values()) {
            columnList.add(column.getName());
        }
        columnList.add("ROWID");
        String[] columns = new String[columnList.size()];
        columns = columnList.toArray(columns);
        return columns;
    }

    public long getTaskId() {
        return taskId;
    }

    public String getTaskUuid() {
        return taskUuid;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
    }
}
