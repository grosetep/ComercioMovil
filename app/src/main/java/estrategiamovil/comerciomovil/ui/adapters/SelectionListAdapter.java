package estrategiamovil.comerciomovil.ui.adapters;

import android.content.Context;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Sector;
import estrategiamovil.comerciomovil.ui.activities.SignupActivity;

public class SelectionListAdapter extends BaseAdapter implements SpinnerAdapter {
        private Sector[] sectors;
        private final Context activity;



    public SelectionListAdapter(Context context,Sector[] elements){
            this.sectors = elements;
            activity = context;
        }

    public int getCount()
    {
        return sectors.length;
    }

    public Object getItem(int i)
    {
        return sectors[i];
    }

    public long getItemId(int i)
    {
        return (long)i;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(activity);
        txt.setPadding(8, 8, 8, 8);
        txt.setTextSize(16);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setText(sectors[position].getDescription());
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }

    public View getView(int i, View view, ViewGroup viewgroup) {
        TextView txt = new TextView(activity);
        txt.setGravity(Gravity.CENTER);
        txt.setPadding(8, 8, 8, 8);
        txt.setTextSize(14);
        txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down, 0);
        txt.setText(sectors[i].getDescription());
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }

    }
