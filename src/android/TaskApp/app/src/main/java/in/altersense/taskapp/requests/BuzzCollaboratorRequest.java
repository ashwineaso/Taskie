package in.altersense.taskapp.requests;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.APIRequest;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.models.Task;

/**
 * Created by mahesmohan on 3/15/15.
 */
public class BuzzCollaboratorRequest extends AsyncTask<Void, Integer, JSONObject> {

    private static final String CLASS_TAG = "BuzzCollaboratorRequest ";

    private Task task;
    private final Activity activity;
    private JSONObject requestObject;

    public BuzzCollaboratorRequest(Task task, Activity activity) {
        this.task = task;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        String TAG = CLASS_TAG + "onPreExecute";
        super.onPreExecute();
        this.requestObject = new JSONObject();

    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        String TAG = CLASS_TAG+"doInBackground";
        JSONObject responseObject = new JSONObject();

        // Checks whether the task is synced and has an id.
        if(
                this.task.getUuid().length()<0 ||
                        !this.task.getSyncStatus()
                ) {
            Log.d(TAG, "No id for task to be buzzed.");
            SyncRequest taskSyncRequest = new SyncRequest(this.task, this.activity);
            try {
                Log.d(TAG, "taskSyncRequest called");
                taskSyncRequest.onPostExecute(taskSyncRequest.get());
                Log.d(TAG, "taskSyncRequest call completed");
                this.task = taskSyncRequest.getTask();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(
                        activity.getApplicationContext(),
                        "Could not send Buzz. Task sync was interrupted. Please connect to the internet.",
                        Toast.LENGTH_LONG
                ).show();
                this.cancel(true);
            } catch (ExecutionException e) {
                e.printStackTrace();
                Toast.makeText(
                        activity.getApplicationContext(),
                        "Could not send Buzz. Some error occurred. Please try again later.",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
        try {
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.UUID.getKey(),
                    this.task.getUuid()
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        APIRequest buzzCollabs = new APIRequest(
                AltEngine.formURL("task/buzzCollaborators"),
                this.requestObject,
                this.activity
        );
        try {
            requestObject = buzzCollabs.request();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseObject;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        try {
            String status = result.getString(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
            if(status.equals(Config.RESPONSE_STATUS_SUCCESS)) {
                Toast.makeText(
                        activity.getApplicationContext(),
                        "Buzz sent!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(
                    activity.getApplicationContext(),
                    "Buzz could not be sent. Please connect to the internet.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}