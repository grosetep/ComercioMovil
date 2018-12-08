package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.ImageSliderPublication;
import estrategiamovil.comerciomovil.modelo.PublicationUser;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.ui.fragments.DatePickerFragment;
import estrategiamovil.comerciomovil.ui.fragments.EditImagesFragment;
import estrategiamovil.comerciomovil.ui.fragments.ManagePublicationsFragment;
import estrategiamovil.comerciomovil.ui.fragments.PublishFragment;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class EditPublicationActivity extends AppCompatActivity implements DatePickerFragment.OnDateSelectedListener {
    private static final String TAG = EditPublicationActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private TextView text_detailed_desc;
    private TextView text_edit_outstanding;
    private TextView text_edit_characteristics;
    private TextView text_edit_date_limit;
    private EditText text_edit_stock;

    private ImageView button_edit_detailed_desc;
    private ImageView button_edit_outstanding;
    private ImageView button_edit_characteristics;
    private ImageView button_edit_date_limit;
    private ImageView button_edit_stock;
    private ImageView button_edit_price;
    private ImageView image_card_cover;
    private ImageView image_edit;

    private LinearLayout layout_large_cover;
    private LinearLayout layout_conditions;
    private LinearLayout layout_stock;
    private LinearLayout layout_spinner;


    private Spinner spinner_percentageOff;
    private MaterialEditText text_regular_price;
    private MaterialEditText text_offer_price;

    private String MOD_DESCRIPTION = "edit_description";
    private String MOD_OUTSTANDING = "edit_outstanding";
    private String MOD_CHARACTERISTICS = "edit_characteristics";
    private String MOD_DATE_LIMIT = "edit_date_limit";
    private String MOD_STOCK = "edit_stock";
    private String MOD_PRICES = "edit_prices";
    private final int UPDATE_IMAGES = 10;

    public static String PARAM_USER_PUBLICATION = "user_publication";
    private PublicationUser publication=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_publication);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        publication = (PublicationUser) i.getSerializableExtra(PARAM_USER_PUBLICATION);
        initGUI();

        if (publication!=null){
            //assign values
            text_detailed_desc.setText(publication.getDetailedDescription());
            text_edit_outstanding.setText(publication.getOutstanding());
            text_edit_characteristics.setText(publication.getCharacteristics());
            int isAd = Integer.parseInt(publication.getIsAds()!=null?publication.getIsAds():"0");
            if(isAd==1) {//is ads
                layout_large_cover.setVisibility(View.GONE);
                layout_conditions.setVisibility(View.GONE);
                layout_stock.setVisibility(View.GONE);
                text_regular_price.setVisibility(View.GONE);
                layout_spinner.setVisibility(View.GONE);
            }
            text_edit_date_limit.setText(publication.getDateLimit());
            text_edit_stock.setText(publication.getAvailability());
            text_regular_price.setText(publication.getRegularPrice());
            text_offer_price.setText(publication.getOfferPrice());
            if (isAd == 0) {
                setSpinnerValue(publication.getPercentageOff());
            }
            //load images
            Glide.with(image_card_cover.getContext())
                    .load(publication.getPath()+publication.getImageName())
                    .centerCrop()
                    .into(image_card_cover);

            assignActions();
        }

    }
    private void setSpinnerValue(String option_selected){
        String options[] = getResources().getStringArray(R.array.percentages);
        for (int index = 0; index < options.length; index++) {
            if (options[index].equals(option_selected)) {
                spinner_percentageOff.setSelection(index);
            }
        }

    }
    private void assignActions(){
        //edit images button
        image_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, EditImagesActivity.class);
                intent.putExtra(DetailPublicationActivity.EXTRA_PUBLICACION, publication!=null?publication.getIdPublication():"0");
                startActivityForResult(intent,UPDATE_IMAGES);
            }
        });


        if (publication.getIsAds().equals("0")) {//cupon
            button_edit_detailed_desc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SelectDetailedDescriptionActivity.class);
                    intent.putExtra(Constants.DESCRIPTION_SELECTED, text_detailed_desc.getText().toString().trim());
                    startActivityForResult(intent, PublishFragment.SELECT_DETAILED_DESCRIPTION);
                }
            });


            button_edit_characteristics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SelectCharacteristicsActivity.class);
                    intent.putExtra(Constants.CHARACTERISTICS_SELECTED, text_edit_characteristics.getText().toString().trim());
                    startActivityForResult(intent, PublishFragment.SELECT_CHARACTERISTICS);
                }
            });
            spinner_percentageOff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    calculate_price();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        button_edit_outstanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectOutstandingActivity.class);
                intent.putExtra(Constants.OUTSTANDING_SELECTED, text_edit_outstanding.getText().toString().trim());
                startActivityForResult(intent, PublishFragment.SELECT_OUTSTANDING);
            }
        });
        button_edit_date_limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment picker = new DatePickerFragment();
                picker.show(getSupportFragmentManager(), "datePicker");
            }
        });
        text_edit_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableForEdition(true,v);
            }
        });
        button_edit_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                if (text_edit_stock.getText().toString().trim().length() > 0) {
                    text_edit_stock.setError(null);
                } else {
                    text_edit_stock.setError(getString(R.string.error_field_required));
                    valid = false;
                }
                if (valid) {
                    attemptUpdate(MOD_STOCK, text_edit_stock.getText().toString().trim(), text_edit_stock);
                }
            }
        });
        text_regular_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableForEdition(true,v);
    }
        });
        text_offer_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!publication.getIsAds().equals(String.valueOf(Constants.cero))) {
                    enableForEdition(true, v);//editable only for ads
                }
            }
        });
        button_edit_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                String percentageOff = spinner_percentageOff.getSelectedItem().toString().trim();
                String regular_price = text_regular_price.getText().toString().trim();
                String offer_price = text_offer_price.getText().toString().trim();
                String options[] = getResources().getStringArray(R.array.percentages);

                if (publication.getIsAds().equals("0")) {//cupon
                    if (percentageOff.equals(options[0])) {
                        Snackbar snackbar = Snackbar.make(spinner_percentageOff, getResources().getString(R.string.error_percentageOff_required), Snackbar.LENGTH_LONG);
                        snackbar.show();
                        valid = false;
                    }
                    if (regular_price.isEmpty()) {
                        text_regular_price.setError("Campo obligatorio");
                        valid = false;
                    } else {
                        text_regular_price.setError(null);
                    }

    }
                if (offer_price.isEmpty()) {
                    text_offer_price.setError("Campo obligatorio");
                    valid = false;
                } else {
                    text_offer_price.setError(null);
                }
                if (valid){
                    attemptUpdatePrices(MOD_PRICES);
                }
            }
        });
    }

    private void enableForEdition(boolean flag, View v) {

        if (v != null) {
            if (v instanceof EditText) {
                EditText txt = (EditText) v;
                txt.setCursorVisible(flag ? true : false);
                txt.setFocusable(flag ? true : false);
                txt.setFocusableInTouchMode(flag ? true : false);
                txt.requestFocus();
            }else if(v instanceof MaterialEditText){
                MaterialEditText txt2 = (MaterialEditText) v;
                txt2.setCursorVisible(flag ? true : false);
                txt2.setFocusable(flag ? true : false);
                txt2.setFocusableInTouchMode(flag ? true : false);
                txt2.requestFocus();
            }
        }
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
    private void initGUI(){
        text_detailed_desc = (TextView) findViewById(R.id.text_detailed_desc);
        text_edit_outstanding = (TextView) findViewById(R.id.text_edit_outstanding);
        text_edit_characteristics = (TextView) findViewById(R.id.text_edit_characteristics);
        text_edit_date_limit = (TextView) findViewById(R.id.text_edit_date_limit);
        text_edit_date_limit.clearFocus();
        text_edit_stock = (EditText) findViewById(R.id.text_edit_stock);
        button_edit_detailed_desc = (ImageView) findViewById(R.id.button_edit_detailed_desc);
        button_edit_outstanding = (ImageView) findViewById(R.id.button_edit_outstanding);
        button_edit_characteristics = (ImageView) findViewById(R.id.button_edit_characteristics);
        button_edit_date_limit = (ImageView) findViewById(R.id.button_edit_date_limit);
        button_edit_stock = (ImageView) findViewById(R.id.button_edit_stock);
        button_edit_price = (ImageView) findViewById(R.id.button_edit_price);
        layout_large_cover = (LinearLayout) findViewById(R.id.layout_large_cover);
        layout_conditions = (LinearLayout) findViewById(R.id.layout_conditions);
        layout_stock = (LinearLayout) findViewById(R.id.layout_stock);
        layout_spinner = (LinearLayout) findViewById(R.id.layout_spinner);
        spinner_percentageOff = (Spinner) findViewById(R.id.spinner_percentageOff);
        text_regular_price = (MaterialEditText) findViewById(R.id.text_regular_price);
        text_regular_price.addTextChangedListener(changePrices);
        text_offer_price = (MaterialEditText) findViewById(R.id.text_offer_price);
        image_card_cover = (ImageView) findViewById(R.id.image_card_cover);
        image_edit = (ImageView) findViewById(R.id.image_edit);
    }

    private void initProcess(boolean flag){
        if (flag)
            createProgressDialog(getString(R.string.promtp_payment_processing));
        else
            closeProgressDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createProgressDialog(String message){
        progressDialog = new ProgressDialog(EditPublicationActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {

            case PublishFragment.SELECT_DETAILED_DESCRIPTION:
                if (resultCode == Activity.RESULT_OK){
                    String detailed_description = data.getStringExtra(Constants.DESCRIPTION_SELECTED);
                    attemptUpdate(MOD_DESCRIPTION, detailed_description, text_detailed_desc);
                }
                break;
            case PublishFragment.SELECT_OUTSTANDING:
                if (resultCode == Activity.RESULT_OK){
                    String outstanding = data.getStringExtra(Constants.OUTSTANDING_SELECTED);
                    attemptUpdate(MOD_OUTSTANDING, outstanding, text_edit_outstanding);
                }
                break;
            case PublishFragment.SELECT_CHARACTERISTICS:
                if (resultCode == Activity.RESULT_OK){
                    String characteristics = data.getStringExtra(Constants.CHARACTERISTICS_SELECTED);
                    attemptUpdate(MOD_CHARACTERISTICS, characteristics, text_edit_characteristics);
                }
                break;
            case UPDATE_IMAGES:
                if (resultCode == Activity.RESULT_OK) {
                    ShowConfirmations.showConfirmationMessage(getString(R.string.text_prompt_profile_updated), this);
                    ManagePublicationsFragment.setStatusListChanged(true);
                    //reload images
                    ImageSliderPublication image =(ImageSliderPublication)data.getSerializableExtra(EditImagesFragment.MAIN_IMAGE_SELECTED);
                    if (image!=null){
                        if (image.getResource().equals(Constants.resource_remote)) {
                            Glide.with(EditPublicationActivity.this)
                                    .load(image.getPath() + image.getImageName())
                                    .centerCrop()
                                    .into(image_card_cover);

                        }else{
                            File file = new File(image.getPath());
                            if(file.exists()) {//load file from local
                                Log.d(TAG, "load file from local");
                                Glide.with(EditPublicationActivity.this)
                                        .load(new File(image.getPath())).asBitmap()
                                        .error(R.drawable.ic_account_circle)
                                        .into(image_card_cover);
                            }
                        }
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void attemptUpdate(String type_modification, String data, View tView) {
        String idUser = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.localUserId);
        initProcess(true);
        HashMap<String,String> params = new HashMap<>();
        params.put("method","editPublication");
        params.put("id_publication",publication.getIdPublication());
        params.put("id_user",idUser);
        params.put("data",data);
        params.put("price_regular", "");
        params.put("price_offer", "");
        params.put("percentageOff", "");
        params.put("type_modification", type_modification);
        VolleyPostRequest(Constants.GET_PUBLICATIONS, params, tView);
    }


    private void attemptUpdatePrices(String type_modification) {
        String idUser = ApplicationPreferences.getLocalStringPreference(getApplicationContext(), Constants.localUserId);
        String percentageOff = spinner_percentageOff.getSelectedItem().toString().trim();
        initProcess(true);
        HashMap<String, String> params = new HashMap<>();
        params.put("method", "editPublication");
        params.put("id_publication", publication.getIdPublication());
        params.put("id_user", idUser);
        params.put("data", "");
        params.put("price_regular", text_regular_price.getText().toString());
        params.put("price_offer", text_offer_price.getText().toString());
        params.put("percentageOff", percentageOff);
        params.put("type_modification", type_modification);
        VolleyPostRequest(Constants.GET_PUBLICATIONS, params, text_regular_price);
    }
    public void VolleyPostRequest(String url, final HashMap<String, String> args, final View tView) {
        JSONObject jobject = new JSONObject(args);
        Log.d(TAG,"VolleyPostRequest:" + jobject.toString());

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                processResponseUpdate(response, args, tView);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                                ShowConfirmations.showConfirmationMessage(getString(R.string.generic_error),EditPublicationActivity.this);
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
                }
        );


    }

    private void processResponseUpdate(JSONObject response, HashMap<String, String> data, View tView) {
        Log.d(TAG,"Result:" +response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    String object = response.getString("result");
                    initProcess(false);
                    ShowConfirmations.showConfirmationMessage(getString(R.string.text_prompt_profile_updated),this);
                    ManagePublicationsFragment.setStatusListChanged(true);
                    //assign updated value to view
                    if (!data.get("type_modification").equals(MOD_PRICES) && !data.get("type_modification").equals(MOD_STOCK)) {
                    if (tView instanceof TextView) {
                        TextView t = (TextView) tView;
                        t.setText(data.get("data"));
                    } else if (tView instanceof EditText) {
                        EditText t = (EditText) tView;
                        t.setText(data.get("data"));
                    }
                    }
                    else if (data.get("type_modification").equals(MOD_STOCK)){
                        enableForEdition(false,text_edit_stock);
                    }
                    else if (data.get("type_modification").equals(MOD_PRICES)){
                        text_regular_price.setText(data.get("price_regular"));
                        text_offer_price.setText(data.get("price_offer"));
                        setSpinnerValue(data.get("percentageOff"));
                        enableForEdition(false,text_regular_price);
                        enableForEdition(false,text_offer_price);
                    }
                    break;
                case "2":
                    String object_error = response.getString("message");
                    ShowConfirmations.showConfirmationMessage(object_error,this);
                    initProcess(false);
                    break;

                default:
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            initProcess(false);
            ShowConfirmations.showConfirmationMessage(getString(R.string.generic_error), this);
        } catch (Exception ex){
            ex.printStackTrace();
            initProcess(false);
            ShowConfirmations.showConfirmationMessage(getString(R.string.generic_error),this);
        }
    }

    @Override
    public void onDateSelected(int year, int month, int day) {
        actualizarFecha(year, month, day);
    }

    private Calendar createDateCalendar(int year, int month, int day) {
        final Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        return c;
    }

    public void actualizarFecha(int ano, int mes, int dia) {
        // Setear en el textview la fecha
        String date = ano + "-" + (mes + 1) + "-" + dia;
        Log.d(TAG, "date selected:" + date);
        Calendar new_date = createDateCalendar(ano, mes, dia);
        attemptUpdate(MOD_DATE_LIMIT, date, text_edit_date_limit);
    }
}
