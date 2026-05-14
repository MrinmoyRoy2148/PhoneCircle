package com.example.phonecircle;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class VerifyImeiActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextInputEditText etImei;
    private Button btnCheckStatus;
    private MaterialCardView cardResult;
    private ImageView ivStatusIcon;
    private TextView tvStatusText, tvStatusDetails;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_imei);

        db = FirebaseFirestore.getInstance();

        btnBack = findViewById(R.id.btn_back_verify);
        etImei = findViewById(R.id.et_verify_imei);
        btnCheckStatus = findViewById(R.id.btn_check_status);
        cardResult = findViewById(R.id.card_verify_result);
        ivStatusIcon = findViewById(R.id.iv_status_icon);
        tvStatusText = findViewById(R.id.tv_status_text);
        tvStatusDetails = findViewById(R.id.tv_status_details);

        btnBack.setOnClickListener(v -> finish());

        btnCheckStatus.setOnClickListener(v -> performVerification());
    }

    private void performVerification() {
        String imei = etImei.getText().toString().trim();

        if (TextUtils.isEmpty(imei) || imei.length() != 15) {
            etImei.setError("Please enter a valid 15-digit IMEI");
            return;
        }

        btnCheckStatus.setEnabled(false);
        btnCheckStatus.setText("Checking...");

        db.collection("phones").document(imei).get()
                .addOnSuccessListener(documentSnapshot -> {
                    btnCheckStatus.setEnabled(true);
                    btnCheckStatus.setText("Check Status");
                    cardResult.setVisibility(View.VISIBLE);

                    if (documentSnapshot.exists()) {
                        String status = documentSnapshot.getString("status");
                        if ("stolen".equalsIgnoreCase(status) || "lost".equalsIgnoreCase(status)) {
                            showReportedStatus(status.toUpperCase());
                        } else {
                            showSafeStatus();
                        }
                    } else {
                        showSafeStatus();
                    }
                })
                .addOnFailureListener(e -> {
                    btnCheckStatus.setEnabled(true);
                    btnCheckStatus.setText("Check Status");
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showSafeStatus() {
        ivStatusIcon.setImageResource(android.R.drawable.checkbox_on_background);
        ivStatusIcon.setColorFilter(getResources().getColor(R.color.stat_reg_text));
        tvStatusText.setText("Status: SAFE");
        tvStatusText.setTextColor(getResources().getColor(R.color.stat_reg_text));
        tvStatusDetails.setText("This device is not reported as stolen or lost in our database.");
    }

    private void showReportedStatus(String status) {
        ivStatusIcon.setImageResource(android.R.drawable.ic_dialog_alert);
        ivStatusIcon.setColorFilter(getResources().getColor(R.color.status_stolen));
        tvStatusText.setText("Status: " + status);
        tvStatusText.setTextColor(getResources().getColor(R.color.status_stolen));
        tvStatusDetails.setText("WARNING: This device has been reported as " + status.toLowerCase() + ".");
    }
}
