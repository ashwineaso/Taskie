package in.altersense.taskapp.events;

import in.altersense.taskapp.models.Task;

/**
 * Created by mahesmohan on 4/27/15.
 */
public class RoseToHighPriorityEvent {

    private Task task;

    public RoseToHighPriorityEvent(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
