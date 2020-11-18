package com.golab.meetnewpeopleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.golab.meetnewpeopleapp.Cards.Array_Adapter;
import com.golab.meetnewpeopleapp.Cards.Cards;
import com.golab.meetnewpeopleapp.matches.MatchesActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {
    private Array_Adapter arrayAdapter;
    private FirebaseAuth mAuth;
    private List<Cards> rowItems;
    private FirebaseFirestore db;
    private Float searchingRange;
    private String currentUId;
    private Location myLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private List<String> lookingFor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        currentUId = mAuth.getCurrentUser().getUid();
        getMyCurrentLocation();
        getDatailsOfSearching();
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


    private void swipe(String direction) {
        Cards object = (Cards) rowItems.get(0);
        String userId = object.getId();
        Map swipe = new HashMap();
        if (direction.equals("left"))
            swipe.put("swipe", false);
        else {
            swipe.put("swipe", true);
            isMatch(userId);
        }
        db.collection("users").document(userId).collection("SwipedBy").document(currentUId).set(swipe);
        rowItems.remove(0);
        arrayAdapter.notifyDataSetChanged();
    }


    private void isMatch(final String userId) {
        db.collection("users").document(currentUId).collection("SwipedBy").
                document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists())
                    if ((Boolean) documentSnapshot.get("swipe")) {
                        Map match = new HashMap();
                        match.put("id1", currentUId);
                        match.put("id2", userId);
                        db.collection("Matches").document().set(match);
                    }
            }
        });
    }
    private void getMyCurrentLocation()
    {
        while (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            GeoPoint lastLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                            myLocation = location;
                            Map myCurrentLocation = new HashMap();
                            myCurrentLocation.put("lastLocation", lastLocation);
                            db.collection("users").document(currentUId).update(myCurrentLocation);
                        }
                        else {
                            myLocation = null;
                            Toast.makeText(MainActivity.this, getString(R.string.notFoundGps),Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }


    private void getDatailsOfSearching(){
        lookingFor =new ArrayList<>();
        db.collection("users").document(currentUId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {       if(task.getResult().get("lookingFor")!=null)
                        {
                                if(task.getResult().get("lookingFor").toString().contains("Male"))
                                        lookingFor.add("Male");
                                if(task.getResult().get("lookingFor").toString().contains("Female"))
                                        lookingFor.add("Female");
                        }
                        if(task.getResult().get("searchingRange")!=null)
                        if(!task.getResult().get("searchingRange").toString().equals("false"))
                        {
                            searchingRange = Float.parseFloat(task.getResult().get("searchingRange").toString());
                        }
                        else searchingRange=null;
                        getOtherProfiles();
                }
            }
        });
    }
    private void checkDistance(final QueryDocumentSnapshot snapshot)
    {
        if(myLocation==null)
        {
            addToRowItems(snapshot, "");
        }

        if(snapshot.get("lastLocation")!=null)
        {
            Location otherUserLocation= new Location("otherUserLocation");
            GeoPoint tmp = (GeoPoint)snapshot.get("lastLocation");
            otherUserLocation.setLatitude(tmp.getLatitude());
            otherUserLocation.setLongitude(tmp.getLongitude());
            int distance = (int)(myLocation.distanceTo(otherUserLocation)/1000);
            if(searchingRange==null || distance<=searchingRange)
                addToRowItems(snapshot, Integer.toString(distance));
        }

    }
    private void getOtherProfiles() {
        Query query =  db.collection("users").whereIn("gender", lookingFor).whereNotEqualTo(FieldPath.documentId(), currentUId);
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
                    checkDistance(snapshot);
                }
            }
        });
    }

    private void addToRowItems(DocumentSnapshot snapshot, String distance)
    {
        distance = distance.concat(getResources().getString(R.string.away));
        Cards item = new Cards(snapshot, distance);
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void goToProfilMenuActivity(View view) {
        Intent intent=new Intent(MainActivity.this, ShowSingleProfileActivity.class);
        Bundle b = new Bundle();
        b.putString("id", currentUId);
        intent.putExtras(b);
        startActivity(intent);
    }
    public void  goToDescription(View view) {
           Intent intent=new Intent(MainActivity.this, Description_Activity.class);
           intent.putExtra("myObject", new Gson().toJson(rowItems.get(0)));
           startActivity(intent);
    }
}