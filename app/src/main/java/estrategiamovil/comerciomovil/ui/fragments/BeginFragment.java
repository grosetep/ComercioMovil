package estrategiamovil.comerciomovil.ui.fragments;


import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.ui.activities.CountryPreferencesActivity;
import estrategiamovil.comerciomovil.ui.activities.StartActivity;


public class BeginFragment extends Fragment {


    public BeginFragment() {   }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_begin, container, false);
        Button button = (Button) v.findViewById(R.id.config_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), CountryPreferencesActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }


}
