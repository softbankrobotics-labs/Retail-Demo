package com.softbankrobotics.retaildemo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.softbankrobotics.retaildemo.MainActivity;
import com.softbankrobotics.retaildemo.R;
import com.softbankrobotics.retaildemo.Utils.Item;
import com.softbankrobotics.retaildemo.Utils.ReturnAdapter;
import com.github.gcacace.signaturepad.views.SignaturePad;

import java.util.ArrayList;
import java.util.Random;

public class CollectConfirmFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "MSI_CollConfirmFragment";

    private MainActivity ma;
    private Boolean signed = false;
    ArrayList<Item> orders;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        int fragmentId = R.layout.fragment_collect_confirm;
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
        SignaturePad mSignaturePad =  view.findViewById(R.id.signature_pad);
        view.findViewById(R.id.button_back_confirm).setOnClickListener(v ->
                ma.setFragment(new CollectFragment()));
        view.findViewById(R.id.button_home_confirm).setOnClickListener(v ->
                ma.setFragment(new MainMenuFragment()));
        ma.status.lockerNumber = (new Random().nextInt(9)+1);
        ma.setQiVariable("locker",""+ ma.status.lockerNumber);
        TextView locker = view.findViewById(R.id.text_locker);
        locker.setText(getResources().getString(R.string.locker_number)+ ma.status.lockerNumber);
        view.findViewById(R.id.button_open_locker).setOnClickListener(v ->{
            if(mSignaturePad.isEmpty()) {
                TextView ts = view.findViewById(R.id.text_sign);
                ts.setText(getResources().getString(R.string.waring_sign));
            }else{
                ma.getCurrentChatData().goToBookmarkSameTopic("locker");
            }
        });
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() { }

            @Override
            public void onSigned() {
                Log.d(TAG, "the pad is signed");
                ma.setQiVariable("signed","true");
                signed = true;
            }

            @Override
            public void onClear() {
                Log.d(TAG, "the pad is cleared");
                ma.setQiVariable("signed","false");
                signed = false;
            }
        });
        view.findViewById(R.id.button_clear).setOnClickListener(v ->mSignaturePad.clear());
        ListView listViewCollect = view.findViewById(R.id.list_item_collect_confirm);
        orders = createTicket(ma.status.currentTicket);
        ReturnAdapter adapter = new ReturnAdapter(getContext(), ma, orders, false, ma.getResources());
        ma.runOnUiThread(()->listViewCollect.setAdapter(adapter));
    }

    public Boolean getSigned(){
        return signed;
    }

    public ArrayList<Item> createTicket(String ticketName){
        if(!ma.status.dataReturned.containsKey(ticketName)){
            ticketName = "default";
        }
        ArrayList<Item> listViewItems = ma.status.getTicket(ticketName);
        return listViewItems;
    }

    public void onResume() {
        super.onResume();
    }

}
