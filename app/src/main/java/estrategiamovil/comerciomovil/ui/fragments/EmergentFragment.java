package estrategiamovil.comerciomovil.ui.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import java.util.List;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.PublicationCardViewModel;
import estrategiamovil.comerciomovil.modelo.SubCategory;
import estrategiamovil.comerciomovil.modelo.SubSubCategory;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Connectivity;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.GeneralFunctions;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.ui.adapters.PublicationAdapter;
import estrategiamovil.comerciomovil.ui.interfaces.DialogCallbackInterface;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmergentFragment extends Fragment {
    private ArrayList<PublicationCardViewModel> emergents = new ArrayList<>();
    private static RecyclerView recList;
    private SwipeRefreshLayout swipeRefresh_emergents;
    private RelativeLayout layout_no_publications;
    private AppCompatButton button_retry_search;
    private RelativeLayout no_connection_layout;
    private ProgressBar pbLoading_emergent;
    LinearLayoutManager llm;
    private static final String TAG = EmergentFragment.class.getSimpleName();
    private Gson gson = new Gson();
    private PublicationAdapter adapter;
    private static HashMap<String, String> params = new HashMap<> ();
    private AppCompatButton button_retry;
    public final boolean load_initial = true;

    public static EmergentFragment createInstance(HashMap<String, String> arguments){//,DrawerLayout drawer
        EmergentFragment fragment = new EmergentFragment();
        if (arguments!=null)
            fragment.setParams(arguments);
        return fragment;
    }
    public EmergentFragment() {
        // Required empty public constructor
    }

    public static void setParams(HashMap<String, String> params) {
        EmergentFragment.params = params;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_emergent, container, false);
        layout_no_publications = (RelativeLayout)rootView.findViewById(R.id.layout_no_publications);
        button_retry_search = (AppCompatButton) rootView.findViewById(R.id.button_retry_search);
        no_connection_layout = (RelativeLayout) rootView.findViewById(R.id.no_connection_layout);
        pbLoading_emergent  = (ProgressBar) rootView.findViewById(R.id.pbLoading_emergent);
        recList = (RecyclerView) rootView.findViewById(R.id.cardList_emergent);
        recList.setItemAnimator(new DefaultItemAnimator());
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        if (recList.getLayoutManager()==null){ recList.setLayoutManager(llm);}

        button_retry = (AppCompatButton) rootView.findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emergents.clear();
                loadingView();
                swipeRefresh_emergents.setRefreshing(false);
                setupListItems(Constants.cero,Constants.load_more_tax,true,false);
            }
        });
        button_retry_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emergents.clear();
                loadingView();
                swipeRefresh_emergents.setRefreshing(false);
                setupListItems(Constants.cero,Constants.load_more_tax,true,false);
            }
        });
        pbLoading_emergent.setVisibility(View.VISIBLE);
        recList.setVisibility(View.GONE);
        swipeRefresh_emergents= (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh_emergents);
        swipeRefresh_emergents.setColorSchemeResources(
                R.color.s1,
                R.color.s2,
                R.color.s3,
                R.color.s4
        );
        swipeRefresh_emergents.setOnRefreshListener(
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
        if (Connectivity.isNetworkAvailable(getContext())) {
            emergents.clear();
            setupListItems(Constants.cero,Constants.load_more_tax,load_initial,false);
        }else {
            updateUI(true);
        }

        return rootView;
    }

    public void VolleyGetRequest(final String url,final boolean load_initial,boolean isRefresh){
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
                                            swipeRefresh_emergents.setRefreshing(false);
                                        }else if (load_initial) {
                                            processingResponseInit(response);
                                        }else {
                                            emergents.remove(emergents.size() - 1);//delete loading..
                                            adapter.notifyItemRemoved(emergents.size());
                                            processingResponse(response,isRefresh);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        if(isRefresh){
                                            swipeRefresh_emergents.setRefreshing(false);
                                            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),getActivity());
                                        }else if(!load_initial) {
                                            emergents.remove(emergents.size() - 1);//delete loading..
                                            adapter.notifyItemRemoved(emergents.size());
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
        Log.d(TAG, "addLoading...");
        final boolean isRefresh = false;
        final boolean first_load= false;
        emergents.add(null);
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                adapter.notifyItemInserted(emergents.size() - 1);
                makeRequest( url, first_load,isRefresh);
            }
        };
        handler.post(r);

    }
    public void setupListItems(int start, int end,boolean load_initial,boolean isRefresh) {//obtener datos de la publicacion mas datos de las url de imagenes
        String newUrl = Constants.GET_PUBLICATIONS;
         VolleyGetRequest(newUrl + "?method=getEmergentPublications"+"&start="+start+"&end="+end,load_initial,isRefresh);
    }

    private void processingResponse(JSONObject response,boolean isRefresh) {
        Log.d(TAG,"Emergents:"+response.toString());
        ArrayList<PublicationCardViewModel> new_emergents = null;
        try {
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("publications");
                    new_emergents = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), PublicationCardViewModel[].class)));
                    addNewElements(new_emergents,isRefresh);
                    break;
                case "2": // NO DATA FOUND
                    String mensaje2 = response.getString("message");
                    Log.d(TAG,"EmergentsFragment.noData..."+mensaje2);
                    if (isRefresh && new_emergents == null && emergents.size()>Constants.cero ){emergents.clear();updateUI(false);}
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            notifyListChanged();
        }

    }
    private void addNewElements(ArrayList<PublicationCardViewModel> new_publications,boolean isRefresh){
        if (new_publications!=null && new_publications.size()>Constants.cero) {
            Log.d(TAG, "new_publications:" + new_publications.size());
            if (isRefresh){Log.d(TAG, "isRefresh..");
                emergents.clear();
                emergents.addAll(new_publications);
            }else{
                emergents.addAll(GeneralFunctions.FilterPublications(emergents, new_publications));
            }
        }
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
        Log.d(TAG,"processingResponse emergents");
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("publications");
                    ArrayList<PublicationCardViewModel> new_publications = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), PublicationCardViewModel[].class)));
                    if (new_publications!=null && new_publications.size()>0) {
                        emergents.addAll(GeneralFunctions.FilterPublications(emergents,new_publications));
                    }
                    break;
                case "2": // NO DATA FOUND
                    String mensaje2 = response.getString("message");
                    Log.d(TAG,"CardsFragment.noData...."+mensaje2);
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            setupAdapter();
            updateUI(false);
        }
    }
    private void setupAdapter(){
        // Inicializar adaptador
        if (recList.getAdapter()==null) {
            adapter = new PublicationAdapter(emergents, getActivity(), recList);

            recList.setAdapter(adapter);
            //load more functionallity

            adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    //Load data from start to end, arraylist start from 0 and mysql from 1, then index = publications.size()+1
                    if (!swipeRefresh_emergents.isRefreshing()) {
                        int index = emergents != null ? (emergents.size()) : 0;
                        int end = index + Constants.load_more_tax;
                        setupListItems(index, end, false, false);
                    }
                }
            });
        }else{
            notifyListChanged();
        }
    }
    private void updateUI(final boolean connection_error){
        getActivity().runOnUiThread(new
                                            Runnable() {
                                                @Override
                                                public void run() {
            if (connection_error) {
                no_connection_layout.setVisibility(View.VISIBLE);
                recList.setVisibility(View.GONE);
                layout_no_publications.setVisibility(View.GONE);
                pbLoading_emergent.setVisibility(View.GONE);
            }else if (getEmergents()!=null && getEmergents().size()>0) {
                no_connection_layout.setVisibility(View.GONE);
                recList.setVisibility(View.VISIBLE);
                layout_no_publications.setVisibility(View.GONE);
                pbLoading_emergent.setVisibility(View.GONE);
            }else{
                no_connection_layout.setVisibility(View.GONE);
                recList.setVisibility(View.GONE);
                layout_no_publications.setVisibility(View.VISIBLE);
                pbLoading_emergent.setVisibility(View.GONE);
            }
        }
    });
    }
    private ArrayList<PublicationCardViewModel> getEmergents(){return emergents;}
    private void loadingView(){
        no_connection_layout.setVisibility(View.GONE);
        recList.setVisibility(View.GONE);
        layout_no_publications.setVisibility(View.GONE);
        pbLoading_emergent.setVisibility(View.VISIBLE);
    }
}
