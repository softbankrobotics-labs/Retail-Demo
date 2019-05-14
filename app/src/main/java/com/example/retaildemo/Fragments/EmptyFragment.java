package com.example.retaildemo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.retaildemo.MainActivity;
import com.example.retaildemo.R;

public class EmptyFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "MSI_Fragment";


    private MainActivity mainActivity;
    private int fragmentId = R.layout.fragment_empty;


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

    }

    public void onResume() {
        super.onResume();
    }

}
