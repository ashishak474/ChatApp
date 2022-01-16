package com.example.chatme.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatme.ChatActivity;
import com.example.chatme.Model.ChatModel;
import com.example.chatme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private ArrayList<ChatModel> chat;
    private FirebaseUser firebaseUser;

    public ChatAdapter(Context context, ArrayList<ChatModel> chat) {
        this.context = context;
        this.chat = chat;
    }

    @NonNull
    @Override
    public ChatAdapter.ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.senderchatlayout, parent, false);
            return new ChatAdapter.ChatHolder(view);
        } else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recieverchatlayout, parent, false);
            return new ChatAdapter.ChatHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {

        ChatModel chatModel = chat.get(position);
        holder.show_msg.setText(chatModel.getMessage());
        Toast.makeText(context, "" + chatModel.isSeen(), Toast.LENGTH_SHORT).show();
        if (position == chat.size() - 1) {
            holder.seen.setVisibility(View.VISIBLE);
            if (chatModel.isSeen()) {

                holder.seen.setText("Seen");
            } else {
                holder.seen.setText("Delivered");
            }
        } else {
            holder.seen.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return chat.size();
    }

    public static class ChatHolder extends RecyclerView.ViewHolder {
        private TextView show_msg;
        private TextView seen;


        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            show_msg = itemView.findViewById(R.id.show_message);
            seen = itemView.findViewById(R.id.txt_seen);


        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chat.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }


}