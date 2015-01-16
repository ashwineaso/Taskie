package in.altersense.taskapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import in.altersense.taskapp.components.Task;
import in.altersense.taskapp.components.TaskGroup;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class TasksActivity extends ActionBarActivity {

    private LinearLayout mainStageLinearLayout;  // For handling the main content area.

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
            Task task = new Task(
                    "Boil Eggs",
                    "Some kinda description goes here, I dont care actually. You can set it to anything.",
                    "Mahesh Mohan",
                    this.getLayoutInflater()
            );
            mainStageLinearLayout.addView(task.getPanelView());
        }

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout taskCollection = (LinearLayout) inflater.inflate(R.layout.tasks_collection, mainStageLinearLayout);

        for(int i=0; i<3; i++) {
            TaskGroup taskGroup = new TaskGroup(
                    "AlterSense",
                    3,
                    false,
                    getLayoutInflater()
            );
            taskCollection.addView(taskGroup.getGroupView());
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
