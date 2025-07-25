package com.example.shopmate.viewmodel;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopmate.data.model.LoginResponse;
import com.example.shopmate.data.repository.AuthRepository;

public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private MutableLiveData<LoginResponse> loginResult;
    private MutableLiveData<Boolean> logoutResult;
    private MutableLiveData<LoginResponse> registerResult;
    private MutableLiveData<String> validationError = new MutableLiveData<>();

    public AuthViewModel() {
        authRepository = AuthRepository.getInstance();
        loginResult = new MutableLiveData<>();
        logoutResult = new MutableLiveData<>();
        registerResult = new MutableLiveData<>();
    }

    public LiveData<LoginResponse> getLoginResult() {
        return loginResult;
    }
    
    public LiveData<Boolean> getLogoutResult() {
        return logoutResult;
    }
    
    public LiveData<LoginResponse> getRegisterResult() {
        return registerResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return authRepository.getIsLoading();
    }

    public LiveData<String> getErrorMessage() {
        return authRepository.getErrorMessage();
    }
    
    public LiveData<String> getValidationError() {
        return validationError;
    }

    public void login(String email, String password) {
        // Clear previous validation errors
        validationError.setValue(null);
        
        // Validate input
        if (email == null || email.trim().isEmpty()) {
            validationError.setValue("Email is required");
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            validationError.setValue("Password is required");
            return;
        }

        if (!isValidEmail(email)) {
            validationError.setValue("Please enter a valid email address");
            return;
        }

        if (password.length() < 4) {
            validationError.setValue("Password must be at least 4 characters");
            return;
        }

        // Call repository and observe the result
        authRepository.login(email, password).observeForever(response -> {
            loginResult.setValue(response);
        });
    }
    
    public void logout() {
        // Clear previous validation errors
        validationError.setValue(null);
        
        // Call repository and observe the result
        authRepository.logout().observeForever(response -> {
            logoutResult.setValue(response);
        });
    }
    
    public void register(String username, String password, String email, 
                        String phoneNumber, String address) {
        // Clear previous validation errors
        validationError.setValue(null);
        
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            validationError.setValue("Username is required");
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            validationError.setValue("Password is required");
            return;
        }
        
        if (email == null || email.trim().isEmpty()) {
            validationError.setValue("Email is required");
            return;
        }
        
        if (!isValidEmail(email)) {
            validationError.setValue("Please enter a valid email address");
            return;
        }
        
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            validationError.setValue("Phone number is required");
            return;
        }
        
        if (address == null || address.trim().isEmpty()) {
            validationError.setValue("Address is required");
            return;
        }
        
        // Gửi raw password lên server (không hash)
        authRepository.register(username, password, email, phoneNumber, address).observeForever(response -> {
            registerResult.setValue(response);
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
