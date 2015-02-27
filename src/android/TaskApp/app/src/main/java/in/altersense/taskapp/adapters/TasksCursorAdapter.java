package in.altersense.taskapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
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

    public TasksCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View taskView = inflater.inflate(R.layout.task_panel, parent);
        bindView(taskView, context, cursor);
        return taskView;
    }

    @Override
    public void bindView(View taskView, Context context, Cursor cursor) {

        Task task = new Task(cursor, context);

        CollaboratorDbHelper collaboratorDbHelper = new CollaboratorDbHelper(context);
        task.setCollaborators(collaboratorDbHelper.getAllCollaborators(task));

        TextView timeStatus = (TextView) taskView.findViewById(R.id.timeStatusCustomFontTextView);
        TextView timeMeasure = (TextView) taskView.findViewById(R.id.timeMeasureCustomFontTextView);
        TextView timeUnit = (TextView) taskView.findViewById(R.id.timeUnitTextCustomFontTextView);
        TextView taskTitle = (TextView) taskView.findViewById(R.id.taskTitleTextView);
        LinearLayout collaboratorsLL = (LinearLayout) taskView.findViewById(R.id.collaboratorsList);
        LinearLayout taskStatus = (LinearLayout) taskView.findViewById(R.id.taskStatusLinearLayout);

        taskTitle.setText(cursor.getString(2));
        timeMeasure.setText(cursor.getString(5));
        taskStatus.setBackgroundResource(Task.getStatusColor(this.getStatus(activity.getApplicationContext())));
    }
}
