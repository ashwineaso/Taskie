package in.altersense.taskapp.components;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by ashwineaso on 2/27/15.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    private static final String CLASS_TAG = "GcmBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(isOrderedBroadcast()) {
            Log.i(CLASS_TAG, "Received ordered broadcast.");
            //explicitly specify that the GCM message handler will handle the intent
            ComponentName componentName = new ComponentName(context.getPackageName(),GcmMessageHandler.class.getName());
            //Start the service keeping the device awake while its launching
            startWakefulService(context,(intent.setComponent(componentName)));
            setResultCode(Activity.RESULT_OK);
        } else {
            // TODO Handle unordered broadcast.
            Log.i(CLASS_TAG, "Received unordered broadcast.");
        }
    }
}
