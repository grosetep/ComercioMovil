package estrategiamovil.comerciomovil.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.BusinessInfo;
import estrategiamovil.comerciomovil.tools.ContactActionsUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class MerchantContactFragment extends Fragment {
    private static final String TAG = MerchantContactFragment.class.getSimpleName();
    private TextView text_merchant_email;
    private TextView text_merchant_phone;
    private TextView text_merchant_web;
    //private TextView text_merchant_loc;
    private BusinessInfo businessInfo;
    private LinearLayout layout_email;
    private LinearLayout layout_phone;
    private LinearLayout layout_web;
    private LinearLayout locations_container;
    public static final String MERCHANT = "merchant";
    public MerchantContactFragment() {
        // Required empty public constructor
    }

    public BusinessInfo getBusinessInfo() {
        return businessInfo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            businessInfo = (BusinessInfo) getArguments().getSerializable(MERCHANT);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=  inflater.inflate(R.layout.fragment_merchant_contact, container, false);
        initGUI(v);
        text_merchant_email.setText(businessInfo.getEmail()!=null?businessInfo.getEmail():getString(R.string.promt_error_no_available));
        text_merchant_phone.setText(businessInfo.getPhone()!=null?businessInfo.getPhone():getString(R.string.promt_error_no_available));
        text_merchant_web.setText(businessInfo.getWeb()!=null && businessInfo.getWeb().length()>0?businessInfo.getWeb():getString(R.string.promt_error_no_available));
        String[] locations = getBusinessInfo().getLocations().split("=");
        Log.d(TAG,"locations:"+locations.length);
        generateLocationsList(locations_container,locations);
        return v;
    }
    private void generateLocationsList(LinearLayout container,String[] elements){
        for(String loc:elements){
            TextView text_temp = new TextView(getActivity());
            text_temp.setText(loc);
            text_temp.setClickable(true);
            text_temp.setPadding(0, 8, 0, 8);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 15, 0, 0);
            text_temp.setLayoutParams(params);
            container.addView(text_temp);
        }
    }
    public static MerchantContactFragment newInstance(BusinessInfo businessInfo) {
        MerchantContactFragment fragment = new MerchantContactFragment();
        Bundle args = new Bundle();
        args.putSerializable(MerchantListPublicationsFragment.MERCHANT,businessInfo);
        fragment.setArguments(args);
        return fragment;
    }
    private void initGUI(View v){
        text_merchant_email = (TextView) v.findViewById(R.id.text_merchant_email);
        text_merchant_phone = (TextView) v.findViewById(R.id.text_merchant_phone);
        text_merchant_web = (TextView) v.findViewById(R.id.text_merchant_web);
        //text_merchant_loc = (TextView) v.findViewById(R.id.text_merchant_loc);
        layout_email = (LinearLayout) v.findViewById(R.id.layout_email);
        layout_phone = (LinearLayout) v.findViewById(R.id.layout_phone);
        layout_web = (LinearLayout) v.findViewById(R.id.layout_web);
        locations_container = (LinearLayout) v.findViewById(R.id.location_container);
        layout_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getBusinessInfo().getEmail()!=null && getBusinessInfo().getEmail().length()>0)
                    ContactActionsUtil.ShowEmail_Popup(getActivity(),getBusinessInfo());
            }
        });
        layout_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getBusinessInfo().getPhone()!=null && getBusinessInfo().getPhone().length()>0)
                    ContactActionsUtil.confirmAction(getActivity(),"Tel: "+getBusinessInfo().getPhone(),getResources().getString(R.string.promt_call_title),getResources().getString(R.string.promt_button_call),getResources().getString(R.string.cancel),getBusinessInfo().getPhone());
            }
        });
        layout_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getBusinessInfo().getWeb()!=null && getBusinessInfo().getWeb().length()>0)
                    ContactActionsUtil.openWebSite(getActivity(),getBusinessInfo().getWeb());
            }
        });
    }
}
