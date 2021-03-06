package in.altersense.taskapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.CursorSwipeAdapter;

import java.util.Calendar;
import java.util.List;

import in.altersense.taskapp.R;
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.models.Collaborator;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.models.User;

/**
 * Created by mahesmohan on 2/26/15.
 */
public class TasksAdapter extends CursorSwipeAdapter{

    private static final String CLASS_TAG = "TasksCursorAdapter ";
    private static final int MAX_COLLABORATORS_DISPLAYED = 8;
    private final Context context;
    private final TaskDbHelper taskDbHelper;

    private LayoutInflater inflater;
    private Activity activity;
    private final User deviceOwner;

    public Context getContext() {
        return context;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.taskSwipe;
    }

    @Override
    public void closeAllItems() {
    }

    public static class ViewHolder{
        public LinearLayout taskStatus;
        public TextView taskTitle, dueDateTimeTV;
        public LinearLayout collaboratorList;

        public TextView[] collaborators = new TextView[10];
        public LinearLayout btnConfirm;
        public CheckBox checkComplete;
        public SwipeLayout taskSwipeLayout;
    }

    public TasksAdapter(Activity activity, Cursor tasksAsCursor) {
        super(
                activity.getApplicationContext(),
                tasksAsCursor,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.inflater = activity.getLayoutInflater();
        this.deviceOwner = User.getDeviceOwner(getContext());
        this.taskDbHelper = new TaskDbHelper(getContext());
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        View convertView = inflater.inflate(R.layout.task_panel, parent, false);

        // Basic layout init
        holder.taskTitle = (TextView) convertView.findViewById(R.id.taskTitleTextView);
        holder.collaboratorList = (LinearLayout) convertView.findViewById(R.id.collaboratorsList);
        holder.taskStatus = (LinearLayout) convertView.findViewById(R.id.taskStatusLinearLayout);
        holder.checkComplete = (CheckBox) convertView.findViewById(R.id.checkComplete);
        holder.taskSwipeLayout = (SwipeLayout) convertView.findViewById(R.id.taskSwipe);

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

        //Due Date Time
        holder.dueDateTimeTV = (TextView) convertView.findViewById(R.id.dueDateTimeTextView);

        //Swipe : Configuring the swipe view.
        holder.taskSwipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.taskSwipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);

        //Swipe : Delete Confirm and Undo buttons
        holder.btnConfirm = (LinearLayout) convertView.findViewById(R.id.btn_confirm);

        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String TAG = CLASS_TAG+"getView";
        final ViewHolder holder = (ViewHolder) view.getTag();

        // Hiding all collaborators layout
        for(int ctr=0;ctr<10;ctr++) {
            holder.collaborators[ctr].setVisibility(View.GONE);
        }

        // Getting current task
        final Task task = new Task(cursor, getContext());

        // Setting task status strip
        holder.taskStatus.setBackgroundResource(
                Task.getStatusColor(
                        task.getStatus(
                                this.getContext()
                        )
                )
        );

        // Setting task title
        holder.taskTitle.setText(
                task.getName()
        );

        // Display owners status
        holder.collaborators[0].setText(task.getOwner().getInitials());
        holder.collaborators[0].setBackgroundResource(task.collaboratorStatusBackground(task.getStatus()));
        holder.collaborators[0].setVisibility(View.VISIBLE);

        //Check whether the count of collaborators are more than 10 if not set number of colaborators to be displayed as 8
        //First populate all collaborators into a list
        final List<Collaborator> collaboratorList = task.getCollaborators(task, this.getContext());
        try {
            int collaboratorsToBeDisplayedCount = collaboratorList.size()<MAX_COLLABORATORS_DISPLAYED ? collaboratorList.size() : MAX_COLLABORATORS_DISPLAYED;

            // display initials and status of the collaborators
            for(int ctr=0; ctr<collaboratorsToBeDisplayedCount; ctr++) {
                Collaborator collaborator = collaboratorList.get(ctr);
                holder.collaborators[ctr+1].setText(collaborator.getInitials());
                holder.collaborators[ctr+1].setBackgroundResource(task.collaboratorStatusBackground(collaborator.getStatus()));
                holder.collaborators[ctr+1].setVisibility(View.VISIBLE);
            }

            // display more collaborators if the count is greater than 8
            if(collaboratorsToBeDisplayedCount < collaboratorList.size()) {
                int remaining = task.getCollaborators().size()-collaboratorsToBeDisplayedCount;
                String messageText = "+"+remaining+" MORE";
                holder.collaborators[9].setText(messageText);
                holder.collaborators[9].setVisibility(View.VISIBLE);
            }

        } catch (NullPointerException e) {
            Log.d(TAG, "Cannot find collaborators");
        }

        holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.taskSwipeLayout.close(true); //Close the list item smoothly
                Toast.makeText(activity.getApplicationContext(), "Task Deleted", Toast.LENGTH_LONG).show();
                task.setStatus(Config.TASK_STATUS.DELETED.getStatus(), activity); //Change the task status to deleted
                changeCursor(taskDbHelper.getAllNonGroupTasksAsCursor());
                notifyDataSetChanged();
            }
        });



        //CheckBox to toggle task status
        holder.checkComplete.setChecked(task.getStatus(getContext()) == 2);
        holder.checkComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the status
                task.toggleStatus(getContext());
                // Set the checkbox checked state according to the status
                holder.checkComplete.setChecked(task.getStatus(getContext()) == 2);
                // Change the task status strip color
                holder.taskStatus.setBackgroundResource(
                        Task.getStatusColor(
                                task.getStatus(
                                        getContext()
                                )
                        )
                );
                // Checks users ownnership of the task
                if (task.isOwnedyDeviceUser(getContext())) {
                    // if owner change the background resource of the first collaborator slot
                    // with the task status color
                    holder.collaborators[0].setBackgroundResource(
                            task.collaboratorStatusBackground(task.getStatus())
                    );
                } else {
                    // if collaborator find the slot and change the ring color
                    // find the collaborator to find the slot position
                    for (Collaborator collaborator : collaboratorList) {
                        if (collaborator.getEmail().equals(deviceOwner.getEmail())) {
                            // update the collaborator to get the status
                            collaborator = taskDbHelper.getCollaborator(task, collaborator);
                            // find the slot position.
                            int deviceUserPosition = collaboratorList.indexOf(collaborator);
                            // check whether it is in the limits of the user.
                            if (deviceUserPosition >= 0 && deviceUserPosition < MAX_COLLABORATORS_DISPLAYED) {
                                // Set status ring color
                                // Increments the slot position by 1 to account for the task owner
                                /// being at postion 0
                                holder.collaborators[deviceUserPosition + 1].setBackgroundResource(
                                        task.collaboratorStatusBackground(collaborator.getStatus())
                                );
                            }
                        }
                    }
                }
            }
        });

        //Display the Due Date time
        long dueDateTime = task.getDueDateTimeAsLong();
        long todayStartTime = getStartTime();
        if (dueDateTime == 0) {
            holder.dueDateTimeTV.setText(null);
        }
        else if (dueDateTime - todayStartTime < 0) {
            holder.dueDateTimeTV.setText("Due on "+task.getDueDateTime());
            holder.dueDateTimeTV.setTextColor(Color.DKGRAY);
        }
        else if(dueDateTime - todayStartTime < 86400000) {
            holder.dueDateTimeTV.setText("Due Today");
            holder.dueDateTimeTV.setTextColor(Color.rgb(255, 186, 26));
        }
        else if (dueDateTime - todayStartTime < 172800000) {
            holder.dueDateTimeTV.setText("Due Tomorrow");
            holder.dueDateTimeTV.setTextColor(Color.rgb(255, 186, 26));
        }
        else {
            holder.dueDateTimeTV.setText("Due on "+task.getDueDateTime());
            holder.dueDateTimeTV.setTextColor(Color.DKGRAY);
        }
    }

    private long getStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
