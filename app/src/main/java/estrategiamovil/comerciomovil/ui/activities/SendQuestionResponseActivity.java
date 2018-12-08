package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.ui.fragments.ManagePublicationsFragment;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class SendQuestionResponseActivity extends AppCompatActivity {
    public static final String ID_PUBLICATION = "id_publicacion";
    public static final String ANSWERABLE = "answerable";
    public static final String FLOW = "flow";
    public static final String FLOW_FROM_DETAIL = "detail";
    public static final String TEXT_SELECTED = "text_selected";
    public static final String ID_QUESTION = "id_question";
    public static final String OPERATION_QUESTION = "question";
    public static final String OPERATION_RESPONSE = "response";
    public static final String TYPE_OPERATION = "type_operation";
    private static final String TAG = SendQuestionResponseActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
    private EditText text;
    private TextView text_counter;
    private int max_length = 1980;
    private View mCustomView;
    private String id_publication = "";
    private String id_question = "";
    private String answerable = "false";
    private String flow = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_question_response);
        progressBar = (ProgressBar) findViewById(R.id.pbLoading_questions);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_questions);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(
                getSupportActionBar().DISPLAY_SHOW_CUSTOM,
                getSupportActionBar().DISPLAY_SHOW_CUSTOM | getSupportActionBar().DISPLAY_SHOW_HOME
                        | getSupportActionBar().DISPLAY_SHOW_TITLE);

        LayoutInflater mInflater = LayoutInflater.from(this);
        mCustomView = mInflater.inflate(R.layout.actionbar_custom_view_done_cancel, null);
        mCustomView.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Done"
                        //if update done then finish
                        String idUser = ApplicationPreferences.getLocalStringPreference(SendQuestionResponseActivity.this,Constants.localUserId);
                        boolean valid = true;
                        if (!(text.getText().toString().trim().length()>0)){
                            valid = false;
                            text.setError(getString(R.string.error_field_required));
                        }else{
                            valid=true;
                            text.setError(null);
                        }
                        if (valid){
                            if (getAnswerable().equals(String.valueOf(true))){//save response
                                Log.d(TAG,"response ...id_publication:"+getId_publication() + " id_question:"+id_question);
                                saveQuestionResponse(getId_publication(),getId_question(),text.getText().toString(),OPERATION_RESPONSE);
                            }else{//save question
                                Log.d(TAG,"save question...id_publication:"+getId_publication());
                                saveQuestionResponse(getId_publication(),null,text.getText().toString(),OPERATION_QUESTION);
                            }
                        }


                    }
                });
        mCustomView.findViewById(R.id.actionbar_cancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Cancel"
                        open();

                    }
                });
        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        text_counter = (TextView) findViewById(R.id.text_counter);
        text = (EditText) findViewById(R.id.text);
        text.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                int length = s.length();
                text_counter.setTextColor(Color.parseColor(length>=max_length?Constants.colorError:Constants.colorSuccess));
                text_counter.setText(length + "/"+max_length + " Caracteres Restantes");//which is placed at the top roght corner
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        //get parameters
        Intent intent = getIntent();
        id_publication= intent.getStringExtra(ID_PUBLICATION);
        answerable = intent.getStringExtra(ANSWERABLE);
        id_question= intent.getStringExtra(ID_QUESTION);
        flow = intent.getStringExtra(FLOW);

        //set text, pending..
        String description = intent.getStringExtra(TEXT_SELECTED);
        if (description!=null && description.length()>0)
            text.setText(description);
        progressBar.setVisibility(ProgressBar.GONE);
    }
    @Override
    public void onBackPressed() {
        open();
    }
    public void open(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Se descartarán todos los cambios.");
        alertDialogBuilder.setTitle("Descartar Cambios");
        alertDialogBuilder.setPositiveButton("DESCARTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(LocationsActivity.this, "You clicked yes button", Toast.LENGTH_LONG).show();

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public String getId_publication() {
        return this.id_publication;
    }

    public String getId_question() {
        return id_question;
    }

    public String getAnswerable() {
        return answerable;
    }

    public String getFlow() {
        return flow;
    }

    private void createProgressDialog(String message) {
        progressDialog = new ProgressDialog(SendQuestionResponseActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }
    private void saveQuestionResponse(String id_publication, String id_question,String data,String type_operation) {
        initProcess(true);
        String idUser = ApplicationPreferences.getLocalStringPreference(SendQuestionResponseActivity.this,Constants.localUserId);
        HashMap<String, String> params = new HashMap<>();
        params.put("method", "saveQuestionResponse");
        params.put("id_publication", id_publication);
        params.put("id_question", id_question);
        params.put("id_user", idUser);
        params.put("data", data);
        params.put(TYPE_OPERATION, type_operation);
        VolleyPostRequest(Constants.GET_QUESTIONS, params);
    }
    private void initProcess(boolean flag) {
        if (flag)
            createProgressDialog(getString(R.string.promtp_payment_processing));
        else
            closeProgressDialog();
    }
    public void VolleyPostRequest(String url, final HashMap<String, String> args) {
        JSONObject jobject = new JSONObject(args);
        Log.d(TAG, "VolleyPostRequest:" + jobject.toString());

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                processResponseUpdate(response,args);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                                ShowConfirmations.showConfirmationMessage(getString(R.string.generic_error), SendQuestionResponseActivity.this);
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
                }
        );


    }

    private void processResponseUpdate(JSONObject response,HashMap<String, String> args) {
        Log.d(TAG, "Result:" + response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    JSONObject result = response.getJSONObject("result");
                    initProcess(false);
                    ShowConfirmations.showConfirmationMessage(result.getString("message"),SendQuestionResponseActivity.this);

                    if (getFlow()!=null &&  !(getFlow().equals(this.FLOW_FROM_DETAIL))) {
                        AskResponseActivity.setList_changed(true);//update notification
                    }
                    Intent intent = new Intent();
                    intent.putExtra(TYPE_OPERATION,args.get(TYPE_OPERATION));
                    setResult(Activity.RESULT_OK, intent);
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

}
