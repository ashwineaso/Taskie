package in.altersense.taskapp.events;

/**
 * Created by mahesmohan on 4/19/15.
 */
public class UserRemovedFromCollaboratorsEvent {

    private String uuid;

    public UserRemovedFromCollaboratorsEvent(String taskUUID) {
        this.uuid = taskUUID;
    }

    public String getUuid() {
        return uuid;
    }
}
