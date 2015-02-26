package in.altersense.taskapp.components;

import android.app.Activity;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.concurrent.Callable;

import in.altersense.taskapp.requests.PushGCMIDRequest;

/**
 * Created by mahesmohan on 2/26/15.
 */
public class CallableGCMPushRequest implements Callable<Void>{

    private String ownerId;

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    private String gcmId;
    private Activity activity;

    public CallableGCMPushRequest(String ownerId, Activity activity) {
        this.ownerId = ownerId;
        this.activity = activity;
    }

    @Override
    public Void call() throws Exception {
        PushGCMIDRequest pushGCMIDRequest = new PushGCMIDRequest(
                this.ownerId,
                this.gcmId,
                this.activity
        );
        pushGCMIDRequest.execute();
        return null;
    }
}
