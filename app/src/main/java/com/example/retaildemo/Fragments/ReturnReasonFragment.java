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

public class ReturnReasonFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "MSI_ReturnReasonFragment";

    private MainActivity mainActivity;
    private int fragmentId = R.layout.fragment_return_reason;

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
        Integer [] reasonsButtonsIds = {R.id.button_dont_fit, R.id.button_dont_like,
                R.id.button_submit, R.id.button_dont_need,R.id.button_wrong_color,
                R.id.button_wrong_size,R.id.button_flawed};
        for (Integer id : reasonsButtonsIds){
            view.findViewById(id).setOnClickListener(v ->
             mainActivity.setFragment(new FeedbackFragment()));
        }
        view.findViewById(R.id.button_back_reason).setOnClickListener(v ->
                mainActivity.setFragment(new ReturnMainFragment()));
        view.findViewById(R.id.button_home_reason).setOnClickListener(v ->
                mainActivity.setFragment(new MainMenuFragment()));
    }

    public void onResume() {
        super.onResume();
    }

}
