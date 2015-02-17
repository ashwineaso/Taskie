package in.altersense.taskapp.models;

/**
 * Created by mahesmohan on 2/17/15.
 */
public class Collaborator extends User {
    public static String TABLE_NAME = "CollaboratorList";
    public static enum KEYS {
        TASK_ROWID("task_rowid", "INTEGER"),
        TASK_UUID("task_uuid", "TEXT"),
        USER_ROWID("user_rowid", "INTEGER"),
        USER_UUID("user_uuid", "TEXT");

        public String getName() {
            return name;
        }

        private final String name;

        public String getType() {
            return type;
        }

        private final String type;

        private KEYS(
                String name,
                String type
        ) {
            this.name = name;
            this.type = type;
        }
    }
}
