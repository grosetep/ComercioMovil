package estrategiamovil.comerciomovil.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Html;
import android.text.Spanned;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Commission;
import estrategiamovil.comerciomovil.modelo.LoginResponse;
import estrategiamovil.comerciomovil.modelo.SuscriptionDetail;
import estrategiamovil.comerciomovil.modelo.SuscriptionPrice;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.tools.UserLocalProfile;
import estrategiamovil.comerciomovil.tools.UserSuscription;
import estrategiamovil.comerciomovil.tools.VolleyErrorHelper;
import estrategiamovil.comerciomovil.ui.adapters.GenericSppinerAdapter;
import estrategiamovil.comerciomovil.ui.fragments.CardsFragment;
import estrategiamovil.comerciomovil.ui.fragments.SplashFragment;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class SuscriptionActivity extends AppCompatActivity {
    private static final String TAG = SuscriptionActivity.class.getSimpleName();
    private CardView layout_button_expandable;
    private CardView card_prices;
    private ImageButton button_display_benefits;
    private ExpandableRelativeLayout container_benefits;
    private ProgressBar pbLoading_send_email;
    private ProgressBar loading_subscription;
    private RadioButton radio_ads;
    private TextView text_ads_price;
    private RadioButton radio_cupons;
    private TextView text_cupons_price;
    private TextView text_promt_effectiveDate;
    private TextView text_benefits_title;
    private SeekBar seekBar;
    private TextView text_days_active;
    private LinearLayout layout_advantage_ads;
    private LinearLayout layout_advantage_cupons;
    private AppCompatButton button_pay_suscription;
    private AppCompatButton button_init_login;
    private AppCompatButton button_upgrade;
    private NestedScrollView container;
    private LinearLayout layout_user;
    private RelativeLayout layout_suscriptor;
    private Spinner spinner_term;
    private TextView text_status;
    private CardView card_detail;
    private TextView form_advantage_title_ads;
    private TextView form_advantage_title_cupons;
    private TextView text_tip_announcement;
    private CardView layout_tip_1;
    private SuscriptionPrice[] prices;
    private final int METHOD_GET_COMMISSIONS = 4;
    private final int METHOD_UPGRADE_SUSCRIPTION = 3;
    private final int METHOD_EMAIL_SUSCRIPTION = 2;
    private final int METHOD_GET_PRICES_SUSCRIPTION = 1;
    private SuscriptionPrice price = null;
    private SuscriptionDetail detail;
    private Commission commission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcription);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_subscription);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initGUI();
        getCostTerms();
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.begin_screen, new SplashFragment(), "SplashFragment")
                    .commit();
        }
    }
    private void getCommissions(){
        VolleyGetRequest(Constants.SUSCRIPTIONS + "?method=getCommisions",METHOD_GET_COMMISSIONS);
    }
    private void initGUI(){
        container = (NestedScrollView) findViewById(R.id.container);
        layout_user = (LinearLayout) findViewById(R.id.layout_user);
        layout_suscriptor = (RelativeLayout) findViewById(R.id.layout_suscriptor);
        pbLoading_send_email = (ProgressBar) findViewById(R.id.pbLoading_send_email);
        layout_tip_1 = (CardView) findViewById(R.id.layout_tip_1);
        text_tip_announcement = (TextView) findViewById(R.id.text_tip_announcement);
        loading_subscription = (ProgressBar) findViewById(R.id.loading_subscription);
        radio_ads = (RadioButton) findViewById(R.id.radio_ads);
        text_ads_price = (TextView) findViewById(R.id.text_ads_price);
        radio_cupons = (RadioButton) findViewById(R.id.radio_cupons);
        text_cupons_price = (TextView) findViewById(R.id.text_cupons_price);
        text_promt_effectiveDate = (TextView) findViewById(R.id.text_promt_effectiveDate);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        text_days_active = (TextView) findViewById(R.id.text_days_active);
        layout_advantage_ads = (LinearLayout) findViewById(R.id.layout_advantage_ads);
        layout_advantage_cupons = (LinearLayout) findViewById(R.id.layout_advantage_cupons);
        button_pay_suscription = (AppCompatButton) findViewById(R.id.button_pay_suscription);
        spinner_term = (Spinner) findViewById(R.id.spinner_term);
        text_status = (TextView) findViewById(R.id.text_status);
        card_detail = (CardView) findViewById(R.id.card_detail);
        form_advantage_title_ads = (TextView) findViewById(R.id.form_advantage_title_ads);
        form_advantage_title_cupons = (TextView) findViewById(R.id.form_advantage_title_cupons);
        button_init_login = (AppCompatButton) findViewById(R.id.button_init_login);
        button_upgrade = (AppCompatButton) findViewById(R.id.button_upgrade);
        layout_button_expandable = (CardView) findViewById(R.id.layout_button_expandable);
        button_display_benefits = (ImageButton) findViewById(R.id.button_display_benefits);
        container_benefits = (ExpandableRelativeLayout) findViewById(R.id.container_benefits);
        text_benefits_title = (TextView) findViewById(R.id.text_benefits_title);
        card_prices = (CardView) findViewById(R.id.card_prices);
        ShowScreen(false);
        button_display_benefits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container_benefits.toggle();
            }
        });
        layout_button_expandable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container_benefits.toggle();
            }
        });
        radio_ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radio_ads.isChecked()) {
                   /* radio_cupons.setChecked(false);*/

                    layout_advantage_cupons.animate()
                            .translationY(0)
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    layout_advantage_ads.setAlpha(1.0f);
                                    layout_advantage_ads.setVisibility(View.VISIBLE);
                                    layout_advantage_cupons.setVisibility(View.GONE);
                                }
                            });
                    //change button text
                    button_pay_suscription.setText(getResources().getString(R.string.text_promt_pay_suscription));
                    button_pay_suscription.setVisibility(View.VISIBLE);
                    form_advantage_title_ads.setText(R.string.text_promt_advantage_ads);
                    text_benefits_title.setText(R.string.prompt_benefits_ads_title);
                }

            }
        });



        setDetail(UserSuscription.getSuscriptionDetails(SuscriptionActivity.this));
        if (getDetail()!=null){// usuarios ya registrados
            text_ads_price.setText(detail.getPrice_suscription());
            text_cupons_price.setText(detail.getPrice_cupons());
            text_status.setText(detail.getMessage());
            text_promt_effectiveDate.setText(getString(R.string.prompt_cupons_suscription));
            layout_advantage_ads.setVisibility(View.GONE);
            layout_advantage_cupons.setVisibility(View.VISIBLE);
            button_pay_suscription.setVisibility(View.GONE);
            button_pay_suscription.setText(getResources().getString(R.string.text_promt_pay_suscription));
            LoginResponse login = UserLocalProfile.getUserProfile(getApplicationContext());
            if (login!=null){
                if (login.getUserType().contains("usuario")){//normal:dar opciones de upgrade
                    layout_suscriptor.setVisibility(View.GONE);
                    layout_user.setVisibility(View.VISIBLE);
                    button_upgrade.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intentRegister = new Intent(getApplicationContext(), UpgradeActivity.class);
                            startActivityForResult(intentRegister, Constants.REQUEST_SIGNUP_SUBSCRIBER);
                        }
                    });
                }else{//suscriptor: Mostrar las comisiones actuales para venta de cupones
                    layout_suscriptor.setVisibility(View.VISIBLE);
                    layout_user.setVisibility(View.GONE);
                    if (getDetail().getExist_suscription_ads().equals("0")){//no hay compras de publicidad
                        //si no hay campaña entonces ocultar card_detail
                        card_detail.setVisibility(View.GONE);
                        card_prices.setVisibility(View.GONE);
                    }else{//hay compra de publicidad
                        card_detail.setVisibility(View.VISIBLE);
                        seekBar.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return true;
                            }
                        });
                        text_promt_effectiveDate.setText(detail.getDate_active() != null ? detail.getDate_active() : getString(R.string.action_buy_disabled));
                        text_days_active.setText((detail.getDays_active()!=null?detail.getDays_active():0)+"/"+detail.getDays_total()   + " "+getResources().getString(R.string.text_promt_days));
                        seekBar.setMax(detail.getDays_total()!=null?Integer.parseInt(detail.getDays_total()):0);
                        seekBar.setProgress(detail.getDays_active()!=null?Integer.parseInt(detail.getDays_active()):0);
                        radio_ads.setChecked(true);
                        layout_advantage_ads.setVisibility(View.VISIBLE);
                        layout_advantage_cupons.setVisibility(View.GONE);
                        button_pay_suscription.setText(getResources().getString(R.string.text_promt_pay_suscription));
                        //status_suscription=0->Usuario, status_suscription=1->Cupones, status_suscription=2-> publicidad no renovada, status_suscription=3-> publicidad activa
                        if (getDetail().getStatus_suscription().equals("2")){
                            layout_tip_1.setVisibility(View.VISIBLE);
                        }else if(getDetail().getStatus_suscription().equals("3")){
                            radio_ads.setChecked(true);
                            layout_tip_1.setVisibility(View.GONE);
                        }
                    }
                }
            }

            button_pay_suscription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (radio_ads.isChecked())
                        sendEmailSuscriptionRequired();
                   /* else if(radio_cupons.isChecked())
                        upgradeSuscription();*/
                }
            });

            spinner_term.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SuscriptionPrice suscription_selected =(SuscriptionPrice) parent.getItemAtPosition(position);
                    if (suscription_selected!=null && !suscription_selected.getId().equals("0")) {
                        price = suscription_selected;
                    }else{ price = null;}

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else{//usuario no registrado: mostrar beneficios de darse de alta y login
            Log.d(TAG,"usuario no registrado");
            card_prices.setVisibility(View.GONE);
            layout_user.setVisibility(View.GONE);
            card_detail.setVisibility(View.GONE);
            layout_advantage_ads.setVisibility(View.GONE);
            layout_advantage_cupons.setVisibility(View.VISIBLE);
            if (radio_ads.isChecked())  form_advantage_title_ads.setText(getResources().getString(R.string.text_promt_advantage_guess));
            else if(radio_cupons.isChecked()) form_advantage_title_cupons.setText(getResources().getString(R.string.text_promt_advantage_guess));
            button_pay_suscription.setVisibility(View.GONE);
            button_init_login.setVisibility(View.VISIBLE);
            button_init_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SuscriptionActivity.this, LoginActivity.class);
                    finish();
                    startActivity(intent);
                }
            });
            layout_tip_1.setVisibility(View.VISIBLE);
            text_tip_announcement.setText(getString(R.string.promt_suscription_tip_2));
        }

    }
    /*private void upgradeSuscription(){
        initWork(true);
        String idUser = ApplicationPreferences.getLocalStringPreference(getApplicationContext(), Constants.localUserId);
        VolleyGetRequest(Constants.SUSCRIPTIONS + "?method=upgradeToCupons&idUser=" + idUser, METHOD_UPGRADE_SUSCRIPTION);
    }*/
    private void initWork(boolean flag){
        pbLoading_send_email.setVisibility(flag?View.VISIBLE:View.GONE);
        button_pay_suscription.setVisibility(flag?View.GONE:View.VISIBLE);
    }
    private void sendEmailSuscriptionRequired(){
        initWork(true);
        //get sppiner value
        Log.d(TAG,"sendEmailSuscriptionRequired...");
        if (price!=null) {
            String idUser = ApplicationPreferences.getLocalStringPreference(getApplicationContext(), Constants.localUserId);
            VolleyGetRequest(Constants.SUSCRIPTIONS + "?method=sendEmailSuscriptionRequired&idUser=" + idUser + "&id_suscription_cost=" + price.getId(), METHOD_EMAIL_SUSCRIPTION);
        }else{
            initWork(false);
            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.promt_select_suscriptino_error),this);
        }
    }
    private void getCostTerms(){
        VolleyGetRequest(Constants.SUSCRIPTIONS + "?method=getPricesAd",METHOD_GET_PRICES_SUSCRIPTION);
    }
    public void VolleyGetRequest(String url,final int callback){
        Log.d(TAG,"VolleyGetRequest:"+url);
        VolleySingleton.
                getInstance(getApplicationContext()).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                (String) null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Procesar la respuesta Json
                                        if (callback == METHOD_GET_PRICES_SUSCRIPTION)
                                            processResponse(response);
                                        else if (callback == METHOD_EMAIL_SUSCRIPTION)
                                            processResponseEmail(response);
                                        else if (callback == METHOD_GET_COMMISSIONS)
                                            processResponseCommissions(response);
                                        else
                                            processResponseUpgrade(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        String message = VolleyErrorHelper.getErrorType(error, getApplicationContext());
                                        //Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                                        Toast.makeText(
                                                getApplicationContext(),
                                                message,
                                                Toast.LENGTH_SHORT).show();
                                        initWork(false);

                                    }
                                }

                        ).setRetryPolicy(new DefaultRetryPolicy(
                                Constants.MY_SOCKET_TIMEOUT_MS,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                );
    }
    public void processResponseCommissions(JSONObject response){
        Log.d(TAG, response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    Gson gson = new Gson();
                    JSONObject mensaje = response.getJSONObject("result");
                    commission = gson.fromJson(mensaje.toString(), Commission.class);
                    text_cupons_price.setText(fromHtml(commission.getMessage_commissions()));
                    ShowScreen(true);
                    break;
                case "2":
                    initWork(false);
                    ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.prompt_email_suscription_error),this);
                    break;
                default:
                    initWork(false);
                    ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.prompt_email_suscription_error),this);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            initWork(false);
            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.prompt_email_suscription_error),this);
        }
    }
    private void processResponse(JSONObject response){
        // On complete call either onSignupSuccess or onSignupFailed
        // depending on success
        Log.d(TAG, response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    JSONArray mensaje = response.getJSONArray("result");
                    Gson gson = new Gson();
                    prices = gson.fromJson(mensaje.toString(), SuscriptionPrice[].class);
                    //remove cupons cost first pass it to adapter
                    ArrayList<SuscriptionPrice> list = new ArrayList<>();
                    list.addAll(Arrays.asList(prices));
                    for (int i=0;i<list.size();i++){
                        if (list.get(i).getIdTypeSuscription().equals("2")){ list.remove(i);}
                    }
                    SuscriptionPrice sp = new SuscriptionPrice();
                    sp.setCost("");sp.setDescription("Seleccione...");sp.setId("0");sp.setIdTypeSuscription("1");list.add(0,sp);
                    GenericSppinerAdapter spinnerAdapter = new GenericSppinerAdapter(SuscriptionActivity.this,R.layout.generic_spinner_rows,list);
                    spinner_term.setAdapter(spinnerAdapter);
                    getCommissions();
                    break;
                case "2":
                    Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_SHORT);
                    ShowScreen(false);

                    break;

            }
        } catch (JSONException e) {
            e.printStackTrace();
            ShowScreen(false);
        }
    }
    public void processResponseEmail(JSONObject response){
        Log.d(TAG, response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    //ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.prompt_email_suscription_required),this);
                    String email = UserLocalProfile.getUserProfile(getApplicationContext()).getEmail();
                    confirmAction(getResources().getString(R.string.prompt_email_suscription_required) + email,null,getResources().getString(R.string.done),null);
                    break;
                case "2":
                    initWork(false);
                    ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.prompt_email_suscription_error),this);
                    break;
                default:
                    initWork(false);
                    ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.prompt_email_suscription_error),this);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            initWork(false);
            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.prompt_email_suscription_error),this);
        }
    }
    public void processResponseUpgrade(JSONObject response){
        Log.d(TAG, response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    String email = UserLocalProfile.getUserProfile(getApplicationContext()).getEmail();
                    confirmAction(getResources().getString(R.string.prompt_suscription_upgrated),getString(R.string.prompt_suscription_upgrated_title),getResources().getString(R.string.done),null);
                    break;
                case "2":
                    initWork(false);
                    ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.prompt_email_suscription_error),this);
                    break;
                default:
                    initWork(false);
                    ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.prompt_email_suscription_error),this);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            initWork(false);
            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.prompt_email_suscription_error),this);
        }
    }

    public void confirmAction(String message, String title, String positive, String negative){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        if (title!=null) alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        if (negative!=null) {
        alertDialogBuilder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(LocationsActivity.this, "You clicked yes button", Toast.LENGTH_LONG).show();

            }
        });
        }
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void ShowScreen(boolean flag){
        container.setVisibility(flag? View.VISIBLE:View.GONE);
        loading_subscription.setVisibility(flag?View.GONE:View.VISIBLE);
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

    public SuscriptionDetail getDetail() {
        return detail;
    }

    public void setDetail(SuscriptionDetail detail) {
        this.detail = detail;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG,"onActivityResult------->requestCode:" + resultCode);
        if (requestCode == Constants.REQUEST_SIGNUP_SUBSCRIBER) {//registro usuario exitoso
            if (resultCode == RESULT_OK || resultCode == Constants.SIGNUP_SUBSCRIBER_OK) {

                ((LinearLayout)findViewById(R.id.begin_screen)).setVisibility(View.VISIBLE);
                ((NestedScrollView)findViewById(R.id.container)).setVisibility(View.GONE);
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(3 * 1000);//3 segundos
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            //nothing to do after automatic login
                            login();
                        }
                    }
                };
                timerThread.start();

            }else if (resultCode == RESULT_CANCELED){
                Log.d(TAG,"RESULT CANCELED!!");
            }
        }
    }
    private void login(){
        ApplicationPreferences.saveLocalPreference(SuscriptionActivity.this, Constants.localUserAdministrator,"0");
        ApplicationPreferences.saveLocalPreference(SuscriptionActivity.this, Constants.showTipSignupSuscriber,String.valueOf(true));
        //load main activity
        startMainActivity();
        //Finish login activity
        finish();
    }
    private void startMainActivity(){
        Context context = SuscriptionActivity.this;
        Intent intentMain =  new Intent(context, MainActivity.class);
        CardsFragment.locationChanged = true;
        context.startActivity(intentMain);
    }
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}
