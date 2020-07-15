package com.softbankrobotics.retaildemo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.softbankrobotics.retaildemo.MainActivity;
import com.softbankrobotics.retaildemo.R;
import java.lang.reflect.Field;

public class CollectLockerFragment extends Fragment {

    private static final String TAG = "MSI_FragmentLocker";

    private MainActivity ma;
    private CountDownTimer countDownFeedback;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
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
        int fragmentId = R.layout.fragment_collect_locker;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button button = view.findViewById(getResId("locker_"+ ma.status.lockerNumber,R.id.class));
        view.findViewById(R.id.button_finish_locker).setOnClickListener( (v) -> ma.setFragment(new FeedbackFragment()));
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(ma, R.anim.shrink_right);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            button.startAnimation(myFadeInAnimation);
            button.setVisibility(View.INVISIBLE);
        }, 1000);

    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        countDownFeedback.cancel();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        countDownFeedback.cancel();
        super.onDestroy();
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
