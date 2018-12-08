package estrategiamovil.comerciomovil.ui.fragments;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Ask;
import estrategiamovil.comerciomovil.modelo.DetailPublication;
import estrategiamovil.comerciomovil.modelo.ImageSliderPublication;
import estrategiamovil.comerciomovil.modelo.LoginResponse;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.tools.StringOperations;
import estrategiamovil.comerciomovil.tools.UserLocalProfile;
import estrategiamovil.comerciomovil.tools.UtilPermissions;
import estrategiamovil.comerciomovil.tools.VolleyErrorHelper;
import estrategiamovil.comerciomovil.ui.activities.BusinessInfoActivity;
import estrategiamovil.comerciomovil.ui.activities.GalleryActivity;
import estrategiamovil.comerciomovil.ui.activities.ShowConditionsActivity;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailAdsFragment extends Fragment implements ViewPager.OnPageChangeListener {
private static final String TAG = DetailAdsFragment.class.getSimpleName();
private HashMap<String, String> params;
private Gson gson = new Gson();
private LinearLayout principal_container_detail;
private TextView text_cover;
private TextView text_ads_category;
private TextView text_ads_effectiveDate;
private TextView text_ads_price;
private TextView detailed_description;
private TextView text_location_info;
private AppCompatButton button_call;
private AppCompatButton  button_email;
private LinearLayout layout_drive;
private DetailPublication detail;
private ProgressBar pb;
private TextView text_business_name;
private LinearLayout layout_business_description;

private static int METHOD_GET_PUBLICATION_IMAGES = 1;
//Carroussell
private ViewPager viewPager;
private static final Integer DURATION = 3500;
private Timer timer = null;
private int position;
private ImageSliderPublication[] images_publication;

public DetailAdsFragment() {}

        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                // The request code used in ActivityCompat.requestPermissions()
                // and returned in the Activity's onRequestPermissionsResult()
                String[] PERMISSIONS = {Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE};

                if(!UtilPermissions.hasPermissions(getActivity(), PERMISSIONS)){
                        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, UtilPermissions.PERMISSION_ALL);
                }
        }


