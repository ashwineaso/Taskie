package in.altersense.taskapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;


public class TasksActivity extends ActionBarActivity {

    private LinearLayout tasksAtHandLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        this.tasksAtHandLinearLayout = (LinearLayout) findViewById(R.id.taskAtHandLinearLayout);
        LayoutInflater inflater = (LayoutInflater) this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        SubHeader
        View subHeaderLayout = inflater.inflate(R.layout.sub_header_panel, null);
        this.tasksAtHandLinearLayout.addView(subHeaderLayout);

//        Tasks as list
        for(int i=0; i<10; i++) {
            View taskPanel = inflater.inflate(R.layout.task_panel, null);
            this.tasksAtHandLinearLayout.addView(taskPanel);
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
