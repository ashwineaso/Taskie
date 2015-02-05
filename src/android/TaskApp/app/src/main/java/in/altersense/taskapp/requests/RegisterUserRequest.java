package in.altersense.taskapp.requests;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.models.User;

/**
 * Created by mahesmohan on 2/5/15.
 */
public class RegisterUserRequest extends AsyncTask<Void, Void, JSONObject> {

    private ProgressDialog dialog;
    private User user;
    private JSONObject requestObject;

    public RegisterUserRequest(User user, Activity activity) {
        this.user = user;
        this.dialog = new ProgressDialog(
                activity.getApplicationContext()
        );
        this.requestObject = new JSONObject();
        try {
            requestObject.put(Config.REQUEST_KEYS.EMAIL.getKey(),this.user.getEmail());
            requestObject.put(Config.REQUEST_KEYS.NAME.getKey(),this.user.getName());
            requestObject.put(Config.REQUEST_KEYS.PASSWORD.getKey(),this.user.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        // Create a URL requset
        
        // Make request and fetch response.
        // Return response.
    }
}
