package in.altersense.taskapp.events;

/**
 * Created by mahesmohan on 4/19/15.
 */
public class TaskDeletedEvent {

    private String uuid;

    public TaskDeletedEvent(String taskUUID) {
        this.uuid = taskUUID;
    }

    public String getUuid() {
        return uuid;
    }

}
