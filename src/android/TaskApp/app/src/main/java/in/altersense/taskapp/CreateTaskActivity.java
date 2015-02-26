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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.customviews.TokenCompleteCollaboratorsEditText;
import in.altersense.taskapp.database.CollaboratorDbHelper;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.database.UserDbHelper;
import in.altersense.taskapp.models.Collaborator;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.models.User;
import in.altersense.taskapp.requests.UpdateTaskRequest;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class CreateTaskActivity extends ActionBarActivity implements TokenCompleteTextView.TokenListener{

    private static final String CLASS_TAG = "CreateTaskActivity ";
    private Task task = new Task();

    private EditText taskTitleET;
    private EditText taskDescriptionET;
    private EditText dueDateET;
    private TokenCompleteCollaboratorsEditText collaboratorsTCET;
    private SeekBar prioritySB;
    private TextView priorityTV;
    private Button createTaskBtn;
    private Button updateTaskBtn;

    private List<User> users;

    private ArrayAdapter<User> adapter;
    private List<Collaborator> collaboratorList = new ArrayList<Collaborator>();
    private List<User> collaboratorAdditionList = new ArrayList<>();
    private List<User> collaboratorRemovalList = new ArrayList<>();

    private boolean isCreate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String TAG = CLASS_TAG+"onCreate";
        // Get intent.
        Intent createEditIntent = getIntent();
        //        Setting up calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Cabin-Medium-TTF.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        long taskId;
        // Check whether there is an extra with the intent
        if(createEditIntent.hasExtra(Config.REQUEST_RESPONSE_KEYS.UUID.getKey())) {
            this.isCreate = false;
            Log.d(TAG, "Intent has taskID");
            taskId = createEditIntent.getExtras().getLong(
                    Task.ID
            );
            Log.d(TAG, "TaskID: "+taskId);
            TaskDbHelper taskDbHelper = new TaskDbHelper(CreateTaskActivity.this);
            // If yes fetch task from the uuid
            Log.d(TAG, "Fetching row from the db");
            this.task = taskDbHelper.getTaskByRowId(taskId, CreateTaskActivity.this);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        // Initialize the views.
        this.taskTitleET = (EditText) findViewById(R.id.taskTitleEditText);
        this.taskDescriptionET = (EditText) findViewById(R.id.taskDescriptionEditText);
        this.collaboratorsTCET = (TokenCompleteCollaboratorsEditText) findViewById(R.id.collaboratorsTCET);
        this.dueDateET = (EditText) findViewById(R.id.dueDateEditText);
        this.prioritySB = (SeekBar) findViewById(R.id.prioritySeekBar);
        this.priorityTV = (TextView) findViewById(R.id.priorityTextView);
        this.createTaskBtn = (Button) findViewById(R.id.createTaskButton);
        this.updateTaskBtn = (Button) findViewById(R.id.updateTaskButton);

        this.updateTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTask();
            }
        });

        this.createTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createaTask();
            }
        });

        this.prioritySB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                priorityTV.setText(Config.PRIORITY.getText(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        UserDbHelper userDbHelper = new UserDbHelper(CreateTaskActivity.this);
        this.users = userDbHelper.listAllUsers();
        User ownerUser = new User(
                AltEngine.readStringFromSharedPref(
                        getApplicationContext(),
                        Config.SHARED_PREF_KEYS.OWNER_ID.getKey(),
                        ""
                ),
                this
        );

        if(this.users.contains(ownerUser)) {
            this.users.remove(ownerUser);
            Log.d(TAG, "Removed owner from list of users.");
        }


        collaboratorsTCET.setTokenListener(this);
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
        collaboratorsTCET.setAdapter(adapter);

        // denying duplicate entries.
        collaboratorsTCET.allowDuplicates(false);

        // setting what happens on clicking token
        collaboratorsTCET.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);

        if(!isCreate) {
            // Populate the form with the data.
            populateTheForm();
        } else {
            this.updateTaskBtn.setVisibility(View.GONE);
        }
    }

    private void createaTask() {
    }

    private void updateTask() {
        String TAG = CLASS_TAG+"updateTask";
        Log.d(TAG, "Task id in activity: "+this.task.getId());
        this.task.updateCollaborators(
                collaboratorAdditionList,
                collaboratorRemovalList,
                this
        );
        this.task.setName(this.taskTitleET.getText().toString());
        this.task.setDescription(this.taskDescriptionET.getText().toString());
        this.task.setPriority(this.prioritySB.getProgress());
        Log.d(TAG, "Values set.");
        this.task.updateTask(this);
        Log.d(TAG, "Task update complete.");
        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest(task, this);
        Log.d(TAG, "Request prepped.");
        updateTaskRequest.execute();
        Log.d(TAG, "Request initiated.");
        this.finish();
    }

    private void populateTheForm() {
        String TAG = CLASS_TAG+"populateTheForm";
        this.taskTitleET.setText(this.task.getName());
        this.taskDescriptionET.setText(this.task.getDescription());
        this.dueDateET.setText(this.task.getDueDateTime()+"");
        prioritySB.setProgress(this.task.getPriority());
        String priority = Config.PRIORITY.getText(this.task.getPriority());
        Log.d(TAG,"Priority: "+priority);
        priorityTV.setText(priority);

        Log.d(CLASS_TAG, "Fetching collaborators.");
        CollaboratorDbHelper collaboratorDbHelper = new CollaboratorDbHelper(this);
        this.task.setCollaborators(collaboratorDbHelper.getAllCollaborators(this.task));

        Log.d(TAG, "Collaborators: "+task.getCollaborators());
        for(Collaborator collaborator:task.getCollaborators()) {
            this.collaboratorsTCET.addObject(collaborator);
            Log.d(TAG, "Added collaborator: "+collaborator.getString());
        }
        Log.d(TAG, "CollaboratorsTCET: "+collaboratorsTCET.getObjects().toString());

        // Hide the CREATE Button and Show the Update button instead.
        this.createTaskBtn.setVisibility(View.GONE);
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
            } else {
                Log.d(TAG, "Invalid email.");
                collaboratorsTCET.removeObject(o);
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
