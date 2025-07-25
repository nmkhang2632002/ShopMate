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
import com.example.shopmate.util.CurrencyUtils;
import com.example.shopmate.data.model.Order;
import com.example.shopmate.ui.activities.MainActivity;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrderSuccessFragment extends Fragment {
    
    private static final String ARG_ORDER_ID = "order_id";
    private static final String ARG_ORDER_DATE = "order_date";
    private static final String ARG_PAYMENT_METHOD = "payment_method";
    private static final String ARG_TRANSACTION_ID = "transaction_id";
    private static final String ARG_IS_VNPAY = "is_vnpay";
    private static final String ARG_USERNAME = "username";
    private static final String ARG_PHONE_NUMBER = "phone_number";
    private static final String ARG_BILLING_ADDRESS = "billing_address";
    private static final String ARG_ORDER_STATUS = "order_status";
    private static final String ARG_TOTAL_AMOUNT = "total_amount";
    
    private TextView orderIdText;
    private TextView orderDateText;
    private TextView paymentMethodText;
    private TextView usernameText;
    private TextView phoneNumberText;
    private TextView billingAddressText;
    private TextView orderStatusText;
    private TextView totalAmountText;
    private TextView transactionIdText;
    private MaterialButton continueShoppingBtn;
    
    public static OrderSuccessFragment newInstance(Order order) {
        OrderSuccessFragment fragment = new OrderSuccessFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ORDER_ID, order.getId());

        // Format Date to String before putting into Bundle
        String orderDateString = "";
        if (order.getOrderDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            orderDateString = dateFormat.format(order.getOrderDate());
        }
        args.putString(ARG_ORDER_DATE, orderDateString);

        args.putString(ARG_PAYMENT_METHOD, order.getPaymentMethod());
        args.putBoolean(ARG_IS_VNPAY, false);
        fragment.setArguments(args);
        return fragment;
    }

    // Constructor cho VNPay payment result với thông tin đầy đủ
    public static OrderSuccessFragment newInstanceForVNPay(String orderId, String transactionId,
            String username, String phoneNumber, String billingAddress, String orderStatus, String totalAmount) {
        OrderSuccessFragment fragment = new OrderSuccessFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_ID, orderId);
        args.putString(ARG_TRANSACTION_ID, transactionId);
        args.putString(ARG_PAYMENT_METHOD, "VNPay");
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_PHONE_NUMBER, phoneNumber);
        args.putString(ARG_BILLING_ADDRESS, billingAddress);
        args.putString(ARG_ORDER_STATUS, orderStatus);
        args.putString(ARG_TOTAL_AMOUNT, totalAmount);
        args.putBoolean(ARG_IS_VNPAY, true);
        fragment.setArguments(args);
        return fragment;
    }

    // Constructor đơn giản cho VNPay (backward compatibility)
    public static OrderSuccessFragment newInstanceForVNPay(String orderId, String transactionId) {
        return newInstanceForVNPay(orderId, transactionId, "", "", "", "", "");
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
        usernameText = view.findViewById(R.id.usernameText);
        phoneNumberText = view.findViewById(R.id.phoneNumberText);
        billingAddressText = view.findViewById(R.id.billingAddressText);
        orderStatusText = view.findViewById(R.id.orderStatusText);
        totalAmountText = view.findViewById(R.id.totalAmountText);
        transactionIdText = view.findViewById(R.id.transactionIdText);
        continueShoppingBtn = view.findViewById(R.id.continueShoppingBtn);
    }
    
    private void displayOrderDetails() {
        Bundle args = getArguments();
        if (args != null) {
            // Basic order info
            String orderId = args.getString(ARG_ORDER_ID, "0");
            String orderDate = args.getString(ARG_ORDER_DATE, "");
            String paymentMethod = args.getString(ARG_PAYMENT_METHOD, "");

            // Additional info
            String username = args.getString(ARG_USERNAME, "");
            String phoneNumber = args.getString(ARG_PHONE_NUMBER, "");
            String billingAddress = args.getString(ARG_BILLING_ADDRESS, "");
            String orderStatus = args.getString(ARG_ORDER_STATUS, "");
            String totalAmount = args.getString(ARG_TOTAL_AMOUNT, "");
            String transactionId = args.getString(ARG_TRANSACTION_ID, "");
            boolean isVNPay = args.getBoolean(ARG_IS_VNPAY, false);

            // Set basic info
            orderIdText.setText("#" + orderId);
            orderDateText.setText(orderDate.isEmpty() ? "Just now" : formatDate(orderDate));
            paymentMethodText.setText(paymentMethod);

            // Set additional info
            usernameText.setText(username.isEmpty() ? "N/A" : username);
            phoneNumberText.setText(phoneNumber.isEmpty() ? "N/A" : phoneNumber);
            billingAddressText.setText(billingAddress.isEmpty() ? "N/A" : billingAddress);
            orderStatusText.setText(orderStatus.isEmpty() ? "Processing" : orderStatus);
            totalAmountText.setText(totalAmount.isEmpty() ? "N/A" : formatTotalAmount(totalAmount));

            // Show transaction ID for VNPay
            if (isVNPay && !transactionId.isEmpty()) {
                transactionIdText.setText(transactionId);
                if (getView() != null) {
                    getView().findViewById(R.id.transactionIdLayout).setVisibility(View.VISIBLE);
                }
            } else if (isVNPay) {
                // Hiển thị transaction ID layout ngay cả khi chưa có transaction ID
                if (getView() != null) {
                    getView().findViewById(R.id.transactionIdLayout).setVisibility(View.VISIBLE);
                    transactionIdText.setText("Processing...");
                }
            }
        }
    }


    
    private String formatDate(String dateString) {
        // Simple formatting for demo
        // In production, use proper date formatting
        return dateString != null ? dateString.substring(0, 10) : "";
    }
    
    private String formatTotalAmount(String totalAmountString) {
        try {
            double amount = Double.parseDouble(totalAmountString);
            return CurrencyUtils.formatVND(amount);
        } catch (NumberFormatException e) {
            return "N/A";
        }
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