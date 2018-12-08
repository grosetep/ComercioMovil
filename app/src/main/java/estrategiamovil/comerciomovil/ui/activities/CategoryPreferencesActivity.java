package estrategiamovil.comerciomovil.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import estrategiamovil.comerciomovil.R;

import estrategiamovil.comerciomovil.modelo.NotificationTopic;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.VolleyErrorHelper;
import estrategiamovil.comerciomovil.ui.fragments.CategoryFragment;
import estrategiamovil.comerciomovil.ui.fragments.PublishFragment;
import estrategiamovil.comerciomovil.web.VolleySingleton;


public class CategoryPreferencesActivity extends AppCompatActivity {
    CategoryFragment categoryFragment;
    private ProgressBar loading;
    private FrameLayout container;
    public static final String SETTINGS_FLOW = "settings_flow";
    private String flow_type = null;
    private static final String TAG = CategoryPreferencesActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        loading = (ProgressBar) findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        container = (FrameLayout) findViewById(R.id.container);
        Intent intent = getIntent();
        flow_type = intent.getStringExtra(SETTINGS_FLOW);
        Log.d(TAG,"settings_flow:"+flow_type);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Creación del fragmento principal
        if (savedInstanceState == null) {
            categoryFragment = new CategoryFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, categoryFragment, "CategoryFragment")
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_ok:
                if (categoryFragment !=null){
                    container.setVisibility(View.GONE);
                    loading.setVisibility(View.VISIBLE);
                    savePreferences();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void loadTopicsByCategory(String categories) {//obtener datos de la publicacion mas datos de las url de imagenes

        // Petición GET
        String url = Constants.GET_CATEGORIES+"?method=getTopicsByCategories&categories="+categories;
        JsonObjectRequest request =  new JsonObjectRequest(Request.Method.GET,
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
                        String message = VolleyErrorHelper.getErrorType(error, CategoryPreferencesActivity.this);
                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                        Toast.makeText(
                                CategoryPreferencesActivity.this,
                                message,
                                Toast.LENGTH_SHORT).show();
                        errorFlow();
                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleySingleton.
                getInstance(CategoryPreferencesActivity.this).
                addToRequestQueue(
                        request
                );
    }
    private void processingResponse(JSONObject response) {
        Log.d(TAG, "processingResponse topics:" + response);
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("result");
                    // Parsear con Gson
                    Gson gson = new Gson();
                    NotificationTopic[] topics = gson.fromJson(mensaje.toString(), NotificationTopic[].class);
                    Set<String> topicsToSubscribe = new HashSet<String>();

                    if (topics!=null){
                        for (NotificationTopic t:topics) {
                            Log.d(TAG, t.getIdTopic() + "-" + t.getTopic() + "-" + t.getIdCategory());
                            topicsToSubscribe.add(t.getTopic());
                        }
                        Log.d(TAG,"save topics...");
                        ApplicationPreferences.saveLocalSetPreference(CategoryPreferencesActivity.this,Constants.topicsUser,topicsToSubscribe);
                    }
                    cotinueFlow();
                    break;
                case "2": // FALLIDO
                    Log.d(TAG,response.getString("message"));
                    String mensaje2 = "Verifique su conexion de Internet y vuela a intentar.";
                    Toast.makeText(
                            CategoryPreferencesActivity.this,
                            mensaje2,
                            Toast.LENGTH_LONG).show();
                    errorFlow();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            errorFlow();
        }

    }
    private void errorFlow(){
        container.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
    }
    private void cotinueFlow(){Log.d(TAG,"settings_flow:"+flow_type);
        if (flow_type!=null && flow_type.equals(SETTINGS_FLOW)){     Log.d(TAG,"finish.....");
            finish();
        }else{//continue to MainActivity
            Log.d(TAG,"To MainActivity.....");
            CategoryPreferencesActivity.this.startActivityForResult(
                    new Intent(CategoryPreferencesActivity.this, MainActivity.class), 3);
        }
    }
    public void savePreferences(){
        String idUser = ApplicationPreferences.getLocalStringPreference(CategoryPreferencesActivity.this,Constants.localUserId);
        StringBuffer categoryPreferences = new StringBuffer();
        CategoryFragment fragment = (CategoryFragment)
                getSupportFragmentManager().findFragmentByTag("CategoryFragment");

        if (fragment != null) {
            Iterator it = fragment.getmAdapter().getCategoriesSelected().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                categoryPreferences.append(e.getValue() + ",");
            }
            if (categoryPreferences.length() > 0) {
                categoryPreferences = new StringBuffer(categoryPreferences.subSequence(0, categoryPreferences.length() - 1));
                Log.d(TAG, "categorias seleccionadas: " + categoryPreferences.toString());
                ApplicationPreferences.saveLocalPreference(CategoryPreferencesActivity.this,Constants.categoriesUser,categoryPreferences.toString());


                if (idUser != null && !idUser.trim().equals("")) {
                    updateCategoriesUserPreferences(categoryPreferences.toString(), idUser);
                }else {
                    loadTopicsByCategory(ApplicationPreferences.getLocalStringPreference(CategoryPreferencesActivity.this,Constants.categoriesUser));
                    //cotinueFlow();
                }

            } else {
                Toast.makeText(CategoryPreferencesActivity.this, "Seleccione una o màs categorias", Toast.LENGTH_SHORT);

            }
        }
    }

    private void updateCategoriesUserPreferences(String categories, String iduser){
        VolleyGetRequest(Constants.GET_CATEGORIES + "?method=savePreferences&idUser=" + iduser+"&categories="+categories );
    }
    public void VolleyGetRequest(String url){
        VolleySingleton.
                getInstance(CategoryPreferencesActivity.this).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                (String) null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Procesar la respuesta Json
                                        processResponse(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                                        Toast.makeText(CategoryPreferencesActivity.this, "Hay un problema, intenta mas tarde...", Toast.LENGTH_SHORT);
                                        errorFlow();
                                    }
                                }

                        )
                );
    }

    private void processResponse(JSONObject response) {
        try {
            Log.d(TAG,"response: " + response.toString());
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    //update notifications subscription by topic selected

                    Log.d(TAG,"Save preferences ok");
                    cotinueFlow();
                    break;
                case "2": // FALLIDO
                    String mensaje2 = response.getString("message");
                    Toast.makeText(
                            CategoryPreferencesActivity.this,
                            mensaje2,
                            Toast.LENGTH_SHORT).show();
                    errorFlow();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            errorFlow();
        }

    }
}
