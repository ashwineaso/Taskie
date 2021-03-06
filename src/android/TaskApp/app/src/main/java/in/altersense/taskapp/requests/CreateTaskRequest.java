package in.altersense.taskapp.requests;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.APIRequest;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.database.UserDbHelper;
import in.altersense.taskapp.models.Collaborator;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.models.User;

/**
 * Created by mahesmohan on 2/11/15.
 */
public class CreateTaskRequest extends AsyncTask<Void, Integer, JSONObject> {

    private static final String CLASS_TAG = "CreateTaskRequest ";
    private TaskDbHelper taskDbHelper;
    private UserDbHelper userDbHelper;
    private Context context;
    private ArrayList<Task> taskList;
    private JSONObject requestObject;
    private JSONArray collaborators;

    public CreateTaskRequest(Task[] tasks, Context context) {
        this.taskList = new ArrayList<Task>(Arrays.asList(tasks));
        this.context = context;
        this.taskDbHelper = new TaskDbHelper(context);
        this.userDbHelper = new UserDbHelper(context);
    }

    public CreateTaskRequest(Task task, Context context) {
        this(new Task[] {task}, context);
    }

    @Override
    protected void onPreExecute() {
        String TAG = CLASS_TAG+"onPreExecute";
        super.onPreExecute();
        JSONArray requestObjectJSONArray = new JSONArray();
        JSONObject taskObject = new JSONObject();
        this.requestObject = new JSONObject();
        this.collaborators = new JSONArray();
        try {
            for(Task task:taskList) {
                taskObject = new JSONObject();
                taskObject.put(
                        Config.REQUEST_RESPONSE_KEYS.OWNER.getKey(),
                        task.getOwner().getUuid()
                );
                taskObject.put(
                        Config.REQUEST_RESPONSE_KEYS.TASK_NAME.getKey(),
                        task.getName()
                );
                //Adding the dueDateTime to the request
                try {
                    taskObject.put(
                            Config.REQUEST_RESPONSE_KEYS.DUE_DATE_TIME.getKey(),
                            task.getDueDateTimeAsLong()
                    );
                } catch (NullPointerException e) {
                    Log.d(TAG, " No Due Date or Time set");
                }
                // Addign the priority to the request
                try {
                    taskObject.put(
                            Config.REQUEST_RESPONSE_KEYS.PRIORITY.getKey(),
                            task.getPriority()
                    );
                } catch (NullPointerException e) {
                    Log.d(TAG, " No priorty specified. Defaults to Normal Priority");
                }
                //Adding the task description to the request
                try {
                    taskObject.put(
                            Config.REQUEST_RESPONSE_KEYS.DESCRIPTION.getKey(),
                            task.getDescription()
                    );
                } catch (NullPointerException e) {
                    Log.d(TAG, " No Description for the task");
                }

                this.collaborators = new JSONArray();
                //Adding Collaborators
                if(task.getCollaborators(task, this.context).size()>0) {
                    for(User collaborator : task.getCollaborators()) {
                        this.collaborators.put(collaborator.getEmail());
                    }
                }
                taskObject.put(
                        Config.REQUEST_RESPONSE_KEYS.TASK_COLLABOATORS.getKey(),
                        this.collaborators
                );

                requestObjectJSONArray.put(taskObject);
            }
            this.requestObject.put(Config.REQUEST_RESPONSE_KEYS.DATA.getKey(), requestObjectJSONArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        String TAG = CLASS_TAG+"doInBackground";
        JSONObject responseObject = new JSONObject();
        APIRequest createTask = new APIRequest(
                AltEngine.formURL("task/addNewTask"),
                this.requestObject,
                this.context
        );
        try {
            responseObject = createTask.request();
            Log.d(TAG, "Request sent");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseObject;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        String TAG = CLASS_TAG+"onPostExecute";
        super.onPostExecute(result);
        Log.d(TAG, "Response received: " + result.toString());
        try {
            String status = result.getString(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
            // Check whether the request was success
            if(status.equals(Config.RESPONSE_STATUS_SUCCESS)) {
                // If success update uuid
                JSONArray dataArray = result.getJSONArray(Config.REQUEST_RESPONSE_KEYS.DATA.getKey());
                for(int ctr = 0; ctr<dataArray.length(); ctr++) {
                    Task task = this.taskList.get(ctr);
                    JSONObject taskdata = dataArray.getJSONObject(ctr);
                    JSONObject data = taskdata.getJSONObject(Config.REQUEST_RESPONSE_KEYS.DATA.getKey());
                    status = taskdata.getString(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
                    if(status.equals(Config.RESPONSE_STATUS_SUCCESS)) {
                        task.setUuid(data.getString(Config.REQUEST_RESPONSE_KEYS.UUID.getKey()));
                        task.setSyncStatus(true);
                        task.updateTask(context);

                        JSONArray collaboratorArray = data.getJSONArray(Config.REQUEST_RESPONSE_KEYS.TASK_COLLABOATORS.getKey());
                        for(int colCtr=0; colCtr<collaboratorArray.length(); colCtr++) {
                            JSONObject collaboratorObject = collaboratorArray.getJSONObject(colCtr);

                            User user = userDbHelper.retrieve(collaboratorObject.getString(
                                    Config.REQUEST_RESPONSE_KEYS.EMAIL.getKey()
                            ));
                            user.setUuid(collaboratorObject.getString(
                                    Config.REQUEST_RESPONSE_KEYS.UUID.getKey()
                            ));
                            user.setName(collaboratorObject.getString(
                                    Config.REQUEST_RESPONSE_KEYS.NAME.getKey()
                            ));
                            user.setSyncStatus(true);
                            userDbHelper.updateUser(user);
                            // TODO: Check whether user exists
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // if failed
            // @TODO: Handle creation task request failure.
    }
}
