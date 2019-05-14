package com.example.retaildemo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.object.human.Human;
import com.aldebaran.qi.sdk.object.humanawareness.HumanAwareness;
import com.example.retaildemo.MainActivity;
import com.example.retaildemo.R;

import java.util.List;

public class MainMenuFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "MSI_MainMenuFragment";
    private MainActivity mainActivity;
    private int fragmentId = R.layout.fragment_main_menu;
    private View languageContainer;
    private CountDownTimer countDownCheckHuman;
    private HumanAwareness humanAwareness;



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        this.mainActivity = (MainActivity) getActivity();
        Integer themeId = mainActivity.getThemeId();
        humanAwareness = mainActivity.getQiContext().getHumanAwareness();
        countDownCheckHuman = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                //Log.i(TAG, "Timer finished");
                findHumansAround();
                countDownCheckHuman.start();
            }
        };
        countDownCheckHuman.start();
        if(themeId != null){
            final Context contextThemeWrapper = new ContextThemeWrapper(mainActivity, themeId);
            LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
            return localInflater.inflate(fragmentId, container, false);
        }
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //TODO update to use product strings
        mainActivity.status.currentTicket = "";
        languageContainer = view.findViewById(R.id.view_lang_container);
        languageContainer.setVisibility(View.INVISIBLE);
        languageContainer.setClickable(false);
        view.findViewById(R.id.buttonId).setOnClickListener(v ->
                mainActivity.setFragment(new CollectFragment()));
        view.findViewById(R.id.buttonShopping).setOnClickListener(v ->
                mainActivity.setFragment(new ProductSelectionFragment()));
        view.findViewById(R.id.buttonReturns).setOnClickListener(v ->
                mainActivity.setFragment(new ReturnMainFragment()));
        view.findViewById(R.id.buttonAdd).setOnClickListener(v -> {
            mainActivity.status.productString = "sportman";
            mainActivity.setFragment(new ProductFragment());
        });
        ImageButton buttonLanguage =  view.findViewById(R.id.buttonLanguage);
        buttonLanguage.setOnClickListener(v ->{
            if(languageContainer.isClickable()){
                languageContainer.setVisibility(View.INVISIBLE);
                languageContainer.setClickable(false);
            }else{
                languageContainer.setVisibility(View.VISIBLE);
                languageContainer.setClickable(true);
            }
        });
    }

    private void findHumansAround() {
        if (humanAwareness != null) {
            // Get the humans around the robot.
            Future<List<Human>> humansAroundFuture = humanAwareness.async().getHumansAround();
            humansAroundFuture.andThenConsume(humansAround -> {
                //Log.i(TAG, humansAround.size() + " human(s) around.");
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
                mainActivity.status.setGender("MALE");
            }else if(gender.equals("FEMALE")){
                mainActivity.status.setGender("FEMALE");
            }
            // Display the characteristics.
            Log.i(TAG, "----- Human " + i + " -----");
            Log.i(TAG, "Age: " + age + " year(s)");
            Log.i(TAG, "Gender: " + gender);
            //updating Status
            if(age != -1){
                mainActivity.status.setEstimatedAge(age);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        countDownCheckHuman.cancel();
    }

    public void onResume() {
        super.onResume();
    }

}
