package in.altersense.taskapp.components;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Switch;

import java.util.List;

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
            case "collStatusChange" : collStatusChangeNotification(); break;
            case "taskDeletion" : taskDeletion(); break;
            case "collAddition" : collAdditionNotification(); break;
            case "collDeletion" : collDeletionNotification(); break;

        }

    }

    private void newTaskNotification() {
        message = "" + ownerName + " has assigned you a new task \" " + taskName + " \".";
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
        message = "" + ownerName + " has updated the task \" " + taskName + " \".";
        //Retrieve the task from the db
        task = taskDbHelper.getTaskByUUID(taskUuid);
        //Update notification is shown only to the collaborators
        if(!task.isOwnedyDeviceUser(context)) {
            //Create a new Notification object
            Notification newTaskNotification = new Notification(task, context, type, message, dateTime);
            //Call the create notification method
            taskDbHelper.createNotification(newTaskNotification);
            sendNotification(message, "Task Updated");
        }

    }

    private void taskStatusChangeNotification() {
        int status = Integer.parseInt(this.extras.getString("status"));
        String statusAsString = Config.TASK_STATUS.INCOMPLETE.getStatusText();
        switch (status) {
            case 1 : statusAsString = Config.TASK_STATUS.INCOMPLETE.getStatusText(); break;
            case 2 : statusAsString = Config.TASK_STATUS.COMPLETE.getStatusText(); break;
            case -1 : statusAsString = Config.TASK_STATUS.DELETED.getStatusText(); break;
        }
        message = "" + ownerName + " has marked the task \"" + taskName + " \" as " + statusAsString;
        //Retrieve the task from the db
        task = taskDbHelper.getTaskByUUID(taskUuid);
        //Status change notification is shown only to the collaborators
        if(!task.isOwnedyDeviceUser(context)) {
            //Create a new Notification object
            Notification newTaskNotification = new Notification(task, context, type, message, dateTime);
            //Call the create notification method
            taskDbHelper.createNotification(newTaskNotification);
            //If user is not device owner, send a push notification
            sendNotification(message, "Task Status");
        }

    }

    private void collStatusChangeNotification() {
        int status = Integer.parseInt(this.extras.getString("status"));
        String statusAsString = Config.COLLABORATOR_STATUS.PENDING.getStatusText();
        switch (status) {
            case 1 : statusAsString = Config.COLLABORATOR_STATUS.ACCEPTED.getStatusText(); break;
            case 2 : statusAsString = Config.COLLABORATOR_STATUS.COMPLETED.getStatusText(); break;
            case -1 : statusAsString = Config.COLLABORATOR_STATUS.DECLINED.getStatusText(); break;
        }
        message = "" + ownerName + " has " + statusAsString + " the task \" " + taskName + " \".";
        //Retrieve the task from the db
        task = taskDbHelper.getTaskByUUID(taskUuid);
        //Create a new Notification object
        Notification newTaskNotification = new Notification(task, context, type, message, dateTime);
        //Call the create notification method
        taskDbHelper.createNotification(newTaskNotification);
        //If user is not device owner, send a push notification
        if(task.isOwnedyDeviceUser(context)) {sendNotification(message, "Collaborator Status");}

    }

    private void taskDeletion() {
        message = "" + ownerName + " has deleted the task \" " + taskName + " \".";
        //Retrieve the task from the db
        task = taskDbHelper.getTaskByUUID(taskUuid);
        //Task Deleted notification goes only to the collaborators
        if(!task.isOwnedyDeviceUser(context)) {
            //Create a new Notification object
            Notification newTaskNotification = new Notification(task, context, type, message, dateTime);
            //Call the create notification method
            taskDbHelper.createNotification(newTaskNotification);
            //send a push notification
            sendNotification(message, "Task Deleted");
        }

    }

    private void collAdditionNotification() {
        //Check if the string is empty or not and then perform operation
        if (!extras.getString("addedColl").equals("")) {
            //Get the collaborator names and number of unNamed collaborators
            collNames = extras.getString("addedColl");
            int unknownColl = Integer.parseInt(extras.getString("unknown"));
            if (unknownColl > 0) { collNames += "and " + unknownColl + " others";}
            //merge it all into a single message
            message = "" + ownerName + " has added " + collNames + " to the task \" " + taskName + " \".";
            //Retrieve the task from the db
            task = taskDbHelper.getTaskByUUID(taskUuid);
            try {
                //Create a new Notification object
                Notification newTaskNotification = new Notification(task, context, type, message, dateTime);
                //Call the create notification method
                taskDbHelper.createNotification(newTaskNotification);
                //If user is not device owner, send a push notification
                if(!task.isOwnedyDeviceUser(context)) {sendNotification(message, "Collaborator Added");}
            } catch (NullPointerException e) {
                Log.d(CLASS_TAG, "Task is not in db");
                //Since the task is not in db, then the user is being assigned the task
                String newmessage = "" + ownerName + " has assigned you a new task \" " + taskName + " \".";
                sendNotification(newmessage, "Task Assigned");
            }
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
            message = "" + ownerName + " has removed " + collNames + " from the task \" " + taskName + " \".";
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
        Intent intent =  new Intent(context, DashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        int notification_id = 1;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setDefaults(1);

        //Retrieve all the unseen notifications
        List<Notification> notificationList = taskDbHelper.retrieveUnseenNotiifcation();
        //Set the big notification style
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        //Set a big content title
        inboxStyle.setBigContentTitle("Taskie");

        //Move the events to the bigview
        for (Notification notification: notificationList) {
            inboxStyle.addLine(notification.getMessage());
        }

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setStyle(inboxStyle);
        mNotificationManager.notify(notification_id, mBuilder.build());

    }
}
