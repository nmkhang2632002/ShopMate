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

import com.bumptech.glide.Glide;
import com.example.shopmate.R;
import com.example.shopmate.data.model.Product;
import com.example.shopmate.viewmodel.ProductDetailViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ProductDetailFragment extends Fragment {

    private static final String TAG = "ProductDetailFragment";
    private static final String ARG_PRODUCT_ID = "product_id";
    private static final String ARG_PRODUCT_NAME = "product_name";

    private ProductDetailViewModel viewModel;
    private int productId;
    private String productName;
    private int currentQuantity = 1;
    private Product currentProduct;

    // UI Components
    private MaterialToolbar toolbar;
    private ImageView productImage;
    private TextView productNameText;
    private TextView productBriefDescription;
    private TextView productPriceText;
    private TextView productDescription;
    private TextView productCategory;
    private TextView quantityText;
    private MaterialButton decreaseQuantityBtn;
    private MaterialButton increaseQuantityBtn;
    private MaterialButton addToCartBtn;
    private FrameLayout loadingContainer;
    private LinearLayout errorContainer;
    private TextView errorMessage;
    private MaterialButton retryBtn;

    public static ProductDetailFragment newInstance(int productId, String productName) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PRODUCT_ID, productId);
        args.putString(ARG_PRODUCT_NAME, productName);
        fragment.setArguments(args);
        return fragment;
    }

    // Backward compatibility - for existing navigation that passes Product object
    public static ProductDetailFragment newInstance(Product product) {
        return newInstance(product.getId(), product.getProductName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getInt(ARG_PRODUCT_ID);
            productName = getArguments().getString(ARG_PRODUCT_NAME);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        
        initViews(view);
        setupViewModel();
        setupClickListeners();
        observeViewModel();
        
        return view;
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        productImage = view.findViewById(R.id.productImage);
        productNameText = view.findViewById(R.id.productName);
        productBriefDescription = view.findViewById(R.id.productBriefDescription);
        productPriceText = view.findViewById(R.id.productPrice);
        productDescription = view.findViewById(R.id.productDescription);
        productCategory = view.findViewById(R.id.productCategory);
        quantityText = view.findViewById(R.id.quantityText);
        decreaseQuantityBtn = view.findViewById(R.id.decreaseQuantityBtn);
        increaseQuantityBtn = view.findViewById(R.id.increaseQuantityBtn);
        addToCartBtn = view.findViewById(R.id.addToCartBtn);
        loadingContainer = view.findViewById(R.id.loadingContainer);
        errorContainer = view.findViewById(R.id.errorContainer);
        errorMessage = view.findViewById(R.id.errorMessage);
        retryBtn = view.findViewById(R.id.retryBtn);

        // Set initial toolbar title
        if (productName != null) {
            toolbar.setTitle(productName);
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);
    }

    private void setupClickListeners() {
        // Toolbar back button
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Quantity controls
        decreaseQuantityBtn.setOnClickListener(v -> {
            if (currentQuantity > 1) {
                currentQuantity--;
                updateQuantityDisplay();
            }
        });

        increaseQuantityBtn.setOnClickListener(v -> {
            if (currentQuantity < 10) { // Max quantity limit
                currentQuantity++;
                updateQuantityDisplay();
            }
        });

        // Add to cart
        addToCartBtn.setOnClickListener(v -> {
            handleAddToCart();
        });

        // Retry button
        retryBtn.setOnClickListener(v -> {
            loadProductDetails();
        });
    }

    private void observeViewModel() {
        // Observe product details
        viewModel.getProduct(productId).observe(getViewLifecycleOwner(), product -> {
            if (product != null) {
                currentProduct = product;
                displayProductDetails(product);
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
        
        // Observe add to cart status
        viewModel.getIsAddingToCart().observe(getViewLifecycleOwner(), isAdding -> {
            if (isAdding) {
                addToCartBtn.setEnabled(false);
                addToCartBtn.setText(R.string.adding_to_cart);
            } else {
                addToCartBtn.setEnabled(true);
                addToCartBtn.setText(R.string.add_to_cart);
            }
        });
        
        viewModel.getAddToCartSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), R.string.added_to_cart_success, Toast.LENGTH_SHORT).show();
            }
        });
        
        viewModel.getAddToCartError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductDetails() {
        errorContainer.setVisibility(View.GONE);
        viewModel.refreshProduct(productId);
    }

    private void displayProductDetails(Product product) {
        Log.d(TAG, "Displaying product: " + product.getProductName());
        
        // Update toolbar title
        toolbar.setTitle(product.getProductName());
        
        // Product basic info
        productNameText.setText(product.getProductName());
        
        // Brief description
        String briefDesc = product.getBriefDescription();
        if (briefDesc != null && !briefDesc.isEmpty()) {
            productBriefDescription.setText(briefDesc);
            productBriefDescription.setVisibility(View.VISIBLE);
        } else {
            productBriefDescription.setVisibility(View.GONE);
        }
        
        // Price
        productPriceText.setText(product.getFormattedPrice());
        
        // Full description
        String description = product.getFullDescription();
        if (description == null || description.trim().isEmpty()) {
            description = product.getBriefDescription();
        }
        if (description == null || description.trim().isEmpty()) {
            description = getString(R.string.no_description_available);
        }
        productDescription.setText(description);
        
        // Product category
        productCategory.setText(product.getCategoryName() != null ? product.getCategoryName() : "N/A");
        
        
        // Update add to cart button state
        addToCartBtn.setEnabled(true);
        addToCartBtn.setText(R.string.add_to_cart);
        
        // Load product image
        if (product.getImageURL() != null && !product.getImageURL().isEmpty()) {
            Glide.with(this)
                    .load(product.getImageURL())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(productImage);
        }
        
        // Hide error state
        errorContainer.setVisibility(View.GONE);
    }

    private void updateQuantityDisplay() {
        quantityText.setText(String.valueOf(currentQuantity));
        decreaseQuantityBtn.setEnabled(currentQuantity > 1);
        increaseQuantityBtn.setEnabled(currentQuantity < 10);
    }

    private void handleAddToCart() {
        if (currentProduct == null) {
            Toast.makeText(getContext(), R.string.product_not_available, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Calculate total price based on quantity
        double totalPrice = currentProduct.getPrice() * currentQuantity;
        
        // Call the view model to add to cart
        viewModel.addToCart(currentProduct.getId(), currentQuantity, totalPrice);
    }

    private void showError(String message) {
        Log.e(TAG, "Error: " + message);
        errorMessage.setText(message);
        errorContainer.setVisibility(View.VISIBLE);
        
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProductDetails();
    }
} 