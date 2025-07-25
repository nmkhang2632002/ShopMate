package com.example.shopmate.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.shopmate.R;
import com.example.shopmate.data.model.User;
import com.example.shopmate.ui.activities.LoginActivity;
import com.example.shopmate.util.AuthManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AdminProfileFragment extends Fragment {

    private AuthManager authManager;
    private User currentUser;

    private ImageView profileAvatar;
    private TextView adminName;
    private TextView adminEmail;
    private TextView adminPhone;
    private TextView adminAddress;
    private TextView adminId;
    private MaterialButton btnLogout; // Add logout button

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_profile, container, false);

        initViews(view);
        setupData();
        setupClickListeners();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authManager = AuthManager.getInstance(requireContext());
        currentUser = authManager.getCurrentUser();
    }

    private void initViews(View view) {
        profileAvatar = view.findViewById(R.id.profileAvatar);
        adminName = view.findViewById(R.id.adminName);
        adminEmail = view.findViewById(R.id.adminEmail);
        adminPhone = view.findViewById(R.id.adminPhone);
        adminAddress = view.findViewById(R.id.adminAddress);
        adminId = view.findViewById(R.id.adminId);
        btnLogout = view.findViewById(R.id.btnLogout); // Initialize logout button
    }

    private void setupData() {
        if (currentUser != null) {
            adminName.setText(currentUser.getUsername());
            adminEmail.setText(currentUser.getEmail());
            adminPhone.setText(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "Not provided");
            adminAddress.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "Not provided");
            adminId.setText("Admin ID: " + currentUser.getId());

            // Use user profile icon instead of admin avatar
            Glide.with(this)
                    .load(R.drawable.ic_profile) // Use user profile icon
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .circleCrop()
                    .into(profileAvatar);
        } else {
            adminName.setText("Admin User");
            adminEmail.setText("admin@shopmate.com");
            adminPhone.setText("Not provided");
            adminAddress.setText("Not provided");
            adminId.setText("Admin ID: 40");
        }
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> {
            // Handle logout
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        authManager.logout();
                        Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                        // Navigate to login screen
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}
