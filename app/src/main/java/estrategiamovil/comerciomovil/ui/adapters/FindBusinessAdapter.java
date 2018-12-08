package estrategiamovil.comerciomovil.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.BusinessInfo;
import estrategiamovil.comerciomovil.modelo.GenericResponse;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.ui.activities.LoginActivity;
import estrategiamovil.comerciomovil.ui.activities.MerchantPublicationsActivity;
import estrategiamovil.comerciomovil.web.VolleySingleton;


/**
 * Created by administrator on 07/09/2016.
 */
public class FindBusinessAdapter extends RecyclerView.Adapter<FindBusinessAdapter.ViewHolder>  {
    private final String METHOD_TOPIC = "topic";
    //private final String METHOD_REVIEW = "review";
    private final List<BusinessInfo> business;
    private View v;
    private Activity activity;
    private static final String TAG = FindBusinessAdapter.class.getSimpleName();
    public FindBusinessAdapter(Activity act, List<BusinessInfo> myDataset) {
        business = new ArrayList<>(myDataset);
        activity=act;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {

        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.business_customer_info_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.idUser = business.get(position).getIdUser();
        holder.idPerson = business.get(position).getIdPerson();
        holder.dateRegister = business.get(position).getRegisterDate();
        String avatar =business.get(position).getImageRoute() + business.get(position).getNameImage();
        Glide.with(holder.image.getContext())
                .load(avatar)
                .centerCrop()
                .into(holder.image);
        holder.text.setText(business.get(position).getCompany());

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder,  position);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //depend of publication type then view detail publication(cupons, ads)
                Context context = v.getContext();
                Intent intent = new Intent(context, MerchantPublicationsActivity.class);
                BusinessInfo businessInfo = (BusinessInfo) business.get(position);
                intent.putExtra(MerchantPublicationsActivity.MERCHANT_OBJECT,businessInfo);
                context.startActivity(intent);
            }
        });
    }
    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(ViewHolder view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.overflow.getContext(), view.overflow);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_subscriber_business, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener().getInstance(view,position));
        popup.show();
    }
    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        ViewHolder holder;
        int position = 0;
        private void setPosition(int p){position = p;}
        private void setHolder(ViewHolder hldr){ holder = hldr;}

        public MyMenuItemClickListener getInstance(ViewHolder holder, int position){
            MyMenuItemClickListener l = new MyMenuItemClickListener();
            l.setHolder(holder);
            l.setPosition(position);
            return l;
        }
        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String topic = holder.idUser+"_"+holder.idPerson+"_"+holder.dateRegister;
            Log.d(TAG,"Se genero nombre de topic para registrar en base:"+topic);
            switch (menuItem.getItemId()) {
                case R.id.action_subscribe:
                    //subcribe and register in database
                    Log.d(TAG,"topic:"+topic);
                    SubscribeUserInTopic(topic,true);
                    break;
                case R.id.action_unsubscribe:
                    SubscribeUserInTopic(topic,false);
                    Log.d(TAG,"cancel:"+topic);
                    break;
                /*case R.id.action_rate:
                    ShowNotification_Popup(activity,activity.getString(R.string.text_promt_send_rate_title));
                    break;*/
                default:
            }
            return true;
        }


        /*private  void ShowNotification_Popup(final Activity activity,String title) {
            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(activity);
            final View content = layoutInflaterAndroid.inflate(R.layout.layout_send_rating, null);
            final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_template, null);
            LinearLayout fields = (LinearLayout)mView.findViewById(R.id.content);
            fields.addView(content);
            AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(activity);
            alertDialogBuilderUserInput.setView(mView);

            final RatingBar ratingBar = (RatingBar) mView.findViewById(R.id.ratingBar);
            final TextView text_message_rated = (TextView) mView.findViewById(R.id.text_message_rated);
            final TextView text_error_rated = (TextView) mView.findViewById(R.id.text_error_rated);
            final ProgressBar pbLoading_rate = (ProgressBar) mView.findViewById(R.id.pbLoading_rate);

            //customize title
            ((TextView)mView.findViewById(R.id.text_title)).setText(title);
            ((TextView)mView.findViewById(R.id.text_title)).setBackgroundColor(Color.parseColor(Constants.colorTitle));
            ((TextView)mView.findViewById(R.id.text_title)).setTextColor(Color.parseColor(Constants.colorBackgroundEnabled));
            AppCompatButton button = (AppCompatButton) mView.findViewById(R.id.button_done);
            //set content
            final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
            alertDialogAndroid.show();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean valid = true;
                    text_error_rated.setText("");
                    text_error_rated.setVisibility(View.GONE);
                    if (ratingBar.getNumStars()==0){
                        valid = false;
                        text_error_rated.setText(activity.getString(R.string.error_field_required));
                        return;
                    }else{
                        text_error_rated.setText("");
                    }
                    if (text_message_rated.getText().toString().trim().isEmpty()){
                        text_message_rated.setError(activity.getString(R.string.error_field_required));
                        valid = false;
                    }else{
                        text_message_rated.setError(null);
                    }
                    if (valid) {
                        pbLoading_rate.setVisibility(View.VISIBLE);
                        sendRate(mView,alertDialogAndroid);
                    }
                }
            });

        }*/

        /*private void sendRate(View mView,AlertDialog alertDialogAndroid){
            int rate = (int)((RatingBar)mView.findViewById(R.id.ratingBar)).getRating();
            String comment = ((EditText)mView.findViewById(R.id.text_message_rated)).getText().toString();
            HashMap<String,String> params = new HashMap<>();
            String idUser = ApplicationPreferences.getLocalStringPreference(activity,Constants.localUserId);
            params.put("idUser", idUser);
            params.put("comment", comment);
            params.put("rate",String.valueOf(rate));
            params.put("id_merchant", holder.idUser);
            params.put("method", "saveRate");

            VolleyPostRequest(Constants.GET_BUSINESS, params,alertDialogAndroid,mView,METHOD_REVIEW);
        }*/
    }
    public void open(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage("Es necesario que te registres para recibir notificaciones del Negocio seleccionado");
        alertDialogBuilder.setTitle("Inicio de Sesión Necesario");
        alertDialogBuilder.setPositiveButton("Iniciar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
            }
        });

        alertDialogBuilder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(LocationsActivity.this, "You clicked yes button", Toast.LENGTH_LONG).show();

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void SubscribeUserInTopic(String topic,boolean flag) {
        String idUser = ApplicationPreferences.getLocalStringPreference(activity,Constants.localUserId);
        if (idUser==null || idUser.equals("")) open();

        HashMap<String,String> params = new HashMap<>();
        params.put("method","SubscribeUserInTopic");
        params.put("topic",topic);
        params.put("idUser",idUser);
        params.put("flag",String.valueOf(flag));
        VolleyPostRequest(Constants.NOTIFICATIONS,params,null,null,METHOD_TOPIC);
    }
    public void VolleyPostRequest(String url, final HashMap<String, String> args, final AlertDialog alertDialogAndroid, final View mView,final String callback){
        JSONObject jobject = new JSONObject(args);
        Log.d(TAG,"VolleyPostRequest:" +  jobject.toString());
        JsonObjectRequest  request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar la respuesta del servidor
                        /*if (callback == METHOD_REVIEW)
                            processResponseReview(response,alertDialogAndroid,mView);
                        else */
                        if(callback == METHOD_TOPIC)
                            processResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ((ProgressBar)mView.findViewById(R.id.pbLoading_rate)).setVisibility(View.GONE);
                        TextView text_error = (TextView)mView.findViewById(R.id.text_error_rated);
                        text_error.setText(activity.getString(R.string.text_promt_send_rate_error));
                        text_error.setVisibility(View.VISIBLE);
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

        VolleySingleton.getInstance(activity).addToRequestQueue(request);
    }
    /*public void processResponseReview(JSONObject response,AlertDialog dialog,View mView){
        Log.d(TAG, response.toString());
        TextView text_error = (TextView) mView.findViewById(R.id.text_error_rated);
        try {
            String status = response.getString("status");

            switch (status) {
                case "1":
                    dialog.dismiss();
                    JSONObject result = response.getJSONObject("result");
                    ShowConfirmations.showConfirmationMessage(result.get("message").toString(),activity);
                    break;
                case "2":
                    ((ProgressBar) mView.findViewById(R.id.pbLoading_rate)).setVisibility(View.GONE);
                    String msg = response.getString("message");
                    text_error.setText(msg);
                    text_error.setVisibility(View.VISIBLE);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ((ProgressBar) mView.findViewById(R.id.pbLoading_rate)).setVisibility(View.GONE);
            text_error.setText(activity.getString(R.string.text_promt_send_rate_error));
            text_error.setVisibility(View.VISIBLE);
        }
    }*/
    private void processResponse(JSONObject response){
        Log.d(TAG, "Register device in Topic:"+response.toString());
        try {
            String status = response.getString("status");
            Gson gson = new Gson();
            GenericResponse result = null;
            JSONObject object = response.getJSONObject("result");
            switch (status) {
                case "1":
                    if (object.getString("status").equals("1")) {
                        result = gson.fromJson(object.toString(), GenericResponse.class);
                        Log.d(TAG,result.getMessage());
                        ShowConfirmations.showConfirmationMessage(result.getMessage(),activity);
                    }
                    break;
                case "2":
                    Log.d(TAG,"No se realizo la operacion.");
                    result = gson.fromJson(object.toString(), GenericResponse.class);
                    ShowConfirmations.showConfirmationMessage(result.getMessage(),activity);
                    break;

                default:
                    ShowConfirmations.showConfirmationMessage(activity.getResources().getString(R.string.generic_error),activity);
                    break;

            }
        } catch (JSONException e) {
            e.printStackTrace();
            ShowConfirmations.showConfirmationMessage(activity.getResources().getString(R.string.generic_error),activity);
        }
    }
    @Override
    public int getItemCount() {
        return business.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView overflow;
        public ImageView image;
        public TextView text;
        public String idUser;
        public String idPerson;
        public String dateRegister;


        public ViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            text = (TextView) v.findViewById(R.id.text);
            overflow = (ImageView) v.findViewById(R.id.overflow);
            idUser = dateRegister = idPerson = "";
        }
    }


    public void animateTo(List<BusinessInfo> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<BusinessInfo> newModels) {
        for (int i = business.size() - 1; i >= 0; i--) {
            final BusinessInfo model = business.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<BusinessInfo> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final BusinessInfo model = newModels.get(i);
            if (!business.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<BusinessInfo> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final BusinessInfo model = newModels.get(toPosition);
            final int fromPosition = business.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public BusinessInfo removeItem(int position) {
        final BusinessInfo model = business.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, BusinessInfo model) {
        business.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final BusinessInfo model = business.remove(fromPosition);
        business.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

}
