package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.ImageSliderPublication;
import estrategiamovil.comerciomovil.modelo.PurchaseItem;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ImagePicker;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.tools.UploadImage;
import estrategiamovil.comerciomovil.ui.fragments.PublishFragment;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class VoucherActivity extends AppCompatActivity {
    public static final String PURCHASE = "purchase";
    private static final String TAG = VoucherActivity.class.getSimpleName();
    private CardView card_new_photo;
    private CardView card_voucher;
    private ImageView photo;
    private AppCompatCheckBox checkbox_image;
    private PurchaseItem purchase;
    private String nameImage;
    private LinearLayout layout_change_images;
    private AppCompatButton button_continue;
    private Bitmap bitmap;
    private Timer timer = null;
    private ProgressDialog progressDialog;
    private boolean modifications_done = false;
    private MenuItem edit_menu;
    private MenuItem remove_menu;
    private boolean remove_mode = false;
    private boolean local_resource_selected = false;
    private static final int PICK_IMAGE_ID = 1;
    private String nameImageToServer;
    private ImageSliderPublication[] gallery;
    private ImageSliderPublication image_gallery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_voucher);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initGUI();
        assignActions();
        Intent i = getIntent();
        purchase =(PurchaseItem) i.getSerializableExtra(PURCHASE);
        if (purchase!=null){
            if (purchase.getIdVoucher()!=null && purchase.getRouteVoucher()!=null && purchase.getImageVoucher()!=null){
                card_new_photo.setVisibility(View.GONE);
                card_voucher.setVisibility(View.VISIBLE);
                nameImage = purchase.getImageVoucher();
                Glide.with(VoucherActivity.this)
                        .load(purchase.getRouteVoucher()+purchase.getImageVoucher())
                        .error(R.drawable.ic_account_circle)
                        .into(photo);
            }else{
                card_new_photo.setVisibility(View.VISIBLE);
                card_voucher.setVisibility(View.GONE);
            }
        }else{
            card_new_photo.setVisibility(View.VISIBLE);
            card_voucher.setVisibility(View.GONE);
        }
    }
    private void initGUI(){
        card_new_photo = (CardView) findViewById(R.id.card_view);
        card_voucher = (CardView) findViewById(R.id.card_voucher);
        photo = (ImageView) findViewById(R.id.photo);
        checkbox_image = (AppCompatCheckBox) findViewById(R.id.checkbox_image);
        checkbox_image.setVisibility(View.GONE);
        layout_change_images = (LinearLayout) findViewById(R.id.layout_change_images);
        layout_change_images.setVisibility(View.GONE);
        button_continue = (AppCompatButton) findViewById(R.id.button_continue);
    }
    private void assignActions(){
        card_new_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //upload image
                initProcess(true);
                updateImageProfile();
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remove_mode){
                    if (checkbox_image.isChecked())
                        checkbox_image.setChecked(false);
                    else
                        checkbox_image.setChecked(true);
                }else {
                    startGalleryActivity();
                }
            }
        });
    }
    public void startGalleryActivity(){
        Intent intent = new Intent(VoucherActivity.this, GalleryActivity.class);
        Bundle bundle = new Bundle();
        gallery = new ImageSliderPublication[1];
        image_gallery = new ImageSliderPublication();

        if (local_resource_selected){//recurso local
            Log.d(TAG,"RECURSO LOCAL................path:"+ImagePicker.getImageName(nameImage)+"->"+ImagePicker.getImagePathLocal(nameImage));
            image_gallery.setResource(Constants.resource_local);
            image_gallery.setImageName(ImagePicker.getImageName(nameImage));
            image_gallery.setPath(nameImage);
        }else{//recurso remoto o no ha habido cambios
            Log.d(TAG,"RECURSO REMOTO--------------path:"+purchase.getRouteVoucher()+"->"+purchase.getImageVoucher());
            image_gallery.setResource(purchase.getIdVoucher() != null ? Constants.resource_remote : Constants.resource_local);
            image_gallery.setImageName(purchase.getImageVoucher());
            image_gallery.setPath(purchase.getRouteVoucher());
        }

        image_gallery.setEnableDeletion(String.valueOf(false));
        gallery[0]=image_gallery;

        bundle.putSerializable(Constants.GALLERY,gallery);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    public void pickImage(){
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(VoucherActivity.this,getString(R.string.pick_image_voucher));
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        switch(requestCode) {
            case PICK_IMAGE_ID:
                bitmap = ImagePicker.getImageFromResult(VoucherActivity.this, resultCode, data);
                if (resultCode == Activity.RESULT_OK) {
                    bitmap = ImagePicker.getImageFromResult(VoucherActivity.this, resultCode, data);
                    photo.setImageBitmap(bitmap);
                    local_resource_selected = true;
                    nameImage = ImagePicker.getRealPathFromURI(photo.getContext(), ImagePicker.getUriBitmapSelected());
                    layout_change_images.setVisibility(View.VISIBLE);
                    showPanelImage(true);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
    private void showPanelImage(boolean flag){
        card_new_photo.setVisibility(flag?View.GONE:View.VISIBLE);
        card_voucher.setVisibility(flag?View.VISIBLE:View.GONE);
    }
    private void updateImageProfile(){

        nameImageToServer = purchase.getIdPurchase()+ "_" + purchase.getCaptureLine()+"_"+ImagePicker.getImageName(nameImage);
        Log.d(TAG,"nombre final imagen: " + nameImageToServer);
        if (bitmap!=null) { //usuario selecciono avatar
            Log.d(TAG,"bitmap: " + bitmap.getByteCount());
            UploadImage.uploadVoucherImage(VoucherActivity.this, nameImageToServer , bitmap);
            start();
        }
    }
    public void start() {
        if (timer != null) {
            timer.cancel();
        }
        Log.d(TAG,"start");
        startChekingUploading();
    }
    public void stop() {
        Log.d(TAG,"stop..");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    public void startChekingUploading() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {

                runOnUiThread(new Runnable() {
                    public void run() {
                        if (UploadImage.ready==1 && UploadImage.error == 0) {
                            stop();
                            //update database
                            updateImageProfileRoute();

                        }else if(UploadImage.ready==1 && UploadImage.error == 1){// hubo errores, reintentar...
                            initProcess(false);
                            stop();
                            ShowConfirmations.showConfirmationMessage(getString(R.string.text_prompt_image_error),VoucherActivity.this);
                            //carga imagen anterior
                            Glide.with(VoucherActivity.this)
                                    .load(purchase.getRouteVoucher()+purchase.getImageVoucher())
                                    .error(R.drawable.ic_account_circle)
                                    .into(photo);
                        }

                    }
                });


            }

        }, 0, PublishFragment.DURATION);
    }
    private void updateImageProfileRoute(){
        HashMap<String,String> params = new HashMap<>();
        params.put("method","updateImageVoucher");
        params.put("id_purchase",purchase.getIdPurchase());
        params.put("id_voucher",(purchase.getIdVoucher()!=null)?purchase.getIdVoucher():"");
        params.put("name_image",ImagePicker.getImageName(nameImage));
        params.put("name_image_old",purchase.getImageVoucher());
        VolleyPostRequest(Constants.GET_PURCHASES,params);
    }
    public void VolleyPostRequest(String url, final HashMap<String, String> args){
        JSONObject jobject = new JSONObject(args);
        Log.d(TAG,"VolleyPostRequest:"+ jobject.toString());
        JsonObjectRequest request =new JsonObjectRequest(
                Request.Method.POST,
                url,
                jobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            processResponseRoute(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                        ShowConfirmations.showConfirmationMessage(getString(R.string.generic_error),VoucherActivity.this);
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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);


    }

    private void processResponseRoute(JSONObject response){
        Log.d(TAG,"Result:" +response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    JSONObject object = response.getJSONObject("result");
                    Log.d(TAG," object result:"+object.toString());
                    if (object.get("status").toString().equals("1")) {
                        initProcess(false);
                        setModifications_done(true);
                        layout_change_images.setVisibility(View.GONE);
                        ShowConfirmations.showConfirmationMessage(getString(R.string.text_prompt_profile_updated), VoucherActivity.this);
                    }else {
                        initProcess(false);
                        setModifications_done(false);
                        ShowConfirmations.showConfirmationMessage(getString(R.string.text_prompt_image_error), VoucherActivity.this);
                    }
                    break;
                case "2":
                    String object_error = response.getString("message");
                    ShowConfirmations.showConfirmationMessage(object_error,this);
                    initProcess(false);
                    break;

                default: initProcess(false);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            initProcess(false);
            ShowConfirmations.showConfirmationMessage(getString(R.string.generic_error),this);
        }
    }
    private void initProcess(boolean flag){
        if (flag)
            createProgressDialog(getString(R.string.promtp_payment_processing));
        else
            closeProgressDialog();
    }
    private void createProgressDialog(String message){
        progressDialog = new ProgressDialog(VoucherActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();
    }

    public boolean getModifications_done() {
        return modifications_done;
    }

    public void setModifications_done(boolean modifications_done) {
        this.modifications_done = modifications_done;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        this.edit_menu = menu.findItem(R.id.action_edit_images);
        this.remove_menu = menu.findItem(R.id.action_remove_images);
        if (remove_mode) {
            edit_menu.setVisible(false);
            remove_menu.setVisible(true);
        } else {
            edit_menu.setVisible(true);
            remove_menu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_images, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:Log.d(TAG,"HOME------------remove_mode:"+remove_mode);
                if (!remove_mode) {Log.d(TAG,"CASO 1");
                    if (!getModifications_done()) {Log.d(TAG,"CASO 1.1");
                        finish();
                    }else{Log.d(TAG,"CASO 1.2");
                        setResult(Activity.RESULT_OK, null);
                        finish();
                    }
                } else {Log.d(TAG,"CASO 2");
                    remove_mode = false;
                    enableEditionControls(remove_mode);
                    addNewPhotoCheck();

                }
                break;
            case R.id.action_edit_images:
                remove_mode = true;
                enableEditionControls(remove_mode);
                return true;
            case R.id.action_remove_images:
                remove_mode = false;
                local_resource_selected = false;
                removeAndUpdatePhoto();
                enableEditionControls(remove_mode);
                addNewPhotoCheck();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void addNewPhotoCheck(){//show or hide button
        if (remove_mode) {
            layout_change_images.setVisibility(View.INVISIBLE);
        }else{
            if (getModifications_done()){
                layout_change_images.setVisibility(View.VISIBLE);
            }else{layout_change_images.setVisibility(View.INVISIBLE);}
        }

    }
    private void removeAndUpdatePhoto(){
        bitmap = null;
        nameImage = "";
        showPanelImage(false);
    }
    private void enableEditionControls(boolean remove_mode){
        edit_menu.setVisible((remove_mode)?false:true);
        remove_menu.setVisible((remove_mode)?true:false);
        checkbox_image.setVisibility((remove_mode)?View.VISIBLE:View.GONE);
        layout_change_images.setVisibility(remove_mode?View.INVISIBLE:View.VISIBLE);
        card_new_photo.setVisibility(remove_mode?View.GONE:(nameImage!=null && !nameImage.isEmpty())?View.GONE:View.VISIBLE);
        card_voucher.setVisibility(remove_mode?View.VISIBLE:(nameImage!=null && !nameImage.isEmpty())?View.VISIBLE:View.GONE);
    }
}
