package com.golab.meetnewpeopleapp.matches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.golab.meetnewpeopleapp.R;
import com.golab.meetnewpeopleapp.chat.ChatActivity;


public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView mMatchId;
            public TextView mMatchName;
            public ImageView mMatchImage;
            private String matchId;
            private String userId;
            public MatchesViewHolders(View itemView)
            {
                super(itemView);
                itemView.setOnClickListener(this);
                mMatchId = (TextView) itemView.findViewById(R.id.Matchid);
                mMatchName = (TextView) itemView.findViewById(R.id.MatchName);
                mMatchImage = (ImageView) itemView.findViewById(R.id.MatchImage);
            }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                Bundle b = new Bundle();
                b.putString("chatKey", matchId);
                b.putString("secondUserId", userId);
                intent.putExtras(b);
                view.getContext().startActivity(intent);
            }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
