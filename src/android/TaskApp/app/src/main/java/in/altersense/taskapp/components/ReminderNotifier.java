package in.altersense.taskapp.components;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import in.altersense.taskapp.R;
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.database.TaskDbHelper;
import in.altersense.taskapp.models.Notification;
import in.altersense.taskapp.models.RemindSyncNotification;
import in.altersense.taskapp.models.Task;

/**
 * Created by mahesmohan on 4/27/15.
 */
public class ReminderNotifier extends BroadcastReceiver {

    private TaskDbHelper taskDbHelper;
    private RemindSyncNotification rsn;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Create a taskDbHelper
        this.context = context;
        this.taskDbHelper = new TaskDbHelper(this.context);
        // Fetch RSN from task id in the intent
        long taskId = intent.getExtras().getLong(Config.REQUEST_RESPONSE_KEYS.UUID.getKey());
        this.rsn = taskDbHelper.retreiveRSN(taskId);
        if(taskDbHelper.hasPendingCollaborators(rsn.getTask())) {
            // Set next alarm
            setNextAlarm(intent);
            // display notification
            displayNotification();
        }
    }

    private void displayNotification() {
        NotificationCompat.Builder notificaion = new NotificationCompat.Builder(this.context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(Config.MESSAGES.TASK_CANT_REACH_COLLABORATOR.getMessage())
                .setContentTitle("Task \""+this.rsn.getTask().getName()+"\" have not yet reached certain collaborators.")
                .setContentText(Config.MESSAGES.TASK_CANT_REACH_COLLABORATOR.getMessage());
    }

    private void setNextAlarm(Intent intent) {
        Task task = this.rsn.getTask();
        // Check if pending collaborators are present
        if(taskDbHelper.hasPendingCollaborators(task)) {
            // Add to reminder notification table.
            RemindSyncNotification rsn = taskDbHelper.createRSN(task);
            // Calculate alarm time.
            long notifInterval = 20 * 60 * 1000;
            if(task.getDueDateTimeAsLong()!=0) {
                long timeDiff = task.getDueDateTimeAsLong() - rsn.getCreatedTime();
                notifInterval = timeDiff/3;
            }
            long nextAlarmTime = 0;
            do {
                nextAlarmTime += rsn.getCreatedTime();
            } while (nextAlarmTime>System.currentTimeMillis()
                    && nextAlarmTime<task.getDueDateTimeAsLong());
            // Setup pending intent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this.context,
                    (int) task.getId(),
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT
            );
            // Setup alarm
            AlarmManager alarmManager = (AlarmManager)
                    this.context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    nextAlarmTime,
                    pendingIntent
            );
        }
    }
}
