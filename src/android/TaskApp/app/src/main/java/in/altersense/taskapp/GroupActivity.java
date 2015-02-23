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

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.components.TaskPanelOnClickListener;
import in.altersense.taskapp.models.TaskGroup;
import in.altersense.taskapp.models.User;
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
                        .setDefaultFontPath("fonts/Cabin-Medium-TTF.ttf")
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
                    "Boil some eggs.",
                    new User(
                            AltEngine.readStringFromSharedPref(
                                this,
                                Config.SHARED_PREF_KEYS.OWNER_ID.getKey(),
                                ""
                            ),
                            this
                    ),
                    GroupActivity.this
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
