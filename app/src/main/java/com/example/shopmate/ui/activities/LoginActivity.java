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

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private MaterialButton loginButton;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private AuthViewModel authViewModel;
    private AuthManager authManager;
    private MaterialButton registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initViewModel();
        checkIfAlreadyLoggedIn();
        setupObservers();
        setupClickListeners();
    }

    private void initViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);
        registerButton = findViewById(R.id.registerButton);
        authManager = AuthManager.getInstance(this);
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void checkIfAlreadyLoggedIn() {
        if (authManager.isLoggedIn()) {
            navigateToMainActivity();
        }
    }

    private void setupObservers() {
        authViewModel.getLoginResult().observe(this, loginResponse -> {
            if (loginResponse != null && loginResponse.isAuthenticated()) {
                authManager.saveLoginData(loginResponse);
                showSuccessMessage("Welcome back, " + loginResponse.getUser().getUsername() + "!");
                navigateToMainActivity();
            }
        });

        authViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                loginButton.setEnabled(!isLoading);
                loginButton.setText(isLoading ? "Signing in..." : "Sign In");
                
                // Disable input fields during loading
                emailEditText.setEnabled(!isLoading);
                passwordEditText.setEnabled(!isLoading);
                
                // Hide error during loading
                if (isLoading) {
                    hideError();
                }
            }
        });

        authViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                showError(getCustomErrorMessage(errorMessage));
                clearInputErrors();
            }
        });
        
        // Observe validation errors
        authViewModel.getValidationError().observe(this, validationError -> {
            if (validationError != null && !validationError.isEmpty()) {
                showError(validationError);
                
                // Set specific field errors based on the validation message
                if (validationError.toLowerCase().contains("email")) {
                    emailInputLayout.setError(validationError);
                } else if (validationError.toLowerCase().contains("password")) {
                    passwordInputLayout.setError(validationError);
                }
            }
        });
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> {
            clearInputErrors();
            hideError();
            
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            
            // Let the ViewModel handle validation
            authViewModel.login(email, password);
        });
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void clearInputErrors() {
        emailInputLayout.setError(null);
        passwordInputLayout.setError(null);
    }

    private void showError(String message) {
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        errorTextView.setVisibility(View.GONE);
    }

    private String getCustomErrorMessage(String serverMessage) {
        // If server provides a message, use it; otherwise use custom message
        if (serverMessage != null && !serverMessage.trim().isEmpty()) {
            // Check for common error patterns and provide user-friendly messages
            if (serverMessage.toLowerCase().contains("password is incorrect")) {
                return "Incorrect password. Please try again.";
            } else if (serverMessage.toLowerCase().contains("invalid") || 
                serverMessage.toLowerCase().contains("incorrect") ||
                serverMessage.toLowerCase().contains("wrong")) {
                return "Invalid email or password. Please try again.";
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
            return "Login failed. Please check your credentials and try again.";
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

    // Static utility methods for other activities
    public static void logout(android.content.Context context) {
        // Clear all SharedPreferences data
        AuthManager.getInstance(context).logout();
        
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
} 