package in.altersense.taskapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.squareup.otto.Subscribe;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.altersense.taskapp.adapters.TaskDetailsViewAdapter;
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.components.BaseApplication;
import in.altersense.taskapp.customviews.TokenCompleteCollaboratorsEditText;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.database.UserDbHelper;
import in.altersense.taskapp.events.ChangeInTaskEvent;
import in.altersense.taskapp.events.TaskDeletedEvent;
import in.altersense.taskapp.events.UserRemovedFromCollaboratorsEvent;
import in.altersense.taskapp.models.Collaborator;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.models.User;
import in.altersense.taskapp.requests.UpdateTaskRequest;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class TaskActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, TokenCompleteTextView.TokenListener {

    private static final String CLASS_TAG = "TaskActivity";

    private static final String DATEPICKER_TAG = "datePicker";
    private static final String TIMEPICKER_TAG = "timePicker";
    private static final int EDIT_MENU = 0;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

    private Task task;
    List<Collaborator> collaboratorList;

    //Adapter implementation
    private ListView collList;
    private TaskDetailsViewAdapter adapter;
    private FilteredArrayAdapter collabListAdapter;

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
    private TextView noCollText;
    private CompoundButton checkComplete;
    private List<User> userAdditonList, userRemovalList;
    private ImageView calendarIV, cancelIV;
    private ImageView addCollabsIV;
    private LinearLayout addCollabsLinearLayout;
    private TokenCompleteCollaboratorsEditText collaboratorsTCET;
    private Button addCollaboratorButton;

    private boolean isEditMode = false;
    private boolean isCollabAdditionMode = false;
    private Intent resultIntent;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private String dueString = "";
    private long duelong = 0;
    private MenuItem editViewToggle;

    private TaskDbHelper taskDbHelper;

    private List<User> userList;
    private List<User> collaboratorAdditionList = new ArrayList<>();

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

        taskDbHelper = new TaskDbHelper(TaskActivity.this);

        BaseApplication.getEventBus().register(this);

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

            // If yes fetch task from the uuid
            Log.d(TAG, "Fetching row from the db");
            this.task = taskDbHelper.getTaskByRowId(taskId);
            //Mark the notiifcations as seen
            taskDbHelper.markNotificationSeen(this.task);
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
        this.calendarIV = (ImageView) findViewById(R.id.calendarImageView);
        this.cancelIV = (ImageView) findViewById(R.id.btnCancelDate);
        this.addCollabsIV = (ImageView) findViewById(R.id.addCollaboratorsImageView);
        this.addCollabsLinearLayout = (LinearLayout) findViewById(R.id.addCollaboratorsLinearLayout);
        this.collaboratorsTCET = (TokenCompleteCollaboratorsEditText) findViewById(R.id.collaboratorsTokenEditText);
        this.addCollaboratorButton = (Button) findViewById(R.id.addCollaboratorButton);
        this.collList = (ListView)findViewById(R.id.collListView);

        //Hide the addCollabsIV if the user is not owner
        if (!this.task.isOwnedyDeviceUser(getApplicationContext())) {
            this.addCollabsIV.setVisibility(View.GONE);
        }
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
        setUpTextViews();

        // Setting up user list for token edit text
        UserDbHelper userDbHelper = new UserDbHelper(this);
        // Setting a Filtered Array Adapter to autocomplete with users in db
        userList = userDbHelper.listAllUsers();
        Log.d(CLASS_TAG, "User list: "+userList.toString());


        // Setup collaborator list
        setUpCollabsList();

        this.addCollaboratorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.updateCollaborators(
                        collaboratorAdditionList,
                        new ArrayList<User>(),
                        getApplicationContext()
                );
                adapter.clear();
                adapter.addAll(task.getCollaborators());
                adapter.notifyDataSetChanged();
                toggleAddCollaborators();
                collaboratorsTCET.clear();
            }
        });

        this.collaboratorsTCET.setTokenListener(this);

        this.addCollabsIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAddCollaborators();
            }
        });

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
                duelong = 0;
                dueString = task.dateToString(duelong);
                dueDateTV.setText(dueString);
            }
        });

        //Set a listener for the checkbox
        this.checkComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                task.toggleStatus(TaskActivity.this);
            }
        });

        this.task.fetchAllCollaborators(this);

        //Fill the ArrayList with the required data
        this.collaboratorList = task.getCollaborators(this.task, getApplicationContext());
        //Create a custom adapter
        adapter = new TaskDetailsViewAdapter(TaskActivity.this, collaboratorList, this.task);
        collList.setAdapter(adapter);
        //Adjust the height of the ListView to accommodate all the children
        setListViewHeightBasedOnChildren(collList);
        collList.setFocusable(false); //To set the focus to top #glitch

        this.resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);

    }

    private void setUpCollabsList() {
        this.collabListAdapter = new FilteredArrayAdapter<User>(this, R.layout.collaorator_list_layout, userList) {
            @Override
            protected boolean keepObject(User user, String s) {
                s = s.toLowerCase();
                return user.getName().toLowerCase().startsWith(s) || user.getEmail().toLowerCase().startsWith(s);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView==null) {
                    LayoutInflater l = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.collaorator_list_layout, parent, false);
                }

                User u = getItem(position);
                ((TextView)convertView.findViewById(R.id.name)).setText(u.getName());
                ((TextView)convertView.findViewById(R.id.email)).setText(u.getEmail());

                return convertView;
            }
        };
        this.collaboratorsTCET.setAdapter(collabListAdapter);
    }

    private void setUpTextViews() {
        this.taskTitleET.setText(this.task.getName());
        this.taskTitleTV.setText(this.task.getName());
        this.taskDescriptionET.setText(this.task.getDescription());
        this.taskDescriptionTV.setText(this.task.getDescription());
        this.dueDateTV.setText(this.task.getDueDateTime());
        this.taskPrioritySpinner.setSelection(this.task.getPriority());
        this.taskPriorityTV.setText(priorityToString(this.task.getPriority()));
        this.taskStatusTV.setText(statusToString(this.task.getStatus(getApplicationContext())));
        this.taskOwnerTV.setText(this.task.getOwner().getName());
    }

    private void toggleAddCollaborators() {
        if(this.isCollabAdditionMode) {
            this.addCollabsLinearLayout.setVisibility(View.GONE);
            this.isCollabAdditionMode = false;
        } else {
            this.addCollabsLinearLayout.setVisibility(View.VISIBLE);
            this.isCollabAdditionMode = true;
        }
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
        String TAG = CLASS_TAG+"setUpViewMode";
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
        this.checkComplete.setVisibility(View.VISIBLE);
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

        // Update resultIntent flag to re-query the list of tasks in dashboard
        this.resultIntent.putExtra(Config.SHARED_PREF_KEYS.UPDATE_LIST.getKey(), true);

    }

    /**
     * Enables an edit mode of the task.
     */
    private void setUpEditMode() {
        // update mode.
        this.isEditMode = true;
        // Hide dsiplay views
        this.checkComplete.setVisibility(View.GONE);
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

    @Override
    public void onBackPressed() {
        String TAG = CLASS_TAG+"onBackPressed";
        if(this.isEditMode) {
            // Set mode.
            this.isEditMode = false;
            // Update TextViews
            this.taskTitleET.setText(this.task.getName());
            this.taskDescriptionET.setText(this.task.getDescription());
            this.taskPrioritySpinner.setSelection(this.task.getPriority());
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
            // Set toggle button to off
            this.editViewToggle.setIcon(R.drawable.ic_edit_white);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_task, menu);
        this.editViewToggle = menu.findItem(R.id.action_toggle_view_edit);
        if (!task.isOwnedyDeviceUser(getApplicationContext())) {
            this.editViewToggle.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_toggle_view_edit:
                if(isEditMode) {
                    editViewToggle.setIcon(R.drawable.ic_edit_white);
                    editViewToggle.setTitle("Edit");
                    setUpViewMode();

                } else {
                    editViewToggle.setIcon(R.drawable.ic_save_white_36dp);
                    editViewToggle.setTitle("Save");
                    setUpEditMode();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTokenAdded(Object o) {
        String TAG = CLASS_TAG+"onTokenAdded";
        try{
            User userObject = (User) o;
            if(AltEngine.verifyEmail(userObject.getEmail())) {
                Log.d(TAG, "Valid email.");
                this.collaboratorAdditionList.add((User) o);
                Log.d(TAG, "Added: " + o.toString());
                String listOfCollabs = "";
                for(User user:this.collaboratorAdditionList) {
                    listOfCollabs+=user.getEmail()+",";
                }
                Log.d(TAG, "New List: "+listOfCollabs);
                adapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "Invalid email.");
                collaboratorsTCET.removeObject(o);
                Log.d(TAG, "Object removed.");
                Toast.makeText(
                        this,
                        Config.MESSAGES.INVALID_EMAIL.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "Nothing added.");
        }
    }

    @Override
    public void onTokenRemoved(Object o) {
        String TAG = CLASS_TAG+"onTokenRemoved";
        try {
            User userObject = (User) o;
            if(this.collaboratorAdditionList.contains(userObject)) {
                this.collaboratorAdditionList.remove(userObject);
                Log.d(TAG, "Removed: " + userObject.getString());
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "Nothing removed.");
        }
    }

    @Subscribe
    public void onChangeInTaskEvent(ChangeInTaskEvent changeInTaskEvent) {
        if(this.task.getId() == changeInTaskEvent.getTaskId()) {
            // User is viewing the task
            // Make changes
            this.task = taskDbHelper.getTaskByRowId(changeInTaskEvent.getTaskId());
            setUpTextViews();
            setUpCollabsList();
            this.collabListAdapter.notifyDataSetChanged();
            // Set the resultIntent with a flag to update the task list.
            this.resultIntent.putExtra(
                    Config.SHARED_PREF_KEYS.UPDATE_LIST.getKey(),
                    true
            );
        }
    }

    @Subscribe
    public void onUserRemovedFromCollaboratorsEvent(UserRemovedFromCollaboratorsEvent userRemovedFromCollaboratorsEvent) {
        if(this.task.getUuid().equals(userRemovedFromCollaboratorsEvent.getUuid())) {
            Toast.makeText(
                    this,
                    "You have been removed from list of collaborators of the task.",
                    Toast.LENGTH_SHORT
            ).show();
            // Set the resultIntent with a flag to update the task list.
            this.resultIntent.putExtra(
                    Config.SHARED_PREF_KEYS.UPDATE_LIST.getKey(),
                    true
            );
            this.finish();
        }
    }

    @Subscribe
    public void onTaskDeletedEvent(TaskDeletedEvent taskDeletedEvent) {
        if(this.task.getUuid().equals(taskDeletedEvent.getUuid())) {
            Toast.makeText(
                    this,
                    "The task was deleted by the owner.",
                    Toast.LENGTH_SHORT
            ).show();
            // Set the resultIntent with a flag to update the task list.
            this.resultIntent.putExtra(
                    Config.SHARED_PREF_KEYS.UPDATE_LIST.getKey(),
                    true
            );
            this.finish();
        }
    }
}
