package estrategiamovil.comerciomovil.ui.fragments;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.DetailPublication;
import estrategiamovil.comerciomovil.modelo.ImageSliderPublication;
import estrategiamovil.comerciomovil.modelo.PublicationAddressAditional;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.tools.StringOperations;
import estrategiamovil.comerciomovil.tools.VolleyErrorHelper;
import estrategiamovil.comerciomovil.ui.activities.AskResponseActivity;
import estrategiamovil.comerciomovil.ui.activities.BusinessInfoActivity;
import estrategiamovil.comerciomovil.ui.activities.GalleryActivity;
import estrategiamovil.comerciomovil.ui.activities.LoginActivity;
import estrategiamovil.comerciomovil.ui.activities.PublicationReviewsActivity;
import estrategiamovil.comerciomovil.ui.activities.ReviewPayActivity;
import estrategiamovil.comerciomovil.ui.activities.SendQuestionResponseActivity;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * Created by administrator on 09/06/2016.
 */
public class DetailPublicationFragment extends Fragment implements OnMapReadyCallback, ViewPager.OnPageChangeListener {
    private static final String TAG = DetailPublicationFragment.class.getSimpleName();
    private HashMap<String, String> params;
    private Gson gson = new Gson();
    private AppCompatButton button_buy;
    private TextView detailed_description;
    private TextView important_description;
    private TextView characteristics_description;
    private TextView text_detail_availability;
    private TextView text_detail_effectiveDate;
    private TextView text_detail_percentageOff;
    private TextView text_detail_price;
    private TextView text_detail_priceOff;
    private TextView text_location_info;
    private TextView text_website;
    private TextView text_business_name;
    private LinearLayout layout_business_description;
    private LinearLayout layout_drive;
    private LinearLayout layout_reviews;
    private LinearLayout layout_questions;
    private LinearLayout layout_shipping_on_store;
    private AppCompatButton button_question;
    private TextView text_shipping_not_available;
    private LinearLayout frameLayout;
    private MapView mapView;
    private GoogleMap map;
    private ProgressBar pbLoading_map;
    private DetailPublication detail;
    private ProgressBar pb;
    private ProgressBar pbLoading_address_other;
    private LinearLayout principal_container_detail;
    private LinearLayout layout_other_locations;
    public static int METHOD_GET_PUBLICATION_IMAGES = 1;
    private static int METHOD_GET_PUBLICATION_LOCATIONS = 2;
    private static int METHOD_ADD_FAVORITE = 3;
    /*Result*/
    //Carroussell
    private ViewPager viewPager;
    private float MIN_SCALE = 1f - 1f / 7f;
    private float MAX_SCALE = 1f;
    private static final Integer DURATION = 3500;
    private Timer timer = null;
    private int position;
    private ImageSliderPublication[] images_publication;
    PublicationAddressAditional[] other_address;
    private Bundle savedIS;
    private View currentView;
    public DetailPublicationFragment() {
        // Required empty public constructor
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public DetailPublication getDetail() {
        return detail;
    }

    public void addToCart(){
        String idUser = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.localUserId);
        HashMap<String,String> params = new HashMap<>();
        params.put("method", "addFavorite");
        params.put("idUser", idUser);
        params.put("idPublication", detail!=null?detail.getIdPublication():"0");
        params.put("flag", "true");
        VolleyPostRequest(Constants.GET_FAVORITES, params, METHOD_ADD_FAVORITE);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        //initialize reference to elemements
        detailed_description = (TextView) v.findViewById(R.id.detailed_description);
        important_description = (TextView) v.findViewById(R.id.important_description);
        characteristics_description = (TextView) v.findViewById(R.id.characteristics_description);
        text_detail_availability = (TextView) v.findViewById(R.id.text_detail_availability);
        text_detail_effectiveDate = (TextView) v.findViewById(R.id.text_detail_effectiveDate);
        text_detail_percentageOff = (TextView) v.findViewById(R.id.text_detail_percentageOff);
        text_detail_price = (TextView) v.findViewById(R.id.text_detail_price);
        text_detail_priceOff = (TextView) v.findViewById(R.id.text_detail_priceOff);
        text_location_info = (TextView) v.findViewById(R.id.text_location_info);
        text_website = (TextView) v.findViewById(R.id.text_website);
        text_business_name = (TextView) v.findViewById(R.id.text_business_name);
        layout_business_description = (LinearLayout) v.findViewById(R.id.layout_business_description);
        layout_drive = (LinearLayout) v.findViewById(R.id.layout_drive);
        frameLayout = (LinearLayout) v.findViewById(R.id.website_container);
        principal_container_detail = (LinearLayout) v.findViewById(R.id.principal_container_detail);
        layout_other_locations = (LinearLayout) v.findViewById(R.id.layout_other_locations);
        layout_reviews = (LinearLayout) v.findViewById(R.id.layout_reviews);
        layout_questions = (LinearLayout) v.findViewById(R.id.layout_questions);
        layout_shipping_on_store = (LinearLayout) v.findViewById(R.id.layout_shipping_on_store);
        button_question = (AppCompatButton) v.findViewById(R.id.button_question);
        text_shipping_not_available = (TextView) v.findViewById(R.id.text_shipping_not_available);
        pbLoading_map = (ProgressBar)v.findViewById(R.id.pbLoading_map);
        pb = (ProgressBar) v.findViewById(R.id.pbLoading_detail);
        pbLoading_address_other = (ProgressBar) v.findViewById(R.id.pbLoading_address_other);
        pbLoading_address_other.setVisibility(View.VISIBLE);
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        pb.setVisibility(ProgressBar.VISIBLE);
        savedIS = savedInstanceState;
        button_buy = (AppCompatButton) v.findViewById(R.id.button_buy);
        button_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(getContext(), ReviewPayActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable(Constants.user_cupon_selected, getDetail());
                    b.putString(ReviewPayFragment.EXTRA_PATH_FIRST_IMAGE, (getImages_publication()[0]).getPath());
                    b.putString(ReviewPayFragment.EXTRA_NAME_FIRST_IMAGE, (getImages_publication()[0]).getImageName());
                    intent.putExtras(b);
                    startActivity(intent);

            }
        });
        layout_business_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), BusinessInfoActivity.class);
                i.putExtra(BusinessInfoFragment.INFO_COMPANY,getDetail().getBusinessDescription());
                startActivity(i);
            }
        });
        layout_drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String geolocation = "geo:".concat(getDetail().getLatitude()).concat(",").concat(getDetail().getLongitude());
                Uri gmmIntentUri = Uri.parse(geolocation+"?q=" + Uri.encode(getDetail().getGoogleAddress()));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                //mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }else{
                    ShowConfirmations.showConfirmationMessage(getString(R.string.generic_error),getActivity());}
            }
        });
        layout_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PublicationReviewsActivity.class);
                i.putExtra(PublicationReviewsActivity.ID_PUBLICATION,getDetail().getIdPublication());
                startActivity(i);
            }
        });
        layout_questions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AskResponseActivity.class);
                i.putExtra(AskResponseActivity.ID_PUBLICATION,getDetail().getIdPublication());
                i.putExtra(AskResponseActivity.ANSWERABLE,String.valueOf(false));
                startActivity(i);
            }
        });
        button_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idUser = ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.localUserId);
                if(idUser==null || idUser.equals("")) {
                    getActivity().finish();
                    Intent i = new Intent(getActivity(),LoginActivity.class);
                    startActivity(i);
                }else{
                    Intent intent = new Intent(getActivity(), SendQuestionResponseActivity.class);
                    intent.putExtra(SendQuestionResponseActivity.ID_PUBLICATION, getDetail().getIdPublication());
                    intent.putExtra(SendQuestionResponseActivity.ANSWERABLE, String.valueOf(false));
                    intent.putExtra(SendQuestionResponseActivity.FLOW,SendQuestionResponseActivity.FLOW_FROM_DETAIL);
                    startActivity(intent);
                }
            }
        });

        currentView = v;
        //call methods
        if (getParams()!=null && getParams().get("idPublication")!=null) {
            getPublicationImages(getParams().get("idPublication"));
            loadInformation();//llamar load information despues al finalizar consulta de imagenes
            loadOtherLocations(getParams().get("idPublication"));
        }else{
            getActivity().onBackPressed();
        }

        return v;
    }

    public ImageSliderPublication[] getImages_publication() {
        return images_publication;
    }

    private void loadOtherLocations(String idPublication){

        HashMap<String,String> params = new HashMap<>();
        params.put("method", "getLocationsByIdPublication");
        params.put("idPublication", idPublication);
        VolleyPostRequest(Constants.GET_PUBLICATIONS, params, METHOD_GET_PUBLICATION_LOCATIONS);
    }
    private void setupViewPager(ViewPager viewPager,ImageSliderPublication[] images) {
        viewPager.setAdapter(new CardsPagerAdapter(images));
        //viewPager.setPageMargin(-156);
        //viewPager.setOffscreenPageLimit(3);
        viewPager.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                Log.d(TAG, "onDrag...");
                return false;
            }
        });
    }

    public void loadMap(){
        if (getActivity()!=null) {
            MapsInitializer.initialize(getActivity());
            mapView = (MapView) currentView.findViewById(R.id.map);
            mapView.onCreate(savedIS);
            if (mapView != null) {
                mapView.getMapAsync(this);
            }
        }
    }
    public void loadInformation() {

        // Añadir parámetro a la URL del web service
        String newURL = Constants.GET_PUBLICATIONS + "?method=getDetailPublication" + "&idPublication=" + params.get("idPublication");
        Log.d(TAG, "newURL:" + newURL);
        // Realizar petición GET_BY_ID
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                newURL,
                (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar la respuesta Json
                        processingResponse(response);
                        //  pDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        String message = VolleyErrorHelper.getErrorType(error, getActivity());
                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                        Toast.makeText(
                                getActivity(),
                                message,
                                Toast.LENGTH_SHORT).show();
                    }
                }

        );
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.
                getInstance(getActivity()).
                addToRequestQueue(request
                );
    }

    public void VolleyPostRequest(String url, HashMap<String, String> params, final int callback){
        JSONObject jobject = new JSONObject(params);
        Log.d(TAG, "VolleyPostRequest:" + jobject.toString());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar la respuesta del servidor
                        if (callback == METHOD_GET_PUBLICATION_IMAGES)
                            setArrayImages(response);
                        else if (callback == METHOD_GET_PUBLICATION_LOCATIONS) {
                            setArrayLocations(response);
                        }else if(callback == METHOD_ADD_FAVORITE){
                            confirmProductAdded(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                        //dismissProgressDialog();
                        String mensaje2 = "Verifique su conexión a Internet.";
                        Toast.makeText(
                                getActivity(),
                                mensaje2,
                                Toast.LENGTH_SHORT).show();
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
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
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }
    private void showConfirmationMessage(final String message){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void confirmProductAdded(JSONObject response){
        Log.d(TAG,"confirmProductAdded: "+ response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");

            switch (status) {
                case "1":
                    JSONObject object = response.getJSONObject("result");
                    showConfirmationMessage(object.getString("message"));
                    Log.d(TAG,object.getString("message"));
                    break;
                case "2":
                    showConfirmationMessage(response.getJSONObject("message").toString());
                    Log.d(TAG,response.getJSONObject("message").toString());
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                showConfirmationMessage(response.getJSONObject("message").toString());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

    }

    private void setArrayLocations(JSONObject response){
        Log.d(TAG, response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    JSONArray result = response.getJSONArray("result");
                    other_address = gson.fromJson(result.toString(), PublicationAddressAditional[].class);
                    //Log.d(TAG, "response:----------" + other_address.toString());
                    if (other_address.length>1){
                        generateAddressesSection(other_address);
                    }else{
                        pbLoading_address_other.setVisibility(View.GONE);
                    }
                    break;
                case "2":
                    pbLoading_address_other.setVisibility(View.GONE);
                    layout_other_locations.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            pbLoading_address_other.setVisibility(View.GONE);
            layout_other_locations.setVisibility(View.GONE);
        }
    }
    private void generateAddressesSection(PublicationAddressAditional[] other_address){

        if (other_address!=null && other_address.length>0) {
            LinearLayout item = new LinearLayout(getActivity());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            for (PublicationAddressAditional p : other_address) {
                TextView addressTemp = new TextView(getActivity());
                addressTemp.setText(p.getGoogleAddress() + ". " + p.getReference());
                addressTemp.setLayoutParams(lp);
                layout_other_locations.addView(addressTemp);
            }
            pbLoading_address_other.setVisibility(View.GONE);
            layout_other_locations.setVisibility(View.VISIBLE);
        }

    }
    private void setArrayImages(JSONObject response){
        Log.d(TAG, response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    JSONArray result = response.getJSONArray("result");
                    images_publication = gson.fromJson(result.toString(), ImageSliderPublication[].class);
                    Log.d(TAG, "response:----------");
                    if (viewPager != null) {// se ejcuta despues de haber consultado
                        setupViewPager(viewPager,images_publication);
                        if (getActivity()!=null) {
                            TextView num_photos = (TextView) getActivity().findViewById(R.id.text_number_photos);
                            if (num_photos != null) {
                                num_photos.setText("" + images_publication.length);
                            }
                            start();
                        }

                    }
                    break;
                case "2":
                    onFailedImages();
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onFailedImages();
        }

    }
    private void onFailedImages(){
        images_publication = new ImageSliderPublication[1];
        ImageSliderPublication img = new ImageSliderPublication();
        img.setIdPathImage("1");
        int lastSlah = params.get("imageFirstPath").lastIndexOf("/");
        img.setPath(params.get("imageFirstPath").substring(0, lastSlah + 1));
        img.setImageName(params.get("imageFirstPath").substring(lastSlah - 1, params.get("imageFirstPath").length()));
        Log.d(TAG,"Error en consulta de imagenes, se poner la anterior: path:"+img.getPath()+ " name:"+img.getImageName());
        images_publication[0]=img;
        ((TextView)getActivity().findViewById(R.id.text_number_photos)).setText(""+images_publication.length);
    }
    public void processingResponse(JSONObject response) {
        //Log.d(TAG, "response:" + response.toString());
        try {
            // Obtener atributo "mensaje"
            String message = response.getString("status");
            switch (message) {
                case "1"://assign values
                    JSONObject object = response.getJSONObject("details");
                    detail = gson.fromJson(object.toString(), DetailPublication.class);
                    detailed_description.setText(detail.getDetailedDescription());
                    important_description.setText(detail.getImportant());
                    characteristics_description.setText(detail.getCharacteristics());

                    text_detail_availability.setText(detail.getAvailability());
                    String label_text ="";
                    if (detail.getEffectiveDate()!=null){
                        int days_created = Integer.parseInt(detail.getEffectiveDate());
                        switch (days_created){
                            case 0:label_text="Hoy";break;
                            case 1:label_text=getString(R.string.text_promt_in)+ days_created+" día";break;
                            default:label_text=getString(R.string.text_promt_in)+days_created+" días";break;
                        }
                    }
                    text_detail_effectiveDate.setText(label_text);
                    text_detail_percentageOff.setText(StringOperations.getPercentageFormat(detail.getPercentageOff()));
                    text_detail_price.setText(StringOperations.getAmountFormatWithNoDecimals(detail.getRegularPrice()));
                    text_detail_priceOff.setText(StringOperations.getAmountFormatWithNoDecimals(detail.getOfferPrice()));
                    text_location_info.setText(detail.getGoogleAddress() + ". " + detail.getReference());
                    text_business_name.setText(detail.getCompany());
                    text_website.setText((detail.getWebsite() != null && !detail.getWebsite().trim().equals("")) ? "Ver Sitio Web" : "Sitio Web no disponible");
                    if (detail.getWebsite()!=null && !detail.getWebsite().trim().equals("")) {
                        frameLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(detail.getWebsite()));
                                startActivity(i);
                            }
                        });
                    }else{frameLayout.setVisibility(View.GONE);}
                    principal_container_detail.setVisibility(View.VISIBLE);
                    if (!detail.getVirtual().equals(String.valueOf(Constants.cero))){text_shipping_not_available.setVisibility(View.VISIBLE);}
                    if (!detail.getShippingOnStore().equals(String.valueOf(Constants.cero))){layout_shipping_on_store.setVisibility(View.VISIBLE);}
                    pb.setVisibility(View.GONE);

                    loadMap();
                    break;
                case "2"://no detail
                    pb.setVisibility(View.GONE);
                    String errorMessage = response.getString("message");
                    Toast.makeText(
                            getActivity(),
                            errorMessage,
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public static DetailPublicationFragment createInstance(HashMap<String, String> arguments) {
        DetailPublicationFragment fragment = new DetailPublicationFragment();
        fragment.setParams(arguments);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {//metodo que se ejecuta primero que onCreateView
        super.onCreate(savedInstanceState);
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (detail!=null) {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            if (detail.getLatitude()!=null && detail.getLongitude()!=null) {Log.d(TAG,"pone coordenadas...");
                Double latitude = Double.parseDouble(detail.getLatitude());
                Double longitude = Double.parseDouble(detail.getLongitude());
                LatLng shop = new LatLng(latitude, longitude);
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(shop)
                        .title((detail.getCompany() != null && !detail.getCompany().equals("")) ? detail.getCompany():"Aquí estamos")
                        .snippet(detail.getReference()));
                marker.showInfoWindow();
                map.moveCamera(CameraUpdateFactory.newLatLng(shop));
                pbLoading_map.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
            }
        }else{
            Log.d(TAG,"No hay coordenadas en momento de carga...");
        }
    }

    private void getPublicationImages(String idPublication){
        HashMap<String,String> params = new HashMap<>();
        //text
        params.put("method","getImagesByIdPublication");
        params.put("idPublication", idPublication);
        VolleyPostRequest(Constants.GET_PUBLICATIONS, params,METHOD_GET_PUBLICATION_IMAGES);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            try {
                mapView.onDestroy();
            } catch (NullPointerException e) {
                Log.e(TAG, "Error while attempting MapView.onDestroy(), ignoring exception", e);
            }
        }
    }

    @Override
    public void onResume() {
        if (mapView != null) {
            mapView.onResume();
        }
        start();
        super.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.d(TAG, "onPageScrollStateChanged...");
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
       /* for (int i = 0; i < viewPager.getChildCount(); i++) {
            View cardView = viewPager.getChildAt(i);
            int itemPosition = (Integer) cardView.getTag();

            if (itemPosition == position) {
                cardView.setScaleX(MAX_SCALE - positionOffset / 7f);
                cardView.setScaleY(MAX_SCALE - positionOffset / 7f);
            }

            if (itemPosition == (position + 1)) {
                cardView.setScaleX(MIN_SCALE + positionOffset / 7f);
                cardView.setScaleY(MIN_SCALE + positionOffset / 7f);
            }
        }*/
    }

    @Override
    public void onPageSelected(int position) {

    }
    /**********************************Auto Scrolling**************************************************/
    public void start() {
        if (timer != null) {
            timer.cancel();
        }
        position = 0;
        startSlider();
    }
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    public void startSlider() {
        timer = new Timer();
        if (getActivity() != null) {
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    // avoid exception:
                    // "Only the original thread that created a view hierarchy can touch its views"
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            if (images_publication != null) {
                                if (position > images_publication.length) { // longitud del arreglo de urls
                                    position = 0;
                                    viewPager.setCurrentItem(position++);
                               /*stop();
                                Toast.makeText(getApplicationContext(), "Timer stoped",
                                        Toast.LENGTH_LONG).show();*/
                                } else {
                                    viewPager.setCurrentItem(position++);
                                }
                            }

                        }
                    });
                }

            }, 0, DURATION);
        }
    }
    // Stops the slider when the Activity is going into the background

    @Override
    public void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
    }



    /************************************************adapter**********************************************************************/
    private class CardsPagerAdapter extends PagerAdapter {

        private boolean mIsDefaultItemSelected = false;
        private ImageSliderPublication[] gallery;
        public CardsPagerAdapter(ImageSliderPublication[] images){
            gallery = images;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView cardImageView = (ImageView) View.inflate(container.getContext(), R.layout.imageview_card, null);
            //cardImageView.setImageDrawable(getResources().getDrawable(gallery[position]));
            ImageSliderPublication temp =  gallery[position];
            Glide.with(cardImageView.getContext())
                    .load(temp.getPath()+temp.getImageName())
                    .centerCrop()
                    .into(cardImageView);

            cardImageView.setTag(position);

          /*  if (!mIsDefaultItemSelected) {
                cardImageView.setScaleX(MAX_SCALE);
                cardImageView.setScaleY(MAX_SCALE);
                mIsDefaultItemSelected = true;
            } else {
                cardImageView.setScaleX(MIN_SCALE);
                cardImageView.setScaleY(MIN_SCALE);
            }
*/
            cardImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startGalleryActivity();
                }
            });
            container.addView(cardImageView);
            return cardImageView;
        }
        public void startGalleryActivity(){
            Intent intent = new Intent(getActivity(), GalleryActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.GALLERY,gallery);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return gallery.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }


}