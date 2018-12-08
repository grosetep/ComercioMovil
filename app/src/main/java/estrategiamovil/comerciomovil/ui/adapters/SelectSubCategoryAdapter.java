package estrategiamovil.comerciomovil.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.SubCategory;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.ui.activities.PublishActivity;
import estrategiamovil.comerciomovil.ui.activities.SelectSubCategoryActivity;

/**
 * Created by administrator on 18/08/2016.
 */
/*
public class SelectSubCategoryAdapter extends RecyclerView.Adapter<SelectSubCategoryAdapter.SubSubCategoryViewHolder> {
    private static final String TAG = SelectSubCategoryAdapter.class.getSimpleName();
    private final LayoutInflater mInflater;
    private final List<SubCategory> mModels;
    private int mBackground;
    private final TypedValue mTypedValue = new TypedValue();
    private Activity activityContext;

    public class SubSubCategoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView text_label;
        private String idSubCategory;
        private String idCategory;


        public SubSubCategoryViewHolder(View itemView) {
            super(itemView);
            text_label = (TextView) itemView.findViewById(R.id.text_label);
            idSubCategory = "";
            idCategory = "";
        }

        public void bind(SubCategory model) {
            text_label.setText(model.getSubcategory());
            idSubCategory = model.getIdSubCategory();
            idCategory = model.getIdCategory();
        }


    }

    public SelectSubCategoryAdapter(Activity context, List<SubCategory> models) {
        activityContext = context;
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mInflater = LayoutInflater.from(context);
        mModels = new ArrayList<>(models);
    }

    @Override
    public SubSubCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.item_text_layout, parent, false);
        itemView.setBackgroundResource(mBackground);
        return new SubSubCategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SubSubCategoryViewHolder holder, int position) {
        final SubCategory model = mModels.get(position);
        holder.bind(model);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activityContext, PublishActivity.class);
                intent.putExtra(Constants.CATEGORY_SELECTED,model.getIdCategory());
                intent.putExtra(Constants.SUBCATEGORY_SELECTED, model.getIdSubCategory());
                activityContext.setResult(Activity.RESULT_OK, intent);
                activityContext.finish();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void animateTo(List<SubCategory> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<SubCategory> newModels) {
        for (int i = mModels.size() - 1; i >= 0; i--) {
            final SubCategory model = mModels.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<SubCategory> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final SubCategory model = newModels.get(i);
            if (!mModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<SubCategory> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final SubCategory model = newModels.get(toPosition);
            final int fromPosition = mModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public SubCategory removeItem(int position) {
        final SubCategory model = mModels.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, SubCategory model) {
        mModels.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final SubCategory model = mModels.remove(fromPosition);
        mModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }


}*/
