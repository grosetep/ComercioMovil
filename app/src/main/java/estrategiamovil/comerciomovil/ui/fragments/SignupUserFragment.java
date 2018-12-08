package estrategiamovil.comerciomovil.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.scottyab.aescrypt.AESCrypt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.SignupResponse;
import estrategiamovil.comerciomovil.modelo.User;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ImagePicker;
import estrategiamovil.comerciomovil.tools.StringOperations;
import estrategiamovil.comerciomovil.tools.UploadImage;
import estrategiamovil.comerciomovil.tools.UtilPermissions;
import estrategiamovil.comerciomovil.tools.ViewAnimationUtils;
import estrategiamovil.comerciomovil.tools.VolleyErrorHelper;
import estrategiamovil.comerciomovil.ui.activities.ShowConditionsActivity;
import estrategiamovil.comerciomovil.ui.activities.SignupActivity;
import estrategiamovil.comerciomovil.web.VolleySingleton;


public class SignupUserFragment extends Fragment {
    private ImageView header_imageview;
    private ImageButton button_add_photo;
    private AppCompatCheckBox checkbox_show_password_signup;
   // private ImageButton button_display_name;
    private ImageButton button_display_name;
    private ImageView image_profile_signup;
    private MaterialEditText text_name_signup;
    private MaterialEditText text_first_signup;
    private MaterialEditText text_last_signup;
    private MaterialEditText text_email;
    private MaterialEditText text_password_signup;
    private ExpandableRelativeLayout container_name;
    private TextView text_conditions;
    private CheckBox checkbox_conditions;
    //private EditText text_password_confirm;
    private AppCompatButton email_sign_in_button;
    private AppCompatButton button_signup_subscriber;
    private ProgressDialog progressDialog;
    private Bitmap bitmap;
    private Uri uriImage;
    private String nameImage;
    private Gson gson = new Gson();
    private static final int METHOD_CALLBACK_SIGNUP = 1;
    private static final int PICK_IMAGE_ID = 2;
    /*params user*/
    private static  int USER = 4;
    private final int METHOD_CALLBACK_USER_VALID = 2;
    private final int METHOD_CALLBACK_USER_IS_ENTERPRICE = 3;
    private final int METHOD_CALLBACK_DELETE_USER = 4;
    private static final String TAG = SignupUserFragment.class.getSimpleName();
    private SignupResponse signup_response;
    private int flow = 0;
    private Timer timer = null;
    private User user;
    public SignupUserFragment() {

    }
    public static SignupUserFragment newInstance(User user) {
        SignupUserFragment fragment = new SignupUserFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
        if(!UtilPermissions.hasPermissions(getActivity(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, UtilPermissions.PERMISSION_ALL);
        }
        if (getArguments() != null) {
            user = (User)getArguments().getSerializable(SignupActivity.LOGIN_PARAMS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_signup_user, container, false);

        button_display_name = (ImageButton) v.findViewById(R.id.button_display_name);
        container_name = (ExpandableRelativeLayout) v.findViewById(R.id.container_name);
        button_display_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                container_name.toggle(); // toggle expand and collapse
            }
        });
        header_imageview = (ImageView) v.findViewById(R.id.header_imageview);
        button_add_photo = (ImageButton) v.findViewById(R.id.button_add_photo);
        button_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity(),getString(R.string.pick_image_intent_text));
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);

            }
        });
        image_profile_signup = (ImageView) v.findViewById(R.id.image_profile_signup);
        if (bitmap!=null) image_profile_signup.setImageBitmap(bitmap);
        text_name_signup = (MaterialEditText) v.findViewById(R.id.text_name_signup);
        text_first_signup = (MaterialEditText) v.findViewById(R.id.text_first_signup);
        text_last_signup = (MaterialEditText) v.findViewById(R.id.text_last_signup);
        text_email = (MaterialEditText) v.findViewById(R.id.text_email);
        text_password_signup = (MaterialEditText) v.findViewById(R.id.text_password_signup);
        checkbox_show_password_signup = (AppCompatCheckBox) v.findViewById(R.id.checkbox_show_password_signup);
        checkbox_show_password_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox_show_password_signup.isChecked()){
                    text_password_signup.setTransformationMethod(null);
                }else{
                    text_password_signup.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });
        //text_password_confirm = (EditText) v.findViewById(R.id.text_password_confirm);
        email_sign_in_button = (AppCompatButton) v.findViewById(R.id.email_sign_in_button);
        email_sign_in_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signup();
                    }
                });
        button_signup_subscriber = (AppCompatButton) v.findViewById(R.id.button_signup_subscriber);
        button_signup_subscriber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    flow = METHOD_CALLBACK_USER_IS_ENTERPRICE;
                    attemptExistUser();
                    return;
                }

            }
        });
        checkbox_conditions = (CheckBox) v.findViewById(R.id.checkbox_conditions);
        text_conditions = (TextView) v.findViewById(R.id.text_conditions);
        text_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowConditionsActivity.class);
                startActivity(intent);
            }
        });
        loadInformation();
        return v;
    }
    private void loadInformation(){
        if(user!=null){
            text_email.setText(user.getEmail()!=null && !user.getEmail().isEmpty()?user.getEmail():"");
            text_password_signup.setText(user.getPassword()!=null && !user.getPassword().isEmpty()?user.getPassword():"");
            image_profile_signup.requestFocus();
        }
    }
    private void saveTempValues(){
        Log.d(TAG,"Pasar a formulario de Suscriptor...");
        Intent i = new Intent(getActivity(), SignupActivity.class);
        Bundle args = new Bundle();
        User u = new User();
        u.setEmail(text_email.getText().toString());
        try {
            u.setPassword(AESCrypt.encrypt(Constants.seedValue,text_password_signup.getText().toString().trim()));//aqui
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            Toast.makeText(getActivity(),"Error de encripcion, reporte a soporte@estrategiamovilmx.com",Toast.LENGTH_SHORT);
            return;
        }
        u.setName(text_name_signup.getText().toString());
        u.setFirst(text_first_signup.getText().toString());
        u.setLast(text_last_signup.getText().toString());
        u.setAvatarPath((bitmap != null) ? ImagePicker.getRealPathFromURI(header_imageview.getContext(), ImagePicker.getUriBitmapSelected()) : null);
        args.putSerializable("user", u);
        i.putExtras(args);

        if (progressDialog!=null && progressDialog.isShowing())
            progressDialog.hide();
        email_sign_in_button.setEnabled(true);
        startActivityForResult(i, Constants.REQUEST_SIGNUP_SUBSCRIBER);
        if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();
        Log.d(TAG, "Complete !!--------------image:"+u.getAvatarPath());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case PICK_IMAGE_ID:
                bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data);
                if (resultCode == Activity.RESULT_OK) {
                    image_profile_signup.setImageBitmap(bitmap);
                    //guardar solo el nombre de imagen
                    uriImage = ImagePicker.getUriBitmapSelected();
                    nameImage = ImagePicker.getRealPathFromURI(header_imageview.getContext(), ImagePicker.getUriBitmapSelected());//ImagePicker.getImageName(ImagePicker.getRealPathFromURI(header_imageview.getContext(), ImagePicker.getUriBitmapSelected()));//
                    //Log.d(TAG, "avatar local a subir:" + nameImage+ " name Image: "+ ImagePicker.getImageName(nameImage) + " external cache dir:"+getActivity().getExternalCacheDir().getPath());
                }else{
                    //Log.d(TAG, "avatar no seleccionado");
                    nameImage="";bitmap=null;}
                // TODO use bitmap
                break;
            case Constants.REQUEST_SIGNUP_SUBSCRIBER:
                if (resultCode == Activity.RESULT_OK) {
                    //Log.d(TAG, "Exito registro subscriptor..");
                    getActivity().setResult(Constants.SIGNUP_SUBSCRIBER_OK, null);
                    getActivity().finish();
                }
                /*else if(resultCode == Activity.RESULT_CANCELED){
                    getActivity().setResult(Activity.RESULT_CANCELED, null);
                    getActivity().finish();
                }*/
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
    private void attempSignup(){

        Log.d(TAG,"Registrando...");
        String name = text_name_signup.getText().toString().trim();
        String first = text_first_signup.getText().toString().trim();
        String last = text_last_signup.getText().toString().trim();
        String email = text_email.getText().toString().trim();
        String password_signup = text_password_signup.getText().toString().trim();
        //String password_confirm = text_password_confirm.getText().toString();


        HashMap<String,String> params = new HashMap<>();
        params.put("method","signupUser");
        params.put("name",name);
        params.put("first", first);
        params.put("last",last);
        params.put("email",email);
        try {
            params.put("password",AESCrypt.encrypt(Constants.seedValue,password_signup));//aqui
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            Toast.makeText(getActivity(), "Error de encripcion, reporte a soporte@estrategiamovilmx.com", Toast.LENGTH_SHORT);
            return;
        }
        params.put("categories",ApplicationPreferences.getLocalStringPreference(getActivity(), Constants.categoriesUser));
        params.put("avatar_path",(uriImage!=null)?uriImage.toString():"");//nameImage
        params.put("name_image",nameImage!=null && nameImage.length()>0?ImagePicker.getImageName(nameImage):"");
        String token = FirebaseInstanceId.getInstance().getToken();
        String localtoken = ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.firebase_token);
        //Log.d(TAG,"token:"+token + " en preferencias tiene: " + localtoken);
        params.put("token",token );
        VolleyPostRequest(Constants.USERS,params,METHOD_CALLBACK_SIGNUP);
    }
    private void processingUserValid(JSONObject response){
        //Log.d(TAG, "processingUserValid:" + response.toString() + " flow:" +flow);
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    JSONObject exist = response.getJSONObject("exist");
                    String result = exist.getString("exist");
                    if (result.equals("0")){
                        if (flow == METHOD_CALLBACK_SIGNUP)
                            attempSignup();
                        else if(flow == METHOD_CALLBACK_USER_IS_ENTERPRICE){
                            saveTempValues();
                        }

                    }else{
                        JSONObject o = new JSONObject();o.put("message","Ya existe un usuario registrado con ese correo.");
                        onSignupFailed(o);
                    }
                    break;
                case "2":
                    onSignupFailed(response);

                    break;
            }
        } catch (JSONException e) {
            JSONObject o = new JSONObject();
            try {
                o.put("message","El servicio no esta disponible por el momento.");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            onSignupFailed(o);
        }

    }
    private void processDeleteUser(JSONObject response){
        Log.d(TAG, "processDeleteUser:" + response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    JSONObject result = response.getJSONObject("result");
                    Log.d(TAG,"Usuario eliminado");
                    initProcess(false);
                    Toast.makeText(getActivity(), "Intenta de nuevo...", Toast.LENGTH_SHORT);
                    break;
                case "2":
                    initProcess(false);
                    Log.d(TAG,"Falló al eliminar usuario");
                    Toast.makeText(getActivity(), "Algo salió mal, intenta de nuevo...", Toast.LENGTH_SHORT);
                    break;
            }
        } catch (JSONException e) {
            initProcess(false);
            JSONObject o = new JSONObject();
            try {
                o.put("message","El servicio no esta disponible por el momento.");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            onSignupFailed(o);
        }

    }
    private void attemptExistUser(){
        email_sign_in_button.setEnabled(false);
        if (flow == METHOD_CALLBACK_USER_IS_ENTERPRICE)
            createProgressDialog("Espere...");
        else if(flow == METHOD_CALLBACK_SIGNUP)
            createProgressDialog("Registrando...");

        StringBuffer url = new StringBuffer();
        String user= text_email.getText().toString().trim();
        url.append(Constants.USERS + "?method=existUser" +"&userLogin="+user);
        VolleyGetRequest(url.toString(), METHOD_CALLBACK_USER_VALID);
    }
    public void signup() {

        if (!validate()) {
            return;
        }
        flow = METHOD_CALLBACK_SIGNUP;
        attemptExistUser();
    }
    public void VolleyPostRequest(String url, final HashMap<String, String> args,final int callback) {
        JSONObject jobject = new JSONObject(args);
        Log.d(TAG, "VolleyPostRequest:" + jobject.toString());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar la respuesta del servidor
                        if (callback == METHOD_CALLBACK_SIGNUP) {
                            processResponse(response);
                        }else if(callback == METHOD_CALLBACK_DELETE_USER){
                            processDeleteUser(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                        email_sign_in_button.setEnabled(true);
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Algo salió mal, intenta de nuevo...", Toast.LENGTH_SHORT);

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
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                request
        );
    }
    public boolean validate() {
        boolean valid = true;

        String name = text_name_signup.getText().toString();
        String first = text_first_signup.getText().toString();
        String last = text_last_signup.getText().toString();
        String email = text_email.getText().toString();
        String password = text_password_signup.getText().toString();
        //String passwordConf = text_password_confirm.getText().toString();

        if (name.isEmpty() || name.length() <= 3) {
            text_name_signup.setError("Nombre demasiado corto");
            valid = false;
        } else {
            text_name_signup.setError(null);
        }

        if (first.isEmpty() || first.length() <= 3) {
            text_first_signup.setError(getString(R.string.error_field_required_2));
            valid = false;
        } else {
            text_first_signup.setError(null);
        }


        if (password.isEmpty()){
            text_password_signup.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            text_password_signup.setError(null);
        }
        if (!(password.length() >= 4 && password.length() < 20)){
            text_password_signup.setError(getString(R.string.error_invalid_password));
            valid = false;
        } else {
            text_password_signup.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            text_email.setError("Email inválido");
            valid = false;
        } else {
            text_email.setError(null);
        }
        if (!checkbox_conditions.isChecked()){
            valid = false;
            Snackbar snackbar = Snackbar.make(checkbox_conditions, getResources().getString(R.string.error_conditions_required), Snackbar.LENGTH_LONG);
            snackbar.show();
            return valid;
        }

        return valid;
    }
    public void onSignupSuccess() {
        //upload avatar image to server
        if (bitmap!=null) { //usuario selecciono avatar
            String nameImageToServer = signup_response.getNewUserId()+ "_" + ImagePicker.getImageName(nameImage);
            Log.d(TAG,"nombre final imagen: " + nameImageToServer);
            Log.d(TAG,"bitmap: " + bitmap.getByteCount());
            UploadImage.uploadImage(getActivity(), nameImageToServer , bitmap);
            start();
        }else {
            endActivity();
        }
    }
    private void endActivity(){
        email_sign_in_button.setEnabled(true);
        //progressDialog.hide();
        getActivity().setResult(Activity.RESULT_OK, null);
        getActivity().finish();
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
                            endActivity();
                        }else if(UploadImage.ready==1 && UploadImage.error == 1){// hubo errores, reintentar...
                            email_sign_in_button.setEnabled(true);
                            if (progressDialog!=null && progressDialog.isShowing())
                                progressDialog.hide();
                            stop();
                            confirmAction(UploadImage.getMapError().size()+" "+getResources().getString(R.string.prompt_retry_promt),getResources().getString(R.string.prompt_uploaderror_promt),getResources().getString(R.string.prompt_action_retry),getResources().getString(R.string.prompt_cancel),PublishFragment.ALERT_RETRY);
                        }

                    }
                });


            }

        }, 0, PublishFragment.DURATION);
    }
    public void confirmAction(String message, String title, String positive, String negative,final int callback){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (callback == PublishFragment.ALERT_DISCART) {
                    createProgressDialog("Espere...");
                    reset();
                }else if (callback == PublishFragment.ALERT_RETRY){
                    initProcess(true);
                    HashMap map = UploadImage.getMapError();
                    if (map!=null) {
                        Log.d(TAG, "Reintentar con : " + map.size() + " imagenes");
                        UploadImage.error = 0;
                        UploadImage.ready = 0;
                        UploadImage.resetMapError();
                        UploadImage.uploadImage(getActivity(),  signup_response.getNewUserId() + "_" + ImagePicker.getImageName(nameImage), bitmap);
                        start();
                    }else{Log.d(TAG," lista errores null--------------");}
                }

            }
        });

        alertDialogBuilder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(LocationsActivity.this, "You clicked yes button", Toast.LENGTH_LONG).show();

                if (callback == PublishFragment.ALERT_RETRY){
                    deleteUser( signup_response.getNewUserId());
                }

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void deleteUser(int idUser){
        HashMap<String,String> params = new HashMap<>();
        params.put("method","removeUser");
        params.put("idUser", String.valueOf(idUser));
        VolleyPostRequest(Constants.REMOVE_USER, params,METHOD_CALLBACK_DELETE_USER);
    }
    private void reset(){
        text_email.setText("");
        text_password_signup.setText("");
        text_name_signup.setText("");
        text_first_signup.setText("");
        text_last_signup.setText("");
        email_sign_in_button.setEnabled(true);
        deleteUser(signup_response.getNewUserId());
    }
    private void initProcess(boolean flag){
        if (flag) {
            email_sign_in_button.setEnabled(false);
            createProgressDialog("Registrando...");
        }else{
            email_sign_in_button.setEnabled(true);
            if (progressDialog!=null && progressDialog.isShowing())
                progressDialog.hide();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
    }
    public void onSignupFailed(JSONObject object) {
        try{
            Toast.makeText(getContext(), object.getString("message"), Toast.LENGTH_LONG).show();
            email_sign_in_button.setEnabled(true);
        if (progressDialog!=null && progressDialog.isShowing())
            progressDialog.hide();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void createProgressDialog(String msg){
        progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        progressDialog.show();
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

                                        if (callbackFunction == METHOD_CALLBACK_SIGNUP)
                                            processResponse(response);
                                        else if (callbackFunction == METHOD_CALLBACK_USER_VALID){
                                            processingUserValid(response);
                                        }


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
                                        progressDialog.dismiss();
                                        email_sign_in_button.setEnabled(true);
                                    }
                                }

                        ).setRetryPolicy(new DefaultRetryPolicy(
                                Constants.MY_SOCKET_TIMEOUT_MS,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                );
    }

    private void processResponse(JSONObject response){
        // On complete call either onSignupSuccess or onSignupFailed
        // depending on success
        // save avatar image as preference and on remote server
        Log.d(TAG, response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    JSONObject object = response.getJSONObject("create");
                        if (object.getString("status").equals("1")){
                            signup_response = gson.fromJson(object.toString(), SignupResponse.class);
                            ApplicationPreferences.saveLocalPreference(getActivity(), Constants.localEmail, text_email.getText().toString());
                            ApplicationPreferences.saveLocalPreference(getActivity(),Constants.localUserId,signup_response.getNewUserId() + "");
                            ApplicationPreferences.saveLocalPreference(getActivity(), Constants.localAvatarPath, (bitmap != null) ? ImagePicker.getRealPathFromURI(header_imageview.getContext(), ImagePicker.getUriBitmapSelected()) : null);
                            try {
                                ApplicationPreferences.saveLocalPreference(getActivity(),
                                        Constants.localPassword, AESCrypt.encrypt(Constants.seedValue,text_password_signup.getText().toString().trim()));//aqui
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "Error al encriptar password: " + text_password_signup.getText().toString());
                                Toast.makeText(getActivity(),"Problema de encriptamiento, contacte a soporte",Toast.LENGTH_SHORT);
                                return;
                            }
                            Log.d(TAG, "preferences guardadas por Registro: " + ApplicationPreferences.getLocalStringPreference(getActivity(), Constants.localEmail) + "," +
                                    ApplicationPreferences.getLocalStringPreference(getActivity(), Constants.localUserId) + "," +
                                    ApplicationPreferences.getLocalStringPreference(getActivity(), Constants.localAvatarPath));
                            onSignupSuccess();
                        }else{//error,usuario ya existente , etc..
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



}
