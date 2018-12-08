package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.ui.fragments.UpgradeFragment;

public class UpgradeActivity extends AppCompatActivity {
    private static final String TAG = UpgradeActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_upgrade);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.upgrade_container, UpgradeFragment.newInstance(), "UpgradeFragment")
                    .commit();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Finish the registration screen and return to the Login activity
                setResult(Activity.RESULT_CANCELED);
                finish();
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
