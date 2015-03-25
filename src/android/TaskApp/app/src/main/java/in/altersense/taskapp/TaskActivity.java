package in.altersense.taskapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.altersense.taskapp.adapters.TaskDetailsViewAdapter;
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.database.CollaboratorDbHelper;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.models.Collaborator;
import in.altersense.taskapp.models.Task;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class TaskActivity extends ActionBarActivity {

    private static final String CLASS_TAG = "TaskActivity";
    private Task task;
    List<Collaborator> collaboratorList;

    //Adapter implementation
    ListView collList;
    TaskDetailsViewAdapter adapter;
    public TaskActivity activity;
    public ArrayList<Collaborator> collaboratorArrayList = new ArrayList<Collaborator>();
    private TextView taskTitleTV;
    private TextView taskDescriptionTV;
    private TextView dueDateTV;
    private TextView taskPriorityTV;
    private TextView taskStatusTV;
    private TextView taskOwnerTV;
    private LinearLayout taskHeaderLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        Setting up calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Cabin-Medium-TTF.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.activity_task);

        String TAG = CLASS_TAG+ " OnCreate";
        //Get the intent
        Intent createViewIntent = getIntent();
        long taskId;
        //Check whether there is an EXTRA with the intent
        if (createViewIntent.hasExtra(Config.REQUEST_RESPONSE_KEYS.UUID.getKey())) {
            Log.d(TAG, "Intent has taskID");
            taskId = createViewIntent.getExtras().getLong(
                    Task.ID
            );
            Log.d(TAG, "TaskID: "+taskId);
            TaskDbHelper taskDbHelper = new TaskDbHelper(TaskActivity.this);
            // If yes fetch task from the uuid
            Log.d(TAG, "Fetching row from the db");
            this.task = taskDbHelper.getTaskByRowId(taskId);
        }


        //Initialize the views
        this.taskTitleTV = (TextView)findViewById(R.id.taskTitleTextView);
        this.taskDescriptionTV = (TextView)findViewById(R.id.taskDescriptionTextView);
        this.dueDateTV = (TextView)findViewById(R.id.dueDateTextView);
        this.taskPriorityTV = (TextView)findViewById(R.id.taskPriorityTextView);
        this.taskStatusTV = (TextView)findViewById(R.id.taskStatusTextView);
        this.taskOwnerTV = (TextView) findViewById(R.id.taskOwnerTV);
        this.taskHeaderLL = (LinearLayout) findViewById(R.id.taskHeaderLinearLayout);

        //Set the text views
        this.taskTitleTV.setText(this.task.getName());
        this.taskDescriptionTV.setText(this.task.getDescription());
        this.dueDateTV.setText(this.task.getDueDateTime());
        this.taskPriorityTV.setText(priorityToString(this.task.getPriority()));
        this.taskStatusTV.setText("Status : " + statusToString(this.task.getStatus(getApplicationContext())));
        this.taskOwnerTV.setText(this.task.getOwner().getName());

        //Fill the ArrayList with the required data
        CollaboratorDbHelper collaboratorDbHelper = new CollaboratorDbHelper(getApplicationContext());
        this.collaboratorList = collaboratorDbHelper.getAllCollaborators(this.task);
        Log.d(TAG, "Fetched collaborator : " + collaboratorList.toString());
        for (Collaborator collaborator: this.collaboratorList) {
            collaboratorArrayList.add(collaborator);
        }

        this.taskHeaderLL.setBackgroundResource(Task.getStatusColor(task.getStatus()));

        Resources res = getResources();

        collList = (ListView)findViewById(R.id.collListView);
        //Create a custom adapter
        adapter = new TaskDetailsViewAdapter(TaskActivity.this, collaboratorArrayList, res);
        collList.setAdapter(adapter);
        //Adjust the height of the ListView to accommodate all the children
        setListViewHeightBasedOnChildren(collList);
        collList.setFocusable(false); //To set the focus to top #glitch

    }

    /**
     * Convert the priority from int format to apropriate string format
     */
    private String priorityToString(int priority) {
        String priorityToString = "";
        switch (priority) {
            case 0 : priorityToString = "Low Priority"; break;
            case 1 : priorityToString = "Normal Priority"; break;
            case 2 : priorityToString = "High Priority"; break;
        }
        return priorityToString;
    }

    /**
     * Convert the status from int format to apropriate string format
     */
    private String statusToString(Integer status) {
        String statusAsString = "Undefined";
        switch (status) {
            case 0 : statusAsString = "Not Accepted"; break;
            case 1 : statusAsString = "Ongoing"; break;
            case 2 : statusAsString = "Completed"; break;
            case -1 : statusAsString = "Declined"; break;
        }
        return statusAsString;
    }

    /**
     * Calligraphy attached to new
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
