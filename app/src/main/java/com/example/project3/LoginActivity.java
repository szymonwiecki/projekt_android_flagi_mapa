// LoginActivity.java
package com.example.project3;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button registerBtn, loginBtn;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        registerBtn = findViewById(R.id.register_button);
        loginBtn = findViewById(R.id.login_button);

        registerBtn.setOnClickListener(v -> register());
        loginBtn.setOnClickListener(v -> login());
    }

    private void register() {
        String mail = email.getText().toString();
        String pass = password.getText().toString();
        auth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Zarejestrowano", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Błąd: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void login() {
        String mail = email.getText().toString();
        String pass = password.getText().toString();
        auth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Logowanie nieudane: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
