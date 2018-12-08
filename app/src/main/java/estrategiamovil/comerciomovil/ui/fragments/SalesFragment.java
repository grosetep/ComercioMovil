package estrategiamovil.comerciomovil.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
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
import estrategiamovil.comerciomovil.modelo.Filter;
import estrategiamovil.comerciomovil.modelo.ImageSliderPublication;
import estrategiamovil.comerciomovil.modelo.SalesItem;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.GeneralFunctions;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.ui.activities.GalleryActivity;
import estrategiamovil.comerciomovil.ui.adapters.SalesAdapter;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalesFragment extends Fragment {
    private static ArrayList<SalesItem> sales = new ArrayList<>();
    private ProgressBar pb;

    private Gson gson = new Gson();
    LinearLayoutManager llm;
    private static final String TAG = SalesFragment.class.getSimpleName();
    private RecyclerView recycler_sales;
    private SalesAdapter mAdapter;

    private SwipeRefreshLayout swipeRefresh_sales;
    public final boolean load_initial = true;
    private AppCompatButton button_retry;
    private RelativeLayout no_connection_layout;
    private static boolean list_filtered = false;
    private Filter filter;

    public Filter getFilter() {
        return filter;
    }
    public void setFilter(Filter filter) {
        this.filter = filter;
    }
    public static boolean isList_filtered() {
        return list_filtered;
    }
    public static void setList_filtered(boolean list_filtered) {
        SalesFragment.list_filtered = list_filtered;
    }

    public SalesFragment() { }
    public static SalesFragment newInstance() {
        SalesFragment fragment = new SalesFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sales, container, false);
        pb = (ProgressBar) v.findViewById(R.id.pbLoading_sales);
        pb.setVisibility(View.VISIBLE);
        no_connection_layout = (RelativeLayout) v.findViewById(R.id.no_connection_layout);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(GridLayoutManager.VERTICAL);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recycler_sales = (RecyclerView) v.findViewById(R.id.recycler_sales);
        recycler_sales.setHasFixedSize(true);
        if (recycler_sales.getLayoutManager() == null) {
            recycler_sales.setLayoutManager(llm);
        }
        swipeRefresh_sales = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh_sales);
        swipeRefresh_sales.setColorSchemeResources(
                R.color.s1,
                R.color.s2,
                R.color.s3,
                R.color.s4
        );
        swipeRefresh_sales.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (sales.size()==0) {
                            getSalesByUser(Constants.cero, Constants.load_more_tax, load_initial, false);
                            swipeRefresh_sales.setRefreshing(false);
                        }else{
                        getSalesByUser(Constants.cero, Constants.load_more_tax, load_initial,true);
                        }

        }
                }
        );
        button_retry = (AppCompatButton) v.findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sales.clear();
                loadingView();
                swipeRefresh_sales.setRefreshing(false);
                getSalesByUser( Constants.cero, Constants.load_more_tax, true,false);
            }
        });
        sales.clear();
        getSalesByUser(Constants.cero, Constants.load_more_tax, load_initial,false);
        return v;
    }
    public void reload(){
        sales.clear();
        getSalesByUser(Constants.cero, Constants.load_more_tax, load_initial,false);
    }
    private void addLoadingAndMakeRequest(final String url) {
        final boolean isRefresh = false;
        final boolean first_load= false;

        sales.add(null);
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                mAdapter.notifyItemInserted(sales.size() - 1);
                makeRequest(url, first_load,isRefresh);
            }
        };
        handler.post(r);

    }
    public void startGalleryActivity(SalesItem sale){
        Intent intent = new Intent(getActivity(), GalleryActivity.class);
        Bundle bundle = new Bundle();
        ImageSliderPublication[] gallery = new ImageSliderPublication[1];
        ImageSliderPublication image_gallery = new ImageSliderPublication();

        image_gallery.setResource(sale.getIdVoucher() != null ? Constants.resource_remote : Constants.resource_local);
        image_gallery.setImageName(sale.getImageVoucher());
        image_gallery.setPath(sale.getRouteVoucher());
        image_gallery.setEnableDeletion(String.valueOf(false));
        gallery[0]=image_gallery;

        bundle.putSerializable(Constants.GALLERY,gallery);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    public void getSalesByUser(int start, int end, boolean load_initial,boolean isRefresh) {
        String idUser = ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.localUserId);
        StringBuffer newUrl = new StringBuffer();
        newUrl.append(Constants.GET_SALES);
        newUrl.append("?method=getSalesByUser&idUser="+idUser);
        newUrl.append("&start="+start+"&end="+end);
        //Log.d(TAG,"----------------------------------->3:"+newUrl.toString());
        VolleyGetRequest(newUrl.toString(),load_initial,isRefresh);
    }

    public void VolleyGetRequest(String url, boolean load_initial,boolean isRefresh){
        if (isRefresh) {
            makeRequest(url,load_initial,isRefresh);
        }else if (!load_initial) {
            addLoadingAndMakeRequest(url);


        } else {
            makeRequest(url, load_initial,isRefresh);
        }

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
                                            processResponse(response,isRefresh);
                                            swipeRefresh_sales.setRefreshing(false);
                                        }else if (load_initial) {
                                            processingResponseInit(response);
                                        }else {
                                            sales.remove(sales.size() - 1);//delete loading..
                                            mAdapter.notifyItemRemoved(sales.size());
                                            processResponse(response,isRefresh);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error){
                                        error.printStackTrace();
                                        if(isRefresh){
                                            swipeRefresh_sales.setRefreshing(false);
                                            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),getActivity());
                                        }else if(!load_initial) {
                                            sales.remove(sales.size() - 1);//delete loading..
                                            mAdapter.notifyItemRemoved(sales.size());
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
    private void processingResponseInit(JSONObject response) {
        Log.d(TAG, "processingResponse sales initial....." + response.toString());
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS

                    JSONArray mensaje = response.getJSONArray("result");
                    ArrayList<SalesItem> new_sales = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), SalesItem[].class)));
                    if (new_sales!= null && new_sales.size() > 0) {
                        sales.addAll(new_sales);
                    }
                    break;
                case "2": // NO DATA FOUND
                    String mensaje2 = response.getString("message");

                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            setupAdapter();
            updateUI(false);
        }
    }
    private void processResponse(JSONObject response,boolean isRefresh) {
        Log.d(TAG, "Response:" + response.toString());
        ArrayList<SalesItem> new_sales = null;
        try {
            // Obtener atributo "status"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    // Obtener array "publications" Json
                    JSONArray mensaje = response.getJSONArray("result");
                    new_sales = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), SalesItem[].class)));
                    addNewElements(new_sales,isRefresh);
                    break;
                case "2": // NO DATA FOUND
                    String mensaje2 = response.getString("message");
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            notifyListChanged();
        }
    }
    private void notifyListChanged() {
        Log.d(TAG, "notifyListChanged()...");
        //Load more data for reyclerview
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                mAdapter.setLoaded();
            }
        }, Constants.cero);
    }
    private void addNewElements(ArrayList<SalesItem> new_elements,boolean isRefresh){
        if (new_elements!=null && new_elements.size()>Constants.cero) {
            if (isRefresh){
                sales.clear();
                sales.addAll(new_elements);
            }else{
                sales.addAll(GeneralFunctions.FilterSales(sales, new_elements));
            }
        }
    }

    private void updateUI(final boolean connection_error){
        getActivity().runOnUiThread(new
                                            Runnable() {
                                                @Override
                                                public void run() {
                                                    if (connection_error) {
                                                        no_connection_layout.setVisibility(View.VISIBLE);
                                                        recycler_sales.setVisibility(View.GONE);
                                                        //layout_no_publications.setVisibility(View.GONE);
                                                        pb.setVisibility(View.GONE);
                                                    }else if (getSales()!=null && getSales().size()>0) {
                                                        no_connection_layout.setVisibility(View.GONE);
                                                        recycler_sales.setVisibility(View.VISIBLE);
                                                        //layout_no_publications.setVisibility(View.GONE);
                                                        pb.setVisibility(View.GONE);
                                                    }else{
                                                        no_connection_layout.setVisibility(View.GONE);
                                                        ShowConfirmations.showConfirmationMessage(getString(R.string.promt_business_info_error),getActivity());
            pb.setVisibility(View.GONE);
        }
                                                }
                                            });
    }

    public static ArrayList<SalesItem> getSales() {
        return sales;
    }

    private void setupAdapter(){
        // Set the adapter
        if (recycler_sales.getAdapter()==null) {
            mAdapter = new SalesAdapter(sales, getActivity(),recycler_sales,this);
        recycler_sales.setAdapter(mAdapter);
        if (recycler_sales.getLayoutManager()==null){
            recycler_sales.setLayoutManager(llm);
        }
            mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (!swipeRefresh_sales.isRefreshing()) {
                        int index = sales != null ? (sales.size()) : Constants.cero;
                        int end = index + Constants.load_more_tax;
                        if (isList_filtered())
                            filter(getFilter(), index, end, false, false);
                        else
                            getSalesByUser( index, end, false, false);
                    }
                }
            });
        pb.setVisibility(View.GONE);
        }else{
            notifyListChanged();
    }
    }
    public void filter(Filter filter,int start, int end,boolean load_initial,boolean isRefresh){
        String idUser = ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.localUserId);
        String newUrl = Constants.GET_SALES;
        if (load_initial){
            sales.clear();
        pb.setVisibility(View.VISIBLE);
        recycler_sales.setVisibility(View.GONE);
    }
        if (filter.isAll()) {
            sales.clear();
            setList_filtered(false);
            newUrl +="?method=getSalesByUser&idUser="+idUser+
                    "&start="+Constants.cero+
                    "&end="+ Constants.load_more_tax;
        }else{
            setList_filtered(true);
            setFilter(filter);
            newUrl +="?method=getSalesByFilter&idUser="+idUser+"&date_from="+filter.getDateFrom()+"&date_to="+filter.getDateTo()+
                    "&capture_line="+filter.getCaptureLine()+
                    "&start="+start+
                    "&end="+end;
        }
        Log.d(TAG,"getSalesByFilter--url--------------------------------->3:"+newUrl);
        VolleyGetRequest(newUrl,load_initial,isRefresh);
    }
    private void loadingView(){
        no_connection_layout.setVisibility(View.GONE);
        recycler_sales.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
    }

}
