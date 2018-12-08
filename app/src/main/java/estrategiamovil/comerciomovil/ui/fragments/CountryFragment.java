package estrategiamovil.comerciomovil.ui.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Country;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.DividerItemDecoration;
import estrategiamovil.comerciomovil.tools.VolleyErrorHelper;
import estrategiamovil.comerciomovil.ui.adapters.CountryAdapter;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * A placeholder fragment containing a simple view.
 */
public class CountryFragment extends Fragment  {
    private static final String TAG = CountryFragment.class.getSimpleName();
    private static RecyclerView mRecyclerView;
    private static CountryAdapter mAdapter;
    private static Country[] countries;
    private Gson gson = new Gson();
    private ProgressBar pb;
    private LinearLayout principal_container_country;
    public CountryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_country, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerviewCountries);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        principal_container_country = (LinearLayout) v.findViewById(R.id.principal_container_country);
        pb = (ProgressBar) v.findViewById(R.id.pbLoading_country);
        pb.setVisibility(ProgressBar.VISIBLE);
        setupListItems();
        return v;
    }

    public void setupListItems() {//obtener datos de la publicacion mas datos de las url de imagenes
        Log.d(TAG, "CountryFragment.setupListItems");
        // Petición GET
        String url = Constants.GET_COUNTRIES+"?method=getAllCountries";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar la respuesta Json
                        processingResponse(response);
                        //  pDialog.hide();
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
                                Toast.LENGTH_LONG).show();
                    }
                }

        );
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.
                getInstance(getActivity()).
                addToRequestQueue(request
                );
    }

    private void processingResponse(JSONObject response) {
        Log.d(TAG, "CountryPreferencesActivity.processingResponse");

        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    Log.d(TAG,"CountryPreferencesActivity.success");
                    // Obtener array "publications" Json
                    JSONArray mensaje = response.getJSONArray("countries");
                    Log.d(TAG,"CountryPreferencesActivity:" +mensaje.toString());
                    // Parsear con Gson
                    countries = gson.fromJson(mensaje.toString(), Country[].class);
                    // Inicializar adaptador

                    mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
                    mAdapter = new CountryAdapter(getActivity(), Arrays.asList(countries));
                    mRecyclerView.setAdapter(mAdapter);
                    principal_container_country.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                    break;
                case "2": // FALLIDO
                    pb.setVisibility(View.GONE);
                    Log.d(TAG,"CountryPreferencesActivity.fail");
                    String mensaje2 = response.getString("message");
                    Toast.makeText(
                            getActivity(),
                            mensaje2,
                            Toast.LENGTH_LONG).show();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

    }

    public static class listener implements  SearchView.OnQueryTextListener{
        @Override
        public boolean onQueryTextChange(String query) {
            final List<Country> filteredModelList = filter(Arrays.asList(countries), query);
            mAdapter.animateTo(filteredModelList);
            mRecyclerView.scrollToPosition(0);
            return false;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        private List<Country> filter(List<Country> models, String query) {
            query = query.toLowerCase();
            final List<Country> filteredModelList = new ArrayList<>();
            for (Country model : models) {
                final String text = model.getCountry().toLowerCase();
                if (text.contains(query)) {
                    filteredModelList.add(model);
                }
            }

            return filteredModelList;
        }
    }
}
