package com.example.shopmate.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.shopmate.R;
import com.example.shopmate.data.model.Banner;
import com.example.shopmate.data.model.Category;
import com.example.shopmate.data.model.Product;
import com.example.shopmate.ui.adapters.BannerAdapter;
import com.example.shopmate.ui.adapters.CategoryAdapter;
import com.example.shopmate.ui.adapters.ProductAdapter;
import com.example.shopmate.viewmodel.CartViewModel;
import com.example.shopmate.viewmodel.HomeViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class HomeFragment extends Fragment implements 
        CategoryAdapter.OnCategoryClickListener,
        ProductAdapter.OnProductClickListener,
        BannerAdapter.OnBannerClickListener {

    private static final String TAG = "HomeFragment";
    
    private HomeViewModel viewModel;
    private CartViewModel cartViewModel;
    private ViewPager2 bannerViewPager;
    private TabLayout bannerIndicator;
    private RecyclerView categoriesRecyclerView;
    private RecyclerView featuredProductsRecyclerView;
    private BannerAdapter bannerAdapter;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private FrameLayout loadingContainer;
    private FrameLayout cartContainer;
    private TextView cartBadge;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        
        // Initialize views
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        bannerIndicator = view.findViewById(R.id.bannerIndicator);
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView);
        featuredProductsRecyclerView = view.findViewById(R.id.featuredProductsRecyclerView);
        loadingContainer = view.findViewById(R.id.loadingContainer);
        cartContainer = view.findViewById(R.id.cartContainer);
        cartBadge = view.findViewById(R.id.cartBadge);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupAdapters();
        setupViewModel();
        observeViewModel();
        setupCartNavigation();
    }

    private void initViews(View view) {
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        bannerIndicator = view.findViewById(R.id.bannerIndicator);
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView);
        featuredProductsRecyclerView = view.findViewById(R.id.featuredProductsRecyclerView);
        loadingContainer = view.findViewById(R.id.loadingContainer);
        cartContainer = view.findViewById(R.id.cartContainer);
        cartBadge = view.findViewById(R.id.cartBadge);
        
        // Initially hide the cart badge until we get data
        cartBadge.setVisibility(View.GONE);
    }

    private void setupAdapters() {
        // Setup Banner ViewPager
        bannerAdapter = new BannerAdapter(getContext());
        bannerViewPager.setAdapter(bannerAdapter);
        
        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(bannerIndicator, bannerViewPager,
            (tab, position) -> {
                // No text for tabs
            }
        ).attach();
        
        // Auto-scroll for banner (optional)
        autoScrollBanner();
        
        // Setup Categories RecyclerView with horizontal scrolling
        categoryAdapter = new CategoryAdapter();
        categoryAdapter.setOnCategoryClickListener(this);
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoriesRecyclerView.setLayoutManager(categoryLayoutManager);
        categoriesRecyclerView.setAdapter(categoryAdapter);
        
        // Setup Featured Products RecyclerView
        productAdapter = new ProductAdapter();
        productAdapter.setOnProductClickListener(this);
        GridLayoutManager productLayoutManager = new GridLayoutManager(getContext(), 2);
        featuredProductsRecyclerView.setLayoutManager(productLayoutManager);
        featuredProductsRecyclerView.setAdapter(productAdapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
    }

    private void observeViewModel() {
        // Observe categories
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                Log.d(TAG, "Categories loaded: " + categories.size());
                categoryAdapter.updateCategories(categories);
            }
        });

        // Observe featured products
        viewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                Log.d(TAG, "Products loaded: " + products.size());
                
                // For featured products, show first 6 products or all if less than 6
                List<Product> featuredProducts = products.size() > 6 ? 
                        products.subList(0, 6) : products;
                productAdapter.updateProducts(featuredProducts);
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

        viewModel.getBanners().observe(getViewLifecycleOwner(), banners -> {
            bannerAdapter.setBanners(banners);
            // Update indicator count
            bannerIndicator.setVisibility(banners.isEmpty() ? View.GONE : View.VISIBLE);
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
    
    private void updateCartBadge(int itemCount) {
        if (itemCount > 0) {
            cartBadge.setVisibility(View.VISIBLE);
            cartBadge.setText(String.valueOf(itemCount));
        } else {
            cartBadge.setVisibility(View.GONE);
        }
    }
    
    private void setupCartNavigation() {
        cartContainer.setOnClickListener(v -> {
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

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onBannerClick(Banner banner) {
    
    }

    private void autoScrollBanner() {
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = bannerViewPager.getCurrentItem();
                int count = bannerAdapter.getItemCount();
                bannerViewPager.setCurrentItem(currentItem < count - 1 ? currentItem + 1 : 0, true);
                handler.postDelayed(this, 3000); // Change banner every 3 seconds
            }
        };
        handler.postDelayed(runnable, 3000);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh cart data when fragment resumes
        cartViewModel.loadCart();
    }
}