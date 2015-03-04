package in.altersense.taskapp.components;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.Callable;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.requests.PushGCMIDRequest;

/**
 * Created by mahesmohan on 2/25/15.
 */
public class GCMHandler {

    private static final String CLASS_TAG = "GCMHandler ";
    private boolean alreadySynced;

    private String CURRENT_APP_VERSION = "currentAppVersion";
    private String GCM_ALREADY_SYNCED = "gcmAlreadySynced";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private final GoogleCloudMessaging gcmInstance;
    private final String sharedPreferenceIdentifier;
    private final String sharedPreferenceKey;
    private final Activity activity;
    private String senderID;
    private String registrationID;

    private int maxCount;

    public GCMHandler(
            String senderID,
            String sharedPreferenceIdentifier,
            String sharedPreferenceKey,
            Activity activity,
            int maxCount
            ) {
        this.senderID = senderID;
        this.sharedPreferenceIdentifier = sharedPreferenceIdentifier;
        this.sharedPreferenceKey = sharedPreferenceKey;
        this.activity = activity;
        this.maxCount = maxCount;

        this.alreadySynced = readBooleanFromSharedPref(
                activity.getApplicationContext(),
                this.GCM_ALREADY_SYNCED,
                false
                );

        this.gcmInstance = GoogleCloudMessaging.getInstance(activity.getApplicationContext());

        if(checkPlayServices()) {
            String registrationId = this.getGCMRegistrationId();
            if(registrationId.isEmpty()) {
                this.registerInBackground();
            } else {
                if(!this.alreadySynced) {
                    new PushGCMIDRequest(senderID,registrationID,activity).execute();
                }
            }
        }
    }

    public GCMHandler(
            String senderID,
            String sharedPreferenceIdentifier,
            String sharedPreferenceKey,
            Activity activity
    ) {
        this(
                senderID,
                sharedPreferenceIdentifier,
                sharedPreferenceKey,
                activity,
                10
        );
    }

    public GoogleCloudMessaging getGcmInstance() {
        return gcmInstance;
    }

    public String getRegistrationID() {
        return registrationID;
    }

    /**
     * Checks whether Google Play Services are active in the device.
     * @return Boolean Whether Google Play Services are active or not.
     */
    public boolean checkPlayServices() {
        String TAG = CLASS_TAG+"checkPlayServices";
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this.activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported as there is no Play Store Services Active.");
                this.activity.finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the stored GCMRegistration ID
     * @return The GCM Registration ID stored in the device.
     */
    public String getGCMRegistrationId() {
        String TAG = CLASS_TAG+"getGCMRegistrationId";
        String regId = readStringFromSharedPref(
                this.activity.getApplicationContext(),
                this.sharedPreferenceKey,
                ""
        );
        if (regId.isEmpty()) {
            Log.i(TAG, "No registration ID.");
            return "";
        }
        int registeredVersion = readIntFromSharedPref(
                this.activity.getApplicationContext(),
                CURRENT_APP_VERSION,
                Integer.MIN_VALUE
        );
        int currentVersion = getAppVersion();
        if(currentVersion!=registeredVersion) {
            Log.i(TAG, "App version changed");
            return "";
        }
        return regId;
    }

    /**
     * Gets the current version of the app.
     * @return Application's current version code.
     */
    private int getAppVersion() {
        Context context = this.activity.getApplicationContext();
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }

    }

