package com.example.shopmate.util;

import android.util.Log;
import com.example.shopmate.config.Constants;

public class ImageUtils {
    private static final String TAG = "ImageUtils";
    
    /**
     * Convert relative image URL to full URL
     * @param imageUrl The image URL from API (could be relative or absolute)
     * @return Full image URL
     */
    public static String getFullImageUrl(String imageUrl) {
        Log.d(TAG, "Original imageUrl: " + imageUrl);
        
        if (imageUrl == null || imageUrl.isEmpty()) {
            Log.w(TAG, "Image URL is null or empty");
            return null;
        }
        
        // If already a full URL (starts with http or https), return as is
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            Log.d(TAG, "URL is already full: " + imageUrl);
            return imageUrl;
        }
        
        // If it's a relative URL, prepend the base URL
        String baseUrl = Constants.BASE_URL;
        Log.d(TAG, "Base URL: " + baseUrl);
        
        // Remove trailing slash from base URL if present
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        
        // Ensure imageUrl starts with /
        if (!imageUrl.startsWith("/")) {
            imageUrl = "/" + imageUrl;
        }
        
        String fullUrl = baseUrl + imageUrl;
        Log.d(TAG, "Generated full URL: " + fullUrl);
        return fullUrl;
    }
}
