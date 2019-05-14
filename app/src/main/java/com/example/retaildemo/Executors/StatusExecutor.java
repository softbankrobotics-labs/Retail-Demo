package com.example.retaildemo.Executors;

import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.object.conversation.BaseQiChatExecutor;
import com.example.retaildemo.Fragments.ProductFragment;
import com.example.retaildemo.Fragments.ReturnMainFragment;
import com.example.retaildemo.Fragments.ProductSelectionFragment;
import com.example.retaildemo.MainActivity;

import java.util.List;

public class StatusExecutor extends BaseQiChatExecutor {
    private final QiContext qiContext;
    private final MainActivity ma;
    private String TAG = "MSI_StatusExecutor";

    public StatusExecutor(QiContext qiContext, MainActivity mainActivity) {
        super(qiContext);
        this.qiContext = qiContext;
        this.ma = mainActivity;
    }

    @Override
    public void runWith(List<String> params) {
        if (params == null || params.isEmpty()) {
            return;
        }
        String field = params.get(0);
        String value = "";
        if(params.size() == 2){
            value = params.get(1);
        }
        Log.i(TAG,"field : " + field + " value : "+ value);
        ProductSelectionFragment selectionFragment;
        ProductFragment productFragment;
        ReturnMainFragment returnMainFragment;
        switch (field){
            case ("gender"):
                ma.status.setGender(value);
                selectionFragment = (ProductSelectionFragment) ma.getFragment();
                selectionFragment.setImage(value,selectionFragment.current_type);
                break;
            case ("type"):
                ma.status.setGender(value);
                selectionFragment = (ProductSelectionFragment) ma.getFragment();
                selectionFragment.setImage(selectionFragment.current_gender,value);
                break;
            case ("color"):
                productFragment = (ProductFragment) ma.getFragment();
                productFragment.setProductColor(value);
                break;
            case ("check"):
                productFragment = (ProductFragment) ma.getFragment();
                productFragment.setProductSize(value);
                break;
            case ("clickreturn"):
                returnMainFragment = (ReturnMainFragment) ma.getFragment();
                returnMainFragment.pressListViewButton(Integer.parseInt(value));
                break;
            default:
                break;
        }
    }

    @Override
    public void stop() {

    }
}