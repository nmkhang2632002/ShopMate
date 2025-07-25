package com.example.shopmate.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.shopmate.R;
import com.example.shopmate.data.model.Order;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AdminOrderStatusDialogFragment extends DialogFragment {

    private static final String ARG_ORDER = "order";

    private Order order;
    private OnStatusUpdatedListener listener;

    private AutoCompleteTextView actvStatus;
    private TextInputEditText etNote;
    private TextInputLayout tilStatus, tilNote;
    private MaterialButton btnUpdate;
    private MaterialButton btnCancel;

    private final String[] orderStatuses = {
        "Pending", "Processing", "Shipped", "Delivered", "Cancelled"
    };

    public interface OnStatusUpdatedListener {
        void onStatusUpdated(int orderId, String newStatus, String note);
    }

    public static AdminOrderStatusDialogFragment newInstance(Order order) {
        AdminOrderStatusDialogFragment fragment = new AdminOrderStatusDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ORDER, order);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            order = (Order) getArguments().getSerializable(ARG_ORDER);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_order_status, null);

        initViews(view);
        setupStatusDropdown();
        populateFields();
        setupClickListeners();

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .create();
    }

    private void initViews(View view) {
        actvStatus = view.findViewById(R.id.actvStatus);
        etNote = view.findViewById(R.id.etNote);
        tilStatus = view.findViewById(R.id.tilStatus);
        tilNote = view.findViewById(R.id.tilNote);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnCancel = view.findViewById(R.id.btnCancel);
    }

    private void setupStatusDropdown() {
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            orderStatuses
        );
        actvStatus.setAdapter(statusAdapter);
    }

    private void populateFields() {
        if (order != null) {
            actvStatus.setText(order.getStatus(), false);
        }
    }

    private void setupClickListeners() {
        btnUpdate.setOnClickListener(v -> updateStatus());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void updateStatus() {
        if (!validateInputs()) {
            return;
        }

        String newStatus = actvStatus.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (listener != null && order != null) {
            listener.onStatusUpdated(order.getId(), newStatus, note);
        }

        dismiss();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Clear previous errors
        clearErrors();

        String status = actvStatus.getText().toString().trim();
        if (status.isEmpty()) {
            tilStatus.setError("Status is required");
            isValid = false;
        }

        return isValid;
    }

    private void clearErrors() {
        tilStatus.setError(null);
        tilNote.setError(null);
    }

    public void setOnStatusUpdatedListener(OnStatusUpdatedListener listener) {
        this.listener = listener;
    }
}
