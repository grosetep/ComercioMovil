package estrategiamovil.comerciomovil.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.SuscriptionPrice;
import estrategiamovil.comerciomovil.tools.StringOperations;

/**
 * Created by administrator on 28/09/2016.
 */
public class GenericSppinerAdapter extends ArrayAdapter<String> {

    private Activity activity;
    private ArrayList data;

    SuscriptionPrice tempValues=null;
    LayoutInflater inflater;


    public GenericSppinerAdapter(
            Activity activitySpinner,
            int textViewResourceId,
            ArrayList objects
    )
    {
        super(activitySpinner, textViewResourceId, objects);
        activity = activitySpinner;
        data     = objects;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.generic_spinner_rows, parent, false);
        tempValues = null;
        tempValues = (SuscriptionPrice) data.get(position);

        TextView label        = (TextView)row.findViewById(R.id.description);
        TextView id          = (TextView)row.findViewById(R.id.value);
        ImageView image = (ImageView)row.findViewById(R.id.image);
        View spinner_divider = (View) row.findViewById(R.id.spinner_divider);
        // Set values for spinner each row
        if (!tempValues.getId().equals("0")) {
            label.setText(StringOperations.getAmountFormat(tempValues.getCost()) + " - " + tempValues.getDescription());
            id.setText(tempValues.getId());
        }else{
            label.setText(tempValues.getDescription());
            id.setText(tempValues.getId());
        }

        return row;
    }

}
