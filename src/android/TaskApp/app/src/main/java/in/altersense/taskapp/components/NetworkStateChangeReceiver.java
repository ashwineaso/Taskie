package in.altersense.taskapp.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by mahesmohan on 3/21/15.
 */
public class NetworkStateChangeReceiver extends BroadcastReceiver {

    private static final String CLASS_TAG = "NetworkStateChangeReceiver ";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String TAG = CLASS_TAG+"onReceive";
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable() || mobile.isAvailable()) {
            // Do something

            onNetworkAvailable();

        }
    }

    public void onNetworkAvailable() {
        final String TAG = CLASS_TAG + "onNetworkAvailable";
        Log.d(TAG, "NetworkConnection Available.");
    }
}
