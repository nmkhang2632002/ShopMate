package com.example.shopmate.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.LoginRequest;
import com.example.shopmate.data.model.LoginResponse;
import com.example.shopmate.data.model.LogoutRequest;
import com.example.shopmate.data.network.AuthApi;
import com.example.shopmate.data.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private static AuthRepository instance;
    private final AuthApi authApi;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private AuthRepository() {
        authApi = RetrofitClient.getInstance().create(AuthApi.class);
    }

    public static synchronized AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<LoginResponse> login(String email, String password) {
        MutableLiveData<LoginResponse> loginData = new MutableLiveData<>();
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        LoginRequest request = new LoginRequest(email, password);
        Call<ApiResponse<LoginResponse>> call = authApi.login(request);
        
        call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                isLoading.setValue(false);
                ApiResponse<LoginResponse> apiResponse = response.body();
                if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = apiResponse.getData();
                        if (loginResponse.isAuthenticated() && loginResponse.getUser() != null) {
                            loginData.setValue(loginResponse);
                            Log.d(TAG, "Login successful for user: " + loginResponse.getUser().getUsername());
                        } else {
                            errorMessage.setValue("Login failed: " + response.body().getMessage());
                            loginData.setValue(null);
                            Log.e(TAG, "Login failed: Authentication not successful or user data missing");
                        }
                } else {
                    errorMessage.setValue("Network Error: " + response.code());
                    loginData.setValue(null);
                    Log.e(TAG, "Login API call failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network Error: " + t.getMessage());
                loginData.setValue(null);
                Log.e(TAG, "Login API call failed", t);
            }
        });
        
        return loginData;
    }
    
    public LiveData<Boolean> logout(String token) {
        MutableLiveData<Boolean> logoutResult = new MutableLiveData<>();
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        LogoutRequest request = new LogoutRequest(token);
        Call<ApiResponse<Void>> call = authApi.logout(request);
        
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                isLoading.setValue(false);
             
                    ApiResponse<Void> apiResponse = response.body();
                    if (!response.isSuccessful() || response.body() == null) {
                        errorMessage.setValue("Logout failed: " + apiResponse.getMessage());
                        logoutResult.setValue(false);
                        return;
                    }
                    logoutResult.setValue(true);
                
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network Error: " + t.getMessage());
                logoutResult.setValue(false);
                Log.e(TAG, "Logout API call failed", t);
            }
        });
        
        return logoutResult;
    }
} 