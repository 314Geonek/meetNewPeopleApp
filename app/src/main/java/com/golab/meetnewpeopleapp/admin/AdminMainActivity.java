package com.golab.meetnewpeopleapp.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.golab.meetnewpeopleapp.Cards.Array_Adapter;
import com.golab.meetnewpeopleapp.Cards.Cards;
import com.golab.meetnewpeopleapp.ChooseLoginOrRegistrationActivity;
import com.golab.meetnewpeopleapp.Description_Activity;
import com.golab.meetnewpeopleapp.LoginActivity;
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
    private TextView tvPicture;
    private  TextView tvDescription;
    private TextView tvChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        tvChat = findViewById(R.id.forchat);
        tvDescription = findViewById(R.id.fordesc);
        tvPicture = findViewById(R.id.forpicture);
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
                    snapshotUser.getReference().collection("Reports").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshotsReports) {
                            int countPhoto=0;
                            int countDesc=0;
                            int countChat=0;
                            List<String> reportedByForPhoto = new ArrayList<>();
                            List<String> reportedByForDesc = new ArrayList<>();
                            List<String> reportedByForChar = new ArrayList<>();

                            for (DocumentSnapshot snapshotReport:queryDocumentSnapshotsReports) {
                                String reason = snapshotReport.get("Reason") != null ? snapshotReport.get("Reason").toString() : "";
                                String reportedByObject = snapshotReport.get("ReportedBy") != null ? snapshotReport.get("ReportedBy").toString() : "";
                                if(!reportedByObject.equals(""))
                                {
                                    switch(reason)
                                    {
                                        case "Photo":
                                            if(!reportedByForPhoto.contains(reportedByObject)) {
                                                countPhoto++;
                                                reportedByForPhoto.add(reportedByObject);
                                            }
                                            break;
                                        case "Description":
                                            if(!reportedByForDesc.contains(reportedByObject)) {
                                                countDesc++;
                                                reportedByForDesc.add(reportedByObject);
                                            }
                                            break;
                                        case  "Messages":
                                        if(!reportedByForChar.equals(reportedByObject)) {
                                            countChat++;
                                            reportedByForChar.add(reportedByObject);
                                        }
                                        break;
                                    }
                                }
                            }
                            if(countChat>10 || countDesc>0 || countPhoto >0)
                            {

                                rowItems.add(new Cards(snapshotUser, "", countChat, countDesc, countPhoto ));
                                arrayAdapter.notifyDataSetChanged();
                                if(rowItems.size()==1)
                                  giveInfoAboutReports(countChat, countDesc, countPhoto);
                             }
                        }
                    });
                }
            }
        });
    }

    private void giveInfoAboutReports(int chat, int desc, int photo) {
        tvPicture.setText(tvPicture.getText().toString().concat("\n").concat(Integer.toString(photo)));
        tvDescription.setText(tvDescription.getText().toString().concat("\n").concat(Integer.toString(desc)));
        tvChat.setText(tvChat.getText().toString().concat("\n").concat(Integer.toString(chat)));
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
        if(rowItems.size()>0)
            giveInfoAboutReports(rowItems.get(0).getReportedForChat(), rowItems.get(0).getReportedForDesc(), rowItems.get(0).getReportedForPhoto());
        arrayAdapter.notifyDataSetChanged();
    }
    public void goToDescription(View view)
    {
        Intent intent=new Intent(AdminMainActivity.this, Description_Activity.class);
        intent.putExtra("myObject", new Gson().toJson(rowItems.get(0)));
        startActivity(intent);
    }
    public void ban(View view) {
        if(rowItems.size()>0)
            swipe("left");
    }

    public void clear(View view) {
        if(rowItems.size()>0)
            swipe("right");
    }

    public void addOtherAdmin(View view) {
    }

    public void logout(View view) {
        mAuth.signOut();
        finish();
        Intent intent=new Intent(AdminMainActivity.this, ChooseLoginOrRegistrationActivity.class);
        startActivity(intent);
    }
}