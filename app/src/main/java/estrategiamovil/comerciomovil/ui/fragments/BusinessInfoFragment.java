package estrategiamovil.comerciomovil.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import estrategiamovil.comerciomovil.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BusinessInfoFragment extends Fragment {
    public static final String INFO_COMPANY = "company";
    private String info;
    public BusinessInfoFragment() {
        // Required empty public constructor
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public static BusinessInfoFragment getInstance(String info){
        BusinessInfoFragment fragment = new BusinessInfoFragment();
        Bundle args = new Bundle();
        args.putString(INFO_COMPANY,info);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            setInfo(getArguments().getString(INFO_COMPANY));
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_business_info, container, false);
        TextView text = (TextView) v.findViewById(R.id.text);
        if (info.length()>0)
            text.setText(info);
        else
            text.setText(getString(R.string.promt_business_info_error));
        return v;
    }

}
