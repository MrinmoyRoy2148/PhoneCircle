package com.example.phonecircle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TextView navUserName, navUserEmail;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Initialize UI
        drawerLayout = findViewById(R.id.drawer_layout);
        ImageView ivNotifications = findViewById(R.id.iv_notifications);
        ImageView ivMenu = findViewById(R.id.iv_menu);
        MaterialCardView cardRegister = findViewById(R.id.card_register);
        MaterialCardView cardVerify = findViewById(R.id.card_verify);
        Button btnAlertDetails = findViewById(R.id.btn_alert_details);

        // Drawer elements
        ImageView btnCloseDrawer = findViewById(R.id.btn_close_drawer);
        navUserName = findViewById(R.id.nav_user_name);
        navUserEmail = findViewById(R.id.nav_user_email);
        Button btnViewProfile = findViewById(R.id.btn_view_profile);
        Button btnLogoutDrawer = findViewById(R.id.btn_logout_drawer);
        LinearLayout navSettings = findViewById(R.id.nav_settings);

        // Fetch User Data for dynamic title and drawer
        if (currentUser != null) {
            navUserEmail.setText(currentUser.getEmail());
            fetchUserProfile(currentUser.getUid());
        }

        // Set Click Listeners
        ivNotifications.setOnClickListener(v -> Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show());
        
        // Burger Menu Click
        ivMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        
        // Drawer Click Listeners
        btnCloseDrawer.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));
        
        btnViewProfile.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        navSettings.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });

        btnLogoutDrawer.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Register Phone Click
        cardRegister.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterPhoneActivity.class));
        });

        cardVerify.setOnClickListener(v -> Toast.makeText(this, "Opening IMEI Verification...", Toast.LENGTH_SHORT).show());
        btnAlertDetails.setOnClickListener(v -> Toast.makeText(this, "Showing Alert Details...", Toast.LENGTH_SHORT).show());
    }

    private void fetchUserProfile(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        if (name != null && !name.isEmpty()) {
                            navUserName.setText(name);
                        }
                    }
                });
    }
}