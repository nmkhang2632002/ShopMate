package com.example.shopmate.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.example.shopmate.ui.activities.MainActivity;
import com.example.shopmate.ui.adapters.CartAdapter;
import com.example.shopmate.viewmodel.CartViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class CartFragment extends Fragment implements CartAdapter.CartItemActionListener {

    private static final String TAG = "CartFragment";

    // UI Components
    private MaterialToolbar toolbar;
    private MaterialButton clearCartBtn;
    private TextView cartItemsHeader;
    private RecyclerView cartItemsRecyclerView;
    private MaterialCardView orderSummaryCard;
    private TextView subtotalValue;
    private TextView shippingValue;
    private TextView totalValue;
    private MaterialButton checkoutBtn;
    private LinearLayout emptyCartContainer;
    private MaterialButton startShoppingBtn;
    private FrameLayout loadingContainer;
    private LinearLayout errorContainer;
    private TextView errorMessage;
    private MaterialButton retryBtn;

    private CartViewModel viewModel;
    private CartAdapter adapter;

    public static CartFragment newInstance() {
        return new CartFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        
        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupClickListeners();
        
        // Show loading when fragment is created
        loadingContainer.setVisibility(View.VISIBLE);
        
        observeViewModel();
        loadCart();
        
        return view;
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        clearCartBtn = view.findViewById(R.id.clearCartBtn);
        cartItemsHeader = view.findViewById(R.id.cartItemsHeader);
        cartItemsRecyclerView = view.findViewById(R.id.cartItemsRecyclerView);
        orderSummaryCard = view.findViewById(R.id.orderSummaryCard);
        subtotalValue = view.findViewById(R.id.subtotalValue);
        shippingValue = view.findViewById(R.id.shippingValue);
        totalValue = view.findViewById(R.id.totalValue);
        checkoutBtn = view.findViewById(R.id.checkoutBtn);
        emptyCartContainer = view.findViewById(R.id.emptyCartContainer);
        startShoppingBtn = view.findViewById(R.id.startShoppingBtn);
        loadingContainer = view.findViewById(R.id.loadingContainer);
        errorContainer = view.findViewById(R.id.errorContainer);
        errorMessage = view.findViewById(R.id.errorMessage);
        retryBtn = view.findViewById(R.id.retryBtn);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new CartAdapter(this);
        cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartItemsRecyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Clear cart button
        clearCartBtn.setOnClickListener(v -> {
            showClearCartConfirmation();
        });

        // Checkout button
        checkoutBtn.setOnClickListener(v -> {
            handleCheckout();
        });

        // Start shopping button (empty cart)
        startShoppingBtn.setOnClickListener(v -> {
            navigateToHome();
        });

        // Retry button
        retryBtn.setOnClickListener(v -> {
            loadCart();
        });
    }

    private void showClearCartConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Clear Cart")
                .setMessage("Are you sure you want to remove all items from your cart?")
                .setPositiveButton("Clear", (dialog, which) -> {
                    viewModel.clearCart();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void observeViewModel() {
        // Observe cart data
        viewModel.getCart().observe(getViewLifecycleOwner(), cart -> {
            if (cart != null) {
                displayCart(cart);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loadingContainer.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showError(error);
            }
        });
    }

    private void loadCart() {
        errorContainer.setVisibility(View.GONE);
        viewModel.loadCart();
    }

    private void displayCart(Cart cart) {
        if (cart.isEmpty()) {
            showEmptyCart();
        } else {
            showCartContent(cart);
        }
    }

    private void showEmptyCart() {
        cartItemsRecyclerView.setVisibility(View.GONE);
        cartItemsHeader.setVisibility(View.GONE);
        orderSummaryCard.setVisibility(View.GONE);
        emptyCartContainer.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);
        clearCartBtn.setVisibility(View.GONE);
    }

    private void showCartContent(Cart cart) {
        cartItemsRecyclerView.setVisibility(View.VISIBLE);
        cartItemsHeader.setVisibility(View.VISIBLE);
        orderSummaryCard.setVisibility(View.VISIBLE);
        emptyCartContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
        clearCartBtn.setVisibility(View.VISIBLE);

        // Update adapter with cart items
        adapter.setCartItems(cart.getCartItems());
        
        // Update cart header with item count
        int itemCount = cart.getCartItems().size();
        cartItemsHeader.setText("My Items (" + itemCount + ")");

        // Update order summary
        subtotalValue.setText(cart.getFormattedTotalPrice());
        totalValue.setText(cart.getFormattedTotalPrice());
    }

    private void handleCheckout() {
        // Show loading
        loadingContainer.setVisibility(View.VISIBLE);
        checkoutBtn.setEnabled(false);
        checkoutBtn.setText(R.string.proceeding_to_checkout);
        
        // Simulate processing delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (getActivity() != null) {
                CheckoutFragment checkoutFragment = CheckoutFragment.newInstance();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, checkoutFragment)
                        .addToBackStack(null)
                        .commit();
            }
            
            // Reset state
            loadingContainer.setVisibility(View.GONE);
            checkoutBtn.setEnabled(true);
            checkoutBtn.setText(R.string.proceed_to_checkout);
        }, 1500);
    }

    private void navigateToHome() {
        // Navigate to home using MainActivity's method
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToHome();
        }
    }

    private void showError(String message) {
        cartItemsRecyclerView.setVisibility(View.GONE);
        cartItemsHeader.setVisibility(View.GONE);
        orderSummaryCard.setVisibility(View.GONE);
        emptyCartContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.VISIBLE);
        clearCartBtn.setVisibility(View.GONE);
        errorMessage.setText(message);
    }

    @Override
    public void onQuantityChanged(int itemId, int newQuantity) {
        viewModel.updateCartItemQuantity(itemId, newQuantity);
    }

    @Override
    public void onRemoveItem(int itemId) {
        viewModel.removeCartItem(itemId);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh cart data when fragment becomes visible
        loadCart();
    }
} 
