package com.example.shopmate.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.shopmate.R;
import com.example.shopmate.ui.activities.LoginActivity;
import com.example.shopmate.util.AuthManager;
import com.example.shopmate.viewmodel.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class ProfileFragment extends Fragment {
    
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView addressTextView;
    private MaterialButton logoutButton;
    private MaterialButton orderHistoryButton;
    private MaterialCardView loadingCard;
    private AuthManager authManager;
    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        initViews(view);
        initViewModel();
        setupObservers();
        setupClickListeners();
        loadUserData();
        
        return view;
    }

    private void initViews(View view) {
        usernameTextView = view.findViewById(R.id.usernameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        addressTextView = view.findViewById(R.id.addressTextView);
        logoutButton = view.findViewById(R.id.logoutButton);
        orderHistoryButton = view.findViewById(R.id.orderHistoryButton);
        loadingCard = view.findViewById(R.id.loadingCard);
        
        authManager = AuthManager.getInstance(requireContext());
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void setupObservers() {
        authViewModel.getLogoutResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                // AuthRepository already cleared SharedPreferences, just redirect to login
                authManager.logout();
                showSuccessMessage("Logged out successfully");
                redirectToLogin();
            }
        });
        
        authViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                loadingCard.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                logoutButton.setEnabled(!isLoading);
                logoutButton.setText(isLoading ? "Logging out..." : "Logout");
            }
        });
        
        authViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                showErrorMessage(getCustomErrorMessage(errorMsg));
            }
        });
    }

    private void setupClickListeners() {
        logoutButton.setOnClickListener(v -> {
            // Call local logout method (no API call)
            authViewModel.logout();
        });

        orderHistoryButton.setOnClickListener(v -> {
            // Navigate to Order History
            navigateToOrderHistory();
        });
    }

    private void navigateToOrderHistory() {
        OrderHistoryFragment orderHistoryFragment = new OrderHistoryFragment();
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, orderHistoryFragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadUserData() {
        // Load user data from AuthManager
        String username = authManager.getUsername();
        String email = authManager.getEmail();
        String phone = authManager.getPhoneNumber();
        String address = authManager.getAddress();
        
        // Set user data to views
        usernameTextView.setText(username != null ? username : "N/A");
        emailTextView.setText(email != null ? email : "N/A");
        phoneTextView.setText(phone != null ? phone : "N/A");
        addressTextView.setText(address != null ? address : "N/A");
    }

    private void redirectToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void showSuccessMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showErrorMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private String getCustomErrorMessage(String serverMessage) {
        // If server provides a message, use it; otherwise use custom message
        if (serverMessage != null && !serverMessage.trim().isEmpty()) {
            // Check for common error patterns and provide user-friendly messages
            if (serverMessage.toLowerCase().contains("network") ||
                serverMessage.toLowerCase().contains("connection")) {
                return "Network error. Please check your connection.";
            } else if (serverMessage.toLowerCase().contains("timeout")) {
                return "Request timeout. Please try again.";
            } else if (serverMessage.toLowerCase().contains("unauthorized") ||
                      serverMessage.toLowerCase().contains("token")) {
                return "Session expired. Please login again.";
            } else {
                return serverMessage;
            }
        } else {
            // Default custom message when server doesn't provide one
            return "Logout failed. Please try again.";
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh user data when fragment resumes
        loadUserData();
    }
} 