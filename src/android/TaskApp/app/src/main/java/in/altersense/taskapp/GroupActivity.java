package in.altersense.taskapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.components.GroupPanelOnClickListener;
import in.altersense.taskapp.components.Task;
import in.altersense.taskapp.components.TaskGroup;
import in.altersense.taskapp.components.TaskPanelOnClickListener;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class GroupActivity extends ActionBarActivity {

    private static final String TAG = "GroupActivity";
    public static TaskGroup currentTaskGroup;
    private LinearLayout mainStageLinearLayout;  // For handling the main content area.
    private List<Task> taskList = new ArrayList<Task>();  // Lists all tasks for traversing convenience.
    private Task task;  // Task iterator.

    public static final void startGroupActivity(Context context, TaskGroup taskGroup) {
        Intent groupStarterIntent = new Intent(context, GroupActivity.class);
        groupStarterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        currentTaskGroup = taskGroup;
        context.startActivity(groupStarterIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        Setting up calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/roboto_slab_regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        setContentView(R.layout.activity_group);

//        Setting action bar title to signify that user is in a group and to display the name of the
//        group
        ActionBar ab = getSupportActionBar();
        ab.setTitle(currentTaskGroup.getTitle());
        ab.setSubtitle("TaskGroup");


        this.mainStageLinearLayout = (LinearLayout) findViewById(R.id.mainStageLinearLayout);
//        Inflate tasks list collections.
        for(int i=0; i<currentTaskGroup.getTaskCount(); i++) {
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
        getMenuInflater().inflate(R.menu.menu_group, menu);
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
