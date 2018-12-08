package estrategiamovil.comerciomovil.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.Map;


import estrategiamovil.comerciomovil.R;

import estrategiamovil.comerciomovil.modelo.City;
import estrategiamovil.comerciomovil.modelo.PublicationCardViewModel;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.ui.adapters.CitiesSpinnerAdapter;
import estrategiamovil.comerciomovil.ui.adapters.PublicationAdapter;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;
import estrategiamovil.comerciomovil.web.VolleySingleton;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private ArrayList<PublicationCardViewModel> publications = new ArrayList<>();
    private static RecyclerView recList;
    //private LinearLayout change_city_container;
    private RelativeLayout layout_no_publications;
    private AppCompatButton button_retry_search;
    private RelativeLayout no_connection_layout;
    private AppCompatButton button_retry;
    private Spinner spinner_city;
    private static City[] cities;
    private TextView spinner_city_text;
    private ProgressBar pbLoading_recommended;
    LinearLayoutManager llm;
    private static final String TAG = SearchFragment.class.getSimpleName();
    private Gson gson = new Gson();
    private static HashMap<String, String> params = new HashMap<> ();
    private PublicationAdapter adapter;
    private String query ="";

    public static SearchFragment createInstance(HashMap<String, String> arguments){
        SearchFragment fragment = new SearchFragment();
        fragment.setParams(arguments);
        return fragment;
    }
    public SearchFragment() {}

    public static void setParams(HashMap<String, String> params) {
        SearchFragment.params= params;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        pbLoading_recommended = (ProgressBar) rootView.findViewById(R.id.pbLoading_recommended);
        layout_no_publications = (RelativeLayout)rootView.findViewById(R.id.layout_no_publications);
        button_retry_search = (AppCompatButton) rootView.findViewById(R.id.button_retry_search);
        no_connection_layout = (RelativeLayout) rootView.findViewById(R.id.no_connection_layout);
        pbLoading_recommended.setVisibility(View.VISIBLE);
        recList = (RecyclerView) rootView.findViewById(R.id.cardList);
        recList.setVisibility(View.INVISIBLE);
        recList.setHasFixedSize(true);
        recList.setItemAnimator(new DefaultItemAnimator());
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        if (recList.getLayoutManager()==null){ recList.setLayoutManager(llm);}
        spinner_city_text = (TextView) rootView.findViewById(R.id.spinner_city_text);
        button_retry = (AppCompatButton) rootView.findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publications.clear();
                loadingView();
                //search(query,Constants.cero, Constants.load_more_tax,true);
                getCities();
            }
        });
        button_retry_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publications.clear();
                loadingView();
                //search(query,Constants.cero, Constants.load_more_tax,true);
                getCities();
            }
        });
        spinner_city = (Spinner) rootView.findViewById(R.id.spinner_city);
        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ImageView bullet = (ImageView) view.findViewById(R.id.image);
                View divider = view.findViewById(R.id.spinner_divider);
                divider.setVisibility(View.GONE);
                bullet.setVisibility(View.GONE);
                City city = (City) parent.getItemAtPosition(position);
                spinner_city_text.setText(city.getIdCity());
                Log.d(TAG, "City:" + city.getIdCity() + " " + city.getCity());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (params.get(Constants.search_flow).equals("true")) {
            pbLoading_recommended.setVisibility(View.GONE);
            layout_no_publications.setVisibility(View.VISIBLE);
        }

        if (savedInstanceState==null) getCities();
        return rootView;
    }

    private void getCities(){
        HashMap<String,String> params = new HashMap<>();
        String idCountry = ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.countryUser);
        params.put("idCountry", idCountry);
        VolleyPostRequest(Constants.GET_CITIES, params);
    }
    private void updateUI(final boolean connection_error){
                if (connection_error) {
                    no_connection_layout.setVisibility(View.VISIBLE);
                    recList.setVisibility(View.GONE);
                    layout_no_publications.setVisibility(View.GONE);
                    pbLoading_recommended.setVisibility(View.GONE);
                }else if (getPublications()!=null && getPublications().size()>0) {
                    no_connection_layout.setVisibility(View.GONE);
                    recList.setVisibility(View.VISIBLE);
                    layout_no_publications.setVisibility(View.GONE);
                    pbLoading_recommended.setVisibility(View.GONE);
                }else{
                    no_connection_layout.setVisibility(View.GONE);
                    recList.setVisibility(View.GONE);
                    layout_no_publications.setVisibility(View.VISIBLE);
                    pbLoading_recommended.setVisibility(View.GONE);
                }
    }
    public void VolleyPostRequest(String url, final HashMap<String, String> args) {
        JSONObject jobject = new JSONObject(args);
        Log.d(TAG, "VolleyPostRequest:" + jobject.toString());
        JsonObjectRequest request = new JsonObjectRequest(
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
                        String idCity = ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.cityUser);
                        spinner_city_text.setText(idCity);
                        error.printStackTrace();
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Accept", "application/json");
                return headers;
            }


            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8" + getParamsEncoding();
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }
    private void onCitiesCreated(){
        ArrayList<City> list = new ArrayList<City>();
        list.addAll(Arrays.asList(cities));
        CitiesSpinnerAdapter spinnerAdapter = new CitiesSpinnerAdapter(getActivity(),R.layout.spinner_rows,list);
        spinner_city.setAdapter(spinnerAdapter);
        int position = 0;
        String idCity = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.cityUser);
        for (int i = 0; i < cities.length; i++) {
            if (cities[i].getIdCity().equals(idCity)) {
                position = i;
                break;
            }
        }
        // Setear selección del Spinner
        spinner_city.setSelection(position);
        spinner_city_text.setText(idCity);
    }
    private void processResponse(JSONObject response) {
        try {
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("cities");
                    cities = gson.fromJson(mensaje.toString(), City[].class);
                    onCitiesCreated();
                    pbLoading_recommended.setVisibility(View.GONE);
                    break;
                case "2": // FALLIDO
                    //clearProducts();
                    String mensaje2 = response.getString("message");
                    Toast.makeText(
                            getContext(),
                            mensaje2,
                            Toast.LENGTH_SHORT).show();
                    break;
                case "3": // FALLIDO
                    //clearProducts();
                    String mensaje3 = response.getString("message");
                    Toast.makeText(
                            getContext(),
                            mensaje3,
                            Toast.LENGTH_LONG).show();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            updateUI(true);
            String mensaje3 = null;
            try {
                mensaje3 = response.getString("message");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            Toast.makeText(
                    getContext(),
                    mensaje3,
                    Toast.LENGTH_LONG).show();
        }

    }
    public void VolleyGetRequest(final String url,final boolean load_initial){
        if (!load_initial) {
            addLoadingAndMakeRequest(url);

        }else{
            makeRequest(url,load_initial);
        }
    }
    private void addLoadingAndMakeRequest(final String url){
        Log.d(TAG,"addLoading...");

        publications.add(null);
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                adapter.notifyItemInserted(publications.size() - 1);
                //Load more data for reyclerview
                makeRequest(url, false);
            }
        };
        handler.post(r);

    }
    private void makeRequest(String url,final boolean load_initial){
        VolleySingleton.
                getInstance(getActivity()).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                (String) null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(final JSONObject response) {
                                        // Procesar la respuesta Json

                                        if (load_initial)
                                            processingResponseInit(response);
                                        else {
                                            publications.remove(publications.size() - 1);//delete loading..
                                            adapter.notifyItemRemoved(publications.size());
                                            processingResponse(response);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        if(!load_initial) {
                                            publications.remove(publications.size() - 1);//delete loading..
                                            adapter.notifyItemRemoved(publications.size());
                                            notifyListChanged();
                                            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),getActivity());
                                        }else{updateUI(true);}
                                    }
                                }

                        ).setRetryPolicy(new DefaultRetryPolicy(
                                Constants.MY_SOCKET_TIMEOUT_MS,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                );
    }
    private void notifyListChanged(){
        //Load more data for reyclerview
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                adapter.setLoaded();
            }
        }, 0);
    }


    private void processingResponseInit(JSONObject response) {
        Log.d(TAG,"processingResponse initial search");
        ArrayList<PublicationCardViewModel> new_publications = null;
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("publications");
                    new_publications = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), PublicationCardViewModel[].class)));
                    if (new_publications!=null && new_publications.size()>0){ publications.addAll(new_publications);}
                    break;
                case "2": // NO DATA FOUND. aqui deberia ir el notify
                    String mensaje2 = response.getString("message");
                    Log.d(TAG,"CardsFragment.noData..."+mensaje2);
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            setupAdapter();
            updateUI(false);
        }

    }


    public void resetSearchParameters(){
        Log.d(TAG,"resetSearchParameters.....");
        params.put(Constants.search_flow,"false");
    }
    public void search(String query,int start,int end,boolean initial){
        Log.d(TAG,"SEARCHING......................:"+query);
        this.query = query;

        if (initial) {
            publications.clear();
            loadingView();
        }

        StringBuffer url = new StringBuffer();


        url.append(Uri.parse(Constants.GET_PUBLICATIONS).buildUpon().
                appendQueryParameter("method", "getPublicationsByDescripcion").
                appendQueryParameter("city", spinner_city_text.getText().toString().trim()).
                appendQueryParameter("start", String.valueOf(start)).
                appendQueryParameter("end", String.valueOf(end)).
                appendQueryParameter("query", query).build().toString());
        Log.d(TAG,"initial: "+initial +" QUERY:" + url.toString());
        VolleyGetRequest(url.toString(),initial);
    }

    public void setupListItems() {//obtener datos de la publicacion mas datos de las url de imagenes
        //text_change_state.setText(params.get("cityName"));

        if(params.get(Constants.search_flow).equals("true")){// search flow
            Log.d(TAG,"SEARCH FLOW:**********************"+params.get(Constants.search_flow));
            pbLoading_recommended.setVisibility(View.GONE);
            layout_no_publications.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        publications = null;
    }
    private void processingResponse(JSONObject response) {
        ArrayList<PublicationCardViewModel> new_publications = null;
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("publications");
                    new_publications = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), PublicationCardViewModel[].class)));
                    if (new_publications!=null && new_publications.size()>0){
                        publications.addAll(new_publications);
                    }
                    break;
                case "2": // NO DATA FOUND
                    String mensaje2 = response.getString("message");
                    Log.d(TAG,"SearchFragment.noData..."+mensaje2);
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            notifyListChanged();
        }

    }

    private void setupAdapter(){
        // Inicializar adaptador
        if(recList.getAdapter()==null) {
            adapter = new PublicationAdapter(getPublications(), getActivity(), recList);

            recList.setAdapter(adapter);
            //load more functionallity

            adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    int index = publications != null ? (getPublications().size()) : 0;
                    int end = index + Constants.load_more_tax;
                    Log.d(TAG, "OnLoadMore search....");
                    search(query, index, end, false);
                }

            });
        }else{
            notifyListChanged();
        }
    }
    private ArrayList<PublicationCardViewModel> getPublications(){return publications;}

    private void loadingView(){
        no_connection_layout.setVisibility(View.GONE);
        recList.setVisibility(View.GONE);
        layout_no_publications.setVisibility(View.GONE);
        pbLoading_recommended.setVisibility(View.VISIBLE);
    }
}
