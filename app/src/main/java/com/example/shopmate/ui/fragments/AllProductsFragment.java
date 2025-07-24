package com.example.shopmate.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopmate.R;
import com.example.shopmate.data.model.Product;
import com.example.shopmate.ui.adapters.ProductAdapter;
import com.example.shopmate.viewmodel.HomeViewModel;
import com.example.shopmate.viewmodel.CartViewModel;

import java.util.List;

public class AllProductsFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private static final String TAG = "AllProductsFragment";

    private HomeViewModel viewModel;
    private CartViewModel cartViewModel;
    private ProductAdapter productAdapter;
    private RecyclerView productsRecyclerView;
    private FrameLayout loadingContainer;
    private LinearLayout emptyStateLayout;
    private TextView productsCount;
    private ImageView backButton;
    
    // Cart components
    private FrameLayout cartContainer;
    private TextView cartBadge;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_products, container, false);
        
        initViews(view);
        setupRecyclerView();
        setupViewModel();
        observeViewModel();
        setupClickListeners();
        setupCartNavigation();
        
        return view;
    }

    private void initViews(View view) {
        productsRecyclerView = view.findViewById(R.id.productsRecyclerView);
        loadingContainer = view.findViewById(R.id.loadingContainer);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        productsCount = view.findViewById(R.id.productsCount);
        backButton = view.findViewById(R.id.backButton);
        
        // Initialize cart components
        cartContainer = view.findViewById(R.id.cartContainer);
        cartBadge = view.findViewById(R.id.cartBadge);
        
        // Initially hide cart badge
        cartBadge.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter();
        productAdapter.setOnProductClickListener(this);
        
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        productsRecyclerView.setLayoutManager(layoutManager);
        productsRecyclerView.setAdapter(productAdapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
    }

    private void observeViewModel() {
        // Observe all products
        viewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                Log.d(TAG, "All products loaded: " + products.size());
                updateProductsDisplay(products);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loadingContainer.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                showError(errorMessage);
            }
        });
        
        // Observe cart to update the badge
        cartViewModel.getCart().observe(getViewLifecycleOwner(), cart -> {
            if (cart != null && cart.getItems() != null) {
                int itemCount = cart.getItems().size();
                updateCartBadge(itemCount);
            } else {
                updateCartBadge(0);
            }
        });
    }

    private void updateProductsDisplay(List<Product> products) {
        if (products.isEmpty()) {
            // Show empty state
            productsRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
            productsCount.setText("0 products found");
        } else {
            // Show products
            productsRecyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
            productAdapter.updateProducts(products);
            
            // Update products count
            String countText = products.size() + " product" + (products.size() != 1 ? "s" : "") + " found";
            productsCount.setText(countText);
        }
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCartBadge(int itemCount) {
        if (itemCount > 0) {
            cartBadge.setText(String.valueOf(itemCount));
            cartBadge.setVisibility(View.VISIBLE);
        } else {
            cartBadge.setVisibility(View.GONE);
        }
    }
    
    private void setupCartNavigation() {
        cartContainer.setOnClickListener(v -> {
            // Navigate to cart
            if (getActivity() != null) {
                CartFragment cartFragment = new CartFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, cartFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    // ProductAdapter.OnProductClickListener implementation
    @Override
    public void onProductClick(Product product) {
        Log.d(TAG, "Product clicked: " + product.getProductName());
        
        // Navigate to product detail screen
        if (getActivity() != null) {
            ProductDetailFragment productDetailFragment = ProductDetailFragment.newInstance(
                product.getId(), product.getProductName());
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, productDetailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
