package estrategiamovil.comerciomovil.ui.adapters;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.RatingItem;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;

/**
 * Created by administrator on 25/01/2017.
 */
public class MerchantRatingAdapter extends RecyclerView.Adapter<MerchantRatingAdapter.ViewHolder>{
    private List<RatingItem> ratings;
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView.OnScrollListener listener;
    private boolean isLoading;
    private Activity activity;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView recyclerview;
    private static final String TAG = MerchantRatingAdapter.class.getSimpleName();

    public MerchantRatingAdapter(List<RatingItem> myDataset,Activity activity,RecyclerView list) {
        ratings = myDataset;
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
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.merchant_rating_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String personType = ratings.get(position).getPersonType();
        String name = ratings.get(position).getName();
        String company = ratings.get(position).getCompany();
        String stars = ratings.get(position).getStars();

        holder.text_comment.setText("\" "+ratings.get(position).getComment()+" \"");
        holder.text_name_or_company.setText(personType.equals("moral")?company:name);
        holder.text_date.setText(ratings.get(position).getDateCreation());
        holder.rating_purchase.setRating(stars!=null?Integer.parseInt(stars):0);
    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView text_comment;
        private RatingBar rating_purchase;
        private TextView text_name_or_company;
        private TextView text_date;
        public ViewHolder(View v) {
            super(v);
            text_comment = (TextView) v.findViewById(R.id.text_comment);
            rating_purchase = (RatingBar) v.findViewById(R.id.rating_purchase);
            text_name_or_company = (TextView) v.findViewById(R.id.text_name_or_company);
            text_date = (TextView) v.findViewById(R.id.text_date);
        }
    }
    public void setLoaded() {
        isLoading = false;
    }
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }
}
