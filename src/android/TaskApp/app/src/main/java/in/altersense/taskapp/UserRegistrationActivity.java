package in.altersense.taskapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.squareup.otto.Subscribe;

import in.altersense.taskapp.components.BaseApplication;
import in.altersense.taskapp.events.UpdateNowEvent;
import in.altersense.taskapp.models.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class UserRegistrationActivity extends ActionBarActivity {

    private EditText nameET;
    private EditText emailET;
    private EditText regPasswordET;
    private Button regButton;
    private ImageButton showPasswordButton;
    private boolean isPasswordHidden = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        Setting up calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/roboto_slab_regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        BaseApplication.getEventBus().register(this);

        setContentView(R.layout.activity_user_registration);

        getSupportActionBar().hide();

        // Initializing views
        this.nameET = (EditText) findViewById(R.id.regNameET);
        this.emailET = (EditText) findViewById(R.id.regEmailET);
        this.regPasswordET = (EditText) findViewById(R.id.regPasswordET);
        this.showPasswordButton = (ImageButton) findViewById(R.id.regShowPassButton);

        this.showPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton imageButton = (ImageButton) v;
                if(isPasswordHidden) {
                    imageButton.setImageResource(R.drawable.ic_hide_password);
                    regPasswordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isPasswordHidden = false;
                } else {
                    imageButton.setImageResource(R.drawable.ic_action_showpassword);
                    regPasswordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isPasswordHidden = true;
                }
                regPasswordET.setSelection(regPasswordET.length());
            }
        });

        this.regButton = (Button) findViewById(R.id.regButton);

        this.regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }

    private void registerUser() {
        User newUser = new User();
        newUser.registerUser(
                nameET.getText().toString(),
                emailET.getText().toString(),
                regPasswordET.getText().toString(),
                UserRegistrationActivity.this
        );
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onUpdateNowEvent(UpdateNowEvent event) {
        Intent showUpdateNowActivityIntent = new Intent(this, UpdateNowActivity.class);
        startActivity(showUpdateNowActivityIntent);
        this.finish();
    }

}
