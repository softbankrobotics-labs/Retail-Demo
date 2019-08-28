package com.softbankrobotics.retaildemo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.softbankrobotics.retaildemo.MainActivity;
import com.softbankrobotics.retaildemo.R;

import java.lang.reflect.Field;

public class ReturnProductFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "MSI_ReturnProdtFragment";

    private MainActivity ma;
    private String name,resourceName;
    private String price;
    private TextView viewPrice;
    private TextView viewDerscription;
    private ImageView imageviewPhoto;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        this.ma = (MainActivity) getActivity();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            name = bundle.getString("name");
            resourceName = bundle.getString("resource");
            price = bundle.getString("price");
        }
        int fragmentId = R.layout.fragment_return_product;
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
        viewPrice = view.findViewById(R.id.text_price);
        viewDerscription = view.findViewById(R.id.text_description);
        imageviewPhoto = view.findViewById(R.id.image_item);
        viewPrice.setText("EUR " + price);
        viewDerscription.setText(name);
        imageviewPhoto.setImageResource(getResId(resourceName,R.drawable.class));

        view.findViewById(R.id.button_back_selected).setOnClickListener(v ->
                ma.setFragment(new ReturnMainFragment()));
        view.findViewById(R.id.button_home_selected).setOnClickListener(v ->
                ma.setFragment(new MainMenuFragment()));
        view.findViewById(R.id.button_return).setOnClickListener(v ->
                ma.setFragment(new ReturnReasonFragment()));
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

    public void onResume() {
        super.onResume();
    }

}
