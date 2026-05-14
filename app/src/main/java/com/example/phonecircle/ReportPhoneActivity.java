package com.example.phonecircle;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ReportPhoneActivity extends AppCompatActivity {

    private RadioGroup rgStatus;
    private TextInputEditText etDesc, etCase, etLocation;
    private FirebaseFirestore db;
    private String imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_phone);

        db = FirebaseFirestore.getInstance();
        imei = getIntent().getStringExtra("imei");

        MaterialToolbar toolbar = findViewById(R.id.toolbar_report);
        toolbar.setNavigationOnClickListener(v -> finish());

        rgStatus = findViewById(R.id.rg_report_status);
        etDesc = findViewById(R.id.et_incident_desc);
        etCase = findViewById(R.id.et_case_number);
        etLocation = findViewById(R.id.et_last_location);
        Button btnSubmit = findViewById(R.id.btn_submit_report);

        btnSubmit.setOnClickListener(v -> {
            if (rgStatus.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please select a status", Toast.LENGTH_SHORT).show();
                return;
            }
            showConfirmationDialog();
        });
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Report")
                .setMessage("Are you sure you want to report this phone?")
                .setPositiveButton("Submit", (dialog, which) -> submitReport())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void submitReport() {
        if (imei == null) {
            Toast.makeText(this, "Error: IMEI not found", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = rgStatus.getCheckedRadioButtonId();
        String status = (selectedId == R.id.rb_stolen) ? "stolen" : "lost";
        String desc = etDesc.getText() != null ? etDesc.getText().toString().trim() : "";
        String caseNum = etCase.getText() != null ? etCase.getText().toString().trim() : "";
        String loc = etLocation.getText() != null ? etLocation.getText().toString().trim() : "";

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("incidentDescription", desc);
        updates.put("caseNumber", caseNum);
        updates.put("lastLocation", loc);
        updates.put("timestamp", System.currentTimeMillis());

        db.collection("phones").document(imei).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Report submitted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
