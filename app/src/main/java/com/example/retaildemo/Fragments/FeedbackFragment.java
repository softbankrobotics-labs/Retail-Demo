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

public class FeedbackFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "MSI_FeedbackFragment";

    private MainActivity mainActivity;
    private int fragmentId = R.layout.fragment_feedback;

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
        Integer [] buttons = {R.id.button_perfect,R.id.button_good,
            R.id.button_okay,R.id.button_bad};
        for(Integer b:buttons){
            view.findViewById(b).setOnClickListener(v ->
                    mainActivity.goToBookmarkInTopic("thanks","feedback"));
        }
    }

    public void onResume() {
        super.onResume();
    }
}
