package in.altersense.taskapp.events;

/**
 * Created by mahesmohan on 4/19/15.
 */
public class ChangeInTaskEvent {

    private long taskId;

    public ChangeInTaskEvent(long taskId) {
        this.taskId = taskId;
    }

    public long getTaskId() {
        return taskId;
    }
}
