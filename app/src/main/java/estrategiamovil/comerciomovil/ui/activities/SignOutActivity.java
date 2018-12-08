package estrategiamovil.comerciomovil.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
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

import java.util.Arrays;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.CategoryViewModel;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.UserLocalProfile;
import estrategiamovil.comerciomovil.tools.UserSuscription;
import estrategiamovil.comerciomovil.tools.VolleyErrorHelper;
import estrategiamovil.comerciomovil.ui.fragments.CardsFragment;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class SignOutActivity extends AppCompatActivity {
    private final int METHOD_LOGOUT = 1;
    private final int METHOD_CATEGORIES = 2;
    private static final String TAG = SignOutActivity.class.getSimpleName();
    private CategoryViewModel[] categories;
    private Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteDevice();
    }
    private void deleteDevice(){
        String idUser = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.localUserId);
        String url = Constants.USERS+"?method=logout&idUser="+idUser;
        VolleyGetRequest(url,METHOD_LOGOUT);
    }
    private void dropSessionValues(){
        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.localEmail,"");
        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.localUserId,"");
        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.localAvatarPath,"");
        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.localUserAdministrator,"");
        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.showTipImage,"");
        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.showTipSignupSuscriber,String.valueOf(false));
        //filters
        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.business_category_name,"");
        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.business_category_id,"");
        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.business_subcategory_id,"");
        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.business_sub_subcategory_id,"");

        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.products_subcategory_selected,"");
        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.products_sub_subcategory_selected,"");

        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.ads_category_selected,"");
        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.ads_subcategory_selected,"");
        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.ads_sub_subcategory_selected,"");
        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.ads_category_search,"");

        UserLocalProfile.deleteUserProfile(getApplicationContext());
        UserSuscription.deleteSuscriptionDetails(getApplicationContext());
    }
    private void getCategories(){
        String url = Constants.GET_CATEGORIES+"?method=getCategories";
        VolleyGetRequest(url,METHOD_CATEGORIES);
    }
    public void VolleyGetRequest(String url,final int callback) {//obtener datos de la publicacion mas datos de las url de imagenes

        JsonObjectRequest request =  new JsonObjectRequest(Request.Method.GET,
                url,
                (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (callback == METHOD_CATEGORIES)
                            processingResponse(response);
                        else if(callback == METHOD_LOGOUT)
                            processingResponseLogout(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        String message = "No se pudo cerrar sesión, intente nuevamente.";
                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                        Toast.makeText(
                                SignOutActivity.this,
                                message,
                                Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleySingleton.
                getInstance(getApplicationContext()).
                addToRequestQueue(
                        request
                );
    }
    private void startMainActivity(){
        Context context = SignOutActivity.this;
        Intent intentMain =  new Intent(context, MainActivity.class);
        context.startActivity(intentMain);
    }
    private void processingResponse(JSONObject response) {

        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    // Obtener array "publications" Json
                    JSONArray mensaje = response.getJSONArray("categories");
                    // Parsear con Gson
                    categories = gson.fromJson(mensaje.toString(), CategoryViewModel[].class);

                    StringBuffer buffer = new StringBuffer();
                    for(int i = 0;i<categories.length;i++){
                        buffer.append(categories[i].getIdCategory());
                        if (i<categories.length-1)
                            buffer.append(",");
                    }
                    Log.d(TAG,"CATEGORIES RESET:"+buffer.toString());
                    ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.categoriesUser,buffer.toString());
                    dropSessionValues();
                    startMainActivity();
                    break;
                case "2": // FALLIDO
                    String mensaje2 = response.getString("message");
                    Toast.makeText(
                            getApplicationContext(),
                            mensaje2,
                            Toast.LENGTH_LONG).show();
                    onBackPressed();
                    break;

            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(
                    getApplicationContext(),
                    "Ocurrio un problema al cerrar su sesión",
                    Toast.LENGTH_LONG).show();
            onBackPressed();
        }

    }
    private void processingResponseLogout(JSONObject response) {

        Log.d(TAG,"result logout="+response.toString());
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    // Parsear con Gson
                    getCategories();
                    break;
                case "2": // FALLIDO
                    String mensaje2 = response.getString("message");
                    Toast.makeText(
                            getApplicationContext(),
                            mensaje2,
                            Toast.LENGTH_LONG).show();
                    onBackPressed();
                    break;

            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(
                    getApplicationContext(),
                    "Ocurrio un problema al cerrar su sesión",
                    Toast.LENGTH_LONG).show();
            onBackPressed();
        }

    }
}
