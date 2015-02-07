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
 * Created by mahesmohan on 2/5/15.
 */
public class RegisterUserRequest extends AsyncTask<Void, Integer, JSONObject> {

    private ProgressDialog dialog;
    private User user;
    private JSONObject requestObject;
    private Activity activity;
    private String TAG = "RegisterUserRequest";

    public RegisterUserRequest(User user, Activity activity) {
        this.user = user;
        this.dialog = new ProgressDialog(
                activity
        );
        this.requestObject = new JSONObject();
        try {
            requestObject.put(Config.REQUEST_KEYS.EMAIL.getKey(),this.user.getEmail());
            requestObject.put(Config.REQUEST_KEYS.NAME.getKey(),this.user.getName());
            requestObject.put(Config.REQUEST_KEYS.PASSWORD.getKey(),this.user.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!this.dialog.isShowing()) {
            this.dialog.show();
        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        // Create a response object.
        JSONObject response = new JSONObject();
        // Create a URL request
        APIRequest apiRequest = new APIRequest(
                AltEngine.formURL("user/register"),
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
        // Return response.
        return response;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        Log.i(TAG, result.toString());
        if(this.dialog.isShowing()) {
            this.dialog.hide();
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
