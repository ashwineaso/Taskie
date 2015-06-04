package in.altersense.taskapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
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

import in.altersense.taskapp.adapters.TasksAdapter;
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.components.BaseApplication;
import in.altersense.taskapp.components.GCMHandler;
import in.altersense.taskapp.customviews.TokenCompleteCollaboratorsEditText;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.database.UserDbHelper;
import in.altersense.taskapp.events.ChangeInTasksEvent;
import in.altersense.taskapp.events.UpdateNowEvent;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.models.User;
import in.altersense.taskapp.requests.AppVersionCheckRequest;
import in.altersense.taskapp.requests.CreateTaskRequest;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class DashboardActivity extends AppCompatActivity implements TokenCompleteTextView.TokenListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private static final String CLASS_TAG = "DashboardActivity ";

    private static final String DATEPICKER_TAG = "datePicker";
    private static final String TIMEPICKER_TAG = "timePicker";

    public static final String TASK_UPDATED = "taskUpdated";
    public static final int TASK_VIEW_REQUEST_CODE = 0;

    private ListView taskList;  // For handling the main content area.
    private LinearLayout quickCreateStageLinearLayout; // Quick task creation area
    private TaskDbHelper taskDbHelper;
    private boolean isQuickTaskCreationHidden;
    private EditText newTaskTitle;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private TokenCompleteCollaboratorsEditText participantNameTCET, newTCET;
    private FilteredArrayAdapter adapter;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

    private MaterialDialog materialDialog;
    private View taskCreationView;

    private TasksAdapter taskAdapter;

    private GCMHandler gcmHandler;

    private User deviceUser;

