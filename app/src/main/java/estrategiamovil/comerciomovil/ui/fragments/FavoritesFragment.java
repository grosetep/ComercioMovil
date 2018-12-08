package estrategiamovil.comerciomovil.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Favorite;
import estrategiamovil.comerciomovil.modelo.PurchaseItem;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.ui.adapters.FavoritesAdapter;
import estrategiamovil.comerciomovil.ui.adapters.PurchasesAdapter;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {
    private static Favorite[] favorites = null;
    private ProgressBar pb;

    private Gson gson = new Gson();
    LinearLayoutManager llm;
    GridLayoutManager llmg;
    private static final String TAG = FavoritesFragment.class.getSimpleName();
    private RecyclerView recycler_favorites;
    private FavoritesAdapter mAdapter;


    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        return fragment;
    }


    public FavoritesFragment() {  }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_favorites, container, false);

        pb = (ProgressBar) v.findViewById(R.id.pbLoading_favorites);
        pb.setVisibility(View.VISIBLE);
        llm = new LinearLayoutManager(getActivity());
        llmg = new GridLayoutManager(getActivity(),2);
        llm.setOrientation(GridLayoutManager.VERTICAL);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recycler_favorites = (RecyclerView) v.findViewById(R.id.recycler_favorites);
        recycler_favorites.setHasFixedSize(true);

        if (favorites==null) {
            getFavoritesByUser();
        }else{
            setupAdapter();
        }


        return v;
    }
    public void getFavoritesByUser() {
        String newUrl = Constants.GET_FAVORITES;
        String idUser = ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.localUserId);
        Log.d(TAG,"----------------------------------->3:"+newUrl+"?method=getFavorites&idUser="+idUser);
        VolleyGetRequest(newUrl + "?method=getFavorites&idUser=" + idUser);

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
                                        processResponse(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        String message = "Verifique su conexión a internet.";
                                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                                        getActivity().onBackPressed();
                                        Toast.makeText(
                                                getContext(),
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
    private void processResponse(JSONObject response) {
        Log.d(TAG, "Response:" + response.toString());
        try {
            // Obtener atributo "status"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    // Obtener array "publications" Json
                    JSONArray mensaje = response.getJSONArray("result");
                    // Parsear con Gson
                    favorites = gson.fromJson(mensaje.toString(), Favorite[].class);
                    setupAdapter();
                    pb.setVisibility(View.GONE);

                    break;
                case "2": // NO DATA FOUND
                    pb.setVisibility(View.GONE);
                    String mensaje2 = response.getString("message");
                    Toast.makeText(
                            getActivity(),
                            mensaje2,
                            Toast.LENGTH_LONG).show();
                    clearFavorites();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            pb.setVisibility(View.GONE);
            clearFavorites();
        }

    }
    private void clearFavorites(){
            favorites = new Favorite[]{};
            mAdapter = new FavoritesAdapter(Arrays.asList(favorites),getActivity(),this);
            recycler_favorites.setAdapter(mAdapter);
            recycler_favorites.invalidate();
            if (recycler_favorites.getLayoutManager()==null){ recycler_favorites.setLayoutManager(llm);}
            pb.setVisibility(View.GONE);
            recycler_favorites.setVisibility(View.GONE);
    }
    private void setupAdapter(){
        // Set the adapter
        mAdapter = new FavoritesAdapter(Arrays.asList(favorites),getActivity(),this);
        recycler_favorites.setAdapter(mAdapter);
        if (recycler_favorites.getLayoutManager()==null){
            recycler_favorites.setLayoutManager(llm);
        }
        pb.setVisibility(View.GONE);
    }

    @Override
    public void onStop(){
        //reset data from list publications for reloading update when user go back to the application
        favorites = null;
        super.onStop();
    }


}
