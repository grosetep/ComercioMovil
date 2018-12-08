package estrategiamovil.comerciomovil.ui.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import estrategiamovil.comerciomovil.modelo.City;
import estrategiamovil.comerciomovil.modelo.CustomerInfo;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.DividerItemDecoration;
import estrategiamovil.comerciomovil.ui.activities.CityPreferencesActivity;
import estrategiamovil.comerciomovil.ui.adapters.CustomersAdapter;
import estrategiamovil.comerciomovil.ui.adapters.FindBusinessAdapter;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageCustomersFragment extends Fragment {
    private ProgressBar pbLoading_customers;
    private EditText edit_message;
    private ImageView button_edit_message;
    private ImageView button_done_message;
    private static CustomerInfo[] customers = null;
    private static final String TAG = ManageCustomersFragment.class.getSimpleName();
    private Gson gson = new Gson();
    private LinearLayoutManager llm;
    private RelativeLayout no_results_layout;
    private static RecyclerView mListView;
    private static boolean list_changed = false;
    private static CustomersAdapter mAdapter;

    public static boolean getStatusListChanged(){return ManageCustomersFragment.list_changed;}
    public static void setStatusListChanged(boolean value){ManageCustomersFragment.list_changed=value; }
    public static ManageCustomersFragment newInstance() {
        ManageCustomersFragment fragment = new ManageCustomersFragment();
        return fragment;
    }
    public ManageCustomersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_manage_customers, container, false);
        edit_message = (EditText) v.findViewById(R.id.edit_message);
        String messageDefault = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.defaultMessageNotif);
        if (messageDefault!=null && !messageDefault.equals("")) edit_message.setText(messageDefault);
        button_edit_message = (ImageView) v.findViewById(R.id.button_edit_message);
        button_edit_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_message.setEnabled(true);
            }
        });
        button_done_message = (ImageView) v.findViewById(R.id.button_done_message);
        button_done_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_message.getText().toString().trim().length()>0)
                    ApplicationPreferences.saveLocalPreference(getContext(),Constants.defaultMessageNotif,edit_message.getText().toString());
                edit_message.setEnabled(false);
            }
        });
        pbLoading_customers = (ProgressBar) v.findViewById(R.id.pbLoading_customers);
        pbLoading_customers.setVisibility(View.VISIBLE);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mListView = (RecyclerView) v.findViewById(R.id.list);
        mListView.addItemDecoration(new DividerItemDecoration(getActivity()));
        mListView.setHasFixedSize(true);
        no_results_layout = (RelativeLayout)v.findViewById(R.id.no_results_layout);

        if (customers==null || ManageCustomersFragment.getStatusListChanged()) {
            ManageCustomersFragment.setStatusListChanged(false);
            getAllCustomers();
        }else{
            setupAdapter();
        }

        return v;
    }
    public void getAllCustomers() {
        String newUrl = Constants.GET_CUSTOMERS;
        String idUser = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.localUserId);
        VolleyGetRequest(newUrl + "?method=getAllCustomers"+"&idUser="+idUser);

    }

    public void VolleyGetRequest(String url){
        Log.d(TAG,"REQUEST getAllCustomers:" +url);
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
        pbLoading_customers.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        no_results_layout.setVisibility(View.GONE);
    }
    private void processResponse(JSONObject response) {
        Log.d(TAG, "Response:" + response.toString());
        try {
            // Obtener atributo "status"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("result");
                    customers = gson.fromJson(mensaje.toString(), CustomerInfo[].class);
                    onSuccess();
                    break;
                case "2": // NO DATA FOUND
                    clear();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            clear();
        }
    }
    private void setupAdapter(){
        // Set the adapter
        Log.d(TAG,"setupAdapter:"+customers.length);
        mAdapter = new CustomersAdapter(getActivity(), Arrays.asList(customers));
        mListView.setAdapter(mAdapter);
        mListView.invalidate();
        if (mListView.getLayoutManager()==null){
            mListView.setLayoutManager(llm);
        }
        pbLoading_customers.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        no_results_layout.setVisibility(View.GONE);
    }
    @Override
    public void onStop(){
        //reset data from list publications for reloading update when user go back to the application
        customers = null;
        super.onStop();
    }

    private void clear(){
        customers = new CustomerInfo[]{};
        mAdapter = new CustomersAdapter(getActivity(),Arrays.asList(customers));
        mListView.setAdapter(mAdapter);
        mListView.invalidate();
        if (mListView.getLayoutManager()==null){ mListView.setLayoutManager(llm);}
        pbLoading_customers.setVisibility(View.GONE);
        no_results_layout.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
    }

    /********************************************************************************************************/
    public static class listener implements  SearchView.OnQueryTextListener{
        @Override
        public boolean onQueryTextChange(String query) {
            final List<CustomerInfo> filteredModelList = filter(Arrays.asList(customers), query);
            mAdapter.animateTo(filteredModelList);
            mListView.scrollToPosition(0);
            return false;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        private List<CustomerInfo> filter(List<CustomerInfo> models, String query) {
            query = query.toLowerCase();
            final List<CustomerInfo> filteredModelList = new ArrayList<>();
            for (CustomerInfo model : models) {
                final String text = model.getAlias().toLowerCase();
                if (text.contains(query)) {
                    filteredModelList.add(model);
                }
            }

            return filteredModelList;
        }
    }
}
