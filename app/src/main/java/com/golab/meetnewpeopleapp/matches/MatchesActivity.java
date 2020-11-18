package com.golab.meetnewpeopleapp.matches;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.golab.meetnewpeopleapp.ShowSingleProfileActivity;
import com.golab.meetnewpeopleapp.R;
import com.golab.meetnewpeopleapp.chat.ChatObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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
                                {
                                    findViewById(R.id.tvNoMatches).setVisibility(View.GONE);
                                    String matchId=document.getId();
                                    key = currentUserID.equals(document.get("id1").toString()) ? document.get("id2").toString() : document.get("id1").toString();
                                    FetchMatchInformation(key, matchId);}
                            }
                        }
                    }
                });

    }
//  private void getId(String id){
//        db.collection("Matches").document(matchID).collection("Messages").orderBy("writed", Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot snapshots,
//                                @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    return;
//                }
//                for (DocumentChange dc : snapshots.getDocumentChanges())
//                    if(dc.getType().toString().equals("ADDED")){
//                        String content = dc.getDocument().get("content") != null ? dc.getDocument().get("content").toString() : null;
//                        String author = dc.getDocument().get("writerId") != null ? dc.getDocument().get("content").toString() : null;
//
//                        ChatObject chatObject= new ChatObject(content, author.equals(currentUserID)?true : false);
//                        MatchesObject obj = new MatchesObject(userId, name, profileImageUrl, matchID, chatObject);
//                        int counter=-1;
//                        for (int i = 0 ; i < resultsMatches.size();i++) {
//                            System.out.println(resultsMatches.get(i).getMatchId());
//                            if(matchID.equals(resultsMatches.get(i).getMatchId()))
//                            {
//                                counter=i;
//                                break;
//                            }
//                        }
//                        if(counter==-1)
//                            resultsMatches.add(obj);
//                        else
//                            resultsMatches.set(counter, obj);
//                        mMatchesAdapter.notifyDataSetChanged();
//                    }
//
//            }
//        });
//    }


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

private void fetchLastMessage(final String userId,final String name,final  String profileImageUrl, final String matchID) {
    db.collection("Matches").document(matchID).collection("Messages").orderBy("writed", Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot snapshots,
                            @Nullable FirebaseFirestoreException e) {
            if (e != null) {
                return;
            }
            for (DocumentChange dc : snapshots.getDocumentChanges())
                if(dc.getType().toString().equals("ADDED")){
                        String content = dc.getDocument().get("content") != null ? dc.getDocument().get("content").toString() : null;
                        String author = dc.getDocument().get("writerId") != null ? dc.getDocument().get("content").toString() : null;

                        ChatObject chatObject= new ChatObject(content, author.equals(currentUserID)?true : false);
                        MatchesObject obj = new MatchesObject(userId, name, profileImageUrl, matchID, chatObject);
                        int counter=-1;
                        for (int i = 0 ; i < resultsMatches.size();i++) {
                            System.out.println(resultsMatches.get(i).getMatchId());
                            if(matchID.equals(resultsMatches.get(i).getMatchId()))
                            {
                                counter=i;
                                break;
                            }
                        }
                        if(counter==-1)
                            resultsMatches.add(obj);
                        else
                            resultsMatches.set(counter, obj);
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
        Intent intent=new Intent(MatchesActivity.this, ShowSingleProfileActivity.class);
        Bundle b =  new Bundle();
        b.putString("id", currentUserID);
        intent.putExtras(b);
        startActivity(intent);
        finish();
        return;
    }
}