package com.softbankrobotics.retaildemo.Fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.softbankrobotics.retaildemo.MainActivity;
import com.softbankrobotics.retaildemo.R;

import java.text.DecimalFormat;

public class ProductSelectionFragment extends Fragment {

    private static final String TAG = "MSI_SelectionFragment";

    private MainActivity ma;
    private TextView price;
    private ConstraintLayout price_old;
    private ImageView productMain;
    private ImageView productSecondary;
    private ViewSwitcher imageSwitcher;
    private RadioGroup groupGender, groupType;
    public String current_type;
    public String current_gender;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        this.ma = (MainActivity) getActivity();
        Log.d(TAG,"estimatedAge : "+ ma.status.estimatedAge);
        Log.d(TAG,"gender : " + ma.status.gender);
        if(ma.status.promotion){
            ma.status.promotion = false;
            ma.getCurrentChatData().goToBookmarkNewTopic("init_promotion","productselection");
        }else{
            if(ma.status.estimatedAge == -1 || ma.status.gender.equals("unknown")){
                ma.getCurrentChatData().goToBookmarkNewTopic("init","productselection");
            }else{
                String type = "Sport";
                if(ma.status.estimatedAge > 50){
                    type = "Dress";
                }
                ma.setDynamicProductName("product_type",type);
                ma.getCurrentChatData().goToBookmarkNewTopic("init_context","productselection");
            }
        }
        int fragmentId = R.layout.fragment_product_selection;
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
        groupGender = view.findViewById(R.id.gender);
        groupType = view.findViewById(R.id.type);
        price = view.findViewById(R.id.text_price);
        price_old = view.findViewById(R.id.text_price_old);
        view.findViewById(R.id.button_home_selection).setOnClickListener(v -> ma.setFragment(new MainMenuFragment()));
        productMain = view.findViewById(R.id.image_main);
        productSecondary = view.findViewById(R.id.image_secondary);
        imageSwitcher = view.findViewById(R.id.image_switcher);

        current_gender = ma.status.getGender();
        Integer age = ma.status.getEstimatedAge();
        if(age > 50){
            current_type = ma.status.option2_1;
        }else{
            current_type = ma.status.option2_2;
        }
        setImage(current_gender,current_type);
        groupGender.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            int idx = group.indexOfChild(checkedRadioButton);
            if(idx == 1){
                current_gender = "MALE";
            }else{
                current_gender = "FEMALE";
            }
            setImage(current_gender,current_type);
        });
        groupType.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            int idx = group.indexOfChild(checkedRadioButton);
            if(idx == 0){
                current_type = ma.status.option2_1;
            }else{
                current_type= ma.status.option2_2;
            }
            setImage(current_gender,current_type);
        });
        view.findViewById(R.id.button_next_selection).setOnClickListener((v) -> {
            ma.setFragment(new ProductFragment(), getProductIdFromGT());
        });

    }


    private String getProductIdFromGT(){
        if (current_gender.equals("MALE")) {
            if (current_type.equals(ma.status.option2_1)) {
                return ma.status.product21;
            } else {
                return ma.status.product22;
            }
        } else {
            if (current_type.equals(ma.status.option2_1)) {
                return ma.status.product11;
            } else {
                return ma.status.product12;
            }
        }
    }


    public void setImage(String gender, String type){
        Log.d(TAG,"setting image gender : "+gender+" type : "+type);
        if(gender.equals("MALE")){
            ma.runOnUiThread(() -> {
                if(type.toLowerCase().equals(ma.status.option2_1.toLowerCase())){
                    ma.runOnUiThread(()->{
                        changeImage(R.drawable.it_dress_man_black);
                        ma.status.productString = ma.status.product21;
                        price.setText("EUR " + ma.status.getPrice(ma.status.product21));
                        price_old.setVisibility(View.INVISIBLE);
                    });
                    checkRadioButton(1,0);
                }else{
                    ma.runOnUiThread(()->{
                        ma.status.productString = ma.status.product22;
                        changeImage(R.drawable.it_sport_man_black);
                        Double tmp = Double.parseDouble(ma.status.getPrice(ma.status.product22));
                        tmp = tmp - tmp*0.2;
                        price.setText("EUR " + new DecimalFormat("##.##").format(tmp));
                        price_old.setVisibility(View.VISIBLE);
                    });
                    checkRadioButton(1,1);
                }
            });
        }else{
            ma.runOnUiThread(() -> {
                if(type.toLowerCase().equals(ma.status.option2_1.toLowerCase())){
                    ma.runOnUiThread(()-> {
                        changeImage(R.drawable.it_dress_woman_black);
                        ma.status.productString = ma.status.product11;
                        price.setText("EUR " + ma.status.getPrice(ma.status.product11));
                        price_old.setVisibility(View.INVISIBLE);
                    });
                    checkRadioButton(0,0);
                }else{
                    ma.runOnUiThread(()->{
                        changeImage(R.drawable.it_sport_woman_black);
                        ma.status.productString = ma.status.product12;
                        price.setText("EUR " + ma.status.getPrice(ma.status.product12));
                        price_old.setVisibility(View.INVISIBLE);
                    });
                    checkRadioButton(0,1);
                }
            });
        }
    }

    private void changeImage(int imageId){
        if (imageSwitcher.getDisplayedChild() == 0) {
            productSecondary.setImageResource(imageId);
            imageSwitcher.showNext();
        } else {
            productMain.setImageResource(imageId);
            imageSwitcher.showPrevious();
        }
    }

    public void checkRadioButton(int gender, int type){
        ma.runOnUiThread(() -> {
            RadioButton tmp =(RadioButton) groupGender.getChildAt(gender);
            tmp.setChecked(true);
            tmp =(RadioButton) groupType.getChildAt(type);
            tmp.setChecked(true);
        });
    }
}
