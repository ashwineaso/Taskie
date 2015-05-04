package in.altersense.taskapp;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import in.altersense.taskapp.requests.SyncRequest;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class SettingsActivity extends ActionBarActivity {

    private static final String CLASS_TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Cabin-Medium-TTF.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        getFragmentManager().beginTransaction()
                .replace(R.id.settingsLL, new MainSettingsFragment()).commit();
    }

    /**
     * Calligraphy attached to new
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public static class MainSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{

        private static final String TAG = CLASS_TAG + " MainSettingsFragment";
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            final Preference syncPref = getPreferenceManager().findPreference(getString(R.string.pref_sync_everything));
            syncPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SyncRequest syncRequest = new SyncRequest(getActivity().getApplicationContext());
                    syncRequest.setWipeEverything(true);
                    syncRequest.setVisibleSync(true, getActivity());
                    syncRequest.execute();
                    return true;
                }
            });

        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            return false;
        }
    }

}
