package com.example.shopmate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopmate.R;
import com.example.shopmate.data.model.Cart;
import com.example.shopmate.ui.adapters.CheckoutItemAdapter;
import com.example.shopmate.viewmodel.CartViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class CheckoutFragment extends Fragment {
    
    private static final String TAG = "CheckoutFragment";
    
    // UI Components
    private MaterialToolbar toolbar;
    private RecyclerView checkoutItemsRecyclerView;
    private MaterialCardView orderSummaryCard;
    private TextView subtotalValue;
    private TextView shippingValue;
    private TextView totalValue;
    private RadioGroup paymentMethodGroup;
    private RadioButton codRadioButton;
    private RadioButton vnpayRadioButton;
    private MaterialButton placeOrderBtn;
    
    private CartViewModel viewModel;
    private CheckoutItemAdapter adapter;
    private Cart currentCart;
    private String selectedPaymentMethod = "COD";
    
    public static CheckoutFragment newInstance() {
        return new CheckoutFragment();
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);
        
        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
        
        return view;
    }
    
    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        checkoutItemsRecyclerView = view.findViewById(R.id.checkoutItemsRecyclerView);
        orderSummaryCard = view.findViewById(R.id.orderSummaryCard);
        subtotalValue = view.findViewById(R.id.subtotalValue);
        shippingValue = view.findViewById(R.id.shippingValue);
        totalValue = view.findViewById(R.id.totalValue);
        paymentMethodGroup = view.findViewById(R.id.paymentMethodGroup);
        codRadioButton = view.findViewById(R.id.codRadioButton);
        vnpayRadioButton = view.findViewById(R.id.vnpayRadioButton);
        placeOrderBtn = view.findViewById(R.id.placeOrderBtn);
        
        toolbar.setTitle("Checkout");
    }
    
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
    }
    
    private void setupRecyclerView() {
        adapter = new CheckoutItemAdapter();
        checkoutItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        checkoutItemsRecyclerView.setAdapter(adapter);
    }
    
    private void setupClickListeners() {
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        
        paymentMethodGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.codRadioButton) {
                selectedPaymentMethod = "COD";
            } else if (checkedId == R.id.vnpayRadioButton) {
                selectedPaymentMethod = "VNPAY";
            }
        });
        
        placeOrderBtn.setOnClickListener(v -> {
            handlePlaceOrder();
        });
    }
    
    private void observeViewModel() {
        viewModel.getCart().observe(getViewLifecycleOwner(), cart -> {
            if (cart != null && !cart.isEmpty()) {
                currentCart = cart;
                displayCheckoutItems(cart);
            }
        });
    }
    
    private void displayCheckoutItems(Cart cart) {
        adapter.setCartItems(cart.getCartItems());
        
        // Update order summary
        subtotalValue.setText(cart.getFormattedTotalPrice());
        shippingValue.setText("FREE");
        totalValue.setText(cart.getFormattedTotalPrice());
    }
    
    private void handlePlaceOrder() {
        if (currentCart == null || currentCart.isEmpty()) {
            Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String message = "Order placed successfully with " + selectedPaymentMethod + " payment!";
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        
        // TODO: Implement actual order placement logic
        // - Call API to create order
        // - Handle VNPAY payment if selected
        // - Navigate to order confirmation
        
        // For now, just go back to cart
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }
}