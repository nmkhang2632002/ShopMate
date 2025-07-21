package com.example.shopmate.config;

public class Constants {

    // ========== SERVER CONFIGURATION ==========
    // Local server config (always available for ConfigManager)
    public static final String SERVER_HOST = "192.168.1.52";
    public static final String SERVER_PORT = "8080";

    // 🔧 DEPLOYMENT: Uncomment for production deployment
    public static final String BASE_URL = "https://saleapp-mspd.onrender.com/";

    // 🔧 LOCAL: Uncomment for local development
    // public static final String BASE_URL = "http://" + SERVER_HOST + ":" + SERVER_PORT + "/";

    // API endpoints
    public static final String API_VERSION = "v1";
    
    // Request timeout
    public static final int CONNECT_TIMEOUT = 30; // seconds
    public static final int READ_TIMEOUT = 30; // seconds
    public static final int WRITE_TIMEOUT = 30; // seconds
    
    // Deep link
    public static final String DEEP_LINK_SCHEME = "shopmate";
    public static final String PAYMENT_RESULT_HOST = "payment-result";
    public static final String MOBILE_RETURN_URL = DEEP_LINK_SCHEME + "://" + PAYMENT_RESULT_HOST;
}
