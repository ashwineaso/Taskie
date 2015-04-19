package in.altersense.taskapp.components;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.net.URI;

import in.altersense.taskapp.DashboardActivity;
import in.altersense.taskapp.R;
import in.altersense.taskapp.TaskActivity;
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.models.Notification;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.requests.SyncRequest;

/**
 * Created by ashwineaso on 2/27/15.
 */
public class GcmMessageHandler extends IntentService {

    String datatype, id;
    private NotificationManager mNotificationManager;
    private Task tempTask;
    private Intent syncCompleteBroadcastIntent;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * //@param Used to name the worker thread, important only for debugging.
     */
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String MessageType = gcm.getMessageType(intent);

        String messageType = gcm.getMessageType(intent);

        if(!extras.isEmpty()) {
            /* Filtering the message based on the message type.
             * For now We will be handling only normal gcm messages */

            if (GoogleCloudMessaging. MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                //perform the operation
                datatype = extras.getString("datatype");
                id = extras.getString("id");
                Log.i("GCM", "Recieved + ( " + MessageType + " ) + datatype : " +datatype + " , id : " + id);
                TaskDbHelper taskDbHelper = new TaskDbHelper(getApplicationContext());

                switch(datatype) {
                    case "Task" :
                        //Implement syncing of a Task
                        Task task = new Task();
                        task.setUuid(id, GcmMessageHandler.this);
                        Log.d("GCM", "Abbout tu sync task");
                        SyncRequest syncRequest = new SyncRequest(task, getApplicationContext());
                        syncRequest.execute();
                        break;
                    case "Buzz" :
                        //Implement showing a buzz
                        tempTask = taskDbHelper.getTaskByUUID(id);
                        sendNotification(tempTask.getOwner().getName()
                                + "has reminded you to complete the task : "
                                + tempTask.getName(),
                                "Reminder",
                                true);
                        break;
                    case "CollRemoved":
                        // Display a notification
                        task = taskDbHelper.getTaskByUUID(id);
                        sendNotification(task.getOwner().getName()
                                + " removed you from collaborators of the task: "
                                + task.getName(),
                                "Collaboration removed.",
                                false);
                        // Implement deletion of the task since the collaborator has been removed
                        taskDbHelper.deleteCollaborator(task);
                        Log.d("GCM", "deletion status" + taskDbHelper.delete(task));
                        this.syncCompleteBroadcastIntent = new Intent(Config.SHARED_PREF_KEYS.SYNC_IN_PROGRESS.getKey());
                        getApplicationContext().sendBroadcast(syncCompleteBroadcastIntent);
                        break;
                    case "Deleted":
                        //Delete the task from the users db
                        task = taskDbHelper.getTaskByUUID(id);
                        sendNotification(task.getOwner().getName()
                                        + " has delete the task: "
                                        + task.getName(),
                                "Task Deleted.",
                                false);
                        // Delete the task from the database
                        taskDbHelper.deleteCollaborator(task);
                        Log.d("GCM", "deletion status" + taskDbHelper.delete(task));
                        this.syncCompleteBroadcastIntent = new Intent(Config.SHARED_PREF_KEYS.SYNC_IN_PROGRESS.getKey());
                        getApplicationContext().sendBroadcast(syncCompleteBroadcastIntent);
                        break;
                }
                NotificationHandler notificationHandler = new NotificationHandler();
                notificationHandler.createNotification(extras, getApplicationContext());

            }
        }


        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    private void sendNotification(String msg, String title, boolean showTask) {

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent;
        if(showTask) {
            intent  = new Intent(this, TaskActivity.class);
            intent.putExtra(Task.ID, tempTask.getUuid());
        } else {
            intent = new Intent(this, DashboardActivity.class);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setDefaults(android.app.Notification.DEFAULT_ALL)
                        .setContentText(msg);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setStyle(new NotificationCompat.InboxStyle());
        mNotificationManager.notify(0, mBuilder.build());

    }
}
