package com.softbankrobotics.retaildemo.Executors;

import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.object.conversation.BaseQiChatExecutor;
import com.softbankrobotics.retaildemo.Fragments.CollectFragment;
import com.softbankrobotics.retaildemo.Fragments.ProductFragment;
import com.softbankrobotics.retaildemo.Fragments.ReturnMainFragment;
import com.softbankrobotics.retaildemo.Fragments.ProductSelectionFragment;
import com.softbankrobotics.retaildemo.MainActivity;

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
        Log.d(TAG,"field : " + field + " value : "+ value);
        ProductSelectionFragment selectionFragment;
        ProductFragment productFragment;
        ReturnMainFragment returnMainFragment;
        CollectFragment collectFragment;
        switch (field){
            case ("gender"):
                ma.status.setGender(value);
                selectionFragment = (ProductSelectionFragment) ma.getFragment();
                selectionFragment.setImage(value,selectionFragment.current_type);
                break;
            case ("type"):
                selectionFragment = (ProductSelectionFragment) ma.getFragment();
                selectionFragment.setImage(selectionFragment.current_gender,value);
                break;
            case ("color"):
                productFragment = (ProductFragment) ma.getFragment();
                productFragment.setProductColor("\""+value+"\"");
                break;
            case ("check"):
                productFragment = (ProductFragment) ma.getFragment();
                productFragment.setProductSize(value);
                break;
            case ("clickreturn"):
                returnMainFragment = (ReturnMainFragment) ma.getFragment();
                returnMainFragment.pressListViewButton(Integer.parseInt(value));
                break;
            case ("clickdemoreturn"):
                returnMainFragment = (ReturnMainFragment) ma.getFragment();
                returnMainFragment.pressDemoTicketButton();
                break;
            case ("clickdemocollect"):
                collectFragment = (CollectFragment) ma.getFragment();
                collectFragment.pressDemoTicketButton();
                break;
            case ("size_color"):
                productFragment = (ProductFragment) ma.getFragment();
                String size = value.split(" ")[0];
                String color = value.split(" ")[1];
                productFragment.setProductColor("\""+color+"\"");
                productFragment.setProductSize(size);
                Log.d(TAG,"size color executor done");
                break;
            case ("gender_type"):
                selectionFragment = (ProductSelectionFragment) ma.getFragment();
                String gender = value.split(" ")[0];
                String type = value.split(" ")[1];
                ma.status.setGender(gender);
                selectionFragment.setImage(gender,type);
                break;
            default:
                break;
        }
    }

    @Override
    public void stop() {

    }
}