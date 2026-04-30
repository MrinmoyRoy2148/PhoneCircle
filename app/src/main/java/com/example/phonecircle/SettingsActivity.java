package com.example.phonecircle;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private LinearLayout btnChangePassword;
    private MaterialSwitch switchDarkMode, switch2FA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnBack = findViewById(R.id.btn_back_settings);
        btnChangePassword = findViewById(R.id.btn_change_password);

        btnBack.setOnClickListener(v -> finish());

        btnChangePassword.setOnClickListener(v -> {
            Toast.makeText(this, "Change Password clicked", Toast.LENGTH_SHORT).show();
        });
    }
}