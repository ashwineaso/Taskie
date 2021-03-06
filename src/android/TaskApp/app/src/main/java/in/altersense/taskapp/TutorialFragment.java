package in.altersense.taskapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.components.AltEngine;

/**
 * Created by mahesmohan on 4/30/15.
 */
public class TutorialFragment extends Fragment {

    private static final String PAGE_NUM = "pageNum";
    private static final int TOTAL_PAGES = 7;
    private Activity activity;
    private boolean isNotFirstTimeDisplay = false;

    public TutorialFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;

        // Fetch the intent
        Intent intent = this.activity.getIntent();

        // Check if an extra is present
        if(intent.hasExtra(TutorialActivity.INVOKED_FROM_SETTINGS)) {
            // set isNotFirstTimeDisplay according to from where the intent is invoked from
            this.isNotFirstTimeDisplay = intent.getExtras().getBoolean(TutorialActivity.INVOKED_FROM_SETTINGS, false);
        }

        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        int page = args.getInt(PAGE_NUM);
        switch (page) {
            case 6: {
                View fragmentView = inflater.inflate(R.layout.tut_page_final, container, false);
                // Checks if the tutorial fragment is loading for the first time
                if(this.isNotFirstTimeDisplay) {
                    // Display the continue using taskie button
                    Button btnContinueTaskie = (Button) fragmentView.findViewById(R.id.btnConitinueTaskie);
                    btnContinueTaskie.setVisibility(View.VISIBLE);
                    btnContinueTaskie.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.finish();
                        }
                    });
                } else {
                    // Display the begin taskie button
                    Button btnEndTut = (Button) fragmentView.findViewById(R.id.btnBeginTaskie);
                    btnEndTut.setVisibility(View.VISIBLE);
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
                            activity.finish();
                        }
                    });
                }
                return fragmentView;
            }
            case 5:
                return inflater.inflate(R.layout.tut_page_6, container, false);
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