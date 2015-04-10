package in.altersense.taskapp.requests;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.APIRequest;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.models.Task;

/**
 * Created by ashwineaso on 4/10/15.
 */
public class TaskDeleteRequest extends AsyncTask<Void, Integer, JSONObject>{

    private static final String CLASS_TAG = "TaskDeleteRequest";
    private final Task task;
    private final Context context;
    private JSONObject requestObject;

    public TaskDeleteRequest(Task task, Context context) {
        this.task = task;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        JSONObject dataObject = new JSONObject();
        JSONArray dataArray = new JSONArray();
        this.requestObject = new JSONObject();
        try {
            dataObject.put(
                    Config.REQUEST_RESPONSE_KEYS.UUID.getKey(),
                    this.task.getUuid()
            );
            dataArray.put(dataObject);
            this.requestObject.put(Config.REQUEST_RESPONSE_KEYS.DATA.getKey(), dataArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject responseObject = new JSONObject();
        APIRequest deleteTask = new APIRequest(
                AltEngine.formURL("task/deleteTask"),
                requestObject,
                this.context
        );
        try {
            responseObject = deleteTask.request();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseObject;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        String TAG = CLASS_TAG + "OnPostExecute";
        Log.d(TAG, "Response for Task delete request: "+result.toString());
        String status = "";
        try {
            status = result.getString(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
            if (status.equals(Config.RESPONSE_STATUS_SUCCESS)) {
                Log.d(TAG, " Task Removed Successfully");
            } else {
                Log.d(TAG, " Task Removal Failed");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
