package in.altersense.taskapp.components;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import in.altersense.taskapp.GroupActivity;

/**
 * Created by mahesmohan on 1/21/15.
 */
public class GroupPanelOnClickListener implements View.OnClickListener {

    private final Activity activity;
    private TaskGroup taskGroup;

    public GroupPanelOnClickListener(TaskGroup currentTaskGroup, Activity currentActivity) {
        this.taskGroup = currentTaskGroup;
        this.activity = currentActivity;
    }

    @Override
    public void onClick(View v) {
        GroupActivity.startGroupActivity(this.activity.getApplicationContext(), this.taskGroup);
    }
}
