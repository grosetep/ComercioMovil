package estrategiamovil.comerciomovil.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Purchase;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.StringOperations;

public class PurchaseConfirmationActivity extends AppCompatActivity {
    private static final String TAG = PurchaseConfirmationActivity.class.getSimpleName();
    private TextView text_total;
    private TextView text_method;
    private TextView text_operation;
    private TextView text_date;
    private TextView text_continue;
    private AppCompatButton button_purchases;
    private LinearLayout layout_receipt;
    private TextView text_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_confirmation);
        Intent intent = this.getIntent();
        Bundle bundle=intent.getExtras();
        final Purchase purchase =(Purchase) bundle.getSerializable(Constants.user_current_purchase);
        String mp_payment_type = (String) bundle.getString(StartMercadoPagoFlowActivity.MP_PAYMENT_TYPE);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_confirm);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layout_receipt = (LinearLayout) findViewById(R.id.layout_receipt);
        layout_receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(purchase.getResourceUrl()));
                startActivity(i);
            }
        });
        text_info = (TextView) findViewById(R.id.text_info);
        text_total = (TextView) findViewById(R.id.text_total);
        text_method = (TextView) findViewById(R.id.text_method);
        text_operation = (TextView) findViewById(R.id.text_operation);
        text_date = (TextView) findViewById(R.id.text_date);
        text_continue = (TextView) findViewById(R.id.text_continue);
        button_purchases = (AppCompatButton) findViewById(R.id.button_purchases);
        button_purchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent i = new Intent( context , PurchasesActivity.class);
                i.putExtra(PurchasesActivity.PURCHASE_FLOW,String.valueOf(true));
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                context.startActivity(i);
                finish();
            }
        });
        if (purchase!=null){
            text_total.setText(StringOperations.getAmountFormat(purchase.getTransaction_amount()) + " M/N");
            text_method.setText(purchase.getTransaction_method());
            text_operation.setText(purchase.getCapture_line() + " - " + purchase.getTransaction_status());
            text_date.setText(purchase.getTransaction_date());
            //check type payment for showing elements
            if (purchase.getTransaction_method_id().equals("3")){//direct_deposit
                layout_receipt.setVisibility(View.GONE);
            }else if(purchase.getTransaction_method_id().equals("1")){//mercado pago
                if (mp_payment_type!=null){
                    if (mp_payment_type.equals(StartMercadoPagoFlowActivity.ticket) || mp_payment_type.equals(StartMercadoPagoFlowActivity.atm)){
                        text_info.setText(getResources().getString(R.string.text_promt_info_purchase_1_2));
                    }else{
                        layout_receipt.setVisibility(View.GONE);//don't show for cards
                        text_info.setText(getResources().getString(R.string.text_promt_info_purchase_1_1));
                    }
                }
            }
        }
        text_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PurchaseConfirmationActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean returnToMainActivity(){
        Intent intent = new Intent(PurchaseConfirmationActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                returnToMainActivity();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        returnToMainActivity();
    }
}
