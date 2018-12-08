package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.scottyab.aescrypt.AESCrypt;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class ChangePasswordActivity extends AppCompatActivity {
    private static final String TAG = ChangePasswordActivity.class.getSimpleName();
    private MaterialEditText text_new_password;
    private MaterialEditText text_new_password_confirm;
    private AppCompatButton button_change;
    private AppCompatCheckBox checkbox_show_password;
    private ProgressDialog progressDialog;
    private String encryptedPassword = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_password);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initGUI();
        assignActions();
    }
    private void initGUI(){
        text_new_password = (MaterialEditText) findViewById(R.id.text_new_password);
        text_new_password_confirm = (MaterialEditText) findViewById(R.id.text_new_password_confirm);
        checkbox_show_password = (AppCompatCheckBox) findViewById(R.id.checkbox_show_password);
        button_change = (AppCompatButton) findViewById(R.id.button_change);
    }
    private void assignActions(){
        checkbox_show_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox_show_password.isChecked()){
                    text_new_password.setTransformationMethod(null);
                    text_new_password_confirm.setTransformationMethod(null);
                }else{
                    text_new_password.setTransformationMethod(new PasswordTransformationMethod());
                    text_new_password_confirm.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });
        button_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                String p1 = text_new_password.getText().toString();
                String p1_confirm = text_new_password_confirm.getText().toString();

                if (p1.isEmpty() || p1.length() <= 3 || p1.length()>20) {
                    text_new_password.setError(getString(R.string.error_invalid_password));
                    valid = false;
                } else {
                    text_new_password.setError(null);
                }
                if (p1_confirm.isEmpty() || p1_confirm.length() <= 3 || p1_confirm.length()>20) {
                    text_new_password_confirm.setError(getString(R.string.error_invalid_password));
                    valid = false;
                } else {
                    text_new_password_confirm.setError(null);
                }
                if (!p1.equals(p1_confirm)){
                    text_new_password_confirm.setError(getString(R.string.error_invalid_password_not_equal));
                    valid = false;
                }else{
                    text_new_password_confirm.setError(null);
                }
                if (valid){
                    updatePassword();
                }
            }
        });
    }
    private void updatePassword(){
        String idUser = ApplicationPreferences.getLocalStringPreference(getApplicationContext(), Constants.localUserId);

        initProcess(true);
        HashMap<String,String> params = new HashMap<>();
        params.put("method","updatePassword");
        params.put("id_user",idUser);
        try {
            encryptedPassword = AESCrypt.encrypt(Constants.seedValue,text_new_password.getText().toString().trim());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        params.put("new_password",encryptedPassword);
        VolleyPostRequest(Constants.USERS,params);

    }
    private void initProcess(boolean flag){
        if (flag)
            createProgressDialog(getString(R.string.promtp_payment_processing));
        else
            closeProgressDialog();
    }
    private void createProgressDialog(String message){
        progressDialog = new ProgressDialog(ChangePasswordActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();
    }
    public void VolleyPostRequest(String url, final HashMap<String, String> args){
        JSONObject jobject = new JSONObject(args);
        Log.d(TAG,"VolleyPostRequest:"+ jobject.toString());
        JsonObjectRequest request =new JsonObjectRequest(
                Request.Method.POST,
                url,
                jobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            processResponseUpdate(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ShowConfirmations.showConfirmationMessage(getString(R.string.generic_error),ChangePasswordActivity.this);
                        initProcess(false);
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Accept", "application/json");
                return headers;
            }


            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8" + getParamsEncoding();
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
    private void processResponseUpdate(JSONObject response) {
        Log.d(TAG, "Result:" + response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    initProcess(false);
                    ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.localPassword,encryptedPassword);
                    hideKeyBoard(text_new_password);
                    setResult(Activity.RESULT_OK, null);
                    finish();
                    break;
                case "2":
                    String object_error = response.getString("message");
                    ShowConfirmations.showConfirmationMessage(object_error, this);
                    initProcess(false);
                    break;

                default:
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            initProcess(false);
            ShowConfirmations.showConfirmationMessage(getString(R.string.generic_error), this);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Finish the registration screen and return to the Login activity
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void hideKeyBoard(View v){
        //hide keyboord
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                v.getWindowToken(), 0);
    }
}
