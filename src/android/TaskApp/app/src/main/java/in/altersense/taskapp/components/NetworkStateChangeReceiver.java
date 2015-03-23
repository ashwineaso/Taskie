package in.altersense.taskapp.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.database.CollaboratorDbHelper;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.database.UserDbHelper;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.requests.SyncRequest;

/**
 * Created by mahesmohan on 3/21/15.
 */
public class NetworkStateChangeReceiver extends BroadcastReceiver {

    private static final String CLASS_TAG = "NetworkStateChangeReceiver ";

    private Context context;
    private TaskDbHelper taskDbHelper;
    private CollaboratorDbHelper collaboratorDbHelper;
    private UserDbHelper userDbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        this.taskDbHelper = new TaskDbHelper(context);
        this.collaboratorDbHelper = new CollaboratorDbHelper(context);
        this.userDbHelper = new UserDbHelper(context);

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

        } else {

            onNetworkLost();

        }
    }

    /**
     * Executed when connection is lost.
     */
    private void onNetworkLost() {

    }

    /**
     * Executed when a connection is established.
     */
    private void onNetworkAvailable() {
        final String TAG = CLASS_TAG + "onNetworkAvailable";
        Log.d(TAG, "NetworkConnection Available.");

        List<Task> unSyncedTasks = this.taskDbHelper.retrieveAllUnsyncedTask();
        List<Task> tasksNotAddedToServer = new ArrayList<>();
        List<Task> tasksNeedUpdation = new ArrayList<>();
        for(Task task:unSyncedTasks) {
            if(task.getUuid().length()==0) {
                tasksNotAddedToServer.add(task);
            } else {
                tasksNeedUpdation.add(task);
            }
        }

    }
}
