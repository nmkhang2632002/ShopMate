package com.example.shopmate.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.example.shopmate.data.response.FilterOptionsResponse;
import com.example.shopmate.ui.adapters.BannerAdapter;
import com.example.shopmate.ui.adapters.CategoryAdapter;
import com.example.shopmate.ui.adapters.ProductAdapter;
import com.example.shopmate.viewmodel.CartViewModel;
import com.example.shopmate.viewmodel.HomeViewModel;
import com.example.shopmate.viewmodel.SearchViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class HomeFragment extends Fragment implements 
        CategoryAdapter.OnCategoryClickListener,
        ProductAdapter.OnProductClickListener,
        BannerAdapter.OnBannerClickListener {

    private static final String TAG = "HomeFragment";
    
    private HomeViewModel viewModel;
    private SearchViewModel searchViewModel;
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
    
    // Search components
    private TextInputEditText searchEditText;
    private ImageView filterIcon;
    private TextView seeAllProducts;
    private TextView seeAllCategories;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private boolean isSearchMode = false;
    private List<Product> originalProducts;

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
        
        // Initialize search components
        searchEditText = view.findViewById(R.id.searchEditText);
        filterIcon = view.findViewById(R.id.filterIcon);
        seeAllProducts = view.findViewById(R.id.seeAllProducts);
        seeAllCategories = view.findViewById(R.id.seeAllCategories);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupAdapters();
        setupViewModel();
        setupSearchFunctionality();
        setupSeeAllButton();
        setupSeeAllCategoriesButton();
        observeViewModel();
        setupCartNavigation();
    }

    private void initViews(View view) {
        // Views are already initialized in onCreateView, just set initial state
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
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
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

        // Observe featured products (from most-ordered API)
        viewModel.getFeaturedProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                Log.d(TAG, "Featured products loaded: " + products.size());
                
                if (!isSearchMode) {
                    productAdapter.updateProducts(products);
                }
            }
        });
        
        // Observe all products for search functionality
        viewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                Log.d(TAG, "All products loaded: " + products.size());
                
                // Store original products for search functionality
                originalProducts = products;
            }
        });

        // Observe search results
        searchViewModel.getSearchResults().observe(getViewLifecycleOwner(), searchResults -> {
            if (isSearchMode && searchResults != null) {
                Log.d(TAG, "Search results: " + searchResults.size());
                productAdapter.updateProducts(searchResults);
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

    private void setupSeeAllButton() {
        seeAllProducts.setOnClickListener(v -> {
            // Navigate to all products screen
            if (getActivity() != null) {
                AllProductsFragment allProductsFragment = new AllProductsFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, allProductsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void setupSeeAllCategoriesButton() {
        seeAllCategories.setOnClickListener(v -> {
            // Navigate to all categories screen
            if (getActivity() != null) {
                AllCategoriesFragment allCategoriesFragment = new AllCategoriesFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, allCategoriesFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    
    private void setupSearchFunctionality() {
        // Setup search EditText with TextWatcher for real-time search
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                
                // Cancel previous search request
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                
                // Schedule new search with 300ms delay (debouncing)
                searchRunnable = () -> performSearch(query);
                searchHandler.postDelayed(searchRunnable, 300);
            }
        });
        
        // Setup filter icon click
        filterIcon.setOnClickListener(v -> showFilterBottomSheet());
    }
    
    private void performSearch(String query) {
        if (query.isEmpty()) {
            // Return to normal mode - show featured products (most-ordered)
            isSearchMode = false;
            List<Product> featuredProducts = viewModel.getFeaturedProducts().getValue();
            if (featuredProducts != null) {
                productAdapter.updateProducts(featuredProducts);
            }
        } else {
            // Enter search mode
            isSearchMode = true;
            searchViewModel.searchProducts(query, null, null, "name", 0, 20);
        }
    }
    
    private void showFilterBottomSheet() {
        // Create and show filter bottom sheet
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_filter, null);
        
        bottomSheetDialog.setContentView(bottomSheetView);
        
        // Get filter options from API first
        searchViewModel.loadFilterOptions();
        
        // Setup filter options when data is available
        searchViewModel.getFilterOptions().observe(getViewLifecycleOwner(), filterOptions -> {
            if (filterOptions != null) {
                setupPriceRangeChips(bottomSheetView, filterOptions.getPriceRanges());
                setupSortByChips(bottomSheetView, filterOptions.getSortOptions());
            }
        });
        
        // Setup close button (there's no closeFilter in the layout, so skip this)
        // ImageView closeFilter = bottomSheetView.findViewById(R.id.closeFilter);
        // closeFilter.setOnClickListener(v -> bottomSheetDialog.dismiss());
        
        // Setup reset button
        MaterialButton resetButton = bottomSheetView.findViewById(R.id.resetFilterButton);
        resetButton.setOnClickListener(v -> {
            resetFilters(bottomSheetView);
        });
        
        // Setup apply button
        MaterialButton applyButton = bottomSheetView.findViewById(R.id.applyFilterButton);
        applyButton.setOnClickListener(v -> {
            applyFilters(bottomSheetView);
            bottomSheetDialog.dismiss();
        });
        
        bottomSheetDialog.show();
    }
    
    private void setupPriceRangeChips(View bottomSheetView, List<FilterOptionsResponse.PriceRange> priceRanges) {
        ChipGroup priceRangeChipGroup = bottomSheetView.findViewById(R.id.priceRangeChipGroup);
        
        // Clear existing chips
        priceRangeChipGroup.removeAllViews();
        
        // Add "All Prices" chip first
        Chip allPricesChip = new Chip(requireContext());
        allPricesChip.setText("All Prices");
        allPricesChip.setCheckable(true);
        allPricesChip.setChecked(true); // Default selected
        allPricesChip.setTag(null); // No price range filter
        priceRangeChipGroup.addView(allPricesChip);
        
        // Add price range chips from API
        if (priceRanges != null && !priceRanges.isEmpty()) {
            for (FilterOptionsResponse.PriceRange priceRange : priceRanges) {
                Chip chip = new Chip(requireContext());
                chip.setText(priceRange.getDisplayName());
                chip.setCheckable(true);
                chip.setTag(priceRange);
                priceRangeChipGroup.addView(chip);
            }
        } else {
            // Add default price ranges if API doesn't provide them
            String[] priceRangeValues = {"under_1m", "1m_to_10m", "over_10m"};
            String[] priceRangeLabels = {"Under 1M", "1M - 10M", "Over 10M"};
            
            for (int i = 0; i < priceRangeValues.length; i++) {
                FilterOptionsResponse.PriceRange priceRange = new FilterOptionsResponse.PriceRange();
                priceRange.setValue(priceRangeValues[i]);
                priceRange.setLabel(priceRangeLabels[i]);
                
                Chip chip = new Chip(requireContext());
                chip.setText(priceRangeLabels[i]);
                chip.setCheckable(true);
                chip.setTag(priceRange);
                priceRangeChipGroup.addView(chip);
            }
        }
    }
    
    private void setupSortByChips(View bottomSheetView, List<FilterOptionsResponse.SortOption> sortOptions) {
        ChipGroup sortByChipGroup = bottomSheetView.findViewById(R.id.sortByChipGroup);
        
        // Clear existing chips
        sortByChipGroup.removeAllViews();
        
        // Add sort option chips
        if (sortOptions != null && !sortOptions.isEmpty()) {
            for (FilterOptionsResponse.SortOption sortOption : sortOptions) {
                Chip chip = new Chip(requireContext());
                chip.setText(sortOption.getLabel());
                chip.setCheckable(true);
                chip.setTag(sortOption.getValue());
                sortByChipGroup.addView(chip);
            }
            // Check first chip by default
            if (sortByChipGroup.getChildCount() > 0) {
                ((Chip) sortByChipGroup.getChildAt(0)).setChecked(true);
            }
        } else {
            // Add default sort options if none from API
            String[] sortLabels = {"Name", "Price: Low to High", "Price: High to Low", "Newest"};
            String[] sortValues = {"name", "price_asc", "price_desc", "createdAt_desc"};
            
            for (int i = 0; i < sortLabels.length; i++) {
                Chip chip = new Chip(requireContext());
                chip.setText(sortLabels[i]);
                chip.setCheckable(true);
                chip.setTag(sortValues[i]);
                sortByChipGroup.addView(chip);
            }
            // Check first chip by default
            if (sortByChipGroup.getChildCount() > 0) {
                ((Chip) sortByChipGroup.getChildAt(0)).setChecked(true);
            }
        }
    }
    
    private void resetFilters(View bottomSheetView) {
        // Reset price range selection to first chip (All Prices)
        ChipGroup priceRangeChipGroup = bottomSheetView.findViewById(R.id.priceRangeChipGroup);
        if (priceRangeChipGroup.getChildCount() > 0) {
            ((Chip) priceRangeChipGroup.getChildAt(0)).setChecked(true);
        }
        
        // Reset sort selection to first chip
        ChipGroup sortByChipGroup = bottomSheetView.findViewById(R.id.sortByChipGroup);
        if (sortByChipGroup.getChildCount() > 0) {
            ((Chip) sortByChipGroup.getChildAt(0)).setChecked(true);
        }
    }
    
    private void applyFilters(View bottomSheetView) {
        // Get selected price range
        ChipGroup priceRangeChipGroup = bottomSheetView.findViewById(R.id.priceRangeChipGroup);
        int selectedPriceChipId = priceRangeChipGroup.getCheckedChipId();
        
        String priceRange = null;
        
        if (selectedPriceChipId != View.NO_ID) {
            Chip selectedPriceChip = bottomSheetView.findViewById(selectedPriceChipId);
            FilterOptionsResponse.PriceRange priceRangeObj = 
                    (FilterOptionsResponse.PriceRange) selectedPriceChip.getTag();
            if (priceRangeObj != null && priceRangeObj.getValue() != null) {
                priceRange = priceRangeObj.getValue();
            }
        }
        
        // Get selected sort option
        ChipGroup sortByChipGroup = bottomSheetView.findViewById(R.id.sortByChipGroup);
        int selectedSortChipId = sortByChipGroup.getCheckedChipId();
        String sortBy = "name"; // default
        
        if (selectedSortChipId != View.NO_ID) {
            Chip selectedSortChip = bottomSheetView.findViewById(selectedSortChipId);
            String sortTag = (String) selectedSortChip.getTag();
            if (sortTag != null) {
                sortBy = sortTag;
            }
        }
        
        // Apply search with filters
        String currentQuery = searchEditText.getText() != null ? 
                searchEditText.getText().toString().trim() : "";
        
        if (currentQuery.isEmpty() && priceRange == null && "name".equals(sortBy)) {
            // No filters applied, return to normal mode - show featured products
            isSearchMode = false;
            List<Product> featuredProducts = viewModel.getFeaturedProducts().getValue();
            if (featuredProducts != null) {
                productAdapter.updateProducts(featuredProducts);
            }
        } else {
            // Apply search with filters
            isSearchMode = true;
            Log.d(TAG, "Applying filters - Query: '" + currentQuery + "', PriceRange: '" + priceRange + "', SortBy: '" + sortBy + "'");
            searchViewModel.searchProducts(currentQuery, null, priceRange, sortBy, 0, 20);
        }
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
        Log.d(TAG, "Product image URL in Home: " + product.getImageURL());
        Log.d(TAG, "Product ID: " + product.getId());
        
        // Navigate to product detail screen
        if (getActivity() != null) {
            // TEST: Try passing the product directly instead of just ID
            ProductDetailFragment productDetailFragment = ProductDetailFragment.newInstanceWithProduct(product);
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