package com.golab.meetnewpeopleapp.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.golab.meetnewpeopleapp.R;
import com.golab.meetnewpeopleapp.matches.MatchesAdapter;
import com.golab.meetnewpeopleapp.matches.MatchesObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private String currentUserID, matchId;
    private EditText mMessage;
    private DatabaseReference mDatabaseUser, mDatabaseChat;
    private String chatId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mMessage= (EditText) findViewById(R.id.userMessage);
        matchId = getIntent().getExtras().getString("matchId");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("Matches").child(matchId).child("ChatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");
        getChatId();

        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);
    }
    private ArrayList<ChatObject> resultsMessages = new ArrayList <ChatObject>();
    private List<ChatObject> getDataSetChat() {
        return resultsMessages;
    }
    private void getChatId()
    {
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    chatId=snapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getChatMessages() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists())
                {   System.out.println("dsadaas");
                    String message= null;
                    String createdBy = null;
                    System.out.println(snapshot.child("text").getValue().toString());
                    if(snapshot.child("text").getValue()!=null)
                    {
                        message= snapshot.child("text").getValue().toString();

                    }
                    if(snapshot.child("writer").getValue()!=null)
                    {
                        createdBy= snapshot.child("writer").getValue().toString();
                    }
                    if(message!=null && createdBy!=null)
                    {
                        Boolean currentUserBoolean =createdBy.equals(currentUserID) ? true : false;
                        ChatObject newMessage = new ChatObject(message,currentUserBoolean);
                        resultsMessages.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void sendMessage(View view) {
    String messageText= mMessage.getText().toString();
    if(!messageText.isEmpty())
    {
        DatabaseReference newMessageDb = mDatabaseChat.push();
        Map newMessage= new HashMap();
        newMessage.put("writer", currentUserID);
        newMessage.put("text", messageText);
        newMessageDb.setValue(newMessage);
    }
    mMessage.setText(null);
    }
}