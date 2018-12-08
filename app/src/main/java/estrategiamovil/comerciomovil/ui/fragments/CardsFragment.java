package estrategiamovil.comerciomovil.ui.fragments;

import android.os.Handler;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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


import estrategiamovil.comerciomovil.R;

import estrategiamovil.comerciomovil.modelo.PublicationCardViewModel;
import estrategiamovil.comerciomovil.tools.Connectivity;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.GeneralFunctions;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.ui.activities.CityPreferencesActivity;
import estrategiamovil.comerciomovil.ui.adapters.PublicationAdapter;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;
import estrategiamovil.comerciomovil.web.VolleySingleton;


public class CardsFragment extends Fragment {

    private ArrayList<PublicationCardViewModel> publications = new ArrayList<>();
    private static RecyclerView recList;
    private SwipeRefreshLayout swipeRefresh_cards;
    private TextView text_change_state;
    private LinearLayout change_city_container;
    private RelativeLayout layout_no_publications;
    private AppCompatButton button_retry_search;
    private RelativeLayout no_connection_layout;
    private ProgressBar pbLoading_recommended;
    LinearLayoutManager llm;
    private static final String TAG = CardsFragment.class.getSimpleName();
    private Gson gson = new Gson();
    public static boolean locationChanged = false;
    private AppCompatButton button_retry;
    public final boolean load_initial = true;

    private static HashMap<String, String> params = new HashMap<> ();

    private PublicationAdapter adapter;
    public static CardsFragment createInstance(HashMap<String, String> arguments){//,DrawerLayout drawer
        CardsFragment fragment = new CardsFragment();
        fragment.setParams(arguments);
        return fragment;
    }
    public CardsFragment() { }

    public static HashMap<String, String> getParams() {
        return params;
    }

