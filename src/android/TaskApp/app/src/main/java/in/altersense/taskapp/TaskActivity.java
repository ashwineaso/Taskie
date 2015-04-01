package in.altersense.taskapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
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

    //Adapter implementation
    private ListView collList;
    private TaskDetailsViewAdapter adapter;

    public ArrayList<Collaborator> collaboratorArrayList = new ArrayList<Collaborator>();
    private EditText taskTitleET;
    private EditText taskDescriptionET;
    private TextView dueDateTV;
    private Spinner taskPrioritySpinner;
    private TextView taskStatusTV;
    private TextView taskOwnerTV;
    private CompoundButton checkComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String TAG = CLASS_TAG+ " OnCreate";
        super.onCreate(savedInstanceState);
        //        Setting up calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Cabin-Medium-TTF.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.activity_task);

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
        this.taskTitleET = (EditText) findViewById(R.id.taskTitleEditText);
        this.taskDescriptionET = (EditText) findViewById(R.id.taskDescriptionEditText);
        this.dueDateTV = (TextView)findViewById(R.id.dueDateTextView);
        this.taskPrioritySpinner = (Spinner) findViewById(R.id.taskPrioritySpineer);
        this.taskStatusTV = (TextView)findViewById(R.id.taskStatusTextView);
        this.taskOwnerTV = (TextView) findViewById(R.id.taskOwnerTV);
        this.checkComplete = (CompoundButton) findViewById(R.id.checkComplete);

        //Set the text views
        this.taskTitleET.setText(this.task.getName());
        this.taskDescriptionET.setText(this.task.getDescription());
        this.dueDateTV.setText(this.task.getDueDateTime());
        // TODO: Fix the task priority to function like a spinner.
//        this.taskPrioritySpinner.setText(priorityToString(this.task.getPriority()));
        this.taskStatusTV.setText(statusToString(this.task.getStatus(getApplicationContext())));
        this.taskOwnerTV.setText(this.task.getOwner().getName());

        //Set a listener for the checkbox
        this.checkComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                task.toggleStatus(TaskActivity.this);
            }
        });

        this.task.fetchAllCollaborators(this);

        //Fill the ArrayList with the required data
        CollaboratorDbHelper collaboratorDbHelper = new CollaboratorDbHelper(getApplicationContext());
//        this.task.getCollaborators() = collaboratorDbHelper.getAllCollaborators(this.task);
        Log.d(TAG, "Fetched collaborator : " + this.task.getCollaborators().toString());
        for (Collaborator collaborator: this.task.getCollaborators()) {
            collaboratorArrayList.add(collaborator);
        }

        collList = (ListView)findViewById(R.id.collListView);
        //Create a custom adapter
        adapter = new TaskDetailsViewAdapter(TaskActivity.this, collaboratorArrayList, this.task);
        collList.setAdapter(adapter);
        //Adjust the height of the ListView to accommodate all the children
        setListViewHeightBasedOnChildren(collList);
        collList.setFocusable(false); //To set the focus to top #glitch

        setUpViewMode();

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

    /**
     * Sets up the activity to task view mode.
     */
    private void setUpViewMode() {
        this.setTheme(R.style.TaskViewTheme);
        this.taskPrioritySpinner.setEnabled(false);
    }

}
