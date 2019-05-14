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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aldebaran.qi.sdk.object.humanawareness.HumanAwareness;
import com.example.retaildemo.MainActivity;
import com.example.retaildemo.R;

public class ProductSelectionFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "MSI_SelectionFragment";

    private MainActivity mainActivity;
    private int fragmentId = R.layout.fragment_product_selection;
    private TextView price;
    private ImageView productMain;
    private RadioGroup groupGenrder, groupType;
    private CountDownTimer countDownCheckHuman;
    private HumanAwareness humanAwareness;
    public String current_type;
    public String current_gender;

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
        groupGenrder = view.findViewById(R.id.gender);
        groupType = view.findViewById(R.id.type);
        price = view.findViewById(R.id.price);
        view.findViewById(R.id.button_home_selection).setOnClickListener(v -> mainActivity.setFragment(new MainMenuFragment()));
        productMain = view.findViewById(R.id.imageMain);
        current_gender = mainActivity.status.getGender();
        Integer age = mainActivity.status.getEstimatedAge();
        //TODO update with the values of the xml
        if(age > 50){
            current_type = "dress";
        }else{
            current_type = "sport";
        }
        setImage(current_gender,current_type);
        groupGenrder.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            Log.d(TAG,"text button : "+ checkedRadioButton.getText());
            if(checkedRadioButton.getText().toString().toLowerCase().equals("\"man\"")){
                setImage("MALE",current_type);
                current_gender = "MALE";
            }else{
                setImage("FEMALE",current_type);
                current_gender = "FEMALE";
            }
        });
        groupType.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            Log.d(TAG,"text button : "+ checkedRadioButton.getText());
            if(checkedRadioButton.getText().toString().toLowerCase().contains("dress")){
                setImage(current_gender,"dress");
                current_type="dress";
            }else{
                setImage(current_gender,"sport");
                current_type="sport";
            }
        });
        view.findViewById(R.id.button_next_selection).setOnClickListener((v) -> {
            String productId;
            if (current_gender.equals("MALE")) {
                if (current_type.equals("dress")) {
                    productId = "dressman";
                } else {
                    productId = "sportman";

                }
            } else {
                if (current_type.equals("dress")) {
                    productId = "dresswom";
                } else {
                    productId = "sportwom";
                }
            }
            mainActivity.setFragment(new ProductFragment(), productId);
        });

    }

    public void onResume(){
        super.onResume();
    }

    public void setImage(String gender, String type){
        Log.d(TAG,"setting image gender : "+gender+" type : "+type);
        //TODO should change this to support products names
        if(gender.equals("MALE")){
            mainActivity.runOnUiThread(() -> {
                if(type.equals("dress")){
                    mainActivity.runOnUiThread(()->{
                        productMain.setImageResource(R.drawable.it_dressman_blk_1);
                        mainActivity.status.productString = "dressman";
                        price.setText("EUR " + mainActivity.status.getPrice("dressman"));
                    });
                    checkRadioButton(1,0);
                }else{
                    mainActivity.runOnUiThread(()->{
                        mainActivity.status.productString = "sportman";
                        productMain.setImageResource(R.drawable.it_sportman_blk_1);
                        price.setText("EUR " + mainActivity.status.getPrice("sportman"));

                    });
                    checkRadioButton(1,1);
                }
            });
        }else{
            mainActivity.runOnUiThread(() -> {
                if(type.equals("dress")){
                    mainActivity.runOnUiThread(()-> {
                        productMain.setImageResource(R.drawable.it_dresswom_blk_1);
                        mainActivity.status.productString = "dresswom";
                        price.setText("EUR " + mainActivity.status.getPrice("dresswom"));
                    });
                    checkRadioButton(0,0);
                }else{
                    mainActivity.runOnUiThread(()->{
                        productMain.setImageResource(R.drawable.it_sportwom_blk_1);
                        mainActivity.status.productString = "sportwom";
                        price.setText("EUR " + mainActivity.status.getPrice("sportwom"));
                    });
                    checkRadioButton(0,1);
                }
            });
        }
    }


    public void checkRadioButton(int gender, int type){
        mainActivity.runOnUiThread(() -> {
            RadioButton tmp =(RadioButton) groupGenrder.getChildAt(gender);
            tmp.setChecked(true);
            tmp =(RadioButton) groupType.getChildAt(type);
            tmp.setChecked(true);
        });
    }


    @Override
    public void onPause() {
        countDownCheckHuman.cancel();
        super.onPause();
    }
}
