package in.altersense.taskapp.requests;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private User user;
    private Task task;
    private final Intent syncCompleteBroadcastIntent;
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
        this.requestObject = new JSONObject();
        if(syncUser) {
            this.mode = 1;
            prepareSyncUser();
        } else if(syncTask) {
            this.mode = 2;
            prepareSyncTask();
        } else if(syncEverything) {
            this.mode = 3;
            prepareSyncEverything();
        } else {
            this.mode=0;
        }
        this.syncCompleteBroadcastIntent = new Intent(Config.SHARED_PREF_KEYS.SYNC_IN_PROGRESS.getKey());
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

    public User getUser() {
        return user;
    }

    public Task getTask() {
        return task;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject responseObject = new JSONObject();

        AltEngine.writeBooleanToSharedPref(
                activity.getApplicationContext(),
                Config.SHARED_PREF_KEYS.SYNC_IN_PROGRESS.getKey(),
                true
        );

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
        this.activity.sendBroadcast(syncCompleteBroadcastIntent);
        Log.d(TAG, "Broadcast sent.");
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
        TaskDbHelper taskDbHelper = new TaskDbHelper(activity.getApplicationContext());
        UserDbHelper userDbHelper = new UserDbHelper(activity.getApplicationContext());
        CollaboratorDbHelper collaboratorDbHelper = new CollaboratorDbHelper(activity.getApplicationContext());
        for(int ctr=0; ctr<taskArray.length();ctr++) {
            Log.d(TAG, "TaskCtr: "+ctr+1);
            JSONObject taskObject = taskArray.getJSONObject(ctr);
            Task task = taskFromJSONObject(taskObject);
            Log.d(TAG, "Check whether task owner is in db");
            User taskOwner = userDbHelper.getUserByUUID(task.getOwner().getUuid());
            if(taskOwner==null) {
                Log.d(TAG, "Task owner not in db. Adding.");
                task.getOwner().setSyncStatus(true);
                taskOwner = userDbHelper.createUser(task.getOwner());
                task.setOwner(taskOwner);
            }
            task = taskDbHelper.createTask(task,activity);
            Log.d(TAG, "Setting up collaborators.");
            collaboratorsFromJSONArray(
                    taskObject.getJSONArray(Config.REQUEST_RESPONSE_KEYS.TASK_COLLABOATORS.getKey()),
                    task,
                    collaboratorDbHelper,
                    userDbHelper
            );
            task.setCollaborators(collaboratorDbHelper.getAllCollaborators(task));
            Log.d(TAG, "Setting up collaborators done.");
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
        TaskDbHelper taskDbHelper = new TaskDbHelper(activity.getApplicationContext());
        UserDbHelper userDbHelper = new UserDbHelper(activity.getApplicationContext());
        CollaboratorDbHelper collaboratorDbHelper = new CollaboratorDbHelper(activity.getApplicationContext());
        Task task = taskFromJSONObject(taskObject);
        Log.d(TAG, "Check whether task owner is in db");
        User taskOwner = userDbHelper.getUserByUUID(task.getOwner().getUuid());
        if(taskOwner==null) {
            Log.d(TAG, "Task owner not in db. Adding.");
            task.getOwner().setSyncStatus(true);
            taskOwner = userDbHelper.createUser(task.getOwner());
            task.setOwner(taskOwner);
        }
        Log.d(TAG, "Check whether the task exists in database.");
        if(this.task.getId()>0) {
            Log.d(TAG, "Task exists so updating.");
            task.setId(this.task.getId());
            taskDbHelper.updateTask(task);
        } else {
            Log.d(TAG, "Task does not exist so adding.");
            task = taskDbHelper.createTask(task,activity);
        }
        Log.d(TAG, "Task creation updation done.");
        Log.d(TAG, "Clearing all collaborators if any.");
        collaboratorDbHelper.delete(task);
        Log.d(TAG, "Setting up collaborators.");
        collaboratorsFromJSONArray(
                taskObject.getJSONArray(Config.REQUEST_RESPONSE_KEYS.TASK_COLLABOATORS.getKey()),
                task,
                collaboratorDbHelper,
                userDbHelper
        );
        task.setCollaborators(collaboratorDbHelper.getAllCollaborators(task));
        Log.d(TAG, "Setting up collaborators done.");
        Log.d(TAG, "Task set: "+task.toString());
        this.task = task;
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
        Log.d(TAG, "User created/updated: "+user.getString());
        this.user = user;
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
        Task task = new Task(
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
        Log.d(TAG, "Returning task: "+task.toString());
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

    private void collaboratorsFromJSONArray(
            JSONArray collaborators,
            Task task,
            CollaboratorDbHelper collaboratorDbHelper,
            UserDbHelper userDbHelper
    ) throws JSONException {
        String TAG = CLASS_TAG+"collaboratorsFromJSONArray";
        for(int collCtr=0; collCtr<collaborators.length(); collCtr++) {
            Log.d(TAG, "Setting up collaborator "+collCtr+1+ " params.");
            JSONObject collaboratorObject = collaborators.getJSONObject(collCtr);
            int collStatus = collaboratorObject.getJSONObject(
                    Config.REQUEST_RESPONSE_KEYS.STATUS.getKey()
            ).getInt(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
            User collaboratorUser = userFromJSONObject(collaboratorObject);
            Log.d(TAG, "Check whether collaborating user is present in database.");
            User userInDb = userDbHelper.getUserByUUID(collaboratorUser.getUuid());
            if(userInDb==null) {
                Log.d(TAG, "Not found.");
                collaboratorUser.setSyncStatus(true);
                collaboratorUser = userDbHelper.createUser(collaboratorUser);
            } else {
                collaboratorUser = userInDb;
            }
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
