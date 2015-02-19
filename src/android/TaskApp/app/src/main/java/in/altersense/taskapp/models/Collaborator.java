package in.altersense.taskapp.models;

import java.util.ArrayList;

/**
 * Created by mahesmohan on 2/17/15.
 */
public class Collaborator extends User {

    public Collaborator(User user) {
        this.setName(user.getName());
        this.setEmail(user.getEmail());
        this.setUuid(user.getUuid());
    }

    public static String TABLE_NAME = "CollaboratorList";
    public static enum KEYS {
        TASK_ROWID("task_rowid", "INTEGER"),
        TASK_UUID("task_uuid", "TEXT"),
        USER_ROWID("user_rowid", "INTEGER"),
        USER_UUID("user_uuid", "TEXT"),
        STATUS("status", "INTEGER");

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

    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static String[] getAllColumns() {
        ArrayList<String> columnList = new ArrayList<String>();
        for(KEYS key:KEYS.values()) {
            columnList.add(key.getName());
        }
        columnList.add("ROWID");
        String[] columns = new String[columnList.size()];
        columns = columnList.toArray(columns);
        return columns;
    }
}
