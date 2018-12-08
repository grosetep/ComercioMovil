package estrategiamovil.comerciomovil.ui.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.DetailPublication;
import estrategiamovil.comerciomovil.modelo.LoginResponse;
import estrategiamovil.comerciomovil.modelo.PaymentReview;
import estrategiamovil.comerciomovil.modelo.PreOrder;
import estrategiamovil.comerciomovil.modelo.Purchase;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.StringOperations;
import estrategiamovil.comerciomovil.tools.UserLocalProfile;
import estrategiamovil.comerciomovil.ui.activities.LoginActivity;
import estrategiamovil.comerciomovil.ui.activities.StartMercadoPagoFlowActivity;
import estrategiamovil.comerciomovil.ui.activities.PurchaseConfirmationActivity;
import estrategiamovil.comerciomovil.ui.activities.SignupActivity;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewPayFragment extends Fragment {
    private static final String TAG = ReviewPayFragment.class.getSimpleName();
    //intent
    private DetailPublication detail;
    private String path_first_image;
    private String name_first_image;

    //containers
    private LinearLayout layout_product;


    //GUI
    private TextView text_label_total;
    private LinearLayout button_mercado_pago;
    private LinearLayout button_oxxo;
    private RelativeLayout layout_general;
    private ProgressBar pbLoading_review;
    private AppCompatButton button_confirm;
    private ImageButton button_add;
    private ImageButton button_less;
    private TextView text_quantity;
    private ProgressBar loading;
    private ImageView image;
    private TextView text_coverDescription;
    private TextView text_price;
    private TextView text_iva;
    private TextView text_total;
    private TextView text_name_client;
    private TextView text_email_client;
    private CheckBox checkbox_mp;
    //private CheckBox checkbox_mp_tdc;
    private CheckBox checkbox_oxxo;
    private int num_items = 1;
    private float total = 0;
    public final static int MERCADO_PAGO_PAYMENT =1;
    public final static int PAYPAL =2;
    public final static int DEPOSIT_OXXO =3;
    private static int PAY_PAL_TDC =4;
    private int type_payment = 0;
    public static final String EXTRA_PATH_FIRST_IMAGE = "path_first_image";
    public static final String EXTRA_NAME_FIRST_IMAGE = "name_first_image";
    /* preOrder */
    public static final String PRE_ORDER = "pre_order";
    private final int METHOD_PAYMENT_EMAIL = 1;
    private final int METHOD_PAYMENT = 2;



    public ReviewPayFragment() {
    }

    public static ReviewPayFragment createInstance(DetailPublication detail, String path, String name) {
        ReviewPayFragment fragment = new ReviewPayFragment();
        Bundle b = new Bundle();
        b.putSerializable(Constants.user_cupon_selected, detail);
        b.putString(ReviewPayFragment.EXTRA_NAME_FIRST_IMAGE, name);
        b.putString(ReviewPayFragment.EXTRA_PATH_FIRST_IMAGE, path);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            detail = (DetailPublication) getArguments().getSerializable(Constants.user_cupon_selected);
            path_first_image = getArguments().getString(ReviewPayFragment.EXTRA_PATH_FIRST_IMAGE);
            name_first_image = getArguments().getString(ReviewPayFragment.EXTRA_NAME_FIRST_IMAGE);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_review_pay, container, false);
        pbLoading_review = (ProgressBar) v.findViewById(R.id.pbLoading_review);
        layout_general = (RelativeLayout) v.findViewById(R.id.layout_general);
        layout_product = (LinearLayout) v.findViewById(R.id.layout_product);
        loading = (ProgressBar) v.findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        text_label_total = (TextView) v.findViewById(R.id.text_label_total);
        button_mercado_pago = (LinearLayout) v.findViewById(R.id.button_mercado_pago);
        button_oxxo = (LinearLayout) v.findViewById(R.id.button_oxxo);
        button_confirm = (AppCompatButton) v.findViewById(R.id.button_confirm);
        loading = (ProgressBar) v.findViewById(R.id.loading);
        image = (ImageView) v.findViewById(R.id.image);
        text_coverDescription = (TextView) v.findViewById(R.id.text_coverDescription);
        text_price = (TextView) v.findViewById(R.id.text_price);
        text_iva = (TextView) v.findViewById(R.id.text_iva);
        text_total = (TextView) v.findViewById(R.id.text_total);
        text_quantity = (TextView) v.findViewById(R.id.text_quantity);
        text_quantity.setText(""+num_items);
        text_name_client = (TextView) v.findViewById(R.id.text_name_client);
        text_email_client = (TextView) v.findViewById(R.id.text_email_client);
        button_add = (ImageButton) v.findViewById(R.id.button_add);
        button_less = (ImageButton) v.findViewById(R.id.button_less);
        //checks
        checkbox_mp = (CheckBox) v.findViewById(R.id.checkbox_mp);
        checkbox_oxxo = (CheckBox) v.findViewById(R.id.checkbox_oxxo);

        button_mercado_pago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox_mp.isChecked())
                    checkbox_mp.setChecked(false);
                else {
                    type_payment = MERCADO_PAGO_PAYMENT;
                    checkbox_mp.setChecked(true);
                    checkbox_oxxo.setChecked(false);
                }
            }
        });

        button_oxxo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox_oxxo.isChecked())
                    checkbox_oxxo.setChecked(false);
                else {
                    type_payment = DEPOSIT_OXXO;
                    checkbox_oxxo.setChecked(true);
                    checkbox_mp.setChecked(false);
                }
                //sendPaymentEmail();

            }
        });
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (detail.getAvailability()!=null && num_items+1<=Integer.parseInt(detail.getAvailability()))
                num_items = num_items + 1;
                updateAmount();
                text_quantity.setText(""+num_items);
            }
        });

        button_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num_items==1){
                    return;
                }else {
                    num_items = num_items - 1;
                    text_quantity.setText("" + num_items);
                    updateAmount();
                }
            }
        });

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //si selecciono pago en oxxo directo, crear compra(metodo_pago,total,id_publicacion,num items)
                //validate type payment
                String idUser = ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.localUserId);
                if ( idUser != null && !idUser.equals("")  ){//user logged
                    if (!checkbox_oxxo.isChecked() && !checkbox_mp.isChecked()){//
                        Toast.makeText(getActivity(), getResources().getString(R.string.text_promt_method_payment_required), Toast.LENGTH_SHORT).show();
                    }else{//process payment request
                        if (type_payment==DEPOSIT_OXXO){
                            initProcess(true);
                            createPurchase(getPreOrder(idUser));
                        }else{
                            Log.d(TAG,"Total::::::::::::::::::::::::::::::::::::::::::::::"+getPreOrder(idUser).getTotalAmount());
                            Intent intent = new Intent(getActivity(), StartMercadoPagoFlowActivity.class);
                            Bundle args = new Bundle();
                            args.putSerializable(ReviewPayFragment.PRE_ORDER,getPreOrder(idUser));
                            intent.putExtras(args);
                            startActivity(intent);
                        }
                    }
                }else{//login request
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, Constants.REQUEST_SIGNUP);
                }
            }
        });
        if (detail != null) {
            LoginResponse loginResponse = UserLocalProfile.getUserProfile(getContext());
            Log.d(TAG, "detail: " + detail.getCompany());
            text_label_total.setText(StringOperations.getAmountFormat(detail.getOfferPrice()));

            StringBuffer url = new StringBuffer();
            url.append(Uri.parse(path_first_image + name_first_image).buildUpon().build().toString());

            Log.d(TAG, "URL image: " + url.toString());

            Glide.with(getContext())
                    .load(url.toString())
                    .centerCrop()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            loading.setVisibility(View.GONE);
                            layout_product.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .into(image);
            text_coverDescription.setText(detail.getCoverDescription());
            text_price.setText(StringOperations.getAmountFormat("" + (Float.parseFloat(detail.getOfferPrice()) - (Float.parseFloat(detail.getOfferPrice()) * 0.15))));
            text_iva.setText(StringOperations.getAmountFormat("" + Float.parseFloat(detail.getOfferPrice()) * 0.15));
            text_total.setText(StringOperations.getAmountFormat(detail.getOfferPrice()));
            if (loginResponse!=null){
                text_name_client.setText(loginResponse.getName() + " "+loginResponse.getFirst() + " "+loginResponse.getLast());
                text_email_client.setText(loginResponse.getEmail());
            }else{
                text_name_client.setText(getResources().getString(R.string.text_user_guess));
                text_email_client.setText("");
            }
        } else {
            Log.d(TAG, "detail: " + null);
            Toast.makeText(getContext(), "Ocurrió un error, intente nuevamente", Toast.LENGTH_SHORT);
        }

        return v;
    }
    private PreOrder getPreOrder(String idUser){
        LoginResponse login = UserLocalProfile.getUserProfile(getContext());
        PreOrder order = new PreOrder();
        order.setIdUser(idUser);
        order.setIdMerchant(detail.getIdMerchant());
        order.setIdPublication(detail.getIdPublication());
        order.setNumItems(String.valueOf(num_items));
        order.setTotalAmount(String.valueOf( (int) (Float.parseFloat(detail.getOfferPrice())*num_items)));
        order.setPaymentMethod(String.valueOf(type_payment));
        order.setProduct(detail.getCoverDescription());
        order.setPrice( "" + (Float.parseFloat(detail.getOfferPrice()) - (Float.parseFloat(detail.getOfferPrice()) * 0.15))* num_items);
        order.setPrice_unit(String.valueOf(Float.parseFloat(detail.getOfferPrice()) - (Float.parseFloat(detail.getOfferPrice()) * 0.15)));
        order.setPrice_publication(detail.getOfferPrice());
        order.setIva("" + (Float.parseFloat(detail.getOfferPrice()) * 0.15 * num_items));
        order.setEmail_user(login.getEmail());
        order.setEmail_publication(detail.getEmail());
        order.setName_suscriptor(detail.getCompany());
        order.setPhone_suscriptor(detail.getPhone());
        order.setName_user(login.getName());
        order.setLast_name_user(login.getFirst() + " "+login.getLast());
        return order;
    }

    private void initProcess(boolean flag){
        layout_general.setVisibility((flag)?View.GONE:View.VISIBLE);
        pbLoading_review.setVisibility((flag)?View.VISIBLE:View.GONE);
    }
    private void updateAmount(){

        float amount_initial = Float.parseFloat(detail.getOfferPrice());
        total = amount_initial * num_items;
        double iva = 0.16 * total;
        text_label_total.setText(StringOperations.getAmountFormat(""+total));
        text_price.setText(StringOperations.getAmountFormat(""+(total-iva)));
        text_iva.setText(StringOperations.getAmountFormat(""+iva));
        text_total.setText(StringOperations.getAmountFormat(""+total));
    }
    /*  private void sendPaymentEmail() {

          HashMap<String, String> params = new HashMap<>();
          params.put("method", "sendOrder");
          params.put("product", detail.getCoverDescription());
          params.put("num_items", String.valueOf(num_items));
          params.put("price", "" + (Float.parseFloat(detail.getOfferPrice()) - (Float.parseFloat(detail.getOfferPrice()) * 0.15)));
          params.put("iva", "" + "" + Float.parseFloat(detail.getOfferPrice()) * 0.15);
          params.put("total", detail.getOfferPrice());//generar fecha y fecha vencimiento en servidor, registrar compra...
          params.put("email_user", UserLocalProfile.getUserProfile(getContext()).getEmail());
          params.put("email_publication", detail.getEmail());
          params.put("name_suscriptor", detail.getCompany());
          params.put("name_user",UserLocalProfile.getUserProfile(getContext()).getName());
          VolleyPostRequest(Constants.EMAIL, params, METHOD_PAYMENT_EMAIL);
      }*/
    private void createPurchase(PreOrder order){
        HashMap<String,String> params = new HashMap<>();
        params.put("method","createPurchase");
        params.put("idUser",order.getIdUser());
        params.put("idMerchant",order.getIdMerchant());
        params.put("idPublication",order.getIdPublication());
        params.put("num_items",order.getNumItems());
        params.put("total_amount",order.getTotalAmount());
        params.put("payment_method_in_app",order.getPaymentMethod());
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

        VolleyPostRequest(Constants.PAYMENT,params,METHOD_PAYMENT);
    }
    public void VolleyPostRequest(String url, HashMap<String, String> params, final int callback) {
        JSONObject jobject = new JSONObject(params);
        Log.d(TAG, "VolleyPostRequest:" + jobject.toString());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar la respuesta del servidor
                        switch (callback){
                           /* case METHOD_PAYMENT_EMAIL:
                                //showConfirmationMessage(getResources().getString(R.string.prompt_email_sent));
                                Intent intent = new Intent(getActivity(), PurchaseConfirmationActivity.class);
                                Bundle args = new Bundle();
                                PaymentReview review = new PaymentReview();
                                args.putSerializable(REVIEW_PAYMENT,review);
                                intent.putExtras(args);
                                startActivityForResult(intent, 1);
                                break;*/
                            case METHOD_PAYMENT:
                                processPurchaseCreated(response);
                                break;

                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                        //dismissProgressDialog();
                        String mensaje2 = "Verifique su conexión a Internet.";
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

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }
    private void showConfirmationMessage(final String message){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showConfirmationActivity(Purchase p){
        Intent intent = new Intent(getContext(), PurchaseConfirmationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle b = new Bundle();
        b.putSerializable(Constants.user_current_purchase,p);
        intent.putExtras(b);
        getActivity().finish();
        startActivity(intent);

    }
    private void processPurchaseCreated(JSONObject response){
        Log.d(TAG,"processPurchaseCreated: "+ response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");

            switch (status) {
                case "1":
                    JSONObject object = response.getJSONObject("result");
                    Gson gson = new Gson();
                    Purchase p = gson.fromJson(object.toString(), Purchase.class);
                    Log.d(TAG,object.getString("message"));
                    if (p!=null)
                        showConfirmationActivity(p);
                    else
                        Log.d(TAG,"No se creo purchase...");
                    break;
                case "2":
                    showConfirmationMessage(response.getString("message").toString());
                    Log.d(TAG,response.getString("message").toString());
                    initProcess(false);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                showConfirmationMessage(response.getString("message").toString());
                initProcess(false);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

    }
}
