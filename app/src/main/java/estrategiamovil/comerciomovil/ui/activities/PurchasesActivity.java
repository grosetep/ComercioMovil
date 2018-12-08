package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Filter;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.ui.fragments.PurchasesFragment;
import estrategiamovil.comerciomovil.ui.fragments.SalesFragment;

public class PurchasesActivity extends AppCompatActivity {
    private ProgressBar loading;
    private static final String TAG = PurchasesActivity.class.getSimpleName();
    public static final String PURCHASE_FLOW = "purchase_flow";
    private String flow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_purchases);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        flow = i.getStringExtra(PurchasesActivity.PURCHASE_FLOW);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_purchases, PurchasesFragment.newInstance(), "PurchasesFragment")
                    .commit();

        }
    }

    private boolean returnToMainActivity(){
        Intent intent = new Intent(PurchasesActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if ("false".equals(flow)) {
                    onBackPressed();
                }else {
                    Intent i = new Intent( getApplicationContext() , MainActivity.class);
                    startActivity(i);
                    finish();
                }
                return true;
            case R.id.action_filter:
                Intent intent = new Intent(PurchasesActivity.this, FilterActivity.class);
                startActivityForResult(intent, SalesActivity.METHOD_FILTER);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filters, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        switch(requestCode) {
            case SalesActivity.METHOD_FILTER:
                if (resultCode == Activity.RESULT_OK){
                    Filter filter = (Filter) data.getSerializableExtra(FilterActivity.FILTER);
                    PurchasesFragment fragment = (PurchasesFragment) getSupportFragmentManager().findFragmentByTag("PurchasesFragment");
                    if (fragment!=null){
                        fragment.filter(filter, Constants.cero, Constants.load_more_tax,true,false);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
