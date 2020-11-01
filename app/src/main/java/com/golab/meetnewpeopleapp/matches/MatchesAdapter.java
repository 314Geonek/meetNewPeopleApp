package com.golab.meetnewpeopleapp.matches;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        holder.mMatchId.setText(matchesList.get(position).getUserId());
        holder.mMatchName.setText(matchesList.get(position).getName());
        if(!matchesList.get(position).getProfileImageUrl().equals("default")){
            Glide.with(context).load(matchesList.get(position).getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).into(holder.mMatchImage);
        }
    }
    @Override
    public int getItemCount() {
        return matchesList.size();
    }
}
