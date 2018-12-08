package estrategiamovil.comerciomovil.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.KeyListener;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Address;
import estrategiamovil.comerciomovil.modelo.CategoryViewModel;
import estrategiamovil.comerciomovil.modelo.Sector;
import estrategiamovil.comerciomovil.modelo.SignupResponse;
import estrategiamovil.comerciomovil.modelo.SubCategory;
import estrategiamovil.comerciomovil.modelo.SubSubCategory;
import estrategiamovil.comerciomovil.modelo.User;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ImagePicker;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.tools.UploadImage;
import estrategiamovil.comerciomovil.tools.UtilPermissions;
import estrategiamovil.comerciomovil.tools.VolleyErrorHelper;
import estrategiamovil.comerciomovil.ui.activities.LocationsActivity;
import estrategiamovil.comerciomovil.ui.activities.SelectCategoryActivity;
import estrategiamovil.comerciomovil.ui.activities.SignupActivity;
import estrategiamovil.comerciomovil.ui.adapters.AddressAdapter;
import estrategiamovil.comerciomovil.ui.adapters.CityAdapter;
import estrategiamovil.comerciomovil.ui.adapters.CustomSpinnerAdapter;
import estrategiamovil.comerciomovil.web.VolleySingleton;


public class SignupSubscriberFragment extends Fragment{
    private final int METHOD_REMOVE_USER = 1;
    private final int METHOD_SIGNUP = 2;
    private Bitmap bitmap_loaded = null;
    private static final Integer DURATION = 1000;
    private JSONArray address_list;
    private RecyclerView recycler_locations;
    private EditText text_bussiness_signup;
    //private Spinner spinner_sector_signup;
    private EditText text_category;
    private EditText text_location_singup;
    private Button email_sign_in_button;
    private CheckBox checkbox_website;
    private ExpandableRelativeLayout layout_website;
    private TextView link_login;
    private TextView text_name_view;
    private TextView text_email_view;
    private ProgressDialog progressDialog;
    private EditText text_phone_subscriber;
    private EditText text_website_subscriber;
    private ImageButton button_select_location;
    private ImageView header_imageview;
    private ImageButton button_add_photo;
    private TextView text_error_spinner;
    private EditText text_business_description;
    private FloatingActionButton button_edit_image;
    private Bitmap bitmap;
    private String nameImage = null;
    private static final String TAG = SignupSubscriberFragment.class.getSimpleName();
    private final int PLACE_PICKER_REQUEST = 1;

    private final int METHOD_CALLBACK_SIGNUP = 1;
    private final int METHOD_CALLBACK_USER_VALID = 2;
    private static final int PICK_IMAGE_ID = 2;
    private static Sector[] sectors;
    private Gson gson = new Gson();
    private User user;
    private SignupResponse signup_response;
    private Sector sector = null;
    private HashMap<String,Address> map_locations;
    private Timer timer = null;
    private CategoryViewModel category;
    private SubCategory sub_category;
    private SubSubCategory sub_sub_category;



