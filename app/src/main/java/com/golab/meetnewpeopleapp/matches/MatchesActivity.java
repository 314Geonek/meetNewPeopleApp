package com.golab.meetnewpeopleapp.matches;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.golab.meetnewpeopleapp.R;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    private ArrayList<MatchesObject> resultsMatches= new ArrayList <MatchesObject>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(MatchesActivity.this);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new MatchesAdapter(getDataSetMatchers(), MatchesActivity.this);
        mRecyclerView.setAdapter(mMatchesAdapter);
            for(int i=0;i<100;i++){
        MatchesObject obj = new MatchesObject(Integer.toString(i));
        resultsMatches.add(obj); }

        mMatchesAdapter.notifyDataSetChanged();
    }

    private List<MatchesObject> getDataSetMatchers() {
        return resultsMatches;
    }
}