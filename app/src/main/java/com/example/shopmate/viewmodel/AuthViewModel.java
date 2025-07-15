package com.example.shopmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopmate.data.model.LoginResponse;
import com.example.shopmate.data.repository.AuthRepository;

public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private LiveData<LoginResponse> loginResult;
    private LiveData<Boolean> logoutResult;

    public AuthViewModel() {
        authRepository = AuthRepository.getInstance();
    }

    public LiveData<LoginResponse> getLoginResult() {
        return loginResult;
    }
    
    public LiveData<Boolean> getLogoutResult() {
        return logoutResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return authRepository.getIsLoading();
    }

    public LiveData<String> getErrorMessage() {
        return authRepository.getErrorMessage();
    }

    public void login(String email, String password) {
        // Validate input
        if (email == null || email.trim().isEmpty()) {
            // Handle validation error - could set a validation error state
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            // Handle validation error - could set a validation error state
            return;
        }

        if (!isValidEmail(email)) {
            // Handle validation error - could set a validation error state
            return;
        }

        if (password.length() < 4) {
            // Handle validation error - could set a validation error state
            return;
        }

        loginResult = authRepository.login(email, password);
    }
    
    public void logout(String token) {
        if (token == null || token.trim().isEmpty()) {
            // Handle validation error - could set a validation error state
            return;
        }
        
        logoutResult = authRepository.logout(token);
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
} 