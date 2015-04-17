package in.altersense.taskapp.requests;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.APIRequest;
import in.altersense.taskapp.components.AltEngine;
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

    private final Context context;
    private final TaskDbHelper taskDbHelper;
    private final UserDbHelper userDbHelper;
    private APIRequest apiRequest;
    private List<User> userList;
    private List<Task> taskList;
    private Intent syncCompleteBroadcastIntent;
    private JSONObject requestObject;
    private String url;

    private Collaborator deviceOwnerAsCollaborator;

    private int mode;
    private boolean collaboratorAcceptedTask = false;

    /**
     * Base constructor called by all the constructors
     * @param context Current context.
     * @param users Users to be synced.
     * @param tasks Tasks to be synced
     * @param syncUser True if this is a userList sync
     * @param syncTask True if this is a taskList sync
     * @param syncEverything True if this is a request to sync an entire account.
     */
    private SyncRequest(
            Context context,
            User[] users,
            Task[] tasks,
            boolean syncUser,
            boolean syncTask,
            boolean syncEverything
    ) {
        this.context = context;
        try {
            this.userList = new ArrayList<User>(Arrays.asList(users));
        } catch (NullPointerException e) {
            this.userList = new ArrayList<>();
        }
        try {
            this.taskList = new ArrayList<>(Arrays.asList(tasks));
        } catch (NullPointerException e) {
            this.taskList = new ArrayList<>();
        }
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

        this.taskDbHelper = new TaskDbHelper(this.context);
        this.userDbHelper = new UserDbHelper(this.context);

        this.syncCompleteBroadcastIntent = new Intent(Config.SHARED_PREF_KEYS.SYNC_IN_PROGRESS.getKey());

        this.apiRequest = new APIRequest(
                this.url,
                this.requestObject,
                this.context
        );

        deviceOwnerAsCollaborator = new Collaborator(User.getDeviceOwner(context));
        deviceOwnerAsCollaborator.setStatus(Config.COLLABORATOR_STATUS.ACCEPTED.getStatus());
    }

    /**
     * A constructor for syncing everything.
     * @param context Current context.
     */
    public SyncRequest(Context context) {
        this(context, null, null, false, false, true);
    }

    /**
     * SyncRequest to sync a single user.
     * @param user User to be synced.
     * @param context Current context.
     */
    public SyncRequest(User user, Context context) {
        this(context, new User[] {user}, null, true, false, false);
    }

    /**
     * Constructor with list of users or list of tasks Nbut not both.
     * @param userList List of users to be synced
     * @param taskList List of users to be synced
     * @param context Current context.
     */
    public SyncRequest(List<User> userList, List<Task> taskList, Context context) {
        this(context,userList.toArray(new User[userList.size()]),taskList.toArray(new Task[taskList.size()]),userList!=null,taskList!=null,false);
    }

    /**
     * SyncRequest to sync a single task.
     * @param task Task to be synced.
     * @param context Current activity.
     */
    public SyncRequest(Task task, Context context) {
        this(context,null,new Task[] {task},false,true,false);
    }

    public User getUser() {
        return userList.get(0);
    }

    public Task getTask() {
        return taskList.get(0);
    }

    public APIRequest getApiRequest() {
        return apiRequest;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject responseObject = new JSONObject();

        AltEngine.writeBooleanToSharedPref(
                context,
                Config.SHARED_PREF_KEYS.SYNC_IN_PROGRESS.getKey(),
                true
        );

        try {
            responseObject = this.apiRequest.request();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseObject;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        String TAG = CLASS_TAG+"onPostExecute";
        super.onPostExecute(result);

        JSONArray responseArray = new JSONArray();
        try {
            responseArray = result.getJSONArray(Config.REQUEST_RESPONSE_KEYS.DATA.getKey());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (this.mode){
            case 1:
                //  Sync User
                try {
                    for(int ctr=0; ctr<responseArray.length();ctr++) {
                        JSONObject responseObject = responseArray.getJSONObject(ctr);
                        postExecuteSyncUser(responseObject, userList.get(ctr));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                // Sync Task
                try {
                    for(int ctr=0; ctr<responseArray.length();ctr++) {
                        JSONObject responseObject = responseArray.getJSONObject(ctr);
                        postExecuteSyncTask(responseObject, taskList.get(ctr));
                    }
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
        this.context.sendBroadcast(syncCompleteBroadcastIntent);
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
                            context,
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
        JSONArray requestObjectsArray = new JSONArray();
        try {
            for (Task task:taskList) {
                requestObjectsArray.put(
                        task.getUuid()
                );
            }
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.DATA.getKey(),
                    requestObjectsArray
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
        JSONArray requestObjectsArray = new JSONArray();
        try {
            for(User user:userList) {
                requestObjectsArray.put(
                        user.getEmail()
                );
            }
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.DATA.getKey(),
                    requestObjectsArray
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
    public void postExecuteSyncEverything(JSONArray taskArray) throws JSONException {
        String TAG = CLASS_TAG+"postExecuteSyncEverything";
        for(int ctr=0; ctr<taskArray.length();ctr++) {
            Log.d(TAG, "TaskCtr: "+ctr+1);
            JSONObject taskObject = taskArray.getJSONObject(ctr);
            Task task = taskFromJSONObject(taskObject);
            Log.d(TAG, "Setting up collaborators.");
            collaboratorsFromJSONArray(
                    taskObject.getJSONArray(Config.REQUEST_RESPONSE_KEYS.TASK_COLLABOATORS.getKey()),
                    task
            );
            task.setCollaborators(taskDbHelper.getAllCollaborators(task));
            Log.d(TAG, "Setting up collaborators done.");
            Log.d(TAG, "Task added to db: "+task.toString());
        }
    }

    /**
     * Post execute for syncTask
     * @param taskObject JSONObject of task returned as response.
     */
    public void postExecuteSyncTask(JSONObject taskObject, Task task) throws JSONException {
        String TAG = CLASS_TAG+"postExecuteSyncTask";
        Log.d(TAG, "Started.");
        String taskStatus = taskObject.getString(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
        if(taskStatus.equals(Config.RESPONSE_STATUS_SUCCESS)) {

            //Assign the Task data to a JSONObject for ease of handling
            JSONObject taskData = taskObject.getJSONObject(Config.REQUEST_RESPONSE_KEYS.DATA.getKey());
            Task taskFromJSONObject = taskFromJSONObject(taskData);
            Log.d(TAG, "Task creation updation done.");

            Log.d(TAG, "Clearing all collaborators if any.");
            taskDbHelper.deleteCollaborator(taskFromJSONObject);
            Log.d(TAG, "Setting up collaborators.");

            try {
                collaboratorsFromJSONArray(
                        taskData.getJSONArray(Config.REQUEST_RESPONSE_KEYS.TASK_COLLABOATORS.getKey()),
                        taskFromJSONObject
                );
                taskFromJSONObject.setCollaborators(taskDbHelper.getAllCollaborators(taskFromJSONObject));
            } catch (JSONException e) {
                Log.d(TAG, "No collaborators.");
                taskFromJSONObject.setCollaborators(new ArrayList<Collaborator>());
            }
            // Check if the device user just accepted the task.
            if(this.collaboratorAcceptedTask) {
                TaskStatusChangeRequest taskStatusChangeRequest = new TaskStatusChangeRequest(
                        taskFromJSONObject, deviceOwnerAsCollaborator,context

                );
                taskStatusChangeRequest.execute();
                this.collaboratorAcceptedTask = false;
            }
            Log.d(TAG, "Setting up collaborators done.");
            Log.d(TAG, "Task set: "+taskFromJSONObject.toString());
            int position = this.taskList.indexOf(task);
            this.taskList.remove(position);
            this.taskList.add(position, taskFromJSONObject);
        }
    }

    /**
     * Post execute for syncUserInfo
     * @param userObject User as JSONObject from the response.
     * @throws JSONException
     */
    public void postExecuteSyncUser(JSONObject userObject, User user) throws JSONException {
        String TAG = CLASS_TAG+"userFromJSONObject";
        String userStatus = userObject.getString(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
        if(userStatus.equals(Config.RESPONSE_STATUS_SUCCESS)) {

            UserDbHelper userDbHelper = new UserDbHelper(context);
            User userFromJSONObject = userFromJSONObject(userObject.getJSONObject(Config.REQUEST_RESPONSE_KEYS.DATA.getKey()));
            userFromJSONObject.setSyncStatus(true);
            Log.d(TAG, "Checking whether userFromJSONObject present in db");
            if(user.getId()>0) {
                Log.d(TAG, "User present in db. Updating db.");
                userFromJSONObject.setId(user.getId());
                userDbHelper.updateUser(userFromJSONObject);
            } else {
                // TODO Make sure that this part is never executed. Normally this shouldn't.
                Log.d(TAG, "User not present in db. Inserting into db.");
                userFromJSONObject = userDbHelper.createUser(userFromJSONObject);
            }
            Log.d(TAG, "User created/updated: " + userFromJSONObject.getString());
            int position = this.userList.indexOf(user);
            this.userList.remove(user);
            this.userList.add(position, userFromJSONObject);
        }
    }

    public Task taskFromJSONObject (JSONObject taskObject) throws JSONException {
        String TAG = CLASS_TAG+"taskFromJSONObject";

        Log.d(TAG, "Setting up task properties");
        String name = taskObject.getString(Config.REQUEST_RESPONSE_KEYS.NAME.getKey());
        String uuid = taskObject.getString(Config.REQUEST_RESPONSE_KEYS.UUID.getKey());
        Integer priority = taskObject.getInt(Config.REQUEST_RESPONSE_KEYS.PRIORITY.getKey());
        Long dueDateTime = taskObject.getLong(Config.REQUEST_RESPONSE_KEYS.DUE_DATE_TIME.getKey());
        String descr = taskObject.getString(Config.REQUEST_RESPONSE_KEYS.DESCRIPTION.getKey());
        int status = taskObject.getJSONObject(
                Config.REQUEST_RESPONSE_KEYS.STATUS.getKey()
        ).getInt(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
        Log.d(TAG, "Setting up task properties done.");
        JSONObject ownerObject = taskObject.getJSONObject(Config.REQUEST_RESPONSE_KEYS.OWNER.getKey());
        Log.d(TAG, "Setting up owner of task.");
        User owner = userFromJSONObject(ownerObject);
        Log.d(TAG, "Setting up owner of task done.");
        Task taskFromDatabase = this.taskDbHelper.getTaskByUUID(uuid);
        Task task;
        Log.d(TAG, "Check whether the taskFromJSONObject exists in database.");
        if(taskFromDatabase != null) {
            Log.d(TAG, "Task exists so updating.");
            task = taskFromDatabase;
            task.setUuid(uuid);
            task.setName(name);
            task.setDescription(descr);
            task.setPriority(priority);
            task.setDueDateTime(dueDateTime);
            task.setStatus(status);
            task.setGroup(false);
            task.setOwner(owner);
            task.setCollaborators(taskDbHelper.getAllCollaborators(task));
            task.updateTask(context);
        } else {
            Log.d(TAG, "Task does not exist so adding.");
            task = new Task(
                    uuid,
                    name,
                    descr,
                    owner,
                    priority,
                    dueDateTime,
                    status,
                    false,
                    null,
                    this.context
            );
            this.taskDbHelper.createTask(task);
        }

        Log.d(TAG, "Returning task: "+task.toString());
        return task;
    }

    public User userFromJSONObject (JSONObject userObject) throws JSONException {
        String TAG = CLASS_TAG+"userFromJSONObject";
        Log.d(TAG, "Setting up user properties.");
        String userName = userObject.getString(Config.REQUEST_RESPONSE_KEYS.NAME.getKey());
        String userUUID = userObject.getString(Config.REQUEST_RESPONSE_KEYS.UUID.getKey());
        String userEmail = userObject.getString(Config.REQUEST_RESPONSE_KEYS.EMAIL.getKey());
        User user = this.userDbHelper.retrieve(userEmail);
        if(user!=null) {
            user.setName(userName);
            user.setUuid(userUUID);
        } else {
            User newUser = new User(userUUID, userEmail, userName, 0);
            user = this.userDbHelper.createUser(newUser);
        }
        Log.d(TAG, "Setting up user properties done.");
        return user;
    }

    public void collaboratorsFromJSONArray(
            JSONArray collaborators,
            Task task
    ) throws JSONException {
        String TAG = CLASS_TAG+"collaboratorsFromJSONArray";
        Log.d(TAG, "Array of collaborators: "+collaborators.toString());
        for(int collCtr=0; collCtr<collaborators.length(); collCtr++) {
            Log.d(TAG, "Setting up collaborator "+collCtr+1+ " params.");
            JSONObject collaboratorObject = collaborators.getJSONObject(collCtr);
            int collStatus = collaboratorObject.getJSONObject(
                    Config.REQUEST_RESPONSE_KEYS.STATUS.getKey()
            ).getInt(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
            User collaboratorUser = userFromJSONObject(collaboratorObject);
            Log.d(TAG, "Making collaborator out of user.");
            Collaborator collaborator = new Collaborator(collaboratorUser);
            // Check if collaborator is the device user.
            if(collaborator.getEmail().equals(User.getDeviceOwner(context).getEmail())) {
                // if status is pending change to accepted
                if(collStatus==Config.COLLABORATOR_STATUS.PENDING.getStatus()) {
                    collStatus = Config.COLLABORATOR_STATUS.ACCEPTED.getStatus();
                    // set a flag to push changes.
                    this.collaboratorAcceptedTask = true;
                }
                // else leave as is
            }
            collaborator.setStatus(collStatus);
            Log.d(TAG, "Adding collaborator to db.");
            this.taskDbHelper.addCollaborator(task, collaborator);
        }
    }
}
