package estrategiamovil.comerciomovil.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Address;
import estrategiamovil.comerciomovil.modelo.City;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.ui.adapters.CitiesSpinnerAdapter;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class LocationsActivity extends AppCompatActivity {
    private static final String TAG = LocationsActivity.class.getSimpleName();
    private ProgressDialog progressBar;
    private static City[] cities;
    private Gson gson = new Gson();
    private View mCustomView;
    //agregados
    private LinearLayout secondary_container_addresses;
    private ImageButton button_add_address;
    private String idCountry;
    private String countryName;
    private HashMap<String,Address> addresses;
    private int addressCounter = 1;
    private String activeTagFrame;

    private final int PLACE_PICKER_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        createProgressDialog(LocationsActivity.this,"Cargando...");
        //retrieve locations early added
        Intent intent = getIntent();
        if (intent!=null) {
            addresses = (HashMap<String, Address>) intent.getSerializableExtra("map_locations");
            if (addresses != null) {
                Log.d(TAG, "lista de ubicaciones antes seleccionadas: " + addresses.size());
                addressCounter = addresses.size();
            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_locations);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(
                getSupportActionBar().DISPLAY_SHOW_CUSTOM,
                getSupportActionBar().DISPLAY_SHOW_CUSTOM |  getSupportActionBar().DISPLAY_SHOW_HOME
                        |  getSupportActionBar().DISPLAY_SHOW_TITLE);

        LayoutInflater mInflater = LayoutInflater.from(this);
        mCustomView = mInflater.inflate(R.layout.actionbar_custom_view_done_cancel, null);
        mCustomView.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Done"
                            boolean valid = true;
                            Intent data = new Intent();
                            Log.d(TAG,"LISTA:" + addresses.size() + " addressCounter:" + addressCounter);
                                    int total = secondary_container_addresses.getChildCount();
                                    for (int i=0;i<total;i++) {
                                        View currentCustomView = secondary_container_addresses.getChildAt(i);
                                        if (currentCustomView != null) {
                                            Log.d(TAG, "secondary_container_addresses.getChildAt(" + i + "):" + currentCustomView.getTag());
                                            View view = secondary_container_addresses.findViewWithTag(currentCustomView.getTag().toString());
                                            if (validate(view))
                                                save(currentCustomView.getTag().toString());
                                            else
                                                valid = false;
                                        }
                                    }
                                if (valid) {
                                    data.putExtra("addresses", addresses);
                                    setResult(Activity.RESULT_OK, data);
                                    progressBar.hide();
                                    progressBar = null;
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


        //agregados
        idCountry = ApplicationPreferences.getLocalStringPreference(LocationsActivity.this,Constants.countryUser);
        if (addresses==null) addresses = new HashMap<>();
        button_add_address = (ImageButton) findViewById(R.id.button_add_address);
        button_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    addAddressViewGroup(null, null);
            }
        });

        // add the first ViewGroup dinamically to secondary_container_addresses
        secondary_container_addresses = (LinearLayout) findViewById(R.id.secondary_container_addresses);

        if (savedInstanceState == null) {
            // get cities catalog the first time of activity cretion
            getCities();
        }
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

    private void getCities(){
        HashMap<String,String> params = new HashMap<>();
        params.put("idCountry", idCountry);
        VolleyPostRequest(Constants.GET_CITIES, params);
    }
    public void VolleyPostRequest(String url, final HashMap<String, String> args) {
        JSONObject jobject = new JSONObject(args);
        Log.d(TAG, "VolleyPostRequest:" + jobject.toString());

        VolleySingleton.getInstance(LocationsActivity.this).addToRequestQueue(
                new JsonObjectRequest(
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
                                progressBar.hide();
                                Snackbar snackbar = Snackbar.make(mCustomView, getResources().getString(R.string.no_internet), Snackbar.LENGTH_SHORT);
                                snackbar.show();
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
                }
        );
    }
    private void processResponse(JSONObject response) {
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    // Obtener array "publications" Json
                    JSONArray mensaje = response.getJSONArray("cities");
                    // Parsear con Gson e inicializar arreglo de ciudades
                    cities = gson.fromJson(mensaje.toString(), City[].class);
                    onCitiesCreated();
                    progressBar.hide();
                    break;
                case "2": // FALLIDO
                    progressBar.hide();
                    String mensaje2 = response.getString("message");
                    Toast.makeText(
                            LocationsActivity.this,
                            mensaje2,
                            Toast.LENGTH_SHORT).show();
                    break;
                case "3": // FALLIDO
                    progressBar.hide();
                    String mensaje3 = response.getString("message");
                    Toast.makeText(
                            LocationsActivity.this,
                            mensaje3,
                            Toast.LENGTH_LONG).show();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

    }
    private void onCitiesCreated(){
        Log.d(TAG, "onCitiesCreated...");
        if (cities!=null) {
            //agregados
            if (addresses!=null && addresses.size()>0) {
                generateLocationViews(addresses);
            }else {
                addAddressViewGroup(null,null);}
        }else{
            Toast.makeText(LocationsActivity.this,"Hubo un problema, verifique su conexión.",Toast.LENGTH_SHORT);

            this.finish();
        }

    }
    private void createProgressDialog(Context context,String message){
        progressBar = new ProgressDialog(context,R.style.AppTheme_Dark_Dialog);
        progressBar.setIndeterminate(true);
        progressBar.setMessage(message);
        progressBar.show();
    }
    //agregados
    private void generateLocationViews(HashMap<String,Address> mp){
        for (Object o : mp.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            addAddressViewGroup(pair.getKey().toString(), (Address) pair.getValue());
        }
    }

    private void addAddressViewGroup(String key,Address location ){

        Spinner spinner_city;
        final TextView spinner_city_text;
        ImageButton button_remove_location;
        ImageButton button_edit_address;
        ImageButton button_save_address;
        ProgressBar pb;
        ImageButton button_select_map;
        final EditText text_location_singup;

        final FrameLayout fl = new FrameLayout(LocationsActivity.this);
        // Create Layout Parameters for FrameLayout
        LayoutInflater mInflater = LayoutInflater.from(LocationsActivity.this);
        final View currentCustomView = mInflater.inflate(R.layout.address_layout, null);
        text_location_singup = (EditText) currentCustomView.findViewById(R.id.text_location_singup);
        spinner_city_text = (TextView) currentCustomView.findViewById(R.id.spinner_city_text);
        pb = (ProgressBar) currentCustomView.findViewById(R.id.pbLoading_address);
        pb.setVisibility(ProgressBar.VISIBLE);
        spinner_city = (Spinner) currentCustomView.findViewById(R.id.spinner_city);
        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ImageView bullet = (ImageView) view.findViewById(R.id.image);
                View divider = view.findViewById(R.id.spinner_divider);
                divider.setVisibility(View.GONE);
                bullet.setVisibility(View.GONE);
                City city = (City) parent.getItemAtPosition(position);
                spinner_city_text.setText(city.getIdCity());
                Log.d(TAG, "City:" + city.getIdCity() + " " + city.getCity());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        button_select_map = (ImageButton) currentCustomView.findViewById(R.id.button_select_map);
        button_select_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar.make(v, getResources().getString(R.string.prompt_loading_map), Snackbar.LENGTH_LONG);
                snackbar.show();
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    activeTagFrame = fl.getTag().toString();
                    startActivityForResult(builder.build(LocationsActivity.this), PLACE_PICKER_REQUEST);


                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                    text_location_singup.setError("Servicio no disponible");

                }
            }
        });


        button_edit_address = (ImageButton) currentCustomView.findViewById(R.id.button_edit_location);
        button_edit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeTagFrame = fl.getTag().toString();
                edit(currentCustomView,activeTagFrame);
            }
        });
        button_save_address = (ImageButton) currentCustomView.findViewById(R.id.button_save_location);
        button_save_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeTagFrame = fl.getTag().toString();
                save(activeTagFrame);
            }
        });
        button_remove_location = (ImageButton) currentCustomView.findViewById(R.id.button_remove_location);
        button_remove_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCustomView.setBackgroundColor(Color.parseColor(Constants.colorBackgroundAnimation));
                fl.animate()
                        .translationY(0)
                        .alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                addresses.remove(fl.getTag().toString());
                                Log.d(TAG, "locations size:" + addresses.size());
                                fl.setVisibility(View.GONE);
                                addressCounter--;
                                Log.d(TAG,"button_remove_location addressCounter:"+addressCounter);
                                secondary_container_addresses.removeView(fl);
                            }
                        });

            }
        });


        ArrayList<City> list = new ArrayList<City>();
        list.addAll(Arrays.asList(cities));
        CitiesSpinnerAdapter spinnerAdapter = new CitiesSpinnerAdapter(LocationsActivity.this,R.layout.spinner_rows,list);
        spinner_city.setAdapter(spinnerAdapter);
        if (countryName==null) {
            getCountryById(idCountry, currentCustomView);
        }else {
            ((EditText) currentCustomView.findViewById(R.id.text_country)).setText(this.countryName);
            ((ProgressBar)currentCustomView.findViewById(R.id.pbLoading_address)).setVisibility(View.GONE);
        }
        fl.addView(currentCustomView);
        if (key!=null){
            fl.setTag(key);
        }
        else {
            Log.d(TAG, "FIRST addressCounter:" + addressCounter);
            fl.setTag("address_" + addressCounter++);
            Log.d(TAG, "AFTER addressCounter:" + addressCounter);
        }
        secondary_container_addresses.addView(fl);//add mCustomView en otro layout con id unico
        if (secondary_container_addresses.getVisibility()==View.INVISIBLE)
            secondary_container_addresses.setVisibility(View.VISIBLE);

        //load values if address is not null
        if (key!=null)
            loadLocationValues(currentCustomView,location);
    }
    private void edit(View mCustomView,String active){
        enableForm( true, active);
    }
    private void enableForm(boolean enable, String active){
        View view = secondary_container_addresses.findViewWithTag(active);
        if (view!=null) {
            ((EditText) view.findViewById(R.id.text_street)).setEnabled(enable ? true : false);
            ((EditText) view.findViewById(R.id.text_number_ext)).setEnabled(enable ? true : false);
            ((EditText) view.findViewById(R.id.text_number_int)).setEnabled(enable ? true : false);
            ((EditText) view.findViewById(R.id.text_cp)).setEnabled(enable ? true : false);
            ((EditText) view.findViewById(R.id.text_reference)).setEnabled(enable ? true : false);
            ((EditText) view.findViewById(R.id.text_location_alias)).setEnabled(enable ? true : false);
            ((Spinner) view.findViewById(R.id.spinner_city)).setEnabled(enable ? true : false);
            ((ImageButton) view.findViewById(R.id.button_select_map)).setEnabled(enable ? true : false);
            view.setBackgroundColor(Color.parseColor(enable ? Constants.colorBackgroundEnabled : Constants.colorBackgroundDisabled));
        }
    }
    private void save(String active){
        View view = secondary_container_addresses.findViewWithTag(active);
        if (view!=null) {
            EditText text_street = (EditText) view.findViewById(R.id.text_street);
            EditText text_number_ext = (EditText) view.findViewById(R.id.text_number_ext);
            EditText text_number_int = (EditText) view.findViewById(R.id.text_number_int);
            EditText text_cp = (EditText) view.findViewById(R.id.text_cp);
            TextView spinner_city_text = (TextView) view.findViewById(R.id.spinner_city_text);
            TextView text_location_ltd = (TextView) view.findViewById(R.id.text_location_ltd);
            TextView text_location_lng = (TextView) view.findViewById(R.id.text_location_lng);
            EditText text_location_singup = (EditText) view.findViewById(R.id.text_location_singup);
            EditText text_reference = (EditText) view.findViewById(R.id.text_reference);
            EditText text_location_alias = (EditText) view.findViewById(R.id.text_location_alias);

            if (validate(view)) {
                Address location = new Address();
                location.setStreet(text_street.getText().toString());
                location.setOutDoorNumber(text_number_ext.getText().toString());
                location.setInteriorNumber(text_number_int.getText().toString().trim().equals("") ? "SN" : text_number_int.getText().toString());
                location.setCountry(idCountry);
                location.setState(spinner_city_text.getText().toString());
                location.setPc(text_cp.getText().toString());
                location.setReference(text_reference.getText().toString());
                location.setShortName(text_location_alias.getText().toString());
                location.setGoogleAddress(text_location_singup.getText().toString());
                location.setLat(text_location_ltd.getText().toString());
                location.setLng(text_location_lng.getText().toString());
                if (addresses.containsKey(active)){//existe = actualiza, sino agrega
                    Log.d(TAG,"Actualiza:"+active);
                    addresses.remove(active);
                    addresses.put(active,location);
                }
                else{addresses.put(active, location);Log.d(TAG, "Agrega:" + active);}
                enableForm(false, active);
                Log.d(TAG, "save..." + active + "-->" + location.toString());
            }
        }
    }
    private boolean validate(View mCustomView){
        boolean valid = true;
        Log.d(TAG,"validate...");
        EditText text_street = (EditText) mCustomView.findViewById(R.id.text_street);
        EditText text_number_ext = (EditText) mCustomView.findViewById(R.id.text_number_ext);
        EditText text_cp = (EditText) mCustomView.findViewById(R.id.text_cp);
        EditText text_reference = (EditText) mCustomView.findViewById(R.id.text_reference);
        EditText text_location_alias = (EditText) mCustomView.findViewById(R.id.text_location_alias);
        EditText text_location_singup = (EditText) mCustomView.findViewById(R.id.text_location_singup);

        if (text_street.getText().toString().trim().isEmpty()) {
            text_street.setError(getResources().getString(R.string.error_field_required));
            valid = false;
        } else {
            text_street.setError(null);
        }

        if (text_number_ext.getText().toString().trim().isEmpty()) {
            text_number_ext.setError(getResources().getString(R.string.error_field_required));
            valid = false;
        } else {
            text_number_ext.setError(null);
        }

        if (text_cp.getText().toString().trim().isEmpty()) {
            text_cp.setError(getResources().getString(R.string.error_field_required));
            valid = false;
        } else {
            text_cp.setError(null);
        }



        if (text_reference.getText().toString().trim().isEmpty()) {
            text_reference.setError(getResources().getString(R.string.error_required_reference));
            valid = false;
        } else {
            text_reference.setError(null);
        }


        if (text_location_alias.getText().toString().trim().isEmpty()) {
            text_location_alias.setError(getResources().getString(R.string.error_required_location_alias));
            valid = false;
        } else {
            text_location_alias.setError(null);
        }
        if (text_location_singup.getText().toString().trim().isEmpty()) {
            text_location_singup.setError(getResources().getString(R.string.error_required_location));
            valid = false;
        } else {
            text_location_singup.setError(null);
        }

        return valid;
    }
    private void getCountryById(String idCountry,View mCustomView){
        Log.d(TAG,"getCountryById: " + idCountry);
        VolleyGetRequest(Constants.GET_COUNTRIES + "?method=getCountryById&idCountry=" + idCountry, mCustomView );
    }
    private void loadLocationValues(View mCustomView,Address location){
        ((EditText) mCustomView.findViewById(R.id.text_street)).setText(location.getStreet());
        ((EditText) mCustomView.findViewById(R.id.text_number_ext)).setText(location.getOutDoorNumber());
        ((EditText) mCustomView.findViewById(R.id.text_number_int)).setText(location.getInteriorNumber());
        ((EditText) mCustomView.findViewById(R.id.text_cp)).setText(location.getPc());
        ((EditText) mCustomView.findViewById(R.id.text_reference)).setText(location.getReference());
        ((EditText) mCustomView.findViewById(R.id.text_location_alias)).setText(location.getShortName());
        ((EditText) mCustomView.findViewById(R.id.text_location_singup)).setText(location.getGoogleAddress());
        ((TextView) mCustomView.findViewById(R.id.text_location_ltd)).setText(location.getLat());
        ((TextView) mCustomView.findViewById(R.id.text_location_lng)).setText(location.getLng());

        int posicion_categoria = 0;
        for (int i = 0; i < cities.length; i++) {
            if (cities[i].getIdCity().equals(location.getState())) {
                posicion_categoria = i;
                break;
            }
        }

        // Setear selección del Spinner de categorías
        ((Spinner) mCustomView.findViewById(R.id.spinner_city)).setSelection(posicion_categoria);
    }
    public void VolleyGetRequest(String url,final View mCustomView){
        VolleySingleton.
                getInstance(LocationsActivity.this).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                (String) null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Procesar la respuesta Json
                                        processResponse(response, mCustomView);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                                        ((ProgressBar) mCustomView.findViewById(R.id.pbLoading_address)).setVisibility(View.GONE);
                                        Toast.makeText(LocationsActivity.this, "Hay un problema, intenta mas tarde...", Toast.LENGTH_SHORT);
                                    }
                                }

                        )
                );
    }
    private void processResponse(JSONObject response,View mCustomView) {
        try {
            Log.d(TAG,"response: "+response);
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    // Obtener array "publications" Json
                    JSONObject mensaje = response.getJSONObject("country");
                    // Parsear con Gson e inicializar arreglo de ciudades
                    countryName = mensaje.getString("country");
                    idCountry = mensaje.getString("idCountry");
                    ((EditText) mCustomView.findViewById(R.id.text_country)).setText(countryName);
                    ((ProgressBar)mCustomView.findViewById(R.id.pbLoading_address)).setVisibility(View.GONE);

                    break;
                case "2": // FALLIDO
                    ((ProgressBar)mCustomView.findViewById(R.id.pbLoading_address)).setVisibility(View.GONE);
                    String mensaje2 = response.getString("message");
                    Toast.makeText(
                            LocationsActivity.this,
                            mensaje2,
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        EditText text_location_singup = null;
        TextView text_location_ltd = null;
        TextView text_location_lng = null;
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PLACE_PICKER_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Place place = PlacePicker.getPlace(LocationsActivity.this, data);
                    View v = secondary_container_addresses.findViewWithTag(activeTagFrame);
                    text_location_singup = ((EditText) v.findViewById(R.id.text_location_singup));
                    text_location_ltd = ((TextView) v.findViewById(R.id.text_location_ltd));
                    text_location_lng = ((TextView) v.findViewById(R.id.text_location_lng));
                    if (place != null && !place.getAddress().equals("")) {
                        Log.d(TAG, place.getAddress() + " -> " + place.getLatLng() + " valor seteado a:" + activeTagFrame);
                        if (v!=null) {
                            text_location_singup.setError(null);
                            text_location_singup.setText(place.getAddress());
                            LatLng latlng = place.getLatLng();
                            text_location_ltd.setText(latlng.latitude+"");
                            text_location_lng.setText(latlng.longitude+"");
                        }
                    } else {
                        if (v!=null){
                            text_location_singup.setError(getResources().getString(R.string.prompt_error_pointselected));
                            text_location_ltd.setText("");
                            text_location_lng.setText("");
                        }
                    }


                }
                break;
        }
    }
}
