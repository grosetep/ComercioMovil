package estrategiamovil.comerciomovil.ui.fragments;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Address;
import estrategiamovil.comerciomovil.modelo.CategoryViewModel;
import estrategiamovil.comerciomovil.modelo.GenericResponse;
import estrategiamovil.comerciomovil.modelo.ImagePublication;
import estrategiamovil.comerciomovil.modelo.Section;
import estrategiamovil.comerciomovil.modelo.SubCategory;
import estrategiamovil.comerciomovil.modelo.SubSubCategory;
import estrategiamovil.comerciomovil.modelo.SuscriptionDetail;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ImagePicker;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.tools.UploadImage;
import estrategiamovil.comerciomovil.tools.UserSuscription;
import estrategiamovil.comerciomovil.tools.UtilPermissions;
import estrategiamovil.comerciomovil.ui.activities.SelectCategoryActivity;
import estrategiamovil.comerciomovil.ui.activities.SelectCharacteristicsActivity;
import estrategiamovil.comerciomovil.ui.activities.SelectDetailedDescriptionActivity;
import estrategiamovil.comerciomovil.ui.activities.SelectLocationActivity;
import estrategiamovil.comerciomovil.ui.activities.SelectOutstandingActivity;
import estrategiamovil.comerciomovil.ui.activities.SelectSubCategoryActivity;
import estrategiamovil.comerciomovil.ui.activities.ShowConditionsActivity;
import estrategiamovil.comerciomovil.ui.activities.ShowImageActivity;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class PublishFragment extends Fragment {
    public static final Integer DURATION = 1000;
    private Timer timer = null;

    //switch
    private Switch switch_type_publication;
    private TextView text_type_publication_selected;
    private Switch switch_emergent;
    //User GUI
    private ImageButton button_add_picture;
    private LinearLayout layout_category;
    private LinearLayout layout_location;
    private LinearLayout list_new_publish_images;
    private LinearLayout layout_effective_date;
    private LinearLayout layout_detailed_description;
    private LinearLayout layout_outstanding;
    private LinearLayout layout_characteristics;
    private LinearLayout layout_emergent;
    private RelativeLayout general_container;
    private TextView text_conditions;
    private CheckBox checkbox_conditions;
    private Button button_publish;
    private ProgressDialog progressDialog;
    private LinearLayout layout_loading;
    private TextView text_effective_date;
    //type and dimens product
    private LinearLayout layout_product_type;
    private RadioButton check_standard;
    private RadioButton check_virtual;
    private TextView text_product_type_info;
    private LinearLayout layout_product_dimens;
    private EditText text_weight;
    private EditText text_heigth;
    private EditText text_long;
    private EditText text_wide;
    //shipping options
    private LinearLayout layout_shipping_type;
    private LinearLayout layout_shipping_option_home;
    private LinearLayout layout_shipping_options;
    private AppCompatCheckBox check_shipping_on_store;
    private AppCompatCheckBox check_shipping_options;
    //text fields
    private MaterialEditText text_cover;
    private Spinner spinner_percentageOff;
    private LinearLayout layout_percentageOff;
    private MaterialEditText text_availability;
    private MaterialEditText text_regular_price;
    private MaterialEditText text_offer_price;


    //Class Variables
    public static final String EXTRA_PATH = "path";
    public static final String EXTRA_TAG = "tag";
    public static final String EXTRA_LABEL = "label";
    private static final int SHOW_IMAGE = 1;
    private static final int PICK_IMAGE_ID = 2;
    public static final int SELECT_CATEGORY = 3;
    public static final int SELECT_SUBCATEGORY = 4;
    private static final int SELECT_LOCATION = 5;
    public static final int SELECT_DETAILED_DESCRIPTION = 6;
    public static final int SELECT_OUTSTANDING = 7;
    public static final int SELECT_CHARACTERISTICS = 8;
    public static final int SELECT_SUBSUBCATEGORY = 9;
    private static int counterReset = 0;
    public static final int ALERT_DISCART = 0;
    public static final int ALERT_RETRY = 1;
    private final int METHOD_PUBLISH = 1;
    private final int METHOD_REMOVE_PUBLICATION = 2;
    private final int SHOW_NOTIFICATION_IMAGE = 1;
    private final int SHOW_NOTIFICATION_SUSCRIPTION = 2;
    private static final String TAG = PublishFragment.class.getSimpleName();
    private Section[] sections;
    private Bitmap bitmap;
    private Context context;
    private CategoryViewModel category;
    private SubCategory sub_category;
    private SubSubCategory sub_sub_category;
    private HashMap<String,Address> addresses;
    private StringBuffer ids_locations;
    private StringBuffer ids_states_publication;
    private String detailed_description;
    private String outstanding;
    private String characteristics;
    private String date;
    private HashMap<String,ImagePublication> image_list = new HashMap<>();
    private Gson gson = new Gson();
    //dividers
    private View divider_5;
    private View divider_6;
    private View divider_7;
    private View divider_8;
    private View divider_9;
    private View divider_10;
    //list Views
    ArrayList<View> fields = new ArrayList<View>();
    String id_new_publication;

    public static PublishFragment newInstance() {
        PublishFragment fragment = new PublishFragment();
        return fragment;
    }

    public PublishFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!UtilPermissions.hasPermissions(getActivity(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, UtilPermissions.PERMISSION_ALL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_publish, container, false);
        context = getActivity().getApplicationContext();
        //dividers
        divider_5 = (View) v.findViewById(R.id.divider_5);
        divider_6 = (View) v.findViewById(R.id.divider_6);
        divider_7 = (View) v.findViewById(R.id.divider_7);
        divider_8 = (View) v.findViewById(R.id.divider_8);
        divider_9 = (View) v.findViewById(R.id.divider_9);
        divider_10 = (View) v.findViewById(R.id.divider_10);
        //type and dimens product
        layout_product_type = (LinearLayout) v.findViewById(R.id.layout_product_type);
        check_standard = (RadioButton) v.findViewById(R.id.check_standard);
        check_virtual = (RadioButton) v.findViewById(R.id.check_virtual);
        text_product_type_info = (TextView) v.findViewById(R.id.text_product_type_info);

        layout_product_dimens = (LinearLayout) v.findViewById(R.id.layout_product_dimens);
        text_weight = (EditText) v.findViewById(R.id.text_weight);
        text_heigth = (EditText) v.findViewById(R.id.text_heigth);
        text_long = (EditText) v.findViewById(R.id.text_long);
        text_wide=(EditText) v.findViewById(R.id.text_wide);
        //shipping options
        layout_shipping_type = (LinearLayout) v.findViewById(R.id.layout_shipping_type);
        layout_shipping_option_home = (LinearLayout) v.findViewById(R.id.layout_shipping_option_home);
        layout_shipping_options = (LinearLayout) v.findViewById(R.id.layout_shipping_options);

        check_shipping_on_store = (AppCompatCheckBox) v.findViewById(R.id.check_shipping_on_store);
        check_shipping_options = (AppCompatCheckBox) v.findViewById(R.id.check_shipping_options);

        //events type and dimens product
        check_standard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_virtual.setChecked(false);
                text_product_type_info.setText(R.string.text_product_type_standard_info);
                //layout_product_dimens.setVisibility(View.VISIBLE);por el momento deshabilitado
                enableShippingProductStandard(true);
            }
        });
        check_virtual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_standard.setChecked(false);
                text_product_type_info.setText(R.string.text_product_type_virtual_info);
                //layout_product_dimens.setVisibility(View.GONE);//por el momento deshabilitado
                enableShippingProductStandard(false);
            }
        });
        //add picture
        button_add_picture = (ImageButton) v.findViewById(R.id.button_add_picture);
        button_add_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show tip
                String show_tip = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.showTipImage);
                if (image_list.size()==0 && show_tip.equals("")){
                    ShowNotification_Popup(SHOW_NOTIFICATION_IMAGE);
                }
                else{
                    pickImage();
                }

            }
        });
        list_new_publish_images = (LinearLayout) v.findViewById(R.id.list_new_publish_images);
        //add category
        layout_category = (LinearLayout) v.findViewById(R.id.layout_category);
        layout_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                //String idCategoryAds =ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.local_idCategory_Ads);
                //if (switch_type_publication.isChecked()) {
                    intent = new Intent(context, SelectCategoryActivity.class);
                    intent.putExtra(FindBusinessFragment.TYPE_FLOW_CATEGORY,String.valueOf(false));
                    startActivityForResult(intent, SELECT_CATEGORY);
                //}else{
                /*    intent = new Intent(context, SelectSubCategoryActivity.class);
                    intent.putExtra(Constants.CATEGORY_SELECTED, idCategoryAds);
                    intent.putExtra(FindBusinessFragment.TYPE_FLOW_CATEGORY,String.valueOf(false));
                    startActivityForResult(intent, SELECT_SUBCATEGORY);*/
                //}

            }
        });
        //add location
        layout_location = (LinearLayout) v.findViewById(R.id.layout_location);
        layout_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SelectLocationActivity.class);
                intent.putExtra(Constants.ADDRESS_SELECTED,addresses);
                startActivityForResult(intent, SELECT_LOCATION);
            }
        });
        layout_effective_date = (LinearLayout) v.findViewById(R.id.layout_effective_date);
        layout_effective_date.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment picker = new DatePickerFragment();
                        picker.show(getFragmentManager(), "datePicker");

                    }
                }
        );
        layout_detailed_description = (LinearLayout) v.findViewById(R.id.layout_detailed_description);
        layout_detailed_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SelectDetailedDescriptionActivity.class);
                intent.putExtra(Constants.DESCRIPTION_SELECTED,detailed_description);
                startActivityForResult(intent, SELECT_DETAILED_DESCRIPTION);
            }
        });

        layout_outstanding = (LinearLayout) v.findViewById(R.id.layout_outstanding);
        layout_outstanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SelectOutstandingActivity.class);
                intent.putExtra(Constants.OUTSTANDING_SELECTED,outstanding);
                startActivityForResult(intent, SELECT_OUTSTANDING);
            }
        });
        layout_characteristics = (LinearLayout) v.findViewById(R.id.layout_characteristics);
        layout_characteristics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SelectCharacteristicsActivity.class);
                intent.putExtra(Constants.CHARACTERISTICS_SELECTED,characteristics);
                startActivityForResult(intent, SELECT_CHARACTERISTICS);
            }
        });

        text_conditions = (TextView) v.findViewById(R.id.text_conditions);
        text_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowConditionsActivity.class);
                startActivity(intent);
            }
        });
        checkbox_conditions = (CheckBox) v.findViewById(R.id.checkbox_conditions);
        button_publish = (Button) v.findViewById(R.id.button_publish);
        button_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    publish();
                }
            }
        });
        text_cover = (MaterialEditText) v.findViewById(R.id.text_cover);
        spinner_percentageOff = (Spinner) v.findViewById(R.id.spinner_percentageOff);
        spinner_percentageOff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculate_price();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        layout_percentageOff = (LinearLayout) v.findViewById(R.id.layout_percentageOff);
        text_availability = (MaterialEditText) v.findViewById(R.id.text_availability);
        text_regular_price = (MaterialEditText) v.findViewById(R.id.text_regular_price);
        text_regular_price.addTextChangedListener(changePrices);
        text_offer_price = (MaterialEditText) v.findViewById(R.id.text_offer_price);
        text_effective_date = (TextView) v.findViewById(R.id.text_effective_date);
        //effective_date_additional_title = (TextView) v.findViewById(R.id.effective_date_additional_title);
        layout_emergent = (LinearLayout) v.findViewById(R.id.layout_emergent);
        switch_emergent = (Switch) v.findViewById(R.id.switch_emergent);
        String campain = ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.sectionNames);
        JSONArray  object = null;
        try {
            object = new JSONArray (campain);
            Gson gson = new Gson();
            sections =  gson.fromJson(object.toString(), Section[].class);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (sections!=null){
            for(Section s:sections){
                if (s.getEmergent().equals("1"))
                switch_emergent.setText(s.getSection());
            }
        }
        addresses = new HashMap<>();
        layout_loading = (LinearLayout) v.findViewById(R.id.layout_loading);
        general_container = (RelativeLayout) v.findViewById(R.id.general_container);
        //switch

        text_type_publication_selected = (TextView) v.findViewById(R.id.text_type_publication_selected);
        switch_type_publication = (Switch) v.findViewById(R.id.switch_type_publication);
        switch_type_publication.setChecked(false);
        setViewsToAnimate();
        showPublicationFields(false);
        switch_type_publication.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){//mostrar campos para publicacion si cuenta con suscripcion cupones
                    SuscriptionDetail suscription = UserSuscription.getSuscriptionDetails(getContext());
                    if (suscription.getSuscription_type().equals(Constants.suscription_cupons) || suscription.getStatus_suscription().equals(Constants.free_period)) {
                        text_type_publication_selected.setText(getResources().getString(R.string.text_promt_type_sale));
                        showPublicationFields(true);
                        text_effective_date.setText("...");
                        //effective_date_additional_title.setVisibility(View.GONE);
                    }else{
                        text_type_publication_selected.setText(getResources().getString(R.string.text_promt_type_ads));
                        showPublicationFields(false);
                        switch_type_publication.setChecked(false);
                        text_effective_date.setText("...");
                        //effective_date_additional_title.setVisibility(View.VISIBLE);
                        ShowNotification_Popup(SHOW_NOTIFICATION_SUSCRIPTION);
                    }
                }else{

                    text_type_publication_selected.setText(getResources().getString(R.string.text_promt_type_ads));
                    showPublicationFields(false);
                    text_effective_date.setText("...");
                    //effective_date_additional_title.setVisibility(View.VISIBLE);
                }
                sub_category = null;
                sub_sub_category = null;
                ((TextView)layout_category.findViewById(R.id.text_category)).setText(getResources().getString(R.string.text_promt_select_category));

            }
        });
        //setting default effective date
        if (!switch_type_publication.isChecked()){//ads
            text_effective_date.setText("...");
            //effective_date_additional_title.setVisibility(View.VISIBLE);
        }
        //type and shipping actions

        return v;
    }
    private void pickImage(){
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity(),getString(R.string.pick_image));
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }
    private void enableShippingProductStandard(boolean flag){
        check_shipping_on_store.setChecked(flag?true:false);
        check_shipping_on_store.setVisibility(flag?View.VISIBLE:View.GONE);
        check_shipping_options.setChecked(false);
        check_shipping_options.setVisibility(flag?View.VISIBLE:View.GONE);
        layout_shipping_type.setVisibility(flag?View.VISIBLE:View.GONE);
    }
    private void setViewsToAnimate(){
        fields.add(layout_percentageOff);
        fields.add(text_availability);
        fields.add(text_regular_price);
        fields.add(layout_detailed_description);
        fields.add(layout_characteristics);
        fields.add(layout_emergent);
        //type and dimens product
        fields.add(layout_product_type);
        //fields.add(layout_product_dimens);por el momento deshabilitado
        //chipping options
        fields.add(layout_shipping_type);

        fields.add(divider_5);
        fields.add(divider_6);
        fields.add(divider_7);
        fields.add(divider_8);
        fields.add(divider_9);
        fields.add(divider_10);
    }
    private void showPublicationFields(final boolean flag){
        for(final View view:fields) {
            view.animate()
                    .alpha((flag)?(1.0f):(0.0f))
                    .setDuration(1000)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setVisibility((flag)?View.VISIBLE:View.GONE);

                        }
                    });
        }
        if(flag) {
            text_offer_price.setClickable(false);
            text_offer_price.setCursorVisible(false);
            text_offer_price.setFocusable(false);
            text_offer_price.setFocusableInTouchMode(false);
            text_offer_price.setScrollContainer(true);


        }else {
            text_offer_price.setClickable(true);
            text_offer_price.setCursorVisible(true);
            text_offer_price.setFocusable(true);
            text_offer_price.setFocusableInTouchMode(true);
            text_offer_price.setScrollContainer(true);
        }

    }
    private boolean validate(){
        boolean valid = true;
        int numOfImages = image_list.size();
        String cover = text_cover.getText().toString().trim();
        String percentageOff = spinner_percentageOff.getSelectedItem().toString().trim();
        String availability = text_availability.getText().toString().trim();
        String regular_price = text_regular_price.getText().toString().trim();
        String offer_price = text_offer_price.getText().toString().trim();


        if (!checkbox_conditions.isChecked()){
            valid = false;
            Snackbar snackbar = Snackbar.make(spinner_percentageOff, getResources().getString(R.string.error_conditions_required), Snackbar.LENGTH_LONG);
            snackbar.show();
            return valid;
        }

        if (numOfImages==0){
            valid = false;
            Snackbar snackbar = Snackbar.make(spinner_percentageOff, getResources().getString(R.string.error_image_required), Snackbar.LENGTH_LONG);
            snackbar.show();
            return valid;
        }

        if (cover.isEmpty()){
            text_cover.setError("Campo obligatorio");
            valid = false;
        } else {
            text_cover.setError(null);
        }
        String options[] = getResources().getStringArray(R.array.percentages);
        if(switch_type_publication.isChecked()) {
            if (percentageOff.equals(options[0])) {
                //text_percentageOff.setError("Campo obligatorio");
                Snackbar snackbar = Snackbar.make(spinner_percentageOff, getResources().getString(R.string.error_percentageOff_required), Snackbar.LENGTH_LONG);
                snackbar.show();
                valid = false;
            }

            if (availability.isEmpty()) {
                text_availability.setError("Campo obligatorio");
                valid = false;
            } else {
                text_availability.setError(null);
            }

            if (regular_price.isEmpty()) {
                text_regular_price.setError("Campo obligatorio");
                valid = false;
            } else {
                text_regular_price.setError(null);
            }
        }
        if (offer_price.isEmpty()){
            text_offer_price.setError("Campo obligatorio");
            valid = false;
        } else {
            text_offer_price.setError(null);
        }

        if (date == null || date.isEmpty()) {
            valid = false;
            text_effective_date.setError("Campo obligatorio");
        } else {
            text_effective_date.setError(null);
        }

        if (sub_sub_category==null || sub_category==null){
            valid = false;
            Snackbar snackbar = Snackbar.make(spinner_percentageOff, getResources().getString(R.string.error_category_required), Snackbar.LENGTH_LONG);
            snackbar.show();
            return valid;
        }
        if (addresses==null || (addresses!=null && addresses.size()==0)){
            valid = false;
            Snackbar snackbar = Snackbar.make(spinner_percentageOff, getResources().getString(R.string.error_required_location), Snackbar.LENGTH_LONG);
            snackbar.show();
            return valid;
        }

        if (outstanding == null) {
            valid = false;
            Snackbar snackbar = Snackbar.make(spinner_percentageOff, getResources().getString(R.string.error_outstanding_required), Snackbar.LENGTH_LONG);
            snackbar.show();
            return valid;
        }

        if(switch_type_publication.isChecked()) {
            if (detailed_description==null){
                valid = false;
                Snackbar snackbar = Snackbar.make(spinner_percentageOff, getResources().getString(R.string.error_description_required), Snackbar.LENGTH_LONG);
                snackbar.show();
                return valid;
            }
            if (characteristics == null) {
                valid = false;
                Snackbar snackbar = Snackbar.make(spinner_percentageOff, getResources().getString(R.string.error_conditions_required_error), Snackbar.LENGTH_LONG);
                snackbar.show();
                return valid;
            }
        }

        return valid;
    }
    private String getImagesNamesArray() {
        Iterator it = image_list.entrySet().iterator();
        StringBuffer buffer = new StringBuffer();
        int iteration = 1;
        if (it.hasNext()) {
            do {
                Map.Entry pair = (Map.Entry) it.next();
                ImagePublication tempPub = (ImagePublication) pair.getValue();
                if (iteration==image_list.size())
                    buffer.append(tempPub.getName());
                else
                    buffer.append(tempPub.getName()+",");
                iteration++;
            } while (it.hasNext());
        }
        return buffer.toString();
    }

    private void initProcess(boolean flag){
        button_publish.setEnabled(flag?false:true);
        general_container.setVisibility(flag?View.GONE:View.VISIBLE);
        layout_loading.setVisibility(flag?View.VISIBLE:View.GONE);
    }
    private void publish(){
        String cover = text_cover.getText().toString().trim();
        String percentageOff = spinner_percentageOff.getSelectedItem().toString().trim();
        String availability = text_availability.getText().toString().trim();
        String regular_price = text_regular_price.getText().toString().trim();
        String offer_price = text_offer_price.getText().toString().trim();


        //create JSONArray to send name images
        String imagesNames = getImagesNamesArray();

        //createProgressDialog("Publicando...");
        initProcess(true);
        HashMap<String,String> params = new HashMap<>();
        //text
        params.put("method","publish");
        params.put("id_user", ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.localUserId));
        params.put("cover",cover);
        params.put("percentageOff",switch_type_publication.isChecked()?percentageOff:"");
        params.put("availability",switch_type_publication.isChecked()?availability:"1");
        params.put("regular_price",switch_type_publication.isChecked()?regular_price:"0");
        params.put("offer_price",offer_price);
        //selections
        params.put("idCategory",category!=null?category.getIdCategory():"0");
        params.put("idSubCategory",sub_category!=null?sub_category.getIdSubCategory():"0");
        params.put("idSubSubCategory",sub_sub_category!=null?sub_sub_category.getIdSubSubCategory():"0");
        Log.d(TAG, "IDS:" + ids_locations + " ids_states_publication:"+ids_states_publication.toString());
        params.put("idsLocation", ids_locations.toString());//mandar lista de direcciones, se debe permitir multi seleccion de ubicaciones
        params.put("ids_states_publication",ids_states_publication.toString());

        params.put("effectiveDate",date);
        params.put("detailed_description",switch_type_publication.isChecked()?detailed_description:"");
        params.put("outstanding",outstanding);
        params.put("characteristics",switch_type_publication.isChecked()?characteristics:"");
        params.put("imagesNames",imagesNames);
        params.put("idTown","");
        params.put("idState",ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.cityUser));
        params.put("idCountry",ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.countryUser));
        params.put("emergent",switch_type_publication.isChecked()? (switch_emergent.isChecked()?"1":"0"):"0");
        params.put("ads",switch_type_publication.isChecked()?"0":"1");//checked = es cupon, else = publicidad
        //type and shipping product
        params.put("product_virtual",check_virtual.isChecked()?"1":"0");
        params.put("shipping_on_store",check_shipping_on_store.isChecked()?"1":"0");
        params.put("shipping_options",check_shipping_options.isChecked()?"1":"0");//if checked then send list of shipping methods selected, disabled now
        params.put("weight","0");
        params.put("heigth","0");
        params.put("long","0");
        params.put("wide","0");
        //images upload after publication
        VolleyPostRequest(Constants.GET_PUBLICATIONS, params,METHOD_PUBLISH);
    }


    public void VolleyPostRequest(String url, final HashMap<String, String> args,final int callback){
        JSONObject jobject = new JSONObject(args);
        Log.d(TAG,"VolleyPostRequest Publish:" +  jobject.toString());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar la respuesta del servidor
                        if (callback == METHOD_PUBLISH)
                            processResponse(response);
                        else if(callback == METHOD_REMOVE_PUBLICATION)
                            processResponseRemove(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                        ShowConfirmations.showConfirmationMessage(getString(R.string.generic_server_timeout),getActivity());
                        initProcess(false);
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

    private void processResponse(JSONObject response){
        Log.d(TAG,"Publish:" +response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    JSONObject object = response.getJSONObject("result");
                    if (object.getString("status").equals("1")) {
                        GenericResponse result = gson.fromJson(object.toString(), GenericResponse.class);
                        Log.d(TAG,"response:"+result.toString());

                        onSuccess(object);
                    }
                    else{
                        onFailed(object);
                    }
                    break;
                case "2":
                    onFailed(response);

                    break;
                case "3":
                    onFailed(response);

                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onFailed(response);
        }
    }
    private void processResponseRemove(JSONObject response){
        Log.d(TAG,"Remove:" +response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    Log.d(TAG,"PUBLICACION RESETED  !!");
                    break;
                case "2":
                    Log.d(TAG,"NO SE ELIMINO LA PUBLICACION: !!");

                    break;

                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onFailed(response);
        }
    }
    private void onFailed(JSONObject object){
        try{
            Toast.makeText(getContext(), object.getString("message"), Toast.LENGTH_LONG).show();
            button_publish.setEnabled(true);
            layout_loading.setVisibility(View.INVISIBLE);
            general_container.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void onSuccess(JSONObject object) {
        id_new_publication = "";

        try {
            id_new_publication = object.getString("newIdPublication");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (id_new_publication!=null && id_new_publication.length()>0){//images loading
            UploadImage.uploadImagesPublication(getActivity(), id_new_publication , image_list,image_list.size());
            start();
        }


    }
    public void start() {
        if (timer != null) {
            timer.cancel();
        }
        startChekingUploading();
    }
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    public void startChekingUploading() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (UploadImage.ready==1 && UploadImage.error == 0) {
                            stop();
                            getActivity().setResult(Activity.RESULT_OK, null);
                            getActivity().finish();
                        }else if(UploadImage.ready==1 && UploadImage.error == 1){// hubo errores, reintentar...
                            initProcess(false);
                            stop();
                            confirmAction(UploadImage.getMapError().size()+" "+getResources().getString(R.string.prompt_retry_promt),getResources().getString(R.string.prompt_uploaderror_promt),getResources().getString(R.string.prompt_action_retry),getResources().getString(R.string.prompt_cancel),PublishFragment.ALERT_RETRY);
                        }

                    }
                });


            }

        }, 0, DURATION);
    }
    @Override
    public void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
    }

    private void createProgressDialog(String msg){
        progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        switch(requestCode) {
            case PICK_IMAGE_ID:
                bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data);

                if (resultCode == Activity.RESULT_OK) {
                    String path = ImagePicker.getRealPathFromURI(getActivity(), ImagePicker.getUriBitmapSelected());
                    Log.d(TAG,"RESULT_OK path:"+path + " image_list: " + image_list.size());
                    if (!image_list.containsKey(path)) {
                        image_list.put(path, new ImagePublication(bitmap, ImagePicker.getImageName(path), path));
                        createImageOnList(bitmap, path);
                    }else{
                        Snackbar snackbar = Snackbar.make(button_add_picture,getResources().getString(R.string.text_prompt_image_exists),Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
                break;
            case SHOW_IMAGE:
                if (resultCode == Activity.RESULT_OK) {//delete image, update list
                    String tag = data.getStringExtra(EXTRA_TAG);
                    final String path = data.getStringExtra(EXTRA_PATH);
                    final CardView card = (CardView) list_new_publish_images.findViewWithTag(tag);
                    card.animate()
                            .translationY(0)
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    image_list.remove(path);
                                    TextView label = (TextView) list_new_publish_images.findViewWithTag(getResources().getString(R.string.label));
                                    if (label != null) {
                                        if (image_list.size() > 0)
                                            label.setText(image_list.size() + "/5");
                                        else
                                            label.setText(getResources().getString(R.string.text_add_photos));
                                    }
                                    card.setVisibility(View.GONE);
                                    list_new_publish_images.removeView(card);

                                }
                            });

                }
                break;
            case SELECT_CATEGORY:
                if (resultCode == Activity.RESULT_OK){
                    category = (CategoryViewModel) data.getSerializableExtra(Constants.CATEGORY_SELECTED);
                    sub_category = (SubCategory) data.getSerializableExtra(Constants.SUBCATEGORY_SELECTED);
                    sub_sub_category = (SubSubCategory) data.getSerializableExtra(Constants.SUBSUBCATEGORY_SELECTED);
                    if (sub_sub_category!=null) {
                        TextView text_category = (TextView) layout_category.findViewById(R.id.text_category);
                        text_category.setText(sub_category.getSubcategory().concat("->").concat(sub_sub_category.getSubsubcategory()));
                    }
                    //Intent intent = new Intent(context, SelectSubCategoryActivity.class);
                    //intent.putExtra(Constants.CATEGORY_SELECTED, c.getIdCategory());
                    //startActivityForResult(intent, SELECT_SUBCATEGORY);

                }
                break;
            case SELECT_SUBCATEGORY:
                if (resultCode == Activity.RESULT_OK){
                    sub_category =(SubCategory) data.getSerializableExtra(Constants.SUBCATEGORY_SELECTED);
                    TextView category = (TextView)layout_category.findViewById(R.id.text_category);
                    category.setText(sub_category.getSubcategory());
                }
                break;
            case SELECT_LOCATION:
                if (resultCode == Activity.RESULT_OK){
                    int interation = 1;
                    addresses = (HashMap<String,Address>) data.getSerializableExtra(Constants.ADDRESS_SELECTED);
                    ids_locations = new StringBuffer();
                    ids_states_publication = new StringBuffer();
                    if(addresses!=null && addresses.size()>0) {
                        StringBuffer buffer = new StringBuffer();
                        Iterator it = addresses.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry e = (Map.Entry) it.next();
                            Address address = (Address)e.getValue();
                            buffer.append(address.getShortName());
                            ids_locations.append(address.getIdAddress());
                            ids_states_publication.append(address.getState());
                            if (interation != addresses.size()){
                                buffer.append(",");
                                ids_locations.append(",");
                                ids_states_publication.append(",");
                            }
                            interation++;
                        }
                        TextView text_location = (TextView) layout_location.findViewById(R.id.text_location);
                        text_location.setText(addresses.size()+ " direcciones agregadas: " + buffer.toString());
                    }
                    Log.d(TAG,"Direcciones agregadas: " + ids_locations.toString());
                }
                break;
            case SELECT_DETAILED_DESCRIPTION:
                if (resultCode == Activity.RESULT_OK){
                    detailed_description = data.getStringExtra(Constants.DESCRIPTION_SELECTED);
                    TextView text_detailed_description = (TextView)layout_detailed_description.findViewById(R.id.text_detailed_description);

                    if (detailed_description.length()>80)
                        text_detailed_description.setText(detailed_description.substring(0,80)+"...");
                    else
                        text_detailed_description.setText(detailed_description);
                }
                break;
            case SELECT_OUTSTANDING:
                if (resultCode == Activity.RESULT_OK){
                    outstanding = data.getStringExtra(Constants.OUTSTANDING_SELECTED);
                    TextView text_outstanding = (TextView)layout_outstanding.findViewById(R.id.text_outstanding);

                    if (outstanding.length()>80)
                        text_outstanding.setText(outstanding.substring(0,80)+"...");
                    else
                        text_outstanding.setText(outstanding);
                }
                break;
            case SELECT_CHARACTERISTICS:
                if (resultCode == Activity.RESULT_OK){
                    characteristics = data.getStringExtra(Constants.CHARACTERISTICS_SELECTED);
                    TextView text_characteristic = (TextView)layout_characteristics.findViewById(R.id.text_characteristic);

                    if (characteristics.length()>80)
                        text_characteristic.setText(characteristics.substring(0,80)+"...");
                    else
                        text_characteristic.setText(characteristics);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void createImageOnList(Bitmap bitmap,final String path ){

        final int count = image_list.size();
        //reset text label
        list_new_publish_images.removeView((TextView) list_new_publish_images.findViewWithTag("label"));
        //create CardView
        CardView card = new CardView(getActivity());
        LayoutParams params = new LayoutParams( LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT  );
        params.gravity = Gravity.CENTER;
        params.setMargins(8,8,8,8);
        card.setLayoutParams(params);
        card.setRadius(20);
        //create image
        ImageView image = new ImageView(getActivity());
        image.setImageBitmap(bitmap);

        float wh = getResources().getDimension(R.dimen.publish_item_image_size);
        LayoutParams lp = new LayoutParams((int)wh, (int)wh);
        lp.gravity = Gravity.CENTER;
        image.setLayoutParams(lp);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowImageActivity.class);
                intent.putExtra(EXTRA_PATH,path);
                intent.putExtra(EXTRA_TAG, "card_" + count);
                startActivityForResult(intent, SHOW_IMAGE);

            }
        });
        //textview at the end
        TextView text_num_photos = new TextView(context);
        text_num_photos.setText(count + "/5");
        text_num_photos.setTag(EXTRA_LABEL);
        text_num_photos.setTextColor(Color.parseColor(Constants.colorNegro));
        LayoutParams paramsLabel = new LayoutParams(      LayoutParams.WRAP_CONTENT,     LayoutParams.WRAP_CONTENT  );
        paramsLabel.gravity = Gravity.CENTER;
        paramsLabel.setMargins(16,8,8,16);
        text_num_photos.setLayoutParams(paramsLabel);
        card.setTag("card_" + count);
        card.addView(image);
        Log.d(TAG,"list_new_publish_images.addView:"+card.getTag());
        list_new_publish_images.addView(card);
        list_new_publish_images.addView(text_num_photos);

    }

    public void confirmAction(String message, String title, String positive, String negative,final int callback){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (callback == ALERT_DISCART) {
                    reset();
                }else if (callback == ALERT_RETRY){
                    initProcess(true);
                    HashMap map = UploadImage.getMapError();
                    if (map!=null) {
                        Log.d(TAG, "Reintentar con : " + map.size() + " imagenes");
                        UploadImage.error = 0;
                        UploadImage.ready = 0;
                        UploadImage.resetMapError();
                        UploadImage.uploadImagesPublication(getActivity(), id_new_publication, map, map.size());
                        start();
                    }else{Log.d(TAG," lista errores null--------------");}
                }

            }
        });

        alertDialogBuilder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(LocationsActivity.this, "You clicked yes button", Toast.LENGTH_LONG).show();

                if (callback == ALERT_RETRY){
                    deletePublication(id_new_publication);
                }

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void ShowNotification_Popup(final int type) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        final View mView = layoutInflaterAndroid.inflate((type == SHOW_NOTIFICATION_IMAGE)?R.layout.dialog_show_tip_image:R.layout.dialog_show_suscription_announcement, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(mView);

        //listeners
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.promt_button_show_tip_image), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        //action buttons
                        if (type == SHOW_NOTIFICATION_IMAGE) {
                            final CheckBox checkbox_show_tip_photo = (CheckBox) mView.findViewById(R.id.checkbox_show_tip_photo);
                            if (checkbox_show_tip_photo.isChecked())
                                ApplicationPreferences.saveLocalPreference(getContext(), Constants.showTipImage, String.valueOf(true));
                            pickImage();
                        }

                    }
                });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }


    private void deletePublication(String id_new_publication){
        HashMap<String,String> params = new HashMap<>();
        params.put("method","removePublication");
        params.put("idPublication", id_new_publication);
        VolleyPostRequest(Constants.REMOVE_PUBLICATION, params,METHOD_REMOVE_PUBLICATION);
    }

    public void reset(){
        //reset images list
        int total = list_new_publish_images.getChildCount();
        for (counterReset=0;counterReset<total;counterReset++) {
            View currentCustomView = list_new_publish_images.getChildAt(counterReset);
            if (currentCustomView != null) {
                Log.d(TAG, "list_new_publish_images.getChildAt(" + counterReset + "):" + currentCustomView.getTag());
                Object obj = currentCustomView.getTag();
                if (obj!=null) {
                    final String tag = (String) currentCustomView.getTag();
                    if (tag != null && !tag.equals(getResources().getString(R.string.label))) {
                        final CardView card = (CardView) list_new_publish_images.findViewWithTag(currentCustomView.getTag());
                        card.animate()
                                .translationY(0)
                                .alpha(0.0f)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        //image_list.remove(key);//key es el path de la imagen , no el tag del cardview
                                        //para eliminar uno por uno debe buscar por tag,
                                        // en este caso simplemente se reinicializara la lista.
                                        counterReset++;
                                        card.setVisibility(View.GONE);
                                        list_new_publish_images.removeView(card);

                                    }
                                });
                    }
                }
            }
        }
        //reinicializar lista de imagenes
        image_list = new HashMap<>();
        TextView label = (TextView) list_new_publish_images.findViewWithTag(getResources().getString(R.string.label));
        if (label != null){
            label.setText(getResources().getString(R.string.text_add_photos));
        }
        //reset selections
        category = null;
        sub_category = null;
        sub_sub_category = null;
        addresses  = null;
        ids_locations  = null;
        detailed_description = "";
        outstanding = "";
        characteristics = "";
        date = "";
        //reset fieldTexts
        text_cover.setText("");
        spinner_percentageOff.setSelection(0);
        text_availability.setText("");
        text_regular_price.setText("");
        text_offer_price.setText("");
        text_effective_date.setText("...");
        //reset TextViews
        TextView category = (TextView)layout_category.findViewById(R.id.text_category);
        category.setText(getString(R.string.text_promt_select_category));

        TextView text_location = (TextView) layout_location.findViewById(R.id.text_location);
        text_location.setText("");

        TextView text_detailed_description = (TextView)layout_detailed_description.findViewById(R.id.text_detailed_description);
        text_detailed_description.setText("");

        TextView text_outstanding = (TextView)layout_outstanding.findViewById(R.id.text_outstanding);
        text_outstanding.setText("");

        TextView text_characteristic = (TextView)layout_characteristics.findViewById(R.id.text_characteristic);
        text_characteristic.setText("");
        //reset images upload errors
        UploadImage.error = 0;
        UploadImage.ready = 0;
        UploadImage.resetMapError();
        //type and shipping options
        check_standard.setChecked(true);
        check_virtual.setChecked(false);
        text_product_type_info.setText(R.string.text_product_type_standard_info);
        text_weight.setText("");
        text_heigth.setText("");
        text_long.setText("");
        text_wide.setText("");
        if (switch_type_publication.isChecked()) {
            enableShippingProductStandard(true);
        }else{
            layout_shipping_type.setVisibility(View.GONE);
            layout_product_type.setVisibility(View.GONE);
        }
    }

    public void actualizarFecha(int ano, int mes, int dia) {
        // Setear en el textview la fecha
        TextView text_effective_date = (TextView)layout_effective_date.findViewById(R.id.text_effective_date);
        text_effective_date.setText(ano + "-" + (mes + 1) + "-" + dia);
        date = ano + "-" + (mes + 1) + "-" + dia;
    }

    private final TextWatcher changePrices = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            calculate_price();
        }

        public void afterTextChanged(Editable s) {

        }
    };
    private void calculate_price(){
        String options[] = getResources().getStringArray(R.array.percentages);
        String regular_price = text_regular_price.getText().toString().trim();
        String percentage_selected = spinner_percentageOff.getSelectedItem().toString();
        if (!percentage_selected.equals(options[0]) && regular_price.length()>0) {
            Log.d(TAG, "calcular precios");
            Float regular = Float.parseFloat(regular_price);
            Float percentage = Float.parseFloat(percentage_selected);
            Float price_offer_calculated = (regular * (percentage/100));
            Integer final_price = Math.round(regular - price_offer_calculated);
            text_offer_price.setText(String.valueOf(final_price));
        }else{text_offer_price.setText("");}
    }
}
