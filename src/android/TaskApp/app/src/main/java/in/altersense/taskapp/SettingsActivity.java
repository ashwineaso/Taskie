package in.altersense.taskapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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

    private static String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        try {
            version = getPackageManager().getPackageInfo(
                    getPackageName(),
                    0
            ).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "Unavailable";
        }

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

            // Initing the prefs
            final Preference syncPref = getPreferenceManager().findPreference(getString(R.string.pref_sync_everything));
            final Preference viewWalkthrougPref = getPreferenceScreen().findPreference(getString(R.string.view_walkthrough));
            final Preference aboutTaskie = getPreferenceScreen().findPreference(getString(R.string.taskie_about));
            final Preference feedback = getPreferenceScreen().findPreference(getString(R.string.taskie_feedback));

            /*
             * Click listeners for Prefs
             */

            syncPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SyncRequest syncRequest = new SyncRequest(getActivity().getApplicationContext());
                    // set up the sync request to wipe all data.
                    syncRequest.setWipeEverything(true);
                    // set up the sync request to be a visible one
                    syncRequest.setVisibleSync(true, getActivity());
                    syncRequest.execute();
                    return true;
                }
            });

            viewWalkthrougPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent displayWalkthroughIntent = new Intent(getActivity().getApplicationContext(), TutorialActivity.class);
                    displayWalkthroughIntent.putExtra(TutorialActivity.INVOKED_FROM_SETTINGS, true);

                    startActivity(displayWalkthroughIntent);
                    return true;

                }
            });

            aboutTaskie.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent displayAboutIntent = new Intent(getActivity().getApplicationContext(), AboutActivity.class);
                    startActivity(displayAboutIntent);
                    return true;
                }
            });

            feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // this intent opens up only email clients
                    Intent sendFeedbackMailIntent = new Intent(Intent.ACTION_SEND);
                    // sets intent content type to messaages so only messaging apps may receive it.
                    sendFeedbackMailIntent.setType("message/rfc822");
                    // set to address
                    sendFeedbackMailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"someone@taskie.me"});
                    // set a subject
                    sendFeedbackMailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Taskie v"+version);
                    // set set basic text format
                    sendFeedbackMailIntent.putExtra(Intent.EXTRA_TEXT, "\n---\nSent from Taskie v"+version+
                            " on phone, "+ Build.MODEL +
                            " running Android " + Build.VERSION.RELEASE + " API version " + Build.VERSION.SDK_INT +"." );
                    // display the mail client
                    startActivity(Intent.createChooser(sendFeedbackMailIntent, "Send feedback using..."));
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
