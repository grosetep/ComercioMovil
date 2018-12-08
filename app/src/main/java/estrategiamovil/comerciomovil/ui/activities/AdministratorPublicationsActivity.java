package estrategiamovil.comerciomovil.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.ui.fragments.AdministratorPublicationsFragment;

public class AdministratorPublicationsActivity extends AppCompatActivity {
    private static final String TAG = AdministratorPublicationsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator_publications);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_admin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        if (savedInstanceState == null) {
            AdministratorPublicationsFragment fragment = new AdministratorPublicationsFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_admin ,fragment, "AdministratorPublicationsFragment")
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
