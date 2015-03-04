package in.altersense.taskapp.requests;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.APIRequest;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.models.User;

/**
 * Created by mahesmohan on 2/26/15.
 */
public class PushGCMIDRequest extends AsyncTask<Void, Integer, JSONObject> {

    private static String CLASS_TAG = "PushGCMIDRequest ";
    private String userUUID;

    private String GCMRegId;
    private Activity activity;
    private JSONObject requestObject;
    private String ALREADY_SYNCED_FLAG;

    public PushGCMIDRequest(String ownerUUID, String GCMRegId, Activity activity, String GCM_ALREADY_SYNCED_KEY) {
        this.userUUID = ownerUUID;
        this.GCMRegId = GCMRegId;
        this.activity = activity;
        this.ALREADY_SYNCED_FLAG = GCM_ALREADY_SYNCED_KEY;
    }

    @Override
    protected void onPreExecute() {
        this.requestObject = new JSONObject();
        try {
            requestObject.put(Config.REQUEST_RESPONSE_KEYS.UUID.getKey(), userUUID);
            requestObject.put(Config.REQUEST_RESPONSE_KEYS.SERVER_PUSH_ID.getKey(), GCMRegId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject reponseObject = new JSONObject();
        try {
            APIRequest gcmRegIdPush = new APIRequest(
                    AltEngine.formURL("user/setServerPushId"),
                    requestObject,
                    activity
            );
            reponseObject = gcmRegIdPush.request();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reponseObject;
    }

    @Override
    protected void onPostExecute(JSONObject response) {
        String TAG = CLASS_TAG+"onPostExecute";
        Log.d(TAG, "Response: "+response.toString());
        try {
            String status = response.getString(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
            if(status.equals(Config.RESPONSE_STATUS_SUCCESS)) {
                Log.d(TAG, "Response is success.");
                AltEngine.writeBooleanToSharedPref(
                        activity.getApplicationContext(),
                        this.ALREADY_SYNCED_FLAG,
                        true
                );
            } else {
                Log.d(TAG, "Request failed.");
                AltEngine.writeBooleanToSharedPref(
                        activity.getApplicationContext(),
                        this.ALREADY_SYNCED_FLAG,
                        false
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
