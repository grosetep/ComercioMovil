package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import com.github.clans.fab.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.UploadImage;
import estrategiamovil.comerciomovil.ui.fragments.ManagePublicationsFragment;
import estrategiamovil.comerciomovil.ui.fragments.PublishFragment;

public class ManagePublicationsActivity extends AppCompatActivity {
    private FloatingActionButton fab_new_publication;
    private static final int REQUEST_NEW_PUBLICATION = 1;
    private ProgressBar loading;
    private static final String TAG = ManagePublicationsActivity.class.getSimpleName();

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_publications);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_manage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab_new_publication = (FloatingActionButton) findViewById(R.id.fab_new_publication);
        fab_new_publication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent i = new Intent(context, PublishActivity.class);
                startActivityForResult(i,REQUEST_NEW_PUBLICATION);

            }
        });

        if (savedInstanceState == null) {
            ManagePublicationsFragment fragment = new ManagePublicationsFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_publications, fragment, "ManagePublicationsFragment")
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult Fragment------->requestCode:" + requestCode);
        switch(requestCode) {
            case REQUEST_NEW_PUBLICATION:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "Exito publicacion..");
                    ManagePublicationsFragment fragment = (ManagePublicationsFragment)
                            getSupportFragmentManager().findFragmentByTag("ManagePublicationsFragment");

                    fragment.reload();//hasta que acaben de subir todas las imagenes...
                    if (progressDialog!=null && progressDialog.isShowing())
                        progressDialog.hide();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
    private void createProgressDialog(String msg){
        progressDialog = new ProgressDialog(ManagePublicationsActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

}
