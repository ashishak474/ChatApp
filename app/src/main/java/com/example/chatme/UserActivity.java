package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chatme.Model.UserModel;
import com.example.chatme.adapter.Useradapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UserActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = findViewById(R.id.toolbarofchat);
        toolbar.setTitle("Chat Me");
        setSupportActionBar(toolbar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(UserActivity.this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();


        DatabaseReference databaseReference = firebaseDatabase.getReference("UserDetails");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<UserModel> list = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModel userDetail = dataSnapshot.getValue(UserModel.class);
                    if (!userDetail.getUserId().equals(FirebaseAuth.getInstance().getUid())) {
                        list.add(userDetail);
                    }

                }

                RecyclerView recyclerView = findViewById(R.id.user_rv);

                Useradapter useradapter = new Useradapter(UserActivity.this, list);
                recyclerView.setAdapter(useradapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(UserActivity.this, "Somthing Went Wrong!!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_menu:
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(UserActivity.this, MainActivity.class);
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


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
//      DatabaseReference  reference = FirebaseDatabase.getInstance().getReference("UserDetails").child(FirebaseAuth.getInstance().getUid());
//        reference.removeEventListener(seenListener);
        status("offline");
    }
}