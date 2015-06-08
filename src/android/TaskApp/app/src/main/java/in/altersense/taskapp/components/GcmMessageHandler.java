package in.altersense.taskapp.components;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import in.altersense.taskapp.DashboardActivity;
import in.altersense.taskapp.R;
import in.altersense.taskapp.TaskFragmentsActivity;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.events.ChangeInTasksEvent;
import in.altersense.taskapp.events.TaskDeletedEvent;
import in.altersense.taskapp.events.UserRemovedFromCollaboratorsEvent;
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
    private NotificationHandler notificationHandler = new NotificationHandler();
    private Handler handler;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * //@param Used to name the worker thread, important only for debugging.
     */
    public GcmMessageHandler() {
        super("GcmMessageHandler");
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String MessageType = gcm.getMessageType(intent);

        String messageType = gcm.getMessageType(intent);

        if(!extras.isEmpty()) {
            Log.d("GCM", "GCM Extras: "+extras.toString());
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
                        Log.d("GCM", "About to sync task");
                        SyncRequest syncRequest = new SyncRequest(task, getApplicationContext());
                        syncRequest.execute();
                        notificationHandler.createNotification(extras, getApplicationContext());
                        break;
                    case "Buzz" :
                        //Implement showing a buzz
                        tempTask = taskDbHelper.getTaskByUUID(id);
                        sendNotification(tempTask.getOwner().getName()
                                + " has reminded you to complete the task : "
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

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Event post for refreshing task list in dashboard.
                                BaseApplication.getEventBus().post(new ChangeInTasksEvent("Collaborator removed."));

                                // Event post for moving user away from TaskView if user is viewing the particular task.
                                BaseApplication.getEventBus().post(new UserRemovedFromCollaboratorsEvent(id));
                            }
                        });

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

                        this.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Event post for refreshing task list in dashboard.
                                BaseApplication.getEventBus().post(new ChangeInTasksEvent("Collaborator removed."));

                                // Event post for moving user away from TaskView if user is viewing the particular task.
                                BaseApplication.getEventBus().post(new TaskDeletedEvent(id));
                            }
                        });
                        break;
                }
            }
        }


        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    private void sendNotification(String msg, String title, boolean showTask) {

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent;
        if(showTask) {
            intent  = new Intent(this, TaskFragmentsActivity.class);
            intent.putExtra(Task.ID, tempTask.getId());
        } else {
            intent = new Intent(this, DashboardActivity.class);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setTicker(msg)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg);

        //Get the notification preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notifSoundEnable = prefs.getBoolean("notification_sounds_preference", true);
        boolean notifEnable = prefs.getBoolean("notfication_push_preference", true);

        if (notifSoundEnable) {
            Uri notification_sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(notification_sound);
        }

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setStyle(new NotificationCompat.InboxStyle());
        if (notifEnable) {
            mNotificationManager.notify(0, mBuilder.build()); //Display notiifcation only if enabled
        }

    }
}
