package com.softbankrobotics.retaildemo.Fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.softbankrobotics.retaildemo.MainActivity;
import com.softbankrobotics.retaildemo.R;

public class SplashScreenFragment extends Fragment {

    private static final String TAG = "MSI_Fragment";

    private MainActivity ma;
    private ImageView myImageView;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        int fragmentId = R.layout.fragment_splash_screen;
        this.ma = (MainActivity) getActivity();
        try {
            ma.getCurrentChatData().currentTopicStatus = null;
            ma.getCurrentChatData().currentTopicName = null;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "onCreateView: Exception : "+ e);
        }

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
        myImageView= view.findViewById(R.id.pepper_retail);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(ma, R.anim.fade_in_scale_up);
        myImageView.startAnimation(myFadeInAnimation);
    }

    public void onResume() {
        super.onResume();
    }

}
