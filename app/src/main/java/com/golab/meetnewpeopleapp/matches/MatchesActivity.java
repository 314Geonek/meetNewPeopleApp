package com.golab.meetnewpeopleapp.matches;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.golab.meetnewpeopleapp.MyProfileActivity;
import com.golab.meetnewpeopleapp.R;
import com.golab.meetnewpeopleapp.chat.ChatObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.LinkedList;
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
        getUserMatchesId("id1");
        getUserMatchesId("id2");

    }

    private void getUserMatchesId(String id) {
        db.collection("Matches").whereEqualTo(id, currentUserID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String key;
                                if(document.get("id1") !=null && document.get("id2")!=null)
                                {   String matchId=document.getId();
                                    key = currentUserID.equals(document.get("id1").toString()) ? document.get("id2").toString() : document.get("id1").toString();
                                    FetchMatchInformation(key, matchId);}
                            }
                        }
                    }
                });

    }

    private void FetchMatchInformation(final  String key, final String matchId) {
        db.collection("users").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.getResult().exists())
                    {
                        Map tsk = task.getResult().getData();
                        String userId = task.getResult().getId();
                        String name = "";
                        String profileImageUrl = "";
                        if(tsk.get("name")!=null)
                        {
                            name = tsk.get("name").toString();
                        }
                        if(tsk.get("profileImageUrl")!=null){
                            profileImageUrl = tsk.get("profileImageUrl").toString();
                        }
                        fetchLastMessage(userId, name, profileImageUrl, matchId);
                    }

            }
        });
    }
    private void fetchLastMessage(final String userId,final String name,final  String profileImageUrl, final String matchID)
    {
        db.collection("Matches").document(matchID).collection("Messages").orderBy("writed", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ChatObject chatObject = null;
                if(task.isSuccessful())
                    for (DocumentSnapshot ds: task.getResult()) {
                        boolean isCreatedByCurrentUser = ds.get("writerId").equals(currentUserID) ? true : false;
                        chatObject= new ChatObject(ds.get("content").toString(),isCreatedByCurrentUser);
                    }
                MatchesObject obj = new MatchesObject(userId, name, profileImageUrl, matchID, chatObject);
                resultsMatches.add(obj);
                mMatchesAdapter.notifyDataSetChanged();
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