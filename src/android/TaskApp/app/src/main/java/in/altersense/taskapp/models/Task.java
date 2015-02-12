package in.altersense.taskapp.models;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.R;

/**
 * Created by mahesmohan on 1/13/15.
 */
public class Task {
    private static final String TAG = "Task";

    private long id;

    private boolean isGroup;

    private User owner;

    private String uuid;
    private String name;
    private String description;
    private String deadlineTimeMeasure;
    private TaskGroup group;
    public boolean isActionsDisplayed;
    private int priority;
    private int status;
    private List<User> collaborators;
    private View panelView, actionsView;
    private LinearLayout taskActionsPlaceHolderView;
    private LinearLayout action1, action2, action3, action4;
    private long dueDateTime;

    public boolean isGroup() {
        return isGroup;
    }

    public TaskGroup getGroup() {
        return group;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public long getDueDateTime() {
        return this.dueDateTime;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public int getStatus() {
        return status;
    }

    public List<User> getCollaborators() {
        return collaborators;
    }


    /**
     * Table name for Tasks
     */
    public static String TABLE_NAME = "TaskTable";

    public String getUuid() {
        return uuid;
    }

    public int getIntIsGroup() {
        if(this.isGroup) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Table Structure for Task
     */
    public static enum KEYS {

        UUID("uuid", "TEXT"),
        OWNER_UUID("owner_uuid", "TEXT"),
        NAME("name", "TEXT"),
        DESCRIPTION("description", "TEXT"),
        PRIORITY("priority", "INT"),
        DUE_DATE_TIME("due_date_time", "INT"),
        STATUS("status", "INT"),
        IS_GROUP("is_group", "INT"),
        GROUP_UUID("group_uuid", "TEXT");

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
     * Task Constructor
     * @param uuid UUID
     * @param name Name of the Task.
     * @param description Task description.
     * @param owner Owner user of the task
     * @param priority Priority level of the task
     * @param dueDateTime Deadline of the task
     * @param status Current status of the task
     * @param isGroup Is task in a group
     * @param group Task group
     * @param inflater Layout inflater.
     */
    public Task(
            String uuid,
            String name,
            String description,
            User owner,
            int priority,
            long dueDateTime,
            int status,
            boolean isGroup,
            TaskGroup group,
            final LayoutInflater inflater
    ) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.priority = priority;
        this.dueDateTime = dueDateTime;
        this.status = status;
        this.isGroup = isGroup;
        this.group = group;
        this.collaborators = new ArrayList<User>();

        this.panelView = createView(inflater);
        this.actionsView = createActionsView(inflater);
        this.taskActionsPlaceHolderView =
                (LinearLayout) this.panelView.findViewById(R.id.actionsPlaceHolderLinearLayout);
        this.taskActionsPlaceHolderView.setVisibility(View.GONE);
        this.taskActionsPlaceHolderView.addView(this.actionsView);
        this.isActionsDisplayed = false;
    }

    /**
     * Create a bare minimum task.
     * @param name Name of the Task.
     * @param description Task description.
     * @param owner Owner user of the task
     * @param inflater Layout inflater.
     */
    public Task(
            String name,
            String description,
            User owner,
            LayoutInflater inflater
    ) {
        this(
                "",
                name,
                description,
                owner,
                0,
                0,
                0,
                false,
                null,
                inflater
        );
    }

    public Task(
            String uuid,
            String name,
            String description,
            User owner,
            int priority,
            long dueDateTime,
            int status,
            int isGroup,
            TaskGroup group,
            final LayoutInflater inflater
    ) {
        this(
                uuid,
                name,
                description,
                owner,
                priority,
                dueDateTime,
                status,
                isGroup==1,
                group,
                inflater
        );
    }

    public Task(Cursor cursor, Activity activity) {
        this(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                new User(cursor.getString(3), activity),
                cursor.getInt(4),
                cursor.getLong(5),
                cursor.getInt(6),
                cursor.getInt(7)==1,
                new TaskGroup(cursor.getString(8), activity),
                activity.getLayoutInflater()
        );
    }

    private View createActionsView(LayoutInflater inflater) {
        final LayoutInflater myInflater = inflater;
        View actionsPanel = inflater.inflate(R.layout.task_actions, null);

        action1 = (LinearLayout) actionsPanel.findViewById(R.id.action1);
        action2 = (LinearLayout) actionsPanel.findViewById(R.id.action2);
        action3 = (LinearLayout) actionsPanel.findViewById(R.id.action3);
        action4 = (LinearLayout) actionsPanel.findViewById(R.id.action4);

        action1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(myInflater.getContext(), "Action1", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        action2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(myInflater.getContext(), "Action2", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        action3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(myInflater.getContext(), "Action3", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        action4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(myInflater.getContext(), "Action4", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        return actionsPanel;
    }

    public void showTaskActions() {
        Log.i(TAG, "Reached showTaskActions");
        this.taskActionsPlaceHolderView.setVisibility(View.VISIBLE);
        this.isActionsDisplayed = true;
    }

    public void hideTaskActions() {
//        Hide task panel
        this.taskActionsPlaceHolderView.setVisibility(View.GONE);
//        Unset the is actions displayed flag so that the parent views can check if panel
//        is open
        this.isActionsDisplayed = false;
    }

    private View createView(LayoutInflater inflater) {
        View taskView = inflater.inflate(R.layout.task_panel, null);
        TextView timeStatus = (TextView) taskView.findViewById(R.id.timeStatusCustomFontTextView);
        TextView timeMeasure = (TextView) taskView.findViewById(R.id.timeMeasureCustomFontTextView);
        TextView timeUnit = (TextView) taskView.findViewById(R.id.timeUnitTextCustomFontTextView);

        TextView taskTitle = (TextView) taskView.findViewById(R.id.taskTitleTextView);
        TextView taskDescr = (TextView) taskView.findViewById(R.id.taskDescriptionTextView);
        TextView taskOwner = (TextView) taskView.findViewById(R.id.taskOwnerTextView);

        taskTitle.setText(this.name);
        taskDescr.setText(this.description);
        taskOwner.setText(this.owner.getName());
        timeMeasure.setText(this.deadlineTimeMeasure);

        return taskView;
    }

    public View getPanelView() {
        return panelView;
    }

    public LinearLayout getTaskActionsPlaceHolderView() {
        return taskActionsPlaceHolderView;
    }

}