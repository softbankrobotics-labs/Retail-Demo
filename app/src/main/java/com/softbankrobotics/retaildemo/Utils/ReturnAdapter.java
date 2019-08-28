package com.softbankrobotics.retaildemo.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.softbankrobotics.retaildemo.Fragments.ReturnProductFragment;
import com.softbankrobotics.retaildemo.MainActivity;
import com.softbankrobotics.retaildemo.R;

import java.lang.reflect.Field;
import java.util.List;

public class ReturnAdapter extends ArrayAdapter<Item> {

    private final String TAG = "MSI_ReturnAdapter";

    private MainActivity mainActivity;
    private Boolean clickable;
    private Resources resources;

    public ReturnAdapter(Context context, MainActivity mainActivity, List<Item> myReturn, Boolean clickable, Resources resources) {
        super(context, 0, myReturn);
        this.mainActivity = mainActivity;
        this.clickable = clickable;
        this.resources = resources;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_return_ticket,parent,false
            );
        }

        final Item currentReturned = getItem(position);
        TextView name = listItemView.findViewById(R.id.selected_name);
        TextView price = listItemView.findViewById(R.id.selected_price);
        LinearLayout linearLayout = listItemView.findViewById(R.id.layout_item);
        ImageView imageView = listItemView.findViewById(R.id.image_returned_item);
        Log.d(TAG,"ressourceName : " + currentReturned.getRessourceName());
        imageView.setImageResource(getResId(currentReturned.getRessourceName(),R.drawable.class));

        name.setText(resources.getString(getResId(currentReturned.getRessourceName(),R.string.class)));
        price.setText(currentReturned.getPrice()+" €");
        if(clickable){
            linearLayout.setOnClickListener(view ->{
                ReturnProductFragment fragment = new ReturnProductFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", resources.getString(getResId(currentReturned.getRessourceName(),R.string.class)));
                bundle.putString("price", currentReturned.getPrice());
                bundle.putString("resource", currentReturned.getRessourceName());
                fragment.setArguments(bundle);
                mainActivity.setFragment(fragment);
            });
        }
        return listItemView;
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

}
