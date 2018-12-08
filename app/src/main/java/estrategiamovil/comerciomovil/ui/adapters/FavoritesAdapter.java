package estrategiamovil.comerciomovil.ui.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import android.widget.ProgressBar;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.DetailPublication;
import estrategiamovil.comerciomovil.modelo.Favorite;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.StringOperations;
import estrategiamovil.comerciomovil.tools.VolleyErrorHelper;
import estrategiamovil.comerciomovil.ui.activities.ReviewPayActivity;
import estrategiamovil.comerciomovil.ui.fragments.FavoritesFragment;
import estrategiamovil.comerciomovil.ui.fragments.ReviewPayFragment;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * Created by administrator on 08/11/2016.
 */
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder>   {
    private List<Favorite> favorites;
    private Activity activity;
    private FavoritesFragment fragment;
    private static final String TAG = FavoritesAdapter.class.getSimpleName();
    public FavoritesAdapter(List<Favorite> myDataset, Activity activity, FavoritesFragment fragment) {
        favorites = myDataset;
        this.activity = activity;
        this.fragment = fragment;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorites_item_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Glide.with(holder.image.getContext())
                .load(favorites.get(position).getPath() + favorites.get(position).getImage())
                .centerCrop()
                .into(holder.image);
        holder.text_coverDescription.setText(favorites.get(position).getCoverDescription());
        holder.text_regular_price.setText(StringOperations.getAmountFormat(favorites.get(position).getRegularPrice()));
        holder.text_offer_price.setText(StringOperations.getAmountFormat(favorites.get(position).getOfferPrice()));
        String days_active = favorites.get(position).getDays();
        if (days_active!=null && !days_active.equals("")) {
            int num_days = Integer.parseInt(days_active);
            if (num_days >= 0) {
                holder.text_days_active.setText(num_days==0?activity.getResources().getString(R.string.text_promt_today):
                activity.getResources().getString(R.string.text_promt_in)+days_active + activity.getResources().getString(R.string.text_promt_days_active));
                holder.button_fav_buy.setVisibility(View.VISIBLE);
                holder.button_fav_buy_disabled.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadInformation(favorites.get(position));

                    }
                });
            }else{
                holder.text_days_active.setText("Expirada");
                holder.button_fav_buy.setVisibility(View.GONE);
                holder.button_fav_buy_disabled.setVisibility(View.VISIBLE);
            }

        }
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder,  position);
            }
        });
    }
    private void showPopupMenu(ViewHolder view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.overflow.getContext(), view.overflow);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_favorites, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener().getInstance(view,position));
        popup.show();
    }
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
            switch (menuItem.getItemId()) {
                case R.id.action_delete_favorite:
                    removeFromCart(favorites.get(position),position);
                    return true;
                default:
            }
            return false;
        }


    }
    private void showConfirmationMessage(final String message){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity,message,Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void confirmProductRemoved(JSONObject response,int position){
        Log.d(TAG,"confirmProductRemoved: "+ response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");

            switch (status) {
                case "1":
                    JSONObject object = response.getJSONObject("result");
                    showConfirmationMessage(object.getString("message"));
                    fragment.getFavoritesByUser();
                    Log.d(TAG,object.getString("message"));
                    break;
                case "2":
                    showConfirmationMessage(response.getJSONObject("message").toString());
                    Log.d(TAG,response.getJSONObject("message").toString());
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                showConfirmationMessage(response.getJSONObject("message").toString());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

    }

    public void removeFromCart(Favorite favorite,int position){
        Log.d(TAG,"Remove cart");
        String idUser = ApplicationPreferences.getLocalStringPreference(activity,Constants.localUserId);
        HashMap<String,String> params = new HashMap<>();
        params.put("method", "addFavorite");
        params.put("idUser", idUser);
        params.put("idPublication", favorite.getIdPublication());
        params.put("flag", "false");
        VolleyPostRequest(Constants.GET_FAVORITES, params,position);
    }
    public void VolleyPostRequest(String url, HashMap<String, String> params,final int position){
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
                        confirmProductRemoved(response,position);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                        //dismissProgressDialog();
                        String mensaje2 = "Verifique su conexión a Internet.";
                        Toast.makeText(
                                activity,
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
        VolleySingleton.getInstance(activity).addToRequestQueue(request);
    }
    public void loadInformation(final Favorite favorite) {

        // Añadir parámetro a la URL del web service
        String newURL = Constants.GET_PUBLICATIONS + "?method=getDetailPublication" + "&idPublication=" + favorite.getIdPublication();
        Log.d(TAG, "newURL:" + newURL);
        // Realizar petición GET_BY_ID
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                newURL,
                (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar la respuesta Json
                        processingResponse(response,favorite);
                        //  pDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        String message = VolleyErrorHelper.getErrorType(error, activity);
                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                        Toast.makeText(
                               activity,
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
                getInstance(activity).
                addToRequestQueue(request
                );
    }
    private void onDetailCreated(DetailPublication detail,Favorite favorite){
        Intent intent = new Intent(activity, ReviewPayActivity.class);
        Bundle b = new Bundle();
        b.putSerializable(Constants.user_cupon_selected,detail);
        b.putString(ReviewPayFragment.EXTRA_PATH_FIRST_IMAGE,favorite.getPath());
        b.putString(ReviewPayFragment.EXTRA_NAME_FIRST_IMAGE,favorite.getImage());
        intent.putExtras(b);
        activity.finish();
        activity.startActivity(intent);
    }
    private void processingResponse(JSONObject response,Favorite favorite) {
        Log.d(TAG, "response:" + response.toString());
        try {
            // Obtener atributo "mensaje"
            String message = response.getString("status");
            switch (message) {
                case "1"://assign values
                    Gson gson = new Gson();
                    JSONObject object = response.getJSONObject("details");
                    DetailPublication detail = gson.fromJson(object.toString(), DetailPublication.class);
                    onDetailCreated(detail,favorite);
                    break;
                case "2"://no detail
                    String errorMessage = response.getString("message");
                    Toast.makeText(
                            activity,
                            errorMessage,
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public int getItemCount() {
        return favorites.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView overflow;
        public ImageView image;
        public TextView text_coverDescription;
        public TextView text_regular_price;
        public TextView text_offer_price;
        public TextView text_days_active;
        public TextView button_fav_buy;
        public TextView button_fav_buy_disabled;


        public ViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            text_coverDescription = (TextView) v.findViewById(R.id.text_coverDescription);
            text_regular_price = (TextView) v.findViewById(R.id.text_regular_price);
            text_offer_price = (TextView) v.findViewById(R.id.text_offer_price);
            text_days_active = (TextView) v.findViewById(R.id.text_days_active);
            button_fav_buy = (TextView) v.findViewById(R.id.button_fav_buy);
            button_fav_buy_disabled = (TextView) v.findViewById(R.id.button_fav_buy_disabled);
            text_coverDescription= (TextView) v.findViewById(R.id.text_coverDescription);
            overflow = (ImageView) v.findViewById(R.id.overflow);
        }
    }
}
