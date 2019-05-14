package com.example.retaildemo.Executors;

import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.object.conversation.BaseQiChatExecutor;
import com.example.retaildemo.Fragments.CollectFragment;
import com.example.retaildemo.Fragments.EmailFragment;
import com.example.retaildemo.Fragments.FeedbackFragment;
import com.example.retaildemo.Fragments.MainMenuFragment;
import com.example.retaildemo.Fragments.ProductMapFragment;
import com.example.retaildemo.Fragments.ProductFragment;
import com.example.retaildemo.Fragments.ProductUpsellFragment;
import com.example.retaildemo.Fragments.ReturnMainFragment;
import com.example.retaildemo.Fragments.ReturnProductFragment;
import com.example.retaildemo.Fragments.ReturnReasonFragment;
import com.example.retaildemo.Fragments.ProductSelectionFragment;
import com.example.retaildemo.MainActivity;

import java.util.List;

public class FragmentExecutor extends BaseQiChatExecutor {
    private final QiContext qiContext;
    private final MainActivity mainActivity;
    private String TAG = "MSI_FragmentExecutor";

    public FragmentExecutor(QiContext qiContext, MainActivity mainActivity) {
        super(qiContext);
        this.qiContext = qiContext;
        this.mainActivity = mainActivity;
    }

    @Override
    public void runWith(List<String> params) {
        if (params == null || params.isEmpty()) {
            return;
        }
        String fragmentName = params.get(0);
        String data = "";
        if(params.size() == 2){
            data = params.get(1);
        }
        Log.i(TAG,"fragmentName :" +fragmentName);
        switch (fragmentName){
            case ("email"):
                mainActivity.setFragment(new EmailFragment());
                break;
            case ("selection"):
                Log.i(TAG,"executor selection fragment");
                mainActivity.setFragment(new ProductSelectionFragment());
                break;
            case ("mainmenu"):
                mainActivity.setFragment(new MainMenuFragment());
                break;
            case ("map"):
                mainActivity.setFragment(new ProductMapFragment());
                break;
            case ("product"):
                //TODO update with string name
                mainActivity.setFragment(new ProductFragment());
                break;
            case ("upsell"):
                ProductFragment productFragment = (ProductFragment) mainActivity.getFragment();
                productFragment.onClickBuy();
                break;
            case ("feedback"):
                mainActivity.setFragment(new FeedbackFragment());
                break;
            case ("returnprod"):
                mainActivity.setFragment(new ReturnProductFragment());
                break;
            case ("returnmain"):
                mainActivity.setFragment(new ReturnMainFragment());
                break;
            case ("returnreason"):
                mainActivity.setFragment(new ReturnReasonFragment());
                break;
            case ("collect"):
                mainActivity.setFragment(new CollectFragment());
                break;
            case ("product_context"):
                mainActivity.setFragment(new ProductFragment());
                break;
            case ("buy"):
                if(mainActivity.status.getSendEmail()){
                    mainActivity.setFragment(new EmailFragment());
                }else{
                    ProductUpsellFragment productUpsellFragment  =
                            (ProductUpsellFragment) mainActivity.getFragment();
                    productUpsellFragment.setProductUpsell();
                    mainActivity.setFragment(new ProductMapFragment());
                }
                break;
            default:
                mainActivity.setFragment(new MainMenuFragment());
        }
    }

    @Override
    public void stop() {

    }
}