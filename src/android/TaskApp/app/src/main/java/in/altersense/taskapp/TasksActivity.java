package in.altersense.taskapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import in.altersense.taskapp.components.GroupPanelOnClickListener;
import in.altersense.taskapp.components.Task;
import in.altersense.taskapp.components.TaskGroup;
import in.altersense.taskapp.components.TaskPanelOnClickListener;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class TasksActivity extends ActionBarActivity {

    private static final String TAG = "TasksActivity";
    private LinearLayout mainStageLinearLayout;  // For handling the main content area.
    private LinearLayout quickCreateStageLinearLayout; // Quick task creation area
    private List<Task> taskList = new ArrayList<Task>();  // Lists all tasks for traversing convenience.
    private Task task;  // Task iterator.
    private boolean isQuickTaskCreationHidden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Setting up calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/roboto_slab_regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        setContentView(R.layout.activity_tasks);

//        Initializing the layout.
        this.quickCreateStageLinearLayout = (LinearLayout) findViewById(R.id.quickTaskCreation);
        this.mainStageLinearLayout = (LinearLayout) findViewById(R.id.mainStageLinearLayout);
        this.isQuickTaskCreationHidden = true;

//        Inflate tasks list collections.
        for(int i=0; i<12; i++) {
            task = new Task(
                    "Boil Eggs",
                    "Some kinda description goes here, I dont care actually. You can set it to anything.",
                    "Mahesh Mohan",
                    this.getLayoutInflater()
            );
            mainStageLinearLayout.addView(task.getPanelView());
//            Adding an onClickListener to TaskPanel to show and hide task actions.
            TaskPanelOnClickListener taskPanelOnClickListener = new TaskPanelOnClickListener(task, this.taskList);
            task.getPanelView().setOnClickListener(taskPanelOnClickListener);
//            Add each task to task list.
            this.taskList.add(task);
        }

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout taskCollection = (LinearLayout) inflater.inflate(R.layout.tasks_collection, mainStageLinearLayout);

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
                displayQuickTaskLayout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayQuickTaskLayout() {
//        Setting up layout inflater
        LayoutInflater inflater = getLayoutInflater();
//        Inflatet task creation view
        View taskCreationView = inflater.inflate(
                R.layout.quick_task_creation,
                null
        );
//        Add view to placeholder
        this.quickCreateStageLinearLayout.addView(taskCreationView);
//        Identify edit text
        final EditText newTaskTitle = (EditText) taskCreationView.findViewById(R.id.newTaskTitle);
//        Request focus to edit text
        newTaskTitle.requestFocus();
//        Display keyboard
        InputMethodManager keyboardManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE
        );
        keyboardManager.showSoftInput(newTaskTitle, InputMethodManager.SHOW_IMPLICIT);
//        Set flag to show the layout is open
        this.isQuickTaskCreationHidden = false;
//        Set an on focus change listener
        taskCreationView.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i(TAG, "FocusChange");
                if(!hasFocus) {
//                    check whether edit text is empty
                    if(newTaskTitle.getText().length()==0) {
//                        if empty hide the create task layout
                        quickCreateStageLinearLayout.removeAllViews();
//                        set flag to denote the view is hidden
                        isQuickTaskCreationHidden = true;
                    }
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "OnTouchEventTriggered");
        float touchPointX = event.getX();
        float touchPointY = event.getY();
        int[] coordinates = new int[2];
        quickCreateStageLinearLayout.getLocationOnScreen(coordinates);
        if(
                touchPointX < coordinates[0] || touchPointX > coordinates[0] + quickCreateStageLinearLayout.getWidth() ||
                        touchPointY < coordinates[1] || touchPointY > coordinates[1] + quickCreateStageLinearLayout.getHeight()
                ) {
            quickCreateStageLinearLayout.removeAllViews();
        }
        return true;
    }
}