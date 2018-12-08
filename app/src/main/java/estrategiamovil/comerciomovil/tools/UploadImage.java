package estrategiamovil.comerciomovil.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import estrategiamovil.comerciomovil.modelo.GenericResponse;
import estrategiamovil.comerciomovil.modelo.ImagePublication;

/**
 * Created by administrator on 22/07/2016.
 */
public class UploadImage {

    private static String KEY_IMAGE = "image";
    private static String KEY_NAME = "name";
    private static String KEY_PATH = "path";
    private static Context context;
    public static int ready = 0;
    public static int error = 0;
    private static ArrayList<String> nameImageUploaded=new ArrayList<>();
    private static int totalIterations;
    private static HashMap<String, ImagePublication> mapError = new HashMap<>();
    private static final String TAG = UploadImage.class.getSimpleName();
    public UploadImage() {

    }

    public static HashMap<String, ImagePublication> getMapError() {
        return mapError;
    }

    public static void resetMapError(){
        mapError = new HashMap<>();
    }
    public static void initUploadImageVariables(){
        UploadImage.ready = 0;
        UploadImage.error = 0;
        UploadImage.resetMapError();
    }
    private static String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static void uploadImage(Context ctx,final String nameImage,final Bitmap imageBitmap) {
        //Showing the progress dialog
        context = ctx;
        final ImagePublication imageIteration = new ImagePublication(imageBitmap,nameImage,nameImage);
        Log.d(TAG,"Imagen a subir....."+nameImage);
        //final ProgressDialog loading = ProgressDialog.show(context,"Guardando Perfíl...","Espere...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPLOAD_IMAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        // loading.dismiss();
                        //Showing toast message of the response

                        Gson gson = new Gson();
                        Log.d(TAG, "response:" + s.toString());
                        GenericResponse response = gson.fromJson(s, GenericResponse.class);
                        String status = response.getStatus();
                        String message = response.getMessage();
                        switch (status) {
                            case "1"://assign values
                                //nothign to show to user
                                Log.d(TAG, "message:" + message);
                                UploadImage.ready = 1;
                                error = 0;
                                Log.d(TAG, "Asignacion caso 1::  ready=" + UploadImage.ready + " error="+UploadImage.error + " map:"+UploadImage.getMapError().size());
                                resetMapError();
                                Log.d(TAG,"reset MapError !!");
                                break;
                            case "2"://login failed
                                mapError.put((imageIteration.getPath()),imageIteration);
                                UploadImage.ready = 1;
                                error = 1;
                                //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                break;
                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        UploadImage.ready = 1;
                        error = 1;
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(imageBitmap);
                //Getting Image Name
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, nameImage);

                //returning parameters
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
    public static void uploadVoucherImage(Context ctx,final String nameImage,final Bitmap imageBitmap) {
        //Showing the progress dialog
        context = ctx;
        final ImagePublication imageIteration = new ImagePublication(imageBitmap,nameImage,nameImage);
        Log.d(TAG,"Imagen a subir....."+nameImage);
        //final ProgressDialog loading = ProgressDialog.show(context,"Guardando Perfíl...","Espere...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPLOAD_VOUCHER_IMAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        // loading.dismiss();
                        //Showing toast message of the response

                        Gson gson = new Gson();
                        Log.d(TAG, "response:" + s.toString());
                        GenericResponse response = gson.fromJson(s, GenericResponse.class);
                        String status = response.getStatus();
                        String message = response.getMessage();
                        switch (status) {
                            case "1"://assign values
                                //nothign to show to user
                                Log.d(TAG, "message:" + message);
                                UploadImage.ready = 1;
                                error = 0;
                                Log.d(TAG, "Asignacion caso 1::  ready=" + UploadImage.ready + " error="+UploadImage.error + " map:"+UploadImage.getMapError().size());
                                resetMapError();
                                Log.d(TAG,"reset MapError !!");
                                break;
                            case "2"://login failed
                                mapError.put((imageIteration.getPath()),imageIteration);
                                UploadImage.ready = 1;
                                error = 1;
                                //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                break;
                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        UploadImage.ready = 1;
                        error = 1;
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(imageBitmap);
                //Getting Image Name
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, nameImage);

                //returning parameters
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public static void uploadImagesPublication(final Context ctx, final String id_new_publication, final HashMap<String, ImagePublication> map, final int total) {
        Log.d(TAG,"uploadImagesPublication.... total = "+total );
            totalIterations = total;
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                final ImagePublication imageIteration = (ImagePublication) pair.getValue();
                context = ctx;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPLOAD_IMAGES_PUBLICATIONS,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Gson gson = new Gson();
                                Log.d(TAG, "response Upload image:" + s);
                                GenericResponse response = gson.fromJson(s, GenericResponse.class);
                                String status = response.getStatus();
                                String message = response.getMessage();

                                switch (status) {
                                    case "1"://assign values

                                        Log.d(TAG, "Exito al subir imagen:" + message);
                                        nameImageUploaded.add(message);
                                        totalIterations = totalIterations - 1;
                                        Log.d(TAG, "1 totalIterations:" + (nameImageUploaded.size() + mapError.size()) + " total: " + total);
                                        if ((nameImageUploaded.size() + mapError.size()) == total) {
                                            UploadImage.ready = 1;
                                            Log.d(TAG, "Se procesaron todas las imagenes !!!!!!, Hay Errores ? : " + UploadImage.error + " numero de fallos: " + mapError.size());
                                        }
                                        break;
                                    case "2":
                                        mapError.put((imageIteration.getPath()), imageIteration);
                                        error = 1;
                                        //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                        Log.d(TAG, "Error al subir imagen:  " + imageIteration.getPath() + " iteracion: " + totalIterations + " lista errores: " + mapError.size());
                                        Log.d(TAG, "2 totalIterations:" + (nameImageUploaded.size() + mapError.size()) + " total: " + total);
                                        if ((nameImageUploaded.size() + mapError.size()) == total) {
                                            UploadImage.ready = 1;
                                            Log.d(TAG, "Se procesaron todas las imagenes !!!, Hay Errores ? : " + UploadImage.error + " numero de fallos: " + mapError.size());
                                        }
                                        break;
                                }


                            }


                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                //Dismissing the progress dialog
                                //  loading.dismiss();
                                mapError.put((imageIteration.getPath()), imageIteration);
                                error = 1;
                                ready = 1;
                                //Showing toast
                                Toast.makeText(context, volleyError.toString(), Toast.LENGTH_LONG).show();

                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        //Converting Bitmap to String
                        String image = getStringImage(imageIteration.getBitmap());
                        //Getting Image Name
                        //Creating parameters
                        Map<String, String> params = new Hashtable<String, String>();

                        //Adding parameters
                        params.put(KEY_IMAGE, image);
                        params.put(KEY_NAME, id_new_publication + "_" + imageIteration.getName());
                        //returning parameters
                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        Constants.MY_SOCKET_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    //Creating a Request Queue
                    RequestQueue requestQueue = Volley.newRequestQueue(context);
                    //Adding request to the queue
                    requestQueue.add(stringRequest);
        }
    }

}
