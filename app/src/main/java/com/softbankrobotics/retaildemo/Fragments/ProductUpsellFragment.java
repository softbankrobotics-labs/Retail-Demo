package com.softbankrobotics.retaildemo.Fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.softbankrobotics.retaildemo.MainActivity;
import com.softbankrobotics.retaildemo.R;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

public class ProductUpsellFragment extends Fragment {

    private static final String TAG = "MSI_Fragment";

    private MainActivity ma;
    private String type;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        int fragmentId = R.layout.fragment_product_upsell;
        this.ma = (MainActivity) getActivity();
        if(ma != null){
            Integer themeId = ma.getThemeId();
            if(themeId != null){
                final Context contextThemeWrapper = new ContextThemeWrapper(ma, themeId);
                LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
                return localInflater.inflate(fragmentId, container, false);
            }else{
                return inflater.inflate(fragmentId, container, false);
            }
        }else{
            Log.e(TAG, "could not get mainActivity, can't create fragment");
            return null;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        EmailFragment emailFragment = new EmailFragment();
        ProductMapFragment mapFragment = new ProductMapFragment();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            type = bundle.getString("type");
            Log.d(TAG,"ressource type : " + type);
            ImageView imageView = view.findViewById(R.id.image_upsell);
            imageView.setImageResource(getResId("sc_"+type, R.drawable.class));
            emailFragment.setArguments(bundle);
            mapFragment.setArguments(bundle);
        }
        view.findViewById(R.id.button_add_upsell).setOnClickListener(v -> {
            ma.status.upsellProductString = type;
            if(ma.status.getSendEmail()){
                ma.setFragment(emailFragment);
            }else{
                ma.setFragment(mapFragment);
            }
        });
        view.findViewById(R.id.button_skip_upsell).setOnClickListener(v -> {
            ma.status.upsellProductString = "";
            if(ma.status.getSendEmail()){
                ma.setFragment(emailFragment);
            }else{
                ma.setFragment(mapFragment);
            }
        });
        TextView oldPrice = view.findViewById(R.id.text_price_old);
        oldPrice.setText("EUR " + ma.status.upsellPrice);

        TextView price = view.findViewById(R.id.text_price);
        Double tmp = Double.parseDouble(ma.status.upsellPrice);
        tmp = tmp/2;
        price.setText("EUR " + new DecimalFormat("00.00").format(tmp));
        view.findViewById(R.id.button_back_upsell).setOnClickListener(v -> ma.setFragment(new ProductSelectionFragment()));

    }

    public void onResume() {
        super.onResume();
    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setProductUpsell(Boolean b){
        if(b){
            ma.status.upsellProductString = type;
        }else{
            ma.status.upsellProductString = "";

        }
    }

}
