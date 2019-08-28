package com.softbankrobotics.retaildemo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.softbankrobotics.retaildemo.MainActivity;
import com.softbankrobotics.retaildemo.R;

import java.lang.reflect.Field;

public class ProductFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "MSI_ProductFragment";

    private MainActivity ma;
    private Button buttonBuy;
    private ImageView productMain;
    private ImageView productSecondary;
    private ViewSwitcher imageSwitcher;
    private TextView inStock;
    private String productString;
    private String color;
    private NumberPicker npSize;
    private CountDownTimer pickerLastChanged;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        int fragmentId = R.layout.fragment_product;
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
        pickerLastChanged = new CountDownTimer(700,100) {
            @Override
            public void onTick(long millisUntilFinished) { }

            @Override
            public void onFinish() {
                Log.d(TAG,"setting value to :" + npSize.getValue());
                ma.setQiVariable("size",""+npSize.getValue());
            }
        };
        view.findViewById(R.id.button_back_products).setOnClickListener(v -> ma.setFragment(new ProductSelectionFragment()));
        view.findViewById(R.id.button_home_products).setOnClickListener(v -> ma.setFragment(new MainMenuFragment()));
        npSize = view.findViewById(R.id.number_picker);
        npSize.setMaxValue(46);
        npSize.setMinValue(35);
        npSize.setOnValueChangedListener((picker, oldVal, newVal) -> {
                if(pickerLastChanged != null){
                    pickerLastChanged.cancel();
                    pickerLastChanged.start();
                }
        });
        npSize.getValue();
        if(ma.getProductString().contains("man")){
            setProductSize("43");
        }else{
            setProductSize("38");
        }
        buttonBuy = view.findViewById(R.id.button_buy_products);
        buttonBuy.setOnClickListener(v -> onClickBuy());

        view.findViewById(R.id.button_color_black).setOnClickListener(v -> setProductColor("black"));
        view.findViewById(R.id.button_color_grey).setOnClickListener(v -> setProductColor("grey"));
        view.findViewById(R.id.button_color_red).setOnClickListener(v -> setProductColor("red"));
        view.findViewById(R.id.button_color_tan).setOnClickListener(v -> setProductColor("tan"));

        productMain = view.findViewById(R.id.image_main);
        productSecondary = view.findViewById(R.id.image_secondary);
        imageSwitcher = view.findViewById(R.id.image_switcher);

        Log.d(TAG,"product String : " + ma.getProductString());
        changeImage(getResId("it_"+ ma.getProductString()+"_black",R.drawable.class));
        color = "black";
        inStock = view.findViewById(R.id.view_in_Stock);
        setLogoInStock("black");
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

    public void onResume() {
        super.onResume();
    }

    public void onClickBuy(){
        String ProductType;
        ma.status.productColor = color;
        if(ma.status.getStock(productString, color)){
            ma.status.setSendEmail(false);
        }else{
            ma.status.setSendEmail(true);
        }
        if(productString.contains(ma.status.option2_2.toLowerCase())){
            if(productString.contains(ma.status.option1_1.toLowerCase())){
                ProductType = ma.status.product12;
            }else{
                ProductType = ma.status.product22;
            }
        }else{
            if(productString.contains(ma.status.option1_1.toLowerCase())){
                ProductType = ma.status.product11;
            }else{
                ProductType = ma.status.product21;
            }
        }
        ProductUpsellFragment fragment = new ProductUpsellFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", ProductType);
        fragment.setArguments(bundle);
        ma.setFragment(fragment);
    }


    public void setProductColor(String color){
        Log.d(TAG,"setting product color : "+color);
        ma.setQiVariable("color",color);
        if(color.equals(getResources().getString(R.string.black).replaceAll("\"","").toLowerCase())){
            color = "black";
        }else if(color.equals(getResources().getString(R.string.red).replaceAll("\"","").toLowerCase())){
            color = "red";
        }else if(color.equals(getResources().getString(R.string.tan).replaceAll("\"","").toLowerCase())){
            color = "tan";
        }else {
            color = "grey";
        }
        this.color = color;
        String tmp = "it_"+ ma.getProductString() + "_"+color;
        int id = getResId(tmp,R.drawable.class);
        ma.runOnUiThread(() -> changeImage(id));
        Log.d(TAG,"Image is set for : "+color);
        setLogoInStock(color);
    }

    public void setProductSize(String size){
        ma.runOnUiThread(()-> npSize.setValue(Integer.parseInt(size)));
    }


    public void setLogoInStock(String color){
        Log.d(TAG,"setting in stock for color : "+color);
        productString = ma.getProductString().replace("it_","");
        Log.d(TAG,"getting stocks for product : "+productString + " colored : "+color );
        if(ma.status.getStock(productString, color)){
            ma.status.setSendEmail(true);
            ma.runOnUiThread(() ->{
                inStock.setVisibility(View.INVISIBLE);
                buttonBuy.setText(R.string.buy);
            });
        }else{
            ma.runOnUiThread(() ->{
                ma.status.setSendEmail(false);
                inStock.setVisibility(View.VISIBLE);
                buttonBuy.setText(R.string.order);
            });
        }
    }

    @Override
    public void onPause() {
        pickerLastChanged.cancel();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        pickerLastChanged.cancel();
        super.onDestroy();
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

}
