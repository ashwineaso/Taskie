package in.altersense.taskapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.customviews.TokenCompleteCollaboratorsEditText;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.database.UserDbHelper;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.models.User;


public class CreateTaskActivity extends ActionBarActivity implements TokenCompleteTextView.TokenListener{

    private static final String CLASS_TAG = "CreateTaskActivity";
    private Task task;

    private EditText taskTitleET;
    private EditText taskDescriptionET;
    private EditText dueDateET;
    private TokenCompleteCollaboratorsEditText collabboraatorsTCET;
    private SeekBar prioritySB;
    private TextView priorityTV;
    private Button createTaskBtn;
    private Button updateTaskBtn;

    private User[] users;
    private ArrayAdapter<User> adapter;

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
        this.collabboraatorsTCET = (TokenCompleteCollaboratorsEditText) findViewById(R.id.collaboratorsTCET);
        this.dueDateET = (EditText) findViewById(R.id.dueDateEditText);
        this.prioritySB = (SeekBar) findViewById(R.id.prioritySeekBar);
        this.priorityTV = (TextView) findViewById(R.id.priorityTextView);
        this.createTaskBtn = (Button) findViewById(R.id.createTaskButton);
        this.updateTaskBtn = (Button) findViewById(R.id.updateTaskButton);

        UserDbHelper userDbHelper = new UserDbHelper(CreateTaskActivity.this);
        this.users = userDbHelper.listAllUsers();

        collabboraatorsTCET.setTokenListener(this);
        /*adapter = new ArrayAdapter<User>(
                this,
                android.R.layout.simple_list_item_1,
                users
        );*/

        // Setting a Filtered Array Adapter to autocomplete with users in db
        adapter = new FilteredArrayAdapter<User>(this, R.layout.collaorator_list_layout, users) {
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
        collabboraatorsTCET.setAdapter(adapter);

        // denying duplicate entries.
        collabboraatorsTCET.allowDuplicates(false);

        // setting what happens on clicking token
        collabboraatorsTCET.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);

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

    @Override
    public void onTokenAdded(Object o) {
        String TAG = CLASS_TAG+"onTokenAdded";
        Log.d(TAG, "Added: "+o.toString());
    }

    @Override
    public void onTokenRemoved(Object o) {

    }
}
