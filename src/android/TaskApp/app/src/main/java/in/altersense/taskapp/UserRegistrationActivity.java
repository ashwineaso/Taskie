package in.altersense.taskapp;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import in.altersense.taskapp.models.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class UserRegistrationActivity extends ActionBarActivity {

    private EditText nameET;
    private EditText emailET;
    private EditText regPasswordET;
    private Button regButton;
    private ImageButton showPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        Setting up calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/roboto_slab_regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
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
                int inputType = regPasswordET.getInputType();
                if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    regPasswordET.setInputType(
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    );
                    imageButton.setImageResource(R.drawable.ic_hide_password);
                } else {
                    regPasswordET.setInputType(
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    );
                    imageButton.setImageResource(R.drawable.ic_action_showpassword);
                }
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
}
