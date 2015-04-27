package in.altersense.taskapp.requests;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.APIRequest;
import in.altersense.taskapp.components.AltEngine;

/**
 * Created by mahesmohan on 4/27/15.
 */
public class ResetPasswordRequest extends AsyncTask<Void, Integer, JSONObject
        > {

    private String email;
    private JSONObject requestObject;
    private Activity activity;
    private ProgressDialog dialog;

    public ResetPasswordRequest(String email, Activity activity) {
        this.email = email;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        this.requestObject = new JSONObject();
        this.dialog = new ProgressDialog(this.activity);
        try {
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.EMAIL.getKey(),
                    this.email
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.dialog.setMessage("Loading request...");
        if(!this.dialog.isShowing()) {
            this.dialog.show();
        }
        this.dialog.setCancelable(false);
        publishProgress(1);
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject responseObject = new JSONObject();
        APIRequest passwordReset = new APIRequest(
                AltEngine.formURL("user/resetPassword"),
                this.requestObject,
                this.activity.getApplicationContext()
        );
        try {
            responseObject = passwordReset.request();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseObject;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        try {
            String status = jsonObject.getString(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
            String message, title;
            if(status.equals(Config.RESPONSE_STATUS_SUCCESS)) {
                message = "Please check your inbox for password reset instructions.";
                title = "Success";
            } else {
                title = "Failed";
                message = jsonObject.getString(Config.REQUEST_RESPONSE_KEYS.MESSAGE.getKey());
            }
            AlertDialog alertDialog = new AlertDialog.Builder(this.activity).create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    activity.finish();
                }
            });
            if(dialog.isShowing()) {
                dialog.hide();
            }
            alertDialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if(values.equals(1)) {
            dialog.setMessage("Please wait...");
        }
    }
}
