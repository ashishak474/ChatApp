package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.chatme.Model.ChatModel;
import com.example.chatme.Model.UserModel;
import com.example.chatme.adapter.ChatAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    private TextView userName;
    private ImageButton imageButton, sendMsg;
    private EditText msg;
    private UserModel userDetail;
    private ImageView imageView;
    private ChatAdapter chatAdapter;
    private DatabaseReference seenReference;
    private ArrayList<ChatModel> chat;
    private ValueEventListener seenListener;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        userName = findViewById(R.id.Nameofuser);
        imageButton = findViewById(R.id.backbuttonofchat);
        imageView = findViewById(R.id.user_img);
        recyclerView = findViewById(R.id.rv_chat);
        sendMsg = findViewById(R.id.imageviewsendmessage);
        userDetail = getIntent().getParcelableExtra("UserModel");
        msg = findViewById(R.id.getmessage);
        Glide
                .with(getApplicationContext())
                .load(userDetail.getUserProfife())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        return false;
                    }
                })
                .into(imageView);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = msg.getText().toString();
                String receiver = userDetail.getUserId();
                String sender = FirebaseAuth.getInstance().getUid();
                if (!message.isEmpty()) {
                    sendMessage(sender, receiver, message);
                    msg.setText("");

                } else {
                    Toast.makeText(ChatActivity.this, "Chat can't be empty", Toast.LENGTH_SHORT).show();
                }


            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        userName.setText(userDetail.getUserName());
        readMessage(FirebaseAuth.getInstance().getUid(), userDetail.getUserId());

        seenMessage(userDetail.getUserId());
    }


    //Sending Message to the firebase
    public void sendMessage(String sender, String recever, String msg) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("recever", recever);
        hashMap.put("message", msg);
        hashMap.put("isSeen", false);
        reference.child("Chats").push().setValue(hashMap);
    }

    //Read Message from Firebase
    public void readMessage(String myid, String userid) {
        chat = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chat.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                    if (chatModel.getRecever().equals(myid) && chatModel.getSender().equals(userid) ||
                            chatModel.getRecever().equals(userid) && chatModel.getSender().equals(myid)) {
                        chat.add(chatModel);

                    }

                    ChatAdapter chatAdapter = new ChatAdapter(ChatActivity.this, chat);
                    recyclerView.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    //Cheacking that user seen our message
    private void seenMessage(final String userId) {
        seenReference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = seenReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                    if (chatModel.getRecever().equals(FirebaseAuth.getInstance().getUid()) && chatModel.getSender().equals(userId)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Cheacking user is online or offline
    private void status(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserDetails").child(FirebaseAuth.getInstance().getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        seenReference.removeEventListener(seenListener);
        status("offline");
    }
}