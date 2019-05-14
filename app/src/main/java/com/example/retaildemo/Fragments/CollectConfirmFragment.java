package com.example.retaildemo.Fragments;

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

import com.example.retaildemo.MainActivity;
import com.example.retaildemo.R;
import com.example.retaildemo.Utils.Item;
import com.example.retaildemo.Utils.ReturnAdapter;
import com.github.gcacace.signaturepad.views.SignaturePad;

import java.util.ArrayList;

public class CollectConfirmFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "MSI_CollConfirmFragment";

    private MainActivity mainActivity;
    private int fragmentId = R.layout.fragment_collect_confirm;
    ArrayList<Item> orders;


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
        SignaturePad mSignaturePad =  view.findViewById(R.id.signature_pad);
        view.findViewById(R.id.button_back_confirm).setOnClickListener(v ->
                mainActivity.setFragment(new CollectFragment()));
        view.findViewById(R.id.button_home_confirm).setOnClickListener(v ->
                mainActivity.setFragment(new MainMenuFragment()));
        view.findViewById(R.id.button_open_locker).setOnClickListener(v ->{
            if(mSignaturePad.isEmpty()) {
                TextView ts = view.findViewById(R.id.text_sign);
                ts.setText(mainActivity.getResources().getString(R.string.waring_sign));
            }else{
                mainActivity.goToBookmarkInTopic("locker","collectconfirm");
            }
        });
        view.findViewById(R.id.clear).setOnClickListener(v ->mSignaturePad.clear());
        ListView listViewCollect = view.findViewById(R.id.listviewCollectConfirm);
        orders = createTicket(mainActivity.status.currentTicket);
        ReturnAdapter adapter = new ReturnAdapter(getContext(), mainActivity, orders, false);
        mainActivity.runOnUiThread(()->listViewCollect.setAdapter(adapter));

        //ReturnAdapter adapter = new ReturnAdapter(getContext(), mainActivity, mainActivity.status.getItemsToCollect());
        //mainActivity.runOnUiThread(()->listViewCollect.setAdapter(adapter));
    }



    public ArrayList<Item> createTicket(String ticketName){
        if(!mainActivity.status.dataReturned.containsKey(ticketName)){
            ticketName = "default";
        }
        ArrayList<Item> listViewItems = mainActivity.status.getTicket(ticketName);
        return listViewItems;
    }

    public void onResume() {
        super.onResume();
    }

}
