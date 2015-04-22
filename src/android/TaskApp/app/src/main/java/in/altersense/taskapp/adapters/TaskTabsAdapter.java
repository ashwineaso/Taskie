package in.altersense.taskapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import in.altersense.taskapp.NotificationFragment;
import in.altersense.taskapp.TaskFragment;

/**
 * Created by ashwineaso on 4/22/15.
 */
public class TaskTabsAdapter extends FragmentPagerAdapter{

    private int TOTAL_TABS = 2;

    public TaskTabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // TODO Auto-generated method stub
        switch (position) {
            case 0:
                return new TaskFragment();

            case 1:
                return new NotificationFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TOTAL_TABS;
    }
}
