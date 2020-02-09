package com.mpdam.threefactorsauthentication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

public class PhoneAuthActivity extends AppCompatActivity {

    private Button btnConfirm;
    private Button btnSend;
    private EditText etPhone;
    private EditText etCode;
    private LinearLayout layoutPhone;
    private LinearLayout layoutCode;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private FirebaseAuth mAuth;

    String sentCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        whiteNotificationBar();

        mAuth = FirebaseAuth.getInstance();

        btnSend = findViewById(R.id.btn_send);
        btnConfirm = findViewById(R.id.btn_code);
        etPhone = findViewById(R.id.et_phone);
        etCode = findViewById(R.id.et_code);
        layoutCode = findViewById(R.id.layout_verif_code);
        layoutPhone = findViewById(R.id.layout_phone_number);

        layoutCode.setVisibility(View.GONE);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPhoneCodeVerification(etPhone);
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPhoneCode(etCode.getText().toString());
            }
        });

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(PhoneAuthActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(new Intent(PhoneAuthActivity.this, HomeActivity.class));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build();

    }

    private void checkDeviceCompatibility() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                //Toast.makeText(this, "App can authenticate using biometrics.", Toast.LENGTH_SHORT).show();
                biometricPrompt.authenticate(promptInfo);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "No biometric features available on this device.", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Biometric features are currently unavailable.", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "The user hasn't associated " +
                        "any biometric credentials with their account.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void sendPhoneCodeVerification(EditText etTel) {

        String phoneNumber = etTel.getText().toString();

        if(phoneNumber.isEmpty()){
            etTel.setError("Numero Telephone est Obligatoire");
            etTel.requestFocus();
            return;
        }else if (phoneNumber.length()!=8){
            etTel.setError("Numero Telephone Invalid");
            etTel.requestFocus();
            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+216" + phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(PhoneAuthActivity.this, "onVerificationCompleted", Toast.LENGTH_SHORT).show();
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            Toast.makeText(PhoneAuthActivity.this, code, Toast.LENGTH_SHORT).show();

            if (code != null) {
                etCode.setText(code);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneAuthActivity.this, "onVerificationFailed", Toast.LENGTH_SHORT).show();

        }



        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(PhoneAuthActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();
            sentCode = s;
            layoutPhone.setVisibility(View.GONE);
            layoutCode.setVisibility(View.VISIBLE);
        }
    };

    private void verifyPhoneCode(String verificationCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(sentCode, verificationCode);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PhoneAuthActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            checkDeviceCompatibility();

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(PhoneAuthActivity.this, "Wrong Verification", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void whiteNotificationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
