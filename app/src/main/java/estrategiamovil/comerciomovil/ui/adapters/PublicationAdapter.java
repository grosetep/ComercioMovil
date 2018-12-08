package estrategiamovil.comerciomovil.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.PublicationCardViewModel;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.tools.StringOperations;
import estrategiamovil.comerciomovil.tools.UtilPermissions;
import estrategiamovil.comerciomovil.ui.activities.DetailPublicationActivity;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * Created by administrator on 09/06/2016.
 */
public class PublicationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PublicationCardViewModel> publications;
    private Activity activity;
    private final int METHOD_LIKE = 2;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView.OnScrollListener listener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView recyclerview;
    private static final String TAG = PublicationAdapter.class.getSimpleName();

    public PublicationAdapter(List<PublicationCardViewModel> myDataset,Activity activity,RecyclerView list) {
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

        Log.d(TAG,"PublicationAdapter created....");
        recyclerview.addOnScrollListener(listener);
    }
    public void clear(){ publications.clear();notifyDataSetChanged();}
    public void addAll(List<PublicationCardViewModel> list){
        if (list!=null && list.size()>0){publications.addAll(list);notifyDataSetChanged(); }
    }
    public void resetListener(){  this.mOnLoadMoreListener = null;}
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
                        .inflate(R.layout.card_view_layout, parent, false);
                ViewHolder vh = new ViewHolder(v);
                return vh;
        }else{
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
      if(holder instanceof PublicationAdapter.ViewHolder) {
          final PublicationAdapter.ViewHolder p_holder = (PublicationAdapter.ViewHolder) holder;
          final String ImagePath = publications.get(position).getPath() + publications.get(position).getImageName();
          p_holder.mBoundString = publications.get(position).getIdPublication();
          p_holder.text_card_name.setText(publications.get(position).getCoverDescription());
          //holder.text_detail_card_name.setText(publications.get(position).getDetailedDescription());
          //Log.d(TAG, "ImagePath:" + ImagePath);

          Glide.with(p_holder.image_card_cover.getContext())
                  .load(ImagePath)
                  .into(p_holder.image_card_cover);
          p_holder.text_priceOff.setText(StringOperations.getStringWithA(StringOperations.getAmountFormatWithNoDecimals(publications.get(position).getOfferPrice())));
          p_holder.text_price.setText(StringOperations.getStringWithDe(StringOperations.getAmountFormatWithNoDecimals(publications.get(position).getRegularPrice())));
          //boolean isfav = publications.get(position).getIsfav().equals("1");
          //if(isfav) holder.image_action_like.setImageResource(R.drawable.ic_action_heart_red);
          p_holder.text_company.setText(publications.get(position).getCompany());
          p_holder.text_state.setText(publications.get(position).getState());
          p_holder.text_availability.setText(publications.get(position).getAvailability());
          p_holder.text_score.setText(publications.get(position).getScore());

          if (publications.get(position).getAvailability().equals("0")) {
              p_holder.layout_over.setVisibility(View.VISIBLE);
              holder.itemView.setOnClickListener(null);
          } else {
              p_holder.layout_over.setVisibility(View.GONE);
              holder.itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Context context = v.getContext();
                      Intent intent = new Intent(context, DetailPublicationActivity.class);
                      intent.putExtra(DetailPublicationActivity.EXTRA_PUBLICACION, p_holder.mBoundString);
                      intent.putExtra(DetailPublicationActivity.EXTRA_TITLE, p_holder.text_company.getText());
                      intent.putExtra(DetailPublicationActivity.EXTRA_IMAGETITLE, ImagePath);
                      intent.putExtra(DetailPublicationActivity.EXTRA_TYPE_PUBLICATION, Constants.type_publicacion_cupons);
                      context.startActivity(intent);
                  }
              });
              //add events to icons
              p_holder.image_action_share.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Bitmap bm = ((GlideBitmapDrawable) p_holder.image_card_cover.getDrawable()).getBitmap();
                      if (bm != null) {
                          String pathofBmp = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bm, "title", null);
                          Uri bmpUri = Uri.parse(pathofBmp);
                          String location = "http://maps.google.com/maps?z=12&t=m&q=loc:" + publications.get(position).getLat() + "+" + publications.get(position).getLng();
                          String message_temp = publications.get(position).getDetailedDescription() + "\n De:$" + publications.get(position).getRegularPrice() + " A Solo:$" + publications.get(position).getOfferPrice() + ".\n Nuestra ubicación:" + location + " Descubre más ofertas en " + activity.getString(R.string.app_name) + ". Disponible en Google Play Store.";
                          Spanned message = null;
                          if (bmpUri != null) {
                              Context context = v.getContext();
                              Intent intent = new Intent(Intent.ACTION_SEND);
                              intent.setType("image/*");
                              intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                              intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                              if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                  message = Html.fromHtml(message_temp, Html.FROM_HTML_MODE_LEGACY);
                              } else {
                                  message = Html.fromHtml(message_temp);
                              }
                              intent.putExtra(Intent.EXTRA_TEXT, message.toString());
                              intent.putExtra(Intent.EXTRA_SUBJECT, "Tienes que comprar este cupón");
                              context.startActivity(Intent.createChooser(intent, "Comparte con tus amigos"));
                          }
                      } else {
                          Log.d(TAG, "null");
                      }
                  }
              });
              p_holder.image_action_like.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      v.setEnabled(false);
                      try {
                          int score = Integer.parseInt(publications.get(position).getScore());
                          p_holder.text_score.setText("" + (score + 1));
                          like(publications.get(position), p_holder);
                      } catch (NumberFormatException e) {
                      }

                  }
              });

          }
      }else if(holder instanceof LoadingViewHolder){
          LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
          loadingViewHolder.progressBar.setIndeterminate(true);
      }
    }
    public void like(PublicationCardViewModel p,ViewHolder holder){
        Log.d(TAG,"like");
        HashMap<String,String> params = new HashMap<>();
        params.put("method", "like");
        params.put("idPublication", p.getIdPublication());
        VolleyPostRequest(Constants.GET_PUBLICATIONS, params, METHOD_LIKE,p,holder);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return publications == null ? 0 : publications.size();
    }
    public void setLoaded() {
        isLoading = false;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public String mBoundString;
        public TextView text_card_name;
        //public TextView text_detail_card_name;
        public ImageView image_card_cover;
        public TextView text_company;
        public TextView text_state;
        //public TextView text_effectiveDate;
        public TextView text_score;
        public TextView text_availability;
        public ImageView image_action_effectiveDate;
       // public ImageView image_action_score;
        public ImageView image_action_availability;
        public TextView text_priceOff;
        public TextView text_price;
        public ImageView image_action_like;
        //public ImageView image_action_flag;
        //public ImageView image_action_heart;
        public ImageView image_action_share;
        public RelativeLayout layout_over;
        //public Toolbar maintoolbar;
        public ViewHolder(View v) {
            super(v);
            layout_over = (RelativeLayout) v.findViewById(R.id.layout_over);
            text_card_name = (TextView) v.findViewById(R.id.text_card_name);
            //text_detail_card_name = (TextView) v.findViewById(R.id.text_detail_card_name);
            image_card_cover = (ImageView) v.findViewById(R.id.image_card_cover);
            text_company = (TextView) v.findViewById(R.id.text_company);
            text_state = (TextView) v.findViewById(R.id.text_state);
            //text_effectiveDate = (TextView) v.findViewById(R.id.text_effectiveDate);
            text_score = (TextView) v.findViewById(R.id.text_score);
            text_availability = (TextView) v.findViewById(R.id.text_availability);
            //image_action_effectiveDate = (ImageView) v.findViewById(R.id.image_action_effectiveDate);
            //image_action_score = (ImageView) v.findViewById(R.id.image_action_score);
            //image_action_availability = (ImageView) v.findViewById(R.id.image_action_availability);
            text_priceOff = (TextView) v.findViewById(R.id.priceOff);
            text_price = (TextView) v.findViewById(R.id.price);
            image_action_like = (ImageView) v.findViewById(R.id.image_action_like);
            //image_action_heart = (ImageView) v.findViewById(R.id.image_action_heart);
            //image_action_flag = (ImageView) v.findViewById(R.id.image_action_flag);
            image_action_share = (ImageView) v.findViewById(R.id.image_action_share);
            //maintoolbar = (Toolbar) v.findViewById(R.id.card_toolbar);
            //maintoolbar.inflateMenu(R.menu.menu_cardview);
        }
    }
    public void VolleyPostRequest(String url, HashMap<String, String> params, final int callback,final PublicationCardViewModel p,final ViewHolder holder){
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
                        if (callback == METHOD_LIKE)
                            confirmLike(response,p,holder);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                        try {
                            int score = Integer.parseInt(p.getScore());
                            holder.text_score.setText("" + score);
                            holder.image_action_like.setEnabled(true);
                        }catch(NumberFormatException e){}
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
    private void confirmLike(JSONObject response,PublicationCardViewModel p,ViewHolder holder){
        Log.d(TAG,"confirmLike: "+ response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");

            switch (status) {
                case "1":
                    break;
                case "2":
                    ShowConfirmations.showConfirmationMessage(response.getString("message"),activity);
                    Log.d(TAG,response.getString("message").toString());
                    try {
                        int score = Integer.parseInt(p.getScore());
                        holder.text_score.setText(""+score);
                        holder.image_action_like.setEnabled(true);
                    }catch(NumberFormatException e){}

                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                ShowConfirmations.showConfirmationMessage(response.getJSONObject("message").toString(),activity);
            } catch (JSONException e1) {
                e1.printStackTrace();
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