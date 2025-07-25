package com.example.shopmate.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.shopmate.data.model.LoginResponse;
import com.example.shopmate.data.model.User;

public class AuthManager {
    private static final String PREFS_NAME = "ShopMatePrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_ROLE = "role";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private static AuthManager instance;
    private SharedPreferences prefs;

    private AuthManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveLoginData(LoginResponse loginResponse) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ACCESS_TOKEN, loginResponse.getAccessToken());
        editor.putInt(KEY_USER_ID, loginResponse.getUser().getId());
        editor.putString(KEY_USERNAME, loginResponse.getUser().getUsername());
        editor.putString(KEY_EMAIL, loginResponse.getUser().getEmail());
        editor.putString(KEY_PHONE, loginResponse.getUser().getPhoneNumber());
        editor.putString(KEY_ADDRESS, loginResponse.getUser().getAddress());
        editor.putString(KEY_ROLE, loginResponse.getUser().getRole());
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public void logout() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getPhoneNumber() {
        return prefs.getString(KEY_PHONE, null);
    }

    public String getAddress() {
        return prefs.getString(KEY_ADDRESS, null);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    // Helper method to get Authorization header for API calls
    public String getAuthorizationHeader() {
        String token = getAccessToken();
        return token != null ? "Bearer " + token : null;
    }

    // Helper method to check if user has specific role
    public boolean hasRole(String role) {
        return role != null && role.equals(getRole());
    }

    // Helper method to check if user is customer
    public boolean isCustomer() {
        return hasRole("CUSTOMER");
    }

    // Helper method to get current user as User object
    public User getCurrentUser() {
        if (!isLoggedIn()) {
            return null;
        }

        User user = new User();
        user.setId(getUserId());
        user.setUsername(getUsername());
        user.setEmail(getEmail());
        user.setPhoneNumber(getPhoneNumber());
        user.setAddress(getAddress());
        user.setRole(getRole());

        return user;
    }
}

