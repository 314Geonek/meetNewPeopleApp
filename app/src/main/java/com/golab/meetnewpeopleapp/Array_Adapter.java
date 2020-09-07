package com.golab.meetnewpeopleapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class Array_Adapter extends ArrayAdapter<cards> {

    public Array_Adapter(@NonNull Context context, int resource, @NonNull List<cards> objects) {
        super(context, resource, objects);
    }

    public View getView(int possition, View convertView, ViewGroup parent)
    {
        cards card_item = getItem(possition);
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent,false);

        TextView textView=convertView.findViewById(R.id.name);
        ImageView imageView = convertView.findViewById(R.id.image);

        textView.setText(card_item.getName());
        imageView.setImageResource(R.mipmap.ic_launcher);

        return convertView;

    }
}
