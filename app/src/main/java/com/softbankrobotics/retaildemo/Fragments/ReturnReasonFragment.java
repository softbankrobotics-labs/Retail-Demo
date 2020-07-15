package com.softbankrobotics.retaildemo.Fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.softbankrobotics.retaildemo.MainActivity;
import com.softbankrobotics.retaildemo.R;

public class ReturnReasonFragment extends Fragment {

    private static final String TAG = "MSI_ReturnResFragment";

    private MainActivity ma;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        int fragmentId = R.layout.fragment_return_reason;
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
        Integer [] reasonsButtonsIds = {R.id.button_dont_fit, R.id.button_dont_like,
                R.id.button_submit, R.id.button_dont_need,R.id.button_wrong_color,
                R.id.button_wrong_size,R.id.button_flawed};
        for (Integer id : reasonsButtonsIds){
            view.findViewById(id).setOnClickListener(v ->
             ma.setFragment(new FeedbackFragment()));
        }
        view.findViewById(R.id.button_back_reason).setOnClickListener(v ->
                ma.setFragment(new ReturnMainFragment()));
        view.findViewById(R.id.button_home_reason).setOnClickListener(v ->
                ma.setFragment(new MainMenuFragment()));
    }

    public void onResume() {
        super.onResume();
    }

}
