package com.example.chatme.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.chatme.ChatActivity;
import com.example.chatme.Model.ChatModel;
import com.example.chatme.R;
import com.example.chatme.Model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Useradapter extends RecyclerView.Adapter<Useradapter.UserHolder> {
    private Context context;
    String theLastMessage;
    private ArrayList<UserModel> list;

    public Useradapter(Context context, ArrayList<UserModel> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatviewlayout, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        UserModel user = list.get(position);
        holder.userName.setText(user.getUserName());
        Glide
                .with(context.getApplicationContext())
                .load(user.getUserProfife())
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
                .into(holder.profile);




            if (user.getStatus().equals("online")) {
                holder.img_on.setVisibility(View.VISIBLE);
            }
        lastMessage(user.getUserId(), holder.last_msg);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("UserModel",user);
                context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class UserHolder extends RecyclerView.ViewHolder{
        private TextView userName;
        private ImageView profile;
        private ImageView img_on;
        private TextView last_msg;


        public UserHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.username);
            img_on = itemView.findViewById(R.id.img_on);
            last_msg = itemView.findViewById(R.id.last_msg);
            profile=itemView.findViewById(R.id.profile_image);
        }
    }

    // Getting user's last message
    private void lastMessage(final String userid, final TextView last_msg){
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatModel chat = snapshot.getValue(ChatModel.class);
                    if (firebaseUser != null && chat != null) {
                        if (chat.getRecever().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getRecever().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                        }
                    }
                }

                switch (theLastMessage){
                    case  "default":
                        last_msg.setText("");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
