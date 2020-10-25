package com.golab.meetnewpeopleapp.matches;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Snapshot;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.golab.meetnewpeopleapp.MainActivity;
import com.golab.meetnewpeopleapp.MyProfileActivity;
import com.golab.meetnewpeopleapp.ProfilMenuActivity;
import com.golab.meetnewpeopleapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    private ArrayList<MatchesObject> resultsMatches= new ArrayList <MatchesObject>();
    private String currentUserID;
    private FirebaseFirestore db;
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
        db = FirebaseFirestore.getInstance();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getUserMatchesId();
    }

    private void getUserMatchesId() {
        
        db.collection("users").document(currentUserID).collection("Matches").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                FetchMatchInformation(document.getId());
                            }
                        }
                    }
                });

    }

    private void FetchMatchInformation(final  String key) {
        db.collection("users").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.getResult().exists())
                    {
                        Map tsk = task.getResult().getData();
                        String userId = key;
                        String name = "";
                        String profileImageUrl = "";
                        if(tsk.get("name")!=null)
                        {
                            name = tsk.get("name").toString();
                        }
                        if(tsk.get("profileImageUrl")!=null){
                            profileImageUrl = tsk.get("profileImageUrl").toString();
                        }
                        MatchesObject obj = new MatchesObject(userId, name, profileImageUrl);
                        resultsMatches.add(obj);
                        mMatchesAdapter.notifyDataSetChanged();
                    }

            }
        });
    }

    private List<MatchesObject> getDataSetMatchers() {
        return resultsMatches;
    }

    public void goToMainActivity(View view) {
        finish();
        return;
    }

    public void goToMyProfile(View view) {
        Intent intent=new Intent(MatchesActivity.this, MyProfileActivity.class);
        startActivity(intent);
        finish();
        return;
    }
}