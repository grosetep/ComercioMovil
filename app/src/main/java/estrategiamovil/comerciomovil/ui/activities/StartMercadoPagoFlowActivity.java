package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.Card;
import com.mercadopago.model.FeeDetail;
import com.mercadopago.model.Order;
import com.mercadopago.model.Payer;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Refund;
import com.mercadopago.model.TransactionDetails;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.mercadopago.CreatePaymentVO;
import estrategiamovil.comerciomovil.mercadopago.Utils;
import estrategiamovil.comerciomovil.modelo.PaymentReview;
import estrategiamovil.comerciomovil.modelo.PreOrder;
import estrategiamovil.comerciomovil.modelo.Purchase;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.StringOperations;
import estrategiamovil.comerciomovil.ui.fragments.ReviewPayFragment;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class StartMercadoPagoFlowActivity extends AppCompatActivity {
    private static final String TAG = StartMercadoPagoFlowActivity.class.getSimpleName();
    private PreOrder order;
    private Button button_mp_tdc;
    private Button button_mp_cash;
    private LinearLayout layout_buttons_mp;
    private ProgressBar pbLoading_mp_flow;
    private TextView text_status;
    private TextView text_result;
    public static final String ticket = "ticket";
    public static final String atm = "atm";
    private PaymentMethod paymentMethod;
    public static final String MP_PAYMENT_TYPE = "mp_payment_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_mercadopago_flow);
        initGUI();
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        order = (PreOrder) b.getSerializable(ReviewPayFragment.PRE_ORDER);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    private void initProcess(boolean flag){
        layout_buttons_mp.setVisibility((flag)?View.GONE:View.VISIBLE);
        pbLoading_mp_flow.setVisibility((flag)?View.VISIBLE:View.GONE);
        text_status.setVisibility((flag)?View.VISIBLE:View.GONE);
    }
    private void showResultMessage(String message,boolean flag){
        text_result.setVisibility((flag)?View.VISIBLE:View.GONE);
        text_result.setText(message);
    }

    private void initGUI(){
        layout_buttons_mp = (LinearLayout) findViewById(R.id.layout_buttons_mp);
        pbLoading_mp_flow = (ProgressBar) findViewById(R.id.pbLoading_mp_flow);
        text_status = (TextView) findViewById(R.id.text_status);
        text_result = (TextView) findViewById(R.id.text_result);
        button_mp_tdc = (Button) findViewById(R.id.button_mp_tdc);
        button_mp_cash = (Button) findViewById(R.id.button_mp_cash);
        button_mp_tdc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResultMessage("",false);
                new MercadoPago.StartActivityBuilder()
                        .setActivity(StartMercadoPagoFlowActivity.this)
                        .setPublicKey(Utils.MY_MERCHANT_PUBLIC_KEY)
                        .setSupportedPaymentTypes(Utils.getmSupportedTDCPaymentTypes())
                        .startPaymentMethodsActivity();
            }
        });
        button_mp_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResultMessage("",false);
                new MercadoPago.StartActivityBuilder()
                        .setActivity(StartMercadoPagoFlowActivity.this)
                        .setPublicKey(Utils.MY_MERCHANT_PUBLIC_KEY)
                        .setSupportedPaymentTypes(Utils.getmSupportedCashPaymentTypes())
                        .startPaymentMethodsActivity();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case MercadoPago.PAYMENT_METHODS_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {

                    paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
                    if (paymentMethod.getPaymentTypeId().equals(this.ticket) || paymentMethod.getPaymentTypeId().equals(this.atm)) {
                        initProcess(true);
                        //register transaction initial, get id purchase and send it as parameter to create payment
                        //create paymentVO
                        CreatePaymentVO myPayment = new CreatePaymentVO();
                        myPayment.setActivity(this);
                        myPayment.setToken(data.getStringExtra("token"));
                        myPayment.setInstallments(1);
                        myPayment.setCardIssuerId(null);
                        myPayment.setPaymentMethod(JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class));
                        myPayment.setDiscount(null);
                        Long campaignId = (myPayment.getDiscount() != null) ? myPayment.getDiscount().getId() : null;
                        myPayment.setCampaign(campaignId);
                        myPayment.setPreOrder(order);
                        //create purchase and payment
                        createPurchase(myPayment);
                    }else {
                        Utils.startCardActivity(this, Utils.MY_MERCHANT_PUBLIC_KEY, paymentMethod);
                    }
                } else {
                    if ((data != null) && (data.getStringExtra("apiException") != null)) {
                        Toast.makeText(getApplicationContext(), data.getStringExtra("apiException"), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "paymentMethod ERROR:" + data.getStringExtra("apiException"));
                    }
                }
                break;
            case Utils.CARD_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    initProcess(true);
                    //register transaction initial, get id purchase and send it as parameter to create payment
                    //create paymentVO
                    CreatePaymentVO myPayment = new CreatePaymentVO();
                    myPayment.setActivity(this);
                    myPayment.setToken(data.getStringExtra("token"));
                    myPayment.setInstallments(1);
                    myPayment.setCardIssuerId(null);
                    myPayment.setPaymentMethod(JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class));
                    myPayment.setDiscount(null);
                    Long campaignId = (myPayment.getDiscount() != null) ? myPayment.getDiscount().getId() : null;
                    myPayment.setCampaign(campaignId);
                    myPayment.setPreOrder(order);
                    //create purchase and payment
                    createPurchase(myPayment);

                }else{
                    if (data != null) {
                        if (data.getStringExtra("apiException") != null) {

                            Toast.makeText(getApplicationContext(), data.getStringExtra("apiException"), Toast.LENGTH_LONG).show();

                        } else if (data.getBooleanExtra("backButtonPressed", false)) {

                            new MercadoPago.StartActivityBuilder()
                                    .setActivity(this)
                                    .setPublicKey(Utils.MY_MERCHANT_PUBLIC_KEY)
                                    .setSupportedPaymentTypes(
                                            (paymentMethod.getPaymentTypeId().equals(this.ticket) || paymentMethod.getPaymentTypeId().equals(this.atm))?Utils.getmSupportedCashPaymentTypes():Utils.getmSupportedTDCPaymentTypes())
                                    .startPaymentMethodsActivity();
                        }
                    }
                }
            break;
            case MercadoPago.CONGRATS_REQUEST_CODE:

                if (resultCode == Activity.RESULT_OK) {
                    LayoutUtil.showRegularLayout(this);
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                } if (data != null) {
                        if (data.getStringExtra("apiException") != null) {

                            Toast.makeText(getApplicationContext(), data.getStringExtra("apiException"), Toast.LENGTH_LONG).show();

                        } else if (data.getBooleanExtra("backButtonPressed", false)) {
                            LayoutUtil.showRegularLayout(this);
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                break;
        }
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

    private void createPurchase(CreatePaymentVO myPayment){
        HashMap<String,String> params = new HashMap<>();
        params.put("method","createPurchase");
        params.put("idUser",order.getIdUser());
        params.put("idPublication",order.getIdPublication());
        params.put("num_items",order.getNumItems());
        params.put("total_amount",order.getTotalAmount());
        params.put("payment_method_in_app",order.getPaymentMethod());//mercado pago=1, paypal=2, deposito oxxo=3
        params.put("product", order.getProduct());
        params.put("price", order.getPrice());
        params.put("price_unit", order.getPrice_unit());
        params.put("price_publication", order.getPrice_publication());
        params.put("iva", order.getIva());
        params.put("email_user", order.getEmail_user());
        params.put("email_publication", order.getEmail_publication());
        params.put("name_suscriptor", order.getName_suscriptor());
        params.put("phone_suscriptor", order.getPhone_suscriptor());
        params.put("name_user",order.getName_user());
        params.put("last_name_user",order.getLast_name_user());
        //mercado pago params
        params.put("payment_installments",String.valueOf(myPayment.getInstallments()));
        params.put("payment_card_issuer_id",String.valueOf(myPayment.getCardIssuerId()));
        params.put("payment_card_token",myPayment.getToken());
        params.put("payment_payment_method_id",myPayment.getPaymentMethod().getId());
        params.put("payment_payment_type_id",myPayment.getPaymentMethod().getPaymentTypeId());
        params.put("payment_campaign_id",String.valueOf(myPayment.getCampaign()));


        VolleyPostRequest(Constants.PAYMENT,params,myPayment);
    }
    public void VolleyPostRequest(String url, HashMap<String, String> params,final CreatePaymentVO myPayment) {
        JSONObject jobject = new JSONObject(params);
        Log.d(TAG, "VolleyPostRequest:" + jobject.toString());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        processPurchaseCreated(response,myPayment);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                        String mensaje2 = "Verifique su conexión a Internet.";
                        initProcess(false);
                        showConfirmationMessage(mensaje2);
                    }
                }

        ){
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

        VolleySingleton.getInstance(StartMercadoPagoFlowActivity.this).addToRequestQueue(request);
    }
    private void processPurchaseCreated(JSONObject response,CreatePaymentVO myPayment){
        Log.d(TAG,"processPurchaseCreated: "+ response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            if (status.equals("1")) {//purchase created, then check payment
                String payment_status = response.getString("payment_success");
                switch (payment_status) {
                    case "1":
                        Log.d(TAG, "Mercado pago successful !!!!");
                        //initProcess(false);
                        //showConfirmationMessage("Pago aprovado !!!");
                       /* String json_string = JsonUtil.getInstance().toJson(response.getString("created_payment"));
                        Log.d(TAG,"objet to json:"+json_string.toString());
                        Payment[] serializedPayment =  JsonUtil.getInstance().fromJson(json_string,Payment[].class);*/


                        JSONObject object = response.getJSONObject("created_payment");
                        Log.d(TAG,"created_payment to JSONObject: "+ object.toString());
                        Payment payment = createPaymentFromResponse(object);
                        if (payment!=null){
                            new MercadoPago.StartActivityBuilder()
                                    .setActivity(this)
                                    .setPayment(payment)
                                    .setPaymentMethod(paymentMethod)
                                    .startCongratsActivity();
                        }else {
                            JSONObject object_alternative = response.getJSONObject("result");
                            if (object_alternative!=null) onPaymentSuccess(object_alternative,myPayment);
                        }
                        break;
                    case "2":
                        Log.d(TAG, "2");
                        showConfirmationMessage("No aprovado");
                        initProcess(false);
                        String payment_message = response.getString("payment_message");
                        showResultMessage(payment_message,true);
                        break;
                    default:
                        Log.d(TAG, "3");
                        finish();
                        break;
                }
            }else{
                showResultMessage("Ocurrió un problema al registrar su compra, inténte nuevamente",true);
                initProcess(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();

            showConfirmationMessage("Ocurrió un problema al registrar su compra, inténte nuevamente");
            initProcess(false);

        }
        finally {
            myPayment = null;
        }

    }
    private boolean checkIfValid(String str){
        return str!=null && !str.trim().equals("") && !str.equals("null");
    }
    private void showConfirmationMessage(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(StartMercadoPagoFlowActivity.this,message,Toast.LENGTH_SHORT).show();
            }
        });
    }
    private Payment createPaymentFromResponse(JSONObject object){
        Payment payment = new Payment();
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        try {
            payment.setBinaryMode(checkIfValid(object.getString("binary_mode"))?object.getBoolean("binary_mode"):false);
            payment.setCallForAuthorizeId(object.getString("call_for_authorize_id"));
            payment.setCaptured(checkIfValid(object.getString("captured"))?object.getBoolean("captured"):false);

            payment.setCollectorId(object.getString("collector_id"));
            payment.setCouponAmount(new BigDecimal(checkIfValid(object.getString("coupon_amount"))?object.getString("coupon_amount"):"0"));
            payment.setCurrencyId(object.getString("currency_id"));
            Date date_approved = checkIfValid(object.getString("date_approved"))?StringOperations.getDate(object.getString("date_approved")):null;
            payment.setDateApproved(date_approved);
            Date date_created = checkIfValid(object.getString("date_created"))?StringOperations.getDate(object.getString("date_created")):null;
            payment.setDateCreated(date_created);
            Date date_last_updated = checkIfValid(object.getString("date_last_updated"))?StringOperations.getDate(object.getString("date_last_updated")):null;
            payment.setDateCreated(date_last_updated);
            payment.setDescription(object.getString("description"));
            payment.setDifferentialPricingId(checkIfValid(object.getString("differential_pricing_id"))?object.getLong("differential_pricing_id"):0);
            payment.setExternalReference(object.getString("external_reference"));

            FeeDetail[] feeDetails = gson.fromJson(object.getString("fee_details"),FeeDetail[].class);
            payment.setFeeDetails(feeDetails!=null?Arrays.asList(feeDetails):null);
            payment.setId(object.getLong("id"));
            payment.setInstallments(object.getInt("installments"));
            payment.setIssuerId(checkIfValid(object.getString("issuer_id"))?object.getInt("issuer_id"):null);
            payment.setLiveMode(checkIfValid(object.getString("live_mode"))?object.getBoolean("live_mode"):false);
            payment.setMetadata(new JsonObject());//not necessary
            payment.setMoneyReleaseDate(checkIfValid(object.getString("money_release_date"))?StringOperations.getDate(object.getString("money_release_date")):null);
            payment.setNotificationUrl(checkIfValid(object.getString("notification_url"))?object.getString("notification_url"):null);
            payment.setOperationType(checkIfValid(object.getString("operation_type"))?object.getString("operation_type"):null);
            Order[] order = gson.fromJson(object.getString("order"),Order[].class);
            payment.setOrder(order!=null && order.length>0?order[0]:null);
            Payer payer = gson.fromJson(object.getString("payer"),Payer.class);
            payment.setPayer(payer);
            payment.setPaymentMethodId(checkIfValid(object.getString("payment_method_id"))?object.getString("payment_method_id"):null);
            payment.setPaymentTypeId(checkIfValid(object.getString("payment_type_id"))?object.getString("payment_type_id"):null);

            if (payment.getPaymentTypeId().toString().equals(Utils.ATM) || payment.getPaymentTypeId().toString().equals(Utils.TICKET)) {
                Card[] card = gson.fromJson(object.getString("card"), Card[].class);
                payment.setCard((card!=null && card.length>0 && card[0]!=null)?card[0]:null);
            }else {
                Card card = gson.fromJson(object.getString("card"), Card.class);
                payment.setCard(card!=null?card:null);
            }


            Refund[] refund = gson.fromJson(object.getString("refunds"),Refund[].class);
            payment.setRefunds(refund!=null?Arrays.asList(refund):null);
            payment.setStatementDescriptor(checkIfValid(object.getString("statement_descriptor"))?object.getString("statement_descriptor"):null);
            payment.setStatus(checkIfValid(object.getString("status"))?object.getString("status"):null);
            payment.setStatusDetail(checkIfValid(object.getString("status_detail"))?object.getString("status_detail"):null);
            payment.setTransactionAmount(new BigDecimal(checkIfValid(object.getString("transaction_amount"))?object.getString("transaction_amount"):"0"));
            payment.setTransactionAmountRefunded(new BigDecimal(checkIfValid(object.getString("transaction_amount_refunded"))?object.getString("transaction_amount_refunded"):"0"));
            TransactionDetails trans_details = gson.fromJson(object.getString("transaction_details"),TransactionDetails.class);
            payment.setTransactionDetails(trans_details);

        } catch (JSONException e) {
            e.printStackTrace();
            return  null;
        }

        return payment;
    }
    private void payment_error(){
        initProcess(false);
       /* if (data != null) {
            if (data.getStringExtra("apiException") != null) {

                Toast.makeText(getApplicationContext(), data.getStringExtra("apiException"), Toast.LENGTH_LONG).show();
                Log.d(TAG, "CARD_REQUEST_CODE ERROR:" + data.getStringExtra("apiException"));
            } else if (data.getBooleanExtra("backButtonPressed", false)) {

                new MercadoPago.StartActivityBuilder()
                        .setActivity(this)
                        .setPublicKey(Utils.MY_MERCHANT_PUBLIC_KEY)
                        .setSupportedPaymentTypes(Utils.getmSupportedTDCPaymentTypes())
                        .startPaymentMethodsActivity();
            }
        }*/
    }
    private void onPaymentSuccess(JSONObject object,CreatePaymentVO paymentVO){
        Gson gson = new Gson();
        Purchase p = gson.fromJson(object.toString(), Purchase.class);
        if (p!=null) showConfirmationActivity(p,paymentVO);
    }
    private void showConfirmationActivity(Purchase p,CreatePaymentVO paymentVO){
        Intent intent = new Intent(getApplicationContext(), PurchaseConfirmationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle b = new Bundle();
        b.putSerializable(Constants.user_current_purchase,p);
        b.putString(StartMercadoPagoFlowActivity.MP_PAYMENT_TYPE,paymentVO.getPaymentMethod().getPaymentTypeId());
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }
}
