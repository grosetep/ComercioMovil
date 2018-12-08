package estrategiamovil.comerciomovil.ui.fragments;

import android.os.Bundle;
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
import android.widget.TextView;
import android.os.Handler;
import com.android.volley.DefaultRetryPolicy;
import com.google.gson.Gson;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.PublicationUser;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.GeneralFunctions;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.ui.adapters.PublicationUserAdapter;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;
import estrategiamovil.comerciomovil.web.VolleySingleton;


public class ManagePublicationsFragment extends Fragment {


    private static final String ARG_ID_MERCHANT = "id_merchant";
    private static ArrayList<PublicationUser> publications = new ArrayList<>();
    private ProgressBar pb;
    private SwipeRefreshLayout swipeRefresh_manage_publications;
    public final boolean load_initial = true;
    private Gson gson = new Gson();
    LinearLayoutManager llm;
    GridLayoutManager llmg;
    private TextView empty;
    private AppCompatButton button_retry;
    private RelativeLayout no_connection_layout;
    private static final String TAG = ManagePublicationsFragment.class.getSimpleName();

    private String id_merchant;

    private RecyclerView mListView;
    private static boolean list_changed = false;
    private PublicationUserAdapter mAdapter;

    public static ManagePublicationsFragment newInstance(String idUser) {
        ManagePublicationsFragment fragment = new ManagePublicationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID_MERCHANT, idUser);
        fragment.setArguments(args);
        return fragment;
    }

    public static boolean getStatusListChanged(){return ManagePublicationsFragment.list_changed;}
    public static void setStatusListChanged(boolean value){ManagePublicationsFragment.list_changed=value; }

    public ManagePublicationsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            id_merchant = getArguments().getString(ARG_ID_MERCHANT);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item, container, false);

        pb = (ProgressBar) v.findViewById(R.id.pbLoading_publications);
        pb.setVisibility(View.VISIBLE);
        no_connection_layout = (RelativeLayout) v.findViewById(R.id.no_connection_layout);
        llm = new LinearLayoutManager(getActivity());
        llmg = new GridLayoutManager(getActivity(),2);
        llm.setOrientation(GridLayoutManager.VERTICAL);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        empty = (TextView)v.findViewById(R.id.empty);
        mListView = (RecyclerView) v.findViewById(R.id.list);
        mListView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        if (mListView.getLayoutManager() == null) {
            mListView.setLayoutManager(llm);
        }
        swipeRefresh_manage_publications = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh_manage_publications);
        swipeRefresh_manage_publications.setColorSchemeResources(
                R.color.s1,
                R.color.s2,
                R.color.s3,
                R.color.s4
        );
        swipeRefresh_manage_publications.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d(TAG,"REFRESH DATA.....");
                        getPublicationsByUser(Constants.cero, Constants.load_more_tax, load_initial,true);

                    }
                }
        );
        button_retry = (AppCompatButton) v.findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



        publications.clear();
                loadingView();
                swipeRefresh_manage_publications.setRefreshing(false);
                getPublicationsByUser( Constants.cero, Constants.load_more_tax, true,false);
            }
        });
        publications.clear();
        getPublicationsByUser(Constants.cero, Constants.load_more_tax, load_initial,false);
        return v;
    }

    public void reload(){
        publications.clear();
        getPublicationsByUser(Constants.cero, Constants.load_more_tax, load_initial,false);
    }


    public void getPublicationsByUser(int start, int end, boolean load_initial,boolean isRefresh) {//obtener datos de la publicacion mas datos de las url de imagenes
        String newUrl = Constants.GET_PUBLICATIONS;
        String idUser = ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.localUserId);
        String params = "?method=getAllPublicationsByIdUser&idUser=" + idUser + "&start=" + start + "&end=" + end;
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

/*

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
                                        processResponse(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        String message = "Verifique su conexión a internet.";
                                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);

                                        Toast.makeText(
                                                getContext(),
                                                message,
                                                Toast.LENGTH_SHORT).show();
                                        pb.setVisibility(View.GONE);
                                        getActivity().onBackPressed();
                                    }
                                }

                        )
                );*/
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
                                            swipeRefresh_manage_publications.setRefreshing(false);
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
                                            swipeRefresh_manage_publications.setRefreshing(false);
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
                    ArrayList<PublicationUser> new_publications = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), PublicationUser[].class)));
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
        ArrayList<PublicationUser> new_publications = null;
        try {
            // Obtener atributo "status"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("result");
                    new_publications = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), PublicationUser[].class)));
                    addNewElements(new_publications,isRefresh);
                    break;
                case "2": // NO DATA FOUND
                    String mensaje2 = response.getString("message");
                   /* Toast.makeText(
                            getActivity(),
                            mensaje2,
                            Toast.LENGTH_LONG).show();*/
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
    private void addNewElements(ArrayList<PublicationUser> new_publications,boolean isRefresh){
        if (new_publications!=null && new_publications.size()>Constants.cero) {
            Log.d(TAG, "new_publications:" + new_publications.size());
            if (isRefresh){Log.d(TAG, "isRefresh..");
                publications.clear();
                publications.addAll(new_publications);
            }else{
                publications.addAll(GeneralFunctions.FilterPublicationsMerchant(publications, new_publications));
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
                        //layout_no_publications.setVisibility(View.GONE);
        pb.setVisibility(View.GONE);
                    }else if (getPublications()!=null && getPublications().size()>0) {
                        no_connection_layout.setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                        //layout_no_publications.setVisibility(View.GONE);
                        pb.setVisibility(View.GONE);
                    }else{
                        no_connection_layout.setVisibility(View.GONE);
                        //mListView.setVisibility(View.GONE);
                        ShowConfirmations.showConfirmationMessage(getString(R.string.promt_no_publications),getActivity());
                        pb.setVisibility(View.GONE);
        }
    }
            });
    }

    public static ArrayList<PublicationUser> getPublications() {
        return publications;
    }

    private void setupAdapter(){
        // Set the adapter
        if (mListView.getAdapter()==null) {
            mAdapter = new PublicationUserAdapter(publications, getActivity(),mListView);
            mListView.setAdapter(mAdapter);
            if (mListView.getLayoutManager() == null) {
                if (empty.getText().toString().equals("grid"))
                    mListView.setLayoutManager(llmg);
                else
                    mListView.setLayoutManager(llm);
            }
            mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (!swipeRefresh_manage_publications.isRefreshing()) {
                        int index = publications != null ? (publications.size()) : Constants.cero;
                        int end = index + Constants.load_more_tax;
                        getPublicationsByUser( index, end, false, false);
                    }
                }
            });
            pb.setVisibility(View.GONE);
        }else{
            notifyListChanged();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"onResume...flag:"+ManagePublicationsFragment.getStatusListChanged());
        if (ManagePublicationsFragment.getStatusListChanged()) {this.reload();ManagePublicationsFragment.setStatusListChanged(false);}

    }
    private void loadingView(){
        no_connection_layout.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
        //layout_no_publications.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
    }
}




