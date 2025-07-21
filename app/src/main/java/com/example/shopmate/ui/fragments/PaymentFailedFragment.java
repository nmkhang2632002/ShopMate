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
import com.example.shopmate.ui.activities.MainActivity;
import com.google.android.material.button.MaterialButton;

public class PaymentFailedFragment extends Fragment {
    
    private static final String ARG_ORDER_ID = "order_id";
    private static final String ARG_ERROR_MESSAGE = "error_message";
    
    private TextView orderIdText;
    private TextView errorMessageText;
    private MaterialButton retryPaymentBtn;
    private MaterialButton backToCartBtn;
    
    public static PaymentFailedFragment newInstance(String orderId, String errorMessage) {
        PaymentFailedFragment fragment = new PaymentFailedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_ID, orderId);
        args.putString(ARG_ERROR_MESSAGE, errorMessage);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_failed, container, false);
        
        initViews(view);
        displayFailureDetails();
        setupClickListeners();
        
        return view;
    }
    
    private void initViews(View view) {
        orderIdText = view.findViewById(R.id.orderIdText);
        errorMessageText = view.findViewById(R.id.errorMessageText);
        retryPaymentBtn = view.findViewById(R.id.retryPaymentBtn);
        backToCartBtn = view.findViewById(R.id.backToCartBtn);
    }
    
    private void displayFailureDetails() {
        Bundle args = getArguments();
        if (args != null) {
            String orderId = args.getString(ARG_ORDER_ID, "N/A");
            String errorMessage = args.getString(ARG_ERROR_MESSAGE, "Payment failed");
            
            orderIdText.setText("Order #" + orderId);
            errorMessageText.setText(errorMessage);
        }
    }
    
    private void setupClickListeners() {
        retryPaymentBtn.setOnClickListener(v -> {
            // Navigate back to checkout to retry payment
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).setCurrentFragment(new CartFragment());
            }
        });
        
        backToCartBtn.setOnClickListener(v -> {
            // Navigate back to cart
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).setCurrentFragment(new CartFragment());
            }
        });
    }
}
