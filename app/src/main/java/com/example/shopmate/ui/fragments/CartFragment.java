package com.example.shopmate.ui.fragments;

import android.os.Bundle;
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
import com.example.shopmate.ui.adapters.CartAdapter;
import com.example.shopmate.viewmodel.CartViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class CartFragment extends Fragment implements CartAdapter.CartItemActionListener {

    private static final String TAG = "CartFragment";

    // UI Components
    private MaterialToolbar toolbar;
    private RecyclerView cartItemsRecyclerView;
    private MaterialCardView orderSummaryCard;
    private TextView subtotalValue;
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
        observeViewModel();
        
        return view;
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        cartItemsRecyclerView = view.findViewById(R.id.cartItemsRecyclerView);
        orderSummaryCard = view.findViewById(R.id.orderSummaryCard);
        subtotalValue = view.findViewById(R.id.subtotalValue);
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
        orderSummaryCard.setVisibility(View.GONE);
        emptyCartContainer.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);
    }

    private void showCartContent(Cart cart) {
        cartItemsRecyclerView.setVisibility(View.VISIBLE);
        orderSummaryCard.setVisibility(View.VISIBLE);
        emptyCartContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);

        // Update adapter with cart items
        adapter.setCartItems(cart.getCartItems());

        // Update order summary
        subtotalValue.setText(cart.getFormattedTotalPrice());
        totalValue.setText(cart.getFormattedTotalPrice());
    }

    private void handleCheckout() {
        Toast.makeText(getContext(), R.string.proceeding_to_checkout, Toast.LENGTH_SHORT).show();
        // In a real app, navigate to checkout screen
    }

    private void navigateToHome() {
        // In a real app, navigate to home screen
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    private void showError(String message) {
        cartItemsRecyclerView.setVisibility(View.GONE);
        orderSummaryCard.setVisibility(View.GONE);
        emptyCartContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.VISIBLE);
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