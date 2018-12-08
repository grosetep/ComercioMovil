package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.User;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.ui.fragments.SignupSubscriberFragment;
import estrategiamovil.comerciomovil.ui.fragments.SignupUserFragment;


public class SignupActivity extends AppCompatActivity {
    public static final String EXTRA_TYPEUSER = "typeuser";
    public static final String LOGIN_PARAMS = "user";
    private static final String TAG = SignupActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSignup1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final String type = intent.getStringExtra(EXTRA_TYPEUSER);
        Bundle bundle=intent.getExtras();
        User user=(User)bundle.getSerializable(SignupActivity.LOGIN_PARAMS);

        if (user!=null)
            Log.d(TAG, user.toString());

        if (savedInstanceState == null) {
            if (type!=null && type.equals(Constants.flowSignupUser)) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.signup_container, SignupUserFragment.newInstance(user), "SignupUserFragment")
                        .commit();
            }
           else {


                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.signup_container, new SignupSubscriberFragment().newInstance(user), "SignupSubscriberFragment")
                            .commit();

            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Finish the registration screen and return to the Login activity
                setResult(Activity.RESULT_CANCELED);
                finish();
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
