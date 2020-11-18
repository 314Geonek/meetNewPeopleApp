package com.golab.meetnewpeopleapp.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ListView;

import com.golab.meetnewpeopleapp.R;
import com.golab.meetnewpeopleapp.admin.journal.JournalAdapter;
import com.golab.meetnewpeopleapp.admin.journal.JournalObject;
import com.golab.meetnewpeopleapp.matches.MatchesActivity;
import com.golab.meetnewpeopleapp.matches.MatchesAdapter;
import com.golab.meetnewpeopleapp.matches.MatchesObject;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminJournalActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mJournalAdapter;
    private RecyclerView.LayoutManager mJournalLayoutManager;
    private String id;
    private ArrayList<JournalObject> resultsReports= new ArrayList <JournalObject>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_journal);
        id= getIntent().getExtras().getString("id");
        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        mJournalLayoutManager = new LinearLayoutManager(AdminJournalActivity.this);
//        mRecyclerView.setLayoutManager(mJournalLayoutManager);
//        mJournalAdapter = new JournalAdapter(getDataSetReports(), AdminJournalActivity.this);
//        mRecyclerView.setAdapter(mJournalAdapter);
//        fetchReports();

    }

    private void fetchReports() {
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        db.collection("users").document(id).collection("Reports").orderBy("date").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot:
                     queryDocumentSnapshots) {
                     resultsReports.add(new JournalObject(snapshot));
                     mJournalAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private List<JournalObject> getDataSetReports() {
        return resultsReports;
    }


}