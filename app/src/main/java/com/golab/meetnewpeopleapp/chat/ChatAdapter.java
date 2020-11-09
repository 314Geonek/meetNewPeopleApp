package com.golab.meetnewpeopleapp.chat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebStorage;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.golab.meetnewpeopleapp.R;
import com.google.common.io.Resources;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders> {
    private  List<ChatObject> chatList;
    private Context context;


    public ChatAdapter(List<ChatObject> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp= new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatViewHolders chatViewHolders = new ChatViewHolders(layoutView);
        return chatViewHolders;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolders holder, int position) {
        LinearLayoutCompat.LayoutParams params = (LinearLayoutCompat.LayoutParams) holder.mContainer.getLayoutParams();
        if(chatList.get(position).getCurrentUser())
        {   params.gravity = Gravity.END;
            params.rightMargin = 0;
            params.leftMargin = 200;
            holder.mMessage.setBackgroundResource(R.drawable.chat_item_right);
        }
        else{
            params.gravity = Gravity.START;
            params.leftMargin = 0;
            params.rightMargin = 200;
            holder.mMessage.setBackgroundResource(R.drawable.chat_item_left);
        }
        holder.mContainer.setLayoutParams(params);
        holder.mMessage.setTextColor(Color.parseColor("#ffffff"));
        holder.mMessage.setText(chatList.get(position).getMessage());
    }
    @Override
    public int getItemCount() {
        return this.chatList.size();
    }
}
