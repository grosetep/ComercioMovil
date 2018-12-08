package estrategiamovil.comerciomovil.ui.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import estrategiamovil.comerciomovil.tools.UploadImage;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.BooleanResult;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.ImagePublication;
import estrategiamovil.comerciomovil.modelo.ImageSliderPublication;
import estrategiamovil.comerciomovil.tools.Connectivity;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ImagePicker;
import estrategiamovil.comerciomovil.ui.activities.DetailPublicationActivity;
import estrategiamovil.comerciomovil.ui.adapters.UpdateImagesAdapter;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditImagesFragment extends Fragment {
    private String id_publication;
    private ProgressBar pbLoading_images;
    private ProgressDialog progressDialog;
    private StaggeredGridLayoutManager llmg;
    private RecyclerView recycler_edit_images;
    private LinearLayout layout_change_images;
    private AppCompatButton button_continue;
    private ArrayList<ImageSliderPublication> images_publication = new ArrayList<>();
    private ArrayList<String> images_to_remove = new ArrayList<>();
    private static final String TAG = EditImagesFragment.class.getSimpleName();
    private UpdateImagesAdapter mAdapter;
    private static final int PICK_IMAGE_ID = 1;
    public static final int ALERT_DISCART = 0;
    public static final int ALERT_RETRY = 1;
    public static final int METHOD_UPDATE_IMAGES = 3;
    public static final Integer DURATION = 1000;
    public static final String MAIN_IMAGE_SELECTED = "main_image";
    private Timer timer = null;
    private HashMap<String,ImagePublication> image_recently_added = new HashMap<>();
    public EditImagesFragment() {
        // Required empty public constructor
    }
    public static EditImagesFragment createInstance(String id_publication) {
        EditImagesFragment fragment = new EditImagesFragment();
        Bundle args = new Bundle();
        args.putString(DetailPublicationActivity.EXTRA_PUBLICACION,id_publication);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            setId_publication(getArguments().getString(DetailPublicationActivity.EXTRA_PUBLICACION));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_images, container, false);
        initGUI(v);
        if (getId_publication()!=null) {
            if (Connectivity.isNetworkAvailable(getContext())) {
                getPublicationImages(getId_publication());
            }else{
                Toast.makeText(
                        getActivity(),
                        getString(R.string.generic_server_timeout),
                        Toast.LENGTH_SHORT).show();
    }
        }else{
            getActivity().onBackPressed();
        }
        return v;
    }
    private void initGUI(View v){
        pbLoading_images  = (ProgressBar) v.findViewById(R.id.pbLoading_images);
        pbLoading_images.setVisibility(View.VISIBLE);
        recycler_edit_images = (RecyclerView) v.findViewById(R.id.recycler_edit_images);
        recycler_edit_images.setItemAnimator(new DefaultItemAnimator());

        llmg = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        llmg.setOrientation(GridLayoutManager.VERTICAL);
        if (recycler_edit_images.getLayoutManager()==null){ recycler_edit_images.setLayoutManager(llmg);}
        button_continue = (AppCompatButton) v.findViewById(R.id.button_continue);
        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //upload images and then delete ald images
                Log.d(TAG,"DELETE IMAGES:"+images_to_remove.size());
                Log.d(TAG,"ADD NEW IMAGES:"+image_recently_added.size());
                Log.d(TAG,"IMAGE NAMES:"+getImagesNamesArray(image_recently_added));
                Log.d(TAG,"IMAGE IDS:"+getIdsImagePublication(images_to_remove));
                //execute procedure to insert new images publications and delete olds images, then upload new images...
                createProgressDialog(getString(R.string.promtp_payment_processing));
                if (image_recently_added.size()>0) {
                    UploadImage.uploadImagesPublication(getActivity(), getId_publication(), image_recently_added, image_recently_added.size());
                    start();
                }else{
                    updateImagesPublication();
                }
            }
        });
        layout_change_images = (LinearLayout) v.findViewById(R.id.layout_change_images);
        layout_change_images.setVisibility(View.GONE);

    }
    private void updateImagesPublication(){//create new images on DB, then upload images
        HashMap<String,String> params = new HashMap<>();
        params.put("method","updateImagesPublication");
        params.put("idPublication", getId_publication());
        params.put("new_imageNames", getImagesNamesArray(image_recently_added));
        params.put("old_image_ids",getIdsImagePublication(images_to_remove) );
        params.put("new_image_counter",String.valueOf(image_recently_added.size()));
        params.put("old_image_counter",String.valueOf(images_to_remove.size()));
        VolleyPostRequest(Constants.GET_PUBLICATIONS, params,METHOD_UPDATE_IMAGES);
    }
    private String getIdsImagePublication(ArrayList<String> list) {
        StringBuffer buffer = new StringBuffer();
        if (list.size()>0) {
            if (list.size()==1) {
                buffer.append(list.get(0));
            }
            else {
                for (int i = 0; i < list.size(); i++) {
                    String id = list.get(i);
                    if (i == list.size()) {
                        buffer.append(id);
                    } else {
                        buffer.append(id.concat(","));
                    }
                }
                //delete las comma
                buffer.deleteCharAt(buffer.length()-1);
            }
        }
        return buffer.toString();
    }
    private String getImagesNamesArray(HashMap<String,ImagePublication> image_list) {
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
                            updateImagesPublication();
                        }else if(UploadImage.ready==1 && UploadImage.error == 1){// hubo errores, reintentar...
                            closeDialog();
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
    public void confirmAction(String message, String title, String positive, String negative,final int callback){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (callback == ALERT_DISCART) {
                    //nothing
                }else if (callback == ALERT_RETRY){
                    createProgressDialog("Actualizando publicación");
                    HashMap map = UploadImage.getMapError();
                    if (map!=null) {
                        Log.d(TAG, "Reintentar con : " + map.size() + " imagenes");
                        UploadImage.error = 0;
                        UploadImage.ready = 0;
                        UploadImage.resetMapError();
                        UploadImage.uploadImagesPublication(getActivity(), getId_publication(), map, map.size());
                        start();
                    }else{Log.d(TAG," lista errores null--------------");}
                }

            }
        });

        alertDialogBuilder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Negative", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void removeAndUpdateList(){
        ArrayList<ImageSliderPublication> imagesToRemove =  mAdapter.getImages_to_remove();
        if (imagesToRemove.size()>0){
            for(ImageSliderPublication image:imagesToRemove){
                Log.d(TAG,"BORRAR "+image.getImageName()+ " de la lista a subir");
                if (image.getResource().equals(Constants.resource_remote)) {
                    images_to_remove.add(image.getIdPathImage());
                }else if(image.getResource().equals(Constants.resource_local)){
                    image_recently_added.remove(image.getPath());
                }

                images_publication.remove(image);
            }
        }
        //reset values and update
        mAdapter.resetImagesToRemove();
        mAdapter.notifyDataSetChanged();
    }
    public void addNewPhotoCheck(){
        if (images_publication.size()<Constants.publication_images_number && !existAddPhoto()) {
            images_publication.add(null);
            mAdapter.notifyItemInserted(images_publication.size()-1);
        }else{
            //check if exist element null
            if (images_publication.size()>Constants.publication_images_number && existAddPhoto()) {
                deleteAddPhotoElement();
            }else{
                //elimina elemento y agregarlo siempre al final
                deleteAddPhotoElement();
                images_publication.add(null);
                mAdapter.notifyItemInserted(images_publication.size()-1);
            }
        }
    }
    private boolean existAddPhoto() {
        boolean exist = false;
        for (int i = 0; i < images_publication.size(); i++) {
            if (images_publication.get(i) == null)
                exist = true;
        }
        return exist;
    }
    private void deleteAddPhotoElement(){
        int index_element_null =-1;
        for (int i = 0;i<images_publication.size();i++){
            if (images_publication.get(i)==null){
                index_element_null = i;
                break;
            }
        }
        if (index_element_null!=-1){//element null exist
            images_publication.remove(index_element_null);
            mAdapter.notifyDataSetChanged();
        }
    }
    public void enableDeletion(boolean flag){
        if (flag) {
            for (int i = 0; i < images_publication.size(); i++) {
                ImageSliderPublication image = images_publication.get(i);
                if (image != null) {
                    image.setEnableDeletion(String.valueOf(flag));
                } else {
                    images_publication.remove(i);
                }
            }
            layout_change_images.setVisibility(View.GONE);
        }else{
            for (ImageSliderPublication image:images_publication){
                image.setEnableDeletion(String.valueOf(flag));
            }
        }
        mAdapter.notifyDataSetChanged();
    }
    public void enableContinueValidation(){
        if ((images_to_remove.size()>0 || image_recently_added.size()>0)){//hubo cambios
            if ( (images_publication.size()>=2 && images_publication.size()==Constants.publication_images_number)){//todas llenas
                layout_change_images.setVisibility(View.VISIBLE);
            }else if(images_publication.size()>=2 &&  images_publication.size()<Constants.publication_images_number && existAddPhoto() ){//minimo una imagen seleccionada
                layout_change_images.setVisibility(View.VISIBLE);
            }
        }else{layout_change_images.setVisibility(View.GONE);}
    }
    public String getId_publication() {
        return id_publication;
    }
    public void setId_publication(String id_publication) {
        this.id_publication = id_publication;
    }
    private void getPublicationImages(String idPublication){
        HashMap<String,String> params = new HashMap<>();
        //text
        params.put("method","getImagesByIdPublication");
        params.put("idPublication", idPublication);
        VolleyPostRequest(Constants.GET_PUBLICATIONS, params,DetailPublicationFragment.METHOD_GET_PUBLICATION_IMAGES);
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
                        if (callback == DetailPublicationFragment.METHOD_GET_PUBLICATION_IMAGES)
                            setArrayImages(response);
                        else if (callback == METHOD_UPDATE_IMAGES) {
                            processResponseUpdate(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                        closeDialog();
                        Toast.makeText(
                                getActivity(),
                                getString(R.string.generic_server_timeout),
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
    private void processResponseUpdate(JSONObject response){
        Log.d(TAG, response.toString());

        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    Gson gson = new Gson();
                    JSONObject result = response.getJSONObject("result");
                    closeDialog();
                    onSuccess(response);
                    break;
                case "2":
                case "3":
                case "4":
                default:
                    onFailed(response);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            closeDialog();
            Toast.makeText(
                    getActivity(),
                    getString(R.string.generic_server_timeout),
                    Toast.LENGTH_SHORT).show();
        }

    }
    public void onSuccess(JSONObject object) {
        //get first image and passing by parameter
        ImageSliderPublication image_cover = images_publication.get(0);
        Intent return_data = new Intent();
        return_data.putExtra(MAIN_IMAGE_SELECTED,image_cover);
        getActivity().setResult(Activity.RESULT_OK, return_data);
        getActivity().finish();
    }

    private void onFailed(JSONObject object){
        try{
            Toast.makeText(getContext(), object.getString("message"), Toast.LENGTH_LONG).show();
            button_continue.setEnabled(true);
            closeDialog();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void setArrayImages(JSONObject response){
        ArrayList<ImageSliderPublication> new_elements = null;
        Gson gson = new Gson();
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    JSONArray result = response.getJSONArray("result");
                    new_elements = new ArrayList<>(Arrays.asList(gson.fromJson(result.toString(), ImageSliderPublication[].class)));
                    if (new_elements!= null && new_elements.size() > 0) {
                        images_publication.addAll(new_elements);
                    }
                    if (images_publication.size()<Constants.publication_images_number){images_publication.add(null);}//add new photo layout
                    setupAdapter();
                    pbLoading_images.setVisibility(View.GONE);

                    break;
                case "2":
                    Toast.makeText(
                            getActivity(),
                            getString(R.string.generic_server_timeout),
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(
                    getActivity(),
                    getString(R.string.generic_server_timeout),
                    Toast.LENGTH_SHORT).show();
        }

    }
    private void setupAdapter(){
        // Set the adapter
        mAdapter = new UpdateImagesAdapter(images_publication,getActivity(),this);
        recycler_edit_images.setAdapter(mAdapter);
        if (recycler_edit_images.getLayoutManager()==null){
            recycler_edit_images.setLayoutManager(llmg);
        }
        pbLoading_images.setVisibility(View.GONE);
    }
    public void pickImage(){
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity(),getString(R.string.pick_image));
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }
    private void closeDialog(){
         if (progressDialog!=null && progressDialog.isShowing())
            progressDialog.hide();
    }
    private void createProgressDialog(String msg){
        progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        switch(requestCode) {
            case PICK_IMAGE_ID:
                Bitmap bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data);
                if (resultCode == Activity.RESULT_OK) {
                    String path = ImagePicker.getRealPathFromURI(getActivity(), ImagePicker.getUriBitmapSelected());
                    if (!image_recently_added.containsKey(path)) {
                        image_recently_added.put(path, new ImagePublication(bitmap, ImagePicker.getImageName(path), path));
                        //create ImageSlidePublication and ImagePublication list to load images
                        ImageSliderPublication new_image = new ImageSliderPublication();
                        new_image.setIdPathImage(null);
                        new_image.setPath(path);
                        new_image.setImageName(ImagePicker.getImageName(path));
                        new_image.setEnableDeletion(String.valueOf(false));
                        new_image.setResource(Constants.resource_local);
                        images_publication.add(new_image);
                        mAdapter.notifyItemInserted(images_publication.size()-1);
                        addNewPhotoCheck();
                        enableContinueValidation();
                    }else{
                        Snackbar snackbar = Snackbar.make(button_continue,getResources().getString(R.string.text_prompt_image_exists),Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
                break;





            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
