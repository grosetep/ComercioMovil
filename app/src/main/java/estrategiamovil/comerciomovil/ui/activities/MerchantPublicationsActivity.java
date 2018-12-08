package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.BusinessInfo;
import estrategiamovil.comerciomovil.ui.fragments.AdsFragment;
import estrategiamovil.comerciomovil.ui.fragments.BusinessInfoFragment;
import estrategiamovil.comerciomovil.ui.fragments.ManagePublicationsFragment;
import estrategiamovil.comerciomovil.ui.fragments.MerchantContactFragment;
import estrategiamovil.comerciomovil.ui.fragments.MerchantListPublicationsFragment;
import estrategiamovil.comerciomovil.ui.fragments.RatingFragment;

public class MerchantPublicationsActivity extends AppCompatActivity {
    public static final String MERCHANT_OBJECT = "merchant";
    private static final String TAG = MerchantPublicationsActivity.class.getSimpleName();
    private BusinessInfo business;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageView header_imageview_merchant;
    private TextView text_bussiness_name;
    private TextView text_bussiness_category;
    private TextView text_bussiness_state;
    private CircleImageView image_profile_merchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_publications);
        Intent intent = getIntent();
        business = (BusinessInfo) intent.getSerializableExtra(MERCHANT_OBJECT);
        Log.d(TAG,"Object recibido negocio:"+business.toString());
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_merchant);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        initGUI();
        initHeaders();


    }
    private void initGUI(){
        header_imageview_merchant = (ImageView) findViewById(R.id.header_imageview_merchant);
        text_bussiness_name = (TextView) findViewById(R.id.text_bussiness_name);
        text_bussiness_category = (TextView) findViewById(R.id.text_bussiness_category);
        text_bussiness_state = (TextView) findViewById(R.id.text_bussiness_state);
        image_profile_merchant = (CircleImageView) findViewById(R.id.image_profile_merchant);
    }
    private void initHeaders(){
        String avatar = business.getImageRoute() + business.getNameImage();
        if (business!=null){
            Glide.with(getApplicationContext())
                    .load(avatar)
                    .into(header_imageview_merchant);
            Glide.with(getApplicationContext())
                    .load(avatar)
                    .into(image_profile_merchant);
            text_bussiness_name.setText(business.getCompany());
            text_bussiness_category.setText(business.getCategory());
            text_bussiness_state.setText(business.getState());
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
    //todos los tabs son estaticos menos el ultimo que se maneja para temporadas
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(MerchantListPublicationsFragment.newInstance(business), "PUBLICACIONES");
        adapter.addFragment(MerchantContactFragment.newInstance(business), "CONTACTO");
        adapter.addFragment(BusinessInfoFragment.getInstance(business.getBusinessDescription()),"QUIENES SOMOS");
        adapter.addFragment(RatingFragment.newInstance(business),"REPUTACIÓN");
        viewPager.setAdapter(adapter);

    }
    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position); //Solo texto en tabs
        }
    }
}
