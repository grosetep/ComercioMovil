package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Ask;
import estrategiamovil.comerciomovil.modelo.Filter;
import estrategiamovil.comerciomovil.modelo.LoginResponse;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.UserLocalProfile;
import estrategiamovil.comerciomovil.ui.fragments.PurchasesFragment;
import estrategiamovil.comerciomovil.ui.fragments.SalesFragment;

public class SalesActivity extends AppCompatActivity {
    private static final String TAG = SalesActivity.class.getSimpleName();
    public static final int METHOD_FILTER = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sales);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_sales, SalesFragment.newInstance(), "SalesFragment")
                    .commit();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_filter:
                Intent intent = new Intent(SalesActivity.this, FilterActivity.class);
                startActivityForResult(intent, METHOD_FILTER);
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
            case METHOD_FILTER:
                if (resultCode == Activity.RESULT_OK){
                    Filter filter = (Filter) data.getSerializableExtra(FilterActivity.FILTER);
                    SalesFragment fragment = (SalesFragment) getSupportFragmentManager().findFragmentByTag("SalesFragment");
                    if (fragment!=null){
                        fragment.filter(filter,Constants.cero, Constants.load_more_tax,true,false);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
