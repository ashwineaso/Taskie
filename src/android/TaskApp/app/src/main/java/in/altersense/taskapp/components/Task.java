package in.altersense.taskapp.components;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
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
    public boolean isActionsDisplayed;
    private int priority;

    private View panelView;
    private LinearLayout taskActionsPlaceHolderView;

    /**
     * Create Task with Title, Description and Owner Name.
     * @param title Title of the task.
     * @param descr Description of the task.
     * @param ownerName Name of the owner of the task.
     * @param inflater An inflator so that the TaskPanel could be infalted.
     */
    public Task(
            String title,
            String descr,
            String ownerName,
            final LayoutInflater inflater
    ) {
        this.title = title;
        this.descr = descr;
        this.ownerName = ownerName;
        this.panelView = createView(inflater);
        this.taskActionsPlaceHolderView =
                (LinearLayout) this.panelView.findViewById(R.id.actionsPlaceHolderLinearLayout);
        this.isActionsDisplayed = false;
        this.panelView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!isActionsDisplayed) {

                } else {
                    taskActionsPlaceHolderView.removeAllViews();
                    isActionsDisplayed = false;
                }
            }
        });
    }

    public void showTaskActions(LayoutInflater inflater) {
        View actionsPanel = inflater.inflate(R.layout.task_actions, null);
        taskActionsPlaceHolderView.addView(actionsPanel);
        isActionsDisplayed = true;
    }

    public void hideTaskActions(LayoutInflater inflater) {
        taskActionsPlaceHolderView.removeAllViews();
        isActionsDisplayed = flase;
    }

    private View createView(LayoutInflater inflater) {
        View taskView = inflater.inflate(R.layout.task_panel, null);
        TextView timeStatus = (TextView) taskView.findViewById(R.id.timeStatusCustomFontTextView);
        TextView timeMeasure = (TextView) taskView.findViewById(R.id.timeMeasureCustomFontTextView);
        TextView timeUnit = (TextView) taskView.findViewById(R.id.timeUnitTextCustomFontTextView);

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

    public LinearLayout getTaskActionsPlaceHolderView() {
        return taskActionsPlaceHolderView;
    }

}