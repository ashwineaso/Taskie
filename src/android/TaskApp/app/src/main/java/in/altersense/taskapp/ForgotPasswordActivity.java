package in.altersense.taskapp;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import in.altersense.taskapp.requests.ResetPasswordRequest;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ForgotPasswordActivity extends ActionBarActivity {

    private EditText emailET;
    private Button resetPasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //        Setting up calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Cabin-Medium-TTF.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        setContentView(R.layout.activity_forgot_password);

        this.emailET = (EditText) findViewById(R.id.emailET);
        this.resetPasswordBtn = (Button) findViewById(R.id.resetPasswordButton);

        this.resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(
                        emailET.getText().toString(),
                        getApplicationContext()
                );
                resetPasswordRequest.execute();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
