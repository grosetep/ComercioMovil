package estrategiamovil.comerciomovil.ui.adapters;

/**
 * Created by administrator on 01/09/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.PublicationCardViewModel;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.StringOperations;
import estrategiamovil.comerciomovil.ui.activities.DetailPublicationActivity;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;

public class SolventRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PublicationCardViewModel> itemList;
    private Activity activity;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView.OnScrollListener listener;
    private RecyclerView recyclerview;
    private boolean isLoading=false;
    private int pastVisibleItems, visibleItemCount, totalItemCount;

    public SolventRecyclerViewAdapter(Activity context, List<PublicationCardViewModel> itemList,RecyclerView list) {
        this.itemList = itemList;
        this.activity = context;
        recyclerview = list;


        final StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) recyclerview.getLayoutManager();
        listener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                visibleItemCount = staggeredGridLayoutManager.getChildCount();//total de items
                totalItemCount = staggeredGridLayoutManager.getItemCount();

                int[] firstVisibleItems = null;
                firstVisibleItems = staggeredGridLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                if(firstVisibleItems != null && firstVisibleItems.length > 0) {
                    pastVisibleItems = firstVisibleItems[0];//ultimos elementos visibles
                }
                if (dy>0) {
                    Log.d("tag", "onScrolled...visibleItemCount:" + visibleItemCount + " totalItemCount:" + totalItemCount + " pastVisibleItems:" + pastVisibleItems);
                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            if (mOnLoadMoreListener != null) {
                                Log.d("tag", "LOAD NEXT ITEM");
                                mOnLoadMoreListener.onLoadMore();
                                isLoading = true;
                            }

                        }
                    }
                }

            }
        };
        recyclerview.addOnScrollListener(listener);
    }
    public void resetListener(){  this.mOnLoadMoreListener = null;}
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public void clear(){
        if (itemList!=null){
        itemList.clear();
        notifyDataSetChanged();}
    }
    public void addAll(List<PublicationCardViewModel> list){
        if (list!=null && list.size()>0){itemList.addAll(list);notifyDataSetChanged(); }
    }
    @Override
    public int getItemViewType(int position) {
        return itemList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.solvent_list_layout, null);
            SolventViewHolders rcv = new SolventViewHolders(layoutView);
            return rcv;
        }else{
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SolventViewHolders) {
            final SolventRecyclerViewAdapter.SolventViewHolders s_holder = (SolventRecyclerViewAdapter.SolventViewHolders)holder;
            s_holder.cover_ads.setText(itemList.get(position).getCoverDescription());
            s_holder.text_ads_price.setText(StringOperations.getAmountFormat(itemList.get(position).getOfferPrice()));
            final String ImagePath = itemList.get(position).getPath() + itemList.get(position).getImageName();
            s_holder.idPublication = itemList.get(position).getIdPublication();
            s_holder.company = itemList.get(position).getCompany();

            Glide.with(activity)
                    .load(ImagePath)
                    .fitCenter()
                    .into(s_holder.photo_ads);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailPublicationActivity.class);
                    intent.putExtra(DetailPublicationActivity.EXTRA_PUBLICACION, s_holder.idPublication);
                    intent.putExtra(DetailPublicationActivity.EXTRA_TITLE, s_holder.company);
                    intent.putExtra(DetailPublicationActivity.EXTRA_IMAGETITLE, ImagePath);
                    intent.putExtra(DetailPublicationActivity.EXTRA_TYPE_PUBLICATION, Constants.type_publication_ads);
                    context.startActivity(intent);
                }
            });
        }else if(holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return this.itemList == null ? 0 : this.itemList.size();
    }
    public void setLoaded() {
        isLoading = false;
    }
    public class SolventViewHolders extends RecyclerView.ViewHolder {//implements View.OnClickListener

        public TextView cover_ads;
        public ImageView photo_ads;
        public String idPublication;
        public String company;
        public TextView text_ads_price;
        public SolventViewHolders(View itemView) {
            super(itemView);
            //itemView.setOnClickListener(this);
            cover_ads = (TextView) itemView.findViewById(R.id.cover_ads);
            photo_ads = (ImageView) itemView.findViewById(R.id.photo_ads);
            idPublication = "";
            company = "";
            text_ads_price =  (TextView) itemView.findViewById(R.id.text_ads_price);
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
