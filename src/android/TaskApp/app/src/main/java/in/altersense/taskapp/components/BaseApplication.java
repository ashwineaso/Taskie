package in.altersense.taskapp.components;

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by mahesmohan on 4/18/15.
 */
public class BaseApplication extends Application {

    private static Bus eventBus;

    public static Bus getEventBus() {
        return eventBus;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        eventBus = new Bus(ThreadEnforcer.ANY);
    }
}
