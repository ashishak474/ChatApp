package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatme.Model.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class UserInfoActivity extends AppCompatActivity {
    private EditText name;
    private FirebaseDatabase firebaseDatabase;
    private Button save;
    private ImageView imageView;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String url;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private final int PICK_IMAGE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
        save = findViewById(R.id.saveProfile);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        name = findViewById(R.id.getusername);
        imageView = findViewById(R.id.userimage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                String uname = name.getText().toString();
                String userId = getIntent().getStringExtra("id");
                String userNumber = getIntent().getStringExtra("userNumber");

                if (imageUri != null) {
                    String imageid = UUID.randomUUID().toString();
                    StorageReference ref = storageReference.child("images/" + imageid);
                    ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                            while (!downloadUri.isSuccessful()) ;
                            url = downloadUri.getResult().toString();

                            UserModel user = new UserModel();
                            user.setUserId(userId);
                            user.setUserNumber(userNumber);
                            user.setUserName(uname);
                            user.setUserProfife(url);
                            DatabaseReference databaseReference = firebaseDatabase.getReference("UserDetails").child(userId);
                            databaseReference.setValue(user);

                            Intent i = new Intent(UserInfoActivity.this, UserActivity.class);
                            startActivity(i);
                            progressDialog.dismiss();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UserInfoActivity.this, "Somthing Went Wrong!!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }


            }
        });


    }


    private void selectImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image "), PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}






