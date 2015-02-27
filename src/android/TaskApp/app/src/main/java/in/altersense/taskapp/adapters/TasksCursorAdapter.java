package in.altersense.taskapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.altersense.taskapp.R;
import in.altersense.taskapp.database.CollaboratorDbHelper;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.models.Task;

/**
 * Created by mahesmohan on 2/26/15.
 */
public class TasksCursorAdapter extends CursorAdapter{

    private Activity activity;
    String CLASS_TAG = "TasksCursorAdapter ";

    public TasksCursorAdapter(Activity activity, Cursor cursor) {
        super(activity.getApplicationContext(), cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.activity = activity;
        Log.d(CLASS_TAG, "Initialized");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        String TAG = CLASS_TAG+"newView";
        LayoutInflater inflater = LayoutInflater.from(context);
        View taskView = inflater.inflate(R.layout.task_panel, null);
        Log.d(TAG, "Inflated new view now calling bindView");
        bindView(taskView, context, cursor);
        return taskView;
    }

    @Override
    public void bindView(View taskView, Context context, Cursor cursor) {
        String TAG = CLASS_TAG+"bindView";

        Task task = new Task(cursor, context);
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
        Log.d(TAG, "Set task panel view.");
        Log.d(TAG, "Setting actions view.");
        task.setActionsView(this.activity);
        task.hideTaskActions();
        Log.d(TAG, "Task actions view was set and hidden.");

    }
}
