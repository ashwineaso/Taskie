package in.altersense.taskapp.requests;

import android.app.Activity;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.models.Task;

/**
 * Created by mahesmohan on 3/15/15.
 */
public class BuzzCollaboratorRequest extends AsyncTask<Void, Integer, JSONObject> {

    private Task task;
    private final Activity activity;
    private JSONObject requestObject;
    private JSONObject responseObject;

    public BuzzCollaboratorRequest(Task task, Activity activity) {
        this.task = task;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.requestObject = new JSONObject();
        if(
                this.task.getUuid().length()<0 ||
                        !this.task.getSyncStatus()
                ) {
            SyncRequest taskSyncRequest = new SyncRequest(this.task, this.activity);
            try {
                this.responseObject = taskSyncRequest.get();
                taskSyncRequest.onPostExecute(this.responseObject);
                this.task = taskSyncRequest.getTask();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        try {
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.UUID.getKey(),
                    this.task.getId()
            );
        } catch (JSONException e) {


        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        return null;
    }
}
