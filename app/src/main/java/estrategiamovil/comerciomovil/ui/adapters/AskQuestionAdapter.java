package estrategiamovil.comerciomovil.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.AskQuestion;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.ui.activities.SendQuestionResponseActivity;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;

/**
 * Created by administrator on 27/04/2017.
 */
public class AskQuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<AskQuestion> asks;
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView.OnScrollListener listener;
    private boolean isLoading;
    private Activity activity;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView recyclerview;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private static final String TAG = AskQuestionAdapter.class.getSimpleName();

    public AskQuestionAdapter(List<AskQuestion> myDataset,Activity activity,RecyclerView list) {
        asks = myDataset;
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

        recyclerview.addOnScrollListener(listener);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ask_question_layout, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }else{
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
    }
    @Override
    public int getItemViewType(int position) {
        return asks.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AskQuestionAdapter.ViewHolder) {
            final AskQuestionAdapter.ViewHolder a_holder = (AskQuestionAdapter.ViewHolder) holder;
            a_holder.answerable = asks.get(position).getAnswerable();
            a_holder.text_question.setText(asks.get(position).getQuestion());
            a_holder.text_response.setText(asks.get(position).getResponse() != null ? asks.get(position).getResponse() : activity.getString(R.string.promt_no_response_yet));
            String days = asks.get(position).getDays();
            //set tint based on status question
            if (asks.get(position).getAnswerable().equals(String.valueOf(true))) {//pregunta aun no contestada se resalta solo para merchants
                if (asks.get(position).getAnswered().equals(String.valueOf(Constants.cero))) {
                    a_holder.text_response.setTextColor(Color.parseColor(Constants.colorError));
                } else {
                    a_holder.text_response.setTextColor(Color.parseColor(Constants.colorTextGray));
                }
            }
            if (days != null && days.length() > 0) {
                int num_days = Integer.parseInt(days);
                a_holder.text_date.setText(num_days <= 0 ? activity.getString(R.string.text_promt_today) : num_days == 1 ? activity.getString(R.string.text_promt_days_from) + num_days + activity.getString(R.string.text_promt_1_day_active) : activity.getString(R.string.text_promt_days_from) + num_days + activity.getString(R.string.text_promt_days_active));

            }
            if (asks.get(position).getAnswerable().equals(String.valueOf(true)) && asks.get(position).getAnswered().toString().equals(String.valueOf(Constants.cero))) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(activity, SendQuestionResponseActivity.class);
                        i.putExtra(SendQuestionResponseActivity.ID_PUBLICATION, asks.get(position).getIdPublication());
                        i.putExtra(SendQuestionResponseActivity.ANSWERABLE, asks.get(position).getAnswerable());
                        i.putExtra(SendQuestionResponseActivity.ID_QUESTION, asks.get(position).getIdAsk());
                        i.putExtra(SendQuestionResponseActivity.FLOW,"response");
                        activity.startActivity(i);
                    }
                });
            }
        }else if(holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return asks == null ? 0 : asks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView text_question;
        private TextView text_response;
        private TextView text_date;
        private String answerable;

        public ViewHolder(View v) {
            super(v);
            text_question = (TextView) v.findViewById(R.id.text_question);
            text_response = (TextView) v.findViewById(R.id.text_response);
            text_date = (TextView) v.findViewById(R.id.text_date);
            answerable="";
        }
    }
    public void setLoaded() {
        isLoading = false;
    }
    public void clear(){ asks.clear();notifyDataSetChanged();}
    public void addAll(List<AskQuestion> list){
        if (list!=null && list.size()>0){asks.addAll(list);notifyDataSetChanged(); }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }
    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }
}
