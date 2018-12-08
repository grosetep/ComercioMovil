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

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.AskQuestion;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Connectivity;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.DividerItemDecoration;
import estrategiamovil.comerciomovil.tools.GeneralFunctions;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.ui.adapters.AskQuestionAdapter;
import estrategiamovil.comerciomovil.ui.fragments.ManagePublicationsFragment;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class AskResponseActivity extends AppCompatActivity {
    public static final String ID_PUBLICATION="id_publication";
    public static final String ANSWERABLE="answerable";
    private ArrayList<AskQuestion> asks = new ArrayList<>();
    private RecyclerView cardList_asks;
    private SwipeRefreshLayout swipeRefresh_asks;
    private RelativeLayout layout_no_publications;
    private AppCompatButton button_retry_search;
    private RelativeLayout no_connection_layout;
    private LinearLayout layout_send_ask;
    private EditText text_send_ask_indicator;
    private ProgressBar pbLoading_asks;
    private LinearLayoutManager llm;
    private static final String TAG = AskResponseActivity.class.getSimpleName();
    private AskQuestionAdapter adapter;
    private AppCompatButton button_retry;
    public final boolean load_initial = true;
    private String id_publication = "";
    private String answerable = String.valueOf(false);
    private TextView text1;
    private final int SEND_QUESTION_RESPONSE = 1;
    private static boolean list_changed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_response);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_asks);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();
        id_publication = i.getStringExtra(AskResponseActivity.ID_PUBLICATION);
        answerable = i.getStringExtra(AskResponseActivity.ANSWERABLE);
        initGUI();
        assignActions();
        if (Connectivity.isNetworkAvailable(getApplicationContext())) {
            asks.clear();
            setupListItems(Constants.cero,Constants.load_more_tax,load_initial,false);
        }else {
            updateUI(true);
        }

    }
    private void initGUI(){
        layout_no_publications = (RelativeLayout) findViewById(R.id.layout_no_publications);
        button_retry_search = (AppCompatButton) findViewById(R.id.button_retry_search);
        text1 = (TextView) findViewById(R.id.text1);
        no_connection_layout = (RelativeLayout) findViewById(R.id.no_connection_layout);
        layout_send_ask = (LinearLayout) findViewById(R.id.layout_send_ask);
        text_send_ask_indicator = (EditText) findViewById(R.id.text_send_ask_indicator);
        pbLoading_asks  = (ProgressBar) findViewById(R.id.pbLoading_asks);
        cardList_asks = (RecyclerView) findViewById(R.id.cardList_asks);
        cardList_asks.setItemAnimator(new DefaultItemAnimator());
        cardList_asks.addItemDecoration(new DividerItemDecoration(AskResponseActivity.this));
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        if (cardList_asks.getLayoutManager()==null){ cardList_asks.setLayoutManager(llm);}

        button_retry = (AppCompatButton) findViewById(R.id.button_retry);

        pbLoading_asks.setVisibility(View.VISIBLE);
        cardList_asks.setVisibility(View.GONE);
        swipeRefresh_asks= (SwipeRefreshLayout) findViewById(R.id.swipeRefresh_asks);
        swipeRefresh_asks.setColorSchemeResources(
                R.color.s1,
                R.color.s2,
                R.color.s3,
                R.color.s4
        );
        swipeRefresh_asks.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d(TAG,"REFRESH DATA.....");
                        boolean refresh = true;
                        setupListItems(Constants.cero,Constants.load_more_tax,load_initial,refresh);
                    }
                }
        );

    }
    private void assignActions(){
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asks.clear();
                loadingView();
                swipeRefresh_asks.setRefreshing(false);
                setupListItems(Constants.cero,Constants.load_more_tax,load_initial,false);
            }
        });
        button_retry_search.setVisibility(View.GONE);
        if (answerable.equals(String.valueOf(true))) layout_send_ask.setVisibility(View.GONE);

        text_send_ask_indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String idUser = ApplicationPreferences.getLocalStringPreference(AskResponseActivity.this,Constants.localUserId);
            if(idUser==null || idUser.equals("")) {
                finish();
                Intent i = new Intent(getBaseContext(),LoginActivity.class);
                startActivity(i);
            }else{
                Intent intent = new Intent(getApplicationContext(), SendQuestionResponseActivity.class);
                intent.putExtra(SendQuestionResponseActivity.ID_PUBLICATION, id_publication);
                intent.putExtra(SendQuestionResponseActivity.ANSWERABLE, answerable);
                intent.putExtra(SendQuestionResponseActivity.FLOW,"questions");
                startActivityForResult(intent, SEND_QUESTION_RESPONSE);
            }
            }
        });
    }
    public void setupListItems(int start, int end,boolean load_initial,boolean isRefresh) {//obtener datos de la publicacion mas datos de las url de imagenes
        String newUrl = Constants.GET_QUESTIONS;
        Log.d(TAG,"setupListItems:"+newUrl + "?method=getQuestions"+"&id_publication="+id_publication+"&answerable="+getAnswerable()+"&start="+start+"&end="+end);
        VolleyGetRequest(newUrl + "?method=getQuestions"+"&id_publication="+id_publication+"&answerable="+getAnswerable()+"&start="+start+"&end="+end,load_initial,isRefresh);
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
                getInstance(AskResponseActivity.this).
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
                                            swipeRefresh_asks.setRefreshing(false);
                                        }else if (load_initial) {
                                            processingResponseInit(response);
                                        }else {
                                            asks.remove(asks.size() - 1);//delete loading..
                                            adapter.notifyItemRemoved(asks.size());
                                            processingResponse(response,isRefresh);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        if(isRefresh){
                                            swipeRefresh_asks.setRefreshing(false);
                                            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),AskResponseActivity.this);
                                        }else if(!load_initial) {
                                            asks.remove(asks.size() - 1);//delete loading..
                                            adapter.notifyItemRemoved(asks.size());
                                            notifyListChanged();
                                            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),AskResponseActivity.this);
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
        Log.d(TAG,"processingResponse asks");
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    Gson gson = new Gson();
                    JSONArray mensaje = response.getJSONArray("result");
                    ArrayList<AskQuestion> new_asks = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), AskQuestion[].class)));
                    if (new_asks!=null && new_asks.size()>0) {
                        asks.addAll(GeneralFunctions.FilterAsks(asks,new_asks));
                    }
                    break;
                case "2": // NO DATA FOUND
                    String mensaje2 = response.getString("message");
                    text1.setText(mensaje2);
                    Log.d(TAG,"Asks...noData...."+mensaje2);
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
        if (cardList_asks.getAdapter()==null) {
            adapter = new AskQuestionAdapter(asks, AskResponseActivity.this,cardList_asks);

            cardList_asks.setAdapter(adapter);
            //load more functionallity

            adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (!swipeRefresh_asks.isRefreshing()) {
                        int index = asks != null ? (asks.size()) : 0;
                        int end = index + Constants.load_more_tax;
                        setupListItems(index, end, false, false);
                    }
                }
            });
        }else{
            notifyListChanged();
        }
    }

    private void processingResponse(JSONObject response,boolean isRefresh) {
        Log.d(TAG,"asks:"+response.toString());
        ArrayList<AskQuestion> new_reviews = null;
        try {
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    Gson gson = new Gson();
                    JSONArray mensaje = response.getJSONArray("result");
                    new_reviews = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), AskQuestion[].class)));
                    addNewElements(new_reviews,isRefresh);
                    break;
                case "2": // NO DATA FOUND
                    String mensaje2 = response.getString("message");
                    text1.setText(mensaje2);
                    if (isRefresh && new_reviews == null && asks.size()>Constants.cero ){asks.clear();updateUI(false);}
                    Log.d(TAG,"Asks..noData..."+mensaje2);
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            notifyListChanged();
        }

    }
    private void addNewElements(ArrayList<AskQuestion> new_asks,boolean isRefresh){
        if (new_asks!=null && new_asks.size()>Constants.cero) {
            Log.d(TAG, "new_reviews:" + new_asks.size());
            if (isRefresh){
                asks.clear();
                asks.addAll(new_asks);
            }else{
                asks.addAll(GeneralFunctions.FilterAsks(asks, new_asks));
            }
        }
    }
    private void addLoadingAndMakeRequest(final String url){
        final boolean isRefresh = false;
        final boolean first_load= false;
        asks.add(null);
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                adapter.notifyItemInserted(asks.size() - 1);
                makeRequest( url, first_load,isRefresh);
            }
        };
        handler.post(r);

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
    private void loadingView(){
        no_connection_layout.setVisibility(View.GONE);
        cardList_asks.setVisibility(View.GONE);
        layout_no_publications.setVisibility(View.GONE);
        pbLoading_asks.setVisibility(View.VISIBLE);
    }
    public ArrayList<AskQuestion> getAsks() {
        return asks;
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
    private void updateUI(final boolean connection_error){
        runOnUiThread(new
                              Runnable() {
                                  @Override
                                  public void run() {
          if (connection_error) {
              no_connection_layout.setVisibility(View.VISIBLE);
              cardList_asks.setVisibility(View.GONE);
              layout_no_publications.setVisibility(View.GONE);
              pbLoading_asks.setVisibility(View.GONE);
          }else if (getAsks()!=null && getAsks().size()>0) {
              no_connection_layout.setVisibility(View.GONE);
              cardList_asks.setVisibility(View.VISIBLE);
              layout_no_publications.setVisibility(View.GONE);
              pbLoading_asks.setVisibility(View.GONE);
          }else{
              no_connection_layout.setVisibility(View.GONE);
              cardList_asks.setVisibility(View.GONE);
              layout_no_publications.setVisibility(View.VISIBLE);
              pbLoading_asks.setVisibility(View.GONE);
          }
      }
  });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        switch(requestCode) {
            case SEND_QUESTION_RESPONSE:
                if (resultCode == Activity.RESULT_OK) {
                    String type_operation = (String)  data.getSerializableExtra(SendQuestionResponseActivity.TYPE_OPERATION);
                    Log.d(TAG,"EXITO type_operation:"+type_operation);
                    //question: reload, response, update element
                    switch (type_operation){
                        case SendQuestionResponseActivity.OPERATION_QUESTION:
                            reload();
                            break;
                        case SendQuestionResponseActivity.OPERATION_RESPONSE:
                            break;
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
    public void reload(){
        asks.clear();
        setupListItems(Constants.cero,Constants.load_more_tax,load_initial,false);
    }

    public String getAnswerable() {
        return answerable;
    }

    public static boolean isList_changed() {
        return list_changed;
    }

    public static void setList_changed(boolean list_changed) {
        AskResponseActivity.list_changed = list_changed;
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"onResume...flag:"+AskResponseActivity.isList_changed());
        if (AskResponseActivity.isList_changed()) {

            this.reload();
            AskResponseActivity.setList_changed(false);
            //update admin publications too
            ManagePublicationsFragment.setStatusListChanged(true);
        }

    }
}
