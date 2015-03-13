package in.altersense.taskapp.requests;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
 * Created by ashwineaso on 2/27/15.
 */
public class SyncTaskRequest extends AsyncTask<Void, Integer, JSONObject> {

    private Task task, temp;
    private User user;
    private Collaborator collaborator;
    private Activity activity;
    private JSONObject requestObject;
    private List<in.altersense.taskapp.models.Collaborator> collaboratorList, newCollaboratorList;

    private static String CLASS_TAG = "SyncTaskRequest ";

    public SyncTaskRequest(Task task, Activity activity) {
        this.task = task;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        this.requestObject = new JSONObject();
        try {
            requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.UUID.getKey(),
                    task.getUuid()
            );

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject responseObject = new JSONObject();
        APIRequest syncTaskRequest = new APIRequest(
                AltEngine.formURL("task/syncTask"),
                requestObject,
                this.activity
        );
        try {
            responseObject = syncTaskRequest.request();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseObject;

    }

    @Override
    protected void onPostExecute(JSONObject result) {

        String TAG = CLASS_TAG+"onPostExecute";
        Log.d(TAG, "Response: " + result.toString());
        boolean responseStatus = false;
        JSONObject taskObject = new JSONObject();
        try {
            responseStatus = result.getBoolean(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
            if(responseStatus) {
                //Set up a task object
                taskObject = result.getJSONObject(Config.REQUEST_RESPONSE_KEYS.DATA.getKey());
                //
                TaskDbHelper taskDbHelper = new TaskDbHelper(activity.getApplicationContext());
                task.setUuid(taskObject.getString(Config.REQUEST_RESPONSE_KEYS.UUID.getKey()), activity);
                task.setName(taskObject.getString(Config.REQUEST_RESPONSE_KEYS.NAME.getKey()));
                task.setDescription(taskObject.getString(Config.REQUEST_RESPONSE_KEYS.DESCRIPTION.getKey()));
                task.setDueDateTime(taskObject.getLong(Config.REQUEST_RESPONSE_KEYS.DESCRIPTION.getKey()));
                task.setPriority(taskObject.getInt(Config.REQUEST_RESPONSE_KEYS.PRIORITY.getKey()));

                try {
                    //Checking if task already exists in the DB
                    temp = taskDbHelper.getTaskByUUID(task.getUuid(), activity);
                    task.setId(temp.getId());
                    taskDbHelper.updateTask(task);
                } catch (Exception e) {
                    //If task is not in db, create a new task
                    temp = taskDbHelper.createTask(task, activity); // The task gets assigned to temp in both cases
                }

                //Get all the collaborators of the task
                CollaboratorDbHelper collaboratorDbHelper = new CollaboratorDbHelper(activity.getApplicationContext());
                collaboratorList = collaboratorDbHelper.getAllCollaborators(temp);
                //Get the collaborators into a list
                JSONArray jsonArray = taskObject.getJSONArray(Config.REQUEST_RESPONSE_KEYS.TASK_COLLABOATORS.getKey());
                UserDbHelper userDbHelper = new UserDbHelper(activity.getApplicationContext());
                for (int i=0; i<jsonArray.length(); i++ ) {
                    JSONObject collaboratorObj = jsonArray.getJSONObject(i);
                    String email = collaboratorObj.getString(Config.REQUEST_RESPONSE_KEYS.EMAIL.getKey());
                    //Check if the user exists in the db
                    try {
                        user = userDbHelper.getUserByNameEmail(email);
                        for(Collaborator coll:collaboratorList) {
                            //If we get a user match set the status and update the value to db and remove from list
                            if (coll.getUser() == user) {
                                coll.setStatus(collaboratorObj.getInt(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey()));
                                collaboratorDbHelper.updateStatus(temp, coll);
                                collaboratorList.remove(coll);
                            }
                        }

                    } catch (Exception e) {
                        //If failed, create a new collaborator
                        String name = collaboratorObj.getString(Config.REQUEST_RESPONSE_KEYS.NAME.getKey());
                        String uuid = collaboratorObj.getString(Config.REQUEST_RESPONSE_KEYS.UUID.getKey());
                        //Create a new user and Add it to DB
                        User newUser = new User(uuid, email, name);
                        newUser = userDbHelper.createUser(newUser);
                        //Create a new Collaborator object using the user and add to the list
                        Collaborator newcoll = new Collaborator(newUser);
                        newcoll.setStatus(collaboratorObj.getInt(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey()));
                        newCollaboratorList.add(newcoll);
                    }
                }

                //collaboratorList now contains collaborators that are no longer in the task
                //So we remove them from the db
                for (Collaborator person:collaboratorList) {
                    collaboratorDbHelper.removeCollaborator(temp, person);
                }

                //Add the new collaborators to the db
                for (Collaborator newcoll:collaboratorList) {
                    collaboratorDbHelper.addCollaborator(temp, newcoll);
                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
