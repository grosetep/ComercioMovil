package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.RatingItem;
import estrategiamovil.comerciomovil.modelo.RatingItem;
import estrategiamovil.comerciomovil.tools.Connectivity;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.GeneralFunctions;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.ui.adapters.MerchantRatingAdapter;
import estrategiamovil.comerciomovil.ui.adapters.PublicationAdapter;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class PublicationReviewsActivity extends AppCompatActivity {
    public static final String ID_PUBLICATION="id_publication";
    private ArrayList<RatingItem> reviews = new ArrayList<>();
    private RecyclerView cardList_reviews;
    private SwipeRefreshLayout swipeRefresh_pub_reviews;
    private RelativeLayout layout_no_publications;
    private AppCompatButton button_retry_search;
    private TextView text1;
    private RelativeLayout no_connection_layout;
    private ProgressBar pbLoading_pub_reviews;
    private LinearLayoutManager llm;
    private static final String TAG = PublicationReviewsActivity.class.getSimpleName();
    private Gson gson = new Gson();
    private MerchantRatingAdapter adapter;
    private AppCompatButton button_retry;
    public final boolean load_initial = true;
    public String id_publication = "";

    public ArrayList<RatingItem> getReviews() {
        return reviews;
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication_reviews);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_reviews);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initGUI();
        assignActions();
    }
    private void initGUI(){
        layout_no_publications = (RelativeLayout) findViewById(R.id.layout_no_publications);
        button_retry_search = (AppCompatButton) findViewById(R.id.button_retry_search);
        text1 = (TextView) findViewById(R.id.text1);
        no_connection_layout = (RelativeLayout) findViewById(R.id.no_connection_layout);
        pbLoading_pub_reviews  = (ProgressBar) findViewById(R.id.pbLoading_pub_reviews);
        cardList_reviews = (RecyclerView) findViewById(R.id.cardList_reviews);
        cardList_reviews.setItemAnimator(new DefaultItemAnimator());
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        if (cardList_reviews.getLayoutManager()==null){ cardList_reviews.setLayoutManager(llm);}

        button_retry = (AppCompatButton) findViewById(R.id.button_retry);

        pbLoading_pub_reviews.setVisibility(View.VISIBLE);
        cardList_reviews.setVisibility(View.GONE);
        swipeRefresh_pub_reviews= (SwipeRefreshLayout) findViewById(R.id.swipeRefresh_pub_reviews);
        swipeRefresh_pub_reviews.setColorSchemeResources(
                R.color.s1,
                R.color.s2,
                R.color.s3,
                R.color.s4
        );
        Intent i = getIntent();
        id_publication = i.getStringExtra(PublicationReviewsActivity.ID_PUBLICATION);
        if (Connectivity.isNetworkAvailable(getApplicationContext())) {
            reviews.clear();
            setupListItems(Constants.cero,Constants.load_more_tax,load_initial,false);
        }else {
            updateUI(true);
        }

    }
    private void assignActions(){
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviews.clear();
                loadingView();
                swipeRefresh_pub_reviews.setRefreshing(false);
                setupListItems(Constants.cero,Constants.load_more_tax,true,false);
            }
        });
        button_retry_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviews.clear();
                loadingView();
                swipeRefresh_pub_reviews.setRefreshing(false);
                setupListItems(Constants.cero,Constants.load_more_tax,true,false);
            }
        });
        swipeRefresh_pub_reviews.setOnRefreshListener(
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
    }
    public void setupListItems(int start, int end,boolean load_initial,boolean isRefresh) {//obtener datos de la publicacion mas datos de las url de imagenes
        String newUrl = Constants.GET_PUBLICATIONS;
        VolleyGetRequest(newUrl + "?method=getPublicationReviews"+"&id_publication="+id_publication+"&start="+start+"&end="+end,load_initial,isRefresh);
    }
    private void updateUI(final boolean connection_error){
        runOnUiThread(new
            Runnable() {
                @Override
                public void run() {
                    if (connection_error) {
                        no_connection_layout.setVisibility(View.VISIBLE);
                        cardList_reviews.setVisibility(View.GONE);
                        layout_no_publications.setVisibility(View.GONE);
                        pbLoading_pub_reviews.setVisibility(View.GONE);
                    }else if (getReviews()!=null && getReviews().size()>0) {
                        no_connection_layout.setVisibility(View.GONE);
                        cardList_reviews.setVisibility(View.VISIBLE);
                        layout_no_publications.setVisibility(View.GONE);
                        pbLoading_pub_reviews.setVisibility(View.GONE);
                    }else{
                        no_connection_layout.setVisibility(View.GONE);
                        cardList_reviews.setVisibility(View.GONE);
                        layout_no_publications.setVisibility(View.VISIBLE);
                        pbLoading_pub_reviews.setVisibility(View.GONE);
                    }
            }
        });
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
                getInstance(PublicationReviewsActivity.this).
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
                                            swipeRefresh_pub_reviews.setRefreshing(false);
                                        }else if (load_initial) {
                                            processingResponseInit(response);
                                        }else {
                                            reviews.remove(reviews.size() - 1);//delete loading..
                                            adapter.notifyItemRemoved(reviews.size());
                                            processingResponse(response,isRefresh);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        if(isRefresh){
                                            swipeRefresh_pub_reviews.setRefreshing(false);
                                            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),PublicationReviewsActivity.this);
                                        }else if(!load_initial) {
                                            reviews.remove(reviews.size() - 1);//delete loading..
                                            adapter.notifyItemRemoved(reviews.size());
                                            notifyListChanged();
                                            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),PublicationReviewsActivity.this);
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
        reviews.add(null);
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                adapter.notifyItemInserted(reviews.size() - 1);
                makeRequest( url, first_load,isRefresh);
            }
        };
        handler.post(r);

    }
    private void processingResponse(JSONObject response,boolean isRefresh) {
        Log.d(TAG,"reviews:"+response.toString());
        ArrayList<RatingItem> new_reviews = null;
        try {
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("result");
                    new_reviews = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), RatingItem[].class)));
                    addNewElements(new_reviews,isRefresh);
                    break;
                case "2": // NO DATA FOUND
                    String mensaje2 = response.getString("message");
                    text1.setText(mensaje2);
                    if (isRefresh && new_reviews == null && reviews.size()>Constants.cero ){reviews.clear();updateUI(false);}
                    Log.d(TAG,"Reviews..noData..."+mensaje2);
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            notifyListChanged();
        }

    }
    private void addNewElements(ArrayList<RatingItem> new_publications,boolean isRefresh){
        if (new_publications!=null && new_publications.size()>Constants.cero) {
            Log.d(TAG, "new_reviews:" + new_publications.size());
            if (isRefresh){
                reviews.clear();
                reviews.addAll(new_publications);
            }else{
                reviews.addAll(GeneralFunctions.FilterReviews(reviews, new_publications));
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
        Log.d(TAG,"processingResponse reviews");
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("result");
                    ArrayList<RatingItem> new_reviews = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), RatingItem[].class)));
                    if (new_reviews!=null && new_reviews.size()>0) {
                        reviews.addAll(GeneralFunctions.FilterReviews(reviews,new_reviews));
                    }
                    break;
                case "2": // NO DATA FOUND
                    String mensaje2 = response.getString("message");
                    text1.setText(mensaje2);
                    Log.d(TAG,"Reviews...noData...."+mensaje2);
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
        if (cardList_reviews.getAdapter()==null) {
            adapter = new MerchantRatingAdapter(reviews, PublicationReviewsActivity.this,cardList_reviews);

            cardList_reviews.setAdapter(adapter);
            //load more functionallity

            adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (!swipeRefresh_pub_reviews.isRefreshing()) {
                        int index = reviews != null ? (reviews.size()) : 0;
                        int end = index + Constants.load_more_tax;
                        setupListItems(index, end, false, false);
                    }
                }
            });
        }else{
            notifyListChanged();
        }
    }
    private void loadingView(){
        no_connection_layout.setVisibility(View.GONE);
        cardList_reviews.setVisibility(View.GONE);
        layout_no_publications.setVisibility(View.GONE);
        pbLoading_pub_reviews.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
