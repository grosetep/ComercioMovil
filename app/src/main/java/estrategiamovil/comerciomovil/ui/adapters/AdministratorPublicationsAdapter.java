package estrategiamovil.comerciomovil.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import estrategiamovil.comerciomovil.modelo.PendingPublication;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.tools.StringOperations;
import estrategiamovil.comerciomovil.ui.activities.DetailPublicationActivity;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * Created by administrator on 31/05/2017.
 */
public class AdministratorPublicationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PendingPublication> publications;
    private Activity activity;
    private RecyclerView recyclerview;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView.OnScrollListener listener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private static final String TAG = MerchantPubsAdapter.class.getSimpleName();

    public AdministratorPublicationsAdapter(List<PendingPublication> myDataset, Activity activity, RecyclerView list) {
        publications = myDataset;
        this.activity = activity;
        recyclerview = list;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerview.getLayoutManager();

        listener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();


                if (dy > 0) {
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

    public void setLoaded() {
        isLoading = false;
    }

    public void clear() {
        publications.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<PendingPublication> list) {
        if (list != null && list.size() > 0) {
            publications.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return publications.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.approval_publications_layout, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        } else {
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder g_holder, final int position) {
        if (g_holder instanceof AdministratorPublicationsAdapter.ViewHolder) {
            final AdministratorPublicationsAdapter.ViewHolder holder = (AdministratorPublicationsAdapter.ViewHolder) g_holder;
            final String ImagePath = publications.get(position).getPath() + publications.get(position).getImageName();
            holder.idPublication = publications.get(position).getIdPublication();
            holder.company = publications.get(position).getCompany();
            holder.isAds = publications.get(position).getIsAds();
            Glide.with(activity.getApplicationContext())
                    .load(ImagePath)
                    .into(holder.image);
            if (publications.get(position).getIsAds().equals("0")) {//cupon
                holder.text_price.setText(StringOperations.getStringWithDe(StringOperations.getStringWithCashSymbol(publications.get(position).getRegularPrice())) + "  " +
                        StringOperations.getStringWithA(StringOperations.getStringWithCashSymbol(publications.get(position).getOfferPrice())));
            } else {//ads
                holder.text_price.setText(StringOperations.getStringWithCashSymbol(publications.get(position).getOfferPrice()));
            }
            holder.text_cover.setText(publications.get(position).getCoverDescription());
            holder.text_date.setText(publications.get(position).getEffectiveDate());
            holder.text_location.setText(publications.get(position).getState());
            holder.text_status.setText(publications.get(position).getStatus());
            String detail = publications.get(position).getApprovalDetail();
            holder.text_approval.setText((detail!=null && detail.length()>0)?detail:"");

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailPublicationActivity.class);
                    intent.putExtra(DetailPublicationActivity.EXTRA_PUBLICACION, holder.idPublication);
                    intent.putExtra(DetailPublicationActivity.EXTRA_TITLE, holder.company);
                    intent.putExtra(DetailPublicationActivity.EXTRA_IMAGETITLE, ImagePath);
                    intent.putExtra(DetailPublicationActivity.EXTRA_TYPE_PUBLICATION, holder.isAds.equals("0") ? Constants.type_publicacion_cupons : Constants.type_publication_ads);
                    context.startActivity(intent);
                }
            });
            holder.button_approval.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    approvalPublication(true,holder,context,position);
                }
            });
            holder.button_no_approved.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    boolean valid = true;
                    if (holder.text_approval.getText().toString().trim().length()==0){
                        valid = false;
                        holder.text_approval.setError(activity.getString(R.string.error_field_required));
                    }else{
                        valid = true;
                        holder.text_approval.setError(null);
                    }
                    if (valid)
                        approvalPublication(false,holder,context,position);
                }
            });
        } else if (g_holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) g_holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return publications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private String idPublication;
        private String company;
        private String isAds;
        private ImageView image;
        private TextView text_cover;
        private TextView text_price;
        private TextView text_location;
        private TextView text_status;
        private TextView text_date;
        private EditText text_approval;
        private AppCompatButton button_approval;
        private AppCompatButton button_no_approved;

        public ViewHolder(View v) {
            super(v);
            idPublication = "";
            company = "";
            isAds = "";
            image = (ImageView) v.findViewById(R.id.image);
            text_cover = (TextView) v.findViewById(R.id.text_cover);
            text_price = (TextView) v.findViewById(R.id.text_price);
            text_location = (TextView) v.findViewById(R.id.text_location);
            text_status = (TextView) v.findViewById(R.id.text_status);
            text_date = (TextView) v.findViewById(R.id.text_date);
            text_approval = (EditText) v.findViewById(R.id.text_approval);
            button_approval = (AppCompatButton) v.findViewById(R.id.button_approval);
            button_no_approved = (AppCompatButton) v.findViewById(R.id.button_no_approved);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    private void approvalPublication(boolean approval,AdministratorPublicationsAdapter.ViewHolder holder,Context ctx, int position) {
        HashMap<String, String> params = new HashMap<>();
        params.put("method", "approvalPublication");
        params.put("idPublication", holder.idPublication);
        params.put("approval", String.valueOf(approval));
        params.put("comment", holder.text_approval.getText().toString());
        VolleyPostRequest(Constants.ADMINISTRATION, params,holder,ctx, position);
    }

    public void VolleyPostRequest(String url, final HashMap<String, String> args,final AdministratorPublicationsAdapter.ViewHolder holder, final Context ctx, final int position) {
        JSONObject jobject = new JSONObject(args);
        Log.d(TAG, "VolleyPostRequest:" + jobject.toString());

        VolleySingleton.getInstance(ctx).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                processResponse(response,  ctx, holder,position,args);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                                ShowConfirmations.showConfirmationMessage(activity.getResources().getString(R.string.generic_server_down), activity);
                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }


                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }

    private void processResponse(JSONObject response,  Context ctx,AdministratorPublicationsAdapter.ViewHolder holder,int position, HashMap<String, String> args) {
        Log.d(TAG, "response:" + response.toString());
        try {
            // Obtener atributo "mensaje"
            String message = response.getString("status");
            switch (message) {
                case "1"://assign values
                    String object = response.getString("result");
                    ShowConfirmations.showConfirmationMessage(activity.getResources().getString(R.string.text_prompt_profile_updated), activity);
                    if (args.get("approval").equals(String.valueOf(true))){//exito al aprobar
                        publications.remove(position);
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(position, getItemCount());
                    }else{//exito al rechazar
                        PendingPublication p = publications.get(position);
                        if (p!=null){
                            p.setStatus(Constants.status_rejected);
                            p.setApprovalDetail(args.get("comment"));
                            notifyItemChanged(holder.getAdapterPosition());
                        }
                    }
                    break;
                case "2"://no detail
                    onError(response);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            onError(response);
        }
    }

    private void onError(JSONObject response) {
        String mensaje2 = null;
        try {
            mensaje2 = response.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Toast.makeText(
                activity,
                mensaje2,
                Toast.LENGTH_SHORT).show();
    }
}