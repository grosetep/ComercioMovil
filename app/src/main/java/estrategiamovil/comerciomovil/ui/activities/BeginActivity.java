package estrategiamovil.comerciomovil.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.ui.fragments.BeginFragment;
import estrategiamovil.comerciomovil.ui.fragments.CountryFragment;

public class BeginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);
        if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.containerBegin, new BeginFragment(), "BeginFragment")
                        .commit();
            }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CountryPreferencesActivity.class);
        startActivity(intent);
    }
}
