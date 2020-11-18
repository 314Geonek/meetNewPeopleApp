package com.golab.meetnewpeopleapp.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.golab.meetnewpeopleapp.Cards.Array_Adapter;
import com.golab.meetnewpeopleapp.Cards.Cards;
import com.golab.meetnewpeopleapp.Description_Activity;
import com.golab.meetnewpeopleapp.MainActivity;
import com.golab.meetnewpeopleapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminMainActivity extends AppCompatActivity {
    private Array_Adapter arrayAdapter;
    private FirebaseAuth mAuth;
    private List<Cards> rowItems;
    private FirebaseFirestore db;
    private String currentUId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();
        search();
        rowItems = new ArrayList<Cards>();
        arrayAdapter = new Array_Adapter(this, R.layout.item, rowItems);
        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                swipe("left");
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                swipe("right");
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });
    }

    private void search() {

        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshotsUser) {
                for (final DocumentSnapshot snapshotUser:queryDocumentSnapshotsUser) {
                    System.out.println("found");
                    snapshotUser.getReference().collection("Reports").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshotsReports) {
                            for (DocumentSnapshot snapshotReport:queryDocumentSnapshotsReports) {
                                System.out.println("found");
                                    rowItems.add(new Cards(snapshotUser, "" ));
                                    arrayAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });
    }


    private void swipe(String direction) {
        Cards object = (Cards) rowItems.get(0);
        String userId = object.getId();
        db.collection("users").document(userId).collection("Reports").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapschot:queryDocumentSnapshots) {
                    Map checked = new HashMap();
                    checked.put("checked", true);
                    snapschot.getReference().set(checked);
                }
            }
        });
        if(direction.equals("left")){
            Map m= new HashMap();
            m.put("Ban", currentUId);
            db.collection("users").document(userId).set(m);
        }
        rowItems.remove(0);
        arrayAdapter.notifyDataSetChanged();
    }
    public void goToDescription(View view)
    {
        Intent intent=new Intent(AdminMainActivity.this, Description_Activity.class);
        intent.putExtra("myObject", new Gson().toJson(rowItems.get(0)));
        startActivity(intent);
    }
    public void ban(View view) {
        swipe("left");
    }

    public void clear(View view) {
        swipe("right");
    }

    public void journal(View view) {
        Intent intent=new Intent(AdminMainActivity.this, AdminJournalActivity.class);
        Bundle b = new Bundle();
        b.putString("id", rowItems.get(0).getId());
        intent.putExtras(b);
        startActivity(intent);
    }

    public void addOtherAdmin(View view) {
    }
}