package in.altersense.taskapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.altersense.taskapp.R;
import in.altersense.taskapp.models.Collaborator;
import in.altersense.taskapp.models.Task;

/**
 * Created by ashwineaso on 2/25/15.
 */
public class TaskDetailsViewAdapter extends BaseAdapter {

    private Task task;
    //Declaring the variables used
    private Activity activity;
    private ArrayList<Collaborator> data;
    private static LayoutInflater inflater = null;
    public Resources res;
    Collaborator collaborator;

    //Constructor of custom adapter
    public TaskDetailsViewAdapter(Activity a, ArrayList d, Task task) {
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

    //Create a HOLDER CLASS to contain the inflated xml elements
    public static class ViewHolder {
        public TextView collName;
        public TextView collInitials;
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

            //Set holder with layout inflater
            vi.setTag(holder);
        }
        else
            holder = (ViewHolder)vi.getTag();

        if (data.size()<=0) {
            holder.collName.setText("No Collaborators");
        }
        else {
            collaborator = null;
            collaborator = data.get(position);

            //Set the Collaborator Model values in the Holder elements
            holder.collName.setText(collaborator.getName());
            holder.collInitials.setText(collaborator.getInitials());
            holder.collInitials.setBackgroundResource(task.collaboratorStatusBackground(collaborator.getStatus()));
        }

        //Finally return the view
        return vi;
    }

}
