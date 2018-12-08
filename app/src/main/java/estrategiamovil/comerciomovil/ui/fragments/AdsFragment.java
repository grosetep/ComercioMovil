package estrategiamovil.comerciomovil.ui.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import estrategiamovil.comerciomovil.modelo.CategoryViewModel;
import estrategiamovil.comerciomovil.modelo.PublicationCardViewModel;
import estrategiamovil.comerciomovil.modelo.SubCategory;
import estrategiamovil.comerciomovil.modelo.SubSubCategory;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Connectivity;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.GeneralFunctions;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.ui.activities.SelectCategoryActivity;
import estrategiamovil.comerciomovil.ui.adapters.PublicationAdapter;
import estrategiamovil.comerciomovil.ui.adapters.SolventRecyclerViewAdapter;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdsFragment extends Fragment {
    private ArrayList<PublicationCardViewModel> ads = new ArrayList<>();
    private boolean request_category = false;
    private boolean request_query = false;
    private RecyclerView cardList_ads;
    private SwipeRefreshLayout swipeRefresh_ads;
    private TextView text_change_category;
    private EditText text_change_filter;
    private LinearLayout change_ads_container_category;
    private RelativeLayout layout_no_publications;
    private AppCompatButton button_retry_search;
    private RelativeLayout no_connection_layout;
    private ProgressBar pbLoading_ads;
    private StaggeredGridLayoutManager llm;
    private static final String TAG = AdsFragment.class.getSimpleName();
    private Gson gson = new Gson();
    private static final int SELECT_CATEGORY_ADS = 1;
    private final int REQUEST_ADS = 2;
    private static HashMap<String, String> params = new HashMap<> ();
    private static SolventRecyclerViewAdapter adapter;
    private AppCompatButton button_retry;
    public final boolean load_initial = true;

    private CategoryViewModel category;
    private SubCategory sub_category;
    private SubSubCategory sub_sub_category;
    private String query;

    public static AdsFragment createInstance(HashMap<String, String> arguments){
        AdsFragment fragment = new AdsFragment();
        if (arguments!=null)
            fragment.setParams(arguments);
        return fragment;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    public AdsFragment() {
        // Required empty public constructor
    }

    public static HashMap<String, String> getParams() {
        return params;
    }

    public static void setParams(HashMap<String, String> params) {
        AdsFragment.params = params;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_ads, container, false);
        layout_no_publications = (RelativeLayout)rootView.findViewById(R.id.layout_no_publications);
        button_retry_search = (AppCompatButton) rootView.findViewById(R.id.button_retry_search);
        no_connection_layout = (RelativeLayout) rootView.findViewById(R.id.no_connection_layout);
        pbLoading_ads = (ProgressBar) rootView.findViewById(R.id.pbLoading_ads);
        cardList_ads = (RecyclerView) rootView.findViewById(R.id.cardList_ads);
        cardList_ads.setHasFixedSize(true);
        //cardList_ads.setItemAnimator(new DefaultItemAnimator());
        cardList_ads.setItemAnimator(null);
        llm = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cardList_ads.setLayoutManager(llm);

        text_change_category = (TextView) rootView.findViewById(R.id.text_change_category);
        change_ads_container_category = (LinearLayout) rootView.findViewById(R.id.change_ads_container_category);
        change_ads_container_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectCategoryActivity.class);
                intent.putExtra(FindBusinessFragment.TYPE_FLOW_CATEGORY,FindBusinessFragment.FLOW_SEARCHING);
                startActivityForResult(intent, SELECT_CATEGORY_ADS);
            }
        });
        text_change_filter = (EditText) rootView.findViewById(R.id.text_change_filter);
        swipeRefresh_ads = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh_ads);
        swipeRefresh_ads.setColorSchemeResources(
                R.color.s1,
                R.color.s2,
                R.color.s3,
                R.color.s4
        );
        swipeRefresh_ads.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d(TAG,"REFRESH DATA.....");
                        boolean first_loading = false;
                        boolean refresh = true;
                        if (request_category) {
                            if (category != null && sub_category != null && sub_sub_category != null)
                                setupListItems(category.getIdCategory(), sub_category.getIdSubCategory(), sub_sub_category.getIdSubSubCategory(),  Constants.cero, Constants.load_more_tax_extended, first_loading,refresh);
                            else
                                setupListItems(null, null, null, Constants.cero, Constants.load_more_tax_extended, first_loading, refresh);
                        }else {
                            SearchByKeyWord(query, Constants.cero, Constants.load_more_tax_extended, false, true);
                        }
                    }
                }
        );
        text_change_filter.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //If the keyevent is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    query = text_change_filter.getText().toString().trim().toLowerCase();
                    Log.d(TAG,"query:"+query);
                    ads.clear();
                    boolean first_loading = true;
                    boolean refresh = false;
                    loadingView();
                    SearchByKeyWord(query,Constants.cero,Constants.load_more_tax_extended, first_loading,refresh);
                    return true;
                }
                return false;
            }
        });
        button_retry = (AppCompatButton) rootView.findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ads!=null)ads.clear();
                loadingView();
                swipeRefresh_ads.setRefreshing(false);
                setupListItems(null,null,null,Constants.cero,Constants.load_more_tax_extended,load_initial,false);
            }
        });
        button_retry_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ads!=null)ads.clear();
                loadingView();
                swipeRefresh_ads.setRefreshing(false);
                setupListItems(null,null,null,Constants.cero,Constants.load_more_tax_extended,load_initial,false);
            }
        });
        cardList_ads.setVisibility(View.GONE);
        pbLoading_ads.setVisibility(View.VISIBLE);

        if (Connectivity.isNetworkAvailable(getContext())) {
            String category_string = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.ads_category_selected);
            String subcategory_string = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.ads_subcategory_selected);
            String sub_subcategory_string = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.ads_sub_subcategory_selected);
            String ads_category_search = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.ads_category_search);
            if (ads!=null) ads.clear();
            if (category_string != null && !category_string.equals("") &&
                    subcategory_string != null && !subcategory_string.equals("") &&
                    sub_subcategory_string != null && !sub_subcategory_string.equals("")) {
                //Log.d(TAG, "Filtro saved: cat:" + category_string + " sub: " + subcategory_string + " subsub:" + sub_subcategory_string + " ads_category_search:" + ads_category_search);
                text_change_category.setText(ads_category_search);

                setupListItems(category_string, subcategory_string, sub_subcategory_string,Constants.cero,Constants.load_more_tax_extended,load_initial,false);
            } else {
                setupListItems(null, null, null,Constants.cero,Constants.load_more_tax_extended,load_initial,false);
            }
        } else {
            showNoConnectionLayout();
        }
        return rootView;
    }

    private void showNoConnectionLayout(){
        pbLoading_ads.setVisibility(View.GONE);
        cardList_ads.setVisibility(View.GONE);
        layout_no_publications.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
    }

    public void SearchByKeyWord(String query,int start, int end,boolean load_initial,boolean isRefresh) {//obtener datos de la publicacion mas datos de las url de imagenes
        request_query = true;
        request_category = false;
        String newUrl = Constants.GET_PUBLICATIONS;
        VolleyGetRequest(newUrl + "?method=getAdsByDescription" + "&query=" + query+"&start="+start+"&end="+end,load_initial,isRefresh);
    }

    public void setupListItems( String category, String subcategory, String sub_subcategory,int start,int end,boolean load_initial, boolean isRefresh) {//obtener datos de la publicacion mas datos de las url de imagenes
        request_query = false;
        request_category = true;
        String newUrl = Constants.GET_PUBLICATIONS;
        //validation
        if (category!=null  && subcategory!=null && sub_subcategory!=null) {
            VolleyGetRequest(newUrl + "?method=getAdsBySubCategory" +
                            "&idCategory=" + category +
                            "&idSubCategory=" + subcategory +
                            "&idSubSubCategory=" + sub_subcategory +
                            "&start="+start+"&end="+end,load_initial,isRefresh);
        }else {//query all ads
            VolleyGetRequest(newUrl+"?method=getAllAds"+"&start="+start+"&end="+end,load_initial,isRefresh);
        }
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
                                        if (isRefresh) {//only update list
                                            processingResponse(response,isRefresh);
                                            swipeRefresh_ads.setRefreshing(false);
                                        }else if (load_initial) {
                                            processingResponseInit(response);
                                        }else {
                                            ads.remove(ads.size() - 1);//delete loading..
                                            adapter.notifyItemRemoved(ads.size());
                                            processingResponse(response,isRefresh);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        if(isRefresh){
                                            swipeRefresh_ads.setRefreshing(false);
                                            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),getActivity());
                                        }else if(!load_initial) {
                                            ads.remove(ads.size() - 1);//delete loading..
                                            adapter.notifyItemRemoved(ads.size());
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
        Log.d(TAG,"addLoading...");
        final boolean isRefresh = false;
        final boolean first_load= false;

        ads.add(null);
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                adapter.notifyItemInserted(ads.size() - 1);
                makeRequest(url, first_load,isRefresh);
            }
        };
        handler.post(r);

    }
    private void processingResponse(JSONObject response,boolean isRefresh) {
        Log.d(TAG,"RESPONSE:"+response.toString());
        ArrayList<PublicationCardViewModel> new_ads = null;
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("publications");
                    new_ads = new ArrayList<PublicationCardViewModel>(Arrays.asList(gson.fromJson(mensaje.toString(), PublicationCardViewModel[].class)));
                    addNewElements(new_ads,isRefresh);
                    break;
                case "2": // NO DATA FOUND
                    Log.d(TAG,"AdsFragment.noData");
                    if (isRefresh && new_ads == null && ads.size()>Constants.cero ){ads.clear();updateUI(false);}
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
            Log.d(TAG, "ads:" + new_publications.size());
            if (isRefresh){Log.d(TAG, "isRefresh..");
                ads.clear();
                ads.addAll(new_publications);
            }else{
                ads.addAll(GeneralFunctions.FilterPublications(ads, new_publications));
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
        }, Constants.cero);
    }



    private void processingResponseInit(JSONObject response) {
        Log.d(TAG,"processingResponse adsinit:"+response.toString());
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("publications");
                    ArrayList<PublicationCardViewModel> new_ads = new ArrayList<PublicationCardViewModel>(Arrays.asList(gson.fromJson(mensaje.toString(), PublicationCardViewModel[].class)));
                    if (new_ads!=null && new_ads.size()>0) {
                        ads.addAll(new_ads);
                    }
                    break;
                case "2": // NO DATA FOUND
                    String mensaje2 = response.getString("message");
                    Log.d(TAG,"AdsFragment.noData..."+mensaje2);
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

            if (connection_error) {
                no_connection_layout.setVisibility(View.VISIBLE);
                cardList_ads.setVisibility(View.GONE);
                layout_no_publications.setVisibility(View.GONE);
                pbLoading_ads.setVisibility(View.GONE);
            }else if (getAds()!=null && getAds().size()>0) {Log.d(TAG,"getAds().size()>0");
                no_connection_layout.setVisibility(View.GONE);
                cardList_ads.setVisibility(View.VISIBLE);
                layout_no_publications.setVisibility(View.GONE);
                pbLoading_ads.setVisibility(View.GONE);
            }else{
                no_connection_layout.setVisibility(View.GONE);
                cardList_ads.setVisibility(View.GONE);
                layout_no_publications.setVisibility(View.VISIBLE);
                pbLoading_ads.setVisibility(View.GONE);
            }

    }
    private ArrayList<PublicationCardViewModel> getAds(){return this.ads;}

    private void setupAdapter(){

        if (cardList_ads.getAdapter()==null) {
            adapter = new SolventRecyclerViewAdapter(getActivity(), ads, cardList_ads);

            cardList_ads.setAdapter(adapter);
            //load more functionallity

            adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (!swipeRefresh_ads.isRefreshing()) {
                        int index = ads != null ? (ads.size()) : Constants.cero;
                        int end = index + Constants.load_more_tax_extended;
                        if (request_category) {
                            if (category != null && sub_category != null && sub_sub_category != null)
                                setupListItems(category.getIdCategory(), sub_category.getIdSubCategory(), sub_sub_category.getIdSubSubCategory(), index, end, false, false);
                            else
                                setupListItems(null, null, null, index, end, false, false);
                        } else if (request_query) {
                            SearchByKeyWord(query, index, end, false, false);
                        } else {
                            if (category != null && sub_category != null && sub_sub_category != null) {
                                setupListItems(category.getIdCategory(), sub_category.getIdSubCategory(), sub_sub_category.getIdSubSubCategory(), index, end, false, false);
                            } else {
                                setupListItems(null, null, null, index, end, false, false);
                            }
                        }
                    }
                }

            });
        }else{
            notifyListChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode) {
            case SELECT_CATEGORY_ADS:
                if (resultCode == Activity.RESULT_OK){
                    category = (CategoryViewModel) data.getSerializableExtra(Constants.CATEGORY_SELECTED);
                    sub_category = (SubCategory) data.getSerializableExtra(Constants.SUBCATEGORY_SELECTED);
                    sub_sub_category = (SubSubCategory) data.getSerializableExtra(Constants.SUBSUBCATEGORY_SELECTED);

                    Log.d(TAG," Activity.RESULT_OK: sub_sub_category:"+sub_sub_category.getIdSubSubCategory() + " " + sub_sub_category.getSubsubcategory());

                    //debug
                    StringBuffer value_property = new StringBuffer();
                    if (category.getIdCategory().equals("0") && sub_category.getIdSubCategory().equals("-1") && sub_sub_category.getIdSubSubCategory().equals("-1")){
                        value_property.append(getString(R.string.promt_all_categories));
                    }
                    if (sub_category.getIdSubCategory().equals("0")){
                        value_property.append("->".concat(getString(R.string.promt_all_subcategories)));
                    }else if(!sub_category.getIdSubCategory().equals("-1")){
                        value_property.append("->".concat(sub_category.getSubcategory()));
                    }
                    if (sub_sub_category.getIdSubSubCategory().equals("0")){
                        value_property.append("->".concat(getString(R.string.promt_all_subcategories)));
                    }else if(!sub_sub_category.getIdSubSubCategory().equals("-1")){
                        value_property.append("->".concat(sub_sub_category.getSubsubcategory()));
                    }
                    ApplicationPreferences.saveLocalPreference(getContext(),Constants.ads_category_selected,category.getIdCategory());
                    ApplicationPreferences.saveLocalPreference(getContext(),Constants.ads_subcategory_selected,sub_category.getIdSubCategory());
                    ApplicationPreferences.saveLocalPreference(getContext(),Constants.ads_sub_subcategory_selected,sub_sub_category.getIdSubSubCategory());
                    ApplicationPreferences.saveLocalPreference(getContext(),Constants.ads_category_search,value_property.toString());

                    Log.d(TAG,"Filtro: cat:" + category.getIdCategory()+" sub: "+sub_category.getIdSubCategory() + "  " + sub_sub_category.getIdSubSubCategory() + " value_property:"+value_property);
                    loadingView();
                    getAds().clear();
                    text_change_category.setText(value_property.toString());
                    setupListItems(category.getIdCategory(),sub_category.getIdSubCategory(),sub_sub_category.getIdSubSubCategory(),Constants.cero,Constants.load_more_tax_extended,true,false);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void loadingView(){
        no_connection_layout.setVisibility(View.GONE);
        cardList_ads.setVisibility(View.GONE);
        layout_no_publications.setVisibility(View.GONE);
        pbLoading_ads.setVisibility(View.VISIBLE);
    }
}
