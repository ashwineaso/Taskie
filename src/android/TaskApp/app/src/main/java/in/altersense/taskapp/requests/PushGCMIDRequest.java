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

    public PushGCMIDRequest(String ownerUUID, String GCMRegId, Activity activity) {
        this.userUUID = ownerUUID;
        this.GCMRegId = GCMRegId;
        this.activity = activity;
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
            } else {
                Log.d(TAG, "Request failed.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
