package in.altersense.taskapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import in.altersense.taskapp.models.User;
import in.altersense.taskapp.requests.UserLoginRequest;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class UserLoginActivity extends ActionBarActivity {

    private static final String TAG = "UserLoginActivity";
    private EditText emailET;
    private EditText passwordET;
    private Button loginButton;
    private ImageButton showPasswordButton;
    private Button regButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        Setting up calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Cabin-Medium-TTF.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.activity_user_login);

        getSupportActionBar().hide();

        // Initializing views
        this.emailET = (EditText) findViewById(R.id.loginEmailET);
        this.passwordET = (EditText) findViewById(R.id.loginPasswordET);
        this.loginButton = (Button) findViewById(R.id.loginButton);
        this.regButton = (Button) findViewById(R.id.regButton);

        this.regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startRegistrationIntent = new Intent(
                        getApplicationContext(),
                        UserRegistrationActivity.class
                );
                startActivity(startRegistrationIntent);
                finish();
            }
        });

        this.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User newUser = new User(emailET.getText().toString(), passwordET.getText().toString());
                UserLoginRequest userLoginRequest = new UserLoginRequest(
                        newUser,
                        UserLoginActivity.this
                );
                userLoginRequest.execute();
            }
        });

        this.showPasswordButton = (ImageButton) findViewById(R.id.loginShowPassButton);

        this.showPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton imageButton = (ImageButton) v;
                int inputType = passwordET.getInputType();
                if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    Log.d(TAG,"1");
                    passwordET.setInputType(
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    );
                    imageButton.setImageResource(R.drawable.ic_hide_password);
                } else {
                    Log.d(TAG,"2");
                    passwordET.setInputType(
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    );
                    imageButton.setImageResource(R.drawable.ic_action_showpassword);
                }
            }
        });

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
        getMenuInflater().inflate(R.menu.menu_user_login, menu);
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
}
