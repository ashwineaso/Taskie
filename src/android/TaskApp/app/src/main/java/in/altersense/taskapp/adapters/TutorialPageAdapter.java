package in.altersense.taskapp.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import in.altersense.taskapp.DashboardActivity;
import in.altersense.taskapp.R;
import in.altersense.taskapp.TutorialFragment;
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.AltEngine;

/**
 * Created by mahesmohan on 3/18/15.
 */
public class TutorialPageAdapter extends FragmentPagerAdapter {

    private static final String PAGE_NUM = "pageNum";
    private static final int TOTAL_PAGES = 7;

    private final Activity activity;

    public TutorialPageAdapter(FragmentManager supportFragmentManager, Activity activity) {
        super(supportFragmentManager);
        this.activity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE_NUM, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return TOTAL_PAGES;
    }

}
