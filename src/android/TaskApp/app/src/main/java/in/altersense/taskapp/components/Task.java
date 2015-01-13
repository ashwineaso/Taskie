package in.altersense.taskapp.components;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import in.altersense.taskapp.R;
import in.altersense.taskapp.customviews.CustomFontTextView;

/**
 * Created by mahesmohan on 1/13/15.
 */
public class Task {
    private long id, deadline, ownerId;
    private String title, descr, ownerName, deadlineText;
    private boolean hasAttachment;
    private int priority;

    private View panelView;

    public Task(
            String title,
            String descr,
            String ownerName,
            LayoutInflater inflater
    ) {
        this.title = title;
        this.descr = descr;
        this.ownerName = ownerName;
        this.panelView = createView(inflater);
    }

    private View createView(LayoutInflater inflater) {
        View taskView = inflater.inflate(R.layout.task_panel, null);
        CustomFontTextView timeStatus = (CustomFontTextView) taskView.findViewById(R.id.timeStatusCustomFontTextView);
        CustomFontTextView timeMeasure = (CustomFontTextView) taskView.findViewById(R.id.timeMeasureCustomFontTextView);
        CustomFontTextView timeUnit = (CustomFontTextView) taskView.findViewById(R.id.timeUnitTextCustomFontTextView);

        TextView taskTitle = (TextView) taskView.findViewById(R.id.taskTitleTextView);
        TextView taskDescr = (TextView) taskView.findViewById(R.id.taskDescriptionTextView);
        TextView taskOwner = (TextView) taskView.findViewById(R.id.taskOwnerTextView);

        taskTitle.setText(this.title);
        taskDescr.setText(this.descr);
        taskOwner.setText(this.ownerName);

        return taskView;
    }

    public View getPanelView() {
        return panelView;
    }
}
