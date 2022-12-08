package com.fastturtle.rememberMe.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.fastturtle.rememberMe.BuildConfig;
import com.fastturtle.rememberMe.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button btSendOtp;
    private EditText etPhoneNo;
    private String number;
    private ProgressBar loadingProgressBar;
    private Button btVerifyOtp;
    private TextView tvResend;
    private EditText etInput1, etInput2, etInput3, etInput4, etInput5, etInput6;
    private String verifId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private GoogleSignInClient googleSignInClient;
    private SignInButton signInButton;

    ActivityResultLauncher<Intent> googleSignInForResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        init();
        addTextChangeListeners();
        resentOTPTVVisibility();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.FIREBASE_WEB_CLIENT_ID)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());

            if (signInAccountTask.isSuccessful()) {
                Toast.makeText(PhoneActivity.this, "Google Sign in successful", Toast.LENGTH_SHORT).show();

                GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult();

                if (googleSignInAccount != null) {
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

                    firebaseAuth.signInWithCredential(authCredential)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    loadingProgressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(PhoneActivity.this, ProfileActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                                    Toast.makeText(PhoneActivity.this, "Firebase authentication successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    loadingProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(PhoneActivity.this, "Authentication failed : " + task.getException(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

        googleBtnUi();

        btSendOtp.setOnClickListener(view -> {

            number = etPhoneNo.getText().toString().trim();
            if (!number.isEmpty()) {
                if (number.length() == 10) {
                    number = "+91" + number;
                    loadingProgressBar.setVisibility(View.VISIBLE);

                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(firebaseAuth)
                                    .setPhoneNumber(number)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(this)                 // Activity (for callback binding)
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                } else {
                    Toast.makeText(this, "Please enter correct number", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Please enter number", Toast.LENGTH_LONG).show();
            }

        });

        tvResend.setOnClickListener(view -> {
            if (number != null && !number.isEmpty()) {
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(firebaseAuth)
                                .setPhoneNumber(number)       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(this)                 // Activity (for callback binding)
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .setForceResendingToken(resendToken)
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            } else {
                Toast.makeText(this, "Please enter number", Toast.LENGTH_SHORT).show();
            }
        });

        btVerifyOtp.setOnClickListener(view -> {
            String typedOtp = etInput1.getText().toString().trim() +
                    etInput2.getText().toString().trim() +
                    etInput3.getText().toString().trim() +
                    etInput4.getText().toString().trim() +
                    etInput5.getText().toString().trim() +
                    etInput6.getText().toString().trim();

            if (!typedOtp.isEmpty()) {
                if (typedOtp.length() == 6) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifId, typedOtp);
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    signInWithPhoneAuthCredential(credential);
                } else {
                    Toast.makeText(this, "Please Enter correct OTP", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please Enter OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        btSendOtp = findViewById(R.id.sendOTPBtn);
        etPhoneNo = findViewById(R.id.phoneEditTextNumber);
        firebaseAuth = FirebaseAuth.getInstance();
        loadingProgressBar = findViewById(R.id.phoneProgressBar);
        loadingProgressBar.setVisibility(View.INVISIBLE);
        btVerifyOtp = findViewById(R.id.verifyOTPBtn);
        tvResend = findViewById(R.id.resendTextView);
        etInput1 = findViewById(R.id.otpEditText1);
        etInput2 = findViewById(R.id.otpEditText2);
        etInput3 = findViewById(R.id.otpEditText3);
        etInput4 = findViewById(R.id.otpEditText4);
        etInput5 = findViewById(R.id.otpEditText5);
        etInput6 = findViewById(R.id.otpEditText6);
        signInButton = findViewById(R.id.signInButton);

    }

    private void googleBtnUi() {

        signInButton.setOnClickListener(view -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInForResult.launch(signInIntent);
            loadingProgressBar.setVisibility(View.VISIBLE);
        });

        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
//                tv.setTextSize(14);
                tv.setText("Google");
//                tv.setTypeface(null, Typeface.NORMAL);
//                tv.setTextColor(ContextCompat.getColor(this, R.color.btn_color));
//                tv.setBackground(ContextCompat.getDrawable(this, R.mipmap.btn_google_signin_dark_disabled));
//                tv.setSingleLine(true);
//                tv.setPadding(pixelToDp(50), pixelToDp(15), pixelToDp(15), pixelToDp(15));

                return;
            }
        }

    }

    private int pixelToDp(int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, getResources().getDisplayMetrics());
    }

    private void resentOTPTVVisibility() {
        etInput1.setText("");
        etInput2.setText("");
        etInput3.setText("");
        etInput4.setText("");
        etInput5.setText("");
        etInput6.setText("");
        tvResend.setVisibility(View.INVISIBLE);
        tvResend.setEnabled(false);

        new Handler(Looper.myLooper()).postDelayed(() -> {

            tvResend.setVisibility(View.VISIBLE);
            tvResend.setEnabled(true);
        }, 60000);

    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            String code = credential.getSmsCode();
            if (code != null) {
                etInput1.setText(String.valueOf(code.charAt(0)));
                etInput2.setText(String.valueOf(code.charAt(1)));
                etInput3.setText(String.valueOf(code.charAt(2)));
                etInput4.setText(String.valueOf(code.charAt(3)));
                etInput5.setText(String.valueOf(code.charAt(4)));
                etInput6.setText(String.valueOf(code.charAt(5)));
                signInWithPhoneAuthCredential(credential);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.


            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG", "onVerificationFailed: " + e.getMessage());
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG", "onVerificationFailed: " + e.getMessage());
            }

            // Show a message and update the UI
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.

            // Save verification ID and resending token so we can use them later

            verifId = verificationId;
            resendToken = token;

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, DashBoardActivity.class));
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingProgressBar.setVisibility(View.INVISIBLE);
                            // Sign in success, update UI with the signed-in user's information
                            new Handler(Looper.myLooper()).postDelayed(() -> {
                                sendToMain();
                                Toast.makeText(PhoneActivity.this, "Authenticated successfully", Toast.LENGTH_SHORT).show();
                            }, 2000);

                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                loadingProgressBar.setVisibility(View.INVISIBLE);
                                Log.d("TAG", "onComplete: " + task.getException().getMessage());
                            }
                        }
                    }
                });
    }

    private void sendToMain() {
        startActivity(new Intent(this, DashBoardActivity.class));
        finish();
    }

    private void addTextChangeListeners() {
        etInput1.addTextChangedListener(new EditTextWatcher(etInput1, etInput2));
        etInput2.addTextChangedListener(new EditTextWatcher(etInput2, etInput3));
        etInput3.addTextChangedListener(new EditTextWatcher(etInput3, etInput4));
        etInput4.addTextChangedListener(new EditTextWatcher(etInput4, etInput5));
        etInput5.addTextChangedListener(new EditTextWatcher(etInput5, etInput6));
        etInput6.addTextChangedListener(new EditTextWatcher(etInput6, null));

        etInput2.setOnKeyListener(new GenericKeyEvent(etInput2, etInput1));
        etInput3.setOnKeyListener(new GenericKeyEvent(etInput3, etInput2));
        etInput4.setOnKeyListener(new GenericKeyEvent(etInput4, etInput3));
        etInput5.setOnKeyListener(new GenericKeyEvent(etInput5, etInput4));
        etInput6.setOnKeyListener(new GenericKeyEvent(etInput6, etInput5));

    }

    class EditTextWatcher implements TextWatcher {

        private EditText currentView;
        private EditText nextView;

        EditTextWatcher(EditText currentView, EditText nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();

            if (nextView != null && text.length() == 1) {
                nextView.requestFocus();
            }
            if (text.length() > 1) {
                currentView.setText(String.valueOf(text.charAt(text.length() - 1)));
                currentView.setSelection(1);
            }
        }
    }

    class GenericKeyEvent implements View.OnKeyListener {
        private EditText currentView;
        private EditText prevView;

        GenericKeyEvent(EditText currentView, EditText prevView) {
            this.currentView = currentView;
            this.prevView = prevView;
        }

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.getText().toString().isEmpty()) {
                if (prevView != null) {
                    prevView.requestFocus();
                }
                return true;
            }
            return false;
        }
    }

}