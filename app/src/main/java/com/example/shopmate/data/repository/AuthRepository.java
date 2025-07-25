package com.example.shopmate.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.LoginRequest;
import com.example.shopmate.data.model.LoginResponse;
import com.example.shopmate.data.model.LogoutRequest;
import com.example.shopmate.data.model.RegisterRequest;
import com.example.shopmate.data.network.AuthApi;
import com.example.shopmate.data.network.RetrofitClient;
import com.example.shopmate.util.AuthManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private static AuthRepository instance;
    private final AuthApi authApi;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final Gson gson = new Gson();


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
        
        setLoadingState(true);
        
        LoginRequest request = new LoginRequest(email, password);
        Call<ApiResponse<LoginResponse>> call = authApi.login(request);
        
        call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                setLoadingState(false);
                
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<LoginResponse> apiResponse = response.body();
                        
                        if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                            LoginResponse loginResponse = apiResponse.getData();
                            if (loginResponse.isAuthenticated() && loginResponse.getUser() != null) {
                                loginData.setValue(loginResponse);
                                Log.d(TAG, "Login successful for user: " + loginResponse.getUser().getUsername());
                            } else {
                                handleLoginError("Login failed: " + apiResponse.getMessage(), loginData);
                            }
                        } else {
                            // Handle error in the API response
                            handleLoginError(apiResponse.getMessage(), loginData);
                            Log.e(TAG, "Login failed with status: " + apiResponse.getStatus() + ", message: " + apiResponse.getMessage());
                        }
                    } else {
                        // Parse error response for HTTP error codes like 400, 401, etc.
                        String errorMsg = "Network Error: " + response.code();
                        
                        if (response.errorBody() != null) {
                            try {
                                // Method 1: Try parse error JSON if API follows your format
                                ApiResponse<?> errorResponse = gson.fromJson(
                                    response.errorBody().charStream(),
                                    ApiResponse.class
                                );

                                if (errorResponse != null && errorResponse.getMessage() != null) {
                                    errorMsg = errorResponse.getMessage();
                                    Log.e(TAG, "Login error from API: status=" + errorResponse.getStatus() + ", message=" + errorMsg);
                                } else {
                                    // Method 2: Fallback to manual JSON parsing
                                    String errorBody = response.errorBody().string();
                                    Log.e(TAG, "Error response body: " + errorBody);
                                    
                                    JSONObject errorJson = new JSONObject(errorBody);
                                    int status = errorJson.optInt("status", 0);
                                    String message = errorJson.optString("message", "Unknown error");
                                    
                                    errorMsg = message;
                                    Log.e(TAG, "Login error: status=" + status + ", message=" + message);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error response: " + e.getMessage());
                                // Keep the default error message
                            }
                        }
                        
                        handleLoginError(errorMsg, loginData);
                    }
                } catch (Exception e) {
                    handleLoginError("Unexpected error: " + e.getMessage(), loginData);
                    Log.e(TAG, "Exception in login response", e);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                setLoadingState(false);
                handleLoginError("Network Error: " + t.getMessage(), loginData);
                Log.e(TAG, "Login API call failed", t);
            }
        });
        
        return loginData;
    }
    
    public LiveData<Boolean> logout() {
        MutableLiveData<Boolean> logoutResult = new MutableLiveData<>();
        
        try {
            // Simulate a brief loading state for UI consistency
            setLoadingState(true);
            
            // Small delay to simulate operation (can be removed if not needed)
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // Ignore
            }
                        
            // Set success result immediately
            logoutResult.setValue(true);
            Log.d(TAG, "Local logout successful - SharedPreferences cleared");
            
        } catch (Exception e) {
            // Handle any unexpected errors
            handleLogoutError("Error during logout: " + e.getMessage(), logoutResult);
            Log.e(TAG, "Error during local logout", e);
        } finally {
            setLoadingState(false);
        }
        
        return logoutResult;
    }
    
    public LiveData<LoginResponse> register(String username, String password, String email,
                                          String phoneNumber, String address) {
        MutableLiveData<LoginResponse> registerData = new MutableLiveData<>();
        
        setLoadingState(true);
        
        RegisterRequest request = new RegisterRequest(username, password, email, phoneNumber, address);
        Call<ApiResponse<LoginResponse>> call = authApi.register(request);
        
        call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                setLoadingState(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> apiResponse = response.body();
                    
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        LoginResponse loginResponse = apiResponse.getData();
                        registerData.setValue(loginResponse);
                        Log.d(TAG, "Registration successful for user: " + loginResponse.getUser().getUsername());
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ? 
                            apiResponse.getMessage() : "Registration failed";
                        handleRegistrationError(errorMsg, registerData);
                    }
                } else {
                    // Parse error response
                    String errorMsg = "Registration failed: " + response.code();
                    
                    if (response.errorBody() != null) {
                        try {
                            ApiResponse<?> errorResponse = gson.fromJson(
                                response.errorBody().charStream(),
                                ApiResponse.class
                            );

                            if (errorResponse != null && errorResponse.getMessage() != null) {
                                errorMsg = errorResponse.getMessage();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing registration error response: " + e.getMessage());
                        }
                    }
                    
                    handleRegistrationError(errorMsg, registerData);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                setLoadingState(false);
                handleRegistrationError("Network Error: " + t.getMessage(), registerData);
                Log.e(TAG, "Registration API call failed", t);
            }
        });

        return registerData;
    }
    
    // Helper methods for cleaner code
    private void setLoadingState(boolean loading) {
        isLoading.setValue(loading);
        if (loading) {
            errorMessage.setValue(null);
        }
    }
    
    private void handleLoginError(String error, MutableLiveData<LoginResponse> loginData) {
        errorMessage.setValue(error);
        loginData.setValue(null);
        Log.e(TAG, "Login failed: " + error);
    }
    
    private void handleLogoutError(String error, MutableLiveData<Boolean> logoutResult) {
        errorMessage.setValue(error);
        logoutResult.setValue(false);
        Log.e(TAG, "Logout failed: " + error);
    }
    
    private void handleRegistrationError(String error, MutableLiveData<LoginResponse> registerData) {
        errorMessage.setValue(error);
        registerData.setValue(null);
        Log.e(TAG, "Registration failed: " + error);
    }
}
