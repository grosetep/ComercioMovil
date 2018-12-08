package estrategiamovil.comerciomovil.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.CategoryViewModel;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.DividerItemDecoration;
import estrategiamovil.comerciomovil.tools.VolleyErrorHelper;
import estrategiamovil.comerciomovil.ui.adapters.CategoryAdapter;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * A placeholder fragment containing a simple view.
 */
public class CategoryFragment extends Fragment {
    private static final String TAG = CategoryFragment.class.getSimpleName();
    private RecyclerView mRecyclerViewCategories;
    private LinearLayout layout_cat_preferences;
    private ProgressBar loading_cat_preferences;
    private CategoryAdapter mAdapter;
    private CategoryViewModel[] categories;
    private Gson gson = new Gson();
    private HashMap<String,String> categoryCatalog=new HashMap<>();

    public CategoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category, container, false);
        layout_cat_preferences = (LinearLayout) v.findViewById(R.id.layout_cat_preferences);
        loading_cat_preferences = (ProgressBar) v.findViewById(R.id.loading_cat_preferences);
        mRecyclerViewCategories = (RecyclerView) v.findViewById(R.id.recyclerviewCategory);
        mRecyclerViewCategories.addItemDecoration(new DividerItemDecoration(getActivity()));
        initProcess(true);
        setupListItems();
        return v;
    }

    private void initProcess(boolean flag){
        if (loading_cat_preferences!=null && layout_cat_preferences!=null){
            layout_cat_preferences.setVisibility(flag?View.GONE:View.VISIBLE);
            loading_cat_preferences.setVisibility(flag?View.VISIBLE:View.GONE);
        }
    }
    public void setupListItems() {//obtener datos de la publicacion mas datos de las url de imagenes

        // Petición GET
        String url = Constants.GET_CATEGORIES+"?method=getCategories";
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
                        String message = VolleyErrorHelper.getErrorType(error, getActivity());
                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                        Toast.makeText(
                                getActivity(),
                                message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleySingleton.
                getInstance(getActivity()).
                addToRequestQueue(
                        request
                );
    }
    private HashMap<String , String> setIconsCategories( HashMap<String,String> categoryCatalog, CategoryViewModel[] categories ){
        HashMap<String , String> map=new HashMap<>();
        for(CategoryViewModel c:categories){
            Log.d(TAG,"id:"+c.getIdCategory()+" image:" + c.getImageCategory() + " path: " + c.getPathImageCategory() + " category:"+c.getCategory());
            if (c.getPathImageCategory().equals(Constants.imageCategoryInLocalPath)) {//imagen desde recursos locales
                int resID = getContext().getResources().getIdentifier(c.getImageCategory().toString(), "drawable", getContext().getPackageName());
                map.put(c.getIdCategory(), "" + resID);
            }
            else{//imagen de servidor remoto
                map.put(c.getIdCategory(),c.getPathImageCategory() + c.getImageCategory());
            }

        }
        return map;
    }
    private void processingResponse(JSONObject response) {
        Log.d(TAG, "CategoryPreferencesActivity.processingResponse:"+response);
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    Log.d(TAG,"CategoryPreferencesActivity.success");
                    // Obtener array "publications" Json
                    JSONArray mensaje = response.getJSONArray("categories");
                    // Parsear con Gson
                    categories = gson.fromJson(mensaje.toString(), CategoryViewModel[].class);
                    for (CategoryViewModel c:categories){
                        //save category products in local preferences
                        if (c.getCategory().toUpperCase().contains("COMPRAS")) {
                            ApplicationPreferences.saveLocalPreference(getContext(), Constants.idCategoryProducts, c.getIdCategory());
                        }
                        Log.d(TAG,c.getIdCategory() + "-" + c.getCategory() + "-" + c.getPathImageCategory() + c.getImageCategory());
                    }
                    // Inicializar adaptador
                    mRecyclerViewCategories.setLayoutManager(new LinearLayoutManager(mRecyclerViewCategories.getContext()));
                    mAdapter = new CategoryAdapter(getActivity(), Arrays.asList(categories));
                    getmAdapter().setIdsDrawableCategories(setIconsCategories(categoryCatalog, categories));
                    mRecyclerViewCategories.setAdapter(getmAdapter());
                    layout_cat_preferences.setVisibility(View.VISIBLE);
                    initProcess(false);
                    break;
                case "2": // FALLIDO
                    Log.d(TAG,"CategoryPreferencesActivity.fail");
                    String mensaje2 = response.getString("message");
                    Toast.makeText(
                            getActivity(),
                            mensaje2,
                            Toast.LENGTH_LONG).show();
                    break;
                case "3": // FALLIDO
                    Log.d(TAG,"CategoryPreferencesActivity.fail");
                    String mensaje3 = response.getString("message");
                    Toast.makeText(
                            getActivity(),
                            mensaje3,
                            Toast.LENGTH_LONG).show();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(
                    getActivity(),
                    getResources().getString(R.string.generic_error),
                    Toast.LENGTH_LONG).show();
        }

    }

    public CategoryAdapter getmAdapter() {
        return mAdapter;
    }
}
