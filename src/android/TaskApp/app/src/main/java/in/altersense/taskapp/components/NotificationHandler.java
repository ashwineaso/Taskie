package in.altersense.taskapp.components;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Switch;

import in.altersense.taskapp.DashboardActivity;
import in.altersense.taskapp.R;
import in.altersense.taskapp.TaskActivity;
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.models.Notification;
import in.altersense.taskapp.models.Task;

/**
 * Created by ashwineaso on 4/18/15.
 */
public class NotificationHandler {

    private static final String CLASS_TAG = "NotificationHandler";

    String type;
    private Bundle extras;
    private String ownerName, taskName, taskUuid;
    private Integer dateTime;
    private TaskDbHelper taskDbHelper;
    private String message;
    private Task task;
    private Context context;
    private String collNames;
    private NotificationManager mNotificationManager;

    public void createNotification(Bundle extras, Context context) {

        String TAG = CLASS_TAG + " createNotification()";
        taskDbHelper = new TaskDbHelper(context);
        this.context = context;
        this.extras = extras;
        type = this.extras.getString("type");
        Log.d(TAG, "Type of notification : " + type);
        taskUuid = this.extras.getString("id");
        ownerName = this.extras.getString("ownerName");
        taskName = this.extras.getString("taskName");
        dateTime = this.extras.getInt("dateTime");
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
        message = "" + ownerName + " has assigned you a new task : " + taskName + ".";
        //Retrieve the task from the db
        task = taskDbHelper.getTaskByUUID(taskUuid);
        //Create a new Notification object
        Notification newTaskNotification = new Notification(task, context, type, message, dateTime);
        //Call the create notification method
        taskDbHelper.createNotification(newTaskNotification);
    }

    private void taskUpdateNotification() {
        message = "" + ownerName + " has updated the task : " + taskName + ".";
        //Retrieve the task from the db
        task = taskDbHelper.getTaskByUUID(taskUuid);
        //Create a new Notification object
        Notification newTaskNotification = new Notification(task, context, type, message, dateTime);
        //Call the create notification method
        taskDbHelper.createNotification(newTaskNotification);

    }

    private void taskStatusChangeNotification() {
        int status = this.extras.getInt("status");
        String statusAsString = Config.TASK_STATUS.INCOMPLETE.getStatusText();
        switch (status) {
            case 1 : statusAsString = Config.TASK_STATUS.INCOMPLETE.getStatusText(); break;
            case 2 : statusAsString = Config.TASK_STATUS.COMPLETE.getStatusText(); break;
            case -1 : statusAsString = Config.TASK_STATUS.DELETED.getStatusText(); break;
        }
        message = "" + ownerName + " has marked the task : " + taskName + " as " + statusAsString;
        //Retrieve the task from the db
        task = taskDbHelper.getTaskByUUID(taskUuid);
        //Create a new Notification object
        Notification newTaskNotification = new Notification(task, context, type, message, dateTime);
        //Call the create notification method
        taskDbHelper.createNotification(newTaskNotification);

    }

    private void taskDeletion() {
        message = "" + ownerName + " has deleted the task : " + taskName + ".";
        //Retrieve the task from the db
        task = taskDbHelper.getTaskByUUID(taskUuid);
        //Create a new Notification object
        Notification newTaskNotification = new Notification(task, context, type, message, dateTime);
        //Call the create notification method
        taskDbHelper.createNotification(newTaskNotification);

    }

    private void collAdditionNotification() {
        String[] addedCollList = extras.getStringArray("removedColl");
        int unknownColl = extras.getInt("unknown");
        for (String s: addedCollList) { collNames += "" + s + "others, "; }
        if (unknownColl > 0) { collNames += "and " + unknownColl + " collaborators";}
        message = "" + ownerName + " has added " + collNames + " to the task : " + taskName;
        //Retrieve the task from the db
        task = taskDbHelper.getTaskByUUID(taskUuid);
        //Create a new Notification object
        Notification newTaskNotification = new Notification(task, context, type, message, dateTime);
        //Call the create notification method
        taskDbHelper.createNotification(newTaskNotification);

    }

    private void collDeletionNotification() {
        String[] addedCollList = extras.getStringArray("removedColl");
        int unknownColl = extras.getInt("unknown");
        for (String s: addedCollList) { collNames += "" + s + "others, "; }
        if (unknownColl > 0) { collNames += "and " + unknownColl + " collaborators";}
        message = "" + ownerName + " has removed " + collNames + " from the task : " + taskName;
        //Retrieve the task from the db
        task = taskDbHelper.getTaskByUUID(taskUuid);
        //Create a new Notification object
        Notification newTaskNotification = new Notification(task, context, type, message, dateTime);
        //Call the create notification method
        taskDbHelper.createNotification(newTaskNotification);

    }
}
