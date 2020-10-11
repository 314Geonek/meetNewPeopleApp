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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Array_Adapter arrayAdapter;
    private FirebaseAuth mAuth;
    List<cards> rowItems;
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
    {
        cards object = (cards) rowItems.get(0);
        String userId= object.getUserId();
        Map swipe = new HashMap();
        if(direction.equals("left"))
        swipe.put("swipe",false);
        else {
            swipe.put("swipe", true);
            isMatch(userId);
        }
        System.out.println(direction);
        db.collection("users").document(userId).collection("swipes").document(currentUId).set(swipe);
        rowItems.remove(0);
        arrayAdapter.notifyDataSetChanged();
    }


    private void isMatch(final String userId) {
        db.collection("users").document(currentUId).collection("swipes").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {   String id = db.collection("chats").document().getId();
                    Map testMessage = new HashMap();
                    testMessage.put("Owner",null);
                    db.collection("chats").document(id).set(testMessage);
                    Map chatId = new HashMap();
                    chatId.put("chatId",id);
                    db.collection("users").document(userId).collection("matches").document(currentUId).set(chatId);
                    db.collection("users").document(currentUId).collection("matches").document(userId).set(chatId);
                    Toast.makeText(MainActivity.this, "Connection",Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    public void checkUserSex(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("users").document(currentUId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {       if(task.getResult().get("wantedSex")!=null)
                        {
                                if(task.getResult().get("wantedSex").equals("Male"))
                                    wantedSex.add("Male");
                                if(task.getResult().get("wantedSex").equals("Feale"))
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                return;
            }
        });
    }

    public void getOtherProfiles() {
        db.collection("users").whereIn("wantedSex", wantedSex)
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        if(wantedSex.contains(document.get("sex").toString()) && document.getId()!=currentUId)
                        {
                                document.getReference().collection("swipes").document(currentUId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(!documentSnapshot.exists())
                                        {
                                            String profileImageUrl = "default";
                                            if (!document.get("profileImageUrl").toString().equals("default")) {
                                                profileImageUrl = document.get("profileImageUrl").toString();
                                            }
                                            cards item = new cards(document.getId(), document.get("name").toString(), profileImageUrl);
                                            rowItems.add(item);
                                            arrayAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                        }
                    }
                    }
            }
        });


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
        Intent intent=new Intent(MainActivity.this, ProfilMenuActivity.class);
        startActivity(intent);
    }
}