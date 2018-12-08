package estrategiamovil.comerciomovil.ui.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Purchase;
import estrategiamovil.comerciomovil.modelo.PurchaseItem;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.tools.StringOperations;
import estrategiamovil.comerciomovil.ui.fragments.PurchasesFragment;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * Created by administrator on 27/10/2016.
 */
public class PurchasesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>   {
    private List<PurchaseItem> purchases;
    private final String METHOD_REVIEW = "review";
    private final String METHOD_SHIPPING = "shipping";
    private Activity activity;
    private RecyclerView recyclerview;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView.OnScrollListener listener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private PurchasesFragment fragment;
    private static final String TAG = PurchasesAdapter.class.getSimpleName();

    public PurchasesAdapter(List<PurchaseItem> myDataset, Activity activity, RecyclerView list, PurchasesFragment fragment) {
        purchases = myDataset;
        this.activity = activity;
        recyclerview = list;
        this.fragment = fragment;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerview.getLayoutManager();

        listener = new RecyclerView.OnScrollListener() {
    @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();


                if (dy>0) {
                    if (!isLoading && lastVisibleItem == totalItemCount - 1) {
                        if (mOnLoadMoreListener != null) {
                            isLoading = true;
                            mOnLoadMoreListener.onLoadMore();
                        }

                    }
                }

            }
        };

