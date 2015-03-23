package in.altersense.taskapp.components;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import in.altersense.taskapp.models.Task;
import in.altersense.taskapp.requests.SyncRequest;

/**
 * Created by ashwineaso on 2/27/15.
 */
public class GcmMessageHandler extends IntentService {

    String datatype, id;

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
                        task.setUuid(id, getApplicationContext());
                        SyncRequest syncRequest = new SyncRequest();
                }
            }
        }


        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }
}
