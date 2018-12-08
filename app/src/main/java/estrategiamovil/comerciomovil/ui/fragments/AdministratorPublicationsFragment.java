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
import estrategiamovil.comerciomovil.modelo.PendingPublication;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.DividerItemDecoration;
import estrategiamovil.comerciomovil.tools.GeneralFunctions;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.ui.adapters.AdministratorPublicationsAdapter;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdministratorPublicationsFragment extends Fragment {


    private static ArrayList<PendingPublication> publications = new ArrayList<>();
    private ProgressBar pb;
    private SwipeRefreshLayout swipeRefresh_administrator;
    public final boolean load_initial = true;
    private Gson gson = new Gson();
    LinearLayoutManager llm;
    private AppCompatButton button_retry;
    private RelativeLayout no_connection_layout;
    private RelativeLayout layout_no_publications;
    private TextView text1;
    private AppCompatButton button_retry_search;
    private static final String TAG = AdministratorPublicationsFragment.class.getSimpleName();

    private String id_merchant;

    private RecyclerView mListView;
    private static boolean list_changed = false;
    private AdministratorPublicationsAdapter mAdapter;

    public static AdministratorPublicationsFragment newInstance() {
        AdministratorPublicationsFragment fragment = new AdministratorPublicationsFragment();
        return fragment;
    }

    public AdministratorPublicationsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_administrator_publications, container, false);
        pb = (ProgressBar) v.findViewById(R.id.pbLoading_publications_admin);
        pb.setVisibility(View.VISIBLE);
        no_connection_layout = (RelativeLayout) v.findViewById(R.id.no_connection_layout);
        layout_no_publications = (RelativeLayout) v.findViewById(R.id.layout_no_publications);
        text1 = (TextView) v.findViewById(R.id.text1);
        text1.setText(getString(R.string.prompt_text_no_pending_publications));
        button_retry_search = (AppCompatButton) v.findViewById(R.id.button_retry_search);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mListView = (RecyclerView) v.findViewById(R.id.publications);
        mListView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        mListView.addItemDecoration(new DividerItemDecoration(getActivity()));
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        if (mListView.getLayoutManager() == null) {
            mListView.setLayoutManager(llm);
    }
        swipeRefresh_administrator = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh_administrator);
        swipeRefresh_administrator.setColorSchemeResources(
                R.color.s1,
                R.color.s2,
                R.color.s3,
                R.color.s4
        );
        swipeRefresh_administrator.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d(TAG,"REFRESH DATA.....");
                        getModeratePublications(Constants.cero, Constants.load_more_tax, load_initial,true);

}
                }
        );
        button_retry = (AppCompatButton) v.findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publications.clear();
                loadingView();
                swipeRefresh_administrator.setRefreshing(false);
                getModeratePublications( Constants.cero, Constants.load_more_tax, true,false);
            }
        });
        button_retry_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publications.clear();
                loadingView();
                swipeRefresh_administrator.setRefreshing(false);
                getModeratePublications( Constants.cero, Constants.load_more_tax, true,false);
            }
        });
        publications.clear();
        getModeratePublications(Constants.cero, Constants.load_more_tax, load_initial,false);
        return v;
    }
    public void getModeratePublications(int start, int end, boolean load_initial,boolean isRefresh) {//obtener datos de la publicacion mas datos de las url de imagenes
        String newUrl = Constants.ADMINISTRATION;
        String params = "?method=getModeratePublications&start=" + start + "&end=" + end;
        Log.d(TAG,"----------------------------------->3:"+newUrl+params);
        VolleyGetRequest(newUrl + params,load_initial,isRefresh);

    }
    private void addLoadingAndMakeRequest(final String url) {
        Log.d(TAG, "addLoading...");
        final boolean isRefresh = false;
        final boolean first_load= false;

        publications.add(null);
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                mAdapter.notifyItemInserted(publications.size() - 1);
                makeRequest(url, first_load,isRefresh);
            }
        };
        handler.post(r);

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
                                            swipeRefresh_administrator.setRefreshing(false);
                                        }else if (load_initial) {
                                            processingResponseInit(response);
                                        }else {
                                            publications.remove(publications.size() - 1);//delete loading..
                                            mAdapter.notifyItemRemoved(publications.size());
                                            processResponse(response,isRefresh);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error){
                                        error.printStackTrace();
                                        if(isRefresh){
                                            swipeRefresh_administrator.setRefreshing(false);
                                            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),getActivity());
                                        }else if(!load_initial) {
                                            publications.remove(publications.size() - 1);//delete loading..
                                            mAdapter.notifyItemRemoved(publications.size());
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
        Log.d(TAG, "processingResponse publications initial....." + response.toString());
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS

                    JSONArray mensaje = response.getJSONArray("result");
                    ArrayList<PendingPublication> new_publications = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), PendingPublication[].class)));
                    if (new_publications!= null && new_publications.size() > 0) {
                        publications.addAll(new_publications);
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
        ArrayList<PendingPublication> new_publications = null;
        try {
            // Obtener atributo "status"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("result");
                    new_publications = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), PendingPublication[].class)));
                    addNewElements(new_publications,isRefresh);
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
    private void addNewElements(ArrayList<PendingPublication> new_publications,boolean isRefresh){
        if (new_publications!=null && new_publications.size()>Constants.cero) {
            Log.d(TAG, "new_publications:" + new_publications.size());
            if (isRefresh){Log.d(TAG, "isRefresh..");
                publications.clear();
                publications.addAll(new_publications);
            }else{
                publications.addAll(GeneralFunctions.FilterPendingPublications(publications, new_publications));
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
                        mListView.setVisibility(View.GONE);
                        layout_no_publications.setVisibility(View.GONE);
                        pb.setVisibility(View.GONE);
                    }else if (getPublications()!=null && getPublications().size()>0) {
                        no_connection_layout.setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                        layout_no_publications.setVisibility(View.GONE);
                        pb.setVisibility(View.GONE);
                    }else{
                        no_connection_layout.setVisibility(View.GONE);
                        mListView.setVisibility(View.GONE);
                        layout_no_publications.setVisibility(View.GONE);
                        pb.setVisibility(View.GONE);
                    }
                }
            });
    }

    public static ArrayList<PendingPublication> getPublications() {
        return publications;
    }

    private void setupAdapter(){
        // Set the adapter
        if (mListView.getAdapter()==null) {
            mAdapter = new AdministratorPublicationsAdapter(publications, getActivity(),mListView);
            mListView.setAdapter(mAdapter);
            if (mListView.getLayoutManager() == null) {
                    mListView.setLayoutManager(llm);
            }
            mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (!swipeRefresh_administrator.isRefreshing()) {
                        int index = publications != null ? (publications.size()) : Constants.cero;
                        int end = index + Constants.load_more_tax;
                        getModeratePublications( index, end, false, false);
                    }
                }
            });
            pb.setVisibility(View.GONE);
        }else{
            notifyListChanged();
        }
    }

    private void loadingView(){
        no_connection_layout.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
    }
}
