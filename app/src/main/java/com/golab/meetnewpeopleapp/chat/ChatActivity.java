package com.golab.meetnewpeopleapp.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.golab.meetnewpeopleapp.MainActivity;
import com.golab.meetnewpeopleapp.ProfilMenuActivity;
import com.golab.meetnewpeopleapp.R;
import com.golab.meetnewpeopleapp.matches.MatchesAdapter;
import com.golab.meetnewpeopleapp.matches.MatchesObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
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
    private String currentUserID, matchId;
    private EditText mMessage;
    private CollectionReference mDbChat;
    private FirebaseFirestore db;
    private String chatId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mMessage= (EditText) findViewById(R.id.userMessage);
        db= FirebaseFirestore.getInstance();
        matchId = getIntent().getExtras().getString("matchId");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
        db.collection("users").document(currentUserID).collection("matches").document(matchId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    if(task.getResult().get("chatId")!=null){
                    chatId = task.getResult().get("chatId").toString();
                    mDbChat= FirebaseFirestore.getInstance().collection("chats").document(chatId).collection("messages");
                    getChatMessages();
                }

                }

            }
        });

    }

    private void getChatMessages() {
        mDbChat .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            String message= null;
                            String createdBy = null;
                            if(dc.getDocument().get("text")!=null)
                            {
                                message=dc.getDocument().get("text").toString();
                            }
                            if(dc.getDocument().get("writer")!=null)
                            {
                                createdBy= dc.getDocument().get("writer").toString();
                            }
                            System.out.println(createdBy.concat(message));
                            if(message!=null && createdBy!=null)
                            {
                                Boolean currentUserBoolean =createdBy.equals(currentUserID) ? true : false;
                                System.out.println(currentUserBoolean);

                                ChatObject newMessage = new ChatObject(message,currentUserBoolean);
                                resultsMessages.add(newMessage);
                                mChatAdapter.notifyDataSetChanged();
                            }
                            break;
                        case MODIFIED:

                            break;
                        case REMOVED:
                            break;
                    }

                }

            }
        });
    }

    public void sendMessage(View view) {
    String messageText= mMessage.getText().toString();
    if(!messageText.isEmpty())
    {
        Map newMessage= new HashMap();
        newMessage.put("writer", currentUserID);
        newMessage.put("text", messageText);
        mDbChat.document().set(newMessage);
    }
    mMessage.setText(null);
    }



    public void goBack(View view) {
        finish();
        return;
    }
}