package in.altersense.taskapp.components;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

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

        datatype = extras.getString("datatype");
        id = extras.getString("id");
        Log.i("GCM", "Recieved + ( " + MessageType + " ) + datatype : " +datatype + " , id : " + id);
        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }
}
