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
    private Long dateTime;
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
        dateTime = Long.valueOf(this.extras.getString("dateTime"));
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
        task = taskDbHelper.getTaskByUUID(taskUuid);
        if (AltEngine.readStringFromSharedPref(context,
                Config.SHARED_PREF_KEYS.OWNER_NAME.getKey(),
                "") == ownerName) {
            Log.d(CLASS_TAG, "Searching for task in db");
            sendNotification(message,
                    "Task Assigned");
        }
    }

    private void taskUpdateNotification() {
        message = "" + ownerName + " has updated the task : " + taskName + ".";
        //Retrieve the task from the db
        task = taskDbHelper.getTaskByUUID(taskUuid);
        //Create a new Notification object
        Notification newTaskNotification = new Notification(task, context, type, message, dateTime);
        //Call the create notification method
        taskDbHelper.createNotification(newTaskNotification);
        sendNotification(message, "Task Updated");

    }

    private void taskStatusChangeNotification() {
        int status = Integer.parseInt(this.extras.getString("status"));
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
        //If user is not device owner, send a push notification
        if(!task.isOwnedyDeviceUser(context)) {sendNotification(message, "Task Status");}

    }

    private void taskDeletion() {
        message = "" + ownerName + " has deleted the task : " + taskName + ".";
        //Retrieve the task from the db
        task = taskDbHelper.getTaskByUUID(taskUuid);
        //Create a new Notification object
        Notification newTaskNotification = new Notification(task, context, type, message, dateTime);
        //Call the create notification method
        taskDbHelper.createNotification(newTaskNotification);
        //If user is not device owner, send a push notification
        if(!task.isOwnedyDeviceUser(context)) {sendNotification(message, "Task Deleted");}

    }

    private void collAdditionNotification() {
        //Check if the string is empty or not and then perform operation
        if (!extras.getString("addedColl").equals("")) {
            //Get the collaborator names and number of unNamed collaborators
            collNames = extras.getString("addedColl");
            int unknownColl = Integer.parseInt(extras.getString("unknown"));
            if (unknownColl > 0) { collNames += "and " + unknownColl + " others";}
            //merge it all into a single message
            message = "" + ownerName + " has added " + collNames + " to the task : " + taskName;
            //Retrieve the task from the db
            task = taskDbHelper.getTaskByUUID(taskUuid);
            //Create a new Notification object
            Notification newTaskNotification = new Notification(task, context, type, message, dateTime);
            //Call the create notification method
            taskDbHelper.createNotification(newTaskNotification);
            //If user is not device owner, send a push notification
            if(!task.isOwnedyDeviceUser(context)) {sendNotification(message, "Collaborator Added");}
        }
    }

    private void collDeletionNotification() {
        //Check if the string is empty or not and then perform the operation
        if (!extras.getString("removedColl").equals("")) {
            //Get the collaborator names and number of unNamed collaborators
            collNames = extras.getString("removedColl");
            int unknownColl = Integer.parseInt(extras.getString("unknown"));
            if (unknownColl > 0) { collNames += "and " + unknownColl + " others";}
            //merge it all into a single message
            message = "" + ownerName + " has removed " + collNames + " from the task : " + taskName;
            //Retrieve the task from the db
            task = taskDbHelper.getTaskByUUID(taskUuid);
            //Create a new Notification object
            Notification newTaskNotification = new Notification(task, context, type, message, dateTime);
            //Call the create notification method
            taskDbHelper.createNotification(newTaskNotification);
            //If user is not device owner, send a push notification
            if(!task.isOwnedyDeviceUser(context)) {sendNotification(message, "Collaborator Removed");}
        }
    }

    private void sendNotification(String msg, String title) {

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent;
        try {
            taskUuid = task.getUuid();
            intent  = new Intent(context, TaskActivity.class);
            intent.putExtra(Task.ID, taskUuid);
        } catch (NullPointerException e){
            intent = new Intent(context, DashboardActivity.class);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
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
