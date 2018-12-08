package estrategiamovil.comerciomovil.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.CustomerInfo;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * Created by administrator on 09/09/2016.
 */
public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.ViewHolder>  {
    private final List<CustomerInfo> customers;
    private Context context;
    private static final String TAG = CustomersAdapter.class.getSimpleName();
    public CustomersAdapter(Context ctx, List<CustomerInfo> myDataset) {
        customers = new ArrayList<>(myDataset);
        context=ctx;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.business_customer_info_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.idUser = customers.get(position).getIdUser();
        String avatar =customers.get(position).getImageRoute() + customers.get(position).getImageName();
        Log.d(TAG,"Cliente: " + avatar);
        Glide.with(holder.image.getContext())
                .load(avatar)
                .centerCrop()
                .into(holder.image);
        holder.text.setText(customers.get(position).getAlias() + " " +customers.get(position).getFirst() + " " + customers.get(position).getLast() );

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder,  position);
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
        inflater.inflate(R.menu.menu_manage_customers, popup.getMenu());
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

            switch (menuItem.getItemId()) {
                case R.id.action_notify:
                    ShowNotification_Popup();
                    break;

                default:
            }
            return true;
        }

        private void ShowNotification_Popup() {

            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
            final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_send_customer_notification, null);
            AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
            alertDialogBuilderUserInput.setView(mView);
            //action buttons
            final EditText userInputDialog = (EditText) mView.findViewById(R.id.userInputDialog);
            String defaultMessage = ApplicationPreferences.getLocalStringPreference(context,Constants.defaultMessageNotif);
            if (defaultMessage!=null && !defaultMessage.equals("")) userInputDialog.setText(defaultMessage);
            final ImageView button_edit_message = (ImageView) mView.findViewById(R.id.button_edit_message);
            button_edit_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userInputDialog.setEnabled(true);
                }
            });
            //listeners
            alertDialogBuilderUserInput
                    .setCancelable(false)
                    .setPositiveButton(context.getResources().getString(R.string.send_notification), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogBox, int id) {
                            boolean valid = true;
                            if (userInputDialog.getText().toString().trim().isEmpty()) {
                                userInputDialog.setError(context.getResources().getString(R.string.error_field_required));
                                valid = false;
                            } else {
                                userInputDialog.setError(null);
                                valid = true;
                            }

                            if (valid) {
                                holder.text_confirmation.setVisibility(View.GONE);
                                holder.text_confirmation.setText("");
                                notifyCustomer(userInputDialog.getText().toString().trim(), holder,mView);
                            }
                        }
                    })

                    .setNegativeButton(holder.overflow.getContext().getResources().getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {
                                    Log.d(TAG,"cancel..");
                                    dialogBox.cancel();
                                }
                            });

            AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
            alertDialogAndroid.show();
        }

        private void notifyCustomer(String description, ViewHolder holder,View mView){
            JSONObject data = new JSONObject();

            HashMap<String,String> params = new HashMap<>();
            String idUser = ApplicationPreferences.getLocalStringPreference(context,Constants.localUserId);
            params.put("idUser_customer", holder.idUser);//user to notify
            params.put("idUser", idUser);//business
            params.put("message", description);//message to customer
            params.put("method", "notifyCustomer");
            try {
                data.put("type_notification",Constants.notifycation_types.get("customer").getNotificationType());
                data.put("reply","1");
                data.put("id_merchant",idUser);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            params.put("process_data", data.toString());
            VolleyPostRequest(Constants.GET_CUSTOMERS, params,mView);
        }
        public void VolleyPostRequest(String url, final HashMap<String, String> args,final View mView){
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
                            processResponse(response,mView);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Snackbar.make(mView,context.getResources().getString(R.string.promt_notified_error),Snackbar.LENGTH_LONG);
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
            VolleySingleton.getInstance(context).addToRequestQueue(request);
        }
        private void processResponse(JSONObject response, View mView){
            Log.d(TAG, "response customers:"+response.toString());
            try {
                String status = response.getString("status");
                String result = response.toString().contains("message_id")?"1":"2";


                switch (result) {
                    case "1":

                        holder.text_confirmation.setVisibility(View.VISIBLE);
                        holder.text_confirmation.setText(context.getResources().getString(R.string.promt_message_noticated));
                        holder.text_confirmation.setTextColor(Color.parseColor(Constants.colorSuccess));
                        break;
                    case "2":
                        Log.d(TAG,"No se realizo la operacion.");
                        //Snackbar.make(mView,context.getResources().getString(R.string.promt_notified_error),Snackbar.LENGTH_LONG);
                        holder.text_confirmation.setVisibility(View.VISIBLE);
                        holder.text_confirmation.setText(context.getResources().getString(R.string.promt_noticated_error));
                        holder.text_confirmation.setTextColor(Color.parseColor(Constants.colorError));
                        break;

                    default:
                        Snackbar.make(mView,context.getResources().getString(R.string.promt_notified_error),Snackbar.LENGTH_LONG);
                        break;

                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG,"No se realizo la operacion");

                Snackbar.make(mView,context.getResources().getString(R.string.promt_notified_error),Snackbar.LENGTH_LONG);
            }
        }

    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView overflow;
        public ImageView image;
        public TextView text;
        public String idUser;
        public TextView text_confirmation;


        public ViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            text = (TextView) v.findViewById(R.id.text);
            overflow = (ImageView) v.findViewById(R.id.overflow);
            idUser = "";
            text_confirmation = (TextView) v.findViewById(R.id.text_confirmation);
        }
    }


    public void animateTo(List<CustomerInfo> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<CustomerInfo> newModels) {
        for (int i = customers.size() - 1; i >= 0; i--) {
            final CustomerInfo model = customers.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<CustomerInfo> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final CustomerInfo model = newModels.get(i);
            if (!customers.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<CustomerInfo> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final CustomerInfo model = newModels.get(toPosition);
            final int fromPosition = customers.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public CustomerInfo removeItem(int position) {
        final CustomerInfo model = customers.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, CustomerInfo model) {
        customers.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final CustomerInfo model = customers.remove(fromPosition);
        customers.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
