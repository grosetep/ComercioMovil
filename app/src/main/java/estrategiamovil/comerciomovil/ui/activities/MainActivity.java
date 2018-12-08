package estrategiamovil.comerciomovil.ui.activities;



import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import estrategiamovil.comerciomovil.R;

import estrategiamovil.comerciomovil.modelo.LoginResponse;
import estrategiamovil.comerciomovil.modelo.Section;
import estrategiamovil.comerciomovil.modelo.SuscriptionDetail;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Connectivity;
import estrategiamovil.comerciomovil.tools.Constants;

import estrategiamovil.comerciomovil.tools.FireBaseOperations;
import estrategiamovil.comerciomovil.tools.ImagePicker;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.tools.StringOperations;
import estrategiamovil.comerciomovil.tools.UserLocalProfile;
import estrategiamovil.comerciomovil.tools.UserSuscription;
import estrategiamovil.comerciomovil.tools.UtilPermissions;
import estrategiamovil.comerciomovil.ui.fragments.AdsFragment;
import estrategiamovil.comerciomovil.ui.fragments.CardsFragment;

import estrategiamovil.comerciomovil.ui.fragments.EmergentFragment;
import estrategiamovil.comerciomovil.ui.fragments.FindBusinessFragment;
import estrategiamovil.comerciomovil.ui.fragments.ProductsFragment;
import estrategiamovil.comerciomovil.ui.interfaces.DialogCallbackInterface;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DialogCallbackInterface {
    private DrawerLayout drawer;
    private Section[] sections;
    private String idUser;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private LoginResponse login;
    //private FloatingActionButton fav_business;
    private FloatingActionButton fav_purchases;
    private FloatingActionButton fav_favorites;
    private FloatingActionButton fav_orders;
    private FloatingActionButton fav_publishing;
    private FloatingActionButton fav_customers;
    private FloatingActionButton fav_admin;
    private FloatingActionMenu   fav_menu;
    private View mCustomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!UtilPermissions.hasPermissions(getApplicationContext(), PERMISSIONS)){
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, UtilPermissions.PERMISSION_ALL);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setContentInsetsAbsolute(0,8);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(
                getSupportActionBar().DISPLAY_SHOW_CUSTOM,
                getSupportActionBar().DISPLAY_SHOW_CUSTOM |  getSupportActionBar().DISPLAY_SHOW_HOME
                        |  getSupportActionBar().DISPLAY_SHOW_TITLE);

        LayoutInflater mInflater = LayoutInflater.from(this);
        mCustomView = mInflater.inflate(R.layout.actionbar_custom_view_search, null);
        mCustomView.findViewById(R.id.layout_search).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Done"
                        Intent intent = new Intent(v.getContext(), SearchActivity.class);
                        startActivity(intent);
                    }
                });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);
        setTabsTitle();
        loadProfile();

        fav_admin = (FloatingActionButton) findViewById(R.id.fav_admin);
        fav_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent i = new Intent(context,AdministratorActivity.class);
                context.startActivity(i);
            }
        });
        fav_favorites = (FloatingActionButton) findViewById(R.id.fav_favorites);
        fav_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Context context = v.getContext();
                    Intent i = new Intent(context, FavoritesActivity.class);
                    context.startActivity(i);
            }
        });
        /*fav_business = (FloatingActionButton) findViewById(R.id.fav_business);
        fav_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent i = new Intent(context,FindMyBusinessActivity.class);
                context.startActivity(i);
            }
        });*/
        fav_purchases = (FloatingActionButton) findViewById(R.id.fav_purchases);
        fav_orders = (FloatingActionButton) findViewById(R.id.fav_orders);
        fav_customers = (FloatingActionButton) findViewById(R.id.fav_customers);
        fav_customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                if((UserSuscription.getSuscriptionDetails(getApplicationContext())).getValid().equals("true")) {
                    Intent i = new Intent(context, ManageCustomersActivity.class);
                    context.startActivity(i);
                }else{
                    Intent i = new Intent(context, SuscriptionActivity.class);
                    context.startActivity(i);
                }
            }
        });
        fav_orders = (FloatingActionButton) findViewById(R.id.fav_orders);
        fav_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                if((UserSuscription.getSuscriptionDetails(getApplicationContext())).getValid().equals("true")) {
                    Intent i = new Intent( context , SalesActivity.class);
                    context.startActivity(i);
                }else{
                    Intent i = new Intent(context, SuscriptionActivity.class);
                    context.startActivity(i);
                }



            }
        });
        fav_purchases = (FloatingActionButton) findViewById(R.id.fav_purchases);
        fav_purchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent i = new Intent( context , PurchasesActivity.class);
                i.putExtra(PurchasesActivity.PURCHASE_FLOW,"false");
                context.startActivity(i);
            }
        });
        fav_publishing = (FloatingActionButton) findViewById(R.id.fav_publishing);
        fav_publishing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                if((UserSuscription.getSuscriptionDetails(getApplicationContext())).getValid().equals("true")) {
                    Intent i = new Intent(context,ManagePublicationsActivity.class);
                    startActivity(i);
                }else{
                    Intent i = new Intent(context, SuscriptionActivity.class);
                    context.startActivity(i);
                }
            }
        });

        fav_menu = (FloatingActionMenu) findViewById(R.id.fav_menu);
        loadUserMenuFavs();
        //subscribeUser();
        idUser = ApplicationPreferences.getLocalStringPreference(getBaseContext(),Constants.localUserId);
        if (idUser!=null && !idUser.equals("")){
            if (checkConnection())
                getSuscription(idUser);
        }

        FireBaseOperations.subscribeUser(getApplicationContext(),true);
        //show first message tip only the first time
        String first_time = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.isFirstTimeUser);
        if (first_time.equals("")){
            ShowConfirmations.ShowNotification_Popup(this,getString(R.string.text_welcome),getString(R.string.promt_show_tip_firsttime),Constants.isFirstTimeUser);
        }else{
            ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.isFirstTimeUser,"false");
        }

        //show first message tip only for new user subscriber new users
        String signup_suscriber_first_time = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.showTipSignupSuscriber);
        if (signup_suscriber_first_time.equals(String .valueOf(true))){
            ShowConfirmations.ShowNotification_Popup_Hightlight(this,getString(R.string.text_welcome),getString(R.string.promt_show_tip_subscriber_firsttime),Constants.showTipSignupSuscriber);
        }else{
            ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.showTipSignupSuscriber,String.valueOf(false));
        }

    }

    /*
    * */
    private void getSuscription(String idUser){
        HashMap<String,String> params = new HashMap<>();
        params.put("method","getSuscription");
        params.put("idUser",idUser);
        VolleyPostRequest(Constants.SUSCRIPTIONS,params);
    }
    private void getEmergentTitle(){
        VolleyGetRequest(Constants.GET_SECTIONS+"?method=getEmergent");
    }
    public void VolleyGetRequest(String url){
        Log.d(TAG, "VolleyGetRequest MainActivity:" + url);
        VolleySingleton.
                getInstance(MainActivity.this).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                (String) null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        processingResponse(response);

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        String message = "Verifique su conexión a internet.";
                                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                                        Toast.makeText(
                                                MainActivity.this,
                                                message,
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }

                        ).setRetryPolicy(new DefaultRetryPolicy(
                                Constants.MY_SOCKET_TIMEOUT_MS,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                );
    }
    private void processingResponse(JSONObject response) {
        // Log.d(TAG, "Emergent::"+response.toString());
        try {

            String status = response.getString("status");
            switch (status) {
                case "1":
                    JSONObject mensaje = response.getJSONObject("section");
                    Gson gson = new Gson();
                    Section emergent = gson.fromJson(mensaje.toString(), Section.class);
                    //Log.d(TAG,"Emergent: "+ emergent.getIdSection() + emergent.getSection());
                    //ApplicationPreferences.saveLocalPreference(MainActivity.this,Constants.emergentSection,emergent.getSection());
                    checkChangesOnTitles(emergent);
                    break;
                case "2": // FALLIDO

                    break;

            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());

        }

    }
    public void VolleyPostRequest(String url, final HashMap<String, String> args){
        JSONObject jobject = new JSONObject(args);
        Log.d(TAG,"VolleyPostRequest:" +  jobject.toString());

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                processResponse(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }


                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }
    private void processResponse(JSONObject response){
        Log.d(TAG, "getSuscription:"+response.toString());
        try {
            String status = response.getString("status");
            switch (status) {
                case "1":
                    Gson gson = new Gson();
                    JSONObject object = response.getJSONObject("result");
                    SuscriptionDetail detail = gson.fromJson(object.toString(),SuscriptionDetail.class);
                    if (detail!=null){
                        UserSuscription.saveSuscriptionDetails(getApplicationContext(),detail);
                        Log.d(TAG,"Consultar Suscripcion..."+ detail.getValid() + " , "+  detail.getMessage());
                    }
                    break;
                case "2":
                    Log.d(TAG,"Error al consultar Suscripcion...");

                    break;

                default:
                    break;

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"Error al consultar Suscripcion...");
        }
    }
    /* private void subscribeUser(){
         String firebase_token = ApplicationPreferences.getLocalStringPreference(MainActivity.this,Constants.firebase_token);
         if (firebase_token!=null){
         // [START subscribe_topics]
         Set<String> topics = ApplicationPreferences.getLocalSetPreference(MainActivity.this,Constants.topicsUser);
         if (topics!=null){//subscribe to each topic
             Iterator iter = topics.iterator();
             while (iter.hasNext()){
                 String topic = (String)iter.next();
                 Log.d(TAG,"subscribe to Topic:" + topic);
                 FirebaseMessaging.getInstance().subscribeToTopic(topic);
             }

         }
         Log.d(TAG,"subscribe to Topic:" + Constants.topicEmergent);
         FirebaseMessaging.getInstance().subscribeToTopic(Constants.topicEmergent);
             // [END subscribe_topics]
         }else{
             Log.d(TAG,"User already subscribed to emergent  :)");
         }

     }*/
    /*
    fav_purchases
    fav_orders
    fav_customers
    fav_orders
    fav_publishing
     */
    private void loadUserMenuFavs(){
        if (fav_menu!=null) {
            if (login != null) {//user registered

                if (login.getUserType().contains("usuario")) {//available options:"establecimientos,mis compras,favoritos
                    fav_menu.removeMenuButton(fav_publishing);
                    fav_menu.removeMenuButton(fav_orders);
                    fav_menu.removeMenuButton(fav_customers);
                } else if (login.getUserType().contains("suscriptor")) {//available options:"mis ventas,publicar,mis clientes,mis compras,favoritos,proveedores
                    if (!login.getStatus().equals("activo")) {
                        //desactivar optiones hasta que se active su usuario
                        fav_menu.removeMenuButton(fav_publishing);
                        fav_menu.removeMenuButton(fav_orders);
                        fav_menu.removeMenuButton(fav_customers);
                    }
                }
                if (login.getAdministrator().equals("0")){fav_menu.removeMenuButton(fav_admin);}

            }else{//guess user, available options:"establecimientos,mis compras"
                fav_menu.removeMenuButton(fav_publishing);
                fav_menu.removeMenuButton(fav_orders);
                fav_menu.removeMenuButton(fav_customers);
                fav_menu.removeMenuButton(fav_favorites);
                fav_menu.removeMenuButton(fav_admin);
            }
        }
    }
    private void loadProfile(){
        Log.d(TAG,"loadProfile--->");
        if (ApplicationPreferences.getLocalStringPreference(MainActivity.this, Constants.localUserId)!=null &&
                !ApplicationPreferences.getLocalStringPreference(MainActivity.this,Constants.localUserId).equals("")){
            //usuario ya logueado, recuperar info
            login = UserLocalProfile.getUserProfile(MainActivity.this);
            if (login!=null){
                Log.d(TAG,"Carga datos login:::::::::::::::::::::::::::::"+login.toString());
                updateNavigationView(login);
            }
        }else{//cierre de session, no existe idUser
            Log.d(TAG,"Usuario no logueado valor ID local: ");
            updateNavigationView(null);

        }
    }
    private void updateNavigationView(LoginResponse login) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        if (hView != null) {
            TextView text_name_user = (TextView) hView.findViewById(R.id.text_name_user);
            TextView text_email_user = (TextView) hView.findViewById(R.id.text_email_user);
            ImageView image_profile = (ImageView) hView.findViewById(R.id.image_profile);
            text_name_user.setText((login!=null)?login.getName() + " " + login.getFirst() + " " + login.getLast():getResources().getString(R.string.text_user_guess));
            text_email_user.setText((login!=null)?login.getEmail():getResources().getString(R.string.text_email_guess));
            if (login!=null) {
                loadProfileImage(login, image_profile);
            }else {
                Glide.with(image_profile.getContext())
                        .load(R.drawable.ic_account_circle)
                        .into(image_profile);
            }

        }
    }
    private void loadProfileImage(LoginResponse login,ImageView image_profile){
        String avatarlocalPath = ApplicationPreferences.getLocalStringPreference(MainActivity.this, Constants.localAvatarPath);
        Log.d(TAG, "Caomparacion de avatars: avatarlocalpath:" + avatarlocalPath + "remoteImageName: "+login.getAvatarPath()+" ,remote_image "+login.getRemoteImage());
        if (avatarlocalPath!=null && avatarlocalPath.length()>0){//algun usuario ya se ha logueado
            File file = new File(avatarlocalPath);
            if(file.exists()){//load file from local
                Log.d(TAG,"load file from local");
                Glide.with(image_profile.getContext())
                        .load(new File(avatarlocalPath))
                        .error(R.drawable.ic_account_circle)
                        .into(image_profile);
            }else{//load file from remote path
                //String nameImageInServer = login.getIdUsuario();
                Log.d(TAG,"load file from remote path");
                Glide.with(image_profile.getContext())
                        .load(Constants.GET_PROFILE_IMAGE + login.getRemoteImage())
                        .error(R.drawable.ic_account_circle)
                        .into(image_profile);
            }
        }else{//usuario nuevo ingreso
            Glide.with(image_profile.getContext())
                    .load(R.drawable.ic_account_circle)
                    .into(image_profile);
        }
        //image_profile.setImageBitmap(bitmap);
    }
    public  HashMap<String, String>  getParametersRecommendedSection(){
        HashMap<String, String> params = new HashMap<>();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String idCountry = mPrefs.getString(Constants.countryUser, "");
        String idCity = mPrefs.getString(Constants.cityUser,"");
        String listCategories= mPrefs.getString(Constants.categoriesUser,"");
        String nameCity = mPrefs.getString(Constants.nameCityUser,"Cambiar ciudad...");
        params.put("idCountry", idCountry);
        params.put("idCity", idCity);
        params.put("listCategories", listCategories);
        params.put("cityName",nameCity);
        params.put(Constants.search_flow,"false");
        return params;
    }
    private  HashMap<String, String>  getParametersProductsSection(){
        HashMap<String, String> params = new HashMap<>();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        params.put("idSubCategory", "");
        return params;
    }

    //todos los tabs son estaticos menos el ultimo que se maneja para temporadas
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(CardsFragment.createInstance(getParametersRecommendedSection()), "RECOMENDADOS");
        adapter.addFragment(ProductsFragment.createInstance(getParametersProductsSection()), "PRODUCTOS");
        adapter.addFragment(EmergentFragment.createInstance(null), "DE TEMPORADA");
        adapter.addFragment(AdsFragment.createInstance(null), "CLASIFICADOS");//new PhotoListFragment()
        adapter.addFragment(FindBusinessFragment.createInstance(), "PROVEEDORES");//new PhotoListFragment()
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);//fragments in memory
    }
    private void setTabsTitle() {
        String names = ApplicationPreferences.getLocalStringPreference(getApplicationContext(), Constants.sectionNames);
        JSONArray  object = null;
        try {
            object = new JSONArray (names);
            Gson gson = new Gson();
            sections =  gson.fromJson(object.toString(), Section[].class);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (sections != null && sections.length > 0){

            ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
            int tabsCount = vg.getChildCount();
            for (int j = 0; j < tabsCount; j++) {
                ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
                int tabChildsCount = vgTab.getChildCount();
                for (int i = 0; i < tabChildsCount; i++) {
                    View tabViewChild = vgTab.getChildAt(i);
                    if (tabViewChild instanceof TextView) {
                        if (j<=sections.length) {//evitar Index exception
                            ((TextView) tabViewChild).setText((sections[j]).getSection());
                        }
                    }
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.searchable_menu, menu);
        //final MenuItem item = menu.findItem(R.id.menu_search);
        //final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        // searchView.setOnQueryTextListener(new CardsFragment.listener());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_preferences) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            // start singup activity
            startActivityForResult(
                    new Intent(this, LoginActivity.class), 1);
        }
        else if (id == R.id.nav_club) {
            startActivityForResult(
                    new Intent(this, SuscriptionActivity.class), 2);
        }
        else if (id == R.id.nav_settings) {
            startActivityForResult(
                    new Intent(this, SettingsActivity.class), 3);
        }
        else if (id == R.id.nav_private) {
            startActivityForResult(
                    new Intent(this, ShowConditionsActivity.class), 4);
        }
        else if (id == R.id.nav_help) {
            startActivityForResult(
                    new Intent(this, HelpActivity.class), 4);
        }
        else if (id == R.id.nav_exit) {
            startActivityForResult(new Intent(this,SignOutActivity.class),6);
        }else if (id == R.id.nav_profile) {
            startActivityForResult(new Intent(this,ProfileActivity.class),7);
        }else if (id == R.id.home){
            drawer.openDrawer(GravityCompat.START);
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void method_positive() {
        if (Connectivity.isNetworkAvailable(getApplicationContext())){
            getSuscription(idUser);
        }else {
            String flag_dialog = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.CONNECTIVITY_DIALOG_SHOWED);
            if (flag_dialog.equals("0"))
                new ShowConfirmations(this, this).openRetry();
        }
    }

    @Override
    public void method_negative(Activity activity) {
        Log.d(TAG,"MainActivity NEGATIVE..");
    }
    private boolean checkConnection(){
        if (Connectivity.isNetworkAvailable(getApplicationContext())){
            return true;
        }else {
            String flag_dialog = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.CONNECTIVITY_DIALOG_SHOWED);
            if (flag_dialog.equals("0")){ new ShowConfirmations(this,this).openSimple();return false;}
        }
        return false;
    }
    static class Adapter extends FragmentStatePagerAdapter  {
        //FragmentPagerAdapter : Mantiene datos en memoria, destruye fragment cuando no son visibles.
        //FragmentStatePagerAdapter: El fragment se destruye uy solo se guarda su estado, es como listview pero con fragments
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();
        /*private int[] imageResId = { //lista de iconos para tablayout
                R.drawable.ic_star_border,
                R.drawable.ic_star_border,
                R.drawable.ic_star_border,
                R.drawable.ic_star_border
        };*/
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

            /*Drawable image = ContextCompat.getDrawable(getContext(), imageResId[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            //SpannableString sb = new SpannableString(" "); // solo iconos
            //SpannableString sb = new SpannableString(" " + mFragmentTitles.get(position));//iconos + texto
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;*/
        }
    }

    /*    @Override
        protected void onPause() {
            super.onPause();
            finish();
        }
    */
    @Override
    protected void onResume(){
        //Log.d(TAG,"onResume...............................MainActivity");
        getEmergentTitle();
        //AQUI
        super.onResume();
    }

    private void checkChangesOnTitles(Section section){
        Log.d(TAG,"checkChangesOnTitles");
        String names = ApplicationPreferences.getLocalStringPreference(getApplicationContext(), Constants.sectionNames);
        JSONArray  object = null;
        try {
            object = new JSONArray (names);
            Gson gson = new Gson();
            sections =  gson.fromJson(object.toString(), Section[].class);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (sections != null && sections.length > 0) {
            for(Section s:sections){
                //Log.d(TAG,"Valores a comparar: actual: " + s.getSection() + " con: " + section.getSection());
                if (s.getEmergent().equals("1") && !section.getSection().equals(s.getSection())){Log.d(TAG,"Entra a condicion..");
                    //update Title
                    ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
                    int tabsCount = vg.getChildCount();
                    for (int j = 0; j < tabsCount; j++) {
                        ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
                        int tabChildsCount = vgTab.getChildCount();
                        for (int i = 0; i < tabChildsCount; i++) {
                            View tabViewChild = vgTab.getChildAt(i);
                            if (tabViewChild instanceof TextView) {
                                if (j<=sections.length) {//evitar Index exception
                                    if (((TextView) tabViewChild).getText().equals(s.getSection())) {
                                        Log.d(TAG,"Se actualiza titulo anterior: " + ((TextView) tabViewChild).getText() + " A: " + section.getSection());
                                        ((TextView) tabViewChild).setText(section.getSection());
                                        //actualizar en preferencias el nuevo titulo tambien
                                        (sections[j]).setSection(section.getSection());
                                        JSONArray jsonArray = StringOperations.getJSONArrayFromArray(sections);
                                        ApplicationPreferences.saveLocalPreference(MainActivity.this,Constants.sectionNames,jsonArray.toString());

                                    }
                                }
                            }
                        }
                    }//end for
                }
            }
        }
    }

}
