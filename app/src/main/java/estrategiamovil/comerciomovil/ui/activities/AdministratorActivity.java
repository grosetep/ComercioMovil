package estrategiamovil.comerciomovil.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.ui.fragments.DatePickerFragment;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class AdministratorActivity extends AppCompatActivity implements DatePickerFragment.OnDateSelectedListener{
    private static final String TAG = AdministratorActivity.class.getSimpleName();
    private EditText id_user;
    private EditText external_reference;
    private EditText payment_type_id;
    private EditText payment_method_reference_id;
    private EditText text_title;
    private EditText text_subtitle;
    private EditText text_message;
    private EditText text_topic;
    private EditText text_id_publication;
    private EditText text_id_user;
    private EditText text_reference_suscription;
    private EditText text_days_patch_aggregate;
    private EditText text_id_cost_suscription;
    private EditText text_reference_suscription_payment;
    private EditText text_id_user_suscription;
    private EditText text_id_suscriptor;
    private EditText text_date_from;
    private EditText text_date_to;
    private EditText text_id_user_purchases;
    private EditText text_date_from_purchases;
    private EditText text_date_to_purchases;
    private EditText text_q_method_payment;
    private EditText text_reference_id;
    private EditText text_q_external_reference;
    private EditText text_purchase_capture_line;
    private EditText text_date_from_detail_p;
    private EditText text_date_to_detail_p;

    private AppCompatButton button_delete_user;
    private AppCompatButton button_send_cupons;
    private AppCompatButton button_send_notification;
    private AppCompatButton button_delete_publication;
    private AppCompatButton button_get_payment;
    private AppCompatButton button_get_users;
    private AppCompatButton button_notify_suscription;
    private AppCompatButton button_get_suscription_payment;
    private AppCompatButton button_get_publications;
    private AppCompatButton button_get_payment_detail;
    private AppCompatButton button_get_purchases;
    private AppCompatButton button_detail_purchase;

    private EditText text_result;
    private EditText text_name;
    private EditText text_first;
    private EditText text_last;
    private EditText text_company;
    private EditText text_user_email;

    private ProgressDialog progressDialog;
    private Calendar from;
    private Calendar to;

    private LinearLayout layout_monitor_publications;

    private final int METHOD_DELETE_USER = 1;
    private final int METHOD_SEND_CUPONS = 2;
    private final int METHOD_DELETE_PUBLICATION = 3;
    private final int METHOD_GET_PAYMENT = 4;
    private final int METHOD_SEND_NOTIFICATION = 5;
    private final int METHOD_GET_USERS = 6;
    private final int METHOD_NOTIFY_SUSCRIPTION = 7;
    private final int METHOD_GET_SUSCRIPTION_PAYMENT = 8;
    private final int METHOD_GET_PUBLICATIONS = 9;
    private final int METHOD_GET_PAYMENT_DETAIL = 10;
    private final int METHOD_GET_PURCHASES = 11;
    private final int METHOD_GET_DETAIL_PURCHASE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initGUI();
    }
    private void initGUI(){
        id_user = (EditText) findViewById(R.id.id_user);
        external_reference = (EditText) findViewById(R.id.external_reference);
        payment_type_id = (EditText) findViewById(R.id.payment_type_id);
        payment_method_reference_id = (EditText) findViewById(R.id.payment_method_reference_id);
        text_title = (EditText) findViewById(R.id.text_title);
        text_subtitle = (EditText) findViewById(R.id.text_subtitle);
        text_message = (EditText) findViewById(R.id.text_message);
        text_topic = (EditText) findViewById(R.id.text_topic);
        text_id_publication = (EditText) findViewById(R.id.text_id_publication);
        text_id_user = (EditText) findViewById(R.id.text_id_user);
        text_name = (EditText) findViewById(R.id.text_name);
        text_first = (EditText) findViewById(R.id.text_first);
        text_last = (EditText) findViewById(R.id.text_last);
        text_company = (EditText) findViewById(R.id.text_company);
        text_user_email = (EditText) findViewById(R.id.text_user_email);
        text_reference_suscription = (EditText) findViewById(R.id.text_reference_suscription);
        text_days_patch_aggregate = (EditText) findViewById(R.id.text_days_patch_aggregate);
        text_id_cost_suscription = (EditText) findViewById(R.id.text_id_cost_suscription);
        text_reference_suscription_payment = (EditText) findViewById(R.id.text_reference_suscription_payment);
        text_id_user_suscription = (EditText) findViewById(R.id.text_id_user_suscription);
        text_id_suscriptor = (EditText) findViewById(R.id.text_id_suscriptor);
        text_date_from = (EditText) findViewById(R.id.text_date_from);
        text_id_user_purchases = (EditText) findViewById(R.id.text_id_user_purchases);
        text_date_from_purchases = (EditText) findViewById(R.id.text_date_from_purchases);
        text_date_to_purchases = (EditText) findViewById(R.id.text_date_to_purchases);

        text_date_from_detail_p = (EditText) findViewById(R.id.text_date_from_detail_p);
        text_date_to_detail_p = (EditText) findViewById(R.id.text_date_to_detail_p);

        text_q_method_payment = (EditText) findViewById(R.id.text_q_method_payment);
        text_reference_id = (EditText) findViewById(R.id.text_reference_id);
        text_q_external_reference = (EditText) findViewById(R.id.text_q_external_reference);
        text_purchase_capture_line = (EditText) findViewById(R.id.text_purchase_capture_line);

        text_date_from.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment picker = new DatePickerFragment();
                        picker.show(getSupportFragmentManager(), "date_from");

                    }
                }
        );
        text_date_to = (EditText) findViewById(R.id.text_date_to);
        text_date_to.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment picker = new DatePickerFragment();
                        picker.show(getSupportFragmentManager(), "date_to");

                    }
                }
        );
        text_date_from_purchases.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment picker = new DatePickerFragment();
                        picker.show(getSupportFragmentManager(), "date_from_purchases");

                    }
                }
        );
        text_date_to_purchases.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment picker = new DatePickerFragment();
                        picker.show(getSupportFragmentManager(), "date_to_purchases");

                    }
                }
        );
        text_date_from_detail_p.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment picker = new DatePickerFragment();
                        picker.show(getSupportFragmentManager(), "text_date_from_detail_p");

                    }
                }
        );
        text_date_to_detail_p.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment picker = new DatePickerFragment();
                        picker.show(getSupportFragmentManager(), "text_date_to_detail_p");

                    }
                }
        );


        button_delete_user = (AppCompatButton) findViewById(R.id.button_delete_user);
        button_delete_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                if (id_user.getText().toString().trim().equals("")){
                    id_user.setError(getString(R.string.error_field_required));
                    valid = false;
                }else{
                    id_user.setError(null);
                    valid = true;
                }

                if (valid) {
                    createProgressDialog("Ejecutando operación..");
                    deleteUser(id_user.getText().toString().trim());

                }
            }
        });
        button_send_cupons = (AppCompatButton) findViewById(R.id.button_send_cupons);
        button_send_cupons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;

                if (payment_type_id.getText().toString().trim().equals("")){
                    payment_type_id.setError(getString(R.string.error_field_required));
                    valid = false;
                }else{
                    payment_type_id.setError(null);
                    valid = true;
                }
                if (external_reference.getText().toString().trim().equals("") && payment_method_reference_id.getText().toString().trim().equals("")){
                    external_reference.setError("Proporciona reference o method_reference");
                    valid = false;
                }else{
                    external_reference.setError(null);
                    valid = true;
                }
                if (valid) {
                    createProgressDialog("Consultando..");
                    String url = Constants.ADMINISTRATION + "?method=sendCupons&external_reference=" +
                            external_reference.getText().toString().trim() + "&payment_type_id=" + payment_type_id.getText().toString().trim() +
                            "&payment_method_reference_id=" + payment_method_reference_id.getText().toString().trim();
                    VolleyGetRequest(url, METHOD_SEND_CUPONS);
                }
            }
        });
        button_send_notification = (AppCompatButton) findViewById(R.id.button_send_notification);
        button_send_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;

                if (text_title.getText().toString().trim().equals("")){
                    text_title.setError(getString(R.string.error_field_required));
                    valid = false;
                }else{
                    text_title.setError(null);
                    valid = true;
                }
                if (text_subtitle.getText().toString().trim().equals("")){
                    text_subtitle.setError(getString(R.string.error_field_required));
                    valid = false;
                }else{
                    text_subtitle.setError(null);
                    valid = true;
                }
                if (text_message.getText().toString().trim().equals("")){
                    text_message.setError(getString(R.string.error_field_required));
                    valid = false;
                }else{
                    text_message.setError(null);
                    valid = true;
                }
                if (text_topic.getText().toString().trim().equals("")){
                    text_topic.setError(getString(R.string.error_field_required));
                    valid = false;
                }else{
                    text_topic.setError(null);
                    valid = true;
                }
                if (valid) {
                    createProgressDialog("Enviando...");
                    StringBuffer url = new StringBuffer();
                    JSONObject data = new JSONObject();
                    try {
                        data.put("type_notification",Constants.notifycation_types.get("administration").getNotificationType());
                        data.put("reply","0");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    url.append(Uri.parse(Constants.GET_PUBLICATIONS).buildUpon().
                            appendQueryParameter("method", "send").
                            appendQueryParameter("title", text_title.getText().toString().trim()).
                            appendQueryParameter("subtitle", text_subtitle.getText().toString().trim()).
                            appendQueryParameter("message", text_message.getText().toString().trim()).
                            appendQueryParameter("topic",  text_topic.getText().toString().trim()).
                            appendQueryParameter("process_data", data.toString() ).
                            build().toString());
                    VolleyGetRequest(url.toString(), METHOD_SEND_NOTIFICATION);
                }
            }
        });
        button_delete_publication = (AppCompatButton) findViewById(R.id.button_delete_publication);
        button_delete_publication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;

                if (text_id_publication.getText().toString().trim().equals("")){
                    text_id_publication.setError(getString(R.string.error_field_required));
                    valid = false;
                }else{
                    text_id_publication.setError(null);
                    valid = true;
                }
                if (valid) {
                    createProgressDialog("Eliminando..");
                    HashMap<String, String> params = new HashMap<>();
                    params.put("method", "removePublication");
                    params.put("idPublication", text_id_publication.getText().toString().trim());
                    VolleyPostRequest(Constants.REMOVE_PUBLICATION, params, METHOD_DELETE_PUBLICATION);
                }
            }
        });
        button_get_payment = (AppCompatButton) findViewById(R.id.button_get_payment);
        button_get_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProgressDialog("Consultando..");
                String url = Constants.GET_SALES+"?method=getSalesByUserBrief&idUser="+text_id_user.getText().toString().trim();
                VolleyGetRequest(url,METHOD_GET_PAYMENT);
            }
        });
        button_get_users = (AppCompatButton) findViewById(R.id.button_get_users);
        button_get_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProgressDialog("Consultando..");
                String url = Constants.USERS+"?method=getUsers&name="+text_name.getText().toString().trim()+
                        "&first="+text_first.getText().toString().trim()+
                        "&last="+text_last.getText().toString().trim()+
                        "&company="+text_company.getText().toString().trim()+
                        "&email="+text_user_email.getText().toString().trim();
                VolleyGetRequest(url,METHOD_GET_USERS);
            }
        });
        button_notify_suscription = (AppCompatButton) findViewById(R.id.button_notify_suscription);
        button_notify_suscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;

                if (text_reference_suscription.getText().toString().trim().equals("")){
                    text_reference_suscription.setError(getString(R.string.error_field_required));
                    valid = false;
                }else{
                    text_reference_suscription.setError(null);
                    valid = true;
                }
                if (text_days_patch_aggregate.getText().toString().trim().equals("")){
                    text_days_patch_aggregate.setError(getString(R.string.error_field_required));
                    valid = false;
                }else{
                    text_days_patch_aggregate.setError(null);
                    valid = true;
                }
                if (text_id_cost_suscription.getText().toString().trim().equals("")){
                    text_id_cost_suscription.setError(getString(R.string.error_field_required));
                    valid = false;
                }else{
                    text_id_cost_suscription.setError(null);
                    valid = true;
                }
                if (valid) {
                    createProgressDialog("Nofiticando..");
                    String url = Constants.ADMINISTRATION + "?method=notifySuscriptionPayment&reference=" + text_reference_suscription.getText().toString().trim() +
                            "&days_patch_aggregate=" + text_days_patch_aggregate.getText().toString().trim() +
                            "&id_cost_suscription=" + text_id_cost_suscription.getText().toString().trim();
                    VolleyGetRequest(url, METHOD_NOTIFY_SUSCRIPTION);
                }
            }
        });
        button_get_suscription_payment = (AppCompatButton) findViewById(R.id.button_get_suscription_payment);
        button_get_suscription_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;

                if (text_reference_suscription_payment.getText().toString().trim().equals("") & text_id_user_suscription.getText().toString().trim().equals("")){
                    text_reference_suscription_payment.setError("Alguno de los campos debe tener valores");
                    valid = false;
                }else{
                    text_reference_suscription_payment.setError(null);
                    valid = true;
                }
                if (valid) {
                    createProgressDialog("Consultando..");
                    String url = Constants.ADMINISTRATION + "?method=getSuscriptionPayments&reference=" + text_reference_suscription_payment.getText().toString().trim() +
                            "&idUser=" + text_id_user_suscription.getText().toString().trim();
                    VolleyGetRequest(url, METHOD_GET_SUSCRIPTION_PAYMENT);
                }
            }
        });
        button_get_publications = (AppCompatButton) findViewById(R.id.button_get_publications);
        button_get_publications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;

                if (text_id_suscriptor.getText().toString().trim().length()==0 && text_date_from.getText().toString().trim().length()==0 && text_date_to.getText().toString().trim().length()==0){
                    text_id_suscriptor.setError("Alguno de los campos debe tener valores");
                    valid = false;
                }else{
                    text_id_suscriptor.setError(null);
                    valid = true;
                }
                if (valid){
                    createProgressDialog("Consultando..");
                    String url = Constants.ADMINISTRATION + "?method=getAllPublicationsByIdUser&idUser=" + text_id_suscriptor.getText().toString().trim() +
                            "&date_from=" + text_date_from.getText().toString().trim() +
                            "&date_to=" + text_date_to.getText().toString().trim();
                    VolleyGetRequest(url, METHOD_GET_PUBLICATIONS);
                }
            }
        });
        button_get_payment_detail = (AppCompatButton) findViewById(R.id.button_get_payment_detail);
        button_get_payment_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;

                if (text_q_method_payment.getText().toString().trim().length()==0){
                    text_q_method_payment.setError(getString(R.string.error_field_required));
                    valid = false;
                }else{
                    text_q_method_payment.setError(null);
                    valid = true;
                }
                if (text_q_external_reference.getText().toString().trim().length()==0){
                    text_q_external_reference.setError(getString(R.string.error_field_required));
                    valid = false;
                }else{
                    text_q_external_reference.setError(null);
                    valid = true;
                }

                if (valid){
                    createProgressDialog("Consultando..");
                    String url = Constants.ADMINISTRATION + "?method=getPaymentDetail&id_metodo_pago=" + text_q_method_payment.getText().toString().trim() +
                            "&external_reference=" + text_q_external_reference.getText().toString().trim() +
                            "&reference_id=" + text_reference_id.getText().toString().trim();
                    VolleyGetRequest(url, METHOD_GET_PAYMENT_DETAIL);
                }
            }
        });
        button_get_purchases = (AppCompatButton) findViewById(R.id.button_get_purchases);
        button_get_purchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                String id_user = text_id_user_purchases.getText().toString().trim().length()>0?text_id_user_purchases.getText().toString().trim():"";
                if (valid){
                    createProgressDialog("Consultando..");
                    String url = Constants.ADMINISTRATION + "?method=getAllPurchasesByIdUser&idUser=" + id_user +
                            "&date_from=" + text_date_from_purchases.getText().toString().trim() +
                            "&date_to=" + text_date_to_purchases.getText().toString().trim();
                    VolleyGetRequest(url, METHOD_GET_PURCHASES);
                }
            }
        });
        layout_monitor_publications = (LinearLayout) findViewById(R.id.layout_monitor_publications);
        layout_monitor_publications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdministratorActivity.this, AdministratorPublicationsActivity.class);
                startActivity(i);
            }
        });
        button_detail_purchase = (AppCompatButton) findViewById(R.id.button_detail_purchase);
        button_detail_purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProgressDialog("Consultando..");
                String url = Constants.ADMINISTRATION + "?method=getDetailPurchases&capture_line=" + text_purchase_capture_line.getText().toString() +
                        "&date_from=" + text_date_from_detail_p.getText().toString().trim() +
                        "&date_to=" + text_date_to_detail_p.getText().toString().trim();
                Log.d(TAG,"REQUEST:"+url);
                VolleyGetRequest(url, METHOD_GET_DETAIL_PURCHASE);
                text_date_from_detail_p.setText("");
                text_date_to_detail_p.setText("");
            }
        });

        text_result = (EditText) findViewById(R.id.text_result);

    }
    private void deleteUser(String id){
        HashMap<String,String> params = new HashMap<>();
        params.put("method","removeUser");
        params.put("idUser", id);
        VolleyPostRequest(Constants.REMOVE_USER, params,METHOD_DELETE_USER);
    }
    @Override
    public void onDateSelected(int ano, int mes, int dia) {
        Log.d(TAG,"fecha: " + ano + " / " + mes + " / " + dia);
        actualizarFecha(ano, mes, dia);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private Calendar createDateCalendar(int year,int month, int day){
        final Calendar c = Calendar.getInstance();
        c.set(year,month,day);
        return c;
    }
    public void actualizarFecha(int ano, int mes, int dia) {
        // Setear en el textview la fecha
        DatePickerFragment f1=(DatePickerFragment)getSupportFragmentManager().findFragmentByTag("date_from");
        DatePickerFragment f2=(DatePickerFragment)getSupportFragmentManager().findFragmentByTag("date_to");
        DatePickerFragment f1_purchases=(DatePickerFragment)getSupportFragmentManager().findFragmentByTag("date_from_purchases");
        DatePickerFragment f2_purchases=(DatePickerFragment)getSupportFragmentManager().findFragmentByTag("date_to_purchases");
        DatePickerFragment f1_purchases_p=(DatePickerFragment)getSupportFragmentManager().findFragmentByTag("text_date_from_detail_p");
        DatePickerFragment f2_purchases_p=(DatePickerFragment)getSupportFragmentManager().findFragmentByTag("text_date_to_detail_p");
        String date = ano + "-" + (mes + 1) + "-" + dia;

            if (f1 != null || f2 != null) {
                Calendar c = createDateCalendar(ano, mes, dia);
                if (f1 != null) {
                    text_date_from.setText(ano + "-" + (mes + 1) + "-" + dia);
                    from = c;
                } else if (f2 != null) {
                    text_date_to.setText(ano + "-" + (mes + 1) + "-" + dia);
                    to = c;
                }
            }
            else if(f1_purchases!=null || f2_purchases!=null){
                Calendar c = createDateCalendar(ano, mes, dia);
                if (f1_purchases != null) {
                    text_date_from_purchases.setText(ano + "-" + (mes + 1) + "-" + dia);
                    from = c;
                } else if (f2_purchases != null) {
                    text_date_to_purchases.setText(ano + "-" + (mes + 1) + "-" + dia);
                    to = c;
                }
            }
        else if(f1_purchases_p!=null || f2_purchases_p!=null){
                Calendar c = createDateCalendar(ano, mes, dia);
                if (f1_purchases_p != null) {
                    text_date_from_detail_p.setText(ano + "-" + (mes + 1) + "-" + dia);
                    from = c;
                } else if (f2_purchases_p != null) {
                    text_date_to_detail_p.setText(ano + "-" + (mes + 1) + "-" + dia);
                    to = c;
                }
            }
    }
    private void VolleyGetRequest(String url, final int callback) {
        text_result.setText("");

        // Realizar petición GET_BY_ID
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar la respuesta Json
                        processResponse(response,callback);
                        //  pDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        text_result.setText(error.getMessage());
                        text_result.requestFocus();
                        closeDialog();
                    }
                }

        );
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.
                getInstance(AdministratorActivity.this).
                addToRequestQueue(request
                );
    }
    public void VolleyPostRequest(String url, final HashMap<String, String> args,final int callback){
        JSONObject jobject = new JSONObject(args);
        Log.d(TAG,"VolleyPostRequest:" +  jobject.toString());

        VolleySingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                    processResponse(response, callback);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                text_result.setText(error.getMessage());
                                text_result.requestFocus();
                                closeDialog();
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
    private void processResponse(JSONObject response, final int callback) {
        Log.d(TAG, "response:" + response.toString());

        try {
            // Obtener atributo "mensaje"
            String message = response.getString("status");
            switch (message) {
                case "1"://assign values
                    String object = response.getString("result");
                    text_result.setText(object.toString());
                    text_result.requestFocus();
                    closeDialog();
                    break;
                case "2"://no detail
                    closeDialog();
                    String errorMessage = response.getString("message");
                    text_result.setText(errorMessage);
                    text_result.requestFocus();
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            closeDialog();
            text_result.setText(e.getMessage());
            text_result.requestFocus();
        }

    }
    private void createProgressDialog(String message){
        progressDialog = new ProgressDialog(AdministratorActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
    private void closeDialog(){
        if (progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
