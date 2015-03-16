package in.altersense.taskapp.requests;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.APIRequest;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.database.CollaboratorDbHelper;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.database.UserDbHelper;
import in.altersense.taskapp.models.Task;

/**
 * Created by mahesmohan on 3/15/15.
 */
public class BuzzCollaboratorRequest extends AsyncTask<Void, Integer, JSONObject> {

    private static final String CLASS_TAG = "BuzzCollaboratorRequest ";

    private Task task;
    private final Activity activity;
    private JSONObject requestObject;
    private TaskDbHelper taskDbHelper;
    private UserDbHelper userDbHelper;
    private CollaboratorDbHelper collaboratorDbHelper;

    public BuzzCollaboratorRequest(Task task, Activity activity) {
        this.task = task;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        String TAG = CLASS_TAG + "onPreExecute";
        super.onPreExecute();
        this.requestObject = new JSONObject();
        this.taskDbHelper = new TaskDbHelper(activity.getApplicationContext());
        this.userDbHelper = new UserDbHelper(activity.getApplicationContext());
        this.collaboratorDbHelper = new CollaboratorDbHelper(activity.getApplicationContext());
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
                //Syncing task.
                Log.d(TAG, "taskSyncRequest called");
                JSONObject responseOfSync = taskSyncRequest.getApiRequest().request();
                taskSyncRequest.postExecuteSyncTask(responseOfSync.getJSONObject(
                        Config.REQUEST_RESPONSE_KEYS.DATA.getKey()
                ));
                this.task = taskSyncRequest.getTask();
                this.task.setSyncStatus(true);
                this.task.updateTask(this.activity);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
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
            responseObject = buzzCollabs.request();
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