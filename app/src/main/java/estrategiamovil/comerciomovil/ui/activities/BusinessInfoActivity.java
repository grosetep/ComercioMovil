package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.ui.fragments.BusinessInfoFragment;

public class BusinessInfoActivity extends AppCompatActivity {

    private static final String TAG = BusinessInfoActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_info);
        Intent i = getIntent();
        String info = i.getStringExtra(BusinessInfoFragment.INFO_COMPANY);
        if (savedInstanceState == null) {


                getSupportFragmentManager().beginTransaction()
                        .add(R.id.info_container, BusinessInfoFragment.getInstance(info) , "BusinessInfoFragment")
                        .commit();

        }

    }
}
