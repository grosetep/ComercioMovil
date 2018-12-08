package estrategiamovil.comerciomovil.ui.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Address;
import estrategiamovil.comerciomovil.modelo.CategoryViewModel;
import estrategiamovil.comerciomovil.modelo.GenericResponse;
import estrategiamovil.comerciomovil.modelo.LoginResponse;
import estrategiamovil.comerciomovil.modelo.SignupResponse;
import estrategiamovil.comerciomovil.modelo.SubCategory;
import estrategiamovil.comerciomovil.modelo.SubSubCategory;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.tools.UserLocalProfile;
import estrategiamovil.comerciomovil.ui.activities.LocationsActivity;
import estrategiamovil.comerciomovil.ui.activities.SelectCategoryActivity;
import estrategiamovil.comerciomovil.ui.adapters.AddressAdapter;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpgradeFragment extends Fragment {
    private JSONArray address_list;
    private RecyclerView recycler_locations;
    private EditText text_bussiness_signup;
    private EditText text_category;
    private EditText text_location_singup;
    private Button email_sign_in_button;
    private CheckBox checkbox_website;
    private ExpandableRelativeLayout layout_website;
    private EditText text_phone_subscriber;
    private EditText text_website_subscriber;
    private ImageButton button_select_location;
    private EditText text_business_description;
    private static final String TAG = UpgradeFragment.class.getSimpleName();
    private Gson gson = new Gson();
    private GenericResponse upgrade_response;
    private HashMap<String, Address> map_locations;
    private CategoryViewModel category;
    private SubCategory sub_category;
    private SubSubCategory sub_sub_category;
    private ProgressDialog progressDialog;
    private final int PLACE_PICKER_REQUEST = 1;

    public static UpgradeFragment newInstance() {
        UpgradeFragment fragment = new UpgradeFragment();
        return fragment;
    }

    public UpgradeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_upgrade, container, false);
        map_locations = new HashMap<>();
        recycler_locations = (RecyclerView) v.findViewById(R.id.recycler_locations);
        recycler_locations.setHasFixedSize(true);
        recycler_locations.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        text_bussiness_signup = (EditText) v.findViewById(R.id.text_bussiness_signup);
        text_category = (EditText) v.findViewById(R.id.text_category);
        text_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectCategoryActivity.class);
                intent.putExtra(FindBusinessFragment.TYPE_FLOW_CATEGORY, String.valueOf(false));
                startActivityForResult(intent, PublishFragment.SELECT_SUBCATEGORY);
            }
        });
        text_location_singup = (EditText) v.findViewById(R.id.text_location_singup);
        text_location_singup.setText("");

        text_phone_subscriber = (EditText) v.findViewById(R.id.text_phone_subscriber);
        button_select_location = (ImageButton) v.findViewById(R.id.button_select_location);
        button_select_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LocationsActivity.class);
                if (map_locations != null) {
                    i.putExtra("map_locations", map_locations);
                }
                startActivityForResult(i, Constants.REQUEST_LOCATIONS);
            }
        });

        email_sign_in_button = (Button) v.findViewById(R.id.email_sign_in_button);
        email_sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        text_website_subscriber = (EditText) v.findViewById(R.id.text_website_subscriber);
        layout_website = (ExpandableRelativeLayout) v.findViewById(R.id.layout_website);
        checkbox_website = (CheckBox) v.findViewById(R.id.checkbox_website);
        checkbox_website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox_website.isChecked()) {
                    layout_website.expand();
                } else {
                    layout_website.collapse();
                }
            }
        });
        text_business_description = (EditText) v.findViewById(R.id.text_business_description);
        return v;
    }

    public void signup() {

        if (!validate()) {
            Toast.makeText(getContext(), getActivity().getString(R.string.error_data_not_valid), Toast.LENGTH_LONG).show();
            return;
        }

        email_sign_in_button.setEnabled(false);
        createProgressDialog("Realizando operación...");


        Log.d(TAG, "lista signup: " + address_list.toString());
        String company = text_bussiness_signup.getText().toString().trim();
        String phone = text_phone_subscriber.getText().toString().trim();
        String web = text_website_subscriber.getText().toString().trim();
        String business_desc = text_business_description.getText().toString().trim();
        HashMap<String, String> params = new HashMap<>();
        params.put("method", "upgradeToSubscriber");
        params.put("idUser", ApplicationPreferences.getLocalStringPreference(getActivity(), Constants.localUserId));
        params.put("email", ApplicationPreferences.getLocalStringPreference(getActivity(), Constants.localEmail));
        params.put("categories", ApplicationPreferences.getLocalStringPreference(getActivity(), Constants.categoriesUser));
        params.put("company", company);
        params.put("phone", phone);
        params.put("id_category", getCategory() != null ? getCategory().getIdCategory() : "0");
        params.put("id_subcategory", getSub_category() != null ? getSub_category().getIdSubCategory() : "0");
        params.put("id_sub_subcategory", getSub_sub_category() != null ? getSub_sub_category().getIdSubSubCategory() : "0");
        params.put("website", checkbox_website.isChecked() ? web : "");
        params.put("address", address_list.toString());//guardar los datos de las ubicaciones a guardar de un usuario(json array)
        String token = FirebaseInstanceId.getInstance().getToken();
        params.put("token", token);
        params.put("business_description", business_desc);
        VolleyPostRequest(Constants.SIGNUP_SUBSCRIBER, params);
    }

    public void VolleyPostRequest(String url, final HashMap<String, String> args) {
        JSONObject jobject = new JSONObject(args);
        Log.d(TAG, "VolleyPostRequest:" + jobject.toString());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            processResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                        email_sign_in_button.setEnabled(true);
                        ShowConfirmations.showConfirmationMessage(getString(R.string.generic_server_timeout),getActivity());
                        dismissProgressDialog();
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
    private void createProgressDialog(String msg){
        progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void dismissProgressDialog(){
        if (progressDialog!=null)
            progressDialog.hide();
    }
    private void processResponse(JSONObject response){
        // On complete call either onSignupSuccess or onSignupFailed
        // depending on success
        Log.d(TAG, response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    JSONObject object = response.getJSONObject("result");
                    if (object.getString("status").equals("1")) {
                        upgrade_response = gson.fromJson(object.toString(), GenericResponse.class);
                        Log.d(TAG,"upgrade_response:"+upgrade_response.toString());
                        onSignupSuccess();
                    }
                    else{
                        onSignupFailed(object);
                    }
                    break;
                case "2":
                    onSignupFailed(response);

                    break;
                case "3":
                    onSignupFailed(response);

                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public boolean validate() {
        boolean valid = true;
        String web = text_website_subscriber.getText().toString().trim();
        String nameBussiness = text_bussiness_signup.getText().toString();
        String phone = text_phone_subscriber.getText().toString();
        String text_business = text_business_description.getText().toString().trim();

        if (nameBussiness.isEmpty() || nameBussiness.length() < 3) {
            text_bussiness_signup.setError("Nombre demasiado corto");
            valid = false;
        } else {
            text_bussiness_signup.setError(null);
        }

        if (sub_sub_category!=null) {
            if (sub_sub_category.getIdSubSubCategory().equals("")) {
                text_category.setError(getString(R.string.error_field_required));
                valid = false;
            } else {
                text_category.setError(null);
            }
        }else{
            text_category.setText(getActivity().getString(R.string.error_field_required));
            valid = false;
        }

        if (phone.isEmpty() || phone.length() < 8 || phone.length() > 10) {
            text_phone_subscriber.setError("Número inválido");
            valid = false;
        } else {
            text_phone_subscriber.setError(null);
        }

        if (checkbox_website.isChecked()){
            String URL_REGEX = "^(https?:\\/\\/)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?$";
            Pattern p = Pattern.compile(URL_REGEX);
            Matcher m = p.matcher(web);
            if(!m.find()) {
                text_website_subscriber.setError("Dirección inválida");
                valid = false;
            } else {
                text_website_subscriber.setError(null);
            }
        }else{
            text_website_subscriber.setError(null);
        }

        if (map_locations==null || map_locations.size()==0){
            recycler_locations.removeAllViews();
            text_location_singup.setText("");
            text_location_singup.setError(getResources().getString(R.string.number_locations_no_added));
            valid =false;
        }else{
            text_location_singup.setError(null);
        }


        if (text_business.isEmpty()) {
            text_business_description.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            text_business_description.setError(null);
        }
        return valid;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case PLACE_PICKER_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Place place = PlacePicker.getPlace(getActivity(), data);
                    if (place != null && !place.getAddress().equals("")) {
                        Log.d(TAG, place.getAddress() + " -> " + place.getLatLng());
                        text_location_singup.setError(null);
                        text_location_singup.setText(place.getAddress());
                    } else {
                        text_location_singup.setError("Elija ubicación exacta en el mapa");
                    }


                }
                break;

            case Constants.REQUEST_LOCATIONS:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.hasExtra("addresses")) {
                        map_locations = (HashMap<String, Address>) data.getSerializableExtra("addresses");
                        if (map_locations!=null && map_locations.size()>0){
                            address_list = convertHashMap(map_locations);
                            generateLocationViews(map_locations);
                            text_location_singup.setError(null);
                            text_location_singup.setText(getResources().getString(R.string.number_locations_added) + " " + map_locations.size());
                        }else{
                            text_location_singup.setText("");
                            text_location_singup.setError(getResources().getString(R.string.number_locations_no_added));
                        }

                    }else{Log.d(TAG, "no hay direcciones:");}
                }
                break;
            case PublishFragment.SELECT_SUBCATEGORY://only for category selection
                if (resultCode == Activity.RESULT_OK) {
                    category = (CategoryViewModel) data.getSerializableExtra(Constants.CATEGORY_SELECTED);
                    sub_category = (SubCategory) data.getSerializableExtra(Constants.SUBCATEGORY_SELECTED);
                    sub_sub_category = (SubSubCategory) data.getSerializableExtra(Constants.SUBSUBCATEGORY_SELECTED);
                    text_category.setText(sub_category.getSubcategory().concat("->").concat(sub_sub_category.getSubsubcategory()));
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
    private JSONArray convertHashMap(HashMap<String,Address> map){
        Iterator it = map.entrySet().iterator();
        JSONArray locations = new JSONArray();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Address tempAddress = (Address)pair.getValue();
            JSONObject object = new JSONObject();
            try{
                object.put("street",tempAddress.getStreet());
                object.put("pc",tempAddress.getPc());
                object.put("outDoorNumber", tempAddress.getOutDoorNumber());
                object.put("interiorNumber",tempAddress.getInteriorNumber());
                object.put("town", tempAddress.getTown());
                object.put("state", tempAddress.getState());
                object.put("country",tempAddress.getCountry());
                object.put("reference",tempAddress.getReference());
                object.put("shortName",tempAddress.getShortName());
                object.put("lat",tempAddress.getLat());
                object.put("lng",tempAddress.getLng());
                object.put("googleAddress",tempAddress.getGoogleAddress());
                locations.put(object);
            }catch(JSONException e){
                continue;
            }
        }

        Log.d(TAG,"locations json:"+locations.toString());
        return locations;
    }
    private void generateLocationViews(HashMap<String,Address> mp){
        ArrayList<Address> list = new ArrayList<>();
        list.addAll(mp.values());
        AddressAdapter mAdapter = new AddressAdapter(getActivity(),list);
        recycler_locations.setAdapter(mAdapter);
    }

    public CategoryViewModel getCategory() {
        return category;
    }

    public SubCategory getSub_category() {
        return sub_category;
    }

    public SubSubCategory getSub_sub_category() {
        return sub_sub_category;
    }

    private LinearLayout getAddressItem(Address address){
        //create row
        LinearLayout item = new LinearLayout(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        item.setLayoutParams(lp);
        TextView title = new TextView(getActivity());
        TextView description = new TextView(getActivity());
        title.setText(address.getShortName());
        title.setLayoutParams(lp);
        description.setLayoutParams(lp);
        description.setText(address.toString());
        item.setOrientation(LinearLayout.VERTICAL);
        item.addView(title);
        item.addView(description);
        return item;

    }
    public void onSignupSuccess() {
        dismissProgressDialog();
        getActivity().setResult(Activity.RESULT_OK, null);
        ApplicationPreferences.saveLocalPreference(getContext(),Constants.showTipSignupSuscriber,String.valueOf(true));
        LoginResponse login = new LoginResponse();
        login.setIdUsuario(ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.localUserId));
        login.setPhone(text_phone_subscriber.getText().toString().trim());
        login.setCompany(text_bussiness_signup.getText().toString().trim());
        login.setWebsite(text_website_subscriber.getText().toString().trim());
        login.setAddress(address_list.toString());
        login.setStatus(Constants.status_user_inactive);
        login.setIdUserType(Constants.user_type_merchant);
        login.setUserType(Constants.user_type_merchant_desc);
        UserLocalProfile.updateSessionValuesUser(getActivity(),login);
        getActivity().finish();
    }
    public void onSignupFailed(JSONObject object) {
        try{
            Toast.makeText(getContext(), object.getString("message"), Toast.LENGTH_LONG).show();
            email_sign_in_button.setEnabled(true);
            dismissProgressDialog();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}