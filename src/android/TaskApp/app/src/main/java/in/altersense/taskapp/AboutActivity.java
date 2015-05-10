package in.altersense.taskapp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class AboutActivity extends AppCompatActivity {

    private TextView versionDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String versionText;

        try {
            versionText = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionText = "Unavilable";
        }

        this.versionDisplay = (TextView) findViewById(R.id.versionDisplayTV);
        this.versionDisplay.setText("Version: "+versionText);
    }

}
