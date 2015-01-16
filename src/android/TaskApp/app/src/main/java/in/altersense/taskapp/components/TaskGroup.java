package in.altersense.taskapp.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.altersense.taskapp.R;

/**
 * Created by mahesmohan on 1/16/15.
 */
public class TaskGroup {
    private final View groupsView;
    private String id;
    private String title;
    private int taskCount;
    private boolean hasUnread;

    public TaskGroup(
            String title,
            int taskCount,
            boolean hasUnread,
            LayoutInflater inflater
    ) {
        this.title = title;
        this.taskCount = taskCount;
        this.hasUnread = hasUnread;

        this.groupsView = this.createGroupsPane(inflater);
    }

    private View createGroupsPane(LayoutInflater inflater) {
        View groupView = inflater.inflate(R.layout.groups_panel, null);
        ImageView groupColorImage = (ImageView) groupView.findViewById(R.id.groupColor);
        TextView groupTitle = (TextView) groupView.findViewById(R.id.groupTitleTextView);
        LinearLayout groupTaskCountLayout = (LinearLayout) groupView.findViewById(R.id.taskCountBackground);
        TextView taskCount = (TextView) groupView.findViewById(R.id.taskCountTextView);

        groupTitle.setText(this.title);
        taskCount.setText(this.taskCount+"");

        return groupView;
    }

    public View getGroupView() {
        return groupsView;
    }
}
