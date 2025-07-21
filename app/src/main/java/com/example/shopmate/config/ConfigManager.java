package com.example.shopmate.config;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigManager {
    
    private static final String PREF_NAME = "app_config";
    private static final String KEY_SERVER_HOST = "server_host";
    private static final String KEY_SERVER_PORT = "server_port";
    
    private static ConfigManager instance;
    private SharedPreferences preferences;
    
    private ConfigManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized ConfigManager getInstance(Context context) {
        if (instance == null) {
            instance = new ConfigManager(context.getApplicationContext());
        }
        return instance;
    }
    
    // Getter methods
    public String getServerHost() {
        return preferences.getString(KEY_SERVER_HOST, Constants.SERVER_HOST);
    }

    public String getServerPort() {
        return preferences.getString(KEY_SERVER_PORT, Constants.SERVER_PORT);
    }
    
    public String getBaseUrl() {
        return "http://" + getServerHost() + ":" + getServerPort() + "/";
    }
    
    public String getApiBaseUrl() {
        // Nếu Constants.BASE_URL đã được set (deployment), sử dụng nó
        if (Constants.BASE_URL.startsWith("https://")) {
            return Constants.BASE_URL + Constants.API_VERSION + "/";
        }
        // Ngược lại sử dụng local config
        return getBaseUrl() + Constants.API_VERSION + "/";
    }
    
    // Setter methods (for dynamic configuration)
    public void setServerHost(String host) {
        preferences.edit().putString(KEY_SERVER_HOST, host).apply();
    }
    
    public void setServerPort(String port) {
        preferences.edit().putString(KEY_SERVER_PORT, port).apply();
    }
    
    // Reset to default
    public void resetToDefault() {
        preferences.edit()
                .putString(KEY_SERVER_HOST, Constants.SERVER_HOST)
                .putString(KEY_SERVER_PORT, Constants.SERVER_PORT)
                .apply();
    }

    // Check if using custom configuration
    public boolean isUsingCustomConfig() {
        return !getServerHost().equals(Constants.SERVER_HOST) ||
               !getServerPort().equals(Constants.SERVER_PORT);
    }
}
