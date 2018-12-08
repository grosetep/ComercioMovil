package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Address;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.DividerItemDecoration;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class SelectLocationActivity extends AppCompatActivity {
    private static final String TAG = SelectLocationActivity.class.getSimpleName();
    private RecyclerView recyclerview_locations;
    private Address[] locations;
    private HashMap<String,Address> locationsIds = new HashMap<>();
    private Gson gson = new Gson();
    private ProgressBar progressBar;//pbLoading_sel_locations
    private SelectLocationAdapter mAdapter;
    private View mCustomView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        Intent i = getIntent();
        HashMap<String,Address> temp = (HashMap<String,Address>)i.getSerializableExtra(Constants.ADDRESS_SELECTED);
        if (temp!=null && temp.size()>0) {locationsIds = temp;temp = null;}

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_select_location);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(
                getSupportActionBar().DISPLAY_SHOW_CUSTOM,
                getSupportActionBar().DISPLAY_SHOW_CUSTOM | getSupportActionBar().DISPLAY_SHOW_HOME
                        | getSupportActionBar().DISPLAY_SHOW_TITLE);

        LayoutInflater mInflater = LayoutInflater.from(this);
        mCustomView = mInflater.inflate(R.layout.actionbar_custom_view_done_cancel, null);
        mCustomView.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Done"
                    Intent intent = new Intent();
                    Bundle args = new Bundle();
                    if  (getmAdapter().getLocationsSelected()!=null && getmAdapter().getLocationsSelected().size()>0) {
                        args.putSerializable(Constants.ADDRESS_SELECTED, getmAdapter().getLocationsSelected());
                        intent.putExtras(args);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }else{
                        setResult(Activity.RESULT_CANCELED, intent);
                        finish();
                    }

                    }
                });
        mCustomView.findViewById(R.id.actionbar_cancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Cancel"
                        open();

                    }
                });
        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        recyclerview_locations = (RecyclerView) findViewById(R.id.recyclerview_locations);
        recyclerview_locations.addItemDecoration(new DividerItemDecoration(SelectLocationActivity.this));
        progressBar = (ProgressBar) findViewById(R.id.pbLoading_sel_locations);
        showScreen(false);
        setupListItems();
    }
    private void showScreen(boolean flag){
        recyclerview_locations.setVisibility(flag?View.VISIBLE:View.GONE);
        progressBar.setVisibility(flag?View.GONE:View.VISIBLE);
    }
    public SelectLocationAdapter getmAdapter() {
        return mAdapter;
    }
    public void open(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Se descartarán todos los cambios.");
        alertDialogBuilder.setTitle("Descartar Cambios");
        alertDialogBuilder.setPositiveButton("DESCARTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(LocationsActivity.this, "You clicked yes button", Toast.LENGTH_LONG).show();

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void setupListItems(){
        HashMap<String,String> params = new HashMap<>();
        params.put("method", "getLocationsByUser");
        params.put("idUser", ApplicationPreferences.getLocalStringPreference(SelectLocationActivity.this, Constants.localUserId));
        VolleyPostRequest(Constants.GET_ADDRESS, params, SelectLocationActivity.this);
    }
    public void VolleyPostRequest(String url, final HashMap<String, String> args, Context context) {
        JSONObject jobject = new JSONObject(args);
        Log.d(TAG, "VolleyPostRequest:" + jobject.toString());
        JsonObjectRequest request =  new JsonObjectRequest(
                Request.Method.POST,
                url,
                jobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar la respuesta del servidor
                        processResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                        onBackPressed();
                        Toast.makeText(SelectLocationActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT);
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Accept", "application/json");
                return headers;
            }


            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8" + getParamsEncoding();
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
    private void processResponse(JSONObject response) {
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    // Obtener array "publications" Json
                    JSONArray mensaje = response.getJSONArray("result");
                    // Parsear con Gson e inicializar arreglo de ciudades
                    locations = gson.fromJson(mensaje.toString(), Address[].class);
                    // Inicializar adaptador
                    recyclerview_locations.setLayoutManager(new LinearLayoutManager(recyclerview_locations.getContext()));
                    mAdapter = new SelectLocationAdapter(SelectLocationActivity.this, Arrays.asList(locations));
                    recyclerview_locations.setAdapter(mAdapter);

                    showScreen(true);
                    break;
                case "2": // FALLIDO
                    String mensaje2 = response.getString("message");
                    finish();
                    Toast.makeText(
                            SelectLocationActivity.this,
                            mensaje2,
                            Toast.LENGTH_SHORT).show();
                    break;
                case "3": // FALLIDO

                    String mensaje3 = response.getString("message");
                    finish();
                    Toast.makeText(
                            SelectLocationActivity.this,
                            mensaje3,
                            Toast.LENGTH_LONG).show();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            finish();
            Toast.makeText(
                    SelectLocationActivity.this,
                    getString(R.string.generic_error),
                    Toast.LENGTH_LONG).show();
        }

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

    /***********************************************Adapter*****************************************************/
    class SelectLocationAdapter extends RecyclerView.Adapter<SelectLocationAdapter.AddressViewHolder> {
        private final LayoutInflater mInflater;
        private final List<Address> mModels;
        private int mBackground;
        private final TypedValue mTypedValue = new TypedValue();
        private Activity activityContext;
        private HashMap<String,Address> locationsSelected = new HashMap<>();
        public HashMap<String,Address> getLocationsSelected(){ return locationsSelected;}
        //private String idAddress;

        public class AddressViewHolder extends RecyclerView.ViewHolder {

            private final TextView text_short_name;
            private final TextView text_address;
            private CheckBox checkBox;
            private String idAddress;



            public AddressViewHolder(View itemView) {
                super(itemView);
                text_short_name = (TextView) itemView.findViewById(R.id.text_short_name);
                text_address = (TextView) itemView.findViewById(R.id.text_address);
                checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_address);
                idAddress = "";
            }

            public void bind(Address model) {
                text_short_name.setText(model.getShortName());
                text_address.setText(model.getGoogleAddress());
                idAddress = model.getIdAddress();
            }


        }

        public SelectLocationAdapter(Activity context, List<Address> models) {
            activityContext = context;
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mInflater = LayoutInflater.from(context);
            mModels = new ArrayList<>(models);
        }

        @Override
        public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = mInflater.inflate(R.layout.select_address_layout, parent, false);
            itemView.setBackgroundResource(mBackground);
            return new AddressViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final AddressViewHolder holder, int position) {
            final Address model = mModels.get(position);
            holder.bind(model);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add locations list
                    CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkbox_address);
                    if (checkBox.isChecked()) {
                        checkBox.setChecked(false);
                        locationsSelected.remove(holder.idAddress);
                    } else {

                        checkBox.setChecked(true);
                        locationsSelected.put(holder.idAddress, model);
                    }
                }
            });

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v;
                    if (checkBox.isChecked()) {
                        locationsSelected.put(holder.idAddress, model);
                    } else {
                        locationsSelected.remove(holder.idAddress);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mModels.size();
        }

    }


}
