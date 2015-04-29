package in.altersense.taskapp.events;

/**
 * Created by mahesmohan on 4/30/15.
 */
public class UpdateNowEvent {

    private int appVersionCode;

    public UpdateNowEvent(int appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public int getAppVersionCode() {
        return appVersionCode;
    }
}