@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_detail_ads, container, false);
        //initialize reference to elemements
        text_business_name = (TextView) v.findViewById(R.id.text_business_name);
        layout_business_description = (LinearLayout) v.findViewById(R.id.layout_business_description);
        layout_drive = (LinearLayout) v.findViewById(R.id.layout_drive);
        detailed_description = (TextView) v.findViewById(R.id.detailed_description);
        text_cover = (TextView) v.findViewById(R.id.text_cover);
        text_ads_category = (TextView) v.findViewById(R.id.text_ads_category);
        text_ads_effectiveDate = (TextView) v.findViewById(R.id.text_ads_effectiveDate);
        text_ads_price = (TextView) v.findViewById(R.id.text_ads_price);
        text_location_info = (TextView) v.findViewById(R.id.text_location_info);
        principal_container_detail = (LinearLayout) v.findViewById(R.id.principal_container_detail);
        pb = (ProgressBar) v.findViewById(R.id.pbLoading_detail);
        pb.setVisibility(ProgressBar.VISIBLE);
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        pb.setVisibility(ProgressBar.VISIBLE);
        getPublicationImages(params.get("idPublication"));
        loadInformation();//llamar load information despues al finalizar consulta de imagenes
        button_call = (AppCompatButton) v.findViewById(R.id.button_call);
        button_email = (AppCompatButton) v.findViewById(R.id.button_email);
        ((FloatingActionButton)getActivity().findViewById(R.id.fav_add_favorite)).setVisibility(View.GONE);

        button_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        confirmAction("Tel: "+getDetail().getPhone(),getResources().getString(R.string.promt_call_title),getResources().getString(R.string.promt_button_call),getResources().getString(R.string.cancel),getDetail().getPhone());
                }
        });
        button_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        ShowEmail_Popup();
                        //composeEmail(new String[]{getDetail().getEmail()},"Tienes una pregunta de: "+getDetail().getCoverDescription(),"grosetep@gmail.com","rosama.coyotl@gmail.com");
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
        layout_business_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), BusinessInfoActivity.class);
                i.putExtra(BusinessInfoFragment.INFO_COMPANY,getDetail().getBusinessDescription());
                startActivity(i);
            }
        });

        return v;
        }
        private void ShowEmail_Popup() {

                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
                final View content = layoutInflaterAndroid.inflate(R.layout.send_email_layout, null);
                final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_template, null);
                LinearLayout fields = (LinearLayout)mView.findViewById(R.id.content);
                fields.addView(content);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());

                alertDialogBuilderUserInput.setView(mView);
                //load information
                TextView text_cover = (TextView)content.findViewById(R.id.text_cover);
                TextView text_company = (TextView)content.findViewById(R.id.text_company);
                final EditText text_name = (EditText) content.findViewById(R.id.text_name);
                final EditText text_email = (EditText)content.findViewById(R.id.text_email);
                final EditText text_phone = (EditText) content.findViewById(R.id.text_phone);
                final EditText text_message = (EditText) content.findViewById(R.id.text_message);
                final Switch switch_copy = (Switch) content.findViewById(R.id.switch_copy);
                final CheckBox checkbox_conditions = (CheckBox) content.findViewById(R.id.checkbox_conditions);
                TextView text_conditions = (TextView) content.findViewById(R.id.text_conditions);
                text_conditions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent intent = new Intent(getContext(), ShowConditionsActivity.class);
                                startActivity(intent);
                        }
                });

                text_cover.setText(getDetail().getCoverDescription());
                text_company.setText(getDetail().getCompany());

                //customize title
                ((TextView)mView.findViewById(R.id.text_title)).setText(getResources().getString(R.string.promt_title_send_email));
                ((TextView)mView.findViewById(R.id.text_title)).setBackgroundColor(Color.parseColor(Constants.colorTitle));
                ((TextView)mView.findViewById(R.id.text_title)).setTextColor(Color.parseColor(Constants.colorBackgroundEnabled));
                //populate data if user is logged
                LoginResponse login = UserLocalProfile.getUserProfile(getContext());
                if (login!=null){
                        text_name.setText(login.getName());
                        text_email.setText(login.getEmail());
                        text_phone.setText(login.getPhone());
                }


                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.action_send_email), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {

                                }
                        })

                        .setNegativeButton(getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogBox, int id) {
                                                dialogBox.cancel();
                                        }
                                });

                final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
                Button b = alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                boolean valid = true;
                                if (!checkbox_conditions.isChecked()){
                                        valid = false;
                                        Snackbar snackbar = Snackbar.make(checkbox_conditions, getResources().getString(R.string.error_conditions_required), Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                }
                                if (text_name.getText().toString().isEmpty()){
                                        text_name.setError("Campo obligatorio");
                                        valid = false;
                                } else {
                                        text_name.setError(null);
                                }


                                if (text_email.getText().toString().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(text_email.getText().toString()).matches()) {
                                        text_email.setError("Email inválido");
                                        valid = false;
                                } else {
                                        text_email.setError(null);
                                }


                                if (text_message.getText().toString().isEmpty()) {
                                        text_message.setError("Campo obligatorio");
                                        valid = false;
                                } else {
                                        text_message.setError(null);
                                }
                                if (valid) {
                                        Ask ask = new Ask();
                                        ask.setName_client(text_name.getText().toString());
                                        ask.setEmail_client(text_email.getText().toString());
                                        ask.setPhone_client(text_phone.getText().toString());
                                        ask.setMessage_client(text_message.getText().toString());
                                        ask.setCopy_client(switch_copy.isChecked()?String.valueOf(true):String.valueOf(false));
                                        ask.setCover_publication(detail.getCoverDescription());
                                        ask.setCompany_suscriptor(detail.getCompany());
                                        ask.setEmail_suscriptor(detail.getEmail());
                                        sendAskEmail(ask,alertDialogAndroid);
                                }

                        }
                });
        }
        private void sendAskEmail(Ask ask,AlertDialog dialog) {

                HashMap<String, String> params = new HashMap<>();
                params.put("method", "sendAsk");
                params.put("name_client", ask.getName_client());
                params.put("email_client", ask.getEmail_client());
                params.put("phone_client", ask.getPhone_client());
                params.put("message_client", ask.getMessage_client());
                params.put("copy_client", ask.getCopy_client());
                params.put("cover_publication", ask.getCover_publication());
                params.put("company_suscriptor", ask.getCompany_suscriptor());
                params.put("email_suscriptor", ask.getEmail_suscriptor());
                VolleyPostRequest(Constants.EMAIL, params,dialog);
        }
        public void processEmailResponse(JSONObject response,AlertDialog dialog){
                Log.d(TAG, response.toString());
                try {
                        // Obtener atributo "mensaje"
                        String status = response.getString("status");
                        switch (status) {
                                case "1":
                                        dialog.dismiss();
                                        showConfirmationMessage(getResources().getString(R.string.prompt_email_ask));
                                        break;
                                case "2":
                                        showConfirmationMessage(getResources().getString(R.string.prompt_email_sent_error));
                                        break;
                                default:
                                        showConfirmationMessage(getResources().getString(R.string.prompt_email_sent_error));
                                        break;
                        }
                } catch (JSONException e) {
                        e.printStackTrace();
                        showConfirmationMessage(getResources().getString(R.string.prompt_email_sent_error));
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
        public void VolleyPostRequest(String url, HashMap<String, String> params,final AlertDialog dialog) {
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
                                processEmailResponse(response,dialog);


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
        /*public void composeEmail(String[] addresses, String subject,String cc,String bcc) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_CC, new String[]{ cc});
                intent.putExtra(Intent.EXTRA_BCC, new String[]{bcc});
                intent.putExtra(Intent.EXTRA_TEXT, "<h1>Mensage....  <strong></strong></h1>");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                }
        }*/
        public DetailPublication getDetail() {
                return detail;
        }

        private void makeCall(String phone){
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+phone));
                startActivity(intent);
        }
        public void confirmAction(String message, String title, String positive, String negative,final String phone){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage(message);
                if (!title.equals(""))
                        alertDialogBuilder.setTitle(title);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                                try {
                                        if (phone!=null && !phone.equals(""))
                                                makeCall(phone);
                                        else
                                                Toast.makeText(getContext(),getResources().getString(R.string.promt_error_phone),
                                                        Toast.LENGTH_SHORT).show();
                                }catch(Exception e) {
                                        Toast.makeText(getContext(),"Problema con la llamada...",
                                                Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                }
                        }
                });

                alertDialogBuilder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
        }
        private void setupViewPager(ViewPager viewPager,ImageSliderPublication[] images) {
        viewPager.setAdapter(new CardsPagerAdapter(images));
        viewPager.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                        Log.d(TAG, "onDrag...");
                        return false;
                        }
                        });
        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
        }

        public void loadInformation() {
                // Añadir parámetro a la URL del web service
                String newURL = Constants.GET_PUBLICATIONS + "?method=getDetailPublication" + "&idPublication=" + params.get("idPublication");
                Log.d(TAG, "newURL:" + newURL);
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

                VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                        new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        jobject,
                        new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                                // Procesar la respuesta del servidor
                                                if (callback == METHOD_GET_PUBLICATION_IMAGES)
                                                   setArrayImages(response);

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
                        public String getBodyContentType() {return "application/json; charset=utf-8" + getParamsEncoding();     }
                        }
                );
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
                                        if (viewPager != null) {// se ejcuta despues de gaber consultado
                                                setupViewPager(viewPager,images_publication);
                                                if (getActivity()!=null) {
                                                    ((TextView) getActivity().findViewById(R.id.text_number_photos)).setText("" + images_publication.length);
                                                    start();
                                                    Log.d(TAG, "start:----------");
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
        Log.d(TAG, "response:" + response.toString());
        try {
                // Obtener atributo "mensaje"
                String message = response.getString("status");
                switch (message) {
                case "1"://assign values
                        JSONObject object = response.getJSONObject("details");
                        detail = gson.fromJson(object.toString(), DetailPublication.class);
                        detailed_description.setText(detail.getImportant());
                        text_cover.setText(detail.getCoverDescription());
                        text_ads_category.setText(detail.getSubcategory());
                        String label_text ="";
                        if (detail.getEffectiveDate()!=null){
                            int days_created = Integer.parseInt(detail.getEffectiveDate());
                            switch (days_created){
                                case 0:label_text="Hoy";break;
                                case 1:label_text=getString(R.string.text_promt_in)+days_created+" día";break;
                                default:label_text=getString(R.string.text_promt_in)+days_created+" días";break;
                            }
                        }
                        text_ads_effectiveDate.setText(label_text);
                        text_ads_price.setText(StringOperations.getAmountFormat(detail.getOfferPrice()));
                        text_location_info.setText(detail.getGoogleAddress() + ". " + detail.getReference());
                        text_business_name.setText(detail.getCompany());
                        principal_container_detail.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);
                        break;
                case "2"://no detail
                        pb.setVisibility(View.GONE);
                        String errorMessage = response.getString("message");
                        Toast.makeText( getActivity(),  errorMessage, Toast.LENGTH_SHORT).show();
                        break;
                }

        } catch (JSONException e) {
                e.printStackTrace();
                pb.setVisibility(View.GONE);
                String errorMessage = getString(R.string.generic_error);
                Toast.makeText( getActivity(),  errorMessage, Toast.LENGTH_SHORT).show();
        }

        }

@Override
public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        }

public static DetailAdsFragment createInstance(HashMap<String, String> arguments) {
        DetailAdsFragment fragment = new DetailAdsFragment();
        fragment.setParams(arguments);
        return fragment;
        }

public void setParams(HashMap<String, String> params) {
        this.params = params;
        }



private void getPublicationImages(String idPublication){
        HashMap<String,String> params = new HashMap<>();
        //text
        params.put("method","getImagesByIdPublication");
        params.put("idPublication", idPublication);
        VolleyPostRequest(Constants.GET_PUBLICATIONS, params,METHOD_GET_PUBLICATION_IMAGES);
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
        if (getActivity()!=null) {
            timer.scheduleAtFixedRate(new TimerTask() {

                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            if (images_publication != null) {
                                if (position > images_publication.length) { // longitud del arreglo de urls
                                    position = 0;
                                    viewPager.setCurrentItem(position++);
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

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

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
        ImageSliderPublication temp =  gallery[position];
        Glide.with(cardImageView.getContext())
                .load(temp.getPath()+temp.getImageName())
                .centerCrop()
                .into(cardImageView);

        cardImageView.setTag(position);
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