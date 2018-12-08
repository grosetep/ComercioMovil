package estrategiamovil.comerciomovil.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.City;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.ui.activities.CategoryPreferencesActivity;
import estrategiamovil.comerciomovil.ui.activities.FindMyBusinessActivity;
import estrategiamovil.comerciomovil.ui.activities.MainActivity;
import estrategiamovil.comerciomovil.ui.activities.SearchActivity;
import estrategiamovil.comerciomovil.ui.fragments.CardsFragment;

/**
 * Created by administrator on 01/06/2016.
 */
public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {
    private final LayoutInflater mInflater;
    private final List<City> mModels;
    private HashMap<String, String> params;
    private static final String TAG = CityAdapter.class.getSimpleName();
    private SharedPreferences mPrefs;
    private int mBackground;
    private final TypedValue mTypedValue = new TypedValue();
    private Context ctx;
    public class CityViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvText;
        public String mBoundString;

        public CityViewHolder(View itemView) {
            super(itemView);

            tvText = (TextView) itemView.findViewById(R.id.text_label);

        }

        public void bind(City model) {
            tvText.setText(model.getCity());
            mBoundString = model.getIdCity();
        }


    }

    public CityAdapter(Context context, List<City> models, HashMap<String, String> params) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mInflater = LayoutInflater.from(context);
        mModels = new ArrayList<>(models);
        ctx = context;
        this.params = params;
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.item_text_layout, parent, false);
        itemView.setBackgroundResource(mBackground);
        return new CityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CityViewHolder holder, int position) {
        final City model = mModels.get(position);
        holder.bind(model);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //guardar preferencia
                boolean save = true;
                Context context = v.getContext();
                ApplicationPreferences.saveLocalPreference(context, Constants.nameCityUser, model.getCity());
                Intent intentCities = null;
                if (params.get("labelFlow").toString().equals(Constants.find_flow)){
                    Intent intent = new Intent();
                    Bundle args = new Bundle();
                    args.putSerializable(Constants.CITY_SELECTED, model);
                    intent.putExtras(args);
                    ((Activity)ctx).setResult(Activity.RESULT_OK,intent);
                    ((Activity)ctx).finish();
                    save = false;

                }else if (params.get("labelFlow") != null && params.get("labelFlow").equalsIgnoreCase(Constants.flowChangeCity)) {
                    intentCities = new Intent(context, MainActivity.class);
                    CardsFragment.locationChanged = true;
                }else {
                    intentCities = new Intent(context, CategoryPreferencesActivity.class);
                    CardsFragment.locationChanged = true;
                }

                Log.d(TAG, "idEstado: " + holder.mBoundString);

                if (save) {
                    mPrefs = PreferenceManager.getDefaultSharedPreferences(holder.itemView.getContext());
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString(Constants.cityUser, holder.mBoundString);
                    editor.commit();
                    context.startActivity(intentCities);
                }

            }
        });


    }


    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void animateTo(List<City> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<City> newModels) {
        for (int i = mModels.size() - 1; i >= 0; i--) {
            final City model = mModels.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<City> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final City model = newModels.get(i);
            if (!mModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<City> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final City model = newModels.get(toPosition);
            final int fromPosition = mModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public City removeItem(int position) {
        final City model = mModels.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, City model) {
        mModels.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final City model = mModels.remove(fromPosition);
        mModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

}
