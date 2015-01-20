package in.altersense.taskapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.components.GroupPanelOnClickListener;
import in.altersense.taskapp.components.Task;
import in.altersense.taskapp.components.TaskGroup;
import in.altersense.taskapp.components.TaskPanelOnClickListener;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class TasksActivity extends ActionBarActivity {

    private static final String TAG = "TasksActivity";
    private LinearLayout mainStageLinearLayout;  // For handling the main content area.
    private List<Task> taskList = new ArrayList<Task>();  // Lists all tasks for traversing convenience.
    private Task task;  // Task iterator.

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

        this.mainStageLinearLayout = (LinearLayout) findViewById(R.id.mainStageLinearLayout);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}