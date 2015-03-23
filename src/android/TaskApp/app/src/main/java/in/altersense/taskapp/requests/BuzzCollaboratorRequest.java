package in.altersense.taskapp.requests;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.APIRequest;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.database.CollaboratorDbHelper;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.database.UserDbHelper;
import in.altersense.taskapp.models.Buzz;
import in.altersense.taskapp.models.Task;

/**
 * Created by mahesmohan on 3/15/15.
 */
public class BuzzCollaboratorRequest extends AsyncTask<Void, Integer, JSONObject> {

    private static final String CLASS_TAG = "BuzzCollaboratorRequest ";

    private final Activity activity;
    private JSONObject requestObject;
    private TaskDbHelper taskDbHelper;
    private UserDbHelper userDbHelper;
    private CollaboratorDbHelper collaboratorDbHelper;
    private List<Buzz> buzzList;

    public BuzzCollaboratorRequest(Buzz buzz, Activity activity) {
        this.activity = activity;
        this.buzzList = new ArrayList<>();
        buzzList.add(buzz);
    }

    public BuzzCollaboratorRequest(List<Buzz> buzzList, Activity activity) {
        this.buzzList = buzzList;
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

        for(Buzz buzzFromList:buzzList) {
            Task task = taskDbHelper.getTaskByRowId(buzzFromList.getTaskId());
            // Checks whether the task is synced and has an id.
            if(
                    buzzFromList.getTaskUuid().length()<0 ||
                            !task.getSyncStatus()
                    ) {
                Log.d(TAG, "No uuid for task to be buzzed.");
                SyncRequest taskSyncRequest = new SyncRequest(task, this.activity);
                try {
                    //Syncing task.
                    Log.d(TAG, "taskSyncRequest called");
                    JSONObject responseOfSync = taskSyncRequest.getApiRequest().request();
                    JSONObject taskObject = responseObject.getJSONObject(Config.REQUEST_RESPONSE_KEYS.DATA.getKey());
                    if(!taskObject.get(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey()).equals(Config.RESPONSE_STATUS_SUCCESS)) {
                        if(taskObject.getInt(
                                Config.REQUEST_RESPONSE_KEYS.ERROR_CODE.getKey()
                        ) == Config.REQUEST_ERROR_CODES.TASK_WITH_ID_NOT_FOUND.getCode()) {
                            continue;
                        }
                    }
                    taskSyncRequest.postExecuteSyncTask(responseOfSync.getJSONObject(
                                    Config.REQUEST_RESPONSE_KEYS.DATA.getKey()
                            ),
                            task
                    );
                    task = taskSyncRequest.getTask();
                    // Task sync complete setting task id to updated task object
                    task.setId(buzzFromList.getTaskId());
                    task.setSyncStatus(true);
                    // update task in database
                    task.updateTask(this.activity);
                    // set the task uuid to buzz instance
                    buzzFromList.setTaskUuid(task.getUuid());
                    // update buzz in db and replace the updated buzz with the one in buzz list
                    if(taskDbHelper.updateBuzz(buzzFromList)) {
                        int buzzPosition = this.buzzList.indexOf(buzzFromList);
                        this.buzzList.remove(buzzPosition);
                        this.buzzList.add(buzzPosition,buzzFromList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Set up Buzz Request
            JSONArray buzzes = new JSONArray();
            buzzes.put(buzzFromList.getTaskUuid());

        }

        try {
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.DATA.getKey(),
                    buzzList
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
        String TAG = CLASS_TAG+"onPostExecute";
        super.onPostExecute(result);
        try {
            JSONArray resultArray = result.getJSONArray(Config.REQUEST_RESPONSE_KEYS.DATA.getKey());
            for(int ctr  = 0; ctr<resultArray.length();ctr++) {
                result = resultArray.getJSONObject(ctr);
                String status = result.getString(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
                if(status.equals(Config.RESPONSE_STATUS_SUCCESS)) {
                    // TODO: Remove the toast.
                    Toast.makeText(
                            activity.getApplicationContext(),
                            "Buzz sent!",
                            Toast.LENGTH_SHORT
                    ).show();
                    taskDbHelper.deleteBuzz(buzzList.get(0));
                    buzzList.remove(0);
                }
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