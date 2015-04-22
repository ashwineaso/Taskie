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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import in.altersense.taskapp.adapters.TaskTabsAdapter;
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.models.Task;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class TaskFragmentsActivity extends ActionBarActivity implements ActionBar.TabListener {

    private ViewPager tabsViewPager;
    private ActionBar actionBar;

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

        tabsViewPager = (ViewPager) findViewById(R.id.tabspager);
        TaskTabsAdapter taskTabsAdapter = new TaskTabsAdapter(getSupportFragmentManager());

        tabsViewPager.setAdapter(taskTabsAdapter);
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab TaskViewTab = actionBar.newTab().setText("Task Details").setTabListener(this);
        ActionBar.Tab NotificationTab = actionBar.newTab().setText("Notifications").setTabListener(this);

        actionBar.addTab(TaskViewTab);
        actionBar.addTab(NotificationTab);

        tabsViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageSelected(int position) {

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        MenuInflater menuInflater = getActivity().getMenuInflater();
//        menuInflater.inflate(R.menu.menu_task, menu);
//        this.editViewToggle = menu.findItem(R.id.action_toggle_view_edit);
//        if (!task.isOwnedyDeviceUser(getActivity().getApplicationContext())) {
//            this.editViewToggle.setVisible(false);
//        }
//        return super.onCreateOptionsMenu(menu, menuInflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        switch (id) {
//            case R.id.action_toggle_view_edit:
//                if(isEditMode) {
//                    editViewToggle.setIcon(R.drawable.ic_edit_white);
//                    editViewToggle.setTitle("Edit");
//                    setUpViewMode();
//
//                } else {
//                    editViewToggle.setIcon(R.drawable.ic_save_white_36dp);
//                    editViewToggle.setTitle("Save");
//                    setUpEditMode();
//                }
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
