package in.altersense.taskapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.components.GroupPanelOnClickListener;
import in.altersense.taskapp.components.TaskPanelOnClickListener;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.models.TaskGroup;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class TasksActivity extends ActionBarActivity {

    private static final String TAG = "TasksActivity";
    private LinearLayout taskListStageLL;  // For handling the main content area.
    private LinearLayout quickCreateStageLinearLayout; // Quick task creation area
    private TaskDbHelper taskDbHelper;
    private List<Task> taskList = new ArrayList<Task>();  // Lists all non group tasks for traversing convenience.
    private Task task;  // Task iterator.
    private boolean isQuickTaskCreationHidden;
    private View taskCreationView;
    private EditText newTaskTitle;
    private ScrollView contentScroll;
    private LinearLayout groupListStageLL;

//    Authenticated user details.
    private String ownerId;
    private String ownerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Setting up calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/roboto_slab_regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        // Authenticate user.
        authenticateUser();

        setContentView(R.layout.activity_tasks);

        // Initializing the fields.
        this.taskDbHelper = new TaskDbHelper(this.getApplicationContext());

//        Initializing the layout.
        this.contentScroll = (ScrollView) findViewById(R.id.contenScroll);
        this.quickCreateStageLinearLayout = (LinearLayout) findViewById(R.id.quickTaskCreation);
        this.quickCreateStageLinearLayout.setVisibility(View.GONE);
        setUpQuickTaskLayout();
        this.taskListStageLL = (LinearLayout) findViewById(R.id.taskListStage);
        this.groupListStageLL = (LinearLayout) findViewById(R.id.groupListStage);
        this.isQuickTaskCreationHidden = true;
        Random random = new Random();

        // Inflate all the nonGroupTasks in the TasksListStage.
        this.taskList = taskDbHelper.getAllNonGroupTasks(this);
        for(Task task:taskList) {
            taskListStageLL.addView(task.getPanelView());
//            Adding an onClickListener to TaskPanel to show and hide task actions.
            TaskPanelOnClickListener taskPanelOnClickListener = new TaskPanelOnClickListener(task, this.taskList);
            task.getPanelView().setOnClickListener(taskPanelOnClickListener);
//            Add each task to task list.
            this.taskList.add(task);
        }

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout taskCollection = (LinearLayout) inflater.inflate(R.layout.task_group_collection, groupListStageLL);

        TaskGroup taskGroup = new TaskGroup(
                "CREMID",
                4,
                true,
                getLayoutInflater()
        );
        taskCollection.addView(taskGroup.getGroupView());
        taskGroup.getGroupView().setOnClickListener(new GroupPanelOnClickListener(taskGroup, this));

        for(int i=0; i<2; i++) {
            taskGroup = new TaskGroup(
                    "AlterSense",
                    3,
                    false,
                    getLayoutInflater()
            );
            taskCollection.addView(taskGroup.getGroupView());
            taskGroup.getGroupView().setOnClickListener(new GroupPanelOnClickListener(taskGroup, this));
        }

    }

    /**
     * Checks for an authenticated user in the device.
     * If it fails login activity is displayed.
     */
    private void authenticateUser() {
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
        }
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

//            Set flag to show the layout is open
            this.isQuickTaskCreationHidden = false;
        } else {
            this.quickCreateStageLinearLayout.setVisibility(View.GONE);
            this.newTaskTitle.clearFocus();
            this.taskListStageLL.requestFocus();
            this.taskListStageLL.requestFocusFromTouch();
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
                if(hasFocus) {
                    Log.i(TAG,"hasFocus");
//                    Display keyboard
                    keyboardManager.showSoftInput(
                            v,
                            InputMethodManager.SHOW_IMPLICIT
                    );
                }
            }
        });
        final CheckBox isGroupTaskCB = (CheckBox) taskCreationView.findViewById(R.id.isGroupTaskCheckBox);
        final EditText participantNameET = (EditText) taskCreationView.findViewById(R.id.quickTaskParticipantName);
        final EditText groupNameET = (EditText) taskCreationView.findViewById(R.id.quickTaskGroupName);
        isGroupTaskCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    // Hide participant edit text
                    participantNameET.setVisibility(View.GONE);
                    // Show Group Name edit text
                    groupNameET.setVisibility(View.VISIBLE);
                    // Request Focus to Group Name edit text
                    groupNameET.requestFocus();
                } else {
                    // Hide Group Name edit text
                    groupNameET.setVisibility(View.GONE);
                    // Show participant edit text
                    participantNameET.setVisibility(View.VISIBLE);
                    // Request focus to participant edit text
                    participantNameET.requestFocus();
                }
            }
        });
        final Button createQuickTask = (Button) taskCreationView.findViewById(R.id.createQuickTaskButton);
        createQuickTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task quickTask = new Task(
                        newTaskTitle.getText().toString(),
                        "",
                        "Mahesh Mohan",
                        0,
                        getLayoutInflater()
                );
                quickTask = createQuickTask(quickTask);
                newTaskTitle.setText("");
                isGroupTaskCB.setChecked(false);
                groupNameET.setText("");
                participantNameET.setText("");
                toggleQuickTaskLayout();
                contentScroll.smoothScrollTo(
                        0,
                        taskListStageLL.getBottom()
                );

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
    private Task createQuickTask(Task quickTask) {
        // Add task to taskList
        this.taskList.add(quickTask);
        quickTask = this.taskList.get(this.taskList.size()-1);
        // Add task to top of the linear layout
        this.taskListStageLL.addView(quickTask.getPanelView());
        // Request focus to the new task.
        quickTask.getPanelView().requestFocus();
        quickTask.getPanelView().requestFocusFromTouch();
        return quickTask;
    }
}