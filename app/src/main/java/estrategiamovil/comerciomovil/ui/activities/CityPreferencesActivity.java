package estrategiamovil.comerciomovil.ui.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.google.gson.Gson;

import java.util.HashMap;
import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.ui.fragments.CityFragment;

public class CityPreferencesActivity extends AppCompatActivity {
    public static final String EXTRA_NAME = "idCountry";
    public static final String EXTRA_FLOW = "labelFlow";
    public static final String EXTRA_COUNTRY = "country";

    HashMap<String, String> params;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        Intent intent = getIntent();
        final String idCountry = intent.getStringExtra(EXTRA_NAME);
        final String labelFlow = intent.getStringExtra(EXTRA_FLOW);
        final String labelCountry = intent.getStringExtra(EXTRA_COUNTRY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCities);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        params = new HashMap<>();// Mapeo previo
        params.put("idCountry", idCountry);
        params.put("labelFlow", labelFlow);
        params.put("labelCountry",labelCountry);

        if (savedInstanceState == null) {
           // CityFragment fragment = new CityFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.containerCities, CityFragment.createInstance(params), "CityFragment")
                    .commit();
           // fragment.setParams(params);
        }



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchable_menu, menu);
        final MenuItem item = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new CityFragment.listener());
        return super.onCreateOptionsMenu(menu);
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
