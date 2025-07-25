package com.example.shopmate.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.shopmate.R;
import com.example.shopmate.data.model.Order;
import com.example.shopmate.ui.adapters.AdminOrderAdapter;
import com.example.shopmate.viewmodel.AdminOrderViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminOrdersFragment extends Fragment implements
        AdminOrderAdapter.OnOrderActionListener {

    private AdminOrderViewModel viewModel;
    private AdminOrderAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextInputEditText searchEditText;
    private LinearProgressIndicator progressIndicator;
    private View emptyStateView;
    private ChipGroup statusChipGroup;
    private AutoCompleteTextView statusFilterSpinner;
    private TextInputLayout statusFilterLayout;

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final int SEARCH_DELAY = 500;

    private String currentStatusFilter = "";
    private final String[] orderStatuses = {
        "All Status", "Pending", "Processing", "Shipped", "Delivered", "Cancelled"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_orders, container, false);

        initViews(view);
        setupRecyclerView();
        setupSearch();
        setupStatusFilter();
        setupObservers();
        setupClickListeners();

        // Load initial data
        viewModel.loadOrders();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AdminOrderViewModel.class);
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewOrders);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        searchEditText = view.findViewById(R.id.searchEditText);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        emptyStateView = view.findViewById(R.id.emptyStateView);
        statusChipGroup = view.findViewById(R.id.statusChipGroup);
        statusFilterSpinner = view.findViewById(R.id.statusFilterSpinner);
        statusFilterLayout = view.findViewById(R.id.statusFilterLayout);
    }

    private void setupRecyclerView() {
        adapter = new AdminOrderAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> {
                    String query = s.toString().trim();
                    performSearch(query);
                };

                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupStatusFilter() {
        // Setup dropdown for status filter
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            orderStatuses
        );
        statusFilterSpinner.setAdapter(statusAdapter);
        statusFilterSpinner.setText("All Status", false);

        statusFilterSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedStatus = orderStatuses[position];
            if (selectedStatus.equals("All Status")) {
                currentStatusFilter = "";
            } else {
                currentStatusFilter = selectedStatus;
            }
            performSearch(searchEditText.getText().toString().trim());
        });

        // Setup status chips for quick filtering
        setupStatusChips();
    }

    private void setupStatusChips() {
        String[] quickStatuses = {"All", "Pending", "Processing", "Delivered"};

        for (String status : quickStatuses) {
            Chip chip = new Chip(getContext());
            chip.setText(status);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(R.color.chip_background);

            if (status.equals("All")) {
                chip.setChecked(true);
            }

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Uncheck other chips
                    for (int i = 0; i < statusChipGroup.getChildCount(); i++) {
                        Chip otherChip = (Chip) statusChipGroup.getChildAt(i);
                        if (otherChip != chip) {
                            otherChip.setChecked(false);
                        }
                    }

                    // Update filter
                    currentStatusFilter = status.equals("All") ? "" : status;
                    statusFilterSpinner.setText(status.equals("All") ? "All Status" : status, false);
                    performSearch(searchEditText.getText().toString().trim());
                }
            });

            statusChipGroup.addView(chip);
        }
    }

    private void performSearch(String query) {
        if (query.isEmpty() && currentStatusFilter.isEmpty()) {
            viewModel.loadOrders();
        } else {
            // Áp dụng filter trạng thái trước (nếu có)
            if (!currentStatusFilter.isEmpty()) {
                viewModel.filterByStatus(currentStatusFilter);
            }

            // Sau đó search theo query (nếu có)
            if (!query.isEmpty()) {
                viewModel.searchOrders(query);
            }

            // Nếu chỉ có query mà không có filter status
            if (!query.isEmpty() && currentStatusFilter.isEmpty()) {
                viewModel.searchOrders(query);
            }

            // Nếu chỉ có filter status mà không có query
            if (query.isEmpty() && !currentStatusFilter.isEmpty()) {
                viewModel.filterByStatus(currentStatusFilter);
            }
        }
    }

    private void setupObservers() {
        viewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) {
                adapter.updateOrders(orders);
                updateEmptyState(orders.isEmpty());
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        viewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Order status updated successfully", Toast.LENGTH_SHORT).show();
                performSearch(searchEditText.getText().toString().trim());
            }
        });
    }

    private void setupClickListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            performSearch(searchEditText.getText().toString().trim());
        });
    }

    private void updateEmptyState(boolean isEmpty) {
        emptyStateView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onViewOrderDetails(Order order) {
        // Navigate to order detail view
        Toast.makeText(getContext(), "View order #" + order.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateOrderStatus(Order order) {
        showUpdateStatusDialog(order);
    }

    private void showUpdateStatusDialog(Order order) {
        AdminOrderStatusDialogFragment dialog = AdminOrderStatusDialogFragment.newInstance(order);
        dialog.setOnStatusUpdatedListener((orderId, newStatus, note) ->
            viewModel.updateOrderStatus(orderId, newStatus));
        dialog.show(getParentFragmentManager(), "UpdateOrderStatusDialog");
    }

    @Override
    public void onDestroy() {
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        super.onDestroy();
    }
}
