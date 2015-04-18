package in.altersense.taskapp.components;

import android.widget.Switch;

/**
 * Created by ashwineaso on 4/18/15.
 */
public class NotificationHandler {

    String type;

    public void createNotification() {

        //Choose create function based on the notification type
        switch (type) {

            case "newTask" : newTaskNotification(); break;
            case "taskUpdate" : taskUpdateNotification(); break;
            case "taskStatusChange" : taskStatusChangeNotification(); break;
            case "taskDeletion" : taskDeletion(); break;
            case "collAddition" : collAdditionNotification(); break;
            case "collDeletion" : collDeletionNotification(); break;

        }

    }

    private void newTaskNotification() {

    }

    private void taskUpdateNotification() {

    }

    private void taskStatusChangeNotification() {

    }

    private void taskDeletion() {

    }

    private void collAdditionNotification() {

    }

    private void collDeletionNotification() {

    }
}
