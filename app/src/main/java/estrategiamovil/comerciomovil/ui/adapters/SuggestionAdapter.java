package estrategiamovil.comerciomovil.ui.adapters;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;


import java.util.ArrayList;
import java.util.List;

import estrategiamovil.comerciomovil.modelo.Category;
import estrategiamovil.comerciomovil.tools.JsonParse;

/**
 * Created by administrator on 09/02/2017.
 */
public class SuggestionAdapter extends ArrayAdapter<String> implements Filterable {

    protected static final String TAG = "SuggestionAdapter";
    private List<String> suggestions;

    public SuggestionAdapter(Activity context) {
        super(context, android.R.layout.simple_dropdown_item_1line);
        suggestions = new ArrayList<String>();

    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public String getItem(int index) {
        return suggestions.get(index);
    }


    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                JsonParse jp=new JsonParse();
                List<String> new_suggestions = jp.getParseJsonWCF(constraint!=null?constraint.toString():"");
                if (constraint != null && new_suggestions!=null && new_suggestions.size()>0) {

                    suggestions.clear();
                    for (int i=0;i<new_suggestions.size();i++) {
                        suggestions.add(new_suggestions.get(i));
                    }

                    // Now assign the values and count to the FilterResults
                    // object
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint,
                                          FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return myFilter;
    }

}