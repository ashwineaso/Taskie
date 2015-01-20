package in.altersense.taskapp.components;

import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by mahesmohan on 1/20/15.
 */

public class TaskPanelOnClickListener implements View.OnClickListener {

    private static final String TAG = "TaskPanelOnClickListener";
    private Task task;
    private List<Task> taskList;

    public TaskPanelOnClickListener(Task task, List<Task> taskList) {
        this.task = task;
        this.taskList = taskList;
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "OnClick on TaskPanel recieved.");
        Log.i(TAG, "isActionsDisplayed: "+task.isActionsDisplayed);
        if(!task.isActionsDisplayed) {
            showTaskActions(task);
        } else {
            hideTaskActions(task);
        }
    }

    private void hideTaskActions(Task currentTask) {
        currentTask.hideTaskActions();
    }

    private void showTaskActions(Task currentTask) {
//        Hides actions of all other tasks
        for(Task task : this.taskList) {
            if(task.isActionsDisplayed) {
                task.hideTaskActions();
            }
        }
//        Displays actions for the task
        currentTask.showTaskActions();
    }
}
