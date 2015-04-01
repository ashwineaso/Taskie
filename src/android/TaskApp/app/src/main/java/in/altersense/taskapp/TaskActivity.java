package in.altersense.taskapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import in.altersense.taskapp.adapters.TaskDetailsViewAdapter;
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.database.CollaboratorDbHelper;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.models.Collaborator;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.requests.UpdateTaskRequest;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class TaskActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String CLASS_TAG = "TaskActivity";

    private static final String DATEPICKER_TAG = "datePicker";
    private static final String TIMEPICKER_TAG = "timePicker";

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

    private Task task;

    //Adapter implementation
    private ListView collList;
    private TaskDetailsViewAdapter adapter;

    public ArrayList<Collaborator> collaboratorArrayList = new ArrayList<Collaborator>();
    private EditText taskTitleET;
    private TextView taskTitleTV;
    private EditText taskDescriptionET;
    private TextView taskDescriptionTV;
    private TextView dueDateTV;
    private Spinner taskPrioritySpinner;
    private TextView taskPriorityTV;
    private TextView taskStatusTV;
    private TextView taskOwnerTV;
    private CompoundButton checkComplete;
    private ToggleButton editViewToggle;
    private ImageView calendarIV, cancelIV;

    private boolean isEditMode = false;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private String dueString = "";
    private long duelong = 0;

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
        final long taskId;
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
        this.taskTitleTV = (TextView) findViewById(R.id.taskTitleTextView);
        this.taskDescriptionET = (EditText) findViewById(R.id.taskDescriptionEditText);
        this.taskDescriptionTV = (TextView) findViewById(R.id.taskDescriptionTextView);
        this.dueDateTV = (TextView)findViewById(R.id.dueDateTextView);
        this.taskPrioritySpinner = (Spinner) findViewById(R.id.taskPrioritySpinner);
        this.taskPriorityTV = (TextView) findViewById(R.id.taskPriorityTextView);
        this.taskStatusTV = (TextView)findViewById(R.id.taskStatusTextView);
        this.taskOwnerTV = (TextView) findViewById(R.id.taskOwnerTV);
        this.checkComplete = (CompoundButton) findViewById(R.id.checkComplete);
        this.editViewToggle = (ToggleButton) findViewById(R.id.taskEditViewToggleButton);
        this.calendarIV = (ImageView) findViewById(R.id.calendarImageView);
        this.cancelIV = (ImageView) findViewById(R.id.btnCancelDate);

        // Initialize date time picker.
        final Calendar calendar = Calendar.getInstance();

        this.datePickerDialog = DatePickerDialog.newInstance(
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                false
        );
        this.timePickerDialog = TimePickerDialog.newInstance(
                this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false,
                false
        );

        //Set the text views
        this.taskTitleET.setText(this.task.getName());
        this.taskTitleTV.setText(this.task.getName());
        this.taskDescriptionET.setText(this.task.getDescription());
        this.taskDescriptionTV.setText(this.task.getDescription());
        this.dueDateTV.setText(this.task.getDueDateTime());
        this.taskPrioritySpinner.setSelection(this.task.getPriority());
        this.taskPriorityTV.setText(priorityToString(this.task.getPriority()));
        this.taskStatusTV.setText(statusToString(this.task.getStatus(getApplicationContext())));
        this.taskOwnerTV.setText(this.task.getOwner().getName());

        this.calendarIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentYear = calendar.get(Calendar.YEAR);
                datePickerDialog.setYearRange(currentYear, currentYear+50<2037?currentYear+50:2037);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });

        this.cancelIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dueDateTV.setText("");
                task.setDueDateTime(Long.parseLong(null));
            }
        });

        //Set a listener for the checkbox
        this.checkComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                task.toggleStatus(TaskActivity.this);
            }
        });

        this.editViewToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    setUpEditMode();
                } else {
                    setUpViewMode();
                }
            }
        });

        this.task.fetchAllCollaborators(this);

        //Fill the ArrayList with the required data
//        CollaboratorDbHelper collaboratorDbHelper = new CollaboratorDbHelper(getApplicationContext());
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
        // Update task params
        this.task.setName(this.taskTitleET.getText().toString());
        this.task.setDescription(this.taskDescriptionET.getText().toString());
        this.task.setPriority(this.taskPrioritySpinner.getSelectedItemPosition());
        if(duelong!=0) {
            this.task.setDueDateTime(this.duelong);
        }
        // Set mode.
        this.isEditMode = false;
        // Update TextViews
        this.taskTitleTV.setText(this.task.getName());
        this.taskDescriptionTV.setText(this.task.getDescription());
        this.taskPriorityTV.setText(priorityToString(this.task.getPriority()));
        this.dueDateTV.setText(this.task.getDueDateTime());
        // Hide edit views
        this.taskTitleET.setVisibility(View.GONE);
        this.taskDescriptionET.setVisibility(View.GONE);
        this.taskPrioritySpinner.setVisibility(View.GONE);
        this.calendarIV.setVisibility(View.GONE);
        this.cancelIV.setVisibility(View.GONE);
        // display display views
        this.taskTitleTV.setVisibility(View.VISIBLE);
        this.taskDescriptionTV.setVisibility(View.VISIBLE);
        this.taskPriorityTV.setVisibility(View.VISIBLE);
        // Update task
        this.task.setSyncStatus(false);
        this.task.updateTask(this);
        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest(
                this.task,
                getApplicationContext()
        );
        updateTaskRequest.execute();
    }

    /**
     * Enables an edit mode of the task.
     */
    private void setUpEditMode() {
        // update mode.
        this.isEditMode = true;
        // Hide dsiplay views
        this.taskTitleTV.setVisibility(View.GONE);
        this.taskDescriptionTV.setVisibility(View.GONE);
        this.taskPriorityTV.setVisibility(View.GONE);
        // display edit views
        this.taskTitleET.setVisibility(View.VISIBLE);
        this.taskDescriptionET.setVisibility(View.VISIBLE);
        this.taskPrioritySpinner.setVisibility(View.VISIBLE);
        this.calendarIV.setVisibility(View.VISIBLE);
        this.cancelIV.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        String TAG = CLASS_TAG + "onDateSet";
        Log.d(TAG, "Date: "+year+"-"+month+"-"+day);
        timePickerDialog.setCloseOnSingleTapMinute(false);
        timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
        this.dueString = year + "-" + (month+1) + "-" + day + " ";
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
        String TAG = CLASS_TAG + "onTimeSet";
        Log.d(TAG, "Time: "+ hour +":"+ minute);
        this.dueString += hour > 12 ? (hour-12) : hour;
        this.dueString +=":"+ minute +" ";
        this.dueString += hour > 12 ? "PM" : "AM";
        try {
            this.duelong = sdf.parse(this.dueString).getTime();
            this.dueDateTV.setText(this.task.dateToString(this.duelong));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
