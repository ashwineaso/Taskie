package in.altersense.taskapp.components;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import in.altersense.taskapp.R;
import in.altersense.taskapp.customviews.CustomFontTextView;

/**
 * Created by mahesmohan on 1/13/15.
 */
public class Task {
    private static final String TAG = "Task";
    private long id, deadline, ownerId;
    private String title, descr, ownerName, deadlineText;
    private boolean hasAttachment;
    public boolean isActionsDisplayed;
    private int priority;

    private View panelView, actionsView;
    private LinearLayout taskActionsPlaceHolderView;
    private LinearLayout action1, action2, action3, action4;

    /**
     * Create Task with Title, Description and Owner Name.
     * @param title Title of the task.
     * @param descr Description of the task.
     * @param ownerName Name of the owner of the task.
     * @param inflater An inflater so that the TaskPanel could be inflated.
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
        this.actionsView = createActionsView(inflater);
        this.taskActionsPlaceHolderView =
                (LinearLayout) this.panelView.findViewById(R.id.actionsPlaceHolderLinearLayout);
        this.isActionsDisplayed = false;
    }

    private View createActionsView(LayoutInflater inflater) {
        final LayoutInflater myInflater = inflater;
        View actionsPanel = inflater.inflate(R.layout.task_actions, null);

        action1 = (LinearLayout) actionsPanel.findViewById(R.id.action1);
        action2 = (LinearLayout) actionsPanel.findViewById(R.id.action2);
        action3 = (LinearLayout) actionsPanel.findViewById(R.id.action3);
        action4 = (LinearLayout) actionsPanel.findViewById(R.id.action4);

        action1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(myInflater.getContext(), "Action1", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        action2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(myInflater.getContext(), "Action2", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        action3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(myInflater.getContext(), "Action3", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        action4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(myInflater.getContext(), "Action4", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        return actionsPanel;
    }

    public void showTaskActions() {
        Log.i(TAG, "Reached showTaskActions");
        this.taskActionsPlaceHolderView.addView(this.actionsView);
        this.isActionsDisplayed = true;
    }

    public void hideTaskActions() {
//        Change the height of layout to 0 before clearing the actions panel.
        taskActionsPlaceHolderView.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
        );
//        Remove the actions panel
        taskActionsPlaceHolderView.removeAllViews();
//        Change the height of layout to WRAP_CONTENT after clearing the actions panel.
        taskActionsPlaceHolderView.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        );
//        Unset the is actions displayed flag so that the parent views can check if panel
//        is open
        this.isActionsDisplayed = false;
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