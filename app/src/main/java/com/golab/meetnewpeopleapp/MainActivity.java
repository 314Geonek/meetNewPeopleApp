package com.golab.meetnewpeopleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
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
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.local.QueryResult;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.lang.reflect.Array;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Array_Adapter arrayAdapter;
    private FirebaseAuth mAuth;
    private List<cards> rowItems;
    private FirebaseFirestore db;
    private String searchingRange;
    private String userSex,  currentUId;
    private GeoPoint geoPoint;
    private List<String> wantedSex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        geoPoint=null;
        mAuth = FirebaseAuth.getInstance();
        db=  FirebaseFirestore.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();
        getSearchingRange();
        getMyCurrentLocation();
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
    private void getMyCurrentLocation()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location==null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            try {
                geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                Map myCurrentLocation = new HashMap();
                myCurrentLocation.put("lastLocation", geoPoint);
                db.collection("users").document(currentUId).update(myCurrentLocation);
            }catch (Exception e)
            {
                Log.d("Gps and vire disabled","Gps and vire disabled" );
            }
        }

    }

    private void checkUserSex(){
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
    private void getSearchingRange()
    {
        db.collection("users").document(currentUId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.get("searchingRange")!=null)
                    searchingRange = documentSnapshot.get("searchingRange").toString();
            }
        });

    }
    private void checkDistance(final QueryDocumentSnapshot snapshot)
    {   if(searchingRange==null)
        {
            checkOrswiped(snapshot);
        }
        if(searchingRange.contains("Unlimited"))
        {
            checkOrswiped(snapshot);
        }
        else if(snapshot.get("lastLocation")!=null)
        {
            GeoPoint otheruserLocation = (GeoPoint) snapshot.get("lastLocation");
            float [] dist = new float[1];
            Location.distanceBetween(otheruserLocation.getLatitude(), otheruserLocation.getLongitude(), geoPoint.getLatitude(), geoPoint.getLongitude(), dist);
            dist[0] *= 0.000621371192f;
            if(dist[0]<=Float.parseFloat(searchingRange));
            checkOrswiped(snapshot);
        }

    }
    private void getOtherProfiles() {
        Query query =  db.collection("users").whereIn("sex", wantedSex).whereNotEqualTo(FieldPath.documentId(), currentUId);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        checkDistance(document);
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