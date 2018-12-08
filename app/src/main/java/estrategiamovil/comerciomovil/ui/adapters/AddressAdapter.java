package estrategiamovil.comerciomovil.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Address;

/**
 * Created by administrator on 08/08/2016.
 */
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
private final LayoutInflater mInflater;
private final ArrayList<Address> mModels;
private static final String TAG = AddressAdapter.class.getSimpleName();

private int mBackground;
private final TypedValue mTypedValue = new TypedValue();

public class AddressViewHolder extends RecyclerView.ViewHolder {

    private final TextView text_title;
    private final TextView text_description;


    public AddressViewHolder(View itemView) {
        super(itemView);

        text_title = (TextView) itemView.findViewById(R.id.text_title);
        text_description = (TextView) itemView.findViewById(R.id.text_address);

    }

    public void bind(Address model) {
        text_title.setText(model.getShortName());
        text_description.setText(model.getGoogleAddress());
    }


}

    public AddressAdapter(Context context,ArrayList<Address> models) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mInflater = LayoutInflater.from(context);
        mModels = new ArrayList<>(models);
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.item_address_layout, parent, false);
        itemView.setBackgroundResource(mBackground);
        return new AddressViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AddressViewHolder holder, int position) {
        final Address model = mModels.get(position);
        holder.bind(model);
    }


    @Override
    public int getItemCount() {
        return mModels.size();
    }

}
