package com.example.shopmate.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.shopmate.ui.adapters.AdminChatCustomerAdapter;
import com.example.shopmate.viewmodel.AdminChatViewModel;

public class AdminChatFragment extends Fragment implements AdminChatCustomerAdapter.OnCustomerClickListener {

    private static final String TAG = "AdminChatFragment";
    private AdminChatViewModel viewModel;
    private RecyclerView rvCustomers;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private AdminChatCustomerAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        rvCustomers = view.findViewById(R.id.rvCustomers);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(AdminChatViewModel.class);
        
        // Setup observers
        setupObservers();
        
        // Load customer list
        Log.d(TAG, "Loading customer list");
        viewModel.loadCustomerList();
    }
    
    private void setupRecyclerView() {
        adapter = new AdminChatCustomerAdapter(this);
        rvCustomers.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCustomers.setAdapter(adapter);
    }
    
    private void setupObservers() {
        // Observe customer list
        viewModel.getCustomers().observe(getViewLifecycleOwner(), customers -> {
            if (customers != null) {
                Log.d(TAG, "Received customer list: " + customers.size() + " customers");
                for (ChatCustomer customer : customers) {
                    Log.d(TAG, "Customer: " + customer.getId() + ", " + customer.getFullName() + ", " + customer.getEmail());
                }
                adapter.submitList(customers);
                
                // Show empty state if no customers
                if (customers.isEmpty()) {
                    showEmptyState();
                } else {
                    hideEmptyState();
                }
            } else {
                Log.e(TAG, "Received null customer list");
                showEmptyState();
            }
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
                Log.e(TAG, "Error: " + errorMessage);
                showError(errorMessage);
            }
        });
    }
    
    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvCustomers.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);
    }
    
    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        rvCustomers.setVisibility(View.VISIBLE);
    }
    
    private void showEmptyState() {
        tvEmptyState.setVisibility(View.VISIBLE);
        rvCustomers.setVisibility(View.GONE);
    }
    
    private void hideEmptyState() {
        tvEmptyState.setVisibility(View.GONE);
        rvCustomers.setVisibility(View.VISIBLE);
    }
    
    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onCustomerClick(ChatCustomer customer) {
        // Set selected customer in ViewModel
        Log.d(TAG, "Customer clicked: " + customer.getId() + ", " + customer.getFullName());
        viewModel.setSelectedCustomer(customer);
        
        // Navigate to chat detail fragment
        if (getActivity() instanceof AdminActivity) {
            AdminChatDetailFragment detailFragment = new AdminChatDetailFragment();
            ((AdminActivity) getActivity()).setCurrentFragment(detailFragment);
        }
    }
} 