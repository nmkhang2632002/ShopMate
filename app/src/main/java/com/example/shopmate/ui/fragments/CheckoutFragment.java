package com.example.shopmate.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.example.shopmate.viewmodel.OrderViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
    private TextInputLayout addressInputLayout;
    private TextInputEditText addressEditText;
    private MaterialButton placeOrderBtn;
    private FrameLayout loadingContainer;
    
    private CartViewModel cartViewModel;
    private OrderViewModel orderViewModel;
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
        setupViewModels();
        setupRecyclerView();
        setupClickListeners();
        observeViewModels();
        
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
        addressInputLayout = view.findViewById(R.id.addressInputLayout);
        addressEditText = view.findViewById(R.id.addressEditText);
        placeOrderBtn = view.findViewById(R.id.placeOrderBtn);
        loadingContainer = view.findViewById(R.id.loadingContainer);
        
        toolbar.setTitle("Checkout");
    }
    
    private void setupViewModels() {
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
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
            if (validateInputs()) {
                handlePlaceOrder();
            }
        });
    }
    
    private void observeViewModels() {
        // Observe cart data
        cartViewModel.getCart().observe(getViewLifecycleOwner(), cart -> {
            if (cart != null && !cart.isEmpty()) {
                currentCart = cart;
                displayCheckoutItems(cart);
            }
        });
        
        // Observe order creation loading state
        orderViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loadingContainer.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            placeOrderBtn.setEnabled(!isLoading);
            placeOrderBtn.setText(isLoading ? "Processing..." : "Place Order");
        });
        
        // Observe order creation error
        orderViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
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
    
    private boolean validateInputs() {
        String address = addressEditText.getText().toString().trim();
        
        if (address.isEmpty()) {
            addressInputLayout.setError("Please enter your address");
            return false;
        }
        
        addressInputLayout.setError(null);
        return true;
    }
    
    private void handlePlaceOrder() {
        if (currentCart == null || currentCart.isEmpty()) {
            Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String billingAddress = addressEditText.getText().toString().trim();

        if ("VNPAY".equals(selectedPaymentMethod)) {
            // Handle VNPay payment
            handleVNPayPayment(billingAddress);
        } else {
            // Handle COD payment
            handleCODPayment(billingAddress);
        }
    }

    private void handleCODPayment(String billingAddress) {
        // Call API to create COD order
        orderViewModel.createOrder("COD", billingAddress).observe(getViewLifecycleOwner(), order -> {
            if (order != null) {
                // Navigate to order success screen
                if (getActivity() != null) {
                    OrderSuccessFragment successFragment = OrderSuccessFragment.newInstance(order);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flFragment, successFragment)
                            .commit();
                }
            }
        });
    }

    private void handleVNPayPayment(String billingAddress) {
        showLoading(true);

        // Call API to create order and get VNPay URL
        orderViewModel.createVNPayOrder(billingAddress).observe(getViewLifecycleOwner(), vnpayUrl -> {
            showLoading(false);

            if (vnpayUrl != null && !vnpayUrl.isEmpty()) {
                // Open VNPay URL in browser
                openVNPayUrl(vnpayUrl);
            } else {
                Toast.makeText(getContext(), "Failed to create VNPay payment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openVNPayUrl(String vnpayUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(vnpayUrl));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Cannot open VNPay payment", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoading(boolean show) {
        if (loadingContainer != null) {
            loadingContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (placeOrderBtn != null) {
            placeOrderBtn.setEnabled(!show);
        }
    }
}
