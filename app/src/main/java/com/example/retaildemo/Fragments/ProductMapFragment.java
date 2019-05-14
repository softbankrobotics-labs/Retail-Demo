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

public class ProductMapFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "MSI_MapFragment";

    private MainActivity mainActivity;
    private int fragmentId = R.layout.fragment_product_map;
    private String productType;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        this.mainActivity = (MainActivity) getActivity();
        Integer themeId = mainActivity.getThemeId();
        if(themeId != null){
            final Context contextThemeWrapper = new ContextThemeWrapper(mainActivity, themeId);
            LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
            return localInflater.inflate(fragmentId, container, false);
        }
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        productType = mainActivity.status.productString;
        ImageView imageView = view.findViewById(R.id.Mapview);
        imageView.setImageResource(getResId("or_"+ "dressman", R.drawable.class));
        ImageView ImageProductOne = view.findViewById(R.id.product_preview_one);
        ImageView ImageProductTwo = view.findViewById(R.id.product_preview_two);
        ImageView pellet_two = view.findViewById(R.id.pellet_product_two);
        ImageView pellet_tow_map = view.findViewById(R.id.pellet_product_two_map);
        ImageView pellet_one_map = view.findViewById(R.id.pellet_product_one_map);
        if(productType.equals("dressman")){
            //TODO change coordinates
        }else if(productType.equals("dresswom")){
            //TODO change coordinates
        }else if(productType.equals("sportman")){
            //TODO change coordinates
        }else{
            //TODO change coordinates
        }
        Log.d(TAG,"product string : " + mainActivity.status.productString);
        String tmp = "it_"+mainActivity.status.productString + "_"+mainActivity.status.productColor + "_1";
        ImageProductOne.setImageResource(getResId(tmp ,R.drawable.class));
        if(mainActivity.status.upsellProductString.length() == 0){
            ImageProductTwo.setVisibility(View.INVISIBLE);
            pellet_two.setVisibility(View.INVISIBLE);
            pellet_tow_map.setVisibility(View.INVISIBLE);
        }else{
            ImageProductTwo.setImageResource(getResId("sc_"+mainActivity.status.upsellProductString,R.drawable.class));
        }
        view.findViewById(R.id.button_home_map).setOnClickListener(v -> mainActivity.setFragment(new MainMenuFragment()));
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

}
