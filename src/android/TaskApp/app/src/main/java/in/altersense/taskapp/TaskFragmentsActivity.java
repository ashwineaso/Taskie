package in.altersense.taskapp;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

import com.squareup.otto.Subscribe;

import in.altersense.taskapp.adapters.TaskTabsAdapter;
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.BaseApplication;
import in.altersense.taskapp.events.BackPressedEvent;
import in.altersense.taskapp.events.FinishingTaskActivityEvent;
import in.altersense.taskapp.events.TaskEditedEvent;
import in.altersense.taskapp.events.UpdateNowEvent;
import in.altersense.taskapp.models.Task;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class TaskFragmentsActivity extends AppCompatActivity implements ActionBar.TabListener {

    private static final String CLASS_TAG = "TaskFragmentsActivity";

    private ViewPager tabsViewPager;
    private ActionBar actionBar;
    private MenuItem editViewToggle;
    private boolean taskUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_fragments);

        //        Setting up calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Cabin-Medium-TTF.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        BaseApplication.getEventBus().register(this);

        tabsViewPager = (ViewPager) findViewById(R.id.tabspager);
        TaskTabsAdapter taskTabsAdapter = new TaskTabsAdapter(getSupportFragmentManager());

        tabsViewPager.setAdapter(taskTabsAdapter);
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab TaskViewTab = actionBar.newTab().setText("Details").setTabListener(this);
        ActionBar.Tab NotificationTab = actionBar.newTab().setText("Notifications").setTabListener(this);

        actionBar.addTab(TaskViewTab);
        actionBar.addTab(NotificationTab);

        tabsViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * Calligraphy attached to new
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        tabsViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Subscribe
    public void onUpdateNowEvent(UpdateNowEvent event) {
        Intent showUpdateNowActivityIntent = new Intent(this, UpdateNowActivity.class);
        startActivity(showUpdateNowActivityIntent);
        Intent resultIntent = new Intent();
        resultIntent.putExtra(DashboardActivity.TASK_UPDATED, false);
        setResult(DashboardActivity.TASK_VIEW_REQUEST_CODE, resultIntent);
        this.finish();
    }

    /**
     * When a task is edited in TaskFramgent
     * @param event TaskEditedEvent with or without taskId
     */
    @Subscribe
    public void onTaskEditedEvent(TaskEditedEvent event) {
        Log.i(CLASS_TAG, "TaskEditedEvent Recieved from Fragment");
        this.taskUpdated = true;
    }

    @Subscribe
    public void onFinishingTaskActivityEvent(FinishingTaskActivityEvent event) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(DashboardActivity.TASK_UPDATED, this.taskUpdated);
        setResult(DashboardActivity.TASK_VIEW_REQUEST_CODE, resultIntent);
        Log.i(CLASS_TAG, "RESULT set in onDestroy");
        finish();
    }

    @Override
    public void onBackPressed() {
        BaseApplication.getEventBus().post(new BackPressedEvent());
    }

}