        recyclerview.addOnScrollListener(listener);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_purchases_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
        }else{
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
    }
    }
    public void setLoaded() {
        isLoading = false;
    }
    public void clear(){ purchases.clear();notifyDataSetChanged();}
    public void addAll(List<PurchaseItem> list){
        if (list!=null && list.size()>0){purchases.addAll(list);notifyDataSetChanged(); }
    }
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }
    @Override
    public int getItemViewType(int position) {
        return purchases.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }
    public void onBindViewHolder(final RecyclerView.ViewHolder p_holder, final int position) {
        if (p_holder instanceof PurchasesAdapter.ViewHolder) {
            final PurchasesAdapter.ViewHolder holder = (PurchasesAdapter.ViewHolder) p_holder;
        holder.idMerchant = purchases.get(position).getIdMerchant();
            holder.capture_line = purchases.get(position).getCaptureLine();
        holder.rated = purchases.get(position).getRated();
        Glide.with(holder.image.getContext())
                .load(purchases.get(position).getPath() + purchases.get(position).getImage())
                .centerCrop()
                .into(holder.image);
        holder.text_amount.setText(StringOperations.getAmountFormat(purchases.get(position).getAmount()));
        holder.text_num_items.setText(purchases.get(position).getNumItems());
        holder.text_date.setText(purchases.get(position).getDateCreated());
        holder.text_coverDescription.setText(purchases.get(position).getCoverDescription());
        holder.text_status_payment_green.setText(purchases.get(position).getStatus());
        holder.text_status_payment_red.setText(purchases.get(position).getStatus());
        holder.text_cupons.setText(purchases.get(position).getCupons());
        holder.text_info.setText(purchases.get(position).getInfo());
            String product_shipping = purchases.get(position).getProductSent().equals(String.valueOf(Constants.cero)) ? "" : purchases.get(position).getDetailSent();
            holder.text_shipping.setText(product_shipping);
            String receice = purchases.get(position).getProductReceived().equals(String.valueOf(Constants.cero))?"No":purchases.get(position).getReceivedDetail();
            holder.text_receive.setText(receice);
        holder.id_purchase = purchases.get(position).getIdPurchase();

            if (purchases.get(position).getIdPaymentMethod().equals(Constants.direct_deposit)){
                holder.layout_voucher.setVisibility(View.VISIBLE);
                String id_voucher = purchases.get(position).getIdVoucher();
                holder.text_voucher.setText((id_voucher!=null && !id_voucher.equals(""))?activity.getString(R.string.promt_yes):activity.getString(R.string.promt_no));
                if (id_voucher==null){
                    holder.text_voucher_label.setVisibility(View.VISIBLE);
                    holder.text_voucher_label.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (fragment!=null){
                                fragment.startActivityVoucher(purchases.get(position));
                            }
                        }
                    });
                }
                //hay comprobante: solo poner:Si, sino, mostrar label voucher y agrgar la accion igual que en el menu
            }else{
                holder.layout_voucher.setVisibility(View.GONE);
                holder.text_voucher_label.setVisibility(View.GONE);
            }
        if (purchases.get(position).getIdStatus().equals("2")) {//approved
            setLabelActive(false,true,false,null,holder);
            } else if (purchases.get(position).getIdStatus().equals("1")) {//pending
                setLabelActive(false, false, true, null, holder);
            } else {//other
                setLabelActive(true, false, false, Constants.estatus_mercado_pago.get(purchases.get(position).getIdStatus()), holder);
            }
            holder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(holder,  position);
                }
            });
    }
        else if(p_holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) p_holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    private void setLabelActive(boolean label_red,boolean label_green,boolean label_orange,String text_red,ViewHolder holder){
        holder.text_status_payment_red.setVisibility(label_red?View.VISIBLE:View.GONE);
        holder.text_status_payment_green.setVisibility(label_green?View.VISIBLE:View.GONE);
        holder.text_status_payment_orange.setVisibility(label_orange?View.VISIBLE:View.GONE);
        holder.text_status_payment_red.setText(text_red!=null && text_red.length()>0?text_red:"");

    }
    @Override
    public int getItemCount() {
        return purchases.size();
    }
    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(ViewHolder view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.overflow.getContext(), view.overflow);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_review, popup.getMenu());
        //opciones disponibles en aprobado:reseña,recibi. Opciones para pendiente:voucher
        if (purchases.get(position).getIdStatus().equals("2")){//approved
            if (!purchases.get(position).getRated().equals(String.valueOf(Constants.cero))) {
            popup.getMenu().findItem(R.id.action_rate).setEnabled(false);
            popup.getMenu().findItem(R.id.action_rate).setTitle(activity.getString(R.string.text_promt_rated_title));
        }
            if (purchases.get(position).getIdVoucher()!=null){//validar si ya existe voucher
                popup.getMenu().findItem(R.id.action_voucher).setEnabled(false);
                popup.getMenu().findItem(R.id.action_voucher).setTitle(activity.getString(R.string.text_promt_voucher_title));
            }
        }else{//pending or other
            popup.getMenu().findItem(R.id.action_rate).setEnabled(false);
            popup.getMenu().findItem(R.id.action_receive).setEnabled(false);
        }


        popup.setOnMenuItemClickListener(new MyMenuItemClickListener().getInstance(view,position));
        popup.show();
    }
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        ViewHolder holder;
        int position = 0;
        private void setPosition(int p){position = p;}
        private void setHolder(ViewHolder hldr){ holder = hldr;}
        public int getPosition(){return position;}
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
                case R.id.action_rate:
                    ShowNotification_Popup(activity,activity.getString(R.string.text_promt_send_rate_title));
                    break;
                case R.id.action_receive:
                    ShowNotification_Popup_shipping(activity,activity.getString(R.string.text_promt_product_sent_title),getPosition());
                    break;
                case R.id.action_voucher:
                    if (fragment!=null){
                        fragment.startActivityVoucher(purchases.get(position));
                    }
                    break;
                default:
            }
            return true;
        }
        private  void ShowNotification_Popup_shipping(final Activity activity,String title,final int position) {
            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(activity);
            final View content = layoutInflaterAndroid.inflate(R.layout.layout_product_sent, null);
            final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_template, null);
            LinearLayout fields = (LinearLayout)mView.findViewById(R.id.content);
            fields.addView(content);
            AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(activity);
            alertDialogBuilderUserInput.setView(mView);


            final TextView text_question = (TextView) mView.findViewById(R.id.text_question);
            text_question.setVisibility(View.GONE);
            final TextView text_tracking = (TextView) mView.findViewById(R.id.text_tracking);
            final EditText text_message = (EditText) mView.findViewById(R.id.text_message);
            text_message.setVisibility(View.GONE);
            final CheckBox check_condition = (CheckBox) mView.findViewById(R.id.check_condition);
            final ProgressBar pbLoading_shipping = (ProgressBar) mView.findViewById(R.id.pbLoading_shipping);

            //customize titles
            ((TextView)mView.findViewById(R.id.text_title)).setText(title);
            ((TextView)mView.findViewById(R.id.text_title)).setBackgroundColor(Color.parseColor(Constants.colorTitle));
            ((TextView)mView.findViewById(R.id.text_title)).setTextColor(Color.parseColor(Constants.colorBackgroundEnabled));

            text_tracking.setText(activity.getString(R.string.text_promt_product_ok_question));
            check_condition.setText(activity.getString(R.string.text_promt_product_ok));


            AppCompatButton button = (AppCompatButton) mView.findViewById(R.id.button_done_shipping);
            //set content
            final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
            alertDialogAndroid.show();
            check_condition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (check_condition.isChecked()) {
                        text_message.setEnabled(true);
                        text_message.setVisibility(View.VISIBLE);
                    }else{
                        text_message.setVisibility(View.GONE);}
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean valid = true;

                    if (check_condition.isChecked()) {//validar que proporcione guia
                        if (text_message.getText().toString().trim().isEmpty()) {
                            text_message.setError(activity.getString(R.string.error_field_required));
                            valid = false;
                        } else {
                            text_message.setError(null);
                        }
                    }
                    if (valid) {
                        pbLoading_shipping.setVisibility(View.VISIBLE);
                        sendDetail(mView,alertDialogAndroid,position);
                    }
                }
            });

        }

        private void sendDetail(View mView,AlertDialog alertDialogAndroid,int position){
            String detail = "";
            CheckBox chk = ((CheckBox) mView.findViewById(R.id.check_condition));
            if (chk!=null && chk.isChecked())
                detail = ((EditText)mView.findViewById(R.id.text_message)).getText().toString();
            else
                detail = activity.getString(R.string.text_promt_product_receive_ok);

            HashMap<String,String> params = new HashMap<>();
            params.put("id_capture_line", holder.capture_line);
            params.put("detail", detail);
            params.put("shipment_confirmed",String.valueOf(1));
            params.put("type_flow","purchases");
            params.put("method", "saveShippingDetail");
            VolleyPostRequest(Constants.GET_SALES, params,alertDialogAndroid,mView,METHOD_SHIPPING,position);
        }

        private  void ShowNotification_Popup(final Activity activity,String title) {
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

        }

        private void sendRate(View mView,AlertDialog alertDialogAndroid){
            int rate = (int)((RatingBar)mView.findViewById(R.id.ratingBar)).getRating();
            String comment = ((EditText)mView.findViewById(R.id.text_message_rated)).getText().toString();
            HashMap<String,String> params = new HashMap<>();
            String idUser = ApplicationPreferences.getLocalStringPreference(activity,Constants.localUserId);
            params.put("idUser", idUser);
            params.put("comment", comment);
            params.put("rate",String.valueOf(rate));
            params.put("id_merchant", holder.idMerchant);
            params.put("id_purchase", holder.id_purchase);
            params.put("method", "saveRate");

            VolleyPostRequest(Constants.GET_BUSINESS, params,alertDialogAndroid,mView,METHOD_REVIEW,0);
        }
        public void VolleyPostRequest(String url, final HashMap<String, String> args, final AlertDialog alertDialogAndroid, final View mView,final String callback,final int position){
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
                        if (callback == METHOD_REVIEW)
                            processResponseReview(response,alertDialogAndroid,mView);
                            else if(callback == METHOD_SHIPPING)
                                processResponseShipping(response,alertDialogAndroid,mView,position);
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
    public void processResponseReview(JSONObject response,AlertDialog dialog,View mView){
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
    }
        public void processResponseShipping(JSONObject response,AlertDialog dialog,View mView,int position){
            Log.d(TAG, response.toString());
            TextView text_error = (TextView) mView.findViewById(R.id.text_error_shipping);
            TextView text_message = (TextView) mView.findViewById(R.id.text_message);
            try {
                String status = response.getString("status");

                switch (status) {
                    case "1":
                        dialog.dismiss();
                        JSONObject result = response.getJSONObject("result");
                        ShowConfirmations.showConfirmationMessage(result.get("message").toString(),activity);
                        PurchaseItem item = purchases.get(position);
                        item.setProductSent(activity.getString(R.string.promt_yes));
                        item.setDetailSent((text_message!=null)?text_message.getText().toString():"");
                        notifyItemChanged(holder.getAdapterPosition());
                        break;
                    case "2":
                        ((ProgressBar) mView.findViewById(R.id.pbLoading_shipping)).setVisibility(View.GONE);
                        String msg = response.getString("message");
                        text_error.setText(msg);
                        text_error.setVisibility(View.VISIBLE);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ((ProgressBar) mView.findViewById(R.id.pbLoading_shipping)).setVisibility(View.GONE);
                text_error.setText(activity.getString(R.string.text_promt_send_rate_error));
                text_error.setVisibility(View.VISIBLE);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView overflow;
        public ImageView image;
        public TextView text_amount;
        public TextView text_num_items;
        public TextView text_date;
        public TextView text_status_payment_green;
        public TextView text_status_payment_red;
        public TextView text_status_payment_orange;
        public TextView text_coverDescription;
        public TextView text_cupons;
        public TextView text_info;
        public String id_purchase;
        public String idMerchant;
        public String rated;
        public String capture_line;
        public TextView text_shipping;
        public TextView text_receive;
        public TextView text_voucher_label;
        public TextView text_voucher;
        public LinearLayout layout_voucher;

        public ViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            text_status_payment_green = (TextView) v.findViewById(R.id.text_status_payment_green);
            text_status_payment_red = (TextView) v.findViewById(R.id.text_status_payment_red);
            text_status_payment_orange = (TextView) v.findViewById(R.id.text_status_payment_orange);
            text_amount= (TextView) v.findViewById(R.id.text_amount);
            text_num_items=  (TextView) v.findViewById(R.id.text_num_items);
            text_date= (TextView) v.findViewById(R.id.text_date);
            text_coverDescription= (TextView) v.findViewById(R.id.text_coverDescription);
            text_cupons = (TextView) v.findViewById(R.id.text_cupons);
            text_info = (TextView) v.findViewById(R.id.text_info);
            overflow = (ImageView) v.findViewById(R.id.overflow);
            text_shipping = (TextView) v.findViewById(R.id.text_shipping);
            text_receive = (TextView) v.findViewById(R.id.text_receive);
            text_voucher_label = (TextView) v.findViewById(R.id.text_voucher_label);
            text_voucher = (TextView) v.findViewById(R.id.text_voucher);
            layout_voucher = (LinearLayout) v.findViewById(R.id.layout_voucher);
            id_purchase = "";
            capture_line = idMerchant = rated = "";
        }
    }
    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }
}
