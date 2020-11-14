package com.golab.meetnewpeopleapp.Cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.golab.meetnewpeopleapp.R;

import java.util.List;

public class Array_Adapter extends ArrayAdapter<Cards> {
    public Array_Adapter(@NonNull Context context, int resource, @NonNull List<Cards> objects) {
        super(context, resource, objects);
    }
    public View getView(int possition, View convertView, ViewGroup parent)
    {
        Cards card_item = getItem(possition);
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent,false);
        TextView textView=convertView.findViewById(R.id.nameAgeTxt);
        ImageView imageView = convertView.findViewById(R.id.image);
        TextView distanceText = convertView.findViewById(R.id.locationNameTxt);
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        textView.setText(card_item.getName());
        distanceText.setText(card_item.getDistance() +" "+ distanceText.getText());
        switch(card_item.getProfileImageUrl()){
            case "":
                Glide.with(convertView.getContext()).load(R.mipmap.ic_launcher_round).apply(options).into(imageView);
                break;
            default:
                Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).apply(options).into(imageView);
                break;
        }
        return convertView;
    }
}
