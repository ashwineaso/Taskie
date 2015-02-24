package in.altersense.taskapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.models.Task;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class TaskActivity extends ActionBarActivity {

    private static final String CLASS_TAG = "TaskActivity";
    private Task task;
    private TextView taskTitleTV, dueDateTV, taskDescriptionTV, taskPriorityTV, taskStatusTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        Setting up calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Cabin-Medium-TTF.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.activity_task);

        String TAG = CLASS_TAG+ " OnCreate";
        //Get the intent
        Intent createViewIntent = getIntent();
        long taskId;
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
            this.task = taskDbHelper.getTaskByRowId(taskId, TaskActivity.this);
        }

        //Initialize the views
        this.taskTitleTV = (TextView)findViewById(R.id.taskTitleTextView);
        this.taskDescriptionTV = (TextView)findViewById(R.id.taskDescriptionTextView);
        this.dueDateTV = (TextView)findViewById(R.id.dueDateTextView);
        this.taskPriorityTV = (TextView)findViewById(R.id.taskStatusTextView);
        this.taskStatusTV = (TextView)findViewById(R.id.taskStatusTextView);

        //Set the text views
        this.taskTitleTV.setText(this.task.getName());
        this.taskDescriptionTV.setText(this.task.getDescription());
        this.dueDateTV.setText(this.task.getDueDateTime());
        this.taskPriorityTV.setText(this.task.getPriority()+"");
        this.taskStatusTV.setText(this.task.getStatus()+"");




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
}
