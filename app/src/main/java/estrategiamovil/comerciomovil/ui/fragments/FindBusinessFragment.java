package estrategiamovil.comerciomovil.ui.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
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
import java.util.List;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.BusinessInfo;
import estrategiamovil.comerciomovil.modelo.CategoryViewModel;
import estrategiamovil.comerciomovil.modelo.City;
import estrategiamovil.comerciomovil.modelo.SubCategory;
import estrategiamovil.comerciomovil.modelo.SubSubCategory;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Connectivity;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.DividerItemDecoration;
import estrategiamovil.comerciomovil.ui.activities.CityPreferencesActivity;
import estrategiamovil.comerciomovil.ui.activities.SelectCategoryActivity;
import estrategiamovil.comerciomovil.ui.adapters.FindBusinessAdapter;
import estrategiamovil.comerciomovil.ui.adapters.SuggestionAdapter;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindBusinessFragment extends Fragment {
    private AutoCompleteTextView text_filter;
    private ImageView image_search;
    private ProgressBar pbLoading_business;
    private static BusinessInfo[] business = null;
    private static final String TAG = FindBusinessFragment.class.getSimpleName();
    private Gson gson = new Gson();
    LinearLayoutManager llm;
    private TextView text_change_state;
    private RelativeLayout no_results_layout;
    private RelativeLayout no_connection_layout;
    private LinearLayout change_city_container;
    private LinearLayout change_category_container;
    private static RecyclerView mListView;
    private static boolean list_changed = false;
    private static FindBusinessAdapter mAdapter;
    private AppCompatButton button_retry;
    private TextView text_change_category;
    private CategoryViewModel category;
    private SubCategory sub_category;
    private SubSubCategory sub_sub_category;
    private City citySelected;
    private final int REQUEST_SEARCH_CITY = 1;
    public static final String FLOW_SEARCHING = "flow_searching";
    public static final String TYPE_FLOW_CATEGORY = "type_flow_category";


    public static boolean getStatusListChanged(){return FindBusinessFragment.list_changed;}
    public static void setStatusListChanged(boolean value){FindBusinessFragment.list_changed=value; }
    public static FindBusinessFragment newInstance() {
        FindBusinessFragment fragment = new FindBusinessFragment();
        return fragment;
    }
    public FindBusinessFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_find_business, container, false);
        pbLoading_business = (ProgressBar) v.findViewById(R.id.pbLoading_business);
        pbLoading_business.setVisibility(View.VISIBLE);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mListView = (RecyclerView) v.findViewById(R.id.list);
        mListView.addItemDecoration(new DividerItemDecoration(getActivity()));
        mListView.setHasFixedSize(true);
        text_change_category = (TextView)v.findViewById(R.id.text_change_category);
        no_results_layout = (RelativeLayout)v.findViewById(R.id.no_results_layout);
        no_connection_layout = (RelativeLayout) v.findViewById(R.id.no_connection_layout);
        change_city_container = (LinearLayout) v.findViewById(R.id.change_city_container);
        change_city_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                String idCountry = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.countryUser);
                Intent intent = new Intent(context, CityPreferencesActivity.class);
                intent.putExtra(CityPreferencesActivity.EXTRA_NAME, idCountry);
                intent.putExtra(CityPreferencesActivity.EXTRA_FLOW, Constants.find_flow);
                intent.putExtra(CityPreferencesActivity.EXTRA_COUNTRY, ApplicationPreferences.getLocalStringPreference(getContext(),Constants.nameCountryUser));
                startActivityForResult(intent,REQUEST_SEARCH_CITY);
            }
        });
        change_category_container = (LinearLayout) v.findViewById(R.id.change_category_container);
        change_category_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectCategoryActivity.class);
                intent.putExtra(FindBusinessFragment.TYPE_FLOW_CATEGORY,FindBusinessFragment.FLOW_SEARCHING);
                startActivityForResult(intent, PublishFragment.SELECT_CATEGORY);
            }
        });
        text_change_state = (TextView) v.findViewById(R.id.text_change_state);
        button_retry = (AppCompatButton) v.findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initProcess(true,false);
                getAllBusiness();
            }
        });
        initProcess(true,false);
        String first_time = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.find_business_module_created);

        text_filter = (AutoCompleteTextView) v.findViewById(R.id.text_filter);
        text_filter.setAdapter(new SuggestionAdapter(getActivity()));
        image_search = (ImageView) v.findViewById(R.id.image_search);
        image_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBusinessByFilter();
            }
        });
        if (first_time.equals("")){
            //general preferences
            String id_city_general_preference = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.cityUser);
            String city_general_preference = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.nameCityUser);
            //setting values
            text_change_state.setText(city_general_preference);
            ApplicationPreferences.saveLocalPreference(getContext(),Constants.business_id_city,id_city_general_preference);
            ApplicationPreferences.saveLocalPreference(getContext(),Constants.business_city,city_general_preference);
        }

        if (business==null || FindBusinessFragment.getStatusListChanged()) {
            if (Connectivity.isNetworkAvailable(getContext())) {
                FindBusinessFragment.setStatusListChanged(false);
                if (first_time.equals("")) {
                    getAllBusiness();
                    ApplicationPreferences.saveLocalPreference(getContext(),Constants.find_business_module_created,String.valueOf(false));
                }else {
                    searchBusinesses();
                }
            }else {
                showNoConnectionLayout();
            }

        }else{
            setupAdapter();
        }

        return v;
    }
    private void initProcess(boolean flag, boolean no_result){
        pbLoading_business.setVisibility(flag?View.VISIBLE:View.GONE);
        mListView.setVisibility(flag?View.INVISIBLE:View.VISIBLE);
        no_results_layout.setVisibility(flag?View.GONE:(no_result?View.VISIBLE:View.GONE));
        no_connection_layout.setVisibility(View.GONE);
    }
    private void showNoConnectionLayout(){
        pbLoading_business.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
        no_results_layout.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
    }
    public void getAllBusiness() {
        String newUrl = Constants.GET_BUSINESS;
        String idCountry = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.countryUser);
        String idCity = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.business_id_city);
        String nameCity = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.business_city);
        if (nameCity!=null){text_change_state.setText(nameCity);}
        text_filter.setText("");
        Log.d(TAG,newUrl + "?method=getAllBusiness&idCountry="+idCountry+"&idCity="+idCity);
        VolleyGetRequest(newUrl + "?method=getAllBusiness&idCountry="+idCountry+"&idCity="+idCity);

    }
    private void searchBusinessByFilter(){
        /* calling query*/
        if (text_filter.getText().toString().trim().length()>0) {
            String[] query = text_filter.getText().toString().split("->");
            StringBuffer url = new StringBuffer();
            String idCountry = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.countryUser);
            String idCity = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.business_id_city);
            url.append(Uri.parse(Constants.GET_BUSINESS).buildUpon().
                    appendQueryParameter("method", "searchBusinessByFilter").
                    appendQueryParameter("SubCategory", query[0]).
                    appendQueryParameter("SubSubCategory", query.length==2?query[1]:"").
                    appendQueryParameter("idCountry", idCountry).
                    appendQueryParameter("idCity", idCity != null ? idCity : "").build().toString());
            Log.d(TAG, url.toString());
            initProcess(true,false);
            VolleyGetRequest(url.toString());
        }
    }
    private void searchBusinesses(){
        Log.d(TAG,"searchBusinesses...");
        /*get Preferences saved*/
        String idCategory = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.business_category_id);
        String idSubCategory = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.business_subcategory_id);
        String idSubsubCategory = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.business_sub_subcategory_id);
        String category_label = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.business_category_name);
        String idCity = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.business_id_city);
        String city_name = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.business_city);
        /* setting values*/
        Log.d(TAG,"check saved preferred values filters: category_label:" + category_label + " city_name:"+city_name);
        if(category_label!=null && category_label.length()>0){text_change_category.setText(category_label);}
        if(city_name!=null){text_change_state.setText(city_name);}
        /* calling query*/
        StringBuffer url = new StringBuffer();
        String idCountry = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.countryUser);
        url.append(Uri.parse(Constants.GET_BUSINESS).buildUpon().
                appendQueryParameter("method", "searchBusinesses").
                appendQueryParameter("idCategory", idCategory!=null?idCategory:"").
                appendQueryParameter("idSubCategory", idSubCategory!=null?idSubCategory:"").
                appendQueryParameter("idSubSubCategory", idSubsubCategory!=null?idSubsubCategory:"").
                appendQueryParameter("idCountry", idCountry).
                appendQueryParameter("idCity",idCity!=null? idCity:"").build().toString());
        Log.d(TAG,url.toString());
        VolleyGetRequest(url.toString());
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

                                            processResponse(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        String message = "Verifique su conexión a internet.";
                                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                                        clear();
                                        initProcess(false,true);
                                        showNoConnectionLayout();
                                    }
                                }

                        ).setRetryPolicy(new DefaultRetryPolicy(
                                Constants.MY_SOCKET_TIMEOUT_MS,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                );
    }
    private void onSuccess(){
        setupAdapter();
        pbLoading_business.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
    }
    public static FindBusinessFragment createInstance(){
        FindBusinessFragment fragment = new FindBusinessFragment();
        return fragment;
    }
    private void processResponse(JSONObject response) {
        Log.d(TAG, "Response:" + response.toString());
        try {
            // Obtener atributo "status"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("result");
                    business = gson.fromJson(mensaje.toString(), BusinessInfo[].class);
                    onSuccess();
                    initProcess(false,false);
                    break;
                case "2": // NO DATA FOUND
                    clear();
                    initProcess(false,true);
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            clear();
            initProcess(false,true);
        }
    }
    private void setupAdapter(){
        // Set the adapter
        mAdapter = new FindBusinessAdapter(getActivity(),Arrays.asList(business));
        mListView.setAdapter(mAdapter);
        mListView.invalidate();
        if (mListView.getLayoutManager()==null){
                mListView.setLayoutManager(llm);
        }
        pbLoading_business.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        no_results_layout.setVisibility(View.GONE);
    }
    @Override
    public void onStop(){
        //reset data from list publications for reloading update when user go back to the application
        business = null;
        text_filter.setText("");
        super.onStop();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SEARCH_CITY:
                if (resultCode == Activity.RESULT_OK){
                    citySelected = (City) data.getSerializableExtra(Constants.CITY_SELECTED);
                    ApplicationPreferences.saveLocalPreference(getContext(),Constants.business_id_city,citySelected.getIdCity());
                    ApplicationPreferences.saveLocalPreference(getContext(),Constants.business_city,citySelected.getCity());
                    Log.d(TAG,"Data selected:" + citySelected.getIdCity() +" - "+ citySelected.getCity());
                    text_change_state.setText(citySelected.getCity());
                    pbLoading_business.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.INVISIBLE);
                    no_results_layout.setVisibility(View.GONE);
                    searchBusinesses();
                }
                break;
            case PublishFragment.SELECT_CATEGORY:
                if (resultCode == Activity.RESULT_OK) {
                    category = (CategoryViewModel) data.getSerializableExtra(Constants.CATEGORY_SELECTED);
                    sub_category = (SubCategory) data.getSerializableExtra(Constants.SUBCATEGORY_SELECTED);
                    sub_sub_category = (SubSubCategory) data.getSerializableExtra(Constants.SUBSUBCATEGORY_SELECTED);


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

                    text_change_category.setText(value_property.toString());
                    ApplicationPreferences.saveLocalPreference(getContext(),Constants.business_category_name,value_property.toString());
                    ApplicationPreferences.saveLocalPreference(getContext(),Constants.business_category_id,category.getIdCategory());
                    ApplicationPreferences.saveLocalPreference(getContext(),Constants.business_subcategory_id,sub_category.getIdSubCategory());
                    ApplicationPreferences.saveLocalPreference(getContext(),Constants.business_sub_subcategory_id,sub_sub_category.getIdSubSubCategory());
                    searchBusinesses();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
    private void clear(){
        business = new BusinessInfo[]{};
        mAdapter = new FindBusinessAdapter(getActivity(),Arrays.asList(business));
        mListView.setAdapter(mAdapter);
        mListView.invalidate();
        if (mListView.getLayoutManager()==null){ mListView.setLayoutManager(llm);}
        /*pbLoading_business.setVisibility(View.GONE);
        no_results_layout.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);*/
    }

    public static BusinessInfo[] getBusiness() {
        return business;
    }

    public CategoryViewModel getCategory() {
        return category;
    }

    public void setCategory(CategoryViewModel category) {
        this.category = category;
    }
    /********************************************************************************************************/
    public static class listener implements  SearchView.OnQueryTextListener{
        @Override
        public boolean onQueryTextChange(String query) {
            if (getBusiness()!=null && getBusiness().length>0) {
                final List<BusinessInfo> filteredModelList = filter(Arrays.asList(getBusiness()), query);
                mAdapter.animateTo(filteredModelList);
                mListView.scrollToPosition(0);
            }
            return false;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        private List<BusinessInfo> filter(List<BusinessInfo> models, String query) {
            query = query.toLowerCase();
            final List<BusinessInfo> filteredModelList = new ArrayList<>();
            for (BusinessInfo model : models) {
                final String text = model.getCompany().toLowerCase();
                if (text.contains(query)) {
                    filteredModelList.add(model);
                }
            }

            return filteredModelList;
        }
    }
}
