package com.mpdam.threefactorsauthentication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;

public class MainActivity extends AppCompatActivity {

    private Button btnPassword;
    private Button btnCode;
    private EditText etEmail;
    private EditText etPwd;
    private EditText etCode;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private FirebaseAuth mAuth;

    String sentCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        whiteNotificationBar();

        mAuth = FirebaseAuth.getInstance();

        btnPassword = findViewById(R.id.btn_password);

        etEmail = findViewById(R.id.et_email);
        etPwd = findViewById(R.id.et_pwd);


        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    private void signIn(){
        String mail = etEmail.getText().toString();
        String pwd = etPwd.getText().toString();
        mAuth.signInWithEmailAndPassword(mail, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Success.",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, PhoneAuthActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
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
