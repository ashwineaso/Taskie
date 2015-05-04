package in.altersense.taskapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.otto.Subscribe;

import java.util.zip.Inflater;

import in.altersense.taskapp.adapters.TutorialPageAdapter;
import in.altersense.taskapp.components.BaseApplication;
import in.altersense.taskapp.events.UpdateNowEvent;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class TutorialActivity extends FragmentActivity {

    public static final String INVOKED_FROM_SETTINGS = "invokedFromSettings";

    private TutorialPageAdapter tutorialPageAdapter;
    private ViewPager viewPager;
    private LinearLayout pagenation;
    private LinearLayout.LayoutParams smallLayoutParams, selectedLayoutParams;
    private LinearLayout[] pageImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_tutorial);

        //Set Calligraphy for font manipulation.
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Cabin-Regular-TTF.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        BaseApplication.getEventBus().register(this);

        smallLayoutParams = new LinearLayout.LayoutParams(16,16);
        smallLayoutParams.setMargins(2,2,2,2);
        selectedLayoutParams = new LinearLayout.LayoutParams(26,26);
        selectedLayoutParams.setMargins(2,2,2,2);

        viewPager = (ViewPager) findViewById(R.id.pager);
        pagenation = (LinearLayout) findViewById(R.id.pagenation);

        tutorialPageAdapter = new TutorialPageAdapter(getSupportFragmentManager(), this);

        pageImages = new LinearLayout[tutorialPageAdapter.getCount()];

        LayoutInflater inflater = getLayoutInflater();

        for(int ctr=0;ctr<tutorialPageAdapter.getCount();ctr++) {
            pageImages[ctr] = (LinearLayout) inflater.inflate(R.layout.pages, pagenation, false);
            if(ctr!=0) {
                pageImages[ctr].setLayoutParams(smallLayoutParams);
            } else {
                pageImages[ctr].setLayoutParams(selectedLayoutParams);
            }
            pagenation.addView(pageImages[ctr]);
        }

        viewPager.setAdapter(tutorialPageAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int ctr=0; ctr<tutorialPageAdapter.getCount();ctr++) {
                    if(ctr==position) {
                        pageImages[ctr].setLayoutParams(selectedLayoutParams);
                    } else {
                        pageImages[ctr].setLayoutParams(smallLayoutParams);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Subscribe
    public void onUpdateNowEvent(UpdateNowEvent event) {
        Intent showUpdateNowActivityIntent = new Intent(this, UpdateNowActivity.class);
        startActivity(showUpdateNowActivityIntent);
        this.finish();
    }

}
