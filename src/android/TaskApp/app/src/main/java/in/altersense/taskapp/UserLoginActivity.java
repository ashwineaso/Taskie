package in.altersense.taskapp;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import in.altersense.taskapp.components.BaseApplication;
import in.altersense.taskapp.models.User;
import in.altersense.taskapp.requests.UserLoginRequest;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class UserLoginActivity extends ActionBarActivity implements
    GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "UserLoginActivity";
    private static final int RC_SIGN_IN = 0;

    private EditText emailET;
    private EditText passwordET;
    private Button loginButton;
    private ImageButton showPasswordButton;
    private ImageButton moreButton;
    private Button regButton;
    private GoogleApiClient googleApiClient;
    private Button googleAuthButton;


    private boolean isPasswordHidden = true;
    private boolean intentInProgress = false;
    private boolean signInClicked;


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

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope("profile"))
                .build();


        // Initializing views
        this.emailET = (EditText) findViewById(R.id.loginEmailET);
        this.passwordET = (EditText) findViewById(R.id.loginPasswordET);
        this.loginButton = (Button) findViewById(R.id.loginButton);
        this.regButton = (Button) findViewById(R.id.regButton);
        this.googleAuthButton = (Button) findViewById(R.id.googleAuthButton);
        this.moreButton = (ImageButton) findViewById(R.id.moreButton);

        this.googleAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInClicked = true;
                googleApiClient.connect();
            }
        });

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
                if(isPasswordHidden) {
                    imageButton.setImageResource(R.drawable.ic_hide_password);
                    passwordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isPasswordHidden = false;
                } else {
                    imageButton.setImageResource(R.drawable.ic_action_showpassword);
                    passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isPasswordHidden = true;
                }
                passwordET.setSelection(passwordET.length());
            }
        });

        this.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
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

    @Override
    public void onConnected(Bundle bundle) {
        signInClicked = false;
        Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
        if(currentPerson != null) {
            String personName = currentPerson.getDisplayName();
            String email = Plus.AccountApi.getAccountName(googleApiClient);
            
            UserLoginRequest userLoginRequest = new UserLoginRequest(
                    new User(
                            "",
                            email,
                            personName
                    ),
                    this,
                    true
            );
            userLoginRequest.execute();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(!intentInProgress) {
            if( signInClicked && connectionResult.hasResolution()) {
                try {
                    intentInProgress = true;
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    intentInProgress = false;
                    googleApiClient.connect();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if(responseCode == RESULT_OK) {
                signInClicked = false;
            }

            intentInProgress = false;

        }
    }

    public void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(this, this.moreButton);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_forgotPassword:
                        Intent forgotPasswordIntent = new Intent(
                                getApplicationContext(),
                                ForgotPasswordActivity.class
                        );
                        startActivity(forgotPasswordIntent);
                        break;
                }
                return true;
            }
        });
        popupMenu.inflate(R.menu.menu_user_login);
        popupMenu.show();
    }

}
