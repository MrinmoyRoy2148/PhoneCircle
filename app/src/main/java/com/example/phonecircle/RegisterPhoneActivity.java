package com.example.phonecircle;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterPhoneActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Spinner spinnerBrand;
    private TextInputEditText etModel, etImei, etColor;
    private Button btnSubmit, btnCancel;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnBack = findViewById(R.id.btn_back_register);
        spinnerBrand = findViewById(R.id.spinner_brand);
        etModel = findViewById(R.id.et_phone_model);
        etImei = findViewById(R.id.et_imei);
        etColor = findViewById(R.id.et_phone_color);
        btnSubmit = findViewById(R.id.btn_register_phone_submit);
        btnCancel = findViewById(R.id.btn_cancel_register);

        String[] brands = {"Select brand", "Samsung", "Apple", "Google", "Xiaomi", "OnePlus", "Oppo", "Vivo", "Realme", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, brands);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrand.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> registerPhone());
    }

    private void registerPhone() {
        String brand = spinnerBrand.getSelectedItem().toString();
        String model = etModel.getText() != null ? etModel.getText().toString().trim() : "";
        String imei = etImei.getText() != null ? etImei.getText().toString().trim() : "";
        String color = etColor.getText() != null ? etColor.getText().toString().trim() : "";

        if (brand.equals("Select brand")) {
            Toast.makeText(this, "Please select a brand", Toast.LENGTH_SHORT).show();
            return;
        }
        if (model.isEmpty()) {
            etModel.setError("Model is required");
            return;
        }
        if (imei.length() != 15) {
            etImei.setError("Enter a valid 15-digit IMEI");
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Registering...");

        db.collection("users").document(mAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String ownerName = documentSnapshot.getString("name");
                    savePhoneToFirestore(ownerName, brand, model, imei, color);
                })
                .addOnFailureListener(e -> {
                    savePhoneToFirestore("Unknown Owner", brand, model, imei, color);
                });
    }

    private void savePhoneToFirestore(String ownerName, String brand, String model, String imei, String color) {
        Map<String, Object> phone = new HashMap<>();
        phone.put("ownerId", mAuth.getCurrentUser().getUid());
        phone.put("ownerName", ownerName);
        phone.put("brand", brand);
        phone.put("model", model);
        phone.put("imei", imei);
        phone.put("color", color);
        phone.put("status", "safe");
        phone.put("timestamp", System.currentTimeMillis());

        db.collection("phones").document(imei).set(phone)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Phone Registered Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Register Phone");
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
