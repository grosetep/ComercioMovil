package estrategiamovil.comerciomovil.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import estrategiamovil.comerciomovil.modelo.SalesItem;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.tools.StringOperations;
import estrategiamovil.comerciomovil.ui.fragments.SalesFragment;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * Created by administrator on 03/11/2016.
 */

public class SalesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<SalesItem> sales;
    private Activity activity;
    private RecyclerView recyclerview;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView.OnScrollListener listener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private SalesFragment fragment;
    private static final String TAG = SalesAdapter.class.getSimpleName();
    public SalesAdapter(List<SalesItem> myDataset,Activity activity,RecyclerView list,SalesFragment fragment) {
        sales = myDataset;
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
                .inflate(R.layout.my_sales_layout, parent, false);
        SalesAdapter.ViewHolder vh = new SalesAdapter.ViewHolder(v);
        return vh;
        }else{
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
    }
    }
    public void setLoaded() {
        isLoading = false;
    }
    public void clear(){ sales.clear();notifyDataSetChanged();}
    public void addAll(List<SalesItem> list){
        if (list!=null && list.size()>0){sales.addAll(list);notifyDataSetChanged(); }
    }
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }
    @Override
    public int getItemViewType(int position) {
        return sales.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }
    public void onBindViewHolder(final RecyclerView.ViewHolder s_holder, final int position) {
        if (s_holder instanceof SalesAdapter.ViewHolder) {
            final SalesAdapter.ViewHolder holder = (SalesAdapter.ViewHolder) s_holder;
        Glide.with(holder.image.getContext())
                .load(sales.get(position).getPath() + sales.get(position).getImage())
                .centerCrop()
                .into(holder.image);
        holder.text_coverDescription.setText(sales.get(position).getCoverDescription());
        holder.text_total.setText(StringOperations.getAmountFormat(sales.get(position).getAmount()));
        holder.text_net.setText(StringOperations.getAmountFormat(sales.get(position).getToPay()));
            holder.text_payer.setText(sales.get(position).getNamePayer());
        holder.text_payment_method.setText(sales.get(position).getPaymentMethod());
        holder.text_num_items.setText(sales.get(position).getNumItems());
        holder.text_date.setText(sales.get(position).getDateCreated());
        holder.text_status_payment_green.setText(sales.get(position).getStatus());
        holder.text_status_payment_red.setText(sales.get(position).getStatus());
        holder.text_capture.setText(sales.get(position).getCaptureLine());
        holder.text_cupons.setText(sales.get(position).getCupons());
            String moneyRelease = sales.get(position).getIdPaymentMethod().equals("3") ? sales.get(position).getMoneyReleaseDirect() : sales.get(position).getMoneyRelease();
            holder.text_money_release.setText(moneyRelease);
            holder.text_commission.setText(StringOperations.getAmountFormat(sales.get(position).getCommission()));
            String product_sent = sales.get(position).getProductSent().toString();
            String product_receive = sales.get(position).getProductReceived().toString();
            String product_receive_detail = !sales.get(position).getReceivedDetail().toString().equals("") ? "Cliente reportó:" + sales.get(position).getReceivedDetail() : "";

            holder.text_shipping.setText(product_sent.equals(String.valueOf(Constants.cero)) ? "No" : "Si");
            holder.text_receive.setText(product_receive.equals(String.valueOf(Constants.cero)) ? "No" : "Si" + (product_receive_detail.length() > 0 ? ". " + product_receive_detail : ""));
        String days = sales.get(position).getCreated();
            holder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(holder, position);
                }
            });

            if (sales.get(position).getIdPaymentMethod().equals(Constants.direct_deposit)){
                holder.layout_voucher.setVisibility(View.VISIBLE);
                String id_voucher = sales.get(position).getIdVoucher();
                holder.text_voucher.setText((id_voucher!=null && !id_voucher.equals(""))?activity.getString(R.string.promt_yes):activity.getString(R.string.promt_no));
            }else{
                holder.layout_voucher.setVisibility(View.GONE);
            }

        if (days!=null){
            int days_created = Integer.parseInt(days);
            String label_text ="";
            switch (days_created){
                    case 0:
                        label_text = "Hoy";
                        break;
                    case 1:
                        label_text = "Ayer";
                        break;
                    default:
                        label_text = "Hace " + days_created + " días";
                        break;
            }
            holder.text_days.setText(label_text);
        }


        if (sales.get(position).getIdStatus().equals("2")) {//approved
            setLabelActive(false,true,false,null,holder);
        }else if(sales.get(position).getIdStatus().equals("1")){//pending
            setLabelActive(false,false,true,null,holder);
        }else{//other
            setLabelActive(true,false,false, Constants.estatus_mercado_pago.get(sales.get(position).getIdStatus()),holder);
        }
        }else if(s_holder instanceof LoadingViewHolder){
        LoadingViewHolder loadingViewHolder = (LoadingViewHolder) s_holder;
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
        return sales.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView overflow;
        public ImageView image;
        public TextView text_coverDescription;
        public TextView text_total;
        public TextView text_net;
        public TextView text_payer;
        public TextView text_payment_method;
        public TextView text_num_items;
        public TextView text_date;
        public TextView text_status_payment_green;
        public TextView text_status_payment_red;
        public TextView text_status_payment_orange;
        public TextView text_capture;
        public TextView text_days;
        public TextView text_cupons;
        public TextView text_money_release;
        public TextView text_commission;
        public TextView text_shipping;
        public TextView text_receive;
        public TextView text_voucher;
        public LinearLayout layout_voucher;

        public ViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            text_capture = (TextView) v.findViewById(R.id.text_capture);
            text_coverDescription = (TextView) v.findViewById(R.id.text_coverDescription);
            text_total = (TextView) v.findViewById(R.id.text_total);
            text_commission = (TextView) v.findViewById(R.id.text_commission);
            text_net = (TextView) v.findViewById(R.id.text_net);
            text_payer = (TextView) v.findViewById(R.id.text_payer);
            text_payment_method = (TextView) v.findViewById(R.id.text_payment_method);
            text_num_items = (TextView) v.findViewById(R.id.text_num_items);
            text_date = (TextView) v.findViewById(R.id.text_date);
            text_status_payment_green = (TextView) v.findViewById(R.id.text_status_payment_green);
            text_status_payment_red = (TextView) v.findViewById(R.id.text_status_payment_red);
            text_status_payment_orange = (TextView) v.findViewById(R.id.text_status_payment_orange);
            text_days = (TextView) v.findViewById(R.id.text_days);
            text_cupons = (TextView) v.findViewById(R.id.text_cupons);
            text_money_release = (TextView) v.findViewById(R.id.text_money_release);
            text_shipping = (TextView) v.findViewById(R.id.text_shipping);
            text_receive = (TextView) v.findViewById(R.id.text_receive);
            text_voucher = (TextView) v.findViewById(R.id.text_voucher);
            layout_voucher = (LinearLayout) v.findViewById(R.id.layout_voucher);
            overflow = (ImageView) v.findViewById(R.id.overflow);
        }
    }
    private void showPopupMenu(ViewHolder view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.overflow.getContext(), view.overflow);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_shipping, popup.getMenu());
        //validation for display options available
        if (sales.get(position).getIdPaymentMethod().equals(Constants.direct_deposit)){
            String id_voucher = sales.get(position).getIdVoucher();
            if (id_voucher==null){//no hay comprobante disponible aun: deshabilitar opcion ver comprobante
                popup.getMenu().findItem(R.id.action_view_voucher).setEnabled(false);
                popup.getMenu().findItem(R.id.action_view_voucher).setTitle(activity.getString(R.string.text_promt_no_voucher_title));
            }
        }else{
            popup.getMenu().findItem(R.id.action_view_voucher).setVisible(false);
        }

        popup.setOnMenuItemClickListener(new MyMenuItemClickListener().getInstance(view,position));
        popup.show();
    }
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        ViewHolder holder;
        int position = 0;
        public int getPosition(){return position;}
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
                case R.id.action_shipping:
                    ShowNotification_Popup(activity,activity.getString(R.string.text_promt_product_sent_title),getPosition());
                    break;
                case R.id.action_view_voucher:
                    if (fragment!=null)
                        fragment.startGalleryActivity(sales.get(position));
                    break;
                default:
            }
            return true;
        }


        private  void ShowNotification_Popup(final Activity activity,String title,final int position) {
            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(activity);
            final View content = layoutInflaterAndroid.inflate(R.layout.layout_product_sent, null);
            final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_template, null);
            LinearLayout fields = (LinearLayout)mView.findViewById(R.id.content);
            fields.addView(content);
            AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(activity);
            alertDialogBuilderUserInput.setView(mView);


            final TextView text_question = (TextView) mView.findViewById(R.id.text_question);
            final TextView text_tracking = (TextView) mView.findViewById(R.id.text_tracking);
            final EditText text_message = (EditText) mView.findViewById(R.id.text_message);
            final CheckBox check_condition = (CheckBox) mView.findViewById(R.id.check_condition);
            final ProgressBar pbLoading_shipping = (ProgressBar) mView.findViewById(R.id.pbLoading_shipping);

            //customize titles
            ((TextView)mView.findViewById(R.id.text_title)).setText(title);
            ((TextView)mView.findViewById(R.id.text_title)).setBackgroundColor(Color.parseColor(Constants.colorTitle));
            ((TextView)mView.findViewById(R.id.text_title)).setTextColor(Color.parseColor(Constants.colorBackgroundEnabled));

            text_question.setText(activity.getString(R.string.text_promt_product_sent));
            text_tracking.setText(activity.getString(R.string.text_promt_tracking_title));
            check_condition.setText(activity.getString(R.string.text_promt_shipping_on_store_condition));


            AppCompatButton button = (AppCompatButton) mView.findViewById(R.id.button_done_shipping);
            //set content
            final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
            alertDialogAndroid.show();
            check_condition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (check_condition.isChecked()) text_message.setEnabled(false);
                    else text_message.setEnabled(true);
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean valid = true;

                    if (!check_condition.isChecked()) {//validar que proporcione guia
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

        private void sendDetail(View mView,AlertDialog alertDialogAndroid, int position){
            String detail = "";
            CheckBox chk = ((CheckBox) mView.findViewById(R.id.check_condition));
            if (chk!=null && !chk.isChecked())
                detail = ((EditText)mView.findViewById(R.id.text_message)).getText().toString();
            else
                detail = activity.getString(R.string.promt_shipping_on_store);

            HashMap<String,String> params = new HashMap<>();
            params.put("id_capture_line", holder.text_capture.getText().toString());
            params.put("detail", detail);
            params.put("shipment_confirmed",String.valueOf(1));
            params.put("type_flow","sales");
            params.put("method", "saveShippingDetail");

            VolleyPostRequest(Constants.GET_SALES, params,alertDialogAndroid,mView,position);
        }
        public void VolleyPostRequest(String url, final HashMap<String, String> args, final AlertDialog alertDialogAndroid, final View mView,final int position){
            JSONObject jobject = new JSONObject(args);
            Log.d(TAG,"VolleyPostRequest:" +  jobject.toString());
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jobject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            processResponseShipping(response,alertDialogAndroid,mView,position);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ((ProgressBar)mView.findViewById(R.id.pbLoading_rate)).setVisibility(View.GONE);
                            TextView text_error = (TextView)mView.findViewById(R.id.text_error_shipping);
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
        public void processResponseShipping(JSONObject response,AlertDialog dialog,View mView, int position){
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
                        SalesItem item = sales.get(position);
                        item.setProductSent(activity.getString(R.string.promt_yes));
                        item.setSentDetail((text_message!=null)?text_message.getText().toString():"");
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
    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }
}
