package com.example.retaildemo.Utils;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.retaildemo.Fragments.ReturnProductFragment;
import com.example.retaildemo.MainActivity;
import com.example.retaildemo.R;

import java.lang.reflect.Field;
import java.util.List;

public class ReturnAdapter extends ArrayAdapter<Item> {

    private MainActivity mainActivity;
    private Boolean clickable;

    public ReturnAdapter(Context context, MainActivity mainActivity, List<Item> myReturn,Boolean clickable) {
        super(context, 0, myReturn);
        this.mainActivity = mainActivity;
        this.clickable = clickable;
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
        imageView.setImageResource(getResId(currentReturned.getRessourceName(),R.drawable.class));

        name.setText(currentReturned.getName());
        price.setText(currentReturned.getPrice()+" €");
        if(clickable){
            linearLayout.setOnClickListener(view ->{
                ReturnProductFragment fragment = new ReturnProductFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", currentReturned.getName());
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
