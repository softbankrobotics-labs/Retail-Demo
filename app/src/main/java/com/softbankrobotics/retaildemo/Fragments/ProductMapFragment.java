package com.softbankrobotics.retaildemo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.softbankrobotics.retaildemo.MainActivity;
import com.softbankrobotics.retaildemo.R;

public class ProductMapFragment extends Fragment {

    private static final String TAG = "MSI_MapFragment";

    private MainActivity ma;
    private CountDownTimer countDownFeedback;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        this.ma = (MainActivity) getActivity();
        countDownFeedback = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                ma.setFragment(new FeedbackFragment());
            }
        };
        countDownFeedback.start();
        int fragmentId = R.layout.fragment_product_map;
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
        Log.d(TAG,"product string : " + ma.status.productString);
        view.findViewById(R.id.button_home_map).setOnClickListener(v -> ma.setFragment(new MainMenuFragment()));
        view.findViewById(R.id.button_finish_map).setOnClickListener(v -> ma.setFragment(new FeedbackFragment()));
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        countDownFeedback.cancel();
    }
}
