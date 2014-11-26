package in.altersense.taskapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class TasksAtHand extends Activity {
	
	private LinearLayout tasksAtHand;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tasks_at_hand);
		
		this.tasksAtHand = (LinearLayout) findViewById(R.id.taskAtHandLinearLayout);
		LayoutInflater inflater = (LayoutInflater) this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View subHeader;
		View taskPanel;
		
		subHeader = inflater.inflate(R.layout.sub_header_panel, null);
		this.tasksAtHand.addView(subHeader);
		
//		Deploying multiple tasks.
		for(int i = 0; i<3; i++) {
			taskPanel = inflater.inflate(R.layout.task_panel, null);
			this.tasksAtHand.addView(taskPanel);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tasks_at_hand, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
