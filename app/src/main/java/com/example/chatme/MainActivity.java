package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MAINACTIVITY";

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;
    private String usernumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");


        firebaseDatabase = FirebaseDatabase.getInstance();

        mAuth = FirebaseAuth.getInstance();


        EditText et_phnumber = findViewById(R.id.et_phnumber);
        EditText et_code = findViewById(R.id.et_code);
        Button btn_send = findViewById(R.id.btn_send);
        //   btn_send.setBackgroundResource(R.drawable.bg_btn);


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (btn_send.getText().toString().equalsIgnoreCase("send otp")) {

                    String number = et_phnumber.getText().toString();
                    if (number.length() == 10) {
                        progressDialog.show();
                        sendVerificationCode("+91" + number);
                        usernumber = et_phnumber.getText().toString();

                    } else {
                        et_phnumber.setError("Please Enter Valid Number");


                    }
                }
            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.e(TAG, "onVerificationCompleted");
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e(TAG, "onVerificationFailed");

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                Log.e(TAG, "onCodeSent");
                progressDialog.dismiss();
                mVerificationId = s;
                Intent i = new Intent(MainActivity.this, OtpActivity.class);
                i.putExtra("number", usernumber);
                i.putExtra("mVerificationId", mVerificationId);
                startActivity(i);

            }
        };
    }

//    private void verifyOtp(String code) {
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
//        signInWithPhoneAuthCredential(credential);
//    }


    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}