package in.altersense.taskapp.components;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.altersense.taskapp.common.Config;

/**
 * Created by mahesmohan on 1/29/15.
 */
public class AltEngine {

    private static String CLASS_TAG = "AltEngine ";

    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * SharedPreference Name for the app.
     */
    public static String SHARED_PREFERENCE = "SharedPref";

    /**
     * Write String to SharedPreferences.
     * @param context Current context
     * @param key The key to store the value.
     * @param value The value to be stored String the key.
     */
    public static void writeStringToSharedPref(Context context, String key, String value) {
        SharedPreferences radioRemotePrefs = context.getSharedPreferences(
                SHARED_PREFERENCE,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor prefEditor = radioRemotePrefs.edit();
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
    public static String readStringFromSharedPref(Context context, String key, String defaultValue) {
        SharedPreferences radioRemotePrefs = context.getSharedPreferences(
                SHARED_PREFERENCE,
                Context.MODE_PRIVATE
        );

        String result = radioRemotePrefs.getString(key, defaultValue);
        Log.i(CLASS_TAG + " readStringFromSharedPref", "Success. (" + key + "," + result + ")");
        return result;
    }

    /**
     * Write String to SharedPreferences.
     * @param context Current context
     * @param key The key to store the value.
     * @param value The value to be stored Boolean the key.
     */
    public static void writeBooleanToSharedPref(Context context, String key, Boolean value) {
        SharedPreferences radioRemotePrefs = context.getSharedPreferences(
                SHARED_PREFERENCE,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor prefEditor = radioRemotePrefs.edit();
        prefEditor.putBoolean(key, value);

        prefEditor.commit();
        Log.i(CLASS_TAG +" writeStringToSharedPref", "Success. ("+key+","+value+")");
    }

    /**
     * Read String from SharedPreferences.
     * @param context Current context
     * @param key The key to fetch the value from.
     * @param defaultValue The default value to be returned.
     * @return Boolean The fetched value.
     */
    public static boolean readBooleanFromSharedPref(Context context, String key, Boolean defaultValue) {
        SharedPreferences radioRemotePrefs = context.getSharedPreferences(
                SHARED_PREFERENCE,
                Context.MODE_PRIVATE
        );

        boolean result = radioRemotePrefs.getBoolean(key, defaultValue);
        Log.i(CLASS_TAG +" readStringFromSharedPref", "Success. ("+key+","+result+")");
        return result;
    }

    /**
     * Forms a URL for the API.
     * @param api API to be called.
     * @return URL
     */
    public static String formURL(String api) {
        return "http://"+Config.SERVER_ADDRESS+"/"+api;
    }

    /**
     * Validates email with regular expression.
     * @param email Email String
     * @return Boolean validation result of email.
     */
    public static boolean verifyEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
