package in.altersense.taskapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.CursorTreeAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import in.altersense.taskapp.CreateTaskActivity;
import in.altersense.taskapp.R;
import in.altersense.taskapp.TaskActivity;
import in.altersense.taskapp.database.CollaboratorDbHelper;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.models.Task;

/**
 * Created by mahesmohan on 2/26/15.
 */
public class TasksCursorAdapter extends CursorTreeAdapter{

    private Activity activity;
    String CLASS_TAG = "TasksCursorAdapter ";
    private Task task;

    public TasksCursorAdapter(Activity activity, Cursor cursor) {
        super(cursor, activity.getApplicationContext(), false);
        this.activity = activity;
        Log.d(CLASS_TAG, "Initialized");
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        return null;
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
        String TAG = CLASS_TAG+"newView";
        LayoutInflater inflater = LayoutInflater.from(context);
        View taskView = inflater.inflate(R.layout.task_panel, null);
        Log.d(TAG, "Inflated new view now calling bindView");
        bindGroupView(taskView, context, cursor, false);
        return taskView;
    }

    @Override
    protected void bindGroupView(View taskView, Context context, Cursor cursor, boolean isExpanded) {
        String TAG = CLASS_TAG+"bindView";

        this.task = new Task(cursor, context);
        Log.d(TAG, "Created new task from cursor");

        Log.d(TAG, "Setting up collaborators for the created task.");
        CollaboratorDbHelper collaboratorDbHelper = new CollaboratorDbHelper(context);
        task.setCollaborators(collaboratorDbHelper.getAllCollaborators(task));
        Log.d(TAG, "All collaborators set.");

        Log.d(TAG, "Initializing the views for the task panel view.");
        TextView timeStatus = (TextView) taskView.findViewById(R.id.timeStatusCustomFontTextView);
        TextView timeMeasure = (TextView) taskView.findViewById(R.id.timeMeasureCustomFontTextView);
        TextView timeUnit = (TextView) taskView.findViewById(R.id.timeUnitTextCustomFontTextView);
        TextView taskTitle = (TextView) taskView.findViewById(R.id.taskTitleTextView);
        LinearLayout collaboratorsLL = (LinearLayout) taskView.findViewById(R.id.collaboratorsList);
        LinearLayout taskStatus = (LinearLayout) taskView.findViewById(R.id.taskStatusLinearLayout);
        Log.d(TAG, "Initialized the new view.");

        Log.d(TAG, "Setting values to panel view.");
        taskTitle.setText(task.getName());
        timeMeasure.setText(task.getDueDateTime());
        taskStatus.setBackgroundResource(Task.getStatusColor(task.getStatus(context)));
        Log.d(TAG, "Basic values set up.");

        Log.d(TAG, "Associating the panel view with the task.");
        task.setTaskPanelView(taskView);
        Log.d(TAG, "Done.");

    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        String TAG = CLASS_TAG+"newChildView";
        Log.d(TAG, "Setting up actions view.");

        LayoutInflater myInflater = LayoutInflater.from(context);
        View actionsPanel = myInflater.inflate(R.layout.task_actions, null);

        Log.d(TAG, "Set up child view now calling bindChildView");
        bindChildView(actionsPanel, context, cursor, isLastChild);
        Log.d(TAG, "Done.");

        return actionsPanel;

    }

    @Override
    protected void bindChildView(View actionsPanel, final Context context, Cursor cursor, boolean isLastChild) {
        LinearLayout action1 = (LinearLayout) actionsPanel.findViewById(R.id.action1);
        LinearLayout action2 = (LinearLayout) actionsPanel.findViewById(R.id.action2);
        LinearLayout action3 = (LinearLayout) actionsPanel.findViewById(R.id.action3);
        LinearLayout action4 = (LinearLayout) actionsPanel.findViewById(R.id.action4);

        action1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Create an intent to view the task
                Intent viewTaskIntent = new Intent(
                        activity.getApplicationContext(),
                        TaskActivity.class
                );
                //Pass the task id to the intent.
                viewTaskIntent.putExtra(
                        Task.ID,
                        task.getId()
                );
                //Start the activity
                activity.startActivity(viewTaskIntent);

            }
        });

        action2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Create an intent to edit screen.
                Intent editTaskIntent = new Intent(
                        activity.getApplicationContext(),
                        CreateTaskActivity.class
                );
                // Pass the task id to the intent.
                editTaskIntent.putExtra(
                        Task.ID,
                        task.getId()
                );
                // Set flags for activity creation.
                editTaskIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // Start the activity.
                activity.startActivity(editTaskIntent);
            }
        });

        action3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Action3", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        action4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String TAG=CLASS_TAG+"statusToggle onClick";
                task.toggleStatus(activity);
            }
        });
    }
}
