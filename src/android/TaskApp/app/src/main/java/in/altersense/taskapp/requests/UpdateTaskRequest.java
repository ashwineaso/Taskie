package in.altersense.taskapp.requests;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.APIRequest;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.models.Task;

/**
 * Created by mahesmohan on 2/24/15.
 */
public class UpdateTaskRequest extends AsyncTask<Void, Integer, JSONObject> {

    private static final String CLASS_TAG = "UpdateTaskRequest";
    private final Task task;
    private Activity activity;
    private final Context context;
    private JSONObject requestObject;

    public UpdateTaskRequest(Task task, Context context) {
        this.task = task;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        this.requestObject = new JSONObject();
        try {
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.UUID.getKey(),
                    this.task.getUuid()
                    );
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.NAME.getKey(),
                    this.task.getName()
            );
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.DESCRIPTION.getKey(),
                    this.task.getDescription()
            );
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.DUE_DATE_TIME.getKey(),
                    this.task.getDueDateTime()
            );
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.PRIORITY.getKey(),
                    this.task.getPriority()
            );

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        String TAG = CLASS_TAG+"doInBackground";
        JSONObject responseObject = new JSONObject();
        APIRequest updateTask = new APIRequest(
                AltEngine.formURL("task/editTask"),
                this.requestObject,
                this.context
        );
        try {
            responseObject = updateTask.request();
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
                JSONObject data = result.getJSONObject(Config.REQUEST_RESPONSE_KEYS.DATA.getKey());
                this.task.setSyncStatus(true);
                this.task.setUuid(data.getString(Config.REQUEST_RESPONSE_KEYS.UUID.getKey()), this.context);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // if failed
        // @TODO: Handle updation task request failure.
    }
}
