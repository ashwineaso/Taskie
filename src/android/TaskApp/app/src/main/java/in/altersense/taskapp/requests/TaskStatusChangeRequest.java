package in.altersense.taskapp.requests;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.APIRequest;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.models.Collaborator;
import in.altersense.taskapp.models.Task;

/**
 * Created by mahesmohan on 2/18/15.
 */
public class TaskStatusChangeRequest extends AsyncTask<Void, Integer, JSONObject>{

    private static String CLASS_TAG = "TaskStatusChangeRequest ";
    private int status;
    private Task task;
    private Activity activity;
    private JSONObject requestObject;

    public TaskStatusChangeRequest(Task task, Activity activity) {
        Log.d(CLASS_TAG, "Created request for owner.");
        this.task = task;
        this.activity = activity;
        this.status = task.getStatus();
    }

    public TaskStatusChangeRequest(Task task, Collaborator collaborator, Activity activity) {
        Log.d(CLASS_TAG, "Created request for collaborator.");
        this.task = task;
        this.activity = activity;
        this.status = collaborator.getStatus();
    }

    @Override
    protected void onPreExecute() {
        this.requestObject = new JSONObject();
        try {
            requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.UUID.getKey(),
                    this.task.getUuid()
            );
            requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.STATUS.getKey(),
                    this.status
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        String TAG = CLASS_TAG+"doInBackground";
        JSONObject responseObject = new JSONObject();
        APIRequest changeStatusRequest = new APIRequest(
                AltEngine.formURL("task/modifyTaskStatus"),
                requestObject,
                this.activity
        );
        try {
            responseObject = changeStatusRequest.request();
            Log.d(TAG, "Made request.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseObject;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        String TAG = CLASS_TAG+"onPostExecute";
        Log.d(TAG, "Response: "+result.toString());
        boolean status = false;
        try {
            status = result.getBoolean(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"Response status "+status);
    }
}
