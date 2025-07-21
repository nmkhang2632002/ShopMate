package com.example.shopmate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.shopmate.R;
import com.example.shopmate.data.model.Order;
import com.example.shopmate.ui.activities.MainActivity;
import com.google.android.material.button.MaterialButton;

public class OrderSuccessFragment extends Fragment {
    
    private static final String ARG_ORDER_ID = "order_id";
    private static final String ARG_ORDER_DATE = "order_date";
    private static final String ARG_PAYMENT_METHOD = "payment_method";
    
    private TextView orderIdText;
    private TextView orderDateText;
    private TextView paymentMethodText;
    private MaterialButton continueShoppingBtn;
    
    public static OrderSuccessFragment newInstance(Order order) {
        OrderSuccessFragment fragment = new OrderSuccessFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ORDER_ID, order.getId());
        args.putString(ARG_ORDER_DATE, order.getOrderDate());
        args.putString(ARG_PAYMENT_METHOD, order.getPaymentMethod());
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_success, container, false);
        
        initViews(view);
        displayOrderDetails();
        setupClickListeners();
        
        return view;
    }
    
    private void initViews(View view) {
        orderIdText = view.findViewById(R.id.orderIdText);
        orderDateText = view.findViewById(R.id.orderDateText);
        paymentMethodText = view.findViewById(R.id.paymentMethodText);
        continueShoppingBtn = view.findViewById(R.id.continueShoppingBtn);
    }
    
    private void displayOrderDetails() {
        if (getArguments() != null) {
            int orderId = getArguments().getInt(ARG_ORDER_ID);
            String orderDate = getArguments().getString(ARG_ORDER_DATE);
            String paymentMethod = getArguments().getString(ARG_PAYMENT_METHOD);
            
            orderIdText.setText(String.format("#%d", orderId));
            orderDateText.setText(formatDate(orderDate));
            paymentMethodText.setText(paymentMethod);
        }
    }
    
    private String formatDate(String dateString) {
        // Simple formatting for demo
        // In production, use proper date formatting
        return dateString != null ? dateString.substring(0, 10) : "";
    }
    
    private void setupClickListeners() {
        continueShoppingBtn.setOnClickListener(v -> {
            // Navigate back to home
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToHome();
            }
        });
    }
}