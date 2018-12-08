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
import estrategiamovil.comerciomovil.modelo.City;
import estrategiamovil.comerciomovil.modelo.Sector;

/**
 * Created by administrator on 29/07/2016.
 */
public class CitiesSpinnerAdapter extends ArrayAdapter<String> {

    private Activity activity;
    private ArrayList data;

    City tempValues=null;
    LayoutInflater inflater;


    public CitiesSpinnerAdapter(
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
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.spinner_rows, parent, false);
        tempValues = null;
        tempValues = (City) data.get(position);

        TextView label        = (TextView)row.findViewById(R.id.sector_description);
        TextView id          = (TextView)row.findViewById(R.id.sector);
        ImageView image = (ImageView)row.findViewById(R.id.image);
        View spinner_divider = (View) row.findViewById(R.id.spinner_divider);


        /*if(position==0){

            // Default selected Spinner item
            label.setText(R.string.prompt_select_city);
            id.setText("");
            image.setVisibility(View.GONE);
            spinner_divider.setVisibility(View.GONE);

        }
        else
        {*/
            // Set values for spinner each row
            label.setText(tempValues.getCity());
            id.setText(tempValues.getIdCity());

       // }

        return row;
    }
}
