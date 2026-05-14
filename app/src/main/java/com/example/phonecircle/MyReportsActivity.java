package com.example.phonecircle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyReportsActivity extends AppCompatActivity {

    private RecyclerView rvReports;
    private View layoutEmptyState;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ReportsAdapter adapter;
    private List<Phone> reportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvReports = findViewById(R.id.rv_my_reports);
        layoutEmptyState = findViewById(R.id.layout_empty_reports);
        ImageView btnBack = findViewById(R.id.btn_back_reports);

        btnBack.setOnClickListener(v -> finish());

        rvReports.setLayoutManager(new LinearLayoutManager(this));
        reportList = new ArrayList<>();
        adapter = new ReportsAdapter(reportList);
        rvReports.setAdapter(adapter);

        loadReports();
    }

    private void loadReports() {
        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();
        db.collection("phones")
                .whereEqualTo("ownerId", userId)
                .whereIn("status", java.util.Arrays.asList("stolen", "lost"))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        reportList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Phone phone = document.toObject(Phone.class);
                            reportList.add(phone);
                        }

                        if (reportList.isEmpty()) {
                            layoutEmptyState.setVisibility(View.VISIBLE);
                            rvReports.setVisibility(View.GONE);
                        } else {
                            layoutEmptyState.setVisibility(View.GONE);
                            rvReports.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(this, getString(R.string.status_label, error), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void markAsSafe(Phone phone) {
        db.collection("phones").document(phone.getImei())
                .update("status", "safe")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, R.string.mark_as_safe, Toast.LENGTH_SHORT).show();
                    loadReports();
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportViewHolder> {
        private final List<Phone> reports;

        public ReportsAdapter(List<Phone> reports) {
            this.reports = reports;
        }

        @NonNull
        @Override
        public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phone, parent, false);
            return new ReportViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
            Phone phone = reports.get(position);
            holder.tvModel.setText(getString(R.string.device_label, phone.getBrand(), phone.getModel()));
            holder.tvImei.setText(getString(R.string.imei_item_label, phone.getImei()));
            
            String status = phone.getStatus();
            holder.tvStatus.setText(status.toUpperCase());
            holder.tvStatus.setBackgroundResource(R.drawable.status_stolen_bg);
            holder.tvStatus.setTextColor(ContextCompat.getColor(MyReportsActivity.this, R.color.status_stolen));
            
            holder.btnAction.setText(R.string.mark_as_safe);
            holder.btnAction.setOnClickListener(v -> {
                new AlertDialog.Builder(MyReportsActivity.this)
                        .setTitle(R.string.mark_as_safe)
                        .setMessage(R.string.safe_confirmation) // Changed to safe_confirmation or another appropriate string
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> markAsSafe(phone))
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return reports.size();
        }

        class ReportViewHolder extends RecyclerView.ViewHolder {
            TextView tvModel, tvImei, tvStatus;
            Button btnAction;

            public ReportViewHolder(@NonNull View itemView) {
                super(itemView);
                tvModel = itemView.findViewById(R.id.tv_item_brand_model);
                tvImei = itemView.findViewById(R.id.tv_item_imei);
                tvStatus = itemView.findViewById(R.id.tv_item_status);
                btnAction = itemView.findViewById(R.id.btn_report_action);
            }
        }
    }
}