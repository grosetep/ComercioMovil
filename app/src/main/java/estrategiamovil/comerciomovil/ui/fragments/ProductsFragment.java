package estrategiamovil.comerciomovil.ui.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import estrategiamovil.comerciomovil.ui.activities.SelectSubCategoryActivity;
import estrategiamovil.comerciomovil.ui.adapters.PublicationAdapter;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment {

    private ArrayList<PublicationCardViewModel> products = new ArrayList<>();
    private static RecyclerView recList;
    private SwipeRefreshLayout swipeRefresh_products;
    private TextView text_change_category;
    private LinearLayout change_products_container;
    private RelativeLayout layout_no_publications;
    private RelativeLayout no_connection_layout;
    private ProgressBar pbLoading_products;
    LinearLayoutManager llm;
    private static final String TAG = ProductsFragment.class.getSimpleName();
    private Gson gson = new Gson();
    private static final int SELECT_CATEGORY = 1;
    //private String products_category;
    private AppCompatButton button_retry;
    private AppCompatButton button_retry_search;
    private static HashMap<String, String> params = new HashMap<>();
    private CategoryViewModel category;
    private SubCategory subcategory;
    private SubSubCategory sub_subcategory;
    public final boolean load_initial = true;
    private PublicationAdapter adapter;

    public static ProductsFragment createInstance(HashMap<String, String> arguments) {
        ProductsFragment fragment = new ProductsFragment();
        fragment.setParams(arguments);
        return fragment;
    }

    public ProductsFragment() {
        // Required empty public constructor
    }

    public static void setParams(HashMap<String, String> params) {
        ProductsFragment.params = params;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_products, container, false);
        //products_category = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.idCategoryProducts);
        layout_no_publications = (RelativeLayout) rootView.findViewById(R.id.layout_no_publications);
        button_retry_search = (AppCompatButton) rootView.findViewById(R.id.button_retry_search);
        no_connection_layout = (RelativeLayout) rootView.findViewById(R.id.no_connection_layout);
        pbLoading_products = (ProgressBar) rootView.findViewById(R.id.pbLoading_products);
        recList = (RecyclerView) rootView.findViewById(R.id.cardList);
        recList.setItemAnimator(new DefaultItemAnimator());
        recList.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        if (recList.getLayoutManager() == null) {
            recList.setLayoutManager(llm);
        }
        swipeRefresh_products = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh_products);
        swipeRefresh_products.setColorSchemeResources(
                R.color.s1,
                R.color.s2,
                R.color.s3,
                R.color.s4
        );
        swipeRefresh_products.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d(TAG,"REFRESH DATA.....");
                        Gson gson = new Gson();
                        String category_string = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.products_category_selected);
                        String subcategory_string = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.products_subcategory_selected);
                        String sub_subcategory_string = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.products_sub_subcategory_selected);

                        if (category_string  !=null && !category_string.equals("") &&
                                subcategory_string != null && !subcategory_string.equals("") &&
                                sub_subcategory_string != null && !sub_subcategory_string.equals("")) {
                            CategoryViewModel category_saved = gson.fromJson(category_string, CategoryViewModel.class);
                            SubCategory subCategory_saved = gson.fromJson(subcategory_string, SubCategory.class);
                            SubSubCategory sub_subCategory_saved = gson.fromJson(sub_subcategory_string, SubSubCategory.class);

                            if (category_saved!=null && subCategory_saved != null && sub_subCategory_saved != null) {
                                Log.d(TAG, "Filtro saved:cat:"+category_saved.getCategory()+ "subcat:" + subCategory_saved.getSubcategory() + " sub: " + sub_subCategory_saved.getSubsubcategory());
                                text_change_category.setText(subCategory_saved.getSubcategory());
                                setupListItems(category_saved, subCategory_saved, sub_subCategory_saved, Constants.cero, Constants.load_more_tax, false, true);
                            }
                        }else {
                            setupListItems(null,null, null, Constants.cero, Constants.load_more_tax, load_initial,true);
                        }
                    }
                }
        );
        text_change_category = (TextView) rootView.findViewById(R.id.text_change_category);
        change_products_container = (LinearLayout) rootView.findViewById(R.id.change_products_container);
        change_products_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(v.getContext(), SelectSubCategoryActivity.class);
                intent.putExtra(Constants.CATEGORY_SELECTED, products_category);
                intent.putExtra(FindBusinessFragment.TYPE_FLOW_CATEGORY, FindBusinessFragment.FLOW_SEARCHING);
                startActivityForResult(intent, SELECT_CATEGORY);*/
                Intent intent = new Intent(getActivity(), SelectCategoryActivity.class);
                intent.putExtra(FindBusinessFragment.TYPE_FLOW_CATEGORY,FindBusinessFragment.FLOW_SEARCHING);
                startActivityForResult(intent, SELECT_CATEGORY);
            }
        });

        button_retry = (AppCompatButton) rootView.findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationPreferences.saveLocalPreference(getContext(), Constants.products_subcategory_selected, "");
                ApplicationPreferences.saveLocalPreference(getContext(), Constants.products_sub_subcategory_selected, "");
                text_change_category.setText("Todas");
                products.clear();
                loadingView();
                swipeRefresh_products.setRefreshing(false);
                setupListItems(null, null, null, Constants.cero, Constants.load_more_tax, true,false);
            }
        });
        button_retry_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                products.clear();
                loadingView();
                swipeRefresh_products.setRefreshing(false);
                setupListItems(null, null, null,Constants.cero,Constants.load_more_tax,true,false);
            }
        });
        pbLoading_products.setVisibility(View.VISIBLE);
        recList.setVisibility(View.GONE);

        if (Connectivity.isNetworkAvailable(getContext())) {
            Gson gson = new Gson();
            products.clear();
            String category_string = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.products_category_selected);
            String subcategory_string = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.products_subcategory_selected);
            String sub_subcategory_string = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.products_sub_subcategory_selected);
            loadingView();
            if (category_string!=null && !category_string.equals("") &&
                subcategory_string != null && !subcategory_string.equals("") &&
                    sub_subcategory_string != null && !sub_subcategory_string.equals("")) {
                CategoryViewModel category_saved = gson.fromJson(category_string, CategoryViewModel.class);
                SubCategory subCategory_saved = gson.fromJson(subcategory_string, SubCategory.class);
                SubSubCategory sub_subCategory_saved = gson.fromJson(sub_subcategory_string, SubSubCategory.class);

                if (subCategory_saved != null && sub_subCategory_saved != null) {
                    Log.d(TAG, "Filtro saved: cat:"+category_saved.getCategory()+" sub:" + subCategory_saved.getSubcategory() + " sub_sub: " + sub_subCategory_saved.getSubsubcategory());
                    text_change_category.setText(subCategory_saved.getSubcategory()!=null && !subCategory_saved.getSubcategory().isEmpty()?subCategory_saved.getSubcategory():category_saved.getCategory());
                    setupListItems(category_saved,subCategory_saved, sub_subCategory_saved, Constants.cero, Constants.load_more_tax, load_initial,false);
                } else {
                    setupListItems(null, null, null, Constants.cero, Constants.load_more_tax, load_initial,false);
                }
            } else {
                setupListItems(null,null, null, Constants.cero, Constants.load_more_tax, load_initial,false);
            }
        } else {
            updateUI(true);
        }

        return rootView;
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
                                            swipeRefresh_products.setRefreshing(false);
                                        }else if (load_initial) {
                                            processingResponseInit(response);
                                        }else {
                                            products.remove(products.size() - 1);//delete loading..
                                            adapter.notifyItemRemoved(products.size());
                                            processingResponse(response,isRefresh);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error){
                                        error.printStackTrace();
                                        if(isRefresh){
                                            swipeRefresh_products.setRefreshing(false);
                                            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),getActivity());
                                        }else if(!load_initial) {
                                            products.remove(products.size() - 1);//delete loading..
                                            adapter.notifyItemRemoved(products.size());
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

    public void setupListItems(CategoryViewModel category,SubCategory subcategory, SubSubCategory sub_subcategory, int start, int end, boolean load_initial,boolean isRefresh) {//obtener datos de la publicacion mas datos de las url de imagenes
        String newUrl = Constants.GET_PUBLICATIONS;
        Log.d(TAG, "setupListItems PRODUCTOS---------------------------------------------load_initial:" + load_initial);
        //validation
        if (subcategory != null && sub_subcategory != null) {
            VolleyGetRequest(newUrl + "?method=getProductsByFilters" +
                    "&idCategory=" + category.getIdCategory() +
                    "&idSubCategory=" + subcategory.getIdSubCategory() +
                    "&idSubSubCategory=" + sub_subcategory.getIdSubSubCategory() +
                    "&start=" + start + "&end=" + end, load_initial,isRefresh);
        } else {//query all categories
            VolleyGetRequest(newUrl + "?method=getAllProducts" + "&start=" + start + "&end=" + end, load_initial,isRefresh);
        }
    }

    private void addLoadingAndMakeRequest(final String url) {
        Log.d(TAG, "addLoading...");
        final boolean isRefresh = false;
        final boolean first_load= false;

        products.add(null);
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                adapter.notifyItemInserted(products.size() - 1);
                makeRequest(url, first_load,isRefresh);
            }
        };
        handler.post(r);

    }

    private void processingResponse(JSONObject response,boolean isRefresh) {
        Log.d(TAG, "RESPONSE:" + response.toString());
        ArrayList<PublicationCardViewModel> new_products = null;
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("publications");
                    new_products = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), PublicationCardViewModel[].class)));
                    addNewElements(new_products,isRefresh);
                    break;
                case "2": // NO DATA FOUND
                    String mensaje2 = response.getString("message");
                    Log.d(TAG, "ProductsFragment.noData..." + mensaje2);
                    if (isRefresh && new_products == null && products.size()>Constants.cero ){products.clear();updateUI(false);}
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
                products.clear();
                products.addAll(new_publications);
            }else{
                products.addAll(GeneralFunctions.FilterPublications(products, new_publications));
            }
        }
    }
    private void notifyListChanged() {
        Log.d(TAG, "notifyListChanged()...");
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
        Log.d(TAG, "processingResponse products initial....." + response.toString());
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS

                    JSONArray mensaje = response.getJSONArray("publications");
                    ArrayList<PublicationCardViewModel> new_products = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), PublicationCardViewModel[].class)));
                    if (new_products != null && new_products.size() > 0) {
                        products.addAll(new_products);
                    }
                    break;
                case "2": // NO DATA FOUND
                    String mensaje2 = response.getString("message");
                    Log.d(TAG, "ProductsFragment.noData..." + mensaje2);
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
        if (recList.getAdapter()==null) {
            adapter = new PublicationAdapter(products, getActivity(), recList);
            recList.setAdapter(adapter);
            //load more functionallity
            adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (!swipeRefresh_products.isRefreshing()) {
                        int index = products != null ? (products.size()) : Constants.cero;
                        int end = index + Constants.load_more_tax;
                        setupListItems(category,subcategory, sub_subcategory, index, end, false, false);
                    }
                }
            });
        }else{
            notifyListChanged();
        }
    }
    private ArrayList<PublicationCardViewModel> getProducts(){return products;}
    private void updateUI(final boolean connection_error){
        getActivity().runOnUiThread(new
                Runnable() {
                    @Override
                    public void run() {
                if (connection_error) {
                    no_connection_layout.setVisibility(View.VISIBLE);
                    recList.setVisibility(View.GONE);
                    layout_no_publications.setVisibility(View.GONE);
                    pbLoading_products.setVisibility(View.GONE);
                }else if (getProducts()!=null && getProducts().size()>0) {
                    no_connection_layout.setVisibility(View.GONE);
                    recList.setVisibility(View.VISIBLE);
                    layout_no_publications.setVisibility(View.GONE);
                    pbLoading_products.setVisibility(View.GONE);
                }else{
                    no_connection_layout.setVisibility(View.GONE);
                    recList.setVisibility(View.GONE);
                    layout_no_publications.setVisibility(View.VISIBLE);
                    pbLoading_products.setVisibility(View.GONE);
                }
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode) {
            case SELECT_CATEGORY:
                if (resultCode == Activity.RESULT_OK){
                    //*************************solo productos inicio**********************************************
                    /*CategoryViewModel category = new CategoryViewModel();
                    category.setIdCategory(products_category!=null && !products_category.equals("")?products_category:"6");
                    category.setCategory("Todos los productos");
                    subcategory = (SubCategory) data.getSerializableExtra(Constants.SUBCATEGORY_SELECTED);
                    sub_subcategory = (SubSubCategory) data.getSerializableExtra(Constants.SUBSUBCATEGORY_SELECTED);
                    if (subcategory!=null && sub_subcategory!=null){
                        Gson gson = new Gson();
                        ApplicationPreferences.saveLocalPreference(getContext(),Constants.products_subcategory_selected,gson.toJson(subcategory));
                        ApplicationPreferences.saveLocalPreference(getContext(),Constants.products_sub_subcategory_selected,gson.toJson(sub_subcategory));

                        text_change_category.setText(subcategory.getSubcategory());
                        Log.d(TAG,"Filtro: cat:" + subcategory.getSubcategory()+" sub: "+sub_subcategory.getSubsubcategory());
                        products.clear();
                        loadingView();
                        setupListItems(subcategory,sub_subcategory,Constants.cero,Constants.load_more_tax,true,false);
                    }*/
                    //*************************solo productos fin**********************************************
                    //***************************Todas las categorias *****************************************
                    category = (CategoryViewModel) data.getSerializableExtra(Constants.CATEGORY_SELECTED);
                    subcategory = (SubCategory) data.getSerializableExtra(Constants.SUBCATEGORY_SELECTED);
                    sub_subcategory = (SubSubCategory) data.getSerializableExtra(Constants.SUBSUBCATEGORY_SELECTED);

                    Log.d(TAG," Activity.RESULT_OK----------------: category:"+category.getIdCategory()+"       sub_category:"+subcategory.getIdSubCategory() + " " + sub_subcategory.getIdSubSubCategory());

                    //debug
                    StringBuffer value_property = new StringBuffer();
                    if (category.getIdCategory().equals("0") && subcategory.getIdSubCategory().equals("-1") && sub_subcategory.getIdSubSubCategory().equals("-1")){
                        value_property.append(getString(R.string.promt_all_categories));
                    }
                    if (subcategory.getIdSubCategory().equals("0")){
                        value_property.append("->".concat(getString(R.string.promt_all_subcategories)));
                    }else if(!subcategory.getIdSubCategory().equals("-1")){
                        value_property.append("->".concat(subcategory.getSubcategory()));
                    }
                    if (sub_subcategory.getIdSubSubCategory().equals("0")){
                        value_property.append("->".concat(getString(R.string.promt_all_subcategories)));
                    }else if(!sub_subcategory.getIdSubSubCategory().equals("-1")){
                        value_property.append("->".concat(sub_subcategory.getSubsubcategory()));
                    }
                    Gson gson = new Gson();
                    ApplicationPreferences.saveLocalPreference(getContext(),Constants.products_category_selected,gson.toJson(category));
                    ApplicationPreferences.saveLocalPreference(getContext(),Constants.products_subcategory_selected,gson.toJson(subcategory));
                    ApplicationPreferences.saveLocalPreference(getContext(),Constants.products_sub_subcategory_selected,gson.toJson(sub_subcategory));
                    text_change_category.setText(value_property.toString());
                    Log.d(TAG,"Filtro: cat:" + subcategory.getSubcategory()+" sub: "+sub_subcategory.getSubsubcategory());
                    products.clear();
                    loadingView();
                    setupListItems(category,subcategory,sub_subcategory,Constants.cero,Constants.load_more_tax,true,false);
                    //***************************Todas las categorias fin *****************************************
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void loadingView(){
        no_connection_layout.setVisibility(View.GONE);
        recList.setVisibility(View.GONE);
        layout_no_publications.setVisibility(View.GONE);
        pbLoading_products.setVisibility(View.VISIBLE);
    }
}
