package in.altersense.taskapp.models;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.CreateTaskActivity;
import in.altersense.taskapp.R;
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.database.CollaboratorDbHelper;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.database.UserDbHelper;

/**
 * Created by mahesmohan on 1/13/15.
 */
public class Task {
    private static final String CLASS_TAG = "Task ";

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

    public void setUuid(String uuid, Activity activity) {
        this.uuid = uuid;
        TaskDbHelper taskDbHelper = new TaskDbHelper(activity);
        taskDbHelper.updateUUID(this);
    }

    public int getIntIsGroup() {
        if(this.isGroup) {
            return 1;
        } else {
            return 0;
        }
    }

    public void setCollaborators(ArrayList<User> newCollaboratorList) {
        this.collaborators = newCollaboratorList;
    }

    public void updateCollaborators(
            List<User> oldCollaborators,
            ArrayList<User> collaboratorAdditionList,
            ArrayList<User> collaboratorRemovalList,
            Activity activity
    ) {
        String TAG = CLASS_TAG+"updateCollaborators";
        CollaboratorDbHelper collaboratorDbHelper = new CollaboratorDbHelper(activity.getApplicationContext());
        // Add each users to the list.
        for(User addedCollaborator:collaboratorAdditionList) {
            Log.d(TAG, "Collaborator addition: "+addedCollaborator.toString());
            Log.d(TAG, "Collaborator ID:"+addedCollaborator.getId());
            // Check if user is present in the database.
            if(addedCollaborator.getId()<1) {
                // If not add user to database.
                Log.d(TAG, "Collaborator not found in User database. Adding to db.");
                UserDbHelper userDbHelper = new UserDbHelper(activity.getApplicationContext());
                addedCollaborator = userDbHelper.createUser(addedCollaborator);
                Log.d(TAG, "Added collaborator to user database.");
                // Sync user to get more information regarding the user.
                SyncUserRequest syncUserRequest = new SyncUserRequest(addedCollaborator,activity);
                Log.d(TAG, "Sending a sync user request to API to get user info.");
                syncUserRequest.execute();
            }
            // Add the user as a collaborator of the task.
            collaboratorDbHelper.addCollaborator(this, addedCollaborator);
            Log.d(TAG, "User added to database as a collaborator of the task.");
            // Add user to the oldCollaborator list.
            oldCollaborators.add(addedCollaborator);
            Log.d(TAG, "User added to oldCollaboratorList of the task.");
        }
        // Remove users from the removal list.
        for(User removedCollaborator:collaboratorRemovalList) {
            // Remove the user from the collaborator list database.
            Log.d(TAG, "About to remove the collaborator from database.");
            collaboratorDbHelper.removeCollaborator(this, removedCollaborator);
            // Remove the removed collaborator from the oldCollaborators list.
            oldCollaborators.remove(removedCollaborator);
            Log.d(TAG, "User removed from oldCollaboratorList of the task.");

        }

        this.collaborators = oldCollaborators;
        Log.d(TAG, "New list of collaborators set as this task's list of collaborators.");
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

    public static ArrayList<String> getAllColumns() {
        ArrayList<String> columnsList = new ArrayList<String>();
        for(KEYS key: KEYS.values()) {
            columnsList.add(key.getName());
        }
        // Add row id to list of columns.
        columnsList.add("ROWID");
        return columnsList;
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
     * @param activity Current activity
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
            Activity activity
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
        Log.d(CLASS_TAG, "Basic fields set.");

        Log.d(CLASS_TAG, "PanelView construction being called.");
        this.panelView = createView(activity.getLayoutInflater());
        Log.d(CLASS_TAG, "PanelView constructed.");

        Log.d(CLASS_TAG, "ActionsView constructions being called.");
        this.actionsView = createActionsView(activity);
        Log.d(CLASS_TAG, "ActionsView constructed.");


        Log.d(CLASS_TAG, "TaskActionsPlaceHolder being set up.");
        this.taskActionsPlaceHolderView =
                (LinearLayout) this.panelView.findViewById(R.id.actionsPlaceHolderLinearLayout);
        this.taskActionsPlaceHolderView.setVisibility(View.GONE);
        Log.d(CLASS_TAG, "TaskActionsPlaceHolder visibility now set to GONE.");
        this.taskActionsPlaceHolderView.addView(this.actionsView);
        Log.d(CLASS_TAG, "TaskActionsPlaceHolder gets the addition of the ActionsView");
        this.isActionsDisplayed = false;
        Log.d(CLASS_TAG, "ActionsDisplayed is set to false.");

    }

    /**
     * Create a bare minimum task.
     * @param name Name of the Task.
     * @param description Task description.
     * @param owner Owner user of the task
     * @param activity Layout activity.
     */
    public Task(
            String name,
            String description,
            User owner,
            Activity activity
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
                activity
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
            Activity activity
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
                activity
        );
    }

    public Task(Cursor cursor, Activity activity) {
        Log.d(CLASS_TAG, " Constructor(cursor,activity)");
        this.uuid = cursor.getString(0);
        this.name = cursor.getString(2);
        this.description = cursor.getString(3);
        this.owner = new User(cursor.getString(1), activity);
        this.priority = cursor.getInt(4);
        this.dueDateTime = cursor.getLong(5);
        this.status = cursor.getInt(6);
        this.isGroup = cursor.getInt(7)==1;
        if(this.isGroup) {
            this.group = new TaskGroup(cursor.getString(8), activity);
        } else {
            this.group = null;
        }
        this.id = cursor.getLong(9);
        this.panelView = createView(activity.getLayoutInflater());
        this.actionsView = createActionsView(activity);
        this.taskActionsPlaceHolderView =
                (LinearLayout) this.panelView.findViewById(R.id.actionsPlaceHolderLinearLayout);
        this.taskActionsPlaceHolderView.setVisibility(View.GONE);
        this.taskActionsPlaceHolderView.addView(this.actionsView);
        this.isActionsDisplayed = false;
    }

    private View createActionsView(final Activity activity) {
        final LayoutInflater myInflater = activity.getLayoutInflater();
        View actionsPanel = myInflater.inflate(R.layout.task_actions, null);

        action1 = (LinearLayout) actionsPanel.findViewById(R.id.action1);
        action2 = (LinearLayout) actionsPanel.findViewById(R.id.action2);
        action3 = (LinearLayout) actionsPanel.findViewById(R.id.action3);
        action4 = (LinearLayout) actionsPanel.findViewById(R.id.action4);

        action1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Create an intent to edit screen.
                Intent editTaskIntent = new Intent(
                        activity.getApplicationContext(),
                        CreateTaskActivity.class
                );
                // Pass the task id to the intent.
                editTaskIntent.putExtra(
                        Config.REQUEST_RESPONSE_KEYS.UUID.getKey(),
                        uuid
                );
                // Set flags for activity creation.
                editTaskIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // Start the activity.
                activity.startActivity(editTaskIntent);
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
        Log.i(CLASS_TAG, "Reached showTaskActions");
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

    @Override
    public String toString() {
        String task ="";
        task+=" id="+this.id;
        task+=" uuid="+this.uuid;
        task+=" name="+this.name;
        task+=" owner="+this.owner.getUuid();
        return task;
    }
}