package com.example.shopmate.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.shopmate.viewmodel.CartViewModel;
import com.example.shopmate.viewmodel.SearchViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private static final String TAG = "SearchFragment";

    private SearchViewModel searchViewModel;
    private CartViewModel cartViewModel;

    private MaterialToolbar toolbar;
    private TextInputEditText searchEditText;
    private FloatingActionButton filterFab;
    private RecyclerView searchResultsRecyclerView;

    private ProductAdapter productAdapter;
    private BottomSheetDialog filterBottomSheet;

    // Filter variables
    private String currentSearchQuery = "";
    private String selectedCategory = "";
    private String selectedPriceRange = "";
    private String selectedSortBy = "name";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initViewModels();
        setupRecyclerView();
        setupSearchFunctionality();
        setupFilterButton();
        observeViewModel();

        // Load initial data
        searchViewModel.loadFilterOptions();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        searchEditText = view.findViewById(R.id.searchEditText);
        filterFab = view.findViewById(R.id.filterFab);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);

        // Setup toolbar
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void initViewModels() {
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter();
        productAdapter.setOnProductClickListener(this);
        searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        searchResultsRecyclerView.setAdapter(productAdapter);
    }

    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentSearchQuery = s.toString().trim();
                performSearch();
            }
        });
    }

    private void setupFilterButton() {
        filterFab.setOnClickListener(v -> showFilterBottomSheet());
    }

    private void observeViewModel() {
        // Observe search results
        searchViewModel.getSearchResults().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                productAdapter.updateProducts(products);
                Log.d(TAG, "Search results updated: " + products.size() + " products");
            }
        });

        // Observe filter options (for future category implementation)
        // searchViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
        //     Log.d(TAG, "Categories loaded: " + (categories != null ? categories.size() : 0));
        // });

        // Observe loading state
        searchViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Handle loading state if needed
        });

        // Observe errors
        searchViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch() {
        searchViewModel.searchProducts(
                currentSearchQuery,
                selectedCategory.isEmpty() ? null : selectedCategory,
                selectedPriceRange.isEmpty() ? null : selectedPriceRange,
                selectedSortBy,
                0, // page
                20 // size
        );
    }

    private void showFilterBottomSheet() {
        if (filterBottomSheet == null) {
            createFilterBottomSheet();
        }
        filterBottomSheet.show();
    }

    private void createFilterBottomSheet() {
        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_filter, null);

        filterBottomSheet = new BottomSheetDialog(getContext());
        filterBottomSheet.setContentView(bottomSheetView);

        // Category filter
        ChipGroup categoryChipGroup = bottomSheetView.findViewById(R.id.categoryChipGroup);
        setupCategoryFilter(categoryChipGroup);

        // Price range filter
        ChipGroup priceRangeChipGroup = bottomSheetView.findViewById(R.id.priceRangeChipGroup);
        setupPriceRangeFilter(priceRangeChipGroup);

        // Sort by filter
        ChipGroup sortByChipGroup = bottomSheetView.findViewById(R.id.sortByChipGroup);
        setupSortByFilter(sortByChipGroup);

        // Apply and Reset buttons
        MaterialButton applyButton = bottomSheetView.findViewById(R.id.applyFilterButton);
        MaterialButton resetButton = bottomSheetView.findViewById(R.id.resetFilterButton);

        applyButton.setOnClickListener(v -> {
            performSearch();
            filterBottomSheet.dismiss();
        });

        resetButton.setOnClickListener(v -> {
            resetFilters();
            updateFilterChips();
            performSearch();
        });
    }

    private void setupCategoryFilter(ChipGroup chipGroup) {
        // Categories not implemented yet - comment out for now
        /*
        List<String> categories = searchViewModel.getCategoriesValue();
        if (categories != null) {
            for (String category : categories) {
                Chip chip = new Chip(getContext());
                chip.setText(category);
                chip.setCheckable(true);
                chip.setChecked(category.equals(selectedCategory));
                chip.setOnCheckedChangeListener((button, isChecked) -> {
                    if (isChecked) {
                        selectedCategory = category;
                        // Uncheck other chips
                        for (int i = 0; i < chipGroup.getChildCount(); i++) {
                            Chip otherChip = (Chip) chipGroup.getChildAt(i);
                            if (otherChip != chip) {
                                otherChip.setChecked(false);
                            }
                        }
                    } else if (category.equals(selectedCategory)) {
                        selectedCategory = "";
                    }
                });
                chipGroup.addView(chip);
            }
        }
        */
    }

    private void setupPriceRangeFilter(ChipGroup chipGroup) {
        String[] priceRanges = {"under_1m", "1m_to_10m", "over_10m"};
        String[] priceLabels = {"Dưới 1 triệu", "1-10 triệu", "Trên 10 triệu"};

        for (int i = 0; i < priceRanges.length; i++) {
            Chip chip = new Chip(getContext());
            chip.setText(priceLabels[i]);
            chip.setCheckable(true);
            chip.setChecked(priceRanges[i].equals(selectedPriceRange));

            final String priceRange = priceRanges[i];
            chip.setOnCheckedChangeListener((button, isChecked) -> {
                if (isChecked) {
                    selectedPriceRange = priceRange;
                    // Uncheck other chips
                    for (int j = 0; j < chipGroup.getChildCount(); j++) {
                        Chip otherChip = (Chip) chipGroup.getChildAt(j);
                        if (otherChip != chip) {
                            otherChip.setChecked(false);
                        }
                    }
                } else if (priceRange.equals(selectedPriceRange)) {
                    selectedPriceRange = "";
                }
            });
            chipGroup.addView(chip);
        }
    }

    private void setupSortByFilter(ChipGroup chipGroup) {
        String[] sortOptions = {"name", "price_asc", "price_desc", "popularity"};
        String[] sortLabels = {"Tên A-Z", "Giá tăng dần", "Giá giảm dần", "Phổ biến"};

        for (int i = 0; i < sortOptions.length; i++) {
            Chip chip = new Chip(getContext());
            chip.setText(sortLabels[i]);
            chip.setCheckable(true);
            chip.setChecked(sortOptions[i].equals(selectedSortBy));

            final String sortOption = sortOptions[i];
            chip.setOnCheckedChangeListener((button, isChecked) -> {
                if (isChecked) {
                    selectedSortBy = sortOption;
                    // Uncheck other chips
                    for (int j = 0; j < chipGroup.getChildCount(); j++) {
                        Chip otherChip = (Chip) chipGroup.getChildAt(j);
                        if (otherChip != chip) {
                            otherChip.setChecked(false);
                        }
                    }
                }
            });
            chipGroup.addView(chip);
        }
    }

    private void resetFilters() {
        selectedCategory = "";
        selectedPriceRange = "";
        selectedSortBy = "name";
    }

    private void updateFilterChips() {
        if (filterBottomSheet != null) {
            filterBottomSheet.dismiss();
            filterBottomSheet = null;
        }
    }

    @Override
    public void onProductClick(Product product) {
        // Navigate to product detail
        Bundle args = new Bundle();
        args.putSerializable("product", product);

        ProductDetailFragment fragment = new ProductDetailFragment();
        fragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.flFragment, fragment)
                .addToBackStack(null)
                .commit();
    }
}
