package in.altersense.taskapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.models.Task;


public class CreateTaskActivity extends ActionBarActivity {

    private static final String CLASS_TAG = "CreateTaskActivity";
    private Task task;

    private EditText taskTitleET;
    private EditText taskDescriptionET;
    private EditText dueDateET;
    private SeekBar prioritySB;
    private TextView priorityTV;
    private Button createTaskBtn;
    private Button updateTaskBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String TAG = CLASS_TAG+"onCreate";
        // Get intent.
        Intent createEditIntent = getIntent();
        String taskUUID;
        // Check whether there is an extra with the intent
        if(createEditIntent.hasExtra(Config.REQUEST_RESPONSE_KEYS.UUID.getKey())) {
            Log.d(TAG, "Intent has taskUUID");
            taskUUID = createEditIntent.getExtras().getString(
                    Config.REQUEST_RESPONSE_KEYS.UUID.getKey()
            );
            Log.d(TAG, "TaskUUID: "+taskUUID);
            TaskDbHelper taskDbHelper = new TaskDbHelper(CreateTaskActivity.this);
            // If yes fetch task from the uuid
            Log.d(TAG, "Fetching row from the db");
            this.task = taskDbHelper.getTaskByUUID(taskUUID, CreateTaskActivity.this);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        // Initialize the views.
        this.taskTitleET = (EditText) findViewById(R.id.taskTitleEditText);
        this.taskDescriptionET = (EditText) findViewById(R.id.taskDescriptionEditText);
        this.dueDateET = (EditText) findViewById(R.id.dueDateEditText);
        this.prioritySB = (SeekBar) findViewById(R.id.prioritySeekBar);
        this.priorityTV = (TextView) findViewById(R.id.priorityTextView);
        this.createTaskBtn = (Button) findViewById(R.id.createTaskButton);
        this.updateTaskBtn = (Button) findViewById(R.id.updateTaskButton);
        if(createEditIntent.hasExtra(Config.REQUEST_RESPONSE_KEYS.UUID.getKey())) {
            // Populate the form with the data.
            populatetheForm();
        } else {
            this.updateTaskBtn.setVisibility(View.GONE);
        }
    }

    private void populatetheForm() {
        this.taskTitleET.setText(this.task.getName());
        this.taskDescriptionET.setText(this.task.getDescription());
        this.dueDateET.setText(this.task.getDueDateTime()+"");
        this.prioritySB.setProgress(this.task.getPriority());
        this.priorityTV.setText(this.task.getPriority()+"");
        // Hide the CREATE Button and Show the Update button instead.
        this.createTaskBtn.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_task, menu);
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
