package estrategiamovil.comerciomovil.ui.activities;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import estrategiamovil.comerciomovil.R;

import estrategiamovil.comerciomovil.ui.fragments.FindBusinessFragment;

public class FindMyBusinessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_my_business);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_business);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            FindBusinessFragment fragment = new FindBusinessFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_business, fragment, "FindBusinessFragment")
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchable_menu, menu);
        final MenuItem item = menu.findItem(R.id.menu_search);
        item.setTitle(getResources().getString(R.string.promt_search));
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new FindBusinessFragment.listener());
        return super.onCreateOptionsMenu(menu);
    }

}
