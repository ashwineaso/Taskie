package in.altersense.taskapp.models;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.altersense.taskapp.R;

/**
 * Created by mahesmohan on 2/2/15.
 */
public class TaskGroup {
    private View groupView;
    private String uuid;
    private String title;
    private List<User> members;
    private int taskCount;
    private boolean hasUpdates;

    /**
     * Task Group table name.
     */
    public static String TABLE_NAME = "TaskGroupTable";

    /**
     * Task Group table keys.
     */
    public static enum  KEYS {
        UUID("uuid", "TEXT"),
        TITLE("title", "TEXT"),
        HAS_UPDATE("has_update", "INTEGER");

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

    public TaskGroup(
            String uuid,
            String title,
            int taskCount,
            boolean hasUpdates,
            LayoutInflater inflater
    ) {
        this.uuid = uuid;
        this.title = title;
        this.taskCount = taskCount;
        this.hasUpdates = hasUpdates;

        this.members = new ArrayList<User>();

        this.groupView = createView(inflater);
    }

    public TaskGroup(
            String title,
            int taskCount,
            boolean hasUpdates,
            LayoutInflater inflater
    ) {
        this(
                "",
                title,
                taskCount,
                hasUpdates,
                inflater
        );
    }

    public TaskGroup(String uuid, Context context) {

    }

    private View createView(LayoutInflater inflater) {
        View groupView = inflater.inflate(R.layout.groups_panel, null);
        TextView groupTitle = (TextView) groupView.findViewById(R.id.groupTitleTextView);
        LinearLayout groupTaskCountLayout = (LinearLayout) groupView.findViewById(R.id.taskCountBackground);
        TextView taskCount = (TextView) groupView.findViewById(R.id.taskCountTextView);

        groupTitle.setText(this.title);
        taskCount.setText(this.taskCount+"");

        if (this.hasUpdates) {
            groupTaskCountLayout.setBackgroundResource(R.drawable.red_circle);
            taskCount.setTextColor(Color.WHITE);
        }

        return groupView;
    }

    public View getGroupView() {
        return groupView;
    }
}
