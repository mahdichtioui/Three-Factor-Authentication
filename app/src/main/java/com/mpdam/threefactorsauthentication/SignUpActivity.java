package com.mpdam.threefactorsauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private Button btnPassword;
    private Button btnCode;
    private EditText etEmail;
    private EditText etPwd;
    private EditText etPwdConfirm;
    private EditText etCode;
    private TextView tvSignIn;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        whiteNotificationBar();

        mAuth = FirebaseAuth.getInstance();

        btnPassword = findViewById(R.id.btn_password);

        etEmail = findViewById(R.id.et_email);
        etPwd = findViewById(R.id.et_pwd);
        etPwdConfirm = findViewById(R.id.et_pwd_confirm);

        tvSignIn = findViewById(R.id.tv_sign_in);

        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpActivity.this.startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            }
        });

    }

    private void signUp(){
        String mail = etEmail.getText().toString();
        String pwd = etPwd.getText().toString();
        String pwdConfirm = etPwdConfirm.getText().toString();

        mAuth.createUserWithEmailAndPassword(mail, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Success.",
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(SignUpActivity.this, PhoneAuthActivity.class));
                        } else {
                            Toast.makeText(SignUpActivity.this, "Authentication failed: " + task.getException().getMessage(),
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