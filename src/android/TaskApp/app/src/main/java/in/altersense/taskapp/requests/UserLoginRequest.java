package in.altersense.taskapp.requests;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import in.altersense.taskapp.DashboardActivity;
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.APIRequest;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.database.UserDbHelper;
import in.altersense.taskapp.models.User;

/**
 * Created by mahesmohan on 2/10/15.
 */
public class UserLoginRequest extends AsyncTask<Void, Integer, JSONObject> {

    private static final String TAG = "UserLoginRequest";

    private String authMethod;
    private boolean startActivity;
    private User user;
    private ProgressDialog dialog;
    private JSONObject requestObject;
    private Activity activity;

    private static final String GOOGLE_AUTH = "google";
    private static final String TASKIE_AUTH = "taskie";

    public UserLoginRequest(User user, Activity activity, boolean startActivity) {
        Log.d(TAG, "Creating login request");
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

    public UserLoginRequest(User user, Activity activity, String authMethod, boolean startActivity) {
        this.user = user;
        this.activity = activity;
        this.authMethod = authMethod;
        this.dialog = new ProgressDialog(
                activity
        );
        this.requestObject = new JSONObject();
        try {
            this.requestObject.put(Config.REQUEST_RESPONSE_KEYS.EMAIL.getKey(),user.getEmail());
            switch (this.authMethod) {
                case GOOGLE_AUTH:
                    this.requestObject.put(Config.REQUEST_RESPONSE_KEYS.NAME.getKey(),user.getName());
                    this.requestObject.put(Config.REQUEST_RESPONSE_KEYS.AUTHMETHOD.getKey(),this.authMethod);
                    break;
                case TASKIE_AUTH:
                    this.requestObject.put(Config.REQUEST_RESPONSE_KEYS.PASSWORD.getKey(),user.getPassword());
                    break;
                default:
                    this.requestObject.put(Config.REQUEST_RESPONSE_KEYS.PASSWORD.getKey(),user.getPassword());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.startActivity = startActivity;
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
                AltEngine.formURL("user/authorize"),
                this.requestObject,
                this.activity
        );
        // Make request and fetch response.
        try {
            publishProgress(1);
            Log.d(TAG, "Request sent.");
            response = apiRequest.requestWithoutTokens();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Return response
        return response;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        Log.i(TAG, "Response: "+result.toString());
        // Hide dialog
        if (this.dialog.isShowing()) {
            this.dialog.hide();
        }
        // Check whether the request was success
        try {
            String responseStatus = result.getString(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
            if (responseStatus.equals(Config.RESPONSE_STATUS_SUCCESS)) {
                // If success
                Log.d(TAG, "Request Success.");
                JSONObject data = result.getJSONObject(
                        Config.REQUEST_RESPONSE_KEYS.DATA.getKey()
                );
                // set access and refresh tokens
                String accessToken = data.getString(
                        Config.REQUEST_RESPONSE_KEYS.ACCESS_TOKEN.getKey()
                );
                String refreshToken = data.getString(
                        Config.REQUEST_RESPONSE_KEYS.REFRESH_TOKEN.getKey()
                );
                AltEngine.writeStringToSharedPref(
                        this.activity.getApplicationContext(),
                        Config.SHARED_PREF_KEYS.ACCESS_TOKEN.getKey(),
                        accessToken
                );
                AltEngine.writeStringToSharedPref(
                        this.activity.getApplicationContext(),
                        Config.SHARED_PREF_KEYS.REFRESH_TOKEN.getKey(),
                        refreshToken
                );
                String uuid = data.getString(
                        Config.REQUEST_RESPONSE_KEYS.UUID.getKey()
                );
                this.user.setUuid(uuid);
                String name = data.getString(
                        Config.REQUEST_RESPONSE_KEYS.NAME.getKey()
                );
                this.user.setName(name);
                // insert user to db.
                UserDbHelper userDbHelper = new UserDbHelper(this.activity.getApplicationContext());
                userDbHelper.createUser(this.user);
                // make user owner
                this.user.makeDeviceOwner(this.activity.getApplicationContext());

                // Call sync everything.
                SyncRequest syncRequest = new SyncRequest(this.activity);
                syncRequest.execute();

                // load TasksActivity according to the the start activity flag
                if(this.startActivity) {
                    Intent tasksActivityStarterIntent = new Intent(
                            this.activity.getApplicationContext(),
                            DashboardActivity.class
                    );
                    tasksActivityStarterIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.activity.startActivity(tasksActivityStarterIntent);
                    this.activity.finish();
                }
            } else {
                // if not
                // display error dialog
                Log.d(TAG, "Request failed.");
                String message = result.getString(
                        Config.REQUEST_RESPONSE_KEYS.MESSAGE.getKey()
                );
                if(this.startActivity) {
                    AlertDialog alertDialog = new AlertDialog.Builder(this.activity).create();
                    alertDialog.setTitle(Config.MESSAGES.LOGIN_ERROR_TITLE.getMessage());
                    alertDialog.setMessage(message);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }
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
