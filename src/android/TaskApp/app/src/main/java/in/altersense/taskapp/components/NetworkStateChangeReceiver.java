package in.altersense.taskapp.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.database.UserDbHelper;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.requests.AppVersionCheckRequest;
import in.altersense.taskapp.requests.CreateTaskRequest;
import in.altersense.taskapp.requests.SyncRequest;

/**
 * Created by mahesmohan on 3/21/15.
 */
public class NetworkStateChangeReceiver extends BroadcastReceiver {

    private static final String CLASS_TAG = "NetworkStateChangeReceiver ";

    private Context context;
    private TaskDbHelper taskDbHelper;
    private UserDbHelper userDbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        this.taskDbHelper = new TaskDbHelper(context);
        this.userDbHelper = new UserDbHelper(context);

        final String TAG = CLASS_TAG+"onReceive";
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);


        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnectedOrConnecting()) {
            onNetworkAvailable();
        } else {
            onNetworkLost();
        }

    }

    /**
     * Executed when connection is lost.
     */
    private void onNetworkLost() {
        final String TAG = CLASS_TAG + "onNetworkLost";
        Log.d(TAG, "NetworkConnection Lost.");

    }

    /**
     * Executed when a connection is established.
     */
    private void onNetworkAvailable() {
        final String TAG = CLASS_TAG + "onNetworkAvailable";
        Log.d(TAG, "NetworkConnection Available.");

        // Check if update is necessary
        AppVersionCheckRequest appVersionCheckRequest = new AppVersionCheckRequest(this.context);
        appVersionCheckRequest.execute();

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

        // Checks id there are tasks to be added to thr server
        if(tasksNotAddedToServer.size()>0) {
            Log.d(TAG, "Adding newly created tasks to server.");
            Task[] tasksNotAddedToServerArray = new Task[tasksNotAddedToServer.size()];
            tasksNotAddedToServerArray = tasksNotAddedToServer.toArray(tasksNotAddedToServerArray);

            CreateTaskRequest createTaskRequest = new CreateTaskRequest(
                    tasksNotAddedToServerArray,
                    this.context
            );
            createTaskRequest.execute();
        }


        // Checking for tasks that need updation.
        if (tasksNeedUpdation.size()>0) {
            Log.d(TAG, "Updating tasks that need to be synced.");
            Task[] tasksNeedUpdationArray = new Task[tasksNeedUpdation.size()];
            tasksNeedUpdationArray = tasksNeedUpdation.toArray(tasksNeedUpdationArray);
            SyncRequest syncRequest = new SyncRequest(tasksNeedUpdationArray, this.context);
            syncRequest.execute();
        }

    }
}
