package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.mercadopago.Utils;
import estrategiamovil.comerciomovil.modelo.DetailPublication;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.ui.fragments.ReviewPayFragment;

public class ReviewPayActivity extends AppCompatActivity {
    private static final String TAG = ReviewPayActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_pay);

        Intent intent = this.getIntent();
        Bundle bundle=intent.getExtras();
        DetailPublication detail =(DetailPublication) bundle.getSerializable(Constants.user_cupon_selected);
        String path = intent.getStringExtra(ReviewPayFragment.EXTRA_PATH_FIRST_IMAGE);
        String name = intent.getStringExtra(ReviewPayFragment.EXTRA_NAME_FIRST_IMAGE);

        Log.d(TAG," Datos recibidos: " + path +  name + " detail:" + detail.getCoverDescription() +" id= "+ detail.getIdPublication());
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_review);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //load data
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_review, ReviewPayFragment.createInstance(detail,path,name), "ReviewPayFragment")
                    .commit();

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


}
