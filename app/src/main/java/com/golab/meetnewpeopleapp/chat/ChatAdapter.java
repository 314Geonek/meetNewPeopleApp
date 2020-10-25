package com.golab.meetnewpeopleapp.chat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
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
        ChatViewHolders cvh = new ChatViewHolders(layoutView);
        return cvh;
    }
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolders holder, int position) {
        holder.mMessage.setText(chatList.get(position).getMessage());
        if(chatList.get(position).getCurrentUser())
        {   ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.mMessage.getLayoutParams();
            holder.mMessage.setTextColor(Color.parseColor("#ffffff"));
            holder.mMessage.setBackgroundResource(R.drawable.chat_item_right);
            holder.mMessage.requestLayout();
        }
        else {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.mMessage.getLayoutParams();
            params.rightToRight = ConstraintLayout.LayoutParams.UNSET;
            params.leftToLeft =R.id.parent;
            holder.mMessage.setTextColor(Color.parseColor("#ffffff"));
            holder.mMessage.setBackgroundResource(R.drawable.chat_item_left);
        }
    }
    @Override
    public int getItemCount() {
        return this.chatList.size();
    }
}
