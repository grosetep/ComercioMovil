package estrategiamovil.comerciomovil.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Country;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.ui.activities.CityPreferencesActivity;


/**
 * Created by administrator on 01/06/2016.
 */
public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {
    private final LayoutInflater mInflater;
    private final List<Country> mModels;
    private static final String TAG = CountryAdapter.class.getSimpleName();
    private SharedPreferences mPrefs;
    private final String countryUser = "countryUser";
    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private Context ctx;
    public class CountryViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvText;
        public String mBoundString;

        public CountryViewHolder(View itemView) {
            super(itemView);

            tvText = (TextView) itemView.findViewById(R.id.text_label);

        }

        public void bind(Country model) {
            tvText.setText(model.getCountry());
            mBoundString = model.getIdCountry();
        }


    }
    public CountryAdapter(Context context, List<Country> models) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        ctx = context;
        mBackground = mTypedValue.resourceId;
        mInflater = LayoutInflater.from(context);
        mModels = new ArrayList<>(models);
    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.item_text_layout, parent, false);
        itemView.setBackgroundResource(mBackground);
        return new CountryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CountryViewHolder holder, int position) {
        final Country model = mModels.get(position);
        holder.bind(model);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, CityPreferencesActivity.class);//CityPreferencesActivity

                mPrefs = PreferenceManager.getDefaultSharedPreferences(holder.itemView.getContext());
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString(Constants.countryUser, holder.mBoundString);
                editor.commit(); // Very important to save the preference

                //save country name
                ApplicationPreferences.saveLocalPreference(ctx,Constants.nameCountryUser, model.getCountry());

                String categoriesSelected = mPrefs.getString(Constants.categoriesUser,"");
                if (categoriesSelected!=null && !categoriesSelected.equals(""))
                    intent.putExtra(CityPreferencesActivity.EXTRA_FLOW, Constants.flowChangeCity);
                else
                    intent.putExtra(CityPreferencesActivity.EXTRA_FLOW, "configFlow");

                intent.putExtra(CityPreferencesActivity.EXTRA_NAME, holder.mBoundString);
                intent.putExtra(CityPreferencesActivity.EXTRA_COUNTRY, model.getCountry());
                Log.d(TAG, "idPais: " + holder.mBoundString);
                context.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void animateTo(List<Country> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Country> newModels) {
        for (int i = mModels.size() - 1; i >= 0; i--) {
            final Country model = mModels.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Country> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Country model = newModels.get(i);
            if (!mModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Country> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Country model = newModels.get(toPosition);
            final int fromPosition = mModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Country removeItem(int position) {
        final Country model = mModels.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, Country model) {
        mModels.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Country model = mModels.remove(fromPosition);
        mModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }



}
