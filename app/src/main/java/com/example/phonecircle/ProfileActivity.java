package com.example.phonecircle;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextInputEditText etName, etEmail, etPhone;
    private Button btnProfileAction;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean isEditMode = false;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        btnBack = findViewById(R.id.btn_back);
        etName = findViewById(R.id.et_profile_name);
        etEmail = findViewById(R.id.et_profile_email);
        etPhone = findViewById(R.id.et_profile_phone);
        btnProfileAction = findViewById(R.id.btn_profile_action);

        btnBack.setOnClickListener(v -> finish());

        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            etEmail.setText(currentUser.getEmail());
            fetchUserProfile();
        }

        btnProfileAction.setOnClickListener(v -> {
            if (isEditMode) {
                saveProfile();
            } else {
                enableEditMode(true);
            }
        });
    }

    private void fetchUserProfile() {
        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        etName.setText(documentSnapshot.getString("name"));
                        etPhone.setText(documentSnapshot.getString("phone"));
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show());
    }

    private void enableEditMode(boolean enable) {
        isEditMode = enable;
        etName.setEnabled(enable);
        etPhone.setEnabled(enable);
        // Email remains disabled as it's the primary login identifier
        btnProfileAction.setText(enable ? "Save Changes" : "Edit Profile");
        
        if (enable) {
            etName.requestFocus();
        }
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("name", name);
        userUpdates.put("phone", phone);

        btnProfileAction.setEnabled(false);
        db.collection("users").document(currentUserId)
                .update(userUpdates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    enableEditMode(false);
                    btnProfileAction.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnProfileAction.setEnabled(true);
                });
    }
}