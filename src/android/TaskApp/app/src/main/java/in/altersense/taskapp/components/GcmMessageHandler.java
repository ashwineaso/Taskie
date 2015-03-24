package in.altersense.taskapp.components;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import in.altersense.taskapp.R;
import in.altersense.taskapp.TaskActivity;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.requests.SyncRequest;

/**
 * Created by ashwineaso on 2/27/15.
 */
public class GcmMessageHandler extends IntentService {

    String datatype, id;
    private NotificationManager mNotificationManager;
    private Task tempTask;

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

                switch(datatype) {
                    case "Task" :
                        //Implement syncing of a Task
                        Task task = new Task();
                        task.setUuid(id, GcmMessageHandler.this);
                        SyncRequest syncRequest = new SyncRequest(task, getApplicationContext());
                        syncRequest.execute();
                        break;
                    case "Buzz" :
                        //Implement showing a buzz
                        TaskDbHelper taskDbHelper = new TaskDbHelper(getApplicationContext());
                        tempTask = taskDbHelper.getTaskByUUID(id);
                        sendNotification(tempTask.getOwner().getName()
                                + "has reminded you to complete the task : "
                                + tempTask.getName());

                }
            }
        }


        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    private void sendNotification(String msg) {

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent  = new Intent(this, TaskActivity.class);
        intent.putExtra(Task.ID, tempTask.getUuid());
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Reminder")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(0, mBuilder.build());

    }
}
