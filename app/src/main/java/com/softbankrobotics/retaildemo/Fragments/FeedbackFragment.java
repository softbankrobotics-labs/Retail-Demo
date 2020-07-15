package com.softbankrobotics.retaildemo.Fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.softbankrobotics.retaildemo.MainActivity;
import com.softbankrobotics.retaildemo.R;

import java.util.ArrayList;

public class FeedbackFragment extends Fragment {

    private static final String TAG = "MSI_FeedbackFragment";

    private MainActivity ma;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        int fragmentId = R.layout.fragment_feedback;
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
        view.findViewById(R.id.button_home_feedback).setOnClickListener(v -> ma.setFragment(new MainMenuFragment()));
        Integer [] buttonIds = {R.id.button_perfect,R.id.button_good,
            R.id.button_okay,R.id.button_bad};
        ArrayList<ImageButton> buttons = new ArrayList<>();
        for(Integer b:buttonIds){
            ImageButton button = view.findViewById(b);
            buttons.add(button);
            button.setOnClickListener(v -> {
                    ma.getCurrentChatData().goToBookmarkSameTopic("thanks");
                    setSelectedButton(button,buttons);
            });
        }
    }

    private void setSelectedButton(ImageButton button,ArrayList<ImageButton> buttons){
        for(ImageButton b:buttons){
            if(b.equals(button)){
                b.setScaleX((float) 1.1);
                b.setScaleY((float) 1.1);
            }else{
                b.setAlpha((float) 0.5);
            }
            b.setClickable(false);
        }
    }



    public void onResume() {
        super.onResume();
    }
}
