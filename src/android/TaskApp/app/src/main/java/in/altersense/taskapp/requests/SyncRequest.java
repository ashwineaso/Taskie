package in.altersense.taskapp.requests;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.APIRequest;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.database.CollaboratorDbHelper;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.database.UserDbHelper;
import in.altersense.taskapp.models.Collaborator;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.models.User;

/**
 * Created by mahesmohan on 3/10/15.
 */
public class SyncRequest extends AsyncTask<Void, Integer, JSONObject> {

    private static final String CLASS_TAG = "SyncRequest ";

    private final Activity activity;
    private final User user;
    private final Task task;
    private JSONObject requestObject;
    private String url;

    private int mode;

    /**
     * Base constructor called by all the constructors
     * @param activity Current activity.
     * @param user User to be synced.
     * @param task Task to be synced
     * @param syncUser True if this is a user sync
     * @param syncTask True if this is a task sync
     * @param syncEverything True if this is a request to sync an entire account.
     */
    private SyncRequest(
            Activity activity,
            User user,
            Task task,
            boolean syncUser,
            boolean syncTask,
            boolean syncEverything) {
        this.activity = activity;
        this.user = user;
        this.task = task;
        if(syncUser) {
            this.mode = 1;
        } else if(syncTask) {
            this.mode = 2;
        } else if(syncEverything) {
            this.mode = 3;
        } else {
            this.mode=0;
        }
    }

    /**
     * A constructor for syncing everything.
     * @param activity Current activity.
     */
    public SyncRequest(Activity activity) {
        this(activity, null, null, false, false, true);
    }

    /**
     * SyncRequest to sync a single user.
     * @param user User to be synced.
     * @param activity Current activity.
     */
    public SyncRequest(User user, Activity activity) {
        this(activity,user,null,true,false,false);
    }

