package com.golab.meetnewpeopleapp.matches;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.golab.meetnewpeopleapp.R;

import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolders> {
    private  List<MatchesObject> matchesList;
    private Context context;


    public MatchesAdapter(List<MatchesObject> matchesList, Context context) {
        this.matchesList = matchesList;
        this.context = context;
    }

    @NonNull
    @Override
    public MatchesViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matches, null, false);
        RecyclerView.LayoutParams lp= new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolders mvh = new MatchesViewHolders(layoutView);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesViewHolders holder, int position) {
        if(matchesList.get(position).getLastMessage()!=null) {
            holder.lastMessage.setText(matchesList.get(position).getLastMessage().getMessage());
            holder.lastMessage.setTextSize(15);
        if(!matchesList.get(position).getLastMessage().getCurrentUser())
            holder.lastMessage.setTypeface(null, Typeface.BOLD);
        }
        holder.mMatchName.setText(matchesList.get(position).getName());
        holder.setMatchId(matchesList.get(position).getMatchId());
        holder.setUserId(matchesList.get(position).getUserId());
        Glide.with(context).load(matchesList.get(position).getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).into(holder.mMatchImage);

    }
    @Override
    public int getItemCount() {
        return matchesList.size();
    }
}
