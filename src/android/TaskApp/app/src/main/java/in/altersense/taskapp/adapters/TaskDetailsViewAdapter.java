package in.altersense.taskapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.altersense.taskapp.R;
import in.altersense.taskapp.models.Collaborator;

/**
 * Created by ashwineaso on 2/25/15.
 */
public class TaskDetailsViewAdapter extends BaseAdapter {

    //Declaring the variables used
    private Activity activity;
    private ArrayList<Collaborator> data;
    private static LayoutInflater inflater = null;
    public Resources res;
    Collaborator collaborator;

    //Constructor of custom adapter
    public TaskDetailsViewAdapter(Activity a, ArrayList d, Resources reslocal) {
        //Assign the passed values
        this.activity = a;
        this.data = d;
        this.res = reslocal;

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
        public LinearLayout collStatus;
    }


    //Depending upon the data size called for each row, Create each listview row
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

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
            holder.collInitials.setBackgroundResource(getBgColor(collaborator.getStatus()));
        }


        //Finally return the view
        return vi;
    }

    private int getBgColor(int status) {
        int backgroundResource = R.drawable.collaborator_status_declined;
        switch (status) {
            case -1:
                backgroundResource = R.drawable.collaborator_status_declined;
                break;
            case 1:
                backgroundResource = R.drawable.collaborator_status_accepted;
                break;
            case 2:
                backgroundResource = R.drawable.collaborator_status_done;
                break;
            case 0:
            default:
                backgroundResource = R.drawable.collaborator_status_pending;
        }
        return backgroundResource;
    }
}
