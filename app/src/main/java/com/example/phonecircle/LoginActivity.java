package com.example.phonecircle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink, tvForgotPassword;
    private CheckBox cbRememberMe;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_REMEMBER = "rememberMe";
    private static final String KEY_EMAIL = "savedEmail";
    private static final String KEY_PASSWORD = "savedPassword";

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // If user is already logged in via Firebase Auth, skip login
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegisterLink = findViewById(R.id.tv_register_link);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        cbRememberMe = findViewById(R.id.cb_remember_me);

        // Load saved credentials if "Remember me" was checked
        if (sharedPreferences.getBoolean(KEY_REMEMBER, false)) {
            etEmail.setText(sharedPreferences.getString(KEY_EMAIL, ""));
            etPassword.setText(sharedPreferences.getString(KEY_PASSWORD, ""));
            cbRememberMe.setChecked(true);
        }

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void loginUser() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }

        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        handleRememberMe(email, password);
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Authentication Failed";
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        btnLogin.setEnabled(true);
                    }
                });
    }

    private void handleRememberMe(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (cbRememberMe.isChecked()) {
            editor.putBoolean(KEY_REMEMBER, true);
            editor.putString(KEY_EMAIL, email);
            editor.putString(KEY_PASSWORD, password);
        } else {
            editor.clear();
        }
        editor.apply();
    }

    private void showForgotPasswordDialog() {
        TextInputEditText resetEmail = new TextInputEditText(this);
        resetEmail.setHint("Enter your registered email");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Reset Password?")
                .setMessage("We will send a reset link to your email.")
                .setView(resetEmail)
                .setPositiveButton("Send", (dialogInterface, i) -> {
                    String email = resetEmail.getText() != null ? resetEmail.getText().toString().trim() : "";
                    if (!TextUtils.isEmpty(email)) {
                        mAuth.sendPasswordResetEmail(email)
                                .addOnSuccessListener(unused -> Toast.makeText(LoginActivity.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(LoginActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
}