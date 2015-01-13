package in.altersense.taskapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.altersense.taskapp.components.Task;


public class TasksActivity extends ActionBarActivity {

    private LinearLayout mainStageLinearLayout;  // For handling the main content area.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);


        this.mainStageLinearLayout = (LinearLayout) findViewById(R.id.mainStageLinearLayout);
//        Layout inflater for inflating the layouts.
        LayoutInflater inflater = (LayoutInflater) this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        Inflate multiple list collections.
        for(int listCollectionCtr = 0; listCollectionCtr<3; listCollectionCtr++) {
            View taskCollection = inflater.inflate(R.layout.tasks_collection, null);
//            Set ids for each list collections for identifying.
            taskCollection.setId(listCollectionCtr + 1);
//            Title each collections appropriately.
            TextView taskCollectionTitle = (TextView) taskCollection.findViewById(R.id.taskCollectionTitleTextView);
            taskCollectionTitle.setText("List Collection "+listCollectionCtr+1);
//            Inflate multiple tasks in each collections.
            LinearLayout taskListLinearLayout = (LinearLayout) taskCollection.findViewById(R.id.taskListLinearLayout);
            for(int i=0; i<3; i++) {
                Task task = new Task(
                        "Boil Eggs",
                        "Some kinda description goes here, I dont care actually. You can set it to anything.",
                        "Mahesh Mohan",
                        this.getLayoutInflater()
                );
                taskListLinearLayout.addView(task.getPanelView());
            }
            this.mainStageLinearLayout.addView(taskCollection);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
