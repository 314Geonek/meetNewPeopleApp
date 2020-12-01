package com.golab.meetnewpeopleapp.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.golab.meetnewpeopleapp.Cards.Array_Adapter;
import com.golab.meetnewpeopleapp.Cards.Cards;
import com.golab.meetnewpeopleapp.Description_Activity;
import com.golab.meetnewpeopleapp.LoginActivity;
import com.golab.meetnewpeopleapp.R;
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
        tvChat = findViewById(R.id.forChat);
        tvDescription = findViewById(R.id.forDesc);
        tvPicture = findViewById(R.id.forPicture);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if(mAuth.getCurrentUser()!=null)
            currentUId = mAuth.getCurrentUser().getUid();
        rowItems = new ArrayList<>();
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
                    if(snapshotUser.get("banned")==null)
                        snapshotUser.getReference().collection("Reports").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshotsReports) {
                                countReports(queryDocumentSnapshotsReports, snapshotUser);
                            }
                        });
                }
            }
        });
    }
private void countReports(QuerySnapshot queryDocumentSnapshotsReports, DocumentSnapshot snapshotUser)
{
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
                    if(!reportedByForChar.contains(reportedByObject)) {
                        countChat++;
                        reportedByForChar.add(reportedByObject);
                    }
                    break;
            }
        }
    }
    if(countChat>10 || countDesc>0 || countPhoto >0)
    {   Cards item = new Cards(snapshotUser, "", countChat, countDesc, countPhoto );
        if(rowItems.size()==0 ||(rowItems.size()>0 && rowItems.get(0).equals(item))) {
        rowItems.add(item);
        arrayAdapter.notifyDataSetChanged();
        if(rowItems.size()==1)
            giveInfoAboutReports(countChat, countDesc, countPhoto);}
    }
}
    @Override
    protected void onStart() {
        for(int i=1; i<rowItems.size(); i++)
            rowItems.remove(i);
        arrayAdapter.notifyDataSetChanged();
        search();
        super.onStart();
    }
    private void giveInfoAboutReports(int chat, int desc, int photo) {
        tvPicture.setText(getResources().getString(R.string.for_picture).concat("\n").concat(Integer.toString(photo)));
        tvDescription.setText(getResources().getString(R.string.for_profile_data).concat("\n").concat(Integer.toString(desc)));
        tvChat.setText(getResources().getString(R.string.for_chat).concat("\n").concat(Integer.toString(chat)));
    }


    private void swipe(String direction) {
        Cards object = (Cards) rowItems.get(0);
        String userId = object.getId();
        if(direction.equals("right"))
        db.collection("users").document(userId).collection("Reports").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapschot:queryDocumentSnapshots) {
                    if(snapschot.get("Reason")!=null)
                    {
                        if(!snapschot.get("Reason").equals("Messages"))
                            snapschot.getReference().delete();
                    }
                }
            }
        });
        if(direction.equals("left")){
                Map m= new HashMap();
                m.put("banned", true);
                db.collection("users").document(userId).update(m);
                removeMatches(userId, "id1");
                removeMatches(userId, "id2");
        }
        rowItems.remove(0);
        if(rowItems.size()>0)
            giveInfoAboutReports(rowItems.get(0).getReportedForChat(), rowItems.get(0).getReportedForDesc(),
                    rowItems.get(0).getReportedForPhoto());
        else giveInfoAboutReports(0,0,0);
        arrayAdapter.notifyDataSetChanged();
    }

    private void removeMatches(String id, String idtext) {
        db.collection("Matches").whereEqualTo(idtext, id).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot dc:
                     queryDocumentSnapshots) {
                    dc.getReference().collection("Messages").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots2) {
                            for (DocumentSnapshot ds2:
                                 queryDocumentSnapshots2) {
                                ds2.getReference().delete();
                            }
                        }
                    });
                    dc.getReference().delete();
                }
            }
        });
    }

    public void goToDescription(View view)
    {
        Intent intent=new Intent(AdminMainActivity.this, Description_Activity.class);
        intent.putExtra("myObject", new Gson().toJson(rowItems.get(0)));
        Bundle b = new Bundle();
        b.putString("admin","y");
        intent.putExtras(b);
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
        Intent intent=new Intent(AdminMainActivity.this, Create_new_admin_Activity.class);
        startActivity(intent);

    }
    public void askAboutLogout(View view)
    {
        findViewById(R.id.makeSureLogout).setVisibility(View.VISIBLE);
    }
    public void sureLogout(View view) {
        mAuth.signOut();
        finish();
        Intent intent=new Intent(AdminMainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
    public void cancelLogout(View view) {
        hideAskAboutLogout();
    }

    private void hideAskAboutLogout() {
    findViewById(R.id.makeSureLogout).setVisibility(View.GONE);
    }
    public void doNothingLogout(View view) {
    }

}