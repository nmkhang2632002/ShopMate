package com.example.shopmate.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.shopmate.R;
import com.example.shopmate.data.model.Category;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AdminCategoryDialogFragment extends DialogFragment {

    private static final String ARG_CATEGORY = "category";

    private Category category;
    private OnCategorySavedListener listener;

    private TextInputEditText etCategoryName;
    private TextInputLayout tilCategoryName;
    private MaterialButton btnSave;
    private MaterialButton btnCancel;

    public interface OnCategorySavedListener {
        void onCategorySaved(Category category);
    }

    public static AdminCategoryDialogFragment newInstance(Category category) {
        AdminCategoryDialogFragment fragment = new AdminCategoryDialogFragment();
        Bundle args = new Bundle();
        if (category != null) {
            args.putSerializable(ARG_CATEGORY, category);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = (Category) getArguments().getSerializable(ARG_CATEGORY);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_category, null);

        initViews(view);

        if (category != null) {
            populateFields();
        }

        setupClickListeners();

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .create();
    }

    private void initViews(View view) {
        etCategoryName = view.findViewById(R.id.etCategoryName);

        tilCategoryName = view.findViewById(R.id.tilCategoryName);

        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);
    }

    private void populateFields() {
        etCategoryName.setText(category.getCategoryName());

        btnSave.setText("Update Category");
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveCategory());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void saveCategory() {
        if (!validateInputs()) {
            return;
        }

        String categoryName = etCategoryName.getText().toString().trim();

        Category newCategory;
        if (category != null) {
            // Update existing category
            newCategory = new Category(
                category.getId(),
                categoryName
            );
        } else {
            // Create new category
            newCategory = new Category(
                0, // ID will be assigned by server
                categoryName
            );
        }

        if (listener != null) {
            listener.onCategorySaved(newCategory);
        }

        dismiss();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Clear previous errors
        clearErrors();

        String categoryName = etCategoryName.getText().toString().trim();
        if (categoryName.isEmpty()) {
            tilCategoryName.setError("Category name is required");
            isValid = false;
        } else if (categoryName.length() < 2) {
            tilCategoryName.setError("Category name must be at least 2 characters");
            isValid = false;
        }

        return isValid;
    }

    private void clearErrors() {
        tilCategoryName.setError(null);
    }

    public void setOnCategorySavedListener(OnCategorySavedListener listener) {
        this.listener = listener;
    }
}
