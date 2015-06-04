package in.altersense.taskapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.ArraySwipeAdapter;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.R;
import in.altersense.taskapp.components.BaseApplication;
import in.altersense.taskapp.events.TaskEditedEvent;
import in.altersense.taskapp.models.Collaborator;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.models.User;

/**
 * Created by ashwineaso on 2/25/15.
 */
public class TaskDetailsViewAdapter extends ArraySwipeAdapter<Collaborator> {

    private static final String CLASS_TAG = "TaskDetailsViewAdapter";
    private Task task;
    //Declaring the variables used
    private Activity activity;
    private List<Collaborator> data;
    private static LayoutInflater inflater = null;
    public Resources res;
    Collaborator collaborator;

    //Constructor of custom adapter
    public TaskDetailsViewAdapter(Activity a, List<Collaborator> d, Task task) {
        super(a.getApplicationContext(), R.layout.collaborator_details_layout, d);
        //Assign the passed values
        this.activity = a;
        this.data = d;
        this.task = task;

        //Layout inflater to call the external xml layout
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (data.size() <=0)
            return 1;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.collSwipe;
    }

    //Create a HOLDER CLASS to contain the inflated xml elements
    public static class ViewHolder {
        public TextView collName;
        public TextView collInitials;
        public LinearLayout btnConfirm;
        public SwipeLayout collSwipeLayout;
    }

    //Depending upon the data size called for each row, Create each listview row
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {

            //Inflate the collaborator_details_layout for each row
            vi = inflater.inflate(R.layout.collaborator_details_layout, null);

            //Assign the values to the ViewHolder object to be given to the elements
            holder = new ViewHolder();
            holder.collName = (TextView)vi.findViewById(R.id.collName);
            holder.collInitials = (TextView)vi.findViewById(R.id.collInitials);

            //Set the confirm button
            holder.btnConfirm = (LinearLayout) vi.findViewById(R.id.btn_confirm);

            //Set holder with layout inflater
            vi.setTag(holder);
        }
        else
            holder = (ViewHolder)vi.getTag();

        //Setting the swipe layout
        holder.collSwipeLayout = (SwipeLayout) vi.findViewById(R.id.collSwipe);

        if (data.size()<=0) {
            holder.collSwipeLayout.setSwipeEnabled(false);
            holder.collName.setText("No Collaborators");
            holder.collInitials.setText("");
            holder.collInitials.setBackgroundColor(Color.WHITE);
        }
        else {
            collaborator = null;
            collaborator = data.get(position);

            //Set the Collaborator Model values in the Holder elements
            holder.collName.setText(collaborator.getName());
            holder.collInitials.setText(collaborator.getInitials());
            holder.collInitials.setBackgroundResource(task.collaboratorStatusBackground(collaborator.getStatus()));

            //Initializing the lists

            //Confirm collaborator deleteCollaborator
            holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.collSwipeLayout.close(true);
                    Log.d(CLASS_TAG, "Collaborator to remove" + collaborator.getName());
                    ArrayList<User> userRemovalList = new ArrayList<User>();
                    userRemovalList.add(collaborator);
                    data.remove(position);
                    task.updateCollaborators(new ArrayList<User>(), userRemovalList, activity.getApplicationContext());
                    notifyDataSetChanged();
                    holder.collSwipeLayout.close();

                    // TaskEditedEvent fired to denote change in task.
                    BaseApplication.getEventBus().post(new TaskEditedEvent());
                    Log.i(CLASS_TAG, "Posted TaskEditedEvent --> TaskFragmentsActivity");

                    Toast.makeText(activity.getApplicationContext(), "Collaborator Removed", Toast.LENGTH_LONG ).show();
                }
            });

            if(this.task.isOwnedyDeviceUser(activity.getApplicationContext())) {
                holder.collSwipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
                holder.collSwipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
                holder.collSwipeLayout.setSwipeEnabled(true);
            }
            else { holder.collSwipeLayout.setSwipeEnabled(false); }

        }

        //Finally return the view
        return vi;
    }

}
