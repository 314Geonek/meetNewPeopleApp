package com.golab.meetnewpeopleapp.admin.journal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.golab.meetnewpeopleapp.R;
import com.golab.meetnewpeopleapp.chat.ChatActivity;

public class JournalViewHolder extends RecyclerView.ViewHolder{
    public TextView reason;
    public TextView reportedBy;
    public TextView date;
    public JournalViewHolder(@NonNull View itemView) {
        super(itemView);
        reason= itemView.findViewById(R.id.reasonOfReport);
        reportedBy = itemView.findViewById(R.id.reasonOfReport);
        date = itemView.findViewById(R.id.date);
    }


}
