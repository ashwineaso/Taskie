package in.altersense.taskapp.requests;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
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
    private Collaborator collaborator;
    private Context context;
    private int status;
    private Task task;
    private Boolean isOwner = false;
    private JSONObject requestObject;

    public TaskStatusChangeRequest(Task task, Context context) {
        Log.d(CLASS_TAG, "Created request for owner.");
        this.task = task;
        this.context = context;
        this.status = task.getStatus();
        this.isOwner = true;
    }

    public TaskStatusChangeRequest(Task task, Collaborator collaborator, Context context) {
        Log.d(CLASS_TAG, "Created request for collaborator.");
        this.task = task;
        this.context = context;
        this.collaborator = collaborator;
        this.status = collaborator.getStatus();
        this.isOwner = false;
    }

    @Override
    protected void onPreExecute() {
        JSONArray dataArray = new JSONArray();
        JSONObject dataObject = new JSONObject();
        this.requestObject = new JSONObject();
        if (isOwner) {
            // Do if the change is made by the owner
            try {
                dataObject.put(
                        Config.REQUEST_RESPONSE_KEYS.UUID.getKey(),
                        this.task.getUuid()
                );
                dataObject.put(
                        Config.REQUEST_RESPONSE_KEYS.STATUS.getKey(),
                        this.status
                );
                dataArray.put(dataObject);
                this.requestObject.put(Config.REQUEST_RESPONSE_KEYS.DATA.getKey(), dataArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            // Do if the change is made by the collaborator
            try {
                dataObject.put(
                        Config.REQUEST_RESPONSE_KEYS.UUID.getKey(),
                        this.task.getUuid()
                );
                dataObject.put(
                        Config.REQUEST_RESPONSE_KEYS.EMAIL.getKey(),
                        this.collaborator.getEmail()
                );
                dataObject.put(
                        Config.REQUEST_RESPONSE_KEYS.STATUS.getKey(),
                        this.status
                );
                dataArray.put(dataObject);
                this.requestObject.put(Config.REQUEST_RESPONSE_KEYS.DATA.getKey(), dataArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        String TAG = CLASS_TAG+"doInBackground";
        JSONObject responseObject = new JSONObject();
        APIRequest changeStatusRequest;
        if (isOwner) {
            changeStatusRequest = new APIRequest(
                    AltEngine.formURL("task/modifyTaskStatus"),
                    requestObject,
                    this.context
            );
        }
        else {
            changeStatusRequest = new APIRequest(
                    AltEngine.formURL("task/modifyCollStatus"),
                    requestObject,
                    this.context
            );
        }
        
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
        String status = "";
        try {
            status = result.getString(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
            if(status.equals(Config.RESPONSE_STATUS_SUCCESS)) {
                this.task.setSyncStatus(true);
                this.task.updateTask(this.context);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