//    Authenticated user details.
    private String ownerId;
    private String ownerName;

    // For quick task creation.
    private List<User> collaboratorAdditionList;
    private List<User> collaboratorRemovalList;
    private List<User> userList;
    private Spinner prioritySpinner;
    private LinearLayout dueDateChangerLinearLayout;
    private TextView dueDateTextView;
    private ImageView cancelDateButton;
    private EditText descriptionEditText;
    private boolean isExpandedDialog;
    private String dueString;
    private long duelong;
    private ImageView calendarIV;
    private UserDbHelper userDbHelper;

    private boolean updateTaskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Setting up calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Cabin-Medium-TTF.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        // Authenticate user.
        if(!authenticateUser()) {
            this.finish();
            return;
        }

        //Messing with the actionbar present shadow in API 21
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);

        setContentView(R.layout.activity_tasks);

        // Register eventBus
        BaseApplication.getEventBus().register(this);

        // Initializing the dbhelper.
        this.taskDbHelper = new TaskDbHelper(this.getApplicationContext());
        this.userDbHelper = new UserDbHelper(this.getApplicationContext());

        // Initializing the layout.
        Log.d(CLASS_TAG,"Initializing layout.");
        this.quickCreateStageLinearLayout = (LinearLayout) findViewById(R.id.quickTaskCreation);
        this.quickCreateStageLinearLayout.setVisibility(View.GONE);

        this.taskList = (ListView) findViewById(R.id.taskListView);
        this.taskAdapter = new TasksAdapter(DashboardActivity.this, taskDbHelper.getAllNonGroupTasksAsCursor());
        this.taskList.setAdapter(this.taskAdapter);

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

        // Setup the create task dialog.
        createNewDialog();
        
        //Set onItemClickListener for the task list
        this.taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor taskCursor = taskAdapter.getCursor();
                taskCursor.moveToPosition(position);
                Task selectedTask = new Task(
                        taskCursor,
                        getApplicationContext()
                );
                Intent intent = new Intent(DashboardActivity.this, TaskFragmentsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Config.REQUEST_RESPONSE_KEYS.UUID.getKey(), selectedTask.getId());
                startActivityForResult(intent, TASK_VIEW_REQUEST_CODE);
            }
        });

        this.isQuickTaskCreationHidden = true;
        Log.d(CLASS_TAG,"Done.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.gcmHandler.checkPlayServices();
    }

    /**
     * Checks for an authenticated user in the device.
     * If it fails login activity is displayed.
     */
    private boolean authenticateUser() {
        String TAG = CLASS_TAG+"authenticateUser";
        Log.d(TAG, "Authenticating user.");
        // Check for if an update is necessary
        AppVersionCheckRequest appVersionCheckRequest = new AppVersionCheckRequest(this.getApplicationContext());
        appVersionCheckRequest.execute();
        boolean displayTutorials = AltEngine.readBooleanFromSharedPref(
                this,
                Config.SHARED_PREF_KEYS.DISPLAY_TUTORIALS.getKey(),
                true
        );

        this.ownerId = AltEngine.readStringFromSharedPref(
                getApplicationContext(),
                Config.SHARED_PREF_KEYS.OWNER_ID.getKey(),
                ""
        );
        this.ownerName = AltEngine.readStringFromSharedPref(
                getApplicationContext(),
                Config.SHARED_PREF_KEYS.OWNER_NAME.getKey(),
                ""
        );

        if(ownerId.equals("")) {
            Intent authenticateUserIntent = new Intent(
                    getApplicationContext(),
                    UserLoginActivity.class
            );
            startActivity(authenticateUserIntent);
            finish();
            return false;
        } else {
            // Setup device user.
            this.deviceUser = new User(
                    AltEngine.readStringFromSharedPref(
                            getApplicationContext(),
                            Config.SHARED_PREF_KEYS.OWNER_ID.getKey(),
                            ""
                    ),
                    DashboardActivity.this
            );
        }

        if(displayTutorials) {
            Intent displayTutorialIntent = new Intent(this,TutorialActivity.class);
            startActivity(displayTutorialIntent);
            this.finish();
            return false;
        }

        gcmHandler = new GCMHandler(
                Config.getGCMSenderId(),
                AltEngine.SHARED_PREFERENCE,
                Config.SHARED_PREF_KEYS.GCM_REG_ID.getKey(),
                this
        );

        return true;
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
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Catches every click on Menu
        switch (id) {
            case R.id.quickTaskCreate:
                this.materialDialog.show();
                break;

            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void createNewDialog() {

        // Create a master dialog
        materialDialog = new MaterialDialog.Builder(this)
                .title("CREATE A TASK")
                .customView(R.layout.create_task_dialog, true)
                .positiveText("DONE")
                .negativeText("CANCEL")
                .neutralText("MORE")
                .autoDismiss(false)
                .callback(
                        new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);
                                dialog.dismiss();
                                createNewDialog();
                            }
                        }
                )
                .widgetColorRes(R.color.taskPrimaryColor)
                .build();

        this.collaboratorAdditionList = new ArrayList<>();
        this.collaboratorRemovalList = new ArrayList<>();

        this.taskCreationView = materialDialog.getCustomView();

        this.newTaskTitle = (EditText) taskCreationView.findViewById(R.id.quickTaskTitle);
        this.participantNameTCET = (TokenCompleteCollaboratorsEditText) taskCreationView.findViewById(R.id.taskParticipantName);
        this.prioritySpinner = (Spinner) taskCreationView.findViewById(R.id.taskPrioritySpinner);
        this.dueDateChangerLinearLayout = (LinearLayout) taskCreationView.findViewById(R.id.dueDateChangerLinearLayout);
        this.calendarIV = (ImageView) taskCreationView.findViewById(R.id.calendarIconImageView);
        this.dueDateTextView = (TextView) taskCreationView.findViewById(R.id.dueDateTextView);
        this.cancelDateButton = (ImageView) taskCreationView.findViewById(R.id.btnCancelDate);
        this.descriptionEditText = (EditText) taskCreationView.findViewById(R.id.taskDescriptionEditText);

        this.participantNameTCET.setTokenListener(this);
        this.participantNameTCET.allowDuplicates(false);
        char[] splitChars = {',', ' ', ';'};
        this.participantNameTCET.setSplitChar(splitChars);

        // Setting a Filtered Array Adapter to autocomplete with users in db
        userList = this.userDbHelper.listAllUsers();
        this.adapter = new FilteredArrayAdapter<User>(this, R.layout.collaorator_list_layout, userList) {
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
        this.participantNameTCET.setAdapter(adapter);

        final Calendar calendar = Calendar.getInstance();
        this.dueDateChangerLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentYear = calendar.get(Calendar.YEAR);
                datePickerDialog.setYearRange(currentYear, currentYear + 50 < 2037 ? currentYear + 50 : 2037);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });

        // By default dialog is not expanded
        this.isExpandedDialog = false;

        // The neutral button that helps users add more info to the task.
        final MDButton neutralButton = (MDButton) materialDialog.getActionButton(DialogAction.NEUTRAL);

        // The positive Button that creates tas and adds it to the database
        final MDButton positiveButton = (MDButton) materialDialog.getActionButton(DialogAction.POSITIVE);

        final View dialogView = materialDialog.getCustomView();

        // Set click listener for positive button.
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                participantNameTCET.clearFocus();
                LinearLayout moreView = (LinearLayout) dialogView.findViewById(R.id.moreLinearLayout);
                if(!isExpandedDialog) {
                    moreView.setVisibility(View.VISIBLE);
                    neutralButton.setText("LESS");
                    isExpandedDialog = true;
                } else {
                    moreView.setVisibility(View.GONE);
                    neutralButton.setText("MORE");
                    isExpandedDialog = false;
                }
            }
        });

        // Set click listener for positive button
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                participantNameTCET.clearFocus();
                String taskName = newTaskTitle.getText().toString();
                taskName = taskName.trim();
                if (!(taskName.length() < 1)) {
                    Task quickTask;
                    if(isExpandedDialog) {
                        quickTask = new Task(
                                "",
                                taskName,
                                descriptionEditText.getText().toString(),
                                deviceUser,
                                prioritySpinner.getSelectedItemPosition(),
                                duelong,
                                Config.TASK_STATUS.INCOMPLETE.getStatus(),
                                getApplicationContext()
                        );
                    } else {
                        quickTask = new Task(
                                taskName,
                                "",
                                deviceUser,
                                DashboardActivity.this
                        );
                    }

                    quickTask = addQuickTaskToDb(quickTask);
                    quickTask.updateCollaborators(
                            collaboratorAdditionList,
                            collaboratorRemovalList,
                            DashboardActivity.this,
                            false
                    );
                    CreateTaskRequest createTaskRequest = new CreateTaskRequest(
                            quickTask,
                            DashboardActivity.this
                    );
                    createTaskRequest.execute();

                    // Add task to the adapter.
                    taskAdapter.changeCursor(taskDbHelper.getAllNonGroupTasksAsCursor());
                    taskAdapter.notifyDataSetChanged();

                    materialDialog.dismiss();
                    createNewDialog();

                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            Config.MESSAGES.TASK_TITLE_TOO_SHORT.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

        this.cancelDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duelong = 0;
                dueDateTextView.setText(Task.dateToString(duelong));
                cancelDateButton.setVisibility(View.GONE);
                calendarIV.setVisibility(View.VISIBLE);
            }
        });
    }

    private  void hideKeyBoard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Creates a quick task and adds it to the main list of tasks.
     * @param quickTask A task object which is created.
     */
    private Task addQuickTaskToDb(Task quickTask) {
        String TAG = CLASS_TAG+" addQuickTaskToDb";
        Log.d(TAG, "adding QuickTask: " + quickTask.toString() + " to db,");
        TaskDbHelper taskDbHelper = new TaskDbHelper(this);
        // Add task to database.
        Task createdTask = taskDbHelper.createTask(
                quickTask
        );
        Log.d(TAG, "Task added to database");

        Log.d(TAG, "createdTask: "+createdTask.toString());
        return createdTask;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(CLASS_TAG, "onActivityResult");
        Log.i(CLASS_TAG, "reqCode: "+requestCode+" resCode: "+resultCode);
        if(requestCode==TASK_VIEW_REQUEST_CODE) {
            if(data!=null) {
                Log.d(CLASS_TAG, "onActivityResult: RESULT_OK");
                if(data.getExtras().getBoolean(TASK_UPDATED, false)) {
                    this.updateTaskList = true;
                    if(this.updateTaskList) {
                        Log.d(CLASS_TAG, "Updating list.");
                        updateList();
                        Log.d(CLASS_TAG, "Update complete.");
                        this.updateTaskList=false;
                    }
                }
            }
        }
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
                participantNameTCET.removeObject(o);
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
            if(AltEngine.verifyEmail(userObject.getEmail())) {
                Log.d(TAG, "Valid email.");
                this.collaboratorRemovalList.add(userObject);
                Log.d(TAG, "Removed: "+userObject.toString());
                String listOfCollabs = "";
                for(User user:this.collaboratorRemovalList) {
                    listOfCollabs+=user.getEmail()+",";
                }
                Log.d(TAG, "New List: " + listOfCollabs);
            } else {
                Log.d(TAG, "Invalid email.");
                Log.d(TAG, "Nothing removed.");
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "Nothing removed.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void updateList() {
        taskAdapter = new TasksAdapter(DashboardActivity.this, taskDbHelper.getAllNonGroupTasksAsCursor());
        taskList.setAdapter(taskAdapter);
    }

    @Subscribe
    public void onChangeInTasksEvent(ChangeInTasksEvent changeInTasksEvent) {
        updateList();
    }

    @Subscribe
    public void onUpdateNowEvent(UpdateNowEvent event) {
        Intent showUpdateNowActivityIntent = new Intent(this, UpdateNowActivity.class);
        startActivity(showUpdateNowActivityIntent);
        this.finish();
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
            dueString = Task.dateToString(duelong);
            cancelDateButton.setVisibility(View.VISIBLE);
            calendarIV.setVisibility(View.GONE);
            this.dueDateTextView.setText(this.dueString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}