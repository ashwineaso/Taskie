package in.altersense.taskapp.adapters;

import android.app.Activity;
import android.content.Intent;
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

import in.altersense.taskapp.CreateTaskActivity;
import in.altersense.taskapp.R;
import in.altersense.taskapp.TaskActivity;
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.models.Collaborator;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.requests.BuzzCollaboratorRequest;

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

            // Basic layout init
            holder.taskTitle = (TextView) convertView.findViewById(R.id.taskTitleTextView);
            holder.collaboratorList = (LinearLayout) convertView.findViewById(R.id.collaboratorsList);
            holder.taskStatus = (LinearLayout) convertView.findViewById(R.id.taskStatusLinearLayout);

            // Collaborators display
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

            // Actiosn view placeholder init.
            holder.actionsPlaceHolder = (LinearLayout) convertView.findViewById(R.id.actionsPlaceHolderLinearLayout);

            // Actions init.
            holder.action1 = (LinearLayout) holder.actionsPlaceHolder.findViewById(R.id.action1);
            holder.action2 = (LinearLayout) holder.actionsPlaceHolder.findViewById(R.id.action2);
            holder.action3 = (LinearLayout) holder.actionsPlaceHolder.findViewById(R.id.action3);
            holder.action4 = (LinearLayout) holder.actionsPlaceHolder.findViewById(R.id.action4);

            // Actions images.
            holder.actionImage4 = (ImageView) holder.action4.findViewById(R.id.action4Image);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // Hiding the actions placeholder by default.
        holder.actionsPlaceHolder.setVisibility(View.GONE);
        // Hiding all collaborators layout
        for(int ctr=0;ctr<10;ctr++) {
            holder.collaborators[ctr].setVisibility(View.GONE);
        }

        // Getting current task
        final Task task = this.taskList.get(position);

        // Action clicklisteners.
        holder.action1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), TaskActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Config.REQUEST_RESPONSE_KEYS.UUID.getKey(),task.getId());
                activity.startActivity(intent);
            }
        });

        holder.action2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), CreateTaskActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Config.REQUEST_RESPONSE_KEYS.UUID.getKey(),task.getId());
                activity.startActivity(intent);
            }
        });

        holder.action3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuzzCollaboratorRequest buzzCollaboratorRequest = new BuzzCollaboratorRequest(task, activity);
                buzzCollaboratorRequest.execute();
            }
        });

        holder.action4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.toggleStatus(activity);
                notifyDataSetChanged();
            }
        });

        // Checking whether user already clicked it.
        switch (viewType) {
            case VIEW_EXPANDED:
                holder.actionsPlaceHolder.setVisibility(View.VISIBLE);
                break;
            case VIEW_ITEM_NORMAL:
            default:
                holder.actionsPlaceHolder.setVisibility(View.GONE);
        }

        // Setting task status strip
        holder.taskStatus.setBackgroundResource(
                Task.getStatusColor(
                        task.getStatus(
                                activity.getApplicationContext()
                        )
                )
        );

        // Setting task title
        holder.taskTitle.setText(
                task.getName()
        );
        Log.d(TAG, "Task: " + task.getName());


        //Check whether the count of collaborators are more than 10 if not set number of colaborators to be displayed as 8
        int collaboratorsToBeDisplayedCount = task.getCollaborators().size()<8 ? task.getCollaborators().size() : 8;
        Log.d(TAG, "CollaboratorCount: " + collaboratorsToBeDisplayedCount);


        // Display owners status
        holder.collaborators[0].setText(task.getOwner().getInitials());
        holder.collaborators[0].setBackgroundResource(task.collaboratorStatusBackground(task.getStatus()));
        holder.collaborators[0].setVisibility(View.VISIBLE);

        // display initials and status of the collaborators
        for(int ctr=0; ctr<collaboratorsToBeDisplayedCount; ctr++) {
            Collaborator collaborator = task.getCollaborators().get(ctr);
            Log.d(TAG, "Collaborator : " + collaborator.toString());
            holder.collaborators[ctr+1].setText(collaborator.getInitials());
            holder.collaborators[ctr+1].setBackgroundResource(task.collaboratorStatusBackground(collaborator.getStatus()));
            holder.collaborators[ctr+1].setVisibility(View.VISIBLE);
        }

        // display more collaborators if the count is greater than 8
        if(collaboratorsToBeDisplayedCount<task.getCollaborators().size()) {
            int remaining = task.getCollaborators().size()-collaboratorsToBeDisplayedCount;
            String messageText = "+"+remaining+" MORE";
            holder.collaborators[9].setText(messageText);
            holder.collaborators[9].setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
