package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.scottyab.aescrypt.AESCrypt;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.LoginResponse;
import estrategiamovil.comerciomovil.modelo.User;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.UserLocalProfile;
import estrategiamovil.comerciomovil.tools.VolleyErrorHelper;
import estrategiamovil.comerciomovil.ui.fragments.CardsFragment;
import estrategiamovil.comerciomovil.ui.fragments.SplashFragment;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class LoginActivity extends AppCompatActivity {

    private View mLoginFormView;
    ProgressDialog progressDialog;
    private static MaterialEditText text_email;
    private static MaterialEditText text_password;
    private static TextView link_password;
    private AppCompatButton button_signup;
    private static AppCompatButton mEmailSignInButton;
    private AppCompatCheckBox checkbox_show_password;
    private LoginResponse login_response;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int METHOD_CALLBACK_LOGIN = 1;
    private static final int METHOD_CALLBACK_PASSWORD = 2;
    private static final int METHOD_CALLBACK_PASSWORD_STEP2 = 3;


    private Gson gson = new Gson();
    private final String USER_NORMAL="user";
    private final String NEW_USER="new_user";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        }

        // Set up the login form.
        checkbox_show_password = (AppCompatCheckBox) findViewById(R.id.checkbox_show_password);
        checkbox_show_password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox_show_password.isChecked()){
                    text_password.setTransformationMethod(null);
                }else{
                    text_password.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        link_password = (TextView) findViewById(R.id.link_password);
        link_password.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                recovery_Popup(v.getContext(),LoginActivity.this);
            }
        });

        button_signup = (AppCompatButton) findViewById(R.id.button_signup);
        button_signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                intent.putExtra(SignupActivity.EXTRA_TYPEUSER, Constants.flowSignupUser);
                User u = new User(); u.setEmail(text_email.getText().toString()); u.setPassword(text_password.getText().toString());
                intent.putExtra(SignupActivity.LOGIN_PARAMS,u );
                startActivityForResult(intent, Constants.REQUEST_SIGNUP);
            }
        });
        text_email = (MaterialEditText) findViewById(R.id.text_email);
        text_password = (MaterialEditText) findViewById(R.id.text_password);
        text_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = (AppCompatButton) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.begin_screen, new SplashFragment(), "SplashFragment")
                    .commit();
        }
    }

    private void recovery_Popup(Context context,Activity activity) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(activity);
        final View content = layoutInflaterAndroid.inflate(R.layout.password_recovery_layout, null);
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_template, null);
        LinearLayout fields = (LinearLayout)mView.findViewById(R.id.content);
        fields.addView(content);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(activity);
        alertDialogBuilderUserInput.setView(mView);

        final EditText text_email_recovery = (EditText) mView.findViewById(R.id.text_email_recovery);
        final ProgressBar pbLoading_update = (ProgressBar) mView.findViewById(R.id.pbLoading_update);
        final TextView layout_text = (TextView) mView.findViewById(R.id.layout_text);
        final LinearLayout layout_email = (LinearLayout) mView.findViewById(R.id.layout_email);
        final TextView layout_text_error = (TextView) mView.findViewById(R.id.layout_text_error);
        //customize title
        ((TextView)mView.findViewById(R.id.text_title)).setText(getResources().getString(R.string.promt_password_recovery_title));
        ((TextView)mView.findViewById(R.id.text_title)).setBackgroundColor(Color.parseColor(Constants.colorTitle));
        ((TextView)mView.findViewById(R.id.text_title)).setTextColor(Color.parseColor(Constants.colorBackgroundEnabled));
        AppCompatButton button = (AppCompatButton) mView.findViewById(R.id.button_password_recovery);
        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                layout_text_error.setVisibility(View.GONE);
                if (text_email_recovery.getText().toString().trim().isEmpty()) {
                    text_email_recovery.setError(getString(R.string.error_field_required));
                    valid = false;
                } else {
                    text_email_recovery.setError(null);
                    valid = true;
                }
                if (valid) {
                    pbLoading_update.setVisibility(View.VISIBLE);
                    layout_text.setVisibility(View.GONE);
                    layout_email.setVisibility(View.GONE);
                    v.setEnabled(false);
                    sendEmailPassword(text_email_recovery.getText().toString().trim(), alertDialogAndroid,mView);
                }
            }
        });

    }
    private void attemptLogin() {

        // Store values at the time of the login attempt.
        String email = text_email.getText().toString();
        String password = text_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            text_password.setError(getString(R.string.error_field_required));
            focusView = text_password;
            cancel = true;
        }
        if (!isPasswordValid(password)) {
            text_password.setError(getString(R.string.error_invalid_password));
            focusView = text_password;
            cancel = true;
        }


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            text_email.setError(getString(R.string.error_field_required));
            focusView = text_email;
            cancel = true;
        } else if (!isEmailValid(email)) {
            text_email.setError(getString(R.string.error_invalid_email));
            focusView = text_email;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            //showProgress(true);
            login(USER_NORMAL);
        }
    }
    private void sendEmailPassword(String email,AlertDialog alert,View mView){
        VolleyGetRequest(Constants.USERS+"?method=recoveryPassword&email="+email,METHOD_CALLBACK_PASSWORD,alert,mView);
    }
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return (password.length() >= 4 && password.length() < 20);
    }


    public void onLoginSuccess() {
        mEmailSignInButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed(JSONObject response) {
        mEmailSignInButton.setEnabled(true);
        if (progressDialog!=null && progressDialog.isShowing())
            progressDialog.dismiss();
        ((LinearLayout)findViewById(R.id.begin_screen)).setVisibility(View.INVISIBLE);
        try {
            Snackbar snackbar = Snackbar.make(mEmailSignInButton, response.getString("message"), Snackbar.LENGTH_LONG);
            snackbar.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void createProgressDialog(String message){
        progressDialog = new ProgressDialog(LoginActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG,"onActivityResult------->requestCode:" + resultCode);
        if (requestCode == Constants.REQUEST_SIGNUP) {//registro usuario exitoso
            if (resultCode == RESULT_OK || resultCode == Constants.SIGNUP_SUBSCRIBER_OK) {

                ((LinearLayout)findViewById(R.id.begin_screen)).setVisibility(View.VISIBLE);
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(3 * 1000);//3 segundos
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            //nothing to do after automatic login
                            login(NEW_USER);
                        }
                    }
                };
                timerThread.start();

            }else if (resultCode == RESULT_CANCELED){
                Log.d(TAG,"RESULT CANCELED!!");
            }
        }else if(requestCode == Constants.REQUEST_SIGNUP_SUBSCRIBER ){//mostrar formulario para registro de suscriptor
            Intent intent=this.getIntent();
            Bundle bundle=intent.getExtras();
            User user=(User)bundle.getSerializable("user");
            Intent intentRegister = new Intent(getApplicationContext(), SignupActivity.class);
            Bundle args = new Bundle();
            args.putSerializable("user",user);
            intentRegister.putExtras(args);
            startActivityForResult(intentRegister, Constants.REQUEST_SIGNUP_SUBSCRIBER);

        }
    }
    private void login(String typeLogin){

        String token = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.firebase_token);
        String email = "";
        String password = "";
        String encryptedPassword = "";

        if (typeLogin.equals(USER_NORMAL)) {
            createProgressDialog("Iniciando...");
            mEmailSignInButton.setEnabled(false);
            email = text_email.getText().toString().trim();
            password = text_password.getText().toString().trim();
            try {
                encryptedPassword =  AESCrypt.encrypt(Constants.seedValue,password);//aqui
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(typeLogin.equals(NEW_USER)){
            email = ApplicationPreferences.getLocalStringPreference(LoginActivity.this,Constants.localEmail);
            encryptedPassword = ApplicationPreferences.getLocalStringPreference(LoginActivity.this,Constants.localPassword);
        }

        StringBuffer url = new StringBuffer();

        url.append(Uri.parse(Constants.USERS).buildUpon().
                appendQueryParameter("method", "login").
                appendQueryParameter("email", email).
                appendQueryParameter("password", encryptedPassword).
                appendQueryParameter("token", token).
                build().toString());

        Log.d(TAG,"request login: " + url.toString());
        Log.d(TAG,"password:"+password + " Encripted: " + encryptedPassword);
        VolleyGetRequest(url.toString(), METHOD_CALLBACK_LOGIN,null,null);
    }
    public void VolleyGetRequest(String url, final int callbackFunction, final AlertDialog alert,final View mView){
        VolleySingleton.
                getInstance(LoginActivity.this).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                (String) null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Procesar la respuesta Json

                                        if (callbackFunction == METHOD_CALLBACK_LOGIN)
                                            processResponse(response);
                                        else if (callbackFunction == METHOD_CALLBACK_PASSWORD)
                                            processResponsePwd(response,alert,mView);
                                        else
                                            processResponsePwd2(response,alert,mView);

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        String message = VolleyErrorHelper.getErrorType(error, LoginActivity.this);
                                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                                        JSONObject o = new JSONObject();
                                        try {
                                            o.put("message",message);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        onLoginFailed(o);

                                    }
                                }

                        )
                );
    }
    private void startMainActivity(){
        Context context = LoginActivity.this;
        Intent intentMain =  new Intent(context, MainActivity.class);
        CardsFragment.locationChanged = true;
        context.startActivity(intentMain);
    }

    private void showErrorPasswordEmail(View mView,String error){
        (mView.findViewById(R.id.pbLoading_update)).setVisibility(View.GONE);
        (mView.findViewById(R.id.layout_text)).setVisibility(View.VISIBLE);
        (mView.findViewById(R.id.layout_text_error)).setVisibility(View.VISIBLE);
        ((TextView)mView.findViewById(R.id.layout_text_error)).setText(error);
        (mView.findViewById(R.id.layout_email)).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.button_password_recovery).setEnabled(true);

    }
    private void processResponsePwd(JSONObject response,final AlertDialog dialog,View mView){
        Log.d(TAG, response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    //make new request to send email
                    String email =  ((TextView)mView.findViewById(R.id.text_email_recovery)).getText().toString().trim();
                    JSONObject object= response.getJSONObject("result");
                    String password = object.getString("password");
                    String decrypted_pwd = AESCrypt.decrypt( Constants.seedValue,password);

                    if (password!=null){
                        VolleyGetRequest(Constants.USERS+"?method=sendRequestRecovery&email="+email+"&password="+decrypted_pwd,METHOD_CALLBACK_PASSWORD_STEP2,dialog,mView);
                    }else{
                        showErrorPasswordEmail(mView,"Email no registrado");
                    }
                    break;
                case "2":
                    showErrorPasswordEmail(mView,response.getString("message"));
                    break;
                default:
                    showErrorPasswordEmail(mView,response.getString("message"));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                showErrorPasswordEmail(mView,response.getString("message"));
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            showErrorPasswordEmail(mView,"El servicio no esta disponible, porfavor intente de nuevo.");
        }
    }

    private void processResponsePwd2(JSONObject response,final AlertDialog dialog,View mView){
        Log.d(TAG, response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    (mView.findViewById(R.id.pbLoading_update)).setVisibility(View.GONE);
                    (mView.findViewById(R.id.layout_text)).setVisibility(View.VISIBLE);
                    ((TextView)mView.findViewById(R.id.layout_text)).setText(getString(R.string.promt_password_recovery_message));
                    AppCompatButton button = (AppCompatButton) mView.findViewById(R.id.button_password_recovery);
                    button.setEnabled(true);
                    button.setText(getString(R.string.buttonOk));
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    break;
                case "2":
                    showErrorPasswordEmail(mView,response.getString("message"));
                    break;
                default:
                    showErrorPasswordEmail(mView,response.getString("message"));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                showErrorPasswordEmail(mView,response.getString("message"));
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }private void processResponse(JSONObject response) {
        Log.d(TAG, "response:" + response.toString());
        try {
            String message = response.getString("status");
            switch (message) {
                case "1"://assign values
                    JSONObject object = response.getJSONObject("login");
                    login_response = gson.fromJson(object.toString(), LoginResponse.class);
                    //save values to local database
                    UserLocalProfile.saveSessionValuesUser(LoginActivity.this, login_response);
                    ApplicationPreferences.saveLocalPreference(LoginActivity.this, Constants.localEmail, login_response.getEmail());
                    ApplicationPreferences.saveLocalPreference(LoginActivity.this,Constants.localUserId,login_response.getIdUsuario());
                    ApplicationPreferences.saveLocalPreference(LoginActivity.this, Constants.localAvatarPath, login_response.getAvatarPath());
                    ApplicationPreferences.saveLocalPreference(LoginActivity.this, Constants.localUserAdministrator, login_response.getAdministrator());
                    ApplicationPreferences.saveLocalPreference(LoginActivity.this, Constants.categoriesUser, login_response.getCategories());
                    if (login_response.getStatus().toString().equals(Constants.status_user_inactive)){
                        ApplicationPreferences.saveLocalPreference(LoginActivity.this,Constants.showTipSignupSuscriber,String.valueOf(true));
                    }

                    Log.d(TAG, "preferences guardadas por logeo: " + ApplicationPreferences.getLocalStringPreference(LoginActivity.this, Constants.localEmail) + "," +
                            ApplicationPreferences.getLocalStringPreference(LoginActivity.this, Constants.localUserId) + "," +
                            ApplicationPreferences.getLocalStringPreference(LoginActivity.this, Constants.localAvatarPath) + ", " + ApplicationPreferences.getLocalStringPreference(LoginActivity.this, Constants.localUserAdministrator));
                    if (progressDialog!=null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    //load main activity
                    startMainActivity();
                    //Finish login activity
                    this.finish();
                    break;
                case "2"://login failed
                    JSONObject o = new JSONObject();
                    o.put("message",getResources().getString(R.string.error_prompt_login));
                    onLoginFailed(o);

                    break;
            }
        }catch (JSONException e) {
            e.printStackTrace();
            onLoginFailed(response);
        }
    }

}

