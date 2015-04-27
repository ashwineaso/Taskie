package in.altersense.taskapp.events;

import in.altersense.taskapp.models.Task;

/**
 * Created by mahesmohan on 4/27/15.
 */
public class FellFromHighPriorityEvent {

    private Task task;

    public FellFromHighPriorityEvent(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
