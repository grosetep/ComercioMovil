package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Category;
import estrategiamovil.comerciomovil.modelo.Section;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Connectivity;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.ui.fragments.PublishFragment;
import estrategiamovil.comerciomovil.ui.fragments.SplashFragment;
import estrategiamovil.comerciomovil.ui.interfaces.DialogCallbackInterface;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class StartActivity extends AppCompatActivity implements DialogCallbackInterface {
    SharedPreferences mPrefs;
    final String welcomeScreenShownPref = "welcomeScreenShown";
    private Section[] sections;
    private Gson gson = new Gson();
    Boolean welcomeScreenShown = false;
    private static final String TAG = StartActivity.class.getSimpleName();
    Bundle savedInstanceState;
    //private final int REQUEST_CATEGORY_ADS = 1;
    private final int REQUEST_GET_SECTIONS = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_start);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        welcomeScreenShown = mPrefs.getBoolean(welcomeScreenShownPref, false);
        if (Connectivity.isNetworkAvailable(getApplicationContext()))
            getSections();
        else
            new ShowConfirmations(StartActivity.this,this).openRetry();

    }
    public void VolleyGetRequest(String url, final int callback){
        Log.d(TAG, "VolleyGetRequest start:" + url);
        VolleySingleton.
                getInstance(StartActivity.this).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                (String) null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Procesar la respuesta Json
                                        switch(callback){
                                            //case REQUEST_CATEGORY_ADS:processResponse(response);break;
                                            case REQUEST_GET_SECTIONS:processingResponse(response);break;
                                            default:break;
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        if (StartActivity.this !=null)
                                            new ShowConfirmations(StartActivity.this,StartActivity.this).openRetry();

                                    }
                                }

                        ).setRetryPolicy(new DefaultRetryPolicy(
                                Constants.MY_SOCKET_TIMEOUT_MS,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                );
    }
    /*private void getCategoryAds(){
        VolleyGetRequest(Constants.GET_CATEGORIES+"?method=getCategoriesByDescription&description="+Constants.PROVIDERS_LABEL,REQUEST_CATEGORY_ADS);
    }*/
    /*private void processResponse(JSONObject response) {
        Log.d(TAG, "response category ads:" + response.toString());
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONObject mensaje = response.getJSONObject("result");
                    Category category = gson.fromJson(mensaje.toString(), Category.class);
                    Log.d(TAG,"Category Ads:"  + category.getIdCategory() + " " + category.getCategory() );
                    if (category!=null){//save to preferences
                        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.local_idCategory_Ads,category.getIdCategory());
                    }
                    init();

                    break;
                case "2": // FALLIDO
                    String mensaje2 = response.getString("message");
                    open();
                    break;
                case "3": // FALLIDO
                    String mensaje3 = response.getString("message");
                    open();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            open();
        }

    }*/
    private void getSections(){
        VolleyGetRequest(Constants.GET_SECTIONS+"?method=getSections",REQUEST_GET_SECTIONS);

    }
    public void open(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.no_connection));
        //alertDialogBuilder.setTitle("Descartar Cambios");
        alertDialogBuilder.setPositiveButton("INTENTAR DE NUEVO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                getSections();
            }
        });

        alertDialogBuilder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(LocationsActivity.this, "You clicked yes button", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
    private void processingResponse(JSONObject response) {
        //Log.d(TAG, "Sections::"+response.toString());
        try {

            String status = response.getString("status");
            switch (status) {
                case "1":
                    JSONArray mensaje = response.getJSONArray("sections");
                    sections = gson.fromJson(mensaje.toString(), Section[].class);
                    JSONArray array = new JSONArray();
                    for(Section s:sections) {
                        //tabsNames.add(s.getSection());
                        JSONObject o = new JSONObject();
                        o.put("idSection",s.getIdSection());
                        o.put("section",s.getSection());
                        o.put("emergent",s.getEmergent());
                        array.put(o);
                    }
                    JSONObject sections = new JSONObject();
                    sections.put("sections", array);
                    saveTabsNamesApplication(array);
                    //getCategoryAds();
                    init();
                    break;
                case "2": // FALLIDO
                    String mensaje2 = response.getString("message");
                    Log.d(TAG,".fail:" + mensaje2);
                    open();
                    break;
                case "3": // FALLIDO
                    String mensaje3 = response.getString("message");
                    Log.d(TAG,".fail:" + mensaje3);
                    open();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            open();
        }

    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
    private void saveTabsNamesApplication(JSONArray sections){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(Constants.sectionNames, sections.toString());
        editor.commit();
    }
    private void init(){
        if (!welcomeScreenShown) {
           /* if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.splash, new SplashFragment(), "SplashFragment")
                        .commit();
            }*/
            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(2 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {

                        Intent intent = new Intent(StartActivity.this, CountryPreferencesActivity.class);
                        startActivity(intent);
                    }
                }
            };
            timerThread.start();
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean(welcomeScreenShownPref, true);
            editor.commit(); // Very important to save the preference
        }else{//start MainActivity
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void method_positive() {
        if (Connectivity.isNetworkAvailable(getApplicationContext())){
            getSections();
        }else {
            String flag_dialog = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.CONNECTIVITY_DIALOG_SHOWED);
            if (flag_dialog.equals("0"))
                new ShowConfirmations(this, this).openRetry();
        }
    }

    @Override
    public void method_negative(Activity activity) {
        activity.finish();
    }
}
