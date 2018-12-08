package estrategiamovil.comerciomovil.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.ui.fragments.ConfirmDialogFragment;
import estrategiamovil.comerciomovil.ui.fragments.DatePickerFragment;
import estrategiamovil.comerciomovil.ui.fragments.PublishFragment;

public class PublishActivity extends AppCompatActivity implements DatePickerFragment.OnDateSelectedListener{
    private static final String TAG = PublishActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_publish);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //load data
        PublishFragment fragment = (savedInstanceState == null)?new PublishFragment():
                (PublishFragment)getSupportFragmentManager().findFragmentByTag("PublishFragment");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_new_publication, fragment, "PublishFragment")
                .commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                open();
                return true;
            case R.id.action_reset:
                PublishFragment fragment = (PublishFragment)
                        getSupportFragmentManager().findFragmentByTag("PublishFragment");

                if (fragment != null) {
                    fragment.confirmAction(getResources().getString(R.string.prompt_confirm_clean),getResources().getString(R.string.prompt_discart_promt),getResources().getString(R.string.prompt_action_discart),getResources().getString(R.string.prompt_cancel),PublishFragment.ALERT_DISCART);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDateSelected(int year, int month, int day) {
        Log.d(TAG,"fecha: " + year + " / " + month + " / " + day);
        PublishFragment fragment = (PublishFragment)
                getSupportFragmentManager().findFragmentByTag("PublishFragment");

        if (fragment != null) {
            fragment.actualizarFecha(year, month, day);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public void open(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.prompt_discart_promt_title));
        alertDialogBuilder.setTitle(getString(R.string.prompt_discart_promt));
        alertDialogBuilder.setPositiveButton(getString(R.string.prompt_action_discart), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton(getString(R.string.prompt_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(LocationsActivity.this, "You clicked yes button", Toast.LENGTH_LONG).show();

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    @Override
    public void onBackPressed() {
        open();
    }
}
