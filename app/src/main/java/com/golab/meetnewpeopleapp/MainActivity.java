package com.golab.meetnewpeopleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.golab.meetnewpeopleapp.Cards.Array_Adapter;
import com.golab.meetnewpeopleapp.Cards.cards;
import com.golab.meetnewpeopleapp.matches.MatchesActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.errorprone.annotations.Var;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.local.QueryResult;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Array_Adapter arrayAdapter;
    private FirebaseAuth mAuth;
    private  List<cards> rowItems;
    private FirebaseFirestore db;
    private String userSex,  currentUId;
    private  List<String> wantedSex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db=  FirebaseFirestore.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();
        checkUserSex();
        rowItems = new ArrayList<cards>();
        arrayAdapter = new Array_Adapter(this, R.layout.item, rowItems );
        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() { }
            @Override
            public void onLeftCardExit(Object dataObject) {
                swipe("left");
            }
            @Override
            public void onRightCardExit(Object dataObject) {
             swipe("right");
            }
            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) { }
            @Override
            public void onScroll(float scrollProgressPercent) { }});
    }
    private void swipe(String direction)
    {   cards object = (cards) rowItems.get(0);
        String userId= object.getUserId();
          Map swipe = new HashMap();
            if(direction.equals("left"))
                swipe.put("swipe",false);
            else{
                    swipe.put("swipe", true);
                    isMatch(userId);
                }
        db.collection("users").document(userId).collection("SwipedBy").document(currentUId).set(swipe);
        rowItems.remove(0);
        arrayAdapter.notifyDataSetChanged();
    }



    private void isMatch(final String userId) {
        db.collection("users").document(currentUId).collection("SwipedBy").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                if((Boolean)documentSnapshot.get("swipe")==true)
                {   String id = db.collection("Matches").document().getId();
                    Map testMessage = new HashMap();
                    testMessage.put("Owner",null);
                    db.collection("Matches").document(id).collection("Messages").document().set(testMessage);
                    Map chatId = new HashMap();
                    chatId.put("matchId",id);
                    db.collection("users").document(userId).collection("Matches").document(currentUId).set(chatId);
                    db.collection("users").document(currentUId).collection("Matches").document(userId).set(chatId);
                }
            }
        });


    }


    public void checkUserSex(){
        wantedSex=new ArrayList<>();
        db.collection("users").document(currentUId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {       if(task.getResult().get("wantedSex")!=null)
                        {
                                if(task.getResult().get("wantedSex").toString().contains("Male"))
                                        wantedSex.add("Male");
                                if(task.getResult().get("wantedSex").toString().contains("Female"))
                                        wantedSex.add("Female");
                        }
                        else{
                                if(task.getResult().get("sex")!=null)
                                {
                                    userSex = task.getResult().get("sex").toString();
                                    wantedSex.add(userSex.equals("Male") ? "Female" : "Male");

                                }
                            }
                        getOtherProfiles();
                }
            }
        });
    }

    public void getOtherProfiles() {
        Query query =  db.collection("users").whereIn("sex", wantedSex).whereNotEqualTo(FieldPath.documentId(), currentUId);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        checkOrswiped(document);
                        }
                    }}
        });
    }
    private void checkOrswiped(final QueryDocumentSnapshot snapshot)
    {
        snapshot.getReference().collection("SwipedBy").document(currentUId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) {
                    addToRowItems(snapshot);
                }
            }
        });
    }
    private void addToRowItems(DocumentSnapshot snapshot)
    {
        String profileImageUrl = "default";
        if (!snapshot.get("profileImageUrl").toString().equals("default")) {
            profileImageUrl = snapshot.get("profileImageUrl").toString();
        }
        cards item = new cards(snapshot.getId(), snapshot.get("name").toString(), profileImageUrl);
        rowItems.add(item);
        arrayAdapter.notifyDataSetChanged();
    }

    public void goToMatches(View view) {
        Intent intent=new Intent(MainActivity.this, MatchesActivity.class);
        startActivity(intent);
    }

    public void btnSwipe(View view) {
        String direction = view.getId() == R.id.btnOk ? "right" : "left";
        if(!rowItems.isEmpty())
        swipe(direction);
    }


    public void goToProfilMenuActivity(View view) {
        Intent intent=new Intent(MainActivity.this, MyProfileActivity.class);
        startActivity(intent);
    }
    public void  goToDescription(View view) {

           Intent intent=new Intent(MainActivity.this, MyProfileActivity.class);
          startActivity(intent);
    }
}