package com.softbankrobotics.retaildemo.Fragments;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.softbankrobotics.retaildemo.MainActivity;
import com.softbankrobotics.retaildemo.R;

public class LoadingFragment extends Fragment {

    private static final String TAG = "MSI_FragmentLoading";

    private MainActivity ma;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        int fragmentId = R.layout.fragment_loading;
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
        ImageView pepperRetail= view.findViewById(R.id.pepper_retail);
        ImageView logoQrcode= view.findViewById(R.id.qrcode_logo);
        ImageView logoReturn= view.findViewById(R.id.return_logo);
        ImageView logoShop= view.findViewById(R.id.shopping_logo);

        logoQrcode.setVisibility(View.INVISIBLE);
        logoReturn.setVisibility(View.INVISIBLE);
        logoShop.setVisibility(View.INVISIBLE);

        Animation myFadeInAnimation = AnimationUtils.loadAnimation(ma, R.anim.fade_in_scale_up);
        Animation myFadeInAnimation1 = AnimationUtils.loadAnimation(ma, R.anim.fade_in_scale_up);
        Animation myFadeInAnimation2 = AnimationUtils.loadAnimation(ma, R.anim.fade_in_scale_up);
        Animation myFadeInAnimation3 = AnimationUtils.loadAnimation(ma, R.anim.fade_in_scale_up);

        Animation myFadeOutAnimation = AnimationUtils.loadAnimation(ma, R.anim.fade_out_1000ms);

        TextView tv = view.findViewById(R.id.text_loading);
        Shader textShader=new LinearGradient(0, 0, 0,300,
                new int[]{getResources().getColor(R.color.colorSpeechBarDraker)
                        ,getResources().getColor(R.color.colorSpeechBar)},
                new float[]{0, 1}, Shader.TileMode.CLAMP);
        tv.getPaint().setShader(textShader);
        tv.setVisibility(View.INVISIBLE);

        pepperRetail.startAnimation(myFadeInAnimation);
        Handler handler = new Handler();

        handler.postDelayed(() -> {
            pepperRetail.startAnimation(myFadeOutAnimation);
            pepperRetail.setVisibility(View.INVISIBLE);
        }, 1200);
        handler.postDelayed(()->{
            logoQrcode.startAnimation(myFadeInAnimation1);
            logoQrcode.setVisibility(View.VISIBLE);
        },2000);
        handler.postDelayed(()->{
            logoShop.startAnimation(myFadeInAnimation3);
            logoShop.setVisibility(View.VISIBLE);
        },2300);
        handler.postDelayed(()->{
            logoReturn.startAnimation(myFadeInAnimation2);
            logoReturn.setVisibility(View.VISIBLE);
            tv.startAnimation(myFadeInAnimation2);
            tv.setVisibility(View.VISIBLE);
            ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    logoQrcode,
                    PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.2f));
            scaleDown.setDuration(600);
            scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
            scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
            scaleDown.start();

        },2600);

        handler.postDelayed(()->{
            ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    logoShop,
                    PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.2f));
            scaleDown.setDuration(600);
            scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
            scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
            scaleDown.start();
        },2900);

        handler.postDelayed(()->{
            ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    logoReturn,
                    PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.2f));
            scaleDown.setDuration(600);
            scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
            scaleDown.setRepeatMode(ObjectAnimator.REVERSE);


            scaleDown.start();
        },3200);

    }

    public void onResume() {
        super.onResume();
    }

}
