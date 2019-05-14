package com.example.retaildemo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.retaildemo.MainActivity;
import com.example.retaildemo.R;

import java.lang.reflect.Field;

public class ProductUpsellFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "MSI_Fragment";


    private MainActivity mainActivity;
    private int fragmentId = R.layout.fragment_product_upsell;
    private String type;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        this.mainActivity = (MainActivity) getActivity();
        Integer themeId = mainActivity.getThemeId();
        if(themeId != null){
            final Context contextThemeWrapper = new ContextThemeWrapper(mainActivity, themeId);
            LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
            return localInflater.inflate(fragmentId, container, false);
        }
        return null;    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        EmailFragment emailFragment = new EmailFragment();
        ProductMapFragment mapFragment = new ProductMapFragment();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            type = bundle.getString("type");
            Log.i(TAG,"ressource type : " + type);
            ImageView imageView = view.findViewById(R.id.upsellPlaceholder);
            imageView.setImageResource(getResId("sc_"+type, R.drawable.class));
            emailFragment.setArguments(bundle);
            mapFragment.setArguments(bundle);
        }
        view.findViewById(R.id.button_add_upsell).setOnClickListener(v -> {
            mainActivity.status.upsellProductString = type;
            if(mainActivity.status.getSendEmail()){
                mainActivity.setFragment(emailFragment);
            }else{
                mainActivity.setFragment(mapFragment);
            }
        });
        view.findViewById(R.id.button_skip_upsell).setOnClickListener(v -> {
            mainActivity.status.upsellProductString = "";
            if(mainActivity.status.getSendEmail()){
                mainActivity.setFragment(emailFragment);
            }else{
                mainActivity.setFragment(mapFragment);
            }
        });
        view.findViewById(R.id.button_back_upsell).setOnClickListener(v -> mainActivity.setFragment(new ProductSelectionFragment()));

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

    public void setProductUpsell(){
        mainActivity.status.upsellProductString = type;
    }

}
