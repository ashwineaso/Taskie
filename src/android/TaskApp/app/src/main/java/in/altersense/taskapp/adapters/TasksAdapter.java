package in.altersense.taskapp.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import in.altersense.taskapp.R;
import in.altersense.taskapp.models.Collaborator;
import in.altersense.taskapp.models.Task;

/**
 * Created by mahesmohan on 2/26/15.
 */
public class TasksAdapter extends ArrayAdapter<Task>{

    private static final String CLASS_TAG = "TasksCursorAdapter ";

    private LayoutInflater inflater;
    private Activity activity;
    private List<Task> taskList;

    private static final int VIEW_ITEM_NORMAL = 0;
    private static final int VIEW_EXPANDED = 1;
    private int selected = -1;

    public static class ViewHolder{
        public LinearLayout taskStatus;
        public TextView taskTitle;
        public LinearLayout collaboratorList;
        public LinearLayout actionsPlaceHolder;
        public LinearLayout action1, action2, action3, action4;
        public ImageView actionImage1, actionImage2, actionImage3, actionImage4;

        public TextView[] collaborators = new TextView[10];

    }

    public TasksAdapter(Activity activity, List<Task> taskList) {
        super(activity.getApplicationContext(), R.layout.task_panel, taskList);
        this.activity = activity;
        this.taskList = taskList;

        this.inflater = activity.getLayoutInflater();
    }

    public void setSelected(int position) {
        this.selected = position;
    }

    public int getSelected() {
        return selected;
    }

    @Override
    public int getItemViewType(int position) {
        return position== selected ? VIEW_EXPANDED : VIEW_ITEM_NORMAL;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String TAG = CLASS_TAG+"getView";
        final ViewHolder holder;
        int viewType = getItemViewType(position);
        Log.d(TAG, "ViewType: "+viewType+" at position: "+position);
        if(convertView==null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.task_panel, parent, false);

            holder.taskTitle = (TextView) convertView.findViewById(R.id.taskTitleTextView);
            holder.collaboratorList = (LinearLayout) convertView.findViewById(R.id.collaboratorsList);
            holder.taskStatus = (LinearLayout) convertView.findViewById(R.id.taskStatusLinearLayout);

            holder.collaborators[0] = (TextView) convertView.findViewById(R.id.collaboratorName1);
            holder.collaborators[1] = (TextView) convertView.findViewById(R.id.collaboratorName2);
            holder.collaborators[2] = (TextView) convertView.findViewById(R.id.collaboratorName3);
            holder.collaborators[3] = (TextView) convertView.findViewById(R.id.collaboratorName4);
            holder.collaborators[4] = (TextView) convertView.findViewById(R.id.collaboratorName5);
            holder.collaborators[5] = (TextView) convertView.findViewById(R.id.collaboratorName6);
            holder.collaborators[6] = (TextView) convertView.findViewById(R.id.collaboratorName7);
            holder.collaborators[7] = (TextView) convertView.findViewById(R.id.collaboratorName8);
            holder.collaborators[8] = (TextView) convertView.findViewById(R.id.collaboratorName9);
            holder.collaborators[9] = (TextView) convertView.findViewById(R.id.collaboratorName10);

            holder.actionsPlaceHolder = (LinearLayout) convertView.findViewById(R.id.actionsPlaceHolderLinearLayout);

            holder.action1 = (LinearLayout) holder.actionsPlaceHolder.findViewById(R.id.action1);
            holder.action2 = (LinearLayout) holder.actionsPlaceHolder.findViewById(R.id.action2);
            holder.action3 = (LinearLayout) holder.actionsPlaceHolder.findViewById(R.id.action3);
            holder.action4 = (LinearLayout) holder.actionsPlaceHolder.findViewById(R.id.action4);

            holder.actionImage4 = (ImageView) holder.action4.findViewById(R.id.action4Image);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.actionsPlaceHolder.setVisibility(View.GONE);


        Task task = this.taskList.get(position);

        Log.d(TAG, "Status: " + task.getStatus(getContext()));


        switch (viewType) {
            case VIEW_EXPANDED:
                holder.actionsPlaceHolder.setVisibility(View.VISIBLE);
                break;
            case VIEW_ITEM_NORMAL:
            default:
                holder.actionsPlaceHolder.setVisibility(View.GONE);
        }

        holder.taskStatus.setBackgroundResource(
                Task.getStatusColor(
                        task.getStatus(
                                activity.getApplicationContext()
                        )
                )
        );

        holder.taskTitle.setText(
                task.getName()
        );

        int collaboratorsToBeDisplayedCount = task.getCollaborators().size()<9 ? task.getCollaborators().size() : 9;

        for(int ctr=0; ctr<collaboratorsToBeDisplayedCount; ctr++) {
            Collaborator collaborator = task.getCollaborators().get(ctr);
            holder.collaborators[ctr].setText(collaborator.getInitials());
            holder.collaborators[ctr].setBackgroundResource(collaborator.getStatus());
        }

        return convertView;
    }
}
