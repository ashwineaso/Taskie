package in.altersense.taskapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.adapters.TasksAdapter;
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.components.GCMHandler;
import in.altersense.taskapp.customviews.TokenCompleteCollaboratorsEditText;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.database.UserDbHelper;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.models.User;
import in.altersense.taskapp.requests.CreateTaskRequest;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class DashboardActivity extends ActionBarActivity implements TokenCompleteTextView.TokenListener {

    private static final String CLASS_TAG = "DashboardActivity ";
    private ListView taskList;  // For handling the main content area.
    private LinearLayout quickCreateStageLinearLayout; // Quick task creation area
    private TaskDbHelper taskDbHelper;
    private boolean isQuickTaskCreationHidden;
    private View taskCreationView;
    private EditText newTaskTitle;
    private LinearLayout groupListStageLL;
    private TokenCompleteCollaboratorsEditText participantNameTCET;
    private FilteredArrayAdapter adapter;

    private TasksAdapter taskAdapter;

    private GCMHandler gcmHandler;

    private BroadcastReceiver syncCompletionReceiver;

//    Authenticated user details.
    private String ownerId;
    private String ownerName;
    private List<User> collaboratorAdditionList;
    private List<User> collaboratorRemovalList;
    private List<User> userList;

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

        setContentView(R.layout.activity_tasks);

        // Initializing the dbhelper.
        this.taskDbHelper = new TaskDbHelper(this.getApplicationContext());

        // Initializing the layout.
        Log.d(CLASS_TAG,"Initializing layout.");
        this.quickCreateStageLinearLayout = (LinearLayout) findViewById(R.id.quickTaskCreation);
        this.quickCreateStageLinearLayout.setVisibility(View.GONE);

        this.taskList = (ListView) findViewById(R.id.taskListStage);
        this.taskAdapter = new TasksAdapter(DashboardActivity.this, taskDbHelper.getAllNonGroupTasks(DashboardActivity.this));
        this.taskList.setAdapter(this.taskAdapter);

        setUpQuickTaskLayout();

        this.isQuickTaskCreationHidden = true;
        Log.d(CLASS_TAG,"Done.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.gcmHandler.checkPlayServices();
        UserDbHelper userDbHelper = new UserDbHelper(this);
        // Setting a Filtered Array Adapter to autocomplete with users in db
        userList = userDbHelper.listAllUsers();
        Log.d(CLASS_TAG, "User list: "+userList.toString());

        adapter = new FilteredArrayAdapter<User>(this, R.layout.collaorator_list_layout, userList) {
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
        participantNameTCET.setAdapter(adapter);

        this.groupListStageLL = (LinearLayout) findViewById(R.id.groupListStage);
        Log.d(CLASS_TAG,"Settng adapter to listview.");

        // Initialize a receiver
        syncCompletionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(CLASS_TAG, "Received broadcast.");
                taskAdapter = new TasksAdapter(DashboardActivity.this, taskDbHelper.getAllNonGroupTasks(DashboardActivity.this));
                taskList.setAdapter(taskAdapter);
            }
        };
        registerReceiver(syncCompletionReceiver, new IntentFilter(Config.SHARED_PREF_KEYS.SYNC_IN_PROGRESS.getKey()));

        // Inflate all the nonGroupTasks in the TasksListStage.
        Log.d(CLASS_TAG, "Done.");

        this.taskList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(CLASS_TAG, "onItemLongClick triggered");
                final TasksAdapter adapter = (TasksAdapter) parent.getAdapter();
                final Task task = adapter.getItem(position);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DashboardActivity.this);
                dialogBuilder.setMessage(Config.MESSAGES.CONFIRM_TASK_DELETE.getMessage());
                dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        task.delete(getApplicationContext());
                        adapter.remove(task);
                        adapter.notifyDataSetChanged();
                    }
                });
                dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialogBuilder.create();
                dialogBuilder.show();
                return false;
            }
        });

        this.taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TasksAdapter adapter = (TasksAdapter)parent.getAdapter();
                if(adapter.getSelected()==position) {
                    adapter.setSelected(-1);
                } else {
                    adapter.setSelected(position);
                }
                adapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * Checks for an authenticated user in the device.
     * If it fails login activity is displayed.
     */
    private boolean authenticateUser() {
        String TAG = CLASS_TAG+"authenticateUser";
        Log.d(TAG, "Authenticating user.");
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
        }

        Log.d(TAG, "Creatig new GCMHandler.");
        gcmHandler = new GCMHandler(
                Config.getGCMSenderId(),
                AltEngine.SHARED_PREFERENCE,
                Config.SHARED_PREF_KEYS.GCM_REG_ID.getKey(),
                this
        );

        Log.d(CLASS_TAG, "Auth completed device owner identified");
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
                toggleQuickTaskLayout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toggleQuickTaskLayout() {

//        Check whether task is displayed
        if(this.isQuickTaskCreationHidden) {
//            Display quick task pane
            this.quickCreateStageLinearLayout.setVisibility(View.VISIBLE);
//            Request focus to edit text
            newTaskTitle.requestFocus();

            // Initializing lists.
            this.collaboratorRemovalList = new ArrayList<>();
            this.collaboratorAdditionList = new ArrayList<>();

            // Code to remove all the objects in the participants
            for(Object object:participantNameTCET.getObjects()) {
                participantNameTCET.removeObject(object);
            }

//            Set flag to show the layout is open
            this.isQuickTaskCreationHidden = false;
        } else {
            this.quickCreateStageLinearLayout.setVisibility(View.GONE);
            this.newTaskTitle.clearFocus();
            this.taskList.requestFocus();
            this.taskList.requestFocusFromTouch();
            hideKeyBoard(
                    getApplicationContext(),
                    getCurrentFocus()
            );
            this.isQuickTaskCreationHidden = true;
        }
    }

    private void setUpQuickTaskLayout() {
//        Setting up layout inflater
        LayoutInflater inflater = getLayoutInflater();
//        Inflate task creation view
        this.taskCreationView = inflater.inflate(
                R.layout.quick_task_creation,
                null
        );

//        Add view to placeholder
        this.quickCreateStageLinearLayout.addView(taskCreationView);
//        Identify edit text
        this.newTaskTitle = (EditText) taskCreationView.findViewById(R.id.newTaskTitle);
        this.newTaskTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                Set up input manager
                InputMethodManager keyboardManager = (InputMethodManager) getApplicationContext()
                        .getSystemService(
                                Context.INPUT_METHOD_SERVICE
                        );
                if (hasFocus) {
                    Log.i(CLASS_TAG, "hasFocus");
//                    Display keyboard
                    keyboardManager.showSoftInput(
                            v,
                            InputMethodManager.SHOW_IMPLICIT
                    );
                }
            }
        });
        participantNameTCET =
                (TokenCompleteCollaboratorsEditText) taskCreationView.findViewById(R.id.quickTaskParticipantName);
        participantNameTCET.setTokenListener(this);
        final Button createQuickTask = (Button) taskCreationView.findViewById(R.id.createQuickTaskButton);
        createQuickTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TAG = CLASS_TAG+" createQuickTask OnClickListener.";
                String taskName = newTaskTitle.getText().toString();
                taskName = taskName.trim();
                if(!(taskName.length() <1)) {
                    Task quickTask = new Task(
                            taskName,
                            "",
                            // TODO: Create user once and call pass that user instead of creating her everytime.
                            new User(
                                    AltEngine.readStringFromSharedPref(
                                            getApplicationContext(),
                                            Config.SHARED_PREF_KEYS.OWNER_ID.getKey(),
                                            ""
                                    ),
                                    DashboardActivity.this
                            ),
                            DashboardActivity.this
                    );
                    quickTask.setStatus(
                            Config.TASK_STATUS.INCOMPLETE.getStatus()
                    );
                    Task createdQuickTask = addQuickTaskToDb(quickTask);
                    createdQuickTask.updateCollaborators(
                            collaboratorAdditionList,
                            collaboratorRemovalList,
                            DashboardActivity.this,
                            false
                    );
                    CreateTaskRequest createTaskRequest = new CreateTaskRequest(
                            createdQuickTask,
                            DashboardActivity.this
                    );
                    createTaskRequest.execute();

                    // Add task to the adapter.
                    taskAdapter.add(createdQuickTask);
                    taskAdapter.notifyDataSetChanged();

                    newTaskTitle.setText("");
                    toggleQuickTaskLayout();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            Config.MESSAGES.TASK_TITLE_TOO_SHORT.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(!this.isQuickTaskCreationHidden) {
            toggleQuickTaskLayout();
        } else {
            super.onBackPressed();
        }
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
        Log.d(TAG, "adding QuickTask: "+quickTask.toString()+" to db,");
        TaskDbHelper taskDbHelper = new TaskDbHelper(this);
        // Add task to database.
        Task createdTask = taskDbHelper.createTask(
                quickTask,
                this
        );
        Log.d(TAG, "Task added to database");

        Log.d(TAG, "createdTask: "+createdTask.toString());
        return createdTask;
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
                Log.d(TAG, "New List: "+listOfCollabs);
            } else {
                Log.d(TAG, "Invalid email.");
                Log.d(TAG, "Nothing removed.");
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "Nothing removed.");
        }
    }
}