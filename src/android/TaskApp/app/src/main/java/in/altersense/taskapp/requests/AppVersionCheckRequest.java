package in.altersense.taskapp.requests;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.APIRequest;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.components.BaseApplication;
import in.altersense.taskapp.events.UpdateNowEvent;

/**
 * Created by mahesmohan on 4/29/15.
 */
public class AppVersionCheckRequest extends AsyncTask<Void, Integer, JSONObject>{

    private Context context;
    private JSONObject requestObject;
    private int appVersionCode;

    public AppVersionCheckRequest(Context context) {
        this.context = context;
        try {
            this.appVersionCode = this.context.getPackageManager().getPackageInfo(
                    this.context.getPackageName(),
                    0
            ).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            this.appVersionCode = 1;
        }
    }

    @Override
    protected void onPreExecute() {
        this.requestObject = new JSONObject();
        try {
            this.requestObject.put(
                    Config.REQUEST_RESPONSE_KEYS.VERSION_CODE.getKey(),
                    this.appVersionCode
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject responseObject = new JSONObject();
        // Check if update is necessary from SHARED PREF
        boolean isUpdateNecessary = AltEngine.readBooleanFromSharedPref(
                context,
                Config.SHARED_PREF_KEYS.IS_UPDATE_NECESSARY.getKey(),
                false
        );
        if(isUpdateNecessary) {
            // Check current version > failed version
            int failedVersion = AltEngine.readIntFromSharedPref(
                    context,
                    Config.SHARED_PREF_KEYS.FAILED_APP_VERSION.getKey(),
                    0
            );
            if(this.appVersionCode > failedVersion) {
                // Set update necessary to false
                AltEngine.writeBooleanToSharedPref(
                        context,
                        Config.SHARED_PREF_KEYS.IS_UPDATE_NECESSARY.getKey(),
                        false
                );
            } else {
                // else set response object status to failed
                try {
                    responseObject.put(
                            Config.REQUEST_RESPONSE_KEYS.STATUS.getKey(),
                            Config.RESPONSE_STATUS_FAILED
                    );
                    responseObject.put(
                            Config.REQUEST_RESPONSE_KEYS.ERROR_CODE.getKey(),
                            Config.REQUEST_ERROR_CODES.APP_VERSION_DEPRECATED.getCode()
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // and return response object
                return responseObject;
            }
        }
        // Create APIRequestObject
        APIRequest appVersionCheck = new APIRequest(
                AltEngine.formURL("appVersionCheck"),
                this.requestObject,
                this.context
        );
        // Make request
        try {
            responseObject = appVersionCheck.request();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // return the response
        return responseObject;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        // Check status
        String responseStatus = "";
        int errorCode = 0;
        try {
            responseStatus = jsonObject.getString(Config.REQUEST_RESPONSE_KEYS.STATUS.getKey());
            errorCode = jsonObject.getInt(Config.REQUEST_RESPONSE_KEYS.ERROR_CODE.getKey());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // if failed
        if(responseStatus.equals(Config.RESPONSE_STATUS_FAILED) && errorCode == Config.REQUEST_ERROR_CODES.APP_VERSION_DEPRECATED.getCode()) {
            // add failed version to SHARED PREF
            AltEngine.writeIntToSharedPref(
                    context,
                    Config.SHARED_PREF_KEYS.FAILED_APP_VERSION.getKey(),
                    this.appVersionCode
            );
            // add true for IS UPDATE NECESSARY in SHARED PREF
            AltEngine.writeBooleanToSharedPref(
                    context,
                    Config.SHARED_PREF_KEYS.IS_UPDATE_NECESSARY.getKey(),
                    true
            );
            // fire an update necessary event to event bus.
            BaseApplication.getEventBus().post(new UpdateNowEvent(this.appVersionCode));
        }
    }
}
