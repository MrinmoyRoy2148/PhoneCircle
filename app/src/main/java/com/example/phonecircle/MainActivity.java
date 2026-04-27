package com.example.phonecircle;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private TextInputEditText etSearchImei;
    private Button btnVerify;
    private MaterialCardView cardRegister, cardReport, cardMyDevices, cardCommunity;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize UI
        tvWelcome = findViewById(R.id.tv_welcome);
        etSearchImei = findViewById(R.id.et_search_imei);
        btnVerify = findViewById(R.id.btn_verify);
        cardRegister = findViewById(R.id.card_register);
        cardReport = findViewById(R.id.card_report);
        cardMyDevices = findViewById(R.id.card_my_devices);
        cardCommunity = findViewById(R.id.card_community);

        // Fetch User Data from Firestore
        if (currentUser != null) {
            fetchUserProfile(currentUser.getUid());
        }

        btnVerify.setOnClickListener(v -> verifyImei());
        
        // Navigation setup (Placeholder toasts)
        cardRegister.setOnClickListener(v -> Toast.makeText(this, "Registering Phone...", Toast.LENGTH_SHORT).show());
        cardReport.setOnClickListener(v -> Toast.makeText(this, "Reporting Stolen...", Toast.LENGTH_SHORT).show());
        cardMyDevices.setOnClickListener(v -> Toast.makeText(this, "Opening My Devices...", Toast.LENGTH_SHORT).show());
        cardCommunity.setOnClickListener(v -> Toast.makeText(this, "Opening Community...", Toast.LENGTH_SHORT).show());
    }

    private void fetchUserProfile(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        tvWelcome.setText("Welcome back, " + name + "!");
                    }
                })
                .addOnFailureListener(e -> tvWelcome.setText("Welcome to PhoneCircle!"));
    }

    private void verifyImei() {
        String imei = etSearchImei.getText().toString().trim();
        if (TextUtils.isEmpty(imei) || imei.length() != 15) {
            etSearchImei.setError("Please enter a valid 15-digit IMEI");
            return;
        }
        Toast.makeText(this, "Searching for: " + imei, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}