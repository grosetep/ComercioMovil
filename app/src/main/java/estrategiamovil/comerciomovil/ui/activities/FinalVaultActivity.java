package estrategiamovil.comerciomovil.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;

import estrategiamovil.comerciomovil.R;

public class FinalVaultActivity extends AdvancedVaultActivity {
    private static final String TAG = FinalVaultActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    protected void setContentView() {

        setContentView(R.layout.activity_final_vault);
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    protected void resolvePaymentMethodsRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            // Set selection status
            mTempIssuer = null;
            mTempPaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);

            if (MercadoPagoUtil.isCardPaymentType(mTempPaymentMethod.getPaymentTypeId())) {  // Card-like methods

                if (mTempPaymentMethod.isIssuerRequired()) {

                    // Call issuer activity
                    startIssuersActivity();

                } else {

                    // Call new card activity
                    startNewCardActivity();
                }
            } else {  // Off-line methods
                Log.d(TAG,"Off-line methods");
                // Set selection status
                mPayerCosts = null;
                mCardToken = null;
                mSelectedPaymentMethodRow = null;
                mSelectedPayerCost = null;
                mSelectedPaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
                mSelectedIssuer = null;
                Log.d(TAG,mSelectedPaymentMethod.getName());
                // Set customer method selection
                mCustomerMethodsText.setText(mSelectedPaymentMethod.getName());
                mCustomerMethodsText.setCompoundDrawablesWithIntrinsicBounds(MercadoPagoUtil.getPaymentMethodIcon(mActivity, mSelectedPaymentMethod.getId()), 0, 0, 0);

                // Set security card visibility
                mSecurityCodeCard.setVisibility(View.GONE);

                // Set installments visibility
                mInstallmentsCard.setVisibility(View.GONE);
                Log.d(TAG,"setEnabled(true)--------------");
                // Set button visibility
                mSubmitButton.setEnabled(true);
            }
        } else {

            if ((data != null) && (data.getStringExtra("apiException") != null)) {
                finishWithApiException(data);
            } else if ((mSelectedPaymentMethodRow == null) && (mCardToken == null)) {
                // if nothing is selected
                finish();
            }
        }
    }

    @Override
    public void submitForm(View view) {
        Log.d(TAG,"submitForm... ");
        LayoutUtil.hideKeyboard(mActivity);

        // Validate installments
        if (((mSelectedPaymentMethodRow != null) || (mCardToken != null)) && mSelectedPayerCost == null) {
            return;
        }
        Log.d(TAG,"1... ");
        // Create token
        if (mSelectedPaymentMethodRow != null) {
            Log.d(TAG,"2... ");
            createSavedCardToken();

        } else if (mCardToken != null) {
            Log.d(TAG,"3... ");
            createNewCardToken();

        } else {  // Off-line methods
            Log.d(TAG,"4...Off-line methods ");
            // Return payment method id
            LayoutUtil.showRegularLayout(mActivity);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mSelectedPaymentMethod));
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }
}
