package estrategiamovil.comerciomovil.ui.activities;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;




import java.util.HashMap;



import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.ui.fragments.DetailAdsFragment;
import estrategiamovil.comerciomovil.ui.fragments.DetailPublicationFragment;

public class DetailPublicationActivity extends AppCompatActivity {

    public static final String EXTRA_PUBLICACION = "idPublication";
    public static final String EXTRA_TITLE = "coverDescription";
    public static final String EXTRA_IMAGETITLE = "imagePath";
    public static final String EXTRA_TYPE_PUBLICATION = "typePublication";
    private static final String TAG = DetailPublicationActivity.class.getSimpleName();
    private String type = "";
    private FloatingActionButton fav_add_favorite;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        final String idPublication = intent.getStringExtra(EXTRA_PUBLICACION);
        final String company = intent.getStringExtra(EXTRA_TITLE);
        final String imagePath = intent.getStringExtra(EXTRA_IMAGETITLE);
        type = intent.getStringExtra(EXTRA_TYPE_PUBLICATION);

        fav_add_favorite = (FloatingActionButton) findViewById(R.id.fav_add_favorite);
        fav_add_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    DetailPublicationFragment detail = (DetailPublicationFragment) getSupportFragmentManager().
                            findFragmentByTag("DetailPublicationFragment");
                    if(detail!=null) detail.addToCart();

            }
        });

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(company);

        //loadBackdrop(imagePath);
        HashMap<String, String> params = new HashMap<>();// Mapeo previo
        params.put("idPublication", idPublication);
        params.put("imageFirstPath",imagePath);

        if (savedInstanceState == null) {

            if (type.equals(Constants.type_publicacion_cupons)) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.containerDetailPublication, DetailPublicationFragment.createInstance(params), "DetailPublicationFragment")
                        .commit();
            }else{
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.containerDetailPublication, DetailAdsFragment.createInstance(params), "DetailAdsFragment")
                        .commit();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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
        if (type.equals(Constants.type_publicacion_cupons)) {
            DetailPublicationFragment fragment = (DetailPublicationFragment) getSupportFragmentManager().
                    findFragmentByTag("DetailPublicationFragment");
            fragment.loadInformation();
        }else{
            DetailAdsFragment fragment = (DetailAdsFragment) getSupportFragmentManager().
                    findFragmentByTag("DetailAdsFragment");
            fragment.loadInformation();
        }





    }
   /*
    @Override
    public void onPageScrollStateChanged(int state) {
        Log.d(TAG, "onPageScrollStateChanged...");
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
       /* for (int i = 0; i < viewPager.getChildCount(); i++) {
            View cardView = viewPager.getChildAt(i);
            int itemPosition = (Integer) cardView.getTag();

            if (itemPosition == position) {
                cardView.setScaleX(MAX_SCALE - positionOffset / 7f);
                cardView.setScaleY(MAX_SCALE - positionOffset / 7f);
            }

            if (itemPosition == (position + 1)) {
                cardView.setScaleX(MIN_SCALE + positionOffset / 7f);
                cardView.setScaleY(MIN_SCALE + positionOffset / 7f);
            }
        }*/
   /* }*/
/*
    @Override
    public void onPageSelected(int position) {

    } */


}
