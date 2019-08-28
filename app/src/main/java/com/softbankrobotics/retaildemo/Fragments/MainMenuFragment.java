package com.softbankrobotics.retaildemo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.object.human.Human;
import com.aldebaran.qi.sdk.object.humanawareness.HumanAwareness;
import com.softbankrobotics.retaildemo.MainActivity;
import com.softbankrobotics.retaildemo.R;

import java.util.List;

public class MainMenuFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "MSI_MainMenuFragment";
    private MainActivity ma;
    private View languageContainer;
    private CountDownTimer countDownCheckHuman;
    private HumanAwareness humanAwareness;



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        this.ma = (MainActivity) getActivity();
        ma.status.reset();
        humanAwareness = ma.getHumanAwareness();
        countDownCheckHuman = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                findHumansAround();
                countDownCheckHuman.start();
            }
        };
        countDownCheckHuman.start();
        int fragmentId = R.layout.fragment_main_menu;
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
        ma.status.currentTicket = "";
        languageContainer = view.findViewById(R.id.view_lang_container);
        languageContainer.setVisibility(View.INVISIBLE);
        languageContainer.setClickable(false);

        Handler handler = new Handler();

        Button buttonShopping = view.findViewById(R.id.button_shop);
        Button buttonReturns = view.findViewById(R.id.button_return);
        Button buttonId = view.findViewById(R.id.button_click);

        buttonReturns.setVisibility(View.INVISIBLE);
        handler.postDelayed(()->{
            buttonReturns.startAnimation(inFromLeftAnimation());
            buttonReturns.setVisibility(View.VISIBLE);

        },100);


        buttonId.setVisibility(View.INVISIBLE);
        handler.postDelayed(()->{
            buttonId.startAnimation(inFromLeftAnimation());
            buttonId.setVisibility(View.VISIBLE);
        },200);

        buttonShopping.startAnimation(inFromRightAnimation());


        buttonId.setOnClickListener(v ->
                ma.setFragment(new CollectFragment()));
        buttonShopping.setOnClickListener(v ->
                ma.setFragment(new ProductSelectionFragment()));
        buttonReturns.setOnClickListener(v ->
                ma.setFragment(new ReturnMainFragment()));

        view.findViewById(R.id.button_add).setOnClickListener(v -> {
            ma.status.promotion = true;
            ma.status.gender = "MALE";
            ma.status.estimatedAge = 20;
            ma.setFragment(new ProductSelectionFragment());
        });
        ImageButton buttonLanguage =  view.findViewById(R.id.button_language);
        ImageButton buttonLang1 = view.findViewById(R.id.button_lang_1);
        buttonLanguage.setOnClickListener(v ->{
            if(languageContainer.isClickable()){
                languageContainer.setVisibility(View.INVISIBLE);
                languageContainer.setClickable(false);
            }else{
                languageContainer.setVisibility(View.VISIBLE);
                languageContainer.setClickable(true);
            }
        });
        if(ma.getCurrentLanguage().toLowerCase().equals("fr")){
            buttonLanguage.setImageResource(R.drawable.lang_fr);
            buttonLang1.setImageResource(R.drawable.lang_uk);
            buttonLang1.setOnClickListener(v-> {
                ma.setLocale("en");
            });
        }else{
            buttonLanguage.setImageResource(R.drawable.lang_uk);
            buttonLang1.setImageResource(R.drawable.lang_fr);
            buttonLang1.setOnClickListener(v-> {
                ma.setLocale("fr");
            });
        }
    }

    private void findHumansAround() {
        if (humanAwareness != null) {
            // Get the humans around the robot.
            Future<List<Human>> humansAroundFuture = humanAwareness.async().getHumansAround();
            humansAroundFuture.andThenConsume(humansAround -> {
                retrieveCharacteristics(humansAround);
            });
        }
    }

    private void retrieveCharacteristics(final List<Human> humans) {
        // Get the Actuation service from the QiContext.
        for (int i = 0; i < humans.size(); i++) {
            Human human = humans.get(i);
            int age = human.getEstimatedAge().getYears();
            String gender = human.getEstimatedGender().toString();
            if(gender.equals("MALE")) {
                ma.status.setGender("MALE");
            }else if(gender.equals("FEMALE")){
                ma.status.setGender("FEMALE");
            }
            // Display the characteristics.
            Log.d(TAG, "----- Human " + i + " -----");
            Log.d(TAG, "Age: " + age + " year(s)");
            Log.d(TAG, "Gender: " + gender);
            //updating Status
            if(age != -1){
                ma.status.setEstimatedAge(age);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        countDownCheckHuman.cancel();
    }

    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(500);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }


    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    public void onResume() {
        super.onResume();
    }

}
