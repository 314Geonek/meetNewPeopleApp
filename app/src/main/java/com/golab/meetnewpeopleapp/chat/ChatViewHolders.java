package com.golab.meetnewpeopleapp.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.golab.meetnewpeopleapp.R;


public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView mMessage;
            public ConstraintLayout mContainer;
            public ChatViewHolders(View itemView)
            {
                super(itemView);
                itemView.setOnClickListener(this);
                mMessage =  itemView.findViewById(R.id.message);
                mContainer = itemView.findViewById(R.id.container);
            }

            @Override
            public void onClick(View view)
            {
            }
}
