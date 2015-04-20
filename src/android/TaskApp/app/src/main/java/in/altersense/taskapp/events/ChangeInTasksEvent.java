package in.altersense.taskapp.events;

/**
 * Created by mahesmohan on 4/18/15.
 */
public class ChangeInTasksEvent {
    private String message;

    public ChangeInTasksEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