    public static SignupSubscriberFragment newInstance(User user) {
        SignupSubscriberFragment fragment = new SignupSubscriberFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    public SignupSubscriberFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User)getArguments().getSerializable("user");
        }

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!UtilPermissions.hasPermissions(getActivity(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, UtilPermissions.PERMISSION_ALL);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_signup_subscriber, container, false);
        createProgressDialog("Cargando...");
        map_locations = new HashMap<>();
        recycler_locations = (RecyclerView) v.findViewById(R.id.recycler_locations);
        recycler_locations.setHasFixedSize(true);
        recycler_locations.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));


        text_name_view = (TextView) v.findViewById(R.id.text_name_view);
        text_email_view = (TextView) v.findViewById(R.id.text_email_view);

        if (user!=null) {
            text_name_view.setText(user.getName()!=null && !user.getName().equals("")?user.getName() + " " + user.getFirst() + " " + user.getLast():"Invitado");
            text_email_view.setText(user.getEmail());
        }

        header_imageview = (ImageView) v.findViewById(R.id.header_imageview);
        header_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity(),getString(R.string.pick_image_intent_text));
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });
        button_add_photo = (ImageButton) v.findViewById(R.id.button_add_photo);
        button_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity(),getString(R.string.pick_image_intent_text));
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });
        loadImageSelected();

        text_bussiness_signup = (EditText) v.findViewById(R.id.text_bussiness_signup);
        text_error_spinner = (TextView) v.findViewById(R.id.text_error_spinner);
        text_category = (EditText) v.findViewById(R.id.text_category);
        text_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectCategoryActivity.class);
                intent.putExtra(FindBusinessFragment.TYPE_FLOW_CATEGORY,String.valueOf(false));
                startActivityForResult(intent, PublishFragment.SELECT_SUBCATEGORY);
            }
        });
        /*spinner_sector_signup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ImageView bullet = (ImageView) view.findViewById(R.id.image);
                View divider = (View) view.findViewById(R.id.spinner_divider);
                divider.setVisibility(View.GONE);
                bullet.setVisibility(View.GONE);
                Sector sector_selected = (Sector)parent.getItemAtPosition(position);
                if (sector_selected!=null && !sector_selected.getIdSector().equals("0")) {
                    sector = (Sector) sector_selected;
                    Log.d(TAG,"Sector:" + sector.getIdSector() + " " + sector.getDescription());
                }else{ sector = null;}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        text_location_singup = (EditText)v.findViewById(R.id.text_location_singup);
        text_location_singup.setText("");

        text_phone_subscriber = (EditText)v.findViewById(R.id.text_phone_subscriber);
        button_select_location = (ImageButton) v.findViewById(R.id.button_select_location);
        button_select_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LocationsActivity.class);
                if (map_locations!=null) {
                    i.putExtra("map_locations", map_locations);
                }
                startActivityForResult(i,Constants.REQUEST_LOCATIONS);
            }
        });

        email_sign_in_button = (Button) v.findViewById(R.id.email_sign_in_button);
        email_sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });


        link_login = (TextView) v.findViewById(R.id.link_login);
        link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });
        text_website_subscriber = (EditText) v.findViewById(R.id.text_website_subscriber);
        layout_website = (ExpandableRelativeLayout) v.findViewById(R.id.layout_website);
        checkbox_website = (CheckBox) v.findViewById(R.id.checkbox_website);
        checkbox_website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox_website.isChecked()){
                    layout_website.expand();
                }else{
                    layout_website.collapse();
                }
            }
        });
        text_business_description = (EditText) v.findViewById(R.id.text_business_description);
        //VolleyGetRequest(Constants.GET_SECTORS + "?method=getSectors", METHOD_CALLBACK_SECTOR);
        dismissProgressDialog();
        return v;
    }

    private void loadImageSelected(){
        if (user!=null && user.getAvatarPath()!=null) {

            Glide.with(getActivity())
                    .load(new File( user.getAvatarPath()))
                    .error(R.drawable.ic_account_circle)
                    .into(header_imageview);
            nameImage = user.getAvatarPath();
            //}
        }else{


            Glide.with(getActivity())
                    .load(R.drawable.ic_account_circle)
                    .error(R.drawable.ic_account_circle)
                    .into(header_imageview);
        }
    }
    public void onSignupSuccess() {
        final String nameImageToServer = signup_response.getNewUserId()+"";
        if (bitmap!=null){//load new image selected
            UploadImage.uploadImage(getActivity(), nameImageToServer + "_" + ImagePicker.getImageName(nameImage), bitmap);
            start();
        }else if (user.getAvatarPath() != null) { //check if there are early image selected
            Log.d(TAG, "subiendo imagen perfil: " + user.getAvatarPath());

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Looper.prepare();
                        try {
                            bitmap_loaded = Glide.
                                    with(getActivity()).
                                    load(new File(user.getAvatarPath())).
                                    asBitmap().
                                    into(300, 300).
                                    get();
                        } catch (final ExecutionException e) {
                            Log.e(TAG, e.getMessage());
                        } catch (final InterruptedException e) {
                            Log.e(TAG, e.getMessage());
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void dummy) {
                        if (null != bitmap_loaded) {
                            // The full bitmap should be available here
                            if (bitmap_loaded != null) {
                                UploadImage.uploadImage(getActivity(), nameImageToServer + "_" + ImagePicker.getImageName(nameImage), bitmap_loaded);
                                start();
                            }
                        }
                        ;
                    }
                }.execute();

            }else {
                getActivity().setResult(Activity.RESULT_OK, null);
                getActivity().finish();
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

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (UploadImage.ready==1 && UploadImage.error == 0) {Log.d(TAG,"1------UploadImage.ready==1..UploadImage.error == 0");
                            UploadImage.initUploadImageVariables();
                            dismissProgressDialog();
                            stop();
                            getActivity().setResult(Activity.RESULT_OK, null);Log.d(TAG,"finish:" + "error:"+UploadImage.error+" ready:"+UploadImage.ready+"map:"+UploadImage.getMapError().size());
                            //save preference to activate account
                            ApplicationPreferences.saveLocalPreference(getContext(),Constants.showTipSignupSuscriber,String.valueOf(true));
                            dismissProgressDialog();
                            getActivity().finish();
                        }else if(UploadImage.ready==1 && UploadImage.error == 1 && UploadImage.getMapError().size()>0){// hubo errores, reintentar...
                            Log.d(TAG,"2-----------UploadImage.ready==1..UploadImage.error == 1 map:"+UploadImage.getMapError().size());
                            dismissProgressDialog();
                            stop();
                            confirmAction(UploadImage.getMapError().size()+" "+getResources().getString(R.string.prompt_retry_promt),getResources().getString(R.string.prompt_uploaderror_promt),getResources().getString(R.string.prompt_action_retry),getResources().getString(R.string.prompt_cancel));
                        }else if(UploadImage.ready==1 && UploadImage.error == 1 && UploadImage.getMapError().size()==0){//error e el servicio
                            Log.d(TAG,"3-------------UploadImage.ready==1..UploadImage.error == 1 map:0");
                            deleteUser(signup_response);
                            dismissProgressDialog();
                            stop();

                            try {
                                onSignupFailed(new JSONObject().put("message",getResources().getString(R.string.generic_error)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

    public void confirmAction(String message, String title, String positive, String negative){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                    String nameImageToServer = signup_response.getNewUserId()+"";
                    createProgressDialog("Reintentando...");
                    HashMap map = UploadImage.getMapError();
                    if (map!=null) {
                        Log.d(TAG, "Reintentar con : " + map.size() + " imagenes"+ " error="+ UploadImage.error + " ready="+UploadImage.ready);
                        UploadImage.initUploadImageVariables();
                        Log.d(TAG, "antes de Reintentar valores reiniciados: map:" + UploadImage.getMapError().size() + " imagenes"+ " error="+ UploadImage.error + " ready="+UploadImage.ready);
                        UploadImage.uploadImage(getActivity(), nameImageToServer + "_" + ImagePicker.getImageName(nameImage),(bitmap!=null)?bitmap:bitmap_loaded);
                        start();
                    }else{Log.d(TAG," lista errores null--------------");}


            }
        });

        alertDialogBuilder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(LocationsActivity.this, "You clicked yes button", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Antes de borrar map : " + UploadImage.getMapError().size() + " error="+ UploadImage.error + " ready="+UploadImage.ready);
                deleteUser(signup_response);

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void deleteUser(SignupResponse response){
        HashMap<String,String> params = new HashMap<>();
        params.put("method","removeUser");
        params.put("idUser", String.valueOf(response.getNewUserId()));
        VolleyPostRequest(Constants.REMOVE_USER, params,METHOD_REMOVE_USER);
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
    public void signup() {

        if (!validate()) {
            Toast.makeText(getContext(), getActivity().getString(R.string.error_data_not_valid), Toast.LENGTH_LONG).show();
            return;
        }

        email_sign_in_button.setEnabled(false);
        createProgressDialog("Registrando...");


        Log.d(TAG,"lista signup: " + address_list.toString());
        String company = text_bussiness_signup.getText().toString().trim();
        String phone = text_phone_subscriber.getText().toString().trim();
        String web = text_website_subscriber.getText().toString().trim();
        String business_desc = text_business_description.getText().toString().trim();
        HashMap<String,String> params = new HashMap<>();
        params.put("method","signupSubscriber");
        params.put("name",user.getName());
        params.put("first", user.getFirst());
        params.put("last",user.getLast());
        params.put("email",user.getEmail());
        params.put("password",user.getPassword());
        params.put("categories",ApplicationPreferences.getLocalStringPreference(getActivity(), Constants.categoriesUser));
        params.put("company",company);
        params.put("phone",phone);
        params.put("id_category",getCategory()!=null?getCategory().getIdCategory():"0");
        params.put("id_subcategory",getSub_category()!=null?getSub_category().getIdSubCategory():"0");
        params.put("id_sub_subcategory",getSub_sub_category()!=null?getSub_sub_category().getIdSubSubCategory():"0");
        params.put("website",checkbox_website.isChecked()?web:"");
        params.put("address",address_list.toString());//guardar los datos de las ubicaciones a guardar de un usuario(json array)
        params.put("avatar_path",nameImage);
        params.put("name_image",ImagePicker.getImageName(nameImage));
        String token = FirebaseInstanceId.getInstance().getToken();
        params.put("token",token );
        params.put("business_description",business_desc );
        VolleyPostRequest(Constants.SIGNUP_SUBSCRIBER,params,METHOD_SIGNUP);

    }
    private void processingUserValid(JSONObject response){
        Log.d(TAG, "processingUserValid:" + response.toString());
        dismissProgressDialog();

    }

    /*private void processingSectors(JSONObject response){
        try {
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("sectors");
                    // Parsear con Gson
                    sectors = gson.fromJson(mensaje.toString(), Sector[].class);
                    ArrayList<Sector> list = new ArrayList<Sector>();
                    // Inicializar adaptador programatically
                    list.addAll(Arrays.asList(sectors));
                    Sector selecOption = new Sector();selecOption.setDescription("Seleccione...");selecOption.setIdSector("0");selecOption.setSector("Seleccione...");
                    list.add(0,selecOption);
                    CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getActivity(),R.layout.spinner_rows,list);
                    spinner_sector_signup.setAdapter(spinnerAdapter);
                    dismissProgressDialog();
                    break;
                case "2": // FALLIDO
                    Log.d(TAG, "fail");
                    dismissProgressDialog();
                    String mensaje2 = response.getString("message");
                    Toast.makeText(
                            getActivity(),
                            mensaje2,
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
    }*/
    private void processResponse(JSONObject response){
        // On complete call either onSignupSuccess or onSignupFailed
        // depending on success
        Log.d(TAG, response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                        JSONObject object = response.getJSONObject("create");
                        if (object.getString("status").equals("1")) {
                            signup_response = gson.fromJson(object.toString(), SignupResponse.class);
                            Log.d(TAG,"signup_response:"+signup_response.toString());
                            ApplicationPreferences.saveLocalPreference(getActivity(), Constants.localEmail, user.getEmail());
                            ApplicationPreferences.saveLocalPreference(getActivity(), Constants.localUserId, signup_response.getNewUserId() +"");
                            ApplicationPreferences.saveLocalPreference(getActivity(), Constants.localAvatarPath, (nameImage != null) ? nameImage : null);
                            ApplicationPreferences.saveLocalPreference(getActivity(), Constants.localPassword, user.getPassword());
                            Log.d(TAG, "preferences guardadas por Registro Subscriptor: " + ApplicationPreferences.getLocalStringPreference(getActivity(), Constants.localEmail) + "," +
                                    ApplicationPreferences.getLocalStringPreference(getActivity(), Constants.localUserId) + "," +
                                    ApplicationPreferences.getLocalStringPreference(getActivity(), Constants.localAvatarPath));
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

    public void VolleyPostRequest(String url, final HashMap<String, String> args, final int callback){
        JSONObject jobject = new JSONObject(args);
        Log.d(TAG,"VolleyPostRequest:" +  jobject.toString());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar la respuesta del servidor
                        if (callback == METHOD_REMOVE_USER)
                            processResponseRemove(response);
                        else if(callback == METHOD_SIGNUP)
                            processResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                        email_sign_in_button.setEnabled(true);
                        Toast.makeText(getActivity(), "Hay un problema, intenta mas tarde...", Toast.LENGTH_SHORT);
                        showConfirmationMessage("Hay un problema, intenta nuevamente...");
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

        VolleySingleton.getInstance(getActivity()).addToRequestQueue( request );


    }
    private void processResponseRemove(JSONObject response){
        Log.d(TAG,"Remove:" +response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    String object = response.getString("result");
                    Log.d(TAG,"EXITO !!:"+object);
                    break;
                case "2":
                    Log.d(TAG,"NO SE ELIMINO EL USUARIO: ");
                    String object_error = response.getString("message");
                    Log.d(TAG,object_error);
                    break;

                default:
                    break;
            }
            email_sign_in_button.setEnabled(true);
            dismissProgressDialog();
            Log.d(TAG,"Al finalizar remove: map:"+UploadImage.getMapError().size() + " error:"+UploadImage.error + " ready:"+UploadImage.ready);
            UploadImage.initUploadImageVariables();
            Log.d(TAG,"Al finalizar reinicializado: map:"+UploadImage.getMapError().size() + " error:"+UploadImage.error + " ready:"+UploadImage.ready);
        } catch (JSONException e) {
            e.printStackTrace();
            email_sign_in_button.setEnabled(true);
            dismissProgressDialog();
            UploadImage.initUploadImageVariables();
            Log.d(TAG,"Excepcion en finalizar remove: map:"+UploadImage.getMapError().size() + " error:"+UploadImage.error + " ready:"+UploadImage.ready);
        }
    }

    private void showConfirmationMessage(final String message){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
            }
        });
    }
    public void VolleyGetRequest(String url, final int callbackFunction){
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
                                        /*if (callbackFunction == METHOD_CALLBACK_SECTOR)
                                            processingSectors(response);
                                        else*/
                                        if (callbackFunction == METHOD_CALLBACK_SIGNUP)
                                            processResponse(response);
                                        else if (callbackFunction == METHOD_CALLBACK_USER_VALID)
                                            processingUserValid(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        String message = VolleyErrorHelper.getErrorType(error, getActivity());
                                        //Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                                        Toast.makeText(
                                                getActivity(),
                                                message,
                                                Toast.LENGTH_SHORT).show();
                                        email_sign_in_button.setEnabled(true);
                                    }
                                }

                        ).setRetryPolicy(new DefaultRetryPolicy(
                                Constants.MY_SOCKET_TIMEOUT_MS,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                );
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
    public boolean validate() {
        boolean valid = true;
        String web = text_website_subscriber.getText().toString().trim();
        String nameBussiness = text_bussiness_signup.getText().toString();
        String phone = text_phone_subscriber.getText().toString();
        String text_business = text_business_description.getText().toString().trim();

        if (user!=null) {//validate  image selected
            Log.d(TAG,"Validando imagen..."+user.getAvatarPath() + ", " + nameImage);
            if(user.getAvatarPath()==null && nameImage==null){//usuario no selecciono ninguna imagen
                Log.d(TAG,"falta imagen...");
                ShowConfirmations.showConfirmationMessage(getString(R.string.error_image__profile_required),getActivity());
                valid = false;
                return false;
            }
        }


        if (nameBussiness.isEmpty() || nameBussiness.length() < 3) {
            text_bussiness_signup.setError("Nombre demasiado corto");
            valid = false;
        } else {
            text_bussiness_signup.setError(null);
        }

        /*if (sector!=null) {
            if (sector.getIdSector().equals("")) {
                text_error_spinner.setError("Seleccione una opción");
                valid = false;
            } else {
                text_error_spinner.setError(null);
            }
        }else{
            text_error_spinner.setText(getActivity().getString(R.string.error_field_required));
            valid = false;
        }*/
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
            String URL_REGEX_YOUTUBE = "^(https?:\\/\\/)?(www.)?(m.)?youtu(\\.be|be\\.com)?\\/(watch\\?v=)?([\\w\\d_-]{11})$";
            Pattern p = Pattern.compile(URL_REGEX);
            Pattern p_youtube = Pattern.compile(URL_REGEX_YOUTUBE);
            Matcher m = p.matcher(web);
            Matcher m_youtube = p_youtube.matcher(web);
            if(!m.find() && !m_youtube.find()) {
                text_website_subscriber.setError("Dirección inválida");
                valid = false;
            } else {
                Log.d(TAG,"URL ok");
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
            case PICK_IMAGE_ID:
                if (resultCode == Activity.RESULT_OK) {
                    bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data);
                    header_imageview.setImageBitmap(bitmap);
                    nameImage = ImagePicker.getRealPathFromURI(header_imageview.getContext(), ImagePicker.getUriBitmapSelected());//ImagePicker.getImageName(ImagePicker.getRealPathFromURI(header_imageview.getContext(), ImagePicker.getUriBitmapSelected())); // ImagePicker.getRealPathFromURI(header_imageview.getContext(), ImagePicker.getUriBitmapSelected());
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
        //container_locations_added
        //generte linear layout horizontal and add text view with text
        Log.d(TAG,"agregar: " + address.toString());
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

        Log.d(TAG, "addView: " + address.toString());

        return item;

    }
}