    public static void setParams(HashMap<String, String> params) {
        CardsFragment.params= params;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main_cards, container, false);
        layout_no_publications = (RelativeLayout)rootView.findViewById(R.id.layout_no_publications);
        button_retry_search = (AppCompatButton) rootView.findViewById(R.id.button_retry_search);
        no_connection_layout = (RelativeLayout) rootView.findViewById(R.id.no_connection_layout);
        pbLoading_recommended = (ProgressBar) rootView.findViewById(R.id.pbLoading_recommended);
        swipeRefresh_cards = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh_cards);
        swipeRefresh_cards.setColorSchemeResources(
                R.color.s1,
                R.color.s2,
                R.color.s3,
                R.color.s4
        );
        swipeRefresh_cards.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d(TAG,"REFRESH DATA.....");
                        boolean first_loading = false;
                        boolean refresh = true;
                        setupListItems(Constants.cero,Constants.load_more_tax,first_loading,refresh);
                    }
                }
        );
        recList = (RecyclerView) rootView.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        recList.setItemAnimator(new DefaultItemAnimator());
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        if (recList.getLayoutManager()==null){ recList.setLayoutManager(llm);}

        text_change_state = (TextView) rootView.findViewById(R.id.text_change_state);
        change_city_container = (LinearLayout) rootView.findViewById(R.id.change_city_container);
        change_city_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, CityPreferencesActivity.class);
                intent.putExtra(CityPreferencesActivity.EXTRA_NAME, getParams().get("idCountry"));
                intent.putExtra(CityPreferencesActivity.EXTRA_FLOW, Constants.flowChangeCity);
                context.startActivity(intent);
            }
        });
        button_retry = (AppCompatButton) rootView.findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publications.clear();
                loadingView();
                swipeRefresh_cards.setRefreshing(false);
                setupListItems(Constants.cero,Constants.load_more_tax,true,false);
            }
        });
        button_retry_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publications.clear();
                loadingView();
                swipeRefresh_cards.setRefreshing(false);
                setupListItems(Constants.cero,Constants.load_more_tax,true,false);
            }
        });
        pbLoading_recommended.setVisibility(View.VISIBLE);
        recList.setVisibility(View.GONE);

        if (CardsFragment.locationChanged) publications =  new ArrayList<>(); //reset when city has changed

        if (Connectivity.isNetworkAvailable(getContext())) {
            setupListItems(Constants.cero,Constants.load_more_tax,load_initial,false);
        }else {
            updateUI(true);
        }
        return rootView;
    }

    public void VolleyGetRequest(final String url,boolean load_initial,boolean isRefresh){
        if (isRefresh) {
            makeRequest(url,load_initial,isRefresh);
        }else if (!load_initial) {
            addLoadingAndMakeRequest(url);
        }else{
            makeRequest(url,load_initial,isRefresh);
        }
    }

    private void makeRequest(String url,final boolean load_initial, final boolean isRefresh){
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
                                        if (isRefresh) {//only update list
                                            processingResponse(response,isRefresh);
                                            swipeRefresh_cards.setRefreshing(false);
                                        }else if (load_initial) {
                                            processingResponseInit(response);
                                        }else {
                                            publications.remove(publications.size() - 1);//delete loading..
                                            adapter.notifyItemRemoved(publications.size());
                                            processingResponse(response,isRefresh);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        if(isRefresh){
                                            swipeRefresh_cards.setRefreshing(false);
                                            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),getActivity());
                                        }else if(!load_initial) {
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
    private void addLoadingAndMakeRequest(final String url){
        final boolean isRefresh = false;
        final boolean first_load= false;

        publications.add(null);
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                adapter.notifyItemInserted(publications.size() - 1);
                makeRequest(url,first_load,isRefresh);
            }
        };
        handler.post(r);

    }
    public void setupListItems(int start, int end,boolean load_initial,boolean isRefresh) {//obtener datos de la publicacion mas datos de las url de imagenes
        String newUrl = Constants.GET_PUBLICATIONS;
        text_change_state.setText(params.get("cityName"));

            //validation
            if (params.get("idCountry").length() != Constants.cero && params.get("idCity").length() != Constants.cero && params.get("listCategories").length() != Constants.cero) {
                VolleyGetRequest(newUrl + "?method=getPublicationsByCategories" + "&idCountry=" + params.get("idCountry") + "&idCity=" + params.get("idCity") + "&listCategories=" + params.get("listCategories")+
                "&start="+start+"&end="+end,load_initial,isRefresh);
            } else {
                VolleyGetRequest(newUrl + "?method=getBestRatedPublications"+"&start="+start+"&end="+end,load_initial,isRefresh);
            }
    }

    private void processingResponse(JSONObject response,boolean isRefresh) {
        ArrayList<PublicationCardViewModel> new_publications = null;
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("publications");
                    new_publications = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), PublicationCardViewModel[].class)));
                    addNewElements(new_publications,isRefresh);
                    break;
                case "2": // NO DATA FOUND
                    Log.d(TAG,"CardsFragment.noData");
                    if (isRefresh && new_publications == null && publications.size()>Constants.cero ){publications.clear();updateUI(false);}
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            setLocationState();
            notifyListChanged();
            //updateUI(false);
        }

    }

    private void addNewElements(ArrayList<PublicationCardViewModel> new_publications,boolean isRefresh){
        if (new_publications!=null && new_publications.size()>Constants.cero) {
            if (isRefresh){
                publications.clear();
                publications.addAll(new_publications);
            }else{
                publications.addAll(GeneralFunctions.FilterPublications(publications, new_publications));
            }
        }
    }
    private void notifyListChanged(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                adapter.setLoaded();
            }
        }, Constants.cero);
    }
    private void processingResponseInit(JSONObject response) {
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("publications");
                    ArrayList<PublicationCardViewModel> new_publications = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), PublicationCardViewModel[].class)));
                    if (new_publications!=null && new_publications.size()>0) {
                        publications.addAll(new_publications);
                    }
                    break;
                case "2": // NO DATA FOUND
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
    private ArrayList<PublicationCardViewModel> getPublications(){ return publications;}
    private void updateUI(final boolean connection_error){
        getActivity().runOnUiThread(new
                                            Runnable() {
                                                @Override
                                                public void run() {
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
        });
    }
    private void setupAdapter(){
        // Inicializar adaptador
        if (recList.getAdapter()==null) {
            adapter = new PublicationAdapter(publications, getActivity(), recList);
            recList.setAdapter(adapter);
            adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (!swipeRefresh_cards.isRefreshing()) {
                        int index = publications != null ? (publications.size()) : Constants.cero;
                        int end = index + Constants.load_more_tax;
                        setupListItems(index, end, false, false);
                    }
                }
            });
        }else{
            notifyListChanged();
        }
    }
    private void setLocationState(){
        if (publications!=null && publications.size()>0){
            PublicationCardViewModel p = publications.get(0);
            if (p!=null){
                text_change_state.setText(p.getState());
            }
        }
    }

    private void loadingView(){
        no_connection_layout.setVisibility(View.GONE);
        recList.setVisibility(View.GONE);
        layout_no_publications.setVisibility(View.GONE);
        pbLoading_recommended.setVisibility(View.VISIBLE);
    }
}