    /**
     * Fetches the GCM registration ID in the background.
     */
    private void registerInBackground() {
        final String TAG = CLASS_TAG+"registerInBackground";
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                String SUB_TAG = TAG+" doInBackground";

                String regId = "";
                int callCtr = 0;
                do {
                    callCtr++;
                    if(callCtr>=maxCount) {
                        Log.d(SUB_TAG, "Made GCM registration call #"+callCtr);
                        this.cancel(true);
                        break;
                    }
                } while (!registerGCM());
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                String SUB_TAG = TAG+" onPostExecute";
                Log.i(SUB_TAG, "regId fetched: "+registrationID);
                if(!registrationID.isEmpty()) {
                    writeStringToSharedPref(
                            activity.getApplicationContext(),
                            sharedPreferenceKey,
                            registrationID
                    );
                    writeIntToSharedPref(
                            activity.getApplicationContext(),
                            CURRENT_APP_VERSION,
                            getAppVersion()
                    );
                    new PushGCMIDRequest(
                            readStringFromSharedPref(
                                    activity.getApplicationContext(),
                                    Config.SHARED_PREF_KEYS.OWNER_ID.getKey(),
                                    ""
                            ),
                            registrationID,
                            activity
                    ).execute();

                } else {
                    Log.d(TAG, "No registration id yet...");
                }
            }
        }.execute();
    }

    private boolean registerGCM(){
        try {
            this.registrationID = gcmInstance.register(senderID);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Write String to SharedPreferences.
     * @param context Current context
     * @param key The key to store the value.
     * @param value The value to be stored String the key.
     */
    public void writeStringToSharedPref(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(
                this.sharedPreferenceIdentifier,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putString(key, value);

        prefEditor.commit();
        Log.i(CLASS_TAG +" writeStringToSharedPref", "Success. ("+key+","+value+")");
    }

    /**
     * Read String from SharedPreferences.
     * @param context Current context
     * @param key The key to fetch the value from.
     * @param defaultValue The default value to be returned.
     * @return String The fetched value.
     */
    public String readStringFromSharedPref(Context context, String key, String defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(
                this.sharedPreferenceIdentifier,
                Context.MODE_PRIVATE
        );

        String result = prefs.getString(key, defaultValue);
        Log.i(CLASS_TAG + " readStringFromSharedPref", "Success. (" + key + "," + result + ")");
        return result;
    }

    /**
     * Write int to SharedPreferences.
     * @param context Current context
     * @param key The key to store the value.
     * @param value The int value to be stored in the key.
     */
    public void writeIntToSharedPref(Context context, String key, int value) {
        SharedPreferences prefs = context.getSharedPreferences(
                this.sharedPreferenceIdentifier,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putInt(key, value);

        prefEditor.commit();
        Log.i(CLASS_TAG +" writeStringToSharedPref", "Success. ("+key+","+value+")");
    }

    /**
     * Read int from SharedPreferences.
     * @param context Current context
     * @param key The key to fetch the value from.
     * @param defaultValue The default value to be returned.
     * @return int The fetched value.
     */
    public int readIntFromSharedPref(Context context, String key, int defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(
                this.sharedPreferenceIdentifier,
                Context.MODE_PRIVATE
        );

        int result = prefs.getInt(key, defaultValue);
        Log.i(CLASS_TAG + " readStringFromSharedPref", "Success. (" + key + "," + result + ")");
        return result;
    }

    /**
     * Write boolean to SharedPreferences.
     * @param context Current context
     * @param key The key to store the value.
     * @param value The int value to be stored in the key.
     */
    public void writeBooleanToSharedPref(Context context, String key, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(
                this.sharedPreferenceIdentifier,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putBoolean(key, value);

        prefEditor.commit();
        Log.i(CLASS_TAG +" writeBooleanToSharedPref", "Success. ("+key+","+value+")");
    }

    /**
     * Read boolean from SharedPreferences.
     * @param context Current context
     * @param key The key to fetch the value from.
     * @param defaultValue The default value to be returned.
     * @return boolean The fetched value.
     */
    public boolean readBooleanFromSharedPref(Context context, String key, boolean defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(
                this.sharedPreferenceIdentifier,
                Context.MODE_PRIVATE
        );

        boolean result = prefs.getBoolean(key, defaultValue);
        Log.i(CLASS_TAG + " readStringFromSharedPref", "Success. (" + key + "," + result + ")");
        return result;
    }

}
