package com.example.shopmate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopmate.R;
import com.example.shopmate.data.model.ChatCustomer;
import com.example.shopmate.ui.activities.AdminActivity;
import com.example.shopmate.ui.adapters.ChatAdapter;
import com.example.shopmate.viewmodel.AdminChatViewModel;

import java.util.Timer;
import java.util.TimerTask;

public class AdminChatDetailFragment extends Fragment {

    private AdminChatViewModel viewModel;
    private ImageButton btnBack;
    private ImageView ivCustomerAvatar;
    private TextView tvCustomerName;
    private TextView tvCustomerEmail;
    private RecyclerView rvChatMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private ChatAdapter chatAdapter;
    private Timer autoRefreshTimer;
    private static final long AUTO_REFRESH_INTERVAL = 5000; // 5 seconds

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_chat_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        btnBack = view.findViewById(R.id.btnBack);
        ivCustomerAvatar = view.findViewById(R.id.ivCustomerAvatar);
        tvCustomerName = view.findViewById(R.id.tvCustomerName);
        tvCustomerEmail = view.findViewById(R.id.tvCustomerEmail);
        rvChatMessages = view.findViewById(R.id.rvChatMessages);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(AdminChatViewModel.class);
        
        // Setup click listeners
        setupClickListeners();
        
        // Setup observers
        setupObservers();
    }
    
    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true); // Messages appear from bottom
        rvChatMessages.setLayoutManager(layoutManager);
        
        // Admin ID is 40
        chatAdapter = new ChatAdapter(40);
        rvChatMessages.setAdapter(chatAdapter);
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            // Navigate back to customer list
            if (getActivity() instanceof AdminActivity) {
                AdminChatFragment listFragment = new AdminChatFragment();
                ((AdminActivity) getActivity()).setCurrentFragment(listFragment);
            }
        });
        
        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                viewModel.sendMessage(message);
                etMessage.setText("");
            }
        });
    }
    
    private void setupObservers() {
        // Observe selected customer
        viewModel.getSelectedCustomer().observe(getViewLifecycleOwner(), this::updateCustomerInfo);
        
        // Observe chat messages
        viewModel.getChatMessages().observe(getViewLifecycleOwner(), messages -> {
            chatAdapter.submitList(messages, () -> {
                // Callback được gọi sau khi adapter đã update xong
                if (messages != null && !messages.isEmpty()) {
                    hideEmptyState();
                    // Tự động scroll xuống cuối với smooth animation
                    rvChatMessages.post(() -> {
                        rvChatMessages.smoothScrollToPosition(messages.size() - 1);
                    });
                } else {
                    showEmptyState();
                }
            });
        });
        
        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading) {
                showLoading();
            } else {
                hideLoading();
            }
        });
        
        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                showError(errorMessage);
            }
        });
    }
    
    private void updateCustomerInfo(ChatCustomer customer) {
        if (customer != null) {
            tvCustomerName.setText(customer.getFullName());
            tvCustomerEmail.setText(customer.getEmail());
        }
    }
    
    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvChatMessages.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);
    }
    
    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        rvChatMessages.setVisibility(View.VISIBLE);
    }
    
    private void showEmptyState() {
        tvEmptyState.setVisibility(View.VISIBLE);
        rvChatMessages.setVisibility(View.GONE);
    }
    
    private void hideEmptyState() {
        tvEmptyState.setVisibility(View.GONE);
        rvChatMessages.setVisibility(View.VISIBLE);
    }
    
    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        startAutoRefresh();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        stopAutoRefresh();
    }
    
    private void startAutoRefresh() {
        stopAutoRefresh(); // Stop any existing timer
        ChatCustomer customer = viewModel.getSelectedCustomer().getValue();
        if (customer != null) {
            autoRefreshTimer = new Timer();
            autoRefreshTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            ChatCustomer currentCustomer = viewModel.getSelectedCustomer().getValue();
                            if (currentCustomer != null) {
                                viewModel.loadChatHistory(currentCustomer.getId());
                            }
                        });
                    }
                }
            }, AUTO_REFRESH_INTERVAL, AUTO_REFRESH_INTERVAL);
        }
    }
    
    private void stopAutoRefresh() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.cancel();
            autoRefreshTimer = null;
        }
    }
} 