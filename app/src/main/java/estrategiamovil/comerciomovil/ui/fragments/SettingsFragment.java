package estrategiamovil.comerciomovil.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.tools.FireBaseOperations;
import estrategiamovil.comerciomovil.ui.activities.CategoryPreferencesActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private static final String TAG = SettingsFragment.class.getSimpleName();
    private LinearLayout layout_notifications;
    private LinearLayout layout_categories;
    private Switch switch_notifications;
    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        switch_notifications = (Switch) v.findViewById(R.id.switch_notifications);
        layout_notifications = (LinearLayout) v.findViewById(R.id.layout_notifications);
        layout_categories = (LinearLayout) v.findViewById(R.id.layout_categories);
        layout_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch_notifications.isChecked()) {
                    switch_notifications.setChecked(false);
                    FireBaseOperations.subscribeUser(getActivity(),false);
                }else {
                    FireBaseOperations.subscribeUser(getActivity(),true);
                    switch_notifications.setChecked(true);
                }
            }
        });
        layout_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), CategoryPreferencesActivity.class);
                i.putExtra(CategoryPreferencesActivity.SETTINGS_FLOW,CategoryPreferencesActivity.SETTINGS_FLOW);
                startActivity(i);
            }
        });
        return v;
    }

}
