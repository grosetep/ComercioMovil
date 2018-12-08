package estrategiamovil.comerciomovil.ui.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import java.util.Arrays;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.BusinessInfo;
import estrategiamovil.comerciomovil.modelo.PublicationCardViewModel;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Connectivity;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.DividerItemDecoration;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.ui.adapters.MerchantPubsAdapter;
import estrategiamovil.comerciomovil.ui.adapters.PublicationAdapter;
import estrategiamovil.comerciomovil.ui.interfaces.DialogCallbackInterface;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class MerchantListPublicationsFragment extends Fragment implements DialogCallbackInterface {
    private ProgressBar pbLoading_merchant;
    public static final String MERCHANT = "merchant";
    private RecyclerView recycler_merchant_pubs;
    private RelativeLayout no_results_layout;
    private BusinessInfo businessInfo;
    private LinearLayoutManager llm;
    private static final String TAG = MerchantListPublicationsFragment.class.getSimpleName();
    private static MerchantPubsAdapter mAdapter;
    private PublicationCardViewModel[] publications_merchant = null;
    public MerchantListPublicationsFragment() {}

    public static MerchantListPublicationsFragment newInstance(BusinessInfo businessInfo) {
        MerchantListPublicationsFragment fragment = new MerchantListPublicationsFragment();
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
        View v = inflater.inflate(R.layout.fragment_merchant_list_publications, container, false);
        initGUI(v);

        if (publications_merchant==null) {
            if (Connectivity.isNetworkAvailable(getContext())) {
                setupListItems();
            }else {
                new ShowConfirmations(getActivity(), this).openRetry();
            }
        }else {
            setupAdapter();
        }

        return v;
    }
    public void setupListItems() {//obtener datos de la publicacion mas datos de las url de imagenes
        String newUrl = Constants.GET_PUBLICATIONS;
        VolleyGetRequest(newUrl + "?method=getMerchantPublications&id_merchant="+businessInfo.getIdUser());
    }
    public void VolleyGetRequest(String url){
        Log.d(TAG, "VolleyGetRequest MerchantPublications:" + url);
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
                                        // Procesar la respuesta Json
                                        processingResponse(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        String message = "Verifique su conexión a internet.";
                                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                                        Toast.makeText(
                                                getActivity(),
                                                message,
                                                Toast.LENGTH_SHORT).show();
                                        pbLoading_merchant.setVisibility(View.GONE);
                                        recycler_merchant_pubs.setVisibility(View.VISIBLE);
                                    }
                                }

                        ).setRetryPolicy(new DefaultRetryPolicy(
                                Constants.MY_SOCKET_TIMEOUT_MS,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                );
    }
    private void processingResponse(JSONObject response) {
        Gson gson = new Gson();
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("result");
                    publications_merchant = gson.fromJson(mensaje.toString(), PublicationCardViewModel[].class);
                    setupAdapter();
                    break;
                case "2": // NO DATA FOUND
                    Log.d(TAG,"noData");
                    String mensaje2 = response.getString("message");
                    Log.d(TAG,mensaje2);
                    clearProducts();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            clearProducts();
        }

    }
    private void clearProducts(){
        publications_merchant = new PublicationCardViewModel[]{};
        mAdapter = new MerchantPubsAdapter(Arrays.asList(publications_merchant),getActivity());
        recycler_merchant_pubs.setAdapter(mAdapter);
        recycler_merchant_pubs.invalidate();
        if (recycler_merchant_pubs.getLayoutManager()==null){ recycler_merchant_pubs.setLayoutManager(llm);}
        pbLoading_merchant.setVisibility(View.GONE);
        no_results_layout.setVisibility(View.VISIBLE);
        recycler_merchant_pubs.setVisibility(View.GONE);
    }
    private void setupAdapter(){
        // Inicializar adaptador
        mAdapter = new MerchantPubsAdapter(Arrays.asList(publications_merchant),getActivity());
        recycler_merchant_pubs.setAdapter(mAdapter);
        if (recycler_merchant_pubs.getLayoutManager()==null){ recycler_merchant_pubs.setLayoutManager(llm);}
        if (publications_merchant.length>0) {
            pbLoading_merchant.setVisibility(View.GONE);
            recycler_merchant_pubs.setVisibility(View.VISIBLE);
            no_results_layout.setVisibility(View.GONE);
        }else{
            pbLoading_merchant.setVisibility(View.GONE);
            recycler_merchant_pubs.setVisibility(View.GONE);
            no_results_layout.setVisibility(View.VISIBLE);
        }
    }
    private void initGUI(View v){
        pbLoading_merchant = (ProgressBar) v.findViewById(R.id.pbLoading_merchant);
        recycler_merchant_pubs = (RecyclerView) v.findViewById(R.id.recycler_merchant_pubs);
        no_results_layout = (RelativeLayout) v.findViewById(R.id.no_results_layout);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_merchant_pubs.addItemDecoration(new DividerItemDecoration(getActivity()));
        recycler_merchant_pubs.setHasFixedSize(true);
        no_results_layout = (RelativeLayout)v.findViewById(R.id.no_results_layout);
        pbLoading_merchant.setVisibility(View.VISIBLE);
    }

    @Override
    public void method_positive() {
        if (Connectivity.isNetworkAvailable(getContext())){
            setupListItems();
        }else {
            String flag_dialog = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.CONNECTIVITY_DIALOG_SHOWED);
            if (flag_dialog.equals("0"))
                new ShowConfirmations(getActivity(), this).openRetry();
        }
    }

    @Override
    public void method_negative(Activity activity) {
        getActivity().finish();
    }
}
