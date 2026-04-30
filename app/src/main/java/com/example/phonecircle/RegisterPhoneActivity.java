package com.example.phonecircle;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class RegisterPhoneActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Spinner spinnerBrand;
    private TextInputEditText etModel, etImei, etColor;
    private Button btnSubmit, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);

        btnBack = findViewById(R.id.btn_back_register);
        spinnerBrand = findViewById(R.id.spinner_brand);
        etModel = findViewById(R.id.et_phone_model);
        etImei = findViewById(R.id.et_imei);
        etColor = findViewById(R.id.et_phone_color);
        btnSubmit = findViewById(R.id.btn_register_phone_submit);
        btnCancel = findViewById(R.id.btn_cancel_register);

        // Setup Spinner
        String[] brands = {"Select brand", "Samsung", "Apple", "Google", "Xiaomi", "OnePlus", "Oppo", "Vivo", "Realme", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, brands);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrand.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> {
            String brand = spinnerBrand.getSelectedItem().toString();
            String model = etModel.getText().toString().trim();
            String imei = etImei.getText().toString().trim();

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

            // Implementation for saving to database would go here
            Toast.makeText(this, "Phone Registered Successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}