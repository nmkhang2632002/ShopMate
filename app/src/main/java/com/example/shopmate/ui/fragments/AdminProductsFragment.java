package com.example.shopmate.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.shopmate.R;
import com.example.shopmate.data.model.Product;
import com.example.shopmate.ui.adapters.AdminProductAdapter;
import com.example.shopmate.viewmodel.AdminProductViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdminProductsFragment extends Fragment implements
        AdminProductAdapter.OnProductActionListener {

    private AdminProductViewModel viewModel;
    private AdminProductAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextInputEditText searchEditText;
    private LinearProgressIndicator progressIndicator;
    private FloatingActionButton fabAdd;
    private View emptyStateView;

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final int SEARCH_DELAY = 500; // 500ms delay for search

    // Advanced search filters
    private String currentCategory = "";
    private String currentPriceRange = "";
    private String currentSortBy = "name";
    private boolean isAdvancedSearchMode = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_products, container, false);

        initViews(view);
        setupRecyclerView();
        setupSearch();
        setupObservers();
        setupClickListeners();

        // Load initial data
        viewModel.loadProducts();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AdminProductViewModel.class);
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        searchEditText = view.findViewById(R.id.searchEditText);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        fabAdd = view.findViewById(R.id.fabAdd);
        emptyStateView = view.findViewById(R.id.emptyStateView);
    }

    private void setupRecyclerView() {
        adapter = new AdminProductAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cancel previous search
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                // Schedule new search with delay
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    if (query.isEmpty()) {
                        viewModel.loadProducts();
                    } else {
                        viewModel.searchProducts(query);
                    }
                };

                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupObservers() {
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                adapter.updateProducts(products);
                updateEmptyState(products.isEmpty());
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        viewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Operation completed successfully", Toast.LENGTH_SHORT).show();
                viewModel.loadProducts(); // Refresh list
            }
        });

        // Observer for product detail
        viewModel.getProductDetail().observe(getViewLifecycleOwner(), product -> {
            if (product != null) {
                showProductDetailDialog(product);
            }
        });

        // Observer for most ordered products
        viewModel.getMostOrderedProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                showMostOrderedProductsDialog(products);
            }
        });
    }

    private void setupClickListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            String query = searchEditText.getText().toString().trim();
            if (query.isEmpty()) {
                viewModel.loadProducts();
            } else {
                viewModel.searchProducts(query);
            }
        });

        fabAdd.setOnClickListener(v -> showAddProductDialog());

        // Add long click listener for advanced options
        fabAdd.setOnLongClickListener(v -> {
            showAdvancedOptionsMenu();
            return true;
        });
    }

    private void updateEmptyState(boolean isEmpty) {
        emptyStateView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onEditProduct(Product product) {
        showEditProductDialog(product);
    }

    @Override
    public void onDeleteProduct(Product product) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete \"" + product.getProductName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteProduct(product.getId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onViewProduct(Product product) {
        // Load product detail using API
        viewModel.getProductDetail(product.getId());
    }

    private void showAddProductDialog() {
        AdminProductDialogFragment dialog = AdminProductDialogFragment.newInstance(null);
        dialog.setOnProductSavedListener(product -> viewModel.addProduct(product));
        dialog.show(getParentFragmentManager(), "AddProductDialog");
    }

    private void showEditProductDialog(Product product) {
        AdminProductDialogFragment dialog = AdminProductDialogFragment.newInstance(product);
        dialog.setOnProductSavedListener(updatedProduct ->
            viewModel.updateProduct(updatedProduct.getId(), updatedProduct));
        dialog.show(getParentFragmentManager(), "EditProductDialog");
    }

    private void showProductDetailDialog(Product product) {
        String productInfo = "Product Details:\n\n" +
                "Name: " + product.getProductName() + "\n" +
                "Price: " + product.getFormattedPrice() + "\n" +
                "Category: " + (product.getCategoryName() != null ? product.getCategoryName() : "No category") + "\n" +
                "Brief Description: " + (product.getBriefDescription() != null ? product.getBriefDescription() : "No brief description") + "\n" +
                "Full Description: " + (product.getFullDescription() != null ? product.getFullDescription() : "No full description") + "\n" +
                "Total Ordered: " + product.getTotalOrdered();

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Product Details")
                .setMessage(productInfo)
                .setPositiveButton("Edit", (dialog, which) -> showEditProductDialog(product))
                .setNegativeButton("Close", null)
                .show();
    }

    private void showMostOrderedProductsDialog(List<Product> products) {
        // Implement most ordered products dialog display
        StringBuilder productList = new StringBuilder("Most Ordered Products:\n");
        for (Product product : products) {
            productList.append("- ").append(product.getProductName()).append("\n");
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Most Ordered Products")
                .setMessage(productList.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void showAdvancedOptionsMenu() {
        // Implement advanced options menu display
        String[] options = {"Filter by Category", "Filter by Price Range", "Sort by Name", "Sort by Price", "Cancel"};
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Advanced Options")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // Filter by Category
                            showCategoryFilterDialog();
                            break;
                        case 1:
                            // Filter by Price Range
                            showPriceRangeFilterDialog();
                            break;
                        case 2:
                            // Sort by Name
                            sortProducts("name");
                            break;
                        case 3:
                            // Sort by Price
                            sortProducts("price");
                            break;
                        case 4:
                            // Cancel
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
    }

    private void showCategoryFilterDialog() {
        // Implement category filter dialog
        String[] categories = {"All", "Electronics", "Clothing", "Home & Kitchen", "Beauty & Health"};
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Category")
                .setItems(categories, (dialog, which) -> {
                    if (which == 0) {
                        // All categories
                        currentCategory = "";
                    } else {
                        // Selected category
                        currentCategory = categories[which];
                    }
                    applyAdvancedSearch();
                })
                .show();
    }

    private void showPriceRangeFilterDialog() {
        // Implement price range filter dialog
        String[] priceRanges = {"All", "$0 - $50", "$51 - $100", "$101 - $200", "$201 and above"};
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Price Range")
                .setItems(priceRanges, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // All price ranges
                            currentPriceRange = "";
                            break;
                        case 1:
                            // $0 - $50
                            currentPriceRange = "0-50";
                            break;
                        case 2:
                            // $51 - $100
                            currentPriceRange = "51-100";
                            break;
                        case 3:
                            // $101 - $200
                            currentPriceRange = "101-200";
                            break;
                        case 4:
                            // $201 and above
                            currentPriceRange = "201-";
                            break;
                    }
                    applyAdvancedSearch();
                })
                .show();
    }

    private void sortProducts(String sortBy) {
        currentSortBy = sortBy;
        applyAdvancedSearch();
    }

    private void applyAdvancedSearch() {
        // Implement the logic to apply advanced search filters
        viewModel.filterProducts(currentCategory, currentPriceRange, currentSortBy);
    }

    @Override
    public void onDestroy() {
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        super.onDestroy();
    }
}
