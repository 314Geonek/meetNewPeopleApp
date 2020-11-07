package com.golab.meetnewpeopleapp.chat;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.golab.meetnewpeopleapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
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
    private String currentUserID, userMatchId;
    private EditText mMessage;
    private CollectionReference mDbChat;
    private FirebaseFirestore db;
    private String matchId;
    private TextView mName;
    private ImageButton ibPicture;
    private NestedScrollView nestedScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mMessage= (EditText) findViewById(R.id.userMessage);
        db= FirebaseFirestore.getInstance();
        matchId = getIntent().getExtras().getString("chatKey");
        userMatchId = matchId = getIntent().getExtras().getString("secondUserId");
        mName = findViewById(R.id.tvMatchName);
        ibPicture=findViewById(R.id.ibMatchpic);
        nestedScrollView= findViewById(R.id.nestedScrollView);
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDbChat= FirebaseFirestore.getInstance().collection("Matches").document(matchId).collection("Messages");
        fillNavBar();
        getChatMessages();
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

    private void fillNavBar()
    {    db.collection("users").document(userMatchId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot document) {
            if(document.exists())
            mName.setText(document.get("name").toString());
            if(!document.get("profileImageUrl").toString().equals("default"))
            Glide.with(getApplication()).load(document.get("profileImageUrl").toString()).apply(RequestOptions.circleCropTransform()).into(ibPicture);
        }
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
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        String content = dc.getDocument().get("content") != null ? dc.getDocument().get("content").toString() : null;
                        String writerId = dc.getDocument().get("writerId") != null ? dc.getDocument().get("writerId").toString() : null;

                        if (content != null && writerId != null) {
                            Boolean currentUserBoolean = writerId.equals(currentUserID) ? true : false;
                            ChatObject newMessage = new ChatObject(content, currentUserBoolean);
                            resultsMessages.add(newMessage);
                            mChatAdapter.notifyDataSetChanged();
                            nestedScrollView.fullScroll(View.FOCUS_DOWN);
                        
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
        newMessage.put("writerId", currentUserID);
        newMessage.put("content", messageText);
        newMessage.put("writed", Timestamp.now().toDate());
        System.out.println(mDbChat.getPath());
        mDbChat.document().set(newMessage);
    }
    mMessage.setText(null);
    }



    public void goBack(View view) {
        finish();
        return;
    }
}