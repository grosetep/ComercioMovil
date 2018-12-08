package estrategiamovil.comerciomovil.ui.fragments;


import android.app.Activity;
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

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Filter;
import estrategiamovil.comerciomovil.modelo.PublicationUser;
import estrategiamovil.comerciomovil.modelo.PurchaseItem;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.GeneralFunctions;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.ui.activities.PurchasesActivity;
import estrategiamovil.comerciomovil.ui.activities.VoucherActivity;
import estrategiamovil.comerciomovil.ui.adapters.PublicationUserAdapter;
import estrategiamovil.comerciomovil.ui.adapters.PurchasesAdapter;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class PurchasesFragment extends Fragment {
    private static ArrayList<PurchaseItem> purchases = new ArrayList<>();
    private ProgressBar pb;

    private Gson gson = new Gson();
    LinearLayoutManager llm;
    private static final String TAG = PurchasesFragment.class.getSimpleName();
    private RecyclerView recycler_purchases;
    private PurchasesAdapter mAdapter;
    private SwipeRefreshLayout swipeRefresh_purchases;
    public final boolean load_initial = true;
    private AppCompatButton button_retry;
    private RelativeLayout no_connection_layout;
    private static boolean list_filtered = false;
    private Filter filter;
    private static final int VOUCHER_REQUEST = 1;

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
        PurchasesFragment.list_filtered = list_filtered;
    }
    public PurchasesFragment() { }
    public static PurchasesFragment newInstance() {
        PurchasesFragment fragment = new PurchasesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_purchases, container, false);
        no_connection_layout = (RelativeLayout) v.findViewById(R.id.no_connection_layout);
        pb = (ProgressBar) v.findViewById(R.id.pbLoading_purchases);
        pb.setVisibility(View.VISIBLE);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(GridLayoutManager.VERTICAL);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recycler_purchases = (RecyclerView) v.findViewById(R.id.recycler_purchases);
        recycler_purchases.setHasFixedSize(true);
        if (recycler_purchases.getLayoutManager() == null) {
            recycler_purchases.setLayoutManager(llm);
        }
        swipeRefresh_purchases = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh_purchases);
        swipeRefresh_purchases.setColorSchemeResources(
                R.color.s1,
                R.color.s2,
                R.color.s3,
                R.color.s4
        );
        swipeRefresh_purchases.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d(TAG,"REFRESH DATA.....");
                        if (purchases.size()==0){
                            getPurchasesByUser(Constants.cero, Constants.load_more_tax, load_initial, false);
                            swipeRefresh_purchases.setRefreshing(false);
                        }else {
                        getPurchasesByUser(Constants.cero, Constants.load_more_tax, load_initial,true);
                        }

        }
                }
        );
        button_retry = (AppCompatButton) v.findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchases.clear();
                loadingView();
                swipeRefresh_purchases.setRefreshing(false);
                getPurchasesByUser( Constants.cero, Constants.load_more_tax, true,false);
            }
        });
        purchases.clear();
        getPurchasesByUser(Constants.cero, Constants.load_more_tax, load_initial,false);
        return v;
    }
    private void addLoadingAndMakeRequest(final String url) {
        Log.d(TAG, "addLoading...");
        final boolean isRefresh = false;
        final boolean first_load= false;

        purchases.add(null);
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                mAdapter.notifyItemInserted(purchases.size() - 1);
                makeRequest(url, first_load,isRefresh);
            }
        };
        handler.post(r);

    }
    public void getPurchasesByUser(int start, int end, boolean load_initial,boolean isRefresh) {
        String idUser = ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.localUserId);
        StringBuffer newUrl = new StringBuffer();
        newUrl.append(Constants.GET_PURCHASES);
        newUrl.append("?method=getPurchasesByUser&idUser="+idUser);
        newUrl.append("&start="+start+"&end="+end);
        Log.d(TAG,"----------------------------------->"+newUrl.toString());
        VolleyGetRequest(newUrl.toString(),load_initial,isRefresh);

    }
    public void startActivityVoucher(PurchaseItem item){
        Intent intent = new Intent(getContext(),VoucherActivity.class);
        intent.putExtra(VoucherActivity.PURCHASE, item);
        startActivityForResult(intent, VOUCHER_REQUEST);
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
                                            swipeRefresh_purchases.setRefreshing(false);
                                        }else if (load_initial) {
                                            processingResponseInit(response);
                                        }else {
                                            purchases.remove(purchases.size() - 1);//delete loading..
                                            mAdapter.notifyItemRemoved(purchases.size());
                                            processResponse(response,isRefresh);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error){
                                        error.printStackTrace();
                                        if(isRefresh){
                                            swipeRefresh_purchases.setRefreshing(false);
                                            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),getActivity());
                                        }else if(!load_initial) {
                                            purchases.remove(purchases.size() - 1);//delete loading..
                                            mAdapter.notifyItemRemoved(purchases.size());
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
        Log.d(TAG, "processingResponse purchases initial....." + response.toString());
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS

                    JSONArray mensaje = response.getJSONArray("result");
                    ArrayList<PurchaseItem> new_sales = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), PurchaseItem[].class)));
                    if (new_sales!= null && new_sales.size() > 0) {
                        purchases.addAll(new_sales);
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
        ArrayList<PurchaseItem> new_purchases = null;
        try {
            // Obtener atributo "status"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("result");
                    new_purchases = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), PurchaseItem[].class)));
                    addNewElements(new_purchases,isRefresh);
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
    private void addNewElements(ArrayList<PurchaseItem> new_elements,boolean isRefresh){
        if (new_elements!=null && new_elements.size()>Constants.cero) {
            Log.d(TAG, "new_elements:" + new_elements.size());
            if (isRefresh){Log.d(TAG, "isRefresh..");
                purchases.clear();
                purchases.addAll(new_elements);
            }else{
                purchases.addAll(GeneralFunctions.FilterPurchases(purchases, new_elements));
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
                            recycler_purchases.setVisibility(View.GONE);
                            pb.setVisibility(View.GONE);
                        }else if (getSales()!=null && getSales().size()>0) {
                            no_connection_layout.setVisibility(View.GONE);
                            recycler_purchases.setVisibility(View.VISIBLE);
                            pb.setVisibility(View.GONE);
                        }else{
                            no_connection_layout.setVisibility(View.GONE);
                            ShowConfirmations.showConfirmationMessage(getString(R.string.promt_business_info_error),getActivity());
            pb.setVisibility(View.GONE);
        }
                    }
                });
    }

    public static ArrayList<PurchaseItem> getSales() {
        return purchases;
    }

    private void setupAdapter(){
        // Set the adapter
        if (recycler_purchases.getAdapter()==null) {
            mAdapter = new PurchasesAdapter(purchases, getActivity(),recycler_purchases,this);
        recycler_purchases.setAdapter(mAdapter);
        if (recycler_purchases.getLayoutManager()==null){
            recycler_purchases.setLayoutManager(llm);
        }
            mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (!swipeRefresh_purchases.isRefreshing()) {
                        int index = purchases != null ? (purchases.size()) : Constants.cero;
                        int end = index + Constants.load_more_tax;
                        if (isList_filtered())
                            filter(getFilter(), index, end, false, false);
                        else
                            getPurchasesByUser( index, end, false, false);
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
        String newUrl = Constants.GET_PURCHASES;
        if (load_initial){
            purchases.clear();
        pb.setVisibility(View.VISIBLE);
        recycler_purchases.setVisibility(View.GONE);
    }
        if (filter.isAll()) {
            purchases.clear();
            setList_filtered(false);
            newUrl +="?method=getPurchasesByUser&idUser="+idUser+
                    "&start="+Constants.cero+
                    "&end="+ Constants.load_more_tax;
        }else{
            setList_filtered(true);
            setFilter(filter);
            newUrl +="?method=getPurchasesByFilter&idUser="+idUser+"&date_from="+filter.getDateFrom()+"&date_to="+filter.getDateTo()+
                    "&capture_line="+filter.getCaptureLine()+
                    "&start="+start+
                    "&end="+end;
        }
        Log.d(TAG,"getSalesByFilter--url--------------------------------->3:"+newUrl);
        VolleyGetRequest(newUrl,load_initial,isRefresh);
    }
    private void loadingView(){
        no_connection_layout.setVisibility(View.GONE);
        recycler_purchases.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        switch(requestCode) {
            case VOUCHER_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    purchases.clear();
                    loadingView();
                    swipeRefresh_purchases.setRefreshing(false);
                    getPurchasesByUser( Constants.cero, Constants.load_more_tax, true,false);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
