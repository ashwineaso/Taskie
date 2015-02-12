package in.altersense.taskapp.requests;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.APIRequest;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.models.User;

/**
 * Created by mahesmohan on 2/11/15.
 */
public class CreateTaskRequest extends AsyncTask<Void, Integer, JSONObject> {

    private static final String CLASS_TAG = "CreateTaskRequest ";
    private Task task;
    private Activity activity;
    private JSONObject requestObject;
    private JSONArray collaborators;

    public CreateTaskRequest(Task task, Activity activity) {
        this.task = task;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        String TAG = CLASS_TAG+"onPreExecute";
        super.onPreExecute();
        this.requestObject = new JSONObject();
        this.collaborators = new JSONArray();
        try {
            if(this.task.getCollaborators().size()>0) {
                for(User collaborator : task.getCollaborators()) {
                    this.collaborators.put(collaborator.getEmail());
                }
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "No collaborators.");
        }
        try {
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.OWNER.getKey(),
                    this.task.getOwner().getUuid()
            );
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.TASK_NAME.getKey(),
                    this.task.getName()
            );
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.TASK_COLLABOATORS.getKey(),
                    this.collaborators
            );
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
                this.activity
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
        // @TODO Set UUID when task is created.
    }
}
