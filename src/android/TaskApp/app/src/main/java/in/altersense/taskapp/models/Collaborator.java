package in.altersense.taskapp.models;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by mahesmohan on 2/17/15.
 */
public class Collaborator extends User {

    private String CLASS_TAG = "Collaborator ";

    public Collaborator(User user) {
        this.setName(user.getName());
        this.setEmail(user.getEmail());
        this.setUuid(user.getUuid());
        this.setId(user.getId());
        String TAG = CLASS_TAG+"Constructor(User)";
        Log.d(TAG, "Creates Collaborator from user: "+super.getString());
    }

    public static String TABLE_NAME = "CollaboratorList";

    public User getUser() {
        User user = new User(
                this.getUuid(),
                this.getEmail(),
                this.getName(),
                this.getId()
        );
        String TAG = CLASS_TAG+"getUser";
        Log.d(TAG, "Returns Collaborator from user: "+super.getString());
        return user;
    }

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

    @Override
    public boolean equals(Object o) {
        Collaborator colllaboratorObject = new Collaborator((User)o);
        return this.getEmail().equals(colllaboratorObject.getEmail());
    }
}
