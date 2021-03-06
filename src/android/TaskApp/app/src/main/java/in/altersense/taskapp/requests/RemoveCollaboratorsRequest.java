package in.altersense.taskapp.requests;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.APIRequest;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.models.Collaborator;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.models.User;

/**
 * Created by mahesmohan on 2/19/15.
 */
public class RemoveCollaboratorsRequest extends AsyncTask<Void, Integer, JSONObject> {

    private static String CLASS_TAG = "AddCollaboratorsRequest ";
    private final Context context;
    private Task task;
    private List<Collaborator> collaborators;
    private Activity activity;
    private JSONObject requestObject;

    public RemoveCollaboratorsRequest(Task task, List<Collaborator> collboratorList, Context context) {
        this.task = task;
        this.collaborators = collboratorList;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        JSONObject dataObject = new JSONObject();
        JSONArray dataArray = new JSONArray();
        this.requestObject = new JSONObject();
        JSONArray collaboratorJSONArray = new JSONArray();
        try {
            dataObject.put(
                    Config.REQUEST_RESPONSE_KEYS.UUID.getKey(),
                    this.task.getUuid()
            );
            for(User collaborator:collaborators) {
                collaboratorJSONArray.put(collaborator.getEmail());
            }
            dataObject.put(
                    Config.REQUEST_RESPONSE_KEYS.TASK_COLLABOATORS.getKey(),
                    collaboratorJSONArray
            );
            dataArray.put(dataObject);
            this.requestObject.put(Config.REQUEST_RESPONSE_KEYS.DATA.getKey(),dataArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject responseObject = new JSONObject();
        APIRequest addCollaborators = new APIRequest(
                AltEngine.formURL("task/remCollaborators"),
                requestObject,
                this.context
        );
        try {
            responseObject = addCollaborators.request();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseObject;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        String TAG = CLASS_TAG+"onPostExecute";
        Log.d(TAG, "Response for removing collaborators: "+result.toString());
        String status = "";
        try {
            status = result.getString(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
            if(status.equals(Config.RESPONSE_STATUS_SUCCESS)) {
                Log.d(TAG, "Collaborators removed.");
            } else {
                Log.d(TAG, "Collaborator removal failed.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
