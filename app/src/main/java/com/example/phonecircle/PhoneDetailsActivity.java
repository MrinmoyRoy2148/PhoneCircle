package com.example.phonecircle;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PhoneDetailsActivity extends AppCompatActivity {

    private TextView tvBrandModel, tvImei;
    private RadioGroup rgStatus;
    private RadioButton rbSafe, rbLost, rbStolen;
    private TextInputEditText etContactInfo;
    private Button btnUpdate;
    private FirebaseFirestore db;
    private String imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_details);

        db = FirebaseFirestore.getInstance();
        imei = getIntent().getStringExtra("imei");

        tvBrandModel = findViewById(R.id.tv_details_brand_model);
        tvImei = findViewById(R.id.tv_details_imei);
        rgStatus = findViewById(R.id.rg_status);
        rbSafe = findViewById(R.id.rb_safe);
        rbLost = findViewById(R.id.rb_lost);
        rbStolen = findViewById(R.id.rb_stolen);
        etContactInfo = findViewById(R.id.et_contact_info);
        btnUpdate = findViewById(R.id.btn_update_status);
        ImageView btnBack = findViewById(R.id.btn_back_details);

        btnBack.setOnClickListener(v -> finish());

        if (imei != null) {
            loadPhoneDetails();
        }

        btnUpdate.setOnClickListener(v -> updateStatus());
    }

    private void loadPhoneDetails() {
        db.collection("phones").document(imei).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Phone phone = documentSnapshot.toObject(Phone.class);
                        if (phone != null) {
                            tvBrandModel.setText(phone.getBrand() + " " + phone.getModel());
                            tvImei.setText("IMEI: " + phone.getImei());
                            etContactInfo.setText(phone.getContactInfo());

                            String status = phone.getStatus();
                            if ("lost".equals(status)) rbLost.setChecked(true);
                            else if ("stolen".equals(status)) rbStolen.setChecked(true);
                            else rbSafe.setChecked(true);
                        }
                    }
                });
    }

    private void updateStatus() {
        String status = "safe";
        int checkedId = rgStatus.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_lost) status = "lost";
        else if (checkedId == R.id.rb_stolen) status = "stolen";

        String contact = etContactInfo.getText().toString().trim();

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("contactInfo", contact);

        btnUpdate.setEnabled(false);
        db.collection("phones").document(imei).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Status Updated Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnUpdate.setEnabled(true);
                    Toast.makeText(this, "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}