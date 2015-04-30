package in.altersense.taskapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.altersense.taskapp.R;
import in.altersense.taskapp.models.Notification;
import in.altersense.taskapp.models.Task;

/**
 * Created by ashwineaso on 4/23/15.
 */
public class NotificationAdapter extends ArrayAdapter<Notification> {

    private static final String CLASS_TAG = "NotificationAdapter";

    private final Task task;
    private final List<Notification> notificationList;
    private final Activity activity;
    private final LayoutInflater inflater;
    private Notification notification;

    //Constructor of the custom adapter
    public NotificationAdapter(Activity activity, List<Notification> notificationList, Task task) {
        super(activity.getApplicationContext(), R.layout.notification_list_layout, notificationList);
        //Assigning the passed value
        this.activity = activity;
        this.notificationList = notificationList;
        this.task = task;

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder {
        public TextView notificationMessage;
        public TextView timeStamp;
        public ImageView imgNotif;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {
            //Inflate the notification_list_layout for each row
            vi = inflater.inflate(R.layout.notification_list_layout, null);

            holder = new ViewHolder();
            holder.notificationMessage = (TextView) vi.findViewById(R.id.txt_message);
            holder.timeStamp = (TextView) vi.findViewById(R.id.txt_timeStamp);
            holder.imgNotif = (ImageView) vi.findViewById(R.id.imgNotif);

            vi.setTag(holder);
        }
        else
            holder = (ViewHolder)vi.getTag();


        notification = null;
        notification = notificationList.get(position);
        holder.notificationMessage.setText(notification.getMessage());
        holder.timeStamp.setText(dateToString(notification.getDateTime()));
        holder.imgNotif.setImageResource(notification.getSymbol(notification.getType()));
        Log.d(CLASS_TAG, "Notification timestap" + notification.getDateTime());
        return vi;
    }

    /**
     * Converts the datetime from long to the format "Wed, Jun 6, 2015 12:45 AM"
     * @tempDateTime = gets long format of the dueDateTime
     * @return dateTime - String format of dueDateTime
     */
    public String dateToString(long dueDateTime) {
        String dateTime = "";
        if (dueDateTime == 0) { return dateTime; }
        Date date = new Date(dueDateTime * 1000); //Converting time to milliseconds for the sdf to work
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a");
        dateTime = sdf.format(date);
        return dateTime;
    }


}