    /**
     * SyncRequest to sync a single task.
     * @param task Task to be synced.
     * @param activity Current activity.
     */
    public SyncRequest(Task task, Activity activity) {
        this(activity,null,task,false,true,false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        switch (this.mode){
            case 1:
                //  Sync User
                prepareSyncUser();
                break;
            case 2:
                // Sync Taskn
                prepareSyncTask();
                break;
            case 3:
                // Sync everything
                prepareSyncEverything();
                break;
            default:
                // Hell breaks loose.
        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject responseObject = new JSONObject();
        APIRequest syncRequest = new APIRequest(
                this.url,
                this.requestObject,
                this.activity
        );
        try {
            responseObject = syncRequest.request();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseObject;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        String TAG = CLASS_TAG+"onPostExecute";
        super.onPostExecute(result);
        String status = "";
        try {
            status = result.getString(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!status.equals(Config.RESPONSE_STATUS_SUCCESS)) {
            Log.d(TAG, "Request failed.");
            return;
        }
        switch (this.mode){
            case 1:
                //  Sync User
                try {
                    postExecuteSyncUser(result.getJSONObject(Config.REQUEST_RESPONSE_KEYS.DATA.getKey()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                // Sync Task
                try {
                    postExecuteSyncTask(result.getJSONObject(Config.REQUEST_RESPONSE_KEYS.DATA.getKey()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                // Sync everything
                try {
                    postExecuteSyncEverything(result.getJSONArray(Config.REQUEST_RESPONSE_KEYS.DATA.getKey()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                // Hell breaks loose.
        }
    }

    /**
     * Preparese for the sync of everything related to the account.
     */
    private void prepareSyncEverything() {
        String TAG = CLASS_TAG+"prepareSyncEverything";
        try {
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.UUID.getKey(),
                    AltEngine.readStringFromSharedPref(
                            activity.getApplicationContext(),
                            Config.SHARED_PREF_KEYS.OWNER_ID.getKey(),
                            ""
                    )
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.url = AltEngine.formURL("task/syncAllTasks");
        Log.d(TAG, "Content: "+this.requestObject.toString());
        Log.d(TAG, "Url: "+this.url);
    }

    /**
     * Prepares payload and url for syncTask.
     */
    private void prepareSyncTask() {
        String TAG = CLASS_TAG+"prepareSyncTask";
        try {
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.UUID.getKey(),
                    this.task.getId()
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.url = AltEngine.formURL("task/syncTask");
        Log.d(TAG, "Content: "+this.requestObject.toString());
        Log.d(TAG, "Url: "+this.url);
    }

    /**
     * Prepares payload and url for syncUserInfo
     */
    private void prepareSyncUser() {
        String TAG = CLASS_TAG+"prepareSyncUser";
        try {
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.UUID.getKey(),
                    user.getUuid()
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.url = AltEngine.formURL("user/syncUserInfo");
        Log.d(TAG, "Content: "+this.requestObject.toString());
        Log.d(TAG, "Url: "+this.url);
    }

    /**
     * PostExecute for SyncEverything
     * @param taskArray The array of tasks returned by the response.
     * @throws JSONException
     */
    private void postExecuteSyncEverything(JSONArray taskArray) throws JSONException {
        String TAG = CLASS_TAG+"postExecuteSyncEverything";
        CollaboratorDbHelper collaboratorDbHelper = new CollaboratorDbHelper(activity.getApplicationContext());
        for(int ctr=0; ctr<taskArray.length();ctr++) {
            Log.d(TAG, "TaskCtr: "+ctr+1);
            JSONObject taskObject = taskArray.getJSONObject(ctr);
            Task task = taskFromJSONObject(taskObject);
            Log.d(TAG, "Task added to db: "+task.toString());
        }
    }

    /**
     * Post execute for syncTask
     * @param taskObject JSONObject of task returned as response.
     */
    private void postExecuteSyncTask(JSONObject taskObject) throws JSONException {
        String TAG = CLASS_TAG+"postExecuteSyncTask";
        Log.d(TAG, "Started.");
        Task task = taskFromJSONObject(taskObject);
        Log.d(TAG, "Task set: "+task.toString());
    }

    /**
     * Post execute for syncUserInfo
     * @param userObject User as JSONObject from the response.
     * @throws JSONException
     */
    private void postExecuteSyncUser(JSONObject userObject) throws JSONException {
        String TAG = CLASS_TAG+"userFromJSONObject";
        UserDbHelper userDbHelper = new UserDbHelper(activity.getApplicationContext());
        User user = userFromJSONObject(userObject);
        user.setSyncStatus(true);
        Log.d(TAG, "Checking whether user present in db");
        if(this.user.getId()>0) {
            Log.d(TAG, "User present in db. Updating db.");
            userDbHelper.updateUser(user);
        } else {
            Log.d(TAG, "User not present in db. Inserting into db.");
            userDbHelper.createUser(user);
        }
    }

    private Task taskFromJSONObject (JSONObject taskObject) throws JSONException {
        String TAG = CLASS_TAG+"taskFromJSONObject";
        TaskDbHelper taskDbHelper = new TaskDbHelper(this.activity.getApplicationContext());
        Log.d(TAG, "Setting up task properties");
        String name = taskObject.getString(Config.REQUEST_RESPONSE_KEYS.NAME.getKey());
        String uuid = taskObject.getString(Config.REQUEST_RESPONSE_KEYS.UUID.getKey());
        int priority = taskObject.getInt(Config.REQUEST_RESPONSE_KEYS.PRIORITY.getKey());
        String descr = taskObject.getString(Config.REQUEST_RESPONSE_KEYS.DESCRIPTION.getKey());
        int status = taskObject.getJSONObject(
                Config.REQUEST_RESPONSE_KEYS.STATUS.getKey()
        ).getInt(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
        Log.d(TAG, "Setting up task properties done.");
        JSONObject ownerObject = taskObject.getJSONObject(Config.REQUEST_RESPONSE_KEYS.OWNER.getKey());
        Log.d(TAG, "Setting up owner of task.");
        User owner = userFromJSONObject(ownerObject);
        Log.d(TAG, "Setting up owner of task done.");
        Log.d(TAG, "Now checking if task is present in db.");
        Task task = taskDbHelper.getTaskByUUID(uuid, activity);
        if(task==null) {
            task = new Task(
                    uuid,
                    name,
                    descr,
                    owner,
                    priority,
                    0,
                    status,
                    false,
                    null,
                    activity
            );
            Log.d(TAG, "Task not found in the db. Adding.");
            task = taskDbHelper.createTask(task, activity);
            Log.d(TAG, "Task set up in the db.");
        }
        Log.d(TAG, "Setting up collaborators.");
        JSONArray collaborators = taskObject.getJSONArray(Config.REQUEST_RESPONSE_KEYS.TASK_COLLABOATORS.getKey());
        collaboratorsFromJSONArray(collaborators);
        Log.d(TAG, "Setting up collaborators done.");
        task.fetchAllCollaborators(this.activity.getApplicationContext());
        return task;
    }

    private User userFromJSONObject (JSONObject userObject) throws JSONException {
        String TAG = CLASS_TAG+"userFromJSONObject";
        Log.d(TAG, "Setting up user properties.");
        UserDbHelper userDbHelper = new UserDbHelper(this.activity.getApplicationContext());
        String userName = userObject.getString(Config.REQUEST_RESPONSE_KEYS.NAME.getKey());
        String userUUID = userObject.getString(Config.REQUEST_RESPONSE_KEYS.UUID.getKey());
        String userEmail = userObject.getString(Config.REQUEST_RESPONSE_KEYS.EMAIL.getKey());
        Log.d(TAG, "Setting up user properties done.");
        User newUser = new User(userUUID, userEmail, userName);
        return newUser;
    }

    private void collaboratorsFromJSONArray(JSONArray collaborators) throws JSONException {
        String TAG = CLASS_TAG+"collaboratorsFromJSONArray";
        UserDbHelper userDbHelper = new UserDbHelper(this.activity.getApplicationContext());
        CollaboratorDbHelper collaboratorDbHelper = new CollaboratorDbHelper(this.activity.getApplicationContext());
        for(int collCtr=0; collCtr<collaborators.length(); collCtr++) {
            Log.d(TAG, "Setting up collaborator "+collCtr+1+ " params.");
            JSONObject collaboratorObject = collaborators.getJSONObject(collCtr);
            int collStatus = collaboratorObject.getJSONObject(
                    Config.REQUEST_RESPONSE_KEYS.STATUS.getKey()
            ).getInt(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
            User collaboratorUser = userFromJSONObject(collaboratorObject);
            Log.d(TAG, "Making collaborator out of user.");
            Collaborator collaborator = new Collaborator(collaboratorUser);
            collaborator.setStatus(collStatus);
            Log.d(TAG, "Checking collaboration.");
            if(!collaboratorDbHelper.isCollaborator(task,collaborator)){
                Log.d(TAG, "Adding collaborator to db.");
                collaboratorDbHelper.addCollaborator(task, collaborator);
                Log.d(TAG, "Added collaborator to db.");
            }
        }
    }
}
