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
import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.AltEngine;

/**
 * Created by mahesmohan on 3/18/15.
 */
public class TutorialPageAdapter extends FragmentPagerAdapter {

    private static final String PAGE_NUM = "pageNum";
    private static final int TOTAL_PAGES = 6;
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

    class TutorialFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Bundle args = getArguments();
            int page = args.getInt(PAGE_NUM);
            switch (page) {
                case 5: {
                    View fragmentView = inflater.inflate(R.layout.tut_page_final, container, false);
                    Button btnEndTut = (Button) fragmentView.findViewById(R.id.btnEndTut);
                    btnEndTut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AltEngine.writeBooleanToSharedPref(
                                    activity.getApplicationContext(),
                                    Config.SHARED_PREF_KEYS.DISPLAY_TUTORIALS.getKey(),
                                    false
                            );
                            Intent intent = new Intent(activity, DashboardActivity.class);
                            activity.startActivity(intent);
                        }
                    });
                    return fragmentView;
                }
                case 4:
                    return inflater.inflate(R.layout.tut_page_5, container, false);
                case 3:
                    return inflater.inflate(R.layout.tut_page_4, container, false);
                case 2:
                    return inflater.inflate(R.layout.tut_page_3, container, false);
                case 1:
                    return inflater.inflate(R.layout.tut_page_2, container, false);
                case 0:
                default:
                    return inflater.inflate(R.layout.tut_page_1, container, false);
            }
        }

    }
}
