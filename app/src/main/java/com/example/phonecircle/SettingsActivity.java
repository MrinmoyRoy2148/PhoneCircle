package com.example.phonecircle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextView tvThemeStatus;
    private MaterialSwitch switchDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);

        ImageView btnBack = findViewById(R.id.btn_back_settings);
        LinearLayout btnChangePassword = findViewById(R.id.btn_change_password);
        LinearLayout btnThemeToggle = findViewById(R.id.btn_theme_toggle_layout);
        switchDarkMode = findViewById(R.id.switch_dark_mode);
        tvThemeStatus = findViewById(R.id.tv_theme_status);

        btnBack.setOnClickListener(v -> finish());

        btnChangePassword.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.change_password) + " coming soon", Toast.LENGTH_SHORT).show();
        });

        // Initialize theme switch state
        boolean isDarkMode = sharedPreferences.getBoolean("DarkMode", false);
        switchDarkMode.setChecked(isDarkMode);
        updateThemeStatusText(isDarkMode);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked != sharedPreferences.getBoolean("DarkMode", false)) {
                applyTheme(isChecked);
            }
        });

        btnThemeToggle.setOnClickListener(v -> switchDarkMode.setChecked(!switchDarkMode.isChecked()));
    }

    private void applyTheme(boolean isChecked) {
        sharedPreferences.edit().putBoolean("DarkMode", isChecked).apply();
        AppCompatDelegate.setDefaultNightMode(isChecked ? 
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        updateThemeStatusText(isChecked);
    }

    private void updateThemeStatusText(boolean isDarkMode) {
        tvThemeStatus.setText(isDarkMode ? getString(R.string.theme_on) : getString(R.string.theme_off));
    }
}