package com.golab.meetnewpeopleapp.chat;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.golab.meetnewpeopleapp.MainActivity;
import com.golab.meetnewpeopleapp.R;
import com.golab.meetnewpeopleapp.ShowSingleProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private String currentUserID, userMatchId;
    private EditText mMessage;
    private CollectionReference mDbChat;
    private FirebaseFirestore db;
    private String matchId;
    private TextView mName;
    private ImageButton ibPicture;
    private ArrayList<ChatObject> resultsMessages;
    private boolean isStillMatch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        matchId = getIntent().getExtras().getString("chatKey");
        userMatchId = getIntent().getExtras().getString("secondUserId");
        mMessage= (EditText) findViewById(R.id.userMessage);
        mName = (TextView) findViewById(R.id.tvMatchName);
        ibPicture=(ImageButton) findViewById(R.id.ibMatchpic);
        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        resultsMessages = new ArrayList <ChatObject>();
        db= FirebaseFirestore.getInstance();
        checkOrStillMatch();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDbChat= FirebaseFirestore.getInstance().collection("Matches").document(matchId).collection("Messages");
        fillNavBar();
        getChatMessages();
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(resultsMessages, ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);
    }

    private void checkOrStillMatch() {
        db.collection("Matches").document(matchId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                db.collection("Matches").document(matchId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(!task.getResult().exists())
                        kill();
                    }
                });
            }
        });
    }

    private void fillNavBar()
    {    db.collection("users").document(userMatchId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot document) {
            if(document.exists()){
                mName.setText(document.get("name").toString().length()>=10 ? document.get("name").toString().substring(0,7).concat("...") :  document.get("name").toString());
                ibPicture.setBackground(null);
            if(!document.get("profileImageUrl").toString().equals("default"))
            Glide.with(getApplication()).load(document.get("profileImageUrl").toString()).apply(RequestOptions.circleCropTransform())
                    .into(ibPicture);
        }}
    });
    }
    private void getChatMessages() {
        mDbChat.orderBy("writed").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (DocumentChange dc : snapshots.getDocumentChanges()){
                        if(dc.getType().toString().equals("ADDED")){
                        String content = dc.getDocument().get("content") != null ? dc.getDocument().get("content").toString() : "";
                        String writerId = dc.getDocument().get("writerId") != null ? dc.getDocument().get("writerId").toString() : "";
                        Boolean readed = dc.getDocument().get("readed")!= null ? (boolean) dc.getDocument().get("readed") : false;
                            if(!readed && !writerId.equals(currentUserID))
                                dc.getDocument().getReference().update("readed",true);
                            Boolean currentUserBoolean = writerId.equals(currentUserID) ? true : false;
                            ChatObject newMessage = new ChatObject(content, currentUserBoolean, readed);
                            resultsMessages.add(newMessage);
                            mChatAdapter.notifyDataSetChanged();
                }
            }}
        });
    }

    public void sendMessage(View view) {
        String messageText= mMessage.getText().toString();
        if(!messageText.isEmpty())
        {
            Map newMessage= new HashMap();
            newMessage.put("writerId", currentUserID);
            newMessage.put("content", messageText);
            newMessage.put("writed", Timestamp.now().toDate());
            newMessage.put("readed", false);
            mDbChat.document().set(newMessage);
        }
        mMessage.setText(null);
    }



    public void goBack(View view) {
        finish();
        return;
    }

    public void showProfile(View view) {
        Intent intent=new Intent(ChatActivity.this, ShowSingleProfileActivity.class);
        Bundle b = new Bundle();
        b.putString("idMatch", matchId);
        b.putString("id", userMatchId);
        intent.putExtras(b);
        startActivity(intent);
    }
    private void kill() {
        finish();
    }
}