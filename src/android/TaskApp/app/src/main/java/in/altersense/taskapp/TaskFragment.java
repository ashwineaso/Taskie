package in.altersense.taskapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.squareup.otto.Subscribe;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.altersense.taskapp.adapters.TaskDetailsViewAdapter;
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.components.BaseApplication;
import in.altersense.taskapp.components.ReminderNotifier;
import in.altersense.taskapp.customviews.TokenCompleteCollaboratorsEditText;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.database.UserDbHelper;
import in.altersense.taskapp.events.ChangeInTaskEvent;
import in.altersense.taskapp.events.TaskDeletedEvent;
import in.altersense.taskapp.events.UserRemovedFromCollaboratorsEvent;
import in.altersense.taskapp.models.Buzz;
import in.altersense.taskapp.models.Collaborator;
import in.altersense.taskapp.models.RemindSyncNotification;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.models.User;
import in.altersense.taskapp.requests.BuzzCollaboratorRequest;
import in.altersense.taskapp.requests.UpdateTaskRequest;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by ashwineaso on 4/22/15.
 */
public class TaskFragment extends Fragment implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, TokenCompleteTextView.TokenListener {

    private static final String CLASS_TAG = "TaskActivity";

    private static final String DATEPICKER_TAG = "datePicker";
    private static final String TIMEPICKER_TAG = "timePicker";
    private static final int EDIT_MENU = 0;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

    private Task task;
    List<Collaborator> collaboratorList;

    //Adapter implementation
    private ListView collList;
    private TaskDetailsViewAdapter adapter;
    private FilteredArrayAdapter collabListAdapter;

    public ArrayList<Collaborator> collaboratorArrayList = new ArrayList<Collaborator>();
    private EditText taskTitleET;
    private TextView taskTitleTV;
    private EditText taskDescriptionET;
    private TextView taskDescriptionTV;
    private TextView dueDateTV;
    private Spinner taskPrioritySpinner;
    private TextView taskPriorityTV;
    private TextView taskStatusTV;
    private TextView taskOwnerTV;
    private TextView noCollText;
    private CompoundButton checkComplete;
    private List<User> userAdditonList, userRemovalList;
    private ImageView calendarIV, cancelIV;
    private ImageButton addCollabsIV;
    private LinearLayout addCollabsLinearLayout;
    private TokenCompleteCollaboratorsEditText collaboratorsTCET;
    private ImageButton addCollaboratorButton;

    private boolean isEditMode = false;
    private boolean isCollabAdditionMode = false;
    private Intent resultIntent;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private String dueString = "";
    private long duelong = 0;
    private MenuItem editViewToggle;

    private TaskDbHelper taskDbHelper;

    private List<User> userList;
    private List<User> collaboratorAdditionList = new ArrayList<>();
    private Context context;
    private Intent createViewIntent;

    private int prevPriority, newPriority;
    private LinearLayout dueDateChangerLL;
    private MenuItem buzz;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_fragment_view, container, false);
        //Initialize the views
        this.taskTitleET = (EditText) view.findViewById(R.id.taskTitleEditText);
        this.taskTitleTV = (TextView) view.findViewById(R.id.taskTitleTextView);
        this.taskDescriptionET = (EditText) view.findViewById(R.id.taskDescriptionEditText);
        this.taskDescriptionTV = (TextView) view.findViewById(R.id.taskDescriptionTextView);
        this.dueDateTV = (TextView) view.findViewById(R.id.dueDateTextView);
        this.taskPrioritySpinner = (Spinner) view.findViewById(R.id.taskPrioritySpinner);
        this.taskPriorityTV = (TextView) view.findViewById(R.id.taskPriorityTextView);
        this.taskStatusTV = (TextView) view.findViewById(R.id.taskStatusTextView);
        this.taskOwnerTV = (TextView) view.findViewById(R.id.taskOwnerTV);
        this.checkComplete = (CompoundButton) view.findViewById(R.id.checkComplete);
        this.calendarIV = (ImageView) view.findViewById(R.id.calendarIconImageView);
        this.dueDateChangerLL = (LinearLayout) view.findViewById(R.id.dueDateChangerLinearLayout);
        this.cancelIV = (ImageView) view.findViewById(R.id.btnCancelDate);
        this.addCollabsIV = (ImageButton) view.findViewById(R.id.addCollaboratorsImageView);
        this.addCollabsLinearLayout = (LinearLayout) view.findViewById(R.id.addCollaboratorsLinearLayout);
        this.collaboratorsTCET = (TokenCompleteCollaboratorsEditText) view.findViewById(R.id.collaboratorsTokenEditText);
        this.addCollaboratorButton = (ImageButton) view.findViewById(R.id.addCollaboratorButton);
        this.collList = (ListView) view.findViewById(R.id.collListView);

        //Set the text views
        setUpTextViews();

        //Hide the addCollabsIV if the user is not owner
        if (!this.task.isOwnedyDeviceUser(getActivity())) {
            this.addCollabsIV.setVisibility(View.GONE);
        }

        // Setup collaborator list
        setUpCollabsList();

        this.addCollaboratorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collaboratorsTCET.clearFocus();
                task.updateCollaborators(
                        collaboratorAdditionList,
                        new ArrayList<User>(),
                        getActivity()
                );
                adapter.clear();
                adapter.addAll(task.getCollaborators(task, context));
                collList.smoothScrollToPosition(adapter.getCount()-1);
                adapter.notifyDataSetChanged();
                toggleAddCollaborators();
                collaboratorsTCET.clear();
            }
        });

        this.collaboratorsTCET.setTokenListener(this);

        this.addCollabsIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAddCollaborators();
            }
        });

        // Initialize date time picker.
        final Calendar calendar = Calendar.getInstance();

        this.datePickerDialog = DatePickerDialog.newInstance(
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                false
        );
        this.timePickerDialog = TimePickerDialog.newInstance(
                this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false,
                false
        );

        this.dueDateChangerLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditMode) {
                    int currentYear = calendar.get(Calendar.YEAR);
                    datePickerDialog.setYearRange(currentYear, currentYear + 50 < 2037 ? currentYear + 50 : 2037);
                    datePickerDialog.setCloseOnSingleTapDay(false);
                    datePickerDialog.show(getActivity().getSupportFragmentManager(), DATEPICKER_TAG);
                }
            }
        });

        this.cancelIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dueDateTV.setText("");
                duelong = 0;
                dueString = task.dateToString(duelong);
                dueDateTV.setText(dueString);
                cancelIV.setVisibility(View.GONE);
            }
        });

        //Set a listener for the checkbox
        this.checkComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                task.toggleStatus(context);
            }
        });

        //Fill the ArrayList with the required data
        this.collaboratorList = task.getCollaborators(this.task, this.context);
        //Create a custom adapter
        adapter = new TaskDetailsViewAdapter(getActivity(), collaboratorList, this.task);
        collList.setAdapter(adapter);
        //Adjust the height of the ListView to accommodate all the children
        setListViewHeightBasedOnChildren(collList);
        collList.setFocusable(false); //To set the focus to top #glitch

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String TAG = CLASS_TAG + " OnCreate";
        super.onCreate(savedInstanceState);

        //        Setting up calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Cabin-Medium-TTF.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        setHasOptionsMenu(true);

        taskDbHelper = new TaskDbHelper(context);
        BaseApplication.getEventBus().register(this);

        //Get the Intent from the Parent Activity
        createViewIntent = getActivity().getIntent();

        final long taskId;
        //Check whether there is an EXTRA with the intent
        if (createViewIntent.hasExtra(Config.REQUEST_RESPONSE_KEYS.UUID.getKey())) {
            Log.d(TAG, "Intent has taskID");
            taskId = createViewIntent.getExtras().getLong(
                    Task.ID
            );
            Log.d(TAG, "TaskID: "+taskId);

            // If yes fetch task from the uuid
            Log.d(TAG, "Fetching row from the db");
            this.task = taskDbHelper.getTaskByRowId(taskId);
            //Mark the notiifcations as seen
            taskDbHelper.markNotificationSeen(this.task);

            if(createViewIntent.getExtras().getBoolean(RemindSyncNotification.KEYS.HIDE_NOTIF.getName(),false)) {
                RemindSyncNotification rsn = taskDbHelper.retreiveRSN(taskId);
                rsn.setHideNotification(true);
                taskDbHelper.updateRSN(rsn);
                Toast.makeText(
                        this.context,
                        "No more reminders of this task will be displayed.",
                        Toast.LENGTH_SHORT
                ).show();
            }


        }


        // Setting up user list for token edit text
        UserDbHelper userDbHelper = new UserDbHelper(getActivity());
        // Setting a Filtered Array Adapter to autocomplete with users in db
        userList = userDbHelper.listAllUsers();
        Log.d(CLASS_TAG, "User list: "+userList.toString());

        this.task.fetchAllCollaborators(this.context);

        this.resultIntent = new Intent();
        this.getActivity().setResult(Activity.RESULT_OK, resultIntent);
    }

    private void setUpCollabsList() {
        this.collabListAdapter = new FilteredArrayAdapter<User>(this.context, R.layout.collaorator_list_layout, userList) {
            @Override
            protected boolean keepObject(User user, String s) {
                s = s.toLowerCase();
                return user.getName().toLowerCase().startsWith(s) || user.getEmail().toLowerCase().startsWith(s);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView==null) {
                    LayoutInflater l = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.collaorator_list_layout, parent, false);
                }

                User u = getItem(position);
                ((TextView)convertView.findViewById(R.id.name)).setText(u.getName());
                ((TextView)convertView.findViewById(R.id.email)).setText(u.getEmail());

                return convertView;
            }
        };
        this.collaboratorsTCET.setAdapter(collabListAdapter);
    }

    private void setUpTextViews() {
        this.taskTitleET.setText(this.task.getName());
        this.taskTitleTV.setText(this.task.getName());
        this.taskDescriptionET.setText(this.task.getDescription());
        this.taskDescriptionTV.setText(this.task.getDescription());
        this.dueDateTV.setText(this.task.getDueDateTime());
        this.taskPrioritySpinner.setSelection(this.task.getPriority());
        this.taskPriorityTV.setText(priorityToString(this.task.getPriority()));
        this.taskStatusTV.setText(statusToString(this.task.getStatus(this.context)));
        this.taskOwnerTV.setText(this.task.getOwner().getName());
    }

    private void toggleAddCollaborators() {
        if(this.isCollabAdditionMode) {
            this.addCollabsLinearLayout.setVisibility(View.GONE);
            this.isCollabAdditionMode = false;
        } else {
            this.addCollabsLinearLayout.setVisibility(View.VISIBLE);
            this.isCollabAdditionMode = true;
        }
    }

    /**
     * Convert the priority from int format to apropriate string format
     */
    private String priorityToString(int priority) {
        String priorityToString = "";
        switch (priority) {
            case 0 : priorityToString = "Low Priority"; break;
            case 1 : priorityToString = "Normal Priority"; break;
            case 2 : priorityToString = "High Priority"; break;
        }
        return priorityToString;
    }

    /**
     * Convert the status from int format to apropriate string format
     */
    private String statusToString(Integer status) {
        String statusAsString = "Undefined";
        switch (status) {
            case 0 : statusAsString = "Not Accepted"; break;
            case 1 : statusAsString = "Ongoing"; break;
            case 2 : statusAsString = "Completed"; break;
            case -1 : statusAsString = "Declined"; break;
        }
        return statusAsString;
    }


    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /**
     * Sets up the activity to task view mode.
     */
    private void setUpViewMode() {
        String TAG = CLASS_TAG+"setUpViewMode";
        // Update task params
        this.task.setName(this.taskTitleET.getText().toString());
        this.task.setDescription(this.taskDescriptionET.getText().toString());
        this.task.setPriority(this.taskPrioritySpinner.getSelectedItemPosition());
        if(duelong!=0) {
            this.task.setDueDateTime(this.duelong);
        }
        // Set mode.
        this.isEditMode = false;

        // Checks for change in priority and fires the event.
        Log.d("checkPriorityChanged", "Calling");
        checkPriorityChanged();

        // Update TextViews
        this.taskTitleTV.setText(this.task.getName());
        this.taskDescriptionTV.setText(this.task.getDescription());
        this.taskPriorityTV.setText(priorityToString(this.task.getPriority()));
        this.dueDateTV.setText(this.task.getDueDateTime());
        // Hide edit views
        this.taskTitleET.setVisibility(View.GONE);
        this.taskDescriptionET.setVisibility(View.GONE);
        this.taskPrioritySpinner.setVisibility(View.GONE);
        this.calendarIV.setVisibility(View.GONE);
        this.cancelIV.setVisibility(View.GONE);
        // display display views
        this.checkComplete.setVisibility(View.VISIBLE);
        this.taskTitleTV.setVisibility(View.VISIBLE);
        this.taskDescriptionTV.setVisibility(View.VISIBLE);
        this.taskPriorityTV.setVisibility(View.VISIBLE);
        // Update task
        this.task.setSyncStatus(false);
        this.task.updateTask(getActivity());
        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest(
                this.task,
                this.context
        );
        updateTaskRequest.execute();

        // Update resultIntent flag to re-query the list of tasks in dashboard
        this.resultIntent.putExtra(Config.SHARED_PREF_KEYS.UPDATE_LIST.getKey(), true);

    }

    /**
     * Enables an edit mode of the task.
     */
    private void setUpEditMode() {
        // update mode.
        this.isEditMode = true;
        // Hide dsiplay views
        this.checkComplete.setVisibility(View.GONE);
        this.taskTitleTV.setVisibility(View.GONE);
        this.taskDescriptionTV.setVisibility(View.GONE);
        this.taskPriorityTV.setVisibility(View.GONE);
        // display edit views
        this.taskTitleET.setVisibility(View.VISIBLE);
        this.taskDescriptionET.setVisibility(View.VISIBLE);
        this.taskPrioritySpinner.setVisibility(View.VISIBLE);
        this.calendarIV.setVisibility(View.VISIBLE);
        if(this.task.getDueDateTimeAsLong()>0) {
            this.cancelIV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        String TAG = CLASS_TAG + "onDateSet";
        Log.d(TAG, "Date: "+year+"-"+month+"-"+day);
        timePickerDialog.setCloseOnSingleTapMinute(false);
        timePickerDialog.show(getActivity().getSupportFragmentManager(), TIMEPICKER_TAG);
        this.dueString = year + "-" + (month+1) + "-" + day + " ";
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
        String TAG = CLASS_TAG + "onTimeSet";
        Log.d(TAG, "Time: "+ hour +":"+ minute);
        this.dueString += hour > 12 ? (hour-12) : hour;
        this.dueString +=":"+ minute +" ";
        this.dueString += hour > 12 ? "PM" : "AM";
        try {
            this.duelong = sdf.parse(this.dueString).getTime();
            this.dueDateTV.setText(this.task.dateToString(this.duelong));
            this.cancelIV.setVisibility(View.VISIBLE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
                adapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "Invalid email.");
                collaboratorsTCET.removeObject(o);
                Log.d(TAG, "Object removed.");
                Toast.makeText(
                        getActivity(),
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
            if(this.collaboratorAdditionList.contains(userObject)) {
                this.collaboratorAdditionList.remove(userObject);
                Log.d(TAG, "Removed: " + userObject.getString());
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "Nothing removed.");
        }
    }

    @Subscribe
    public void onChangeInTaskEvent(ChangeInTaskEvent changeInTaskEvent) {
        if(this.task.getId() == changeInTaskEvent.getTaskId()) {
            // User is viewing the task
            // Make changes
            this.task = taskDbHelper.getTaskByRowId(changeInTaskEvent.getTaskId());
            setUpTextViews();
            setUpCollabsList();
            this.collabListAdapter.notifyDataSetChanged();
            // Set the resultIntent with a flag to update the task list.
            this.resultIntent.putExtra(
                    Config.SHARED_PREF_KEYS.UPDATE_LIST.getKey(),
                    true
            );
        }
    }

    @Subscribe
    public void onUserRemovedFromCollaboratorsEvent(UserRemovedFromCollaboratorsEvent event) {
        if(this.task.getUuid().equals(event.getUuid())) {
            Toast.makeText(
                    getActivity(),
                    "You have been removed from list of collaborators of the task.",
                    Toast.LENGTH_SHORT
            ).show();
            // Set the resultIntent with a flag to update the task list.
            this.resultIntent.putExtra(
                    Config.SHARED_PREF_KEYS.UPDATE_LIST.getKey(),
                    true
            );
            getActivity().finish();
        }
    }

    @Subscribe
    public void onTaskDeletedEvent(TaskDeletedEvent taskDeletedEvent) {
        if(this.task.getUuid().equals(taskDeletedEvent.getUuid())) {
            Toast.makeText(
                    getActivity(),
                    "The task was deleted by the owner.",
                    Toast.LENGTH_SHORT
            ).show();
            // Set the resultIntent with a flag to update the task list.
            this.resultIntent.putExtra(
                    Config.SHARED_PREF_KEYS.UPDATE_LIST.getKey(),
                    true
            );
            getActivity().finish();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.menu_task, menu);
        this.editViewToggle = menu.findItem(R.id.action_toggle_view_edit);
        this.buzz = menu.findItem(R.id.action_buzz);
        if (!task.isOwnedyDeviceUser(context)) {
            //Not shown if the task is not owned by the device user
            this.editViewToggle.setVisible(false);
            this.buzz.setVisible(false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_toggle_view_edit:
                if(isEditMode) {
                    this.editViewToggle.setIcon(R.drawable.ic_edit_white);
                    this.editViewToggle.setTitle("Edit");
                    this.setUpViewMode();

                } else {
                    this.editViewToggle.setIcon(R.drawable.ic_save_white_36dp);
                    this.editViewToggle.setTitle("Save");
                    this.setUpEditMode();
                }
                break;

            case R.id.action_buzz:
                Buzz buzz = new Buzz(this.task, context);
                BuzzCollaboratorRequest buzzCollaboratorRequest = new BuzzCollaboratorRequest(buzz, context);
                buzzCollaboratorRequest.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkPriorityChanged() {
        Log.d("checkPriorityChanged", "Started");
        // Check if a priority change had occurred.
        if(this.prevPriority !=this.task.getPriority()) {
            this.newPriority = this.task.getPriority();
            if(this.prevPriority == Config.PRIORITY.HIGH.getValue()) {
                // Priority fell from HIGH
                // Remove from reminder notification table.
                this.taskDbHelper.deleteRSN(this.task.getId());
                Log.d("checkPriorityChanged", "Fell from high priority");
                this.prevPriority = task.getPriority();
            } else if(this.newPriority == Config.PRIORITY.HIGH.getValue()) {
                // Priority was set to HIGH
                // Check if pending collaborators are present
                Log.d("checkPriorityChanged", "Rose to high priority");
                if(taskDbHelper.hasPendingCollaborators(this.task)) {
                    // Add to reminder notification table.
                    RemindSyncNotification rsn = taskDbHelper.createRSN(this.task);
                    // Calculate alarm time.
                    long notifInterval = 20 * 60 * 1000;
                    if(task.getDueDateTimeAsLong()!=0) {
                        long timeDiff = Math.abs(task.getDueDateTimeAsLong() - rsn.getCreatedTime());
                        notifInterval = timeDiff/3;
                    }
                    long nextAlarmTime = rsn.getCreatedTime();
                    do {
                        nextAlarmTime += notifInterval;
                    } while (nextAlarmTime<System.currentTimeMillis());
                    // Setup intent for pending intent
                    Intent myIntent = new Intent(this.context, ReminderNotifier.class);
                    // Add task id to intent
                    myIntent.putExtra(Config.REQUEST_RESPONSE_KEYS.UUID.getKey(), rsn.getTaskId());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            this.context,
                            (int) task.getId(),
                            myIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                    );
                    AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            nextAlarmTime,
                            pendingIntent
                    );
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy h:mm:ss a");

                    Log.d("checkPriorityChanged", "alarm set for "+sdf.format(new Date(nextAlarmTime)) );

                }
            }
        }
    }


//    @Override
//    public void onBackPressed() {
//        String TAG = CLASS_TAG+"onBackPressed";
//        if(this.isEditMode) {
//            // Set mode.
//            this.isEditMode = false;
//            // Update TextViews
//            this.taskTitleET.setText(this.task.getName());
//            this.taskDescriptionET.setText(this.task.getDescription());
//            this.taskPrioritySpinner.setSelection(this.task.getPriority());
//            this.dueDateTV.setText(this.task.getDueDateTime());
//            // Hide edit views
//            this.taskTitleET.setVisibility(View.GONE);
//            this.taskDescriptionET.setVisibility(View.GONE);
//            this.taskPrioritySpinner.setVisibility(View.GONE);
//            this.calendarIV.setVisibility(View.GONE);
//            this.cancelIV.setVisibility(View.GONE);
//            // display display views
//            this.taskTitleTV.setVisibility(View.VISIBLE);
//            this.taskDescriptionTV.setVisibility(View.VISIBLE);
//            this.taskPriorityTV.setVisibility(View.VISIBLE);
//            // Set toggle button to off
//            this.editViewToggle.setIcon(R.drawable.ic_edit_white);
//        } else {
//            getActivity().onBackPressed();
//        }
//    }
}
