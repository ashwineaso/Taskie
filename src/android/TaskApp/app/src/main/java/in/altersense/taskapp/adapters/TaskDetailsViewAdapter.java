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
    private ArrayList<User> userAdditonList, userRemovalList;
    private SwipeLayout collSwipeLayout;

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
    }


    //Depending upon the data size called for each row, Create each listview row
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

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
        this.collSwipeLayout = (SwipeLayout) vi.findViewById(R.id.collSwipe);

        if (data.size()<=0) {
            this.collSwipeLayout.setSwipeEnabled(false);
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
            this.userAdditonList = new ArrayList<>();
            this.userRemovalList = new ArrayList<>();

            //Confirm collaborator deleteCollaborator
            holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(CLASS_TAG, "Collaborator to remove" + collaborator.getName());
                    userRemovalList.add(collaborator);
                    task.updateCollaborators(userAdditonList, userRemovalList, activity.getApplicationContext());
                    data.remove(collaborator);
                    notifyDataSetChanged();
                    Toast.makeText(activity.getApplicationContext(), "Collaborator Removed", Toast.LENGTH_LONG ).show();
                }
            });

            if(this.task.isOwnedyDeviceUser(activity.getApplicationContext())) {
                this.collSwipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
                this.collSwipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
                this.collSwipeLayout.setSwipeEnabled(true);
            }
            else { this.collSwipeLayout.setSwipeEnabled(false); }

        }

        //Finally return the view
        return vi;
    }

}
