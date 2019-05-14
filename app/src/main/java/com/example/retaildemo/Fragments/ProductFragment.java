package com.example.retaildemo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.retaildemo.MainActivity;
import com.example.retaildemo.R;

import java.lang.reflect.Field;

public class ProductFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "MSI_ProductFragment";

    private MainActivity mainActivity;
    private int fragmentId = R.layout.fragment_product;
    private Button buttonBuy;
    private ImageView productImage;
    private TextView inStock;
    private String productString;
    private String color;
    private EditText textsize;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        this.mainActivity = (MainActivity) getActivity();
        Integer themeId = mainActivity.getThemeId();
        if(themeId != null){
            final Context contextThemeWrapper = new ContextThemeWrapper(mainActivity, themeId);
            LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
            return localInflater.inflate(fragmentId, container, false);
        }
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.button_back_products).setOnClickListener(v -> mainActivity.setFragment(new ProductSelectionFragment()));
        view.findViewById(R.id.button_home_products).setOnClickListener(v -> mainActivity.setFragment(new MainMenuFragment()));
        if(mainActivity.getProductString().equals("sportman")){
            view.findViewById(R.id.promotion_price).setVisibility(View.VISIBLE);
        }else{
            view.findViewById(R.id.promotion_price).setVisibility(View.INVISIBLE);
        }
        textsize = view.findViewById(R.id.text_size);
        if(mainActivity.getProductString().contains("man")){
            setProductSize("43");
        }else{
            setProductSize("38");
        }
        buttonBuy = view.findViewById(R.id.button_buy_products);
        buttonBuy.setOnClickListener(v -> onClickBuy());

        view.findViewById(R.id.button_color_black).setOnClickListener(v -> setProductColor("blk"));
        view.findViewById(R.id.button_color_grey).setOnClickListener(v -> setProductColor("gry"));
        view.findViewById(R.id.button_color_red).setOnClickListener(v -> setProductColor("red"));
        view.findViewById(R.id.button_color_tan).setOnClickListener(v -> setProductColor("tan"));

        productImage = view.findViewById(R.id.imageProduct);
        Log.i(TAG,"product String : " + mainActivity.getProductString());
        productImage.setImageResource(getResId("it_"+mainActivity.getProductString()+"_blk_1",R.drawable.class));
        color = "blk";
        inStock = view.findViewById(R.id.view_in_Stock);
        setLogoInStock("blk");
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
        //TODO update with product name string
        String ProductType;
        mainActivity.status.productColor = color;
        //Carefull ! if the name of the products are changed this needs to be updated
        if(mainActivity.status.getStock(productString, color)){
            mainActivity.status.setSendEmail(false);
        }else{
            mainActivity.status.setSendEmail(true);
        }
        if(productString.contains("sport")){
            if(productString.contains("wom")){
                ProductType = "sportwom";
            }else{
                ProductType = "sportman";
            }
        }else{
            if(productString.contains("wom")){
                ProductType = "dresswom";
            }else{
                ProductType = "dressman";
            }
        }
        ProductUpsellFragment fragment = new ProductUpsellFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", ProductType);
        fragment.setArguments(bundle);
        mainActivity.setFragment(fragment);
    }



    public void setProductColor(String color){
        Log.d(TAG,"setting product color : "+color);
        this.color = color;
        String tmp = "it_"+mainActivity.getProductString() + "_"+color + "_1";
        int id = getResId(tmp,R.drawable.class);
        mainActivity.runOnUiThread(() -> productImage.setImageResource(id));
        Log.d(TAG,"Image is set for : "+color);
        setLogoInStock(color);
    }

    public void setProductSize(String size){
        mainActivity.runOnUiThread(()-> textsize.setText(size));
    }


    public void setLogoInStock(String color){
        Log.i(TAG,"setting in stock for color : "+color);
        productString = mainActivity.getProductString().replace("it_","");
        productString = productString.replace("_","");
        Log.d(TAG,"getting stocks for product : "+productString + " colored : "+color );
        if(mainActivity.status.getStock(productString, color)){
            mainActivity.status.setSendEmail(true);
            mainActivity.runOnUiThread(() ->{
                inStock.setVisibility(View.INVISIBLE);
                buttonBuy.setText(R.string.buy);
            });
        }else{
            mainActivity.runOnUiThread(() ->{
                mainActivity.status.setSendEmail(false);
                inStock.setVisibility(View.VISIBLE);
                buttonBuy.setText(R.string.order);
            });
        }
    }

}
