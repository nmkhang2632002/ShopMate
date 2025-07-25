package com.example.shopmate.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.shopmate.R;
import com.example.shopmate.util.AuthManager;
import com.example.shopmate.viewmodel.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout usernameInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout confirmPasswordInputLayout;
    private TextInputLayout phoneInputLayout;
    private TextInputLayout addressInputLayout;
    
    private TextInputEditText usernameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;
    private TextInputEditText phoneEditText;
    private TextInputEditText addressEditText;
    
    private MaterialButton registerButton;
    private TextView loginTextView;
    private TextView errorTextView;
    private ProgressBar progressBar;
    private View backButton;
    
    private AuthViewModel authViewModel;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        initViews();
        initViewModel();
        setupObservers();
        setupClickListeners();
    }
    
    private void initViews() {
        usernameInputLayout = findViewById(R.id.usernameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);
        phoneInputLayout = findViewById(R.id.phoneInputLayout);
        addressInputLayout = findViewById(R.id.addressInputLayout);
        
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);
        errorTextView = findViewById(R.id.errorTextView);
        progressBar = findViewById(R.id.progressBar);
        backButton = findViewById(R.id.backButton);
        
        authManager = AuthManager.getInstance(this);
    }
    
    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }
    
    private void setupObservers() {
        authViewModel.getRegisterResult().observe(this, loginResponse -> {
            if (loginResponse != null && loginResponse.isAuthenticated()) {
                authManager.saveLoginData(loginResponse);
                showSuccessMessage("Account created successfully!");
                navigateBasedOnUserRole();
            }
        });
        
        authViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                registerButton.setEnabled(!isLoading);
                registerButton.setText(isLoading ? "Creating account..." : "Create Account");
                
                // Disable input fields during loading
                setInputFieldsEnabled(!isLoading);
                
                // Hide error during loading
                if (isLoading) {
                    hideError();
                }
            }
        });
        
        authViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                showError(getCustomErrorMessage(errorMessage));
            }
        });
        
        // Observe validation errors
        authViewModel.getValidationError().observe(this, validationError -> {
            if (validationError != null && !validationError.isEmpty()) {
                showError(validationError);
                
                // Set specific field errors based on the validation message
                if (validationError.toLowerCase().contains("username")) {
                    usernameInputLayout.setError(validationError);
                } else if (validationError.toLowerCase().contains("email")) {
                    emailInputLayout.setError(validationError);
                } else if (validationError.toLowerCase().contains("password") && 
                          !validationError.toLowerCase().contains("confirm")) {
                    passwordInputLayout.setError(validationError);
                } else if (validationError.toLowerCase().contains("confirm password") || 
                          validationError.toLowerCase().contains("passwords do not match")) {
                    confirmPasswordInputLayout.setError(validationError);
                } else if (validationError.toLowerCase().contains("phone")) {
                    phoneInputLayout.setError(validationError);
                } else if (validationError.toLowerCase().contains("address")) {
                    addressInputLayout.setError(validationError);
                }
            }
        });
    }
    
    private void setupClickListeners() {
        registerButton.setOnClickListener(v -> {
            if (validateInputs()) {
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                
                // Truyền raw password, để AuthViewModel tự xử lý hash
                authViewModel.register(username, password, email, phone, address);
            }
        });
        
        loginTextView.setOnClickListener(v -> {
            finish();
        });
        
        backButton.setOnClickListener(v -> {
            finish();
        });
    }
    
    private boolean validateInputs() {
        boolean isValid = true;
        
        // Reset errors
        clearInputErrors();
        
        // Validate username
        String username = usernameEditText.getText().toString().trim();
        if (username.isEmpty()) {
            usernameInputLayout.setError("Username is required");
            isValid = false;
        } else if (username.length() < 3) {
            usernameInputLayout.setError("Username must be at least 3 characters");
            isValid = false;
        }
        
        // Validate email
        String email = emailEditText.getText().toString().trim();
        if (email.isEmpty()) {
            emailInputLayout.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError("Please enter a valid email address");
            isValid = false;
        }
        
        // Validate password
        String password = passwordEditText.getText().toString().trim();
        if (password.isEmpty()) {
            passwordInputLayout.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordInputLayout.setError("Password must be at least 6 characters");
            isValid = false;
        }
        
        // Validate confirm password
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        if (confirmPassword.isEmpty()) {
            confirmPasswordInputLayout.setError("Please confirm your password");
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            confirmPasswordInputLayout.setError("Passwords do not match");
            isValid = false;
        }
        
        // Validate phone
        String phone = phoneEditText.getText().toString().trim();
        if (phone.isEmpty()) {
            phoneInputLayout.setError("Phone number is required");
            isValid = false;
        } else if (phone.length() < 10) {
            phoneInputLayout.setError("Please enter a valid phone number");
            isValid = false;
        }
        
        // Validate address
        String address = addressEditText.getText().toString().trim();
        if (address.isEmpty()) {
            addressInputLayout.setError("Address is required");
            isValid = false;
        } else if (address.length() < 3) {
            addressInputLayout.setError("Please enter a valid address");
            isValid = false;
        }
        
        return isValid;
    }
    
    private void clearInputErrors() {
        usernameInputLayout.setError(null);
        emailInputLayout.setError(null);
        passwordInputLayout.setError(null);
        confirmPasswordInputLayout.setError(null);
        phoneInputLayout.setError(null);
        addressInputLayout.setError(null);
    }
    
    private void setInputFieldsEnabled(boolean enabled) {
        usernameEditText.setEnabled(enabled);
        emailEditText.setEnabled(enabled);
        passwordEditText.setEnabled(enabled);
        confirmPasswordEditText.setEnabled(enabled);
        phoneEditText.setEnabled(enabled);
        addressEditText.setEnabled(enabled);
    }
    
    private void showError(String message) {
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
    }
    
    private void hideError() {
        errorTextView.setVisibility(View.GONE);
    }
    
    private String getCustomErrorMessage(String serverMessage) {
        if (serverMessage != null && !serverMessage.trim().isEmpty()) {
            // Check for common error patterns and provide user-friendly messages
            if (serverMessage.toLowerCase().contains("email") && 
                serverMessage.toLowerCase().contains("exist")) {
                return "This email is already registered. Please use a different email.";
            } else if (serverMessage.toLowerCase().contains("username") && 
                      serverMessage.toLowerCase().contains("exist")) {
                return "This username is already taken. Please choose a different one.";
            } else if (serverMessage.toLowerCase().contains("network") ||
                      serverMessage.toLowerCase().contains("connection")) {
                return "Network error. Please check your connection.";
            } else if (serverMessage.toLowerCase().contains("timeout")) {
                return "Request timeout. Please try again.";
            } else {
                return serverMessage;
            }
        } else {
            // Default custom message when server doesn't provide one
            return "Registration failed. Please try again.";
        }
    }
    
    private void showSuccessMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateBasedOnUserRole() {
        if (isAdmin()) {
            navigateToAdminActivity();
        } else {
            navigateToMainActivity();
        }
    }

    private boolean isAdmin() {
        // Check if current user is admin (user ID 40 according to README)
        return authManager.getCurrentUser() != null &&
               authManager.getCurrentUser().getId() == 40;
    }

    private void navigateToAdminActivity() {
        Intent intent = new Intent(this, com.example.shopmate.ui.activities.AdminActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
