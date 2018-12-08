package estrategiamovil.comerciomovil.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.LoginResponse;
import estrategiamovil.comerciomovil.modelo.ProfileUser;
import estrategiamovil.comerciomovil.modelo.PublicationCardViewModel;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ImagePicker;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.tools.UploadImage;
import estrategiamovil.comerciomovil.tools.UserLocalProfile;
import estrategiamovil.comerciomovil.ui.fragments.ManagePublicationsFragment;
import estrategiamovil.comerciomovil.ui.fragments.PublishFragment;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private int user_modifications_done = 0;
    private static final int PICK_IMAGE_ID = 2;
    private static final int METHOD_UPDATE_USER = 3;
    private static final int METHOD_UPDATE_IMAGE = 4;
    private static final int METHOD_GET_INFO = 5;
    private static final int METHOD_CHANGE_PASSORD = 6;
    public static final String GET_INFO_BUSINESS = "get_info_business";
    private ProgressDialog progressDialog;
    private ProgressBar loading_profile;
    private NestedScrollView profile_container;
    private CircleImageView header_imageview;
    private ImageButton button_add_photo;
    private TextView text_email_view;
    private TextView text_name_view;

    private MaterialEditText text_name_profile;
    private MaterialEditText text_first_profile;
    private MaterialEditText text_last_profile;
    private MaterialEditText text_name_business;
    private MaterialEditText text_business_phone;
    private MaterialEditText text_web_site;
    private TextView text_business_info;

    private ImageView button_edit_name;
    private ImageView button_done_name;
    private ImageView button_edit_first;
    private ImageView button_done_first;
    private ImageView button_edit_last;
    private ImageView button_done_last;
    private ImageView button_edit_business_name;
    private ImageView button_done_business_name;
    private ImageView button_edit_web;
    private ImageView button_done_web;
    private ImageView button_edit_phone;
    private ImageView button_done_phone;
    private ImageView button_edit_info;
    private LinearLayout layout_change_password;


    private ProfileUser profile;
    private Timer timer = null;
    private Bitmap bitmap;
    private String nameImage;
    private LinearLayout detail_form;

    private final String MODIFY_NAME = "modify_name";
    private final String MODIFY_FIRST = "modify_first";
    private final String MODIFY_LAST = "modify_last";

    private final String MODIFY_NAME_BUSINESS = "modify_name_business";
    private final String MODIFY_WEB = "modify_web";
    private final String MODIFY_PHONE = "modify_phone";
    private final String MODIFY_INFO = "modify_info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initGUI();
        String idUser = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.localUserId);
        if (idUser==null || idUser.equals("")){
            finish();
            Intent i = new Intent(getBaseContext(),LoginActivity.class);
            startActivity(i);
        }else {
            assignActions();
            loading_profile.setVisibility(View.VISIBLE);
            loadInfoUser();
        }
    }
    private void assignActions(){
        header_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(ProfileActivity.this,getString(R.string.pick_image_intent_text));
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });
        button_edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_name_profile.setEnabled(true);
                text_name_profile.requestFocus();
            }
        });
        button_edit_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_first_profile.setEnabled(true);
                text_first_profile.requestFocus();
            }
        });
        button_edit_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_last_profile.setEnabled(true);
                text_last_profile.requestFocus();
            }
        });
        button_edit_business_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_name_business.setEnabled(true);
                text_name_business.requestFocus();
            }
        });
        button_edit_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_web_site.setEnabled(true);
                text_web_site.requestFocus();
            }
        });
        button_edit_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_business_phone.setEnabled(true);
                text_business_phone.requestFocus();
            }
        });
        button_edit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GetInfoBusinessActivity.class);
                intent.putExtra(GET_INFO_BUSINESS,text_business_info.getText().toString().trim());
                startActivityForResult(intent, METHOD_GET_INFO);
            }
        });

        button_done_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value=text_name_profile.getText().toString().trim();
                if (!getProfile().getName().equals(value) && value.length()>0){
                    attemptUpdate(MODIFY_NAME);
                }else if(getProfile().getName().equals(value)){
                    text_name_profile.setEnabled(false);
                }
            }
        });
        button_done_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value=text_first_profile.getText().toString().trim();
                if (!getProfile().getFirst().equals(value) && value.length()>0){
                    attemptUpdate(MODIFY_FIRST);
                }else if(getProfile().getFirst().equals(value)){
                    text_first_profile.setEnabled(false);
                }
            }
        });
        button_done_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value=text_last_profile.getText().toString().trim();
                if (!getProfile().getLast().equals(value) && value.length()>0){
                    Log.d(TAG,"UPDATE LAST");
                    attemptUpdate(MODIFY_LAST);
                }else if(getProfile().getLast().equals(value)){
                    text_last_profile.setEnabled(false);
                }
            }
        });
        button_done_business_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value=text_name_business.getText().toString().trim();
                if (!getProfile().getCompany().equals(value) && value.length()>0){
                    Log.d(TAG,"UPDATE COMPANY");
                    attemptUpdate(MODIFY_NAME_BUSINESS);
                }else if(getProfile().getCompany().equals(value)){
                    text_name_business.setEnabled(false);
                }
            }
        });
        button_done_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_web_site.setError(null);
                String value = text_web_site.getText().toString().trim();
                String URL_REGEX = "^(https?:\\/\\/)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?$";
                String URL_REGEX_YOUTUBE = "^(https?:\\/\\/)?(www.)?(m.)?youtu(\\.be|be\\.com)?\\/(watch\\?v=)?([\\w\\d_-]{11})$";
                Pattern p = Pattern.compile(URL_REGEX);
                Pattern p_youtube = Pattern.compile(URL_REGEX_YOUTUBE);
                Matcher m = p.matcher(value);
                Matcher m_youtube = p_youtube.matcher(value);

                if(!m.find() && !m_youtube.find()) {
                    text_web_site.setError("Dirección inválida");
                } else {
                    Log.d(TAG,"URL ok");
                    if (!getProfile().getWebsite().equals(value) && value.length()>0){
                        Log.d(TAG,"UPDATE WEB");
                        attemptUpdate(MODIFY_WEB);
                    }else if(getProfile().getWebsite().equals(value)){
                        text_web_site.setEnabled(false);
                    }
                }
            }
        });
        button_done_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = text_business_phone.getText().toString().trim();
                if (!getProfile().getPhone().equals(value) && value.length()>0){
                    Log.d(TAG,"UPDATE PHONE");
                    attemptUpdate(MODIFY_PHONE);
                }else if(getProfile().getPhone().equals(value)){
                    text_business_phone.setEnabled(false);
                }
            }
        });

        button_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(ProfileActivity.this,getString(R.string.pick_image_intent_text));
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });
        layout_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                startActivityForResult(intent, METHOD_CHANGE_PASSORD);
            }
        });

    }
    private void attemptUpdate(String type_modification){
        String idUser = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.localUserId);

        initProcess(true);
        HashMap<String,String> params = new HashMap<>();
        params.put("method","updateUser");
        params.put("id_user",idUser);
        params.put("type_user",getProfile().getUserType());
        params.put("name",text_name_profile.getText().toString().trim());
        params.put("first", text_first_profile.getText().toString().trim());
        params.put("last",text_last_profile.getText().toString().trim());
        params.put("company",text_name_business.getText().toString().trim());
        params.put("phone",text_business_phone.getText().toString().trim());
        params.put("website",text_web_site.getText().toString().trim());
        params.put("business_desc",text_business_info.getText().toString().trim());
        params.put("type_modification",type_modification);

        VolleyPostRequest(Constants.USERS,params,METHOD_UPDATE_USER,type_modification);

    }
    private void updateImageProfileRoute(){
        String idUser = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.localUserId);
        initProcess(true);
        HashMap<String,String> params = new HashMap<>();
        params.put("method","updateImageProfileRoute");
        params.put("id_route",getProfile().getIdRouteImage());
        params.put("id_user",idUser);
        params.put("avatar_path",nameImage!=null && nameImage.length()>0?nameImage:"");
        params.put("name_image",ImagePicker.getImageName(nameImage));
        params.put("name_image_old",getProfile().getRemoteImage());
        VolleyPostRequest(Constants.USERS,params,METHOD_UPDATE_IMAGE,null);
    }
    public void VolleyPostRequest(String url, final HashMap<String, String> args, final int callback,final String type_modification){
        JSONObject jobject = new JSONObject(args);
        Log.d(TAG,"VolleyPostRequest:" + METHOD_UPDATE_USER+" "+ jobject.toString());
        JsonObjectRequest request =new JsonObjectRequest(
                Request.Method.POST,
                url,
                jobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar la respuesta del servidor
                        if (callback == METHOD_UPDATE_USER)
                            processResponseUpdate(response,type_modification);
                        else if (callback == METHOD_UPDATE_IMAGE)
                            processResponseRoute(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                        ShowConfirmations.showConfirmationMessage(getString(R.string.generic_error),ProfileActivity.this);
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

    public int getUser_modifications_done() {
        return user_modifications_done;
    }

    public void setUser_modifications_done(int user_modifications_done) {
        this.user_modifications_done = user_modifications_done;
    }
    private void hideKeyBoard(View v){
        //hide keyboord
        InputMethodManager imm = (InputMethodManager)
        getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                v.getWindowToken(), 0);
    }
    private void processResponseUpdate(JSONObject response,String type_modification){
        Log.d(TAG,"Result:" +response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    String object = response.getString("result");
                    initProcess(false);
                    setUser_modifications_done(1);
                    //update login user
                    LoginResponse login = UserLocalProfile.getUserProfile(getApplicationContext());
                    switch(type_modification){
                        case MODIFY_NAME:
                            login.setName(text_name_profile.getText().toString());
                            profile.setName(text_name_profile.getText().toString());
                            text_name_profile.clearFocus();
                            hideKeyBoard(text_name_profile);
                            break;
                        case MODIFY_FIRST:
                            login.setFirst(text_first_profile.getText().toString());
                            profile.setFirst(text_first_profile.getText().toString());
                            hideKeyBoard(text_first_profile);
                            text_first_profile.clearFocus();
                            break;
                        case MODIFY_LAST:
                            login.setLast(text_last_profile.getText().toString());
                            profile.setLast(text_last_profile.getText().toString());
                            hideKeyBoard(text_last_profile);
                            text_last_profile.clearFocus();
                            break;
                        case MODIFY_NAME_BUSINESS:hideKeyBoard(text_name_business);
                            text_name_business.clearFocus();
                            profile.setCompany(text_name_business.getText().toString());
                            break;
                        case MODIFY_PHONE:hideKeyBoard(text_business_phone);
                            text_business_phone.clearFocus();
                            profile.setPhone(text_business_phone.getText().toString());
                            break;
                        case MODIFY_WEB:hideKeyBoard(text_web_site);
                            text_web_site.clearFocus();
                            profile.setWebsite(text_web_site.getText().toString());
                            break;
                    }
                    UserLocalProfile.saveSessionValuesUser(getApplicationContext(),login);
                    ShowConfirmations.showConfirmationMessage(getString(R.string.text_prompt_profile_updated),this);
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
            ShowConfirmations.showConfirmationMessage(getString(R.string.generic_error),this);
        }
    }
    private void processResponseRoute(JSONObject response){
        Log.d(TAG,"Result:" +response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    String object = response.getString("result");
                    initProcess(false);
                    Glide.with(ProfileActivity.this)
                            .load(nameImage)
                            .error(R.drawable.ic_account_circle)
                            .into(header_imageview);
                    setUser_modifications_done(1);
                    ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.localAvatarPath,nameImage);
                    String idUser = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.localUserId);
                    String nameImageToServer = idUser+ "_" + ImagePicker.getImageName(nameImage);
                    getProfile().setRemoteImage(ImagePicker.getImageName(nameImageToServer));
                    ShowConfirmations.showConfirmationMessage(getString(R.string.text_prompt_image_updated),ProfileActivity.this);
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
            ShowConfirmations.showConfirmationMessage(getString(R.string.generic_error),this);
        }
    }
    private void initProcess(boolean flag){
        if (flag)
            createProgressDialog(getString(R.string.promtp_payment_processing));
        else
            closeProgressDialog();
    }

    public ProfileUser getProfile() {
        return profile;
    }


    private void loadInfoUser(){
        String id = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.localUserId);
        VolleyGetRequest(Constants.USERS + "?method=getUserProfile&idUser="+id);
    }
    private void initGUI(){
        profile_container = (NestedScrollView) findViewById(R.id.profile_container);
        detail_form = (LinearLayout) findViewById(R.id.detail_form);
        text_email_view = (TextView) findViewById(R.id.text_email_view);
        text_name_view = (TextView) findViewById(R.id.text_name_view);
        text_name_profile = (MaterialEditText) findViewById(R.id.text_name_profile);
        text_first_profile = (MaterialEditText) findViewById(R.id.text_first_profile);
        text_last_profile = (MaterialEditText) findViewById(R.id.text_last_profile);
        text_name_business = (MaterialEditText) findViewById(R.id.text_name_business);
        text_business_phone = (MaterialEditText) findViewById(R.id.text_business_phone);
        text_web_site = (MaterialEditText) findViewById(R.id.text_web_site);
        loading_profile = (ProgressBar)findViewById(R.id.loading_profile);
        loading_profile.setVisibility(View.VISIBLE);
        header_imageview = (CircleImageView) findViewById(R.id.header_imageview);
        button_add_photo = (ImageButton) findViewById(R.id.button_add_photo);
        text_business_info = (TextView) findViewById(R.id.text_business_info);

        button_edit_name = (ImageView) findViewById(R.id.button_edit_name);
        button_done_name = (ImageView) findViewById(R.id.button_done_name);
        button_edit_first = (ImageView) findViewById(R.id.button_edit_first);
        button_done_first = (ImageView) findViewById(R.id.button_done_first);
        button_edit_last = (ImageView) findViewById(R.id.button_edit_last);
        button_done_last = (ImageView) findViewById(R.id.button_done_last);
        button_edit_business_name = (ImageView) findViewById(R.id.button_edit_business_name);
        button_done_business_name = (ImageView) findViewById(R.id.button_done_business_name);
        button_edit_web = (ImageView) findViewById(R.id.button_edit_web);
        button_done_web = (ImageView) findViewById(R.id.button_done_web);
        button_edit_phone = (ImageView) findViewById(R.id.button_edit_phone);
        button_done_phone = (ImageView) findViewById(R.id.button_done_phone);
        button_edit_info = (ImageView) findViewById(R.id.button_edit_info);
        layout_change_password = (LinearLayout) findViewById(R.id.layout_change_password);
        nameImage = null;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getUser_modifications_done()==0)
                    finish();
                else{
                    Intent i = new Intent(ProfileActivity.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void VolleyGetRequest(String url){
        Log.d(TAG, "VolleyGetRequest:" + url);
        VolleySingleton.
                getInstance(this).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                (String) null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Procesar la respuesta Json
                                        processingResponse(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        String message = "Verifique su conexión a internet.";
                                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                                        Toast.makeText(
                                                ProfileActivity.this,
                                                message,
                                                Toast.LENGTH_SHORT).show();
                                        initProcess(false);
                                    }
                                }

                        ).setRetryPolicy(new DefaultRetryPolicy(
                                Constants.MY_SOCKET_TIMEOUT_MS,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                );
    }
    private void processingResponse(JSONObject response) {

        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    Gson gson = new Gson();
                    JSONObject mensaje = response.getJSONObject("result");
                    profile = gson.fromJson(mensaje.toString(), ProfileUser.class);
                    setupProfile(profile);
                    loading_profile.setVisibility(View.GONE);
                    profile_container.setVisibility(View.VISIBLE);
                    break;
                case "2": // NO DATA FOUND
                    Log.d(TAG,"noData");
                    String mensaje2 = response.getString("message");
                    ShowConfirmations.showConfirmationMessage(mensaje2,this);
                    loading_profile.setVisibility(View.GONE);
                    onBackPressed();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            String mensaje2 = getString(R.string.generic_error);
            ShowConfirmations.showConfirmationMessage(mensaje2,this);
            initProcess(false);

        }

    }
    private void setupProfile(ProfileUser user){
        if (user.getRemotePath()!=null && user.getRemoteImage()!=null){
            Glide.with(ProfileActivity.this)
                    .load(user.getRemotePath()+user.getRemoteImage())
                    .error(R.drawable.ic_account_circle)
                    .into(header_imageview);
        }
        if (user.getUserType().contains("suscriptor"))
            detail_form.setVisibility(View.VISIBLE);

        text_email_view.setText(user.getEmail());
        text_name_view.setText(user.getName()+" "+user.getFirst()+" "+ user.getLast());
        text_name_profile.setText(user.getName());
        text_first_profile.setText(user.getFirst());
        text_last_profile.setText(user.getLast());
        text_name_business.setText(user.getCompany());
        text_business_phone.setText(user.getPhone());
        text_web_site.setText(user.getWebsite());
        text_business_info.setText(user.getBusinessDescription());
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case PICK_IMAGE_ID:
                if (resultCode == Activity.RESULT_OK) {
                    bitmap = ImagePicker.getImageFromResult(ProfileActivity.this, resultCode, data);
                    header_imageview.setImageBitmap(bitmap);
                    nameImage = ImagePicker.getRealPathFromURI(header_imageview.getContext(), ImagePicker.getUriBitmapSelected());
                    //upload image
                    updateImageProfile();
                }
                break;
            case METHOD_GET_INFO:
                if (resultCode == Activity.RESULT_OK){
                    String value = data.getStringExtra(GET_INFO_BUSINESS);
                    if (!getProfile().getBusinessDescription().equals(value) && value.length()>0){
                        Log.d(TAG,"UPDATE INFO");
                        text_business_info.setText(value);
                        attemptUpdate(MODIFY_INFO);
                    }
                }
                break;
            case METHOD_CHANGE_PASSORD:
                if (resultCode == Activity.RESULT_OK){
                    ShowConfirmations.showConfirmationMessage(getString(R.string.text_prompt_profile_updated),this);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
        }
    private void updateImageProfile(){
        String idUser = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.localUserId);
        String nameImageToServer = idUser+ "_" + ImagePicker.getImageName(nameImage);
        Log.d(TAG,"nombre final imagen: " + nameImageToServer);
        if (bitmap!=null) { //usuario selecciono avatar
            Log.d(TAG,"bitmap: " + bitmap.getByteCount());
            UploadImage.uploadImage(ProfileActivity.this, nameImageToServer , bitmap);
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
                            ShowConfirmations.showConfirmationMessage(getString(R.string.text_prompt_image_error),ProfileActivity.this);
                            //carga imagen anterior
                            Glide.with(ProfileActivity.this)
                                    .load(getProfile().getRemotePath()+getProfile().getRemoteImage())
                                    .error(R.drawable.ic_account_circle)
                                    .into(header_imageview);
                        }

                    }
                });


            }

        }, 0, PublishFragment.DURATION);
    }
    @Override
    public void onBackPressed() {
        if (getUser_modifications_done()==0){
            finish();
        }else{
            Intent i = new Intent(ProfileActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }
    }
    private void createProgressDialog(String message){
        progressDialog = new ProgressDialog(ProfileActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();
    }

}
