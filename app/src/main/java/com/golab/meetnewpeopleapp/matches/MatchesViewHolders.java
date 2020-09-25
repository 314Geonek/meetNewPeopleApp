package com.golab.meetnewpeopleapp.matches;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.golab.meetnewpeopleapp.R;


public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView mMatchesId;
            public MatchesViewHolders(View itemView)
            {
                super(itemView);
                itemView.setOnClickListener(this);
                mMatchesId= (TextView) itemView.findViewById(R.id.MatchId);
            }

            @Override
            public void onClick(View view)
            {

            }
}
