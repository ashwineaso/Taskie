package in.altersense.taskapp.requests;

import android.app.AlertDialog;
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
    private Context context;

    public ResetPasswordRequest(String email, Context context) {
        this.email = email;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        this.requestObject = new JSONObject();
        try {
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.EMAIL.getKey(),
                    this.email
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject responseObject = new JSONObject();
        APIRequest passwordReset = new APIRequest(
                AltEngine.formURL("user/resetPassword"),
                this.requestObject,
                this.context
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
            AlertDialog alertDialog = new AlertDialog.Builder(this.context).create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
