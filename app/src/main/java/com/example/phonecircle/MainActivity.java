package com.example.phonecircle;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TextView navUserName, navUserEmail, tvStatReg, tvStatRep;
    private MaterialCardView cardEmptyState;
    private RecyclerView rvPhones;
    private PhoneAdapter adapter;
    private List<Phone> phoneList;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration profileListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // UI Components
        drawerLayout = findViewById(R.id.drawer_layout);
        navUserName = findViewById(R.id.nav_user_name);
        navUserEmail = findViewById(R.id.nav_user_email);
        tvStatReg = findViewById(R.id.tv_stat_registered);
        tvStatRep = findViewById(R.id.tv_stat_reports);
        cardEmptyState = findViewById(R.id.card_empty_state);
        rvPhones = findViewById(R.id.rv_phones);
        
        rvPhones.setLayoutManager(new LinearLayoutManager(this));
        phoneList = new ArrayList<>();
        adapter = new PhoneAdapter(phoneList);
        rvPhones.setAdapter(adapter);

        // Header/Drawer Click Listeners
        findViewById(R.id.card_menu).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        findViewById(R.id.btn_close_drawer).setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));
        
        findViewById(R.id.btn_view_profile).setOnClickListener(v -> navigateTo(ProfileActivity.class));
        findViewById(R.id.nav_settings).setOnClickListener(v -> navigateTo(SettingsActivity.class));
        findViewById(R.id.nav_registered_phones).setOnClickListener(v -> navigateTo(RegisteredPhonesActivity.class));
        findViewById(R.id.nav_my_reports).setOnClickListener(v -> navigateTo(MyReportsActivity.class));
        
        findViewById(R.id.btn_logout_drawer).setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        });

        // Dashboard Buttons
        findViewById(R.id.card_register).setOnClickListener(v -> startActivity(new Intent(this, RegisterPhoneActivity.class)));
        findViewById(R.id.btn_register_first).setOnClickListener(v -> startActivity(new Intent(this, RegisterPhoneActivity.class)));
        findViewById(R.id.card_verify).setOnClickListener(v -> startActivity(new Intent(this, VerifyImeiActivity.class)));

        if (currentUser != null) {
            navUserEmail.setText(currentUser.getEmail());
            setupProfileListener(currentUser.getUid());
        }
    }

    private void navigateTo(Class<?> activityClass) {
        drawerLayout.closeDrawer(GravityCompat.START);
        startActivity(new Intent(this, activityClass));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() != null) fetchUserPhones(mAuth.getCurrentUser().getUid());
    }

    private void setupProfileListener(String userId) {
        profileListener = db.collection("users").document(userId).addSnapshotListener((snapshot, e) -> {
            if (snapshot != null && snapshot.exists() && navUserName != null) {
                navUserName.setText(snapshot.getString("name"));
            }
        });
    }

    private void fetchUserPhones(String userId) {
        db.collection("phones").whereEqualTo("ownerId", userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                phoneList.clear();
                int reports = 0;
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Phone p = doc.toObject(Phone.class);
                    phoneList.add(p);
                    if (!"safe".equals(p.getStatus())) reports++;
                }
                if (tvStatReg != null) tvStatReg.setText(String.valueOf(phoneList.size()));
                if (tvStatRep != null) tvStatRep.setText(String.valueOf(reports));
                
                boolean empty = phoneList.isEmpty();
                cardEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
                rvPhones.setVisibility(empty ? View.GONE : View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.ViewHolder> {
        private List<Phone> phones;
        public PhoneAdapter(List<Phone> phones) { this.phones = phones; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_phone, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            Phone p = phones.get(pos);
            h.tvModel.setText(p.getBrand() + " " + p.getModel());
            h.tvImei.setText("IMEI: " + p.getImei());
            
            String status = p.getStatus() != null ? p.getStatus() : "safe";
            h.tvStatus.setText(status.toUpperCase());
            boolean safe = "safe".equals(status);
            h.tvStatus.setTextColor(getResources().getColor(safe ? R.color.stat_reg_text : R.color.status_stolen));
            
            h.btnAction.setText(safe ? "Report Status" : "Mark as Safe");
            h.btnAction.setOnClickListener(v -> {
                if (safe) {
                    startActivity(new Intent(MainActivity.this, ReportPhoneActivity.class).putExtra("imei", p.getImei()));
                } else {
                    startActivity(new Intent(MainActivity.this, PhoneDetailsActivity.class).putExtra("imei", p.getImei()));
                }
            });
            h.itemView.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, PhoneDetailsActivity.class).putExtra("imei", p.getImei())));
        }

        @Override
        public int getItemCount() { return phones.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvModel, tvImei, tvStatus;
            Button btnAction;
            public ViewHolder(View iv) {
                super(iv);
                tvModel = iv.findViewById(R.id.tv_item_brand_model);
                tvImei = iv.findViewById(R.id.tv_item_imei);
                tvStatus = iv.findViewById(R.id.tv_item_status);
                btnAction = iv.findViewById(R.id.btn_report_action);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (profileListener != null) profileListener.remove();
    }
}
