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
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.shopmate.R;
import com.example.shopmate.data.model.Order;
import com.example.shopmate.ui.adapters.AdminOrderAdapter;
import com.example.shopmate.ui.fragments.OrderDetailFragment;
import com.example.shopmate.viewmodel.AdminOrderViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
    private AutoCompleteTextView sortFilterSpinner;
    private TextInputLayout sortFilterLayout;

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final int SEARCH_DELAY = 500;

    private String currentStatusFilter = "";
    private String currentSortFilter = "Default";
    private final String[] orderStatuses = {
        "All Status", "Pending", "Processing", "Delivered", "Cancelled"
    };
    private final String[] sortOptions = {
        "Default", "Total Amount (Low to High)", "Total Amount (High to Low)", "Order ID (Low to High)", "Order ID (High to Low)"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_orders, container, false);

        initViews(view);
        setupRecyclerView();
        setupSearch();
        setupStatusChips();
        setupSortFilter();
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
        sortFilterSpinner = view.findViewById(R.id.sortFilterSpinner);
        sortFilterLayout = view.findViewById(R.id.sortFilterLayout);
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

    private void setupStatusChips() {
        String[] quickStatuses = {"All", "Pending", "Processing", "Delivered", "Cancelled"};

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
                    performSearch(searchEditText.getText().toString().trim());
                }
            });

            statusChipGroup.addView(chip);
        }
    }

    private void setupSortFilter() {
        // Setup dropdown for sort filter
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            sortOptions
        );
        sortFilterSpinner.setAdapter(sortAdapter);
        sortFilterSpinner.setText("Default", false);

        sortFilterSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSort = sortOptions[position];
            currentSortFilter = selectedSort;
            performSearch(searchEditText.getText().toString().trim());
        });
    }

    private void performSearch(String query) {
        if (query.isEmpty() && currentStatusFilter.isEmpty() && currentSortFilter.equals("Default")) {
            viewModel.loadOrders();
        } else {
            // Apply filters and search
            viewModel.searchAndFilterOrders(query, currentStatusFilter, currentSortFilter);
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
                Toast.makeText(getContext(), "Status updated successfully", Toast.LENGTH_SHORT).show();
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
        // Navigate to order detail view - sử dụng admin version để ẩn buy again button
        OrderDetailFragment detailFragment = OrderDetailFragment.newInstanceForAdmin(order.getId());
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onUpdateOrderStatus(Order order) {
        showUpdateStatusDialog(order);
    }

    @Override
    public void onUpdatePaymentStatus(Order order) {
        showUpdatePaymentStatusDialog(order);
    }

    private void showUpdateStatusDialog(Order order) {
        AdminOrderStatusDialogFragment dialog = AdminOrderStatusDialogFragment.newInstance(order);
        dialog.setOnStatusUpdatedListener((orderId, newStatus, note) ->
            viewModel.updateOrderStatus(orderId, newStatus));
        dialog.show(getParentFragmentManager(), "UpdateOrderStatusDialog");
    }

    private void showUpdatePaymentStatusDialog(Order order) {
        if (order.getPayments() == null || order.getPayments().isEmpty()) {
            Toast.makeText(getContext(), "No payment found for this order", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get payment info from the first payment
        Order.Payment payment = order.getPayments().get(0);
        int paymentId = payment.getId();
        String currentStatus = payment.getPaymentStatus();
        
        // Check if payment status is already final
        if ("Paid".equalsIgnoreCase(currentStatus) || "Cancelled".equalsIgnoreCase(currentStatus)) {
            Toast.makeText(getContext(), "Payment status '" + currentStatus + "' cannot be changed", Toast.LENGTH_SHORT).show();
            return;
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Update Payment Status")
                .setMessage("Order #" + order.getId() + 
                           "\nPayment ID: " + paymentId +
                           "\nCurrent Status: " + currentStatus +
                           "\n\nChoose new payment status:")
                .setPositiveButton("Mark as Paid", (dialog, which) -> {
                    showPaymentUpdateConfirmation(paymentId, "Paid", "Paid - Payment completed successfully", order);
                })
                .setNegativeButton("Mark as Cancelled", (dialog, which) -> {
                    showPaymentUpdateConfirmation(paymentId, "Cancelled", "Cancelled - Payment was cancelled", order);
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void showPaymentUpdateConfirmation(int paymentId, String newStatus, String statusDescription, Order order) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirm Payment Update")
                .setMessage("Are you sure you want to update payment status to:\n\n" + 
                           statusDescription + "\n\n" +
                           "Order #" + order.getId() + 
                           "\nPayment ID: " + paymentId)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    updatePaymentStatus(paymentId, newStatus);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updatePaymentStatus(int paymentId, String newStatus) {
        // Show progress
        Toast.makeText(getContext(), "Updating payment status to " + newStatus + "...", Toast.LENGTH_SHORT).show();
        
        // Call payment API to update status
        viewModel.updatePaymentStatus(paymentId, newStatus);
    }

    @Override
    public void onDestroy() {
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        super.onDestroy();
    }
}
