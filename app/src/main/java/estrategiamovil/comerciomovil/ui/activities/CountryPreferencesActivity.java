package estrategiamovil.comerciomovil.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.ui.fragments.CountryFragment;
import estrategiamovil.comerciomovil.ui.fragments.SplashFragment;

public class CountryPreferencesActivity extends AppCompatActivity {
    private static final String TAG = CountryPreferencesActivity.class.getSimpleName();
    private CountryFragment countryFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCountries);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.containerCountries, new CountryFragment(), "CountryFragment")
                    .commit();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK || resultCode == 203) {
            countryFragment = (CountryFragment) getSupportFragmentManager().
                    findFragmentByTag("CountryFragment");

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchable_menu, menu);
        final MenuItem item = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new CountryFragment.listener());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, BeginActivity.class);
                this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
