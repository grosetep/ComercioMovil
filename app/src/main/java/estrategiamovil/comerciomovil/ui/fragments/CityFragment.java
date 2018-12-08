package estrategiamovil.comerciomovil.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.City;
import estrategiamovil.comerciomovil.modelo.Country;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.DividerItemDecoration;
import estrategiamovil.comerciomovil.tools.VolleyErrorHelper;
import estrategiamovil.comerciomovil.ui.activities.CityPreferencesActivity;
import estrategiamovil.comerciomovil.ui.activities.CountryPreferencesActivity;
import estrategiamovil.comerciomovil.ui.adapters.CityAdapter;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * A placeholder fragment containing a simple view.
 */
public class CityFragment extends Fragment {
    private static final String TAG = CityFragment.class.getSimpleName();
    private static RecyclerView mRecyclerViewCities;
    private static CityAdapter mAdapter;
    private TextView text_change_country;
    private LinearLayout change_country_container;
    private static City[] cities;
    private Gson gson = new Gson();
    private HashMap<String, String> params;
    private ProgressBar pb;
    private LinearLayout principal_container_city;


    public CityFragment() {   }

    public static CityFragment createInstance(HashMap<String, String> params){
        CityFragment city = new CityFragment();
        city.setParams(params);
        return city;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_city, container, false);
        mRecyclerViewCities = (RecyclerView) v.findViewById(R.id.recyclerviewCities);
        mRecyclerViewCities.addItemDecoration(new DividerItemDecoration(getActivity()));
        mRecyclerViewCities.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        text_change_country = (TextView) v.findViewById(R.id.text_change_country);

        if ( getParams()!=null) {
            if (getParams().get("labelCountry") == null || getParams().get("labelCountry").equals("")) {
                getCountryLabel();
            }else {
                text_change_country.setText(getParams().get("labelCountry"));
            }
        }

        change_country_container = (LinearLayout) v.findViewById(R.id.change_country_container);
        change_country_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, CountryPreferencesActivity.class);
                context.startActivity(intent);
            }
        });
        //validate flow
        if (getParams().get(CityPreferencesActivity.EXTRA_FLOW).equals(Constants.find_flow)){change_country_container.setVisibility(View.GONE);}

        principal_container_city = (LinearLayout) v.findViewById(R.id.principal_container_city);
        pb = (ProgressBar) v.findViewById(R.id.pbLoading_city);
        pb.setVisibility(ProgressBar.VISIBLE);
        setupListItems(getParams());
        return v;
    }
    private void getCountryLabel(){
        String url= Constants.GET_COUNTRIES+"?method=getCountryById"+"&idCountry=" + getParams().get("idCountry");
        Log.d(TAG,"URL:"+url);
        VolleyGetRequest(url);
    }
    public void VolleyGetRequest(String url){
        VolleySingleton.
                getInstance(getActivity()).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                (String) null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Procesar la respuesta Json
                                        processingResponseCity(response);
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
                                }

                        )
                );
    }

    private void processingResponseCity(JSONObject response) {
        Country c = null;
        Log.d(TAG,"RESPONSE:"+response);
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS

                    JSONObject mensaje = response.getJSONObject("country");
                    Log.d(TAG,"Success:" +mensaje.toString());
                    // Parsear con Gson
                    c = gson.fromJson(mensaje.toString(), Country.class);
                    if (c!=null) text_change_country.setText(c.getCountry());
                    break;
                case "2": // NO DATA FOUND

                    String mensaje2 = response.getString("message");
                    Toast.makeText(
                            getActivity(),
                            mensaje2,
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

    }
    public void setupListItems(HashMap<String, String> params) {//obtener datos de la publicacion mas datos de las url de imagenes
        JSONObject jobject = new JSONObject(params);
        Log.d(TAG, jobject.toString());

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constants.GET_CITIES,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                processingResponse(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                                pb.setVisibility(View.GONE);
                                String mensaje2 = "Servicio no disponible, verifique conexión a Internet";
                                Toast.makeText(
                                        getActivity(),
                                        mensaje2,
                                        Toast.LENGTH_SHORT).show();
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

    private void processingResponse(JSONObject response) {
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    // Obtener array "publications" Json
                    JSONArray mensaje = response.getJSONArray("cities");
                    // Parsear con Gson
                    cities = gson.fromJson(mensaje.toString(), City[].class);
                    // Inicializar adaptador
                    mRecyclerViewCities.setLayoutManager(new LinearLayoutManager(mRecyclerViewCities.getContext()));
                    mAdapter = new CityAdapter(getActivity(), Arrays.asList(cities),getParams());
                    mRecyclerViewCities.setAdapter(mAdapter);
                    principal_container_city.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                    break;
                case "2": // FALLIDO
                    pb.setVisibility(View.GONE);
                    String mensaje2 = response.getString("message");
                    Toast.makeText(
                            getActivity(),
                            mensaje2,
                            Toast.LENGTH_SHORT).show();
                    break;
                case "3": // FALLIDO
                    pb.setVisibility(View.GONE);
                    String mensaje3 = response.getString("message");
                    Toast.makeText(
                            getActivity(),
                            mensaje3,
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

    }


    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public static class listener implements  SearchView.OnQueryTextListener{
        @Override
        public boolean onQueryTextChange(String query) {
            final List<City> filteredModelList = filter(Arrays.asList(cities), query);
            mAdapter.animateTo(filteredModelList);
            mRecyclerViewCities.scrollToPosition(0);
            return false;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        private List<City> filter(List<City> models, String query) {
            query = query.toLowerCase();
            final List<City> filteredModelList = new ArrayList<>();
            for (City model : models) {
                final String text = model.getCity().toLowerCase();
                if (text.contains(query)) {
                    filteredModelList.add(model);
                }
            }

            return filteredModelList;
        }
    }
}
