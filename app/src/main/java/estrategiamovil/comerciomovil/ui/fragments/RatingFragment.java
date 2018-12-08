package estrategiamovil.comerciomovil.ui.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

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

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.BusinessInfo;
import estrategiamovil.comerciomovil.modelo.PublicationCardViewModel;
import estrategiamovil.comerciomovil.modelo.RatingItem;
import estrategiamovil.comerciomovil.modelo.SubCategory;
import estrategiamovil.comerciomovil.modelo.SubSubCategory;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Connectivity;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.DividerItemDecoration;
import estrategiamovil.comerciomovil.tools.GeneralFunctions;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.ui.adapters.MerchantRatingAdapter;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class RatingFragment extends Fragment{
    private ProgressBar pbLoading_rating;
    public static final String MERCHANT = "merchant";
    private RecyclerView recycler_rating;
    private RelativeLayout no_results_layout;
    private RelativeLayout no_connection_layout;
    private BusinessInfo businessInfo;
    private LinearLayoutManager llm;
    private static final String TAG = RatingFragment.class.getSimpleName();
    private MerchantRatingAdapter mAdapter;
    private ArrayList<RatingItem> ratings = new ArrayList<>();;
    private AppCompatButton button_retry;
    public final boolean load_initial = true;
    private SwipeRefreshLayout swipeRefresh_rating;

    public RatingFragment() {
        // Required empty public constructor
    }

    private void showNoConnectionLayout(){
        pbLoading_rating.setVisibility(View.GONE);
        recycler_rating.setVisibility(View.GONE);
        no_results_layout.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
    }
    public static RatingFragment newInstance(BusinessInfo businessInfo) {
        RatingFragment fragment = new RatingFragment();
        Bundle args = new Bundle();
        args.putSerializable(MERCHANT,businessInfo);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            businessInfo = (BusinessInfo) getArguments().getSerializable(MERCHANT);
        }


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_rating, container, false);
        initGUI(v);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratings.clear();
                loadingView();
                swipeRefresh_rating.setRefreshing(false);
                setupListItems( Constants.cero, Constants.load_more_tax, true,false);
            }
        });
        pbLoading_rating.setVisibility(View.VISIBLE);
        recycler_rating.setVisibility(View.GONE);

            if (Connectivity.isNetworkAvailable(getContext())) {
            ratings.clear();
            setupListItems( Constants.cero, Constants.load_more_tax, load_initial,false);
            }else {
            updateUI(true);
            }


        return v;
    }
    public void setupListItems(int start, int end, boolean load_initial,boolean isRefresh) {//obtener datos de la publicacion mas datos de las url de imagenes
        String newUrl = Constants.GET_PUBLICATIONS;
        VolleyGetRequest(newUrl + "?method=getMerchantRating&id_merchant="+businessInfo.getIdUser() + "&start=" + start + "&end=" + end, load_initial,isRefresh);
    }
    public void VolleyGetRequest(final String url, boolean load_initial,boolean isRefresh) {
        Log.d(TAG, "VolleyGetRequest Products:" + url);
        if (isRefresh) {
            makeRequest(url,load_initial,isRefresh);
        }else if (!load_initial) {
            addLoadingAndMakeRequest(url);


        } else {
            makeRequest(url, load_initial,isRefresh);
        }
    }
    private void addLoadingAndMakeRequest(final String url) {
        Log.d(TAG, "addLoading...");
        final boolean isRefresh = false;
        final boolean first_load= false;

        ratings.add(null);
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                mAdapter.notifyItemInserted(ratings.size() - 1);
                makeRequest(url, first_load,isRefresh);
            }
        };
        handler.post(r);

    }
    private void makeRequest(String url, final boolean load_initial, final boolean isRefresh) {
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
                                        if (isRefresh) {//only update list
                                            processingResponse(response,isRefresh);
                                            swipeRefresh_rating.setRefreshing(false);
                                        }else if (load_initial) {
                                            processingResponseInit(response);
                                        }else {
                                            ratings.remove(ratings.size() - 1);//delete loading..
                                            mAdapter.notifyItemRemoved(ratings.size());
                                            processingResponse(response,isRefresh);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        if(isRefresh){
                                            swipeRefresh_rating.setRefreshing(false);
                                            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),getActivity());
                                        }else if(!load_initial) {
                                            ratings.remove(ratings.size() - 1);//delete loading..
                                            mAdapter.notifyItemRemoved(ratings.size());
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
    private void processingResponse(JSONObject response,boolean isRefresh) {
        Log.d(TAG, "RESPONSE:" + response.toString());

        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    Gson gson = new Gson();
                    JSONArray mensaje = response.getJSONArray("result");
                    ArrayList<RatingItem> new_ratings = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), RatingItem[].class)));
                    addNewElements(new_ratings,isRefresh);
                    break;
                case "2": // NO DATA FOUND
                    String mensaje2 = response.getString("message");
                    Log.d(TAG, "RatingFragment.noData..." + mensaje2);
                    break;
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());

        }finally {
            notifyListChanged();
    }
    }
    private void addNewElements(ArrayList<RatingItem> new_ratings, boolean isRefresh){
        if (new_ratings!=null && new_ratings.size()>Constants.cero) {
            Log.d(TAG, "new_ratings:" + new_ratings.size());
            if (isRefresh){Log.d(TAG, "isRefresh..");
                ratings.clear();
                ratings.addAll(new_ratings);
            }else{
                ratings.addAll(GeneralFunctions.FilterReviews(ratings, new_ratings));
            }
        }
    }
    private void notifyListChanged() {
        //Load more data for reyclerview
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                mAdapter.setLoaded();
            }
        }, Constants.cero);
    }
    private void setupAdapter(){
        // Inicializar adaptador
        mAdapter = new MerchantRatingAdapter(ratings,getActivity(),recycler_rating);
        recycler_rating.setAdapter(mAdapter);
        if (recycler_rating.getLayoutManager()==null){ recycler_rating.setLayoutManager(llm);}
        if (ratings.size()>0) {
            pbLoading_rating.setVisibility(View.GONE);
            recycler_rating.setVisibility(View.VISIBLE);
            no_results_layout.setVisibility(View.GONE);
        }else{
            pbLoading_rating.setVisibility(View.GONE);
            recycler_rating.setVisibility(View.GONE);
            no_results_layout.setVisibility(View.VISIBLE);
        }
    }
    private void initGUI(View v){
        pbLoading_rating = (ProgressBar) v.findViewById(R.id.pbLoading_rating);
        recycler_rating = (RecyclerView) v.findViewById(R.id.recycler_rating);
        no_results_layout = (RelativeLayout) v.findViewById(R.id.no_results_layout);
        no_connection_layout = (RelativeLayout) v.findViewById(R.id.no_connection_layout);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_rating.addItemDecoration(new DividerItemDecoration(getActivity()));
        recycler_rating.setHasFixedSize(true);
        button_retry = (AppCompatButton) v.findViewById(R.id.button_retry);
        swipeRefresh_rating = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh_rating);
        swipeRefresh_rating.setColorSchemeResources(
                R.color.s1,
                R.color.s2,
                R.color.s3,
                R.color.s4
        );
        swipeRefresh_rating.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d(TAG,"REFRESH DATA.....");
                        setupListItems( Constants.cero, Constants.load_more_tax, false, true);

                    }
                }
        );
    }
    private void loadingView(){
        no_connection_layout.setVisibility(View.GONE);
        recycler_rating.setVisibility(View.GONE);
        no_results_layout.setVisibility(View.GONE);
        pbLoading_rating.setVisibility(View.VISIBLE);
    }
    private void processingResponseInit(JSONObject response) {
        Log.d(TAG, "processingResponse ratings initial....." + response.toString());
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    Gson gson = new Gson();
                    JSONArray mensaje = response.getJSONArray("result");
                    ArrayList<RatingItem> new_ratings = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), RatingItem[].class)));
                    if (new_ratings != null && new_ratings.size() > 0) {
                        ratings.addAll(new_ratings);
                    }
                    break;
                case "2": // NO DATA FOUND
                    String mensaje2 = response.getString("message");
                    Log.d(TAG, "Ratings.noData..." + mensaje2);
                    break;
    }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            setupAdapter();
            updateUI(false);
        }
    }
    private void updateUI(final boolean connection_error){
        getActivity().runOnUiThread(new
                                            Runnable() {
                                                @Override
                                                public void run() {
            if (connection_error) {
                no_connection_layout.setVisibility(View.VISIBLE);
                recycler_rating.setVisibility(View.GONE);
                no_results_layout.setVisibility(View.GONE);
                pbLoading_rating.setVisibility(View.GONE);
            }else if (getRatings()!=null && getRatings().size()>0) {
                no_connection_layout.setVisibility(View.GONE);
                recycler_rating.setVisibility(View.VISIBLE);
                no_results_layout.setVisibility(View.GONE);
                pbLoading_rating.setVisibility(View.GONE);
            }else{
                no_connection_layout.setVisibility(View.GONE);
                recycler_rating.setVisibility(View.GONE);
                no_results_layout.setVisibility(View.VISIBLE);
                pbLoading_rating.setVisibility(View.GONE);
            }
        }
    });
    }

    public ArrayList<RatingItem> getRatings() {
        return ratings;
    }
}
