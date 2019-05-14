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
import android.widget.TextView;

import com.example.retaildemo.MainActivity;
import com.example.retaildemo.R;

import java.lang.reflect.Field;

public class ReturnProductFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "MSI_ReturnProdtFragment";

    private MainActivity mainActivity;
    private int fragmentId = R.layout.fragment_return_product;

    private String name,resourceName;
    private String price;
    private TextView viewPrice;
    private TextView viewDerscription;
    private ImageView imageviewPhoto;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        this.mainActivity = (MainActivity) getActivity();
        Integer themeId = mainActivity.getThemeId();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            name = bundle.getString("name");
            resourceName = bundle.getString("resource");
            price = bundle.getString("price");
        }
        Log.i(TAG, "name : "+ name +" resource : "+resourceName+ " price : "+ price);
        if(themeId != null){
            final Context contextThemeWrapper = new ContextThemeWrapper(mainActivity, themeId);
            LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
            return localInflater.inflate(fragmentId, container, false);
        }
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        viewPrice = view.findViewById(R.id.price);
        viewDerscription = view.findViewById(R.id.description);
        imageviewPhoto = view.findViewById(R.id.photoPreview);
        viewPrice.setText("EUR "+price);
        viewDerscription.setText(name);
        imageviewPhoto.setImageResource(getResId(resourceName,R.drawable.class));

        view.findViewById(R.id.button_back_selected).setOnClickListener(v ->
                mainActivity.setFragment(new ReturnMainFragment()));
        view.findViewById(R.id.button_home_selected).setOnClickListener(v ->
                mainActivity.setFragment(new MainMenuFragment()));
        view.findViewById(R.id.button_return).setOnClickListener(v ->
                mainActivity.setFragment(new ReturnReasonFragment()));
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
