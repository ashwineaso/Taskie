package in.altersense.taskapp.events;

/**
 * Created by mahesmohan on 5/30/15.
 */
public class TaskEditedEvent {

    public TaskEditedEvent(long taskId) {
        this.taskId = taskId;
    }

    public TaskEditedEvent() {
        this.taskId = 0;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    private long taskId;

}
