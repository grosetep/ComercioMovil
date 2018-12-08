package estrategiamovil.comerciomovil.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Item;
import com.mercadopago.model.MerchantPayment;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import estrategiamovil.comerciomovil.modelo.PreOrder;
import estrategiamovil.comerciomovil.modelo.Purchase;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.ui.activities.CardActivity;
import estrategiamovil.comerciomovil.ui.activities.FinalVaultActivity;
import retrofit2.Response;
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    public static final int SIMPLE_VAULT_REQUEST_CODE = 10;
    public static final int ADVANCED_VAULT_REQUEST_CODE = 11;
    public static final int FINAL_VAULT_REQUEST_CODE = 12;
    public static final int CARD_REQUEST_CODE = 13;
    /* Type payments*/
    public static final String CREDIT_CARD = "credit_card";
    public static final String DEBIT_CARD = "debit_card";
    public static final String PREPAID_CARD = "prepaid_card";
    public static final String TICKET = "ticket";
    public static final String ATM = "atm";
    // * Merchant public key
    //public static final String MY_MERCHANT_PUBLIC_KEY = "TEST-3e2e6ce2-5a68-43ee-9d36-5b8f345e131b";
    // production
    public static final String MY_MERCHANT_PUBLIC_KEY = "APP_USR-1d8c8431-67da-4ebe-a46a-de853266ed6e";

    // * Merchant server vars
    public static final String DUMMY_MERCHANT_BASE_URL = "https://www.mercadopago.com";
    public static final String DUMMY_MERCHANT_GET_CUSTOMER_URI = "/checkout/examples/getCustomer";
    public static final String DUMMY_MERCHANT_CREATE_PAYMENT_URI = "/cmovilservice/doPayment";
    public static final String MP_PAYMENTS = Constants.PUERTO_HOST + "/cmovilservice/index.php";
    //public static final String DUMMY_MERCHANT_GET_DISCOUNT_URI = "/checkout/examples/getDiscounts";

    // * Merchant access token
    //public static final String MY_MERCHANT_ACCESS_TOKEN = "mlm-cards-data";
    //public static final String MY_MERCHANT_ACCESS_TOKEN = "TEST-1049706224941432-100414-da909184affb64c8664c646a64d41d47__LC_LD__-103161623";
    //production
    public static final String MY_MERCHANT_ACCESS_TOKEN = "APP_USR-1049706224941432-100414-fbfcd9a4816ec459c774cf2aeeac3ba1__LD_LA__-103161623";

    // * Payment item
    public static final String DUMMY_ITEM_ID = "id1";
    public static final Integer DUMMY_ITEM_QUANTITY = 1;
    public static final BigDecimal DUMMY_ITEM_UNIT_PRICE = new BigDecimal("100");

    public static void startCardActivity(Activity activity, String merchantPublicKey, PaymentMethod paymentMethod) {
        Log.d(TAG,"startCardActivity.....");
        Intent cardIntent = new Intent(activity, CardActivity.class);
        cardIntent.putExtra("merchantPublicKey", merchantPublicKey);
        cardIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        activity.startActivityForResult(cardIntent, CARD_REQUEST_CODE);
    }


    public static void startFinalVaultActivity(Activity activity, String merchantPublicKey, String merchantBaseUrl, String merchantGetCustomerUri, String merchantAccessToken, BigDecimal amount, List<String> supportedPaymentTypes) {

        Intent finalVaultIntent = new Intent(activity, FinalVaultActivity.class);
        finalVaultIntent.putExtra("merchantPublicKey", merchantPublicKey);
        finalVaultIntent.putExtra("merchantBaseUrl", merchantBaseUrl);
        finalVaultIntent.putExtra("merchantGetCustomerUri", merchantGetCustomerUri);
        finalVaultIntent.putExtra("merchantAccessToken", merchantAccessToken);
        finalVaultIntent.putExtra("amount", amount.toString());
        putListExtra(finalVaultIntent, "supportedPaymentTypes", supportedPaymentTypes);
        activity.startActivityForResult(finalVaultIntent, FINAL_VAULT_REQUEST_CODE);
    }

    public static void createPayment(final Activity activity,final CreatePaymentVO myPayment) {

        if (myPayment.getPaymentMethod()!= null) {

            LayoutUtil.showProgressLayout(activity);

            // Set item
            Item item = new Item(DUMMY_ITEM_ID, DUMMY_ITEM_QUANTITY,
                    DUMMY_ITEM_UNIT_PRICE);
           /* BigDecimal unit_price = new BigDecimal(myPayment.getPreOrder().getTotalAmount());
            Log.d(TAG,"DUMMY_ITEM_ID:"+ myPayment.getPurchase().getCapture_line() + " DUMMY_ITEM_QUANTITY:"+Integer.parseInt(myPayment.getPreOrder().getNumItems())+" DUMMY_ITEM_UNIT_PRICE:"+unit_price);
            Item item = new Item(myPayment.getPurchase().getCapture_line(), Integer.parseInt(myPayment.getPreOrder().getNumItems()),
                    unit_price);*/


            // Set payment method id
            String paymentMethodId = myPayment.getPaymentMethod().getId();

            // Set campaign id
            Long campaignId = (myPayment.getDiscount() != null) ? myPayment.getDiscount().getId() : null;

            // Set merchant payment
            MerchantPayment payment = new MerchantPayment(item, myPayment.getInstallments(), myPayment.getCardIssuerId(),
                    myPayment.getToken(), paymentMethodId, campaignId, MY_MERCHANT_ACCESS_TOKEN);

            // Create payment
            ErrorHandlingCallAdapter.MyCall<Payment> call = MerchantServer.createPayment(activity, DUMMY_MERCHANT_BASE_URL, DUMMY_MERCHANT_CREATE_PAYMENT_URI, payment);
            call.enqueue(new ErrorHandlingCallAdapter.MyCallback<Payment>() {
                @Override
                public void success(Response<Payment> response) {
                    //update success status on transaction by id purchase
                    new MercadoPago.StartActivityBuilder()
                            .setActivity(activity)
                            .setPayment(response.body())
                            .setPaymentMethod(myPayment.getPaymentMethod())
                            .startCongratsActivity();
                }

                @Override
                public void failure(ApiException apiException) {

                    LayoutUtil.showRegularLayout(activity);
                    if (apiException!=null) {
                        Toast.makeText(activity, apiException.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, " apiException.getMessage(): " + apiException.getMessage());
                        ApiUtil.finishWithApiException(activity, apiException);
                        //update fail status on transaction by id purchase
                    }
                }
            });
        } else {

            Toast.makeText(activity, "Invalid payment method", Toast.LENGTH_LONG).show();
        }
    }

    private static void putListExtra(Intent intent, String listName, List<String> list) {

        if (list != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>(){}.getType();
            intent.putExtra(listName, gson.toJson(list, listType));
        }
    }

    protected static List<String> mSupportedPaymentTypes = new ArrayList<String>(){{
        add(CREDIT_CARD);
        add(DEBIT_CARD);
        add(PREPAID_CARD);
        add(TICKET);
        add(ATM);
    }};
    protected static List<String> mSupportedCashPaymentTypes = new ArrayList<String>(){{
        add(TICKET);
        add(ATM);
    }};
    protected static List<String> mSupportedTDCPaymentTypes = new ArrayList<String>(){{
        add(CREDIT_CARD);
        add(DEBIT_CARD);
        add(PREPAID_CARD);
    }};

    public static List<String> getmSupportedCashPaymentTypes() {
        return mSupportedCashPaymentTypes;
    }

    public static List<String> getmSupportedTDCPaymentTypes() {
        return mSupportedTDCPaymentTypes;
    }

    public static List<String> getmSupportedPaymentTypes() {
        return mSupportedPaymentTypes;
    }
}
