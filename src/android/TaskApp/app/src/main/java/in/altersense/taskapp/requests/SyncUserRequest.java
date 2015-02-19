package in.altersense.taskapp.requests;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.APIRequest;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.database.CollaboratorDbHelper;
import in.altersense.taskapp.database.UserDbHelper;
import in.altersense.taskapp.models.Collaborator;
import in.altersense.taskapp.models.User;

/**
 * Created by mahesmohan on 2/18/15.
 */
public class SyncUserRequest extends AsyncTask<Void, Integer, JSONObject> {
    private User collaborator;
    private Activity activity;
    private JSONObject requestObject;

    private static String CLASS_TAG = "SyncUserRequest ";

    public SyncUserRequest(User collaborator, Activity activity) {
        this.collaborator = collaborator;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        this.requestObject = new JSONObject();
        try {
            requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.EMAIL.getKey(),
                    collaborator.getEmail()
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject responseObject = new JSONObject();
        APIRequest syncUserRequest = new APIRequest(
                AltEngine.formURL("user/syncUserInfo"),
                requestObject,
                this.activity
        );
        try {
            responseObject = syncUserRequest.request();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseObject;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        String TAG = CLASS_TAG+"onPostExecute";
        Log.d(TAG, "Response: "+result.toString());
        boolean responseStatus = false;
        JSONObject userObject = new JSONObject();
        try {
            responseStatus = result.getBoolean(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
            if(responseStatus) {
                // Set up user object
                userObject = result.getJSONObject(Config.REQUEST_RESPONSE_KEYS.DATA.getKey());
                // Update user.
                UserDbHelper collaboratorDbHelper = new UserDbHelper(activity.getApplicationContext());
                collaborator.setName(userObject.getString(Config.REQUEST_RESPONSE_KEYS.NAME.getKey()));
                collaborator.setUuid(userObject.getString(Config.REQUEST_RESPONSE_KEYS.UUID.getKey()));
                collaboratorDbHelper.updateUser(collaborator);
            }
        } catch (JSONException e) {
                e.printStackTrace();
        }
    }
}
