package in.altersense.taskapp.requests;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.APIRequest;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.models.User;

/**
 * Created by mahesmohan on 2/10/15.
 */
public class UserLoginRequest extends AsyncTask<Void, Integer, JSONObject> {

    private boolean startActivity;
    private User user;
    private ProgressDialog dialog;
    private JSONObject requestObject;
    private Activity activity;
    private String TAG = "UserLoginRequest";

    public UserLoginRequest(User user, Activity activity, boolean startActivity) {
        this.user = user;
        this.activity = activity;
        this.dialog = new ProgressDialog(
                activity
        );
        this.requestObject = new JSONObject();
        try {
            this.requestObject.put(Config.REQUEST_RESPONSE_KEYS.EMAIL.getKey(),user.getEmail());
            this.requestObject.put(Config.REQUEST_RESPONSE_KEYS.PASSWORD.getKey(),user.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.startActivity = startActivity;
    }

    public UserLoginRequest(User user, Activity activity) {
        this(
                user,
                activity,
                true
        );
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(!this.dialog.isShowing()) {
            this.dialog.setMessage(Config.MESSAGES.LOGIN_REQUEST.getMessage());
            this.dialog.show();
        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        // Create a response object.
        JSONObject response = new JSONObject();
        // Create a URL request
        APIRequest apiRequest= new APIRequest(
                AltEngine.formURL("user/autorize"),
                this.requestObject,
                this.activity
        );
        // Make request and fetch response.
        try {
            publishProgress(1);
            response = apiRequest.request();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Return response
        return response;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        Log.i(TAG, result.toString());
        // Hide dialog
        if (this.dialog.isShowing()) {
            this.dialog.hide();
        }
        // Check whether the request was success
        try {
            String responseStatus = result.getString(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
            if (responseStatus.equals(Config.RESPONSE_STATUS_SUCCESS)) {
                // If success
                // set access and refresh tokens
                // load TasksActivity according to the the start activity flag
            } else {
                // if not
                // display error dialog
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

        @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if(values.equals(1)) {
            this.dialog.setCancelable(false);
        }
    }
}
