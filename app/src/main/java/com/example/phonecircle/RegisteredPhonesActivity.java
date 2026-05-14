package com.example.phonecircle;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RegisteredPhonesActivity extends AppCompatActivity {

    private RecyclerView rvPhones;
    private View layoutEmptyState;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private PhoneAdapter adapter;
    private List<Phone> phoneList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_phones);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvPhones = findViewById(R.id.rv_registered_phones);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        ImageView btnBack = findViewById(R.id.btn_back_phones);

        btnBack.setOnClickListener(v -> finish());
        findViewById(R.id.btn_register_first).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterPhoneActivity.class));
        });

        rvPhones.setLayoutManager(new LinearLayoutManager(this));
        phoneList = new ArrayList<>();
        adapter = new PhoneAdapter(phoneList);
        rvPhones.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPhones();
    }

    private void loadPhones() {
        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();
        db.collection("phones")
                .whereEqualTo("ownerId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        phoneList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Phone phone = document.toObject(Phone.class);
                            phoneList.add(phone);
                        }
                        
                        if (phoneList.isEmpty()) {
                            layoutEmptyState.setVisibility(View.VISIBLE);
                            rvPhones.setVisibility(View.GONE);
                        } else {
                            layoutEmptyState.setVisibility(View.GONE);
                            rvPhones.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.PhoneViewHolder> {
        private List<Phone> phones;

        public PhoneAdapter(List<Phone> phones) {
            this.phones = phones;
        }

        @NonNull
        @Override
        public PhoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phone, parent, false);
            return new PhoneViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhoneViewHolder holder, int position) {
            Phone phone = phones.get(position);
            holder.tvModel.setText(phone.getBrand() + " " + phone.getModel());
            holder.tvImei.setText("IMEI: " + phone.getImei());
            
            String status = phone.getStatus();
            holder.tvStatus.setText(status != null ? status.toUpperCase() : "SAFE");
            
            boolean isSafe = status == null || "safe".equalsIgnoreCase(status);
            holder.tvStatus.setBackgroundResource(isSafe ? R.drawable.tag_safe_bg : R.drawable.status_stolen_bg);
            holder.tvStatus.setTextColor(getResources().getColor(isSafe ? R.color.stat_reg_text : R.color.status_stolen));
            
            holder.btnReport.setText("Manage Status");
            holder.btnReport.setOnClickListener(v -> {
                Intent intent = new Intent(RegisteredPhonesActivity.this, PhoneDetailsActivity.class);
                intent.putExtra("imei", phone.getImei());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return phones.size();
        }

        class PhoneViewHolder extends RecyclerView.ViewHolder {
            TextView tvModel, tvImei, tvStatus;
            Button btnReport;

            public PhoneViewHolder(@NonNull View itemView) {
                super(itemView);
                tvModel = itemView.findViewById(R.id.tv_item_brand_model);
                tvImei = itemView.findViewById(R.id.tv_item_imei);
                tvStatus = itemView.findViewById(R.id.tv_item_status);
                btnReport = itemView.findViewById(R.id.btn_report_action);
            }
        }
    }
}