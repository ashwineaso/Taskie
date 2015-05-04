package in.altersense.taskapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import in.altersense.taskapp.adapters.NotificationAdapter;
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.BaseApplication;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.models.Buzz;
import in.altersense.taskapp.models.Notification;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.requests.BuzzCollaboratorRequest;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by ashwineaso on 4/22/15.
 */
public class NotificationFragment extends Fragment {

    private static final String CLASS_TAG = "NotficationFragment";

    private Context context;
    private TaskDbHelper taskDbHelper;
    private Intent createViewIntent;
    private Task task;
    private List<Notification> notificationList;
    private ListView notifList;
    private NotificationAdapter adapter;
    private TextView noNotification;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_fragment_view, container, false);

        //retrieve the notification list by id
        this.notifList = (ListView) view.findViewById(R.id.notifList);
        this.noNotification = (TextView) view.findViewById(R.id.txt_no_notif);
        final long taskId;
        //Check whether there is an EXTRA with the intent
        if (createViewIntent.hasExtra(Config.REQUEST_RESPONSE_KEYS.UUID.getKey())) {
            Log.d(CLASS_TAG, "Intent has taskID");
            taskId = createViewIntent.getExtras().getLong(
                    Task.ID
            );
            Log.d(CLASS_TAG, "TaskID: "+taskId);

            // If yes fetch task from the uuid
            Log.d(CLASS_TAG, "Fetching row from the db");
            this.task = taskDbHelper.getTaskByRowId(taskId);
        }

        this.notificationList = taskDbHelper.retrieveNotification(this.task);

        if(notificationList.size()<=0) {
            Log.d(CLASS_TAG, "No notification to display");
            this.noNotification.setVisibility(View.VISIBLE);
        }
        else {
            this.noNotification.setVisibility(View.GONE);
            //Create a custom adapter
            adapter = new NotificationAdapter(getActivity(), this.notificationList, this.task);
            notifList.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        String TAG = CLASS_TAG + " OnCreate";

        //        Setting up calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Cabin-Medium-TTF.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        setHasOptionsMenu(true);

        //Obtain the context
        context = getActivity().getApplicationContext();

        //Get the TaskDbHelper
        taskDbHelper = new TaskDbHelper(context);
        BaseApplication.getEventBus().register(this);
        //Get the Intent from the Parent Activity
        createViewIntent = getActivity().getIntent();

    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
//        Inflate the menu; this adds items to the action bar if it is present.
//        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.menu_notification, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_clear_notifs:
                taskDbHelper.deleteNotifications(this.task);
                adapter.notifyDataSetChanged();

        }

        return super.onOptionsItemSelected(item);
    }
}
