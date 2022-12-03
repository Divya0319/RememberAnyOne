package com.fastturtle.rememberMe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fastturtle.rememberme.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText etPhone, etOTP;

    private Button btGetOtp, btVerifyOtp;

    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        mAuth = FirebaseAuth.getInstance();

        etPhone = findViewById(R.id.idEdtPhoneNumber);
        etOTP = findViewById(R.id.idEdtOtp);
        btGetOtp = findViewById(R.id.idBtnGetOtp);
        btVerifyOtp = findViewById(R.id.idBtnVerify);

        btGetOtp.setOnClickListener(view -> {
            if (TextUtils.isEmpty(etPhone.getText().toString())) {
                Toast.makeText(PhoneAuthActivity.this, "Please enter a valid phone number", Toast.LENGTH_LONG).show();
            } else {
                String phone = "+91" + etPhone.getText().toString();
                sendVerificationCode(phone);
            }
        });

        btVerifyOtp.setOnClickListener(view -> {
            if (TextUtils.isEmpty(etOTP.getText().toString())) {
                Toast.makeText(PhoneAuthActivity.this, "Please enter OTP", Toast.LENGTH_LONG).show();
            } else {
                verifyCode(etOTP.getText().toString());
            }
        });
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent addUserIntent = new Intent(PhoneAuthActivity.this, AddUserActivity.class);
                        startActivity(addUserIntent);
                        finish();
                    } else {
                        Toast.makeText(PhoneAuthActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void sendVerificationCode(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallBack)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);

                    verificationId = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    final String code = phoneAuthCredential.getSmsCode();

                    if(code != null) {
                        etOTP.setText(code);

                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {

                    Toast.makeText(PhoneAuthActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            };
}