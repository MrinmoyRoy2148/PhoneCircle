package com.example.phonecircle;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private ImageView btnBack, btnEditProfile;
    private TextView tvName, tvEmail, tvPhone;
    private Button btnEditSave;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        btnBack = findViewById(R.id.btn_back);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        tvName = findViewById(R.id.tv_profile_name);
        tvEmail = findViewById(R.id.tv_profile_email);
        tvPhone = findViewById(R.id.tv_profile_phone);
        btnEditSave = findViewById(R.id.btn_edit_save);

        btnBack.setOnClickListener(v -> finish());

        if (currentUser != null) {
            tvEmail.setText(currentUser.getEmail());
            fetchUserProfile(currentUser.getUid());
        }

        btnEditProfile.setOnClickListener(v -> toggleEditMode());
        btnEditSave.setOnClickListener(v -> {
            if (isEditMode) {
                saveProfile();
            } else {
                toggleEditMode();
            }
        });
    }

    private void fetchUserProfile(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        tvName.setText(documentSnapshot.getString("name"));
                        tvPhone.setText(documentSnapshot.getString("phone"));
                    }
                });
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;
        if (isEditMode) {
            btnEditSave.setText("Save Changes");
            Toast.makeText(this, "Edit Mode Enabled", Toast.LENGTH_SHORT).show();
            // In a real app, you'd switch TextViews to EditTexts or enable editing
        } else {
            btnEditSave.setText("Edit Profile");
        }
    }

    private void saveProfile() {
        Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
        toggleEditMode();
    }
}