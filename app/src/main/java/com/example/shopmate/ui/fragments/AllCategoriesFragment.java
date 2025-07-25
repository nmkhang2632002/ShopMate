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
import com.example.shopmate.data.model.Category;
import com.example.shopmate.ui.adapters.CategoryAdapter;
import com.example.shopmate.viewmodel.HomeViewModel;
import com.example.shopmate.viewmodel.CartViewModel;

import java.util.List;

public class AllCategoriesFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {

    private static final String TAG = "AllCategoriesFragment";

    private HomeViewModel viewModel;
    private CartViewModel cartViewModel;
    private CategoryAdapter categoryAdapter;
    private RecyclerView categoriesRecyclerView;
    private FrameLayout loadingContainer;
    private LinearLayout emptyStateLayout;
    private TextView categoriesCount;
    private ImageView backButton;
    
    // Cart components
    private FrameLayout cartContainer;
    private TextView cartBadge;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_categories, container, false);
        
        initViews(view);
        setupRecyclerView();
        setupViewModel();
        observeViewModel();
        setupClickListeners();
        setupCartNavigation();
        
        return view;
    }

    private void initViews(View view) {
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView);
        loadingContainer = view.findViewById(R.id.loadingContainer);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        categoriesCount = view.findViewById(R.id.categoriesCount);
        backButton = view.findViewById(R.id.backButton);
        
        // Initialize cart components
        cartContainer = view.findViewById(R.id.cartContainer);
        cartBadge = view.findViewById(R.id.cartBadge);
        
        // Initially hide cart badge
        cartBadge.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter();
        categoryAdapter.setOnCategoryClickListener(this);
        
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        categoriesRecyclerView.setLayoutManager(layoutManager);
        categoriesRecyclerView.setAdapter(categoryAdapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
    }

    private void observeViewModel() {
        // Observe all categories
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                Log.d(TAG, "All categories loaded: " + categories.size());
                updateCategoriesDisplay(categories);
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

    private void updateCategoriesDisplay(List<Category> categories) {
        if (categories.isEmpty()) {
            // Show empty state
            categoriesRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
            categoriesCount.setText("0 categories found");
        } else {
            // Show categories
            categoriesRecyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
            categoryAdapter.updateCategories(categories);
            
            // Update categories count
            String countText = categories.size() + " categor" + (categories.size() != 1 ? "ies" : "y") + " found";
            categoriesCount.setText(countText);
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

    // CategoryAdapter.OnCategoryClickListener implementation
    @Override
    public void onCategoryClick(Category category) {
        Log.d(TAG, "Category clicked: " + category.getCategoryName());
        
        // Navigate to category products screen
        if (getActivity() != null) {
            CategoryProductsFragment categoryProductsFragment = CategoryProductsFragment.newInstance(category);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, categoryProductsFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
