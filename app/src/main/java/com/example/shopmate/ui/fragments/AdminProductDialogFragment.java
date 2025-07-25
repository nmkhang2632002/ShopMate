package com.example.shopmate.ui.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.shopmate.R;
import com.example.shopmate.data.model.Category;
import com.example.shopmate.data.model.Product;
import com.example.shopmate.viewmodel.AdminCategoryViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class AdminProductDialogFragment extends DialogFragment {

    private static final String ARG_PRODUCT = "product";

    private Product product;
    private OnProductSavedListener listener;
    private AdminCategoryViewModel categoryViewModel;

    private TextInputEditText etProductName;
    private TextInputEditText etBriefDescription;
    private TextInputEditText etFullDescription;
    private TextInputEditText etTechnicalSpecifications;
    private TextInputEditText etPrice;
    private TextInputEditText etImageURL;
    private AutoCompleteTextView actvCategory;
    private ImageView ivImagePreview;
    private MaterialButton btnSelectImage;
    private TextInputLayout tilProductName, tilBriefDescription, tilFullDescription, tilTechnicalSpecifications, tilPrice, tilImageURL, tilCategory;
    private MaterialButton btnSave;
    private MaterialButton btnCancel;

    // Image handling variables
    private Uri selectedImageUri;
    private String uploadedImageUrl;
    private boolean isUploadingImage = false;

    // Activity result launcher for image selection
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private List<Category> categories = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;

    public interface OnProductSavedListener {
        void onProductSaved(Product product);
    }

    public static AdminProductDialogFragment newInstance(Product product) {
        AdminProductDialogFragment fragment = new AdminProductDialogFragment();
        Bundle args = new Bundle();
        if (product != null) {
            args.putSerializable(ARG_PRODUCT, product);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable(ARG_PRODUCT);
        }
        categoryViewModel = new ViewModelProvider(this).get(AdminCategoryViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_product, null);

        initViews(view);
        setupCategoryDropdown();
        setupObservers();
        loadCategories();

        if (product != null) {
            populateFields();
        }

        setupClickListeners();

        // Register image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == -1 && result.getData() != null) {
                        handleImageResult(result.getData());
                    }
                }
        );

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .create();
    }

    private void initViews(View view) {
        etProductName = view.findViewById(R.id.etProductName);
        etBriefDescription = view.findViewById(R.id.etBriefDescription);
        etFullDescription = view.findViewById(R.id.etFullDescription);
        etTechnicalSpecifications = view.findViewById(R.id.etTechnicalSpecifications);
        etPrice = view.findViewById(R.id.etPrice);
        etImageURL = view.findViewById(R.id.etImageURL);
        actvCategory = view.findViewById(R.id.actvCategory);
        ivImagePreview = view.findViewById(R.id.ivImagePreview);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);

        tilProductName = view.findViewById(R.id.tilProductName);
        tilBriefDescription = view.findViewById(R.id.tilBriefDescription);
        tilFullDescription = view.findViewById(R.id.tilFullDescription);
        tilTechnicalSpecifications = view.findViewById(R.id.tilTechnicalSpecifications);
        tilPrice = view.findViewById(R.id.tilPrice);
        tilImageURL = view.findViewById(R.id.tilImageURL);
        tilCategory = view.findViewById(R.id.tilCategory);

        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);
    }

    private void setupCategoryDropdown() {
        categoryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        actvCategory.setAdapter(categoryAdapter);
    }

    private void setupObservers() {
        categoryViewModel.getCategories().observe(this, categoryList -> {
            if (categoryList != null) {
                categories.clear();
                categories.addAll(categoryList);

                List<String> categoryNames = new ArrayList<>();
                for (Category category : categoryList) {
                    categoryNames.add(category.getCategoryName());
                }

                categoryAdapter.clear();
                categoryAdapter.addAll(categoryNames);
                categoryAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadCategories() {
        categoryViewModel.loadCategories();
    }

    private void populateFields() {
        etProductName.setText(product.getProductName());
        etBriefDescription.setText(product.getBriefDescription());
        etFullDescription.setText(product.getFullDescription());

        // Handle technicalSpecifications safely
        String techSpecs = product.getTechnicalSpecifications();
        etTechnicalSpecifications.setText(techSpecs != null ? techSpecs : "");

        etPrice.setText(String.valueOf(product.getPrice()));
        etImageURL.setText(product.getImageURL());

        if (product.getCategoryName() != null) {
            actvCategory.setText(product.getCategoryName(), false);
        }

        btnSave.setText("Update Product");

        // Load image preview
        Glide.with(this)
                .load(product.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivImagePreview);
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveProduct());
        btnCancel.setOnClickListener(v -> dismiss());
        btnSelectImage.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void handleImageResult(Intent data) {
        selectedImageUri = data.getData();
        if (selectedImageUri != null) {
            // Show image preview
            ivImagePreview.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(selectedImageUri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivImagePreview);

            // Clear URL field when image is selected from device
            etImageURL.setText("");

            // Update button text to indicate image is selected
            btnSelectImage.setText("✓ Image Selected - Tap to change");

            // Clear any previous errors
            tilImageURL.setError(null);

            Toast.makeText(getContext(), "Image selected successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProduct() {
        if (!validateInputs()) {
            return;
        }

        String productName = etProductName.getText().toString().trim();
        String briefDescription = etBriefDescription.getText().toString().trim();
        String fullDescription = etFullDescription.getText().toString().trim();
        String technicalSpecifications = etTechnicalSpecifications.getText().toString().trim();
        String priceText = etPrice.getText().toString().trim();
        String imageURL = etImageURL.getText().toString().trim();
        String categoryName = actvCategory.getText().toString().trim();

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            tilPrice.setError("Invalid price format");
            return;
        }

        // Determine final image URL
        String finalImageUrl = imageURL;
        if (selectedImageUri != null && imageURL.isEmpty()) {
            // If user selected image from device but no URL provided,
            // use the selected image URI as string (for now)
            // In a real app, you would upload this to a server first
            finalImageUrl = selectedImageUri.toString();
        }

        // Find category ID
        int categoryId = 0;
        for (Category category : categories) {
            if (category.getCategoryName().equals(categoryName)) {
                categoryId = category.getId();
                break;
            }
        }

        Product newProduct;
        if (product != null) {
            // Update existing product
            newProduct =  new Product(
                product.getId(),
                productName,
                briefDescription,
                fullDescription,
                technicalSpecifications,
                price,
                finalImageUrl,
                categoryId,
                categoryName
            );
        } else {
            // Create new product
            newProduct = new Product(
                0, // ID will be assigned by server
                productName,
                briefDescription,
                fullDescription,
                technicalSpecifications,
                price,
                finalImageUrl,
                categoryId,
                categoryName
            );
        }

        if (listener != null) {
            listener.onProductSaved(newProduct);
        }

        dismiss();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Clear previous errors
        clearErrors();

        String productName = etProductName.getText().toString().trim();
        if (productName.isEmpty()) {
            tilProductName.setError("Product name is required");
            isValid = false;
        }

        String briefDescription = etBriefDescription.getText().toString().trim();
        if (briefDescription.isEmpty()) {
            tilBriefDescription.setError("Brief description is required");
            isValid = false;
        }

        String fullDescription = etFullDescription.getText().toString().trim();
        if (fullDescription.isEmpty()) {
            tilFullDescription.setError("Full description is required");
            isValid = false;
        }

        String priceText = etPrice.getText().toString().trim();
        if (priceText.isEmpty()) {
            tilPrice.setError("Price is required");
            isValid = false;
        } else {
            try {
                double price = Double.parseDouble(priceText);
                if (price <= 0) {
                    tilPrice.setError("Price must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilPrice.setError("Invalid price format");
                isValid = false;
            }
        }

        // Image validation - chỉ yêu cầu 1 trong 2: chọn ảnh từ thiết bị HOẶC nhập URL
        String imageURL = etImageURL.getText().toString().trim();
        boolean hasImageUrl = !imageURL.isEmpty();
        boolean hasSelectedImage = selectedImageUri != null;

        if (!hasImageUrl && !hasSelectedImage) {
            tilImageURL.setError("Please select an image from device OR enter an image URL");
            isValid = false;
        } else {
            // Clear error if at least one option is provided
            tilImageURL.setError(null);
        }

        String categoryName = actvCategory.getText().toString().trim();
        if (categoryName.isEmpty()) {
            tilCategory.setError("Category is required");
            isValid = false;
        }

        return isValid;
    }

    private void clearErrors() {
        tilProductName.setError(null);
        tilBriefDescription.setError(null);
        tilFullDescription.setError(null);
        tilTechnicalSpecifications.setError(null);
        tilPrice.setError(null);
        tilImageURL.setError(null);
        tilCategory.setError(null);
    }

    public void setOnProductSavedListener(OnProductSavedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
}
