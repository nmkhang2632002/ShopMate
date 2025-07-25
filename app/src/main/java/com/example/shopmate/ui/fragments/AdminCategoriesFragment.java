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

import com.example.shopmate.R;
import com.example.shopmate.data.model.Category;
import com.example.shopmate.ui.adapters.AdminCategoryAdapter;
import com.example.shopmate.viewmodel.AdminCategoryViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdminCategoriesFragment extends Fragment implements
        AdminCategoryAdapter.OnCategoryActionListener {

    private AdminCategoryViewModel viewModel;
    private AdminCategoryAdapter adapter;
    private RecyclerView recyclerView;
    private TextInputEditText searchEditText;
    private LinearProgressIndicator progressIndicator;
    private FloatingActionButton fabAdd;
    private View emptyStateView;

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final int SEARCH_DELAY = 500;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_categories, container, false);

        initViews(view);
        setupRecyclerView();
        setupSearch();
        setupObservers();
        setupClickListeners();

        // Load initial data
        viewModel.loadCategories();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AdminCategoryViewModel.class);
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewCategories);
        searchEditText = view.findViewById(R.id.searchEditText);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        fabAdd = view.findViewById(R.id.fabAdd);
        emptyStateView = view.findViewById(R.id.emptyStateView);
    }

    private void setupRecyclerView() {
        adapter = new AdminCategoryAdapter(new ArrayList<>(), this);
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
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> {
                    String query = s.toString().trim();
                    if (query.isEmpty()) {
                        viewModel.loadCategories();
                    } else {
                        viewModel.searchCategories(query);
                    }
                };

                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupObservers() {
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                adapter.updateCategories(categories);
                updateEmptyState(categories.isEmpty());
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Operation completed successfully", Toast.LENGTH_SHORT).show();
                viewModel.loadCategories();
            }
        });
    }

    private void setupClickListeners() {
        fabAdd.setOnClickListener(v -> showAddCategoryDialog());
    }

    private void updateEmptyState(boolean isEmpty) {
        emptyStateView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onEditCategory(Category category) {
        showEditCategoryDialog(category);
    }

    @Override
    public void onDeleteCategory(Category category) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete \"" + category.getCategoryName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteCategory(category.getId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onViewCategory(Category category) {
        // Gọi API để lấy chi tiết category và hiển thị dialog view
        viewModel.getCategoryById(category.getId());
        showViewCategoryDialog(category);
    }

    private void showViewCategoryDialog(Category category) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_view_category, null);

        // Khởi tạo các view trong dialog - chỉ cần ID và Name
        android.widget.TextView tvCategoryId = dialogView.findViewById(R.id.tvCategoryId);
        android.widget.TextView tvCategoryName = dialogView.findViewById(R.id.tvCategoryName);

        // Điền thông tin category - chỉ ID và categoryName
        tvCategoryId.setText(String.valueOf(category.getId()));
        tvCategoryName.setText(category.getCategoryName());

        // Hiển thị dialog
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Category Details")
            .setView(dialogView)
            .setPositiveButton("Close", null)
            .setNeutralButton("Edit", (dialog, which) -> {
                dialog.dismiss();
                showEditCategoryDialog(category);
            })
            .show();
    }

    private void showAddCategoryDialog() {
        AdminCategoryDialogFragment dialog = AdminCategoryDialogFragment.newInstance(null);
        dialog.setOnCategorySavedListener(category -> viewModel.addCategory(category));
        dialog.show(getParentFragmentManager(), "AddCategoryDialog");
    }

    private void showEditCategoryDialog(Category category) {
        AdminCategoryDialogFragment dialog = AdminCategoryDialogFragment.newInstance(category);
        dialog.setOnCategorySavedListener(updatedCategory ->
            viewModel.updateCategory(updatedCategory.getId(), updatedCategory));
        dialog.show(getParentFragmentManager(), "EditCategoryDialog");
    }

    @Override
    public void onDestroy() {
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        super.onDestroy();
    }
}
