package estrategiamovil.comerciomovil.ui.adapters;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Color;
import android.support.v7.app.AlertDialog;

import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import estrategiamovil.comerciomovil.modelo.PublicationUser;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.ui.activities.AskResponseActivity;
import estrategiamovil.comerciomovil.ui.activities.EditPublicationActivity;
import estrategiamovil.comerciomovil.ui.activities.SendQuestionResponseActivity;
import estrategiamovil.comerciomovil.ui.interfaces.OnLoadMoreListener;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * Created by administrator on 11/08/2016.
 */
public class PublicationUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<PublicationUser> publications;
    public static final int METHOD_EDIT_TITLE = 1;
    public static final int METHOD_PAUSE_PUBLICATION = 2;
    public static final int METHOD_ACTIVATE_PUBLICATION = 3;
    public static final int METHOD_REMOVE_PUBLICATION = 4;
    private Activity activity;

    private RecyclerView recyclerview;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView.OnScrollListener listener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    private static final String TAG = PublicationUserAdapter.class.getSimpleName();
    public PublicationUserAdapter(List<PublicationUser> myDataset,Activity activity,RecyclerView list) {
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

        recyclerview.addOnScrollListener(listener);
    }
    public void setLoaded() {
        isLoading = false;
    }
    public void clear(){ publications.clear();notifyDataSetChanged();}
    public void addAll(List<PublicationUser> list){
        if (list!=null && list.size()>0){publications.addAll(list);notifyDataSetChanged(); }
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
                .inflate(R.layout.manage_publication_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
        }else{
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof PublicationUserAdapter.ViewHolder) {
            final PublicationUserAdapter.ViewHolder p_holder = (PublicationUserAdapter.ViewHolder) holder;
            p_holder.status = publications.get(position).getStatus();
            Glide.with(p_holder.image.getContext())
                .load(publications.get(position).getPath() + publications.get(position).getImageName())
                .centerCrop()
                    .into(p_holder.image);
            p_holder.text_coverDescription.setText(publications.get(position).getCoverDescription());
            p_holder.text_availability_value.setText(publications.get(position).getAvailability());
            p_holder.text_offerPrice_value.setText(publications.get(position).getOfferPrice());
            p_holder.text_regularPrice_value.setText(publications.get(position).getRegularPrice());
            p_holder.text_percentageOff_value.setText(publications.get(position).getPercentageOff());
            p_holder.text_active_days.setText((Integer.parseInt(publications.get(position).getActive_days()) <= 0) ? "0" + activity.getResources().getString(R.string.text_promt_days_active) : publications.get(position).getActive_days() + activity.getResources().getString(R.string.text_promt_days_active));
            p_holder.text_questions.setText(publications.get(position).getPendingQuestions());
            p_holder.text_approval_detail.setText(publications.get(position).getApprovalDetail());
            p_holder.text_approval_status.setText(publications.get(position).getStatus());
            p_holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Intent intent = new Intent(activity,AskResponseActivity.class);
                        intent.putExtra(AskResponseActivity.ID_PUBLICATION,publications.get(position).getIdPublication());
                        intent.putExtra(AskResponseActivity.ANSWERABLE,String.valueOf(true));
                        activity.startActivity(intent);
            }
            });
            p_holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    showPopupMenu(p_holder, position);
            }
        });
            //se TextColor based on status
            switch (publications.get(position).getStatus()) {
                case Constants.status_active:
                    p_holder.text_approval_status.setTextColor(Color.parseColor(Constants.colorSuccess));
                    p_holder.layout_approval_detail.setVisibility(View.GONE);
                    break;
                case Constants.status_paused:
                    p_holder.text_approval_status.setTextColor(Color.parseColor(Constants.colorWarning));
                    p_holder.layout_approval_detail.setVisibility(View.GONE);
                    break;
                case Constants.status_pending:
                case Constants.status_rejected:
                case Constants.status_new:
                    p_holder.text_approval_status.setTextColor(Color.parseColor(Constants.colorWarning));
                    p_holder.layout_approval_detail.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }


        //setBackground based on status
        if (publications.get(position).getStatus().equals(Constants.status_paused) ) {
                p_holder.layout_over.setVisibility(View.VISIBLE);
                p_holder.text_over.setText(activity.getString(R.string.text_publication_paused));

            } else if (Integer.parseInt(publications.get(position).getActive_days()) <= 0) {
                p_holder.layout_over.setVisibility(View.VISIBLE);//Finalizada
                p_holder.text_over.setText(activity.getString(R.string.text_promt_finished));
        }else{
                p_holder.layout_over.setVisibility(View.GONE);
                p_holder.overflow.setEnabled(true);
            }
        }else if(holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }
    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(ViewHolder view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.overflow.getContext(), view.overflow);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_publication_user, popup.getMenu());
        if (Integer.parseInt(publications.get(position).getActive_days())<=0){//finalizada
            popup.getMenu().findItem(R.id.action_update).setEnabled(false);
            popup.getMenu().findItem(R.id.action_edit_publication).setEnabled(false);
            popup.getMenu().findItem(R.id.action_pause_publication).setEnabled(false);
            popup.getMenu().findItem(R.id.action_activate_publication).setEnabled(false);
            popup.getMenu().findItem(R.id.action_response).setEnabled(false);
        }
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
        public int getPosition(){return position;}
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
                case R.id.action_update:
                    ShowRegister_Popup();
                    return true;
                case R.id.action_edit_publication:
                    Intent i = new Intent(activity,EditPublicationActivity.class);
                    i.putExtra(EditPublicationActivity.PARAM_USER_PUBLICATION,publications.get(position));
                    activity.startActivity(i);
                    return true;
                case R.id.action_pause_publication:
                    pausePublication(publications.get(position).getIdPublication(), holder.overflow.getContext(), holder, getPosition());
                    return true;
                case R.id.action_activate_publication:
                    activatePublication(publications.get(position).getIdPublication(), holder.overflow.getContext(), holder, getPosition());
                    return true;
                case R.id.action_remove_publication:
                    removePublication(publications.get(position).getIdPublication(), holder.overflow.getContext(), holder, getPosition());
                    return true;
                case R.id.action_response:
                    Intent intent = new Intent(activity,AskResponseActivity.class);
                    intent.putExtra(AskResponseActivity.ID_PUBLICATION,publications.get(position).getIdPublication());
                    intent.putExtra(AskResponseActivity.ANSWERABLE,String.valueOf(true));
                    activity.startActivity(intent);
                    return true;
                default:
            }
            return false;
        }

        private void ShowRegister_Popup() {

            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(holder.overflow.getContext());
            final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_update_publication, null);
            AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(holder.overflow.getContext());
            alertDialogBuilderUserInput.setView(mView);

            final EditText text_new_card_name = (EditText) mView.findViewById(R.id.text_new_card_name);
            text_new_card_name.setText(holder.text_coverDescription.getText().toString());
            ((TextView)mView.findViewById(R.id.text_card_name)).setText(holder.text_coverDescription.getText().toString());
            Glide.with(holder.image.getContext())
                    .load(publications.get(position).getPath() + publications.get(position).getImageName())
                    .centerCrop()
                    .into((ImageView)mView.findViewById(R.id.image_card_cover));

            alertDialogBuilderUserInput
                    .setCancelable(false)
                    .setPositiveButton(holder.overflow.getContext().getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogBox, int id) {
                            boolean valid = true;
                            if (text_new_card_name.getText().toString().trim().isEmpty()) {

                                text_new_card_name.setError(holder.overflow.getContext().getResources().getString(R.string.error_field_required));
                                valid = false;
                            } else {
                                text_new_card_name.setError(null);
                                valid = true;
                            }

                            if (valid) {
                                ((ProgressBar) mView.findViewById(R.id.pbLoading_update)).setVisibility(View.VISIBLE);
                                updatePublicationDesc(text_new_card_name.getText().toString().trim(), publications.get(position).getIdPublication(), holder.overflow.getContext(), dialogBox, holder,mView,getPosition());
                            }
                        }
                    })

                    .setNegativeButton(holder.overflow.getContext().getResources().getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {
                                    dialogBox.cancel();
                                }
                            });

            AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
            alertDialogAndroid.show();
        }

    private void updatePublicationDesc(String description, String idPublication,Context ctx, DialogInterface dialog, ViewHolder holder,View mView,int position){
        HashMap<String,String> params = new HashMap<>();
        params.put("idPublication", idPublication);
        params.put("description", description);
        params.put("method", "updatePublication");
            VolleyPostRequest(Constants.GET_PUBLICATIONS, params, ctx, dialog, holder,mView, position,METHOD_EDIT_TITLE);
    }
        private void pausePublication(String idPublication,Context ctx, ViewHolder holder,int position){
            HashMap<String,String> params = new HashMap<>();
            params.put("idPublication", idPublication);
            params.put("method", "update_status_publication");
            params.put("operation", "pause");
            VolleyPostRequest(Constants.GET_PUBLICATIONS, params, ctx, null, holder,null, position,METHOD_PAUSE_PUBLICATION);
        }
        private void activatePublication(String idPublication,Context ctx, ViewHolder holder,int position){
            HashMap<String,String> params = new HashMap<>();
            params.put("idPublication", idPublication);
            params.put("method", "update_status_publication");
            params.put("operation", "activate");
            VolleyPostRequest(Constants.GET_PUBLICATIONS, params, ctx, null, holder,null, position,METHOD_ACTIVATE_PUBLICATION);
        }
        private void removePublication(String idPublication,Context ctx, ViewHolder holder, int position){
            HashMap<String,String> params = new HashMap<>();
            params.put("method", "update_status_publication");
            params.put("idPublication", idPublication);
            params.put("operation", "inactive");
            VolleyPostRequest(Constants.GET_PUBLICATIONS, params, ctx, null,holder,null, position,METHOD_REMOVE_PUBLICATION);
        }
        public void VolleyPostRequest(String url, final HashMap<String, String> args, final Context ctx, final DialogInterface dialog, final ViewHolder holder,final View mView,final int position,final int callback) {
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
                                    switch (callback) {
                                        case METHOD_EDIT_TITLE:
                                processResponse(response, dialog, holder, ctx,mView,position);
                                            break;
                                        case METHOD_PAUSE_PUBLICATION:
                                            processUpdateStatusResponse(response, dialog, holder, ctx, mView, position,METHOD_PAUSE_PUBLICATION);
                                            break;
                                        case METHOD_ACTIVATE_PUBLICATION:
                                            processUpdateStatusResponse(response, dialog, holder, ctx, mView, position,METHOD_ACTIVATE_PUBLICATION);
                                            break;
                                        case METHOD_REMOVE_PUBLICATION:
                                            processUpdateStatusResponse(response, dialog, holder, ctx, mView, position,METHOD_REMOVE_PUBLICATION);
                                            break;
                                    }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                                ShowConfirmations.showConfirmationMessage(activity.getResources().getString(R.string.generic_server_down),activity);
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

        private void processUpdateStatusResponse(JSONObject response,final DialogInterface dialog, ViewHolder holder, Context ctx,View mView, int position, int callback) {
            Log.d(TAG, "response:" + response.toString());

            try {
                // Obtener atributo "mensaje"
                String message = response.getString("status");
                switch (message) {
                    case "1"://assign values
                        String object = response.getString("result");
                        if (callback == METHOD_REMOVE_PUBLICATION){
                            publications.remove(position);
                            notifyItemRemoved(holder.getAdapterPosition());
                            notifyItemRangeChanged(position, getItemCount());
                        }else if(callback == METHOD_PAUSE_PUBLICATION){
                            holder.text_over.setText(activity.getString(R.string.text_publication_paused));
                            holder.layout_over.setVisibility(View.VISIBLE);
                            PublicationUser pub = publications.get(position);pub.setStatus(Constants.status_paused);
                        }else if(callback == METHOD_ACTIVATE_PUBLICATION){
                            holder.layout_over.setVisibility(View.GONE);
                            PublicationUser pub = publications.get(position);pub.setStatus(Constants.status_active);


                        }
                        ShowConfirmations.showConfirmationMessage(activity.getResources().getString(R.string.text_prompt_profile_updated),activity);
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
        private void onError(JSONObject response){
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



    private void processResponse(JSONObject response,final DialogInterface dialog, ViewHolder holder, Context ctx,View mView, int position) {
        try {
            Log.d(TAG,"response: " + response.toString());
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    // update text holder, hide progressBar and close
                    holder.text_coverDescription.setText(((EditText) mView.findViewById(R.id.text_new_card_name)).getText().toString());
                    ((ProgressBar)mView.findViewById(R.id.pbLoading_update)).setVisibility(View.GONE);
                    ShowConfirmations.showConfirmationMessage(activity.getResources().getString(R.string.text_prompt_profile_updated),activity);
                    PublicationUser ad =  publications.get(position);
                    if (ad!=null){ad.setCoverDescription(((EditText) mView.findViewById(R.id.text_new_card_name)).getText().toString());}
                    notifyItemChanged(holder.getAdapterPosition());
                    dialog.cancel();
                    break;
                case "2": // FALLIDO
                    ((ProgressBar)mView.findViewById(R.id.pbLoading_update)).setVisibility(View.GONE);
                    dialog.dismiss();
                    String mensaje2 = response.getString("message");
                    Toast.makeText(
                            ctx,
                            mensaje2,
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

    }
    @Override
    public int getItemCount() {
        return publications == null ? 0 : publications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout layout_over;
        public ImageView overflow;
        public ImageView image;
        public TextView text_coverDescription;
        public TextView text_regularPrice_value;
        public TextView text_offerPrice_value;
        public TextView text_percentageOff_value;
        public TextView text_availability_value;
        public TextView text_active_days;
        public TextView text_over;
        public TextView text_questions;
        public String status;
        public TextView text_approval_status;
        public TextView text_approval_detail;
        private LinearLayout layout_approval_detail;

        public ViewHolder(View v) {
            super(v);
            layout_approval_detail = (LinearLayout) v.findViewById(R.id.layout_approval_detail);
            text_approval_status = (TextView) v.findViewById(R.id.text_approval_status);
            text_approval_detail = (TextView) v.findViewById(R.id.text_approval_detail);
            layout_over = (RelativeLayout) v.findViewById(R.id.layout_over);
            text_over = (TextView) v.findViewById(R.id.text_over);
            image = (ImageView) v.findViewById(R.id.image);
            text_coverDescription = (TextView) v.findViewById(R.id.text_coverDescription);
            text_regularPrice_value = (TextView) v.findViewById(R.id.text_regularPrice_value);
            text_offerPrice_value =  (TextView) v.findViewById(R.id.text_offerPrice_value);
            text_percentageOff_value = (TextView) v.findViewById(R.id.text_percentageOff_value);
            text_availability_value =  (TextView) v.findViewById(R.id.text_availability_value);
            text_active_days = (TextView)v.findViewById(R.id.text_active_days);
            text_questions = (TextView) v.findViewById(R.id.text_questions);
            overflow = (ImageView) v.findViewById(R.id.overflow);
            status = "";
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
