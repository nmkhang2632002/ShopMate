package com.example.shopmate.data.network;

import android.content.Context;

import com.example.shopmate.util.AuthManager;
import com.example.shopmate.config.Constants;
import com.example.shopmate.config.ConfigManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static Context context;

    private RetrofitClient() {}
    
    public static void initialize(Context appContext) {
        context = appContext.getApplicationContext();
        retrofit = null; // Reset retrofit instance when context changes
    }
    
    public static Retrofit getInstance() {
        if (retrofit == null) {
            // Ưu tiên sử dụng Constants.BASE_URL (cho deployment)
            String baseUrl;
            if (Constants.BASE_URL.startsWith("https://")) {
                // Deployment mode - sử dụng Constants.BASE_URL
                baseUrl = Constants.BASE_URL + Constants.API_VERSION + "/";
            } else if (context != null) {
                // Local mode - sử dụng ConfigManager
                ConfigManager configManager = ConfigManager.getInstance(context);
                baseUrl = configManager.getApiBaseUrl();
            } else {
                // Fallback
                baseUrl = Constants.BASE_URL + Constants.API_VERSION + "/";
            }

            // Optional: add logging
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS) // Set connection timeout
                    .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS) // Set read timeout
                    .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS); // Set write timeout

            // Add authentication interceptor if context is available
            if (context != null) {
                clientBuilder.addInterceptor(new AuthInterceptor());
            }

            OkHttpClient client = clientBuilder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    // Reset retrofit instance when configuration changes
    public static void resetInstance() {
        retrofit = null;
    }

    // Interceptor to add authentication headers
    private static class AuthInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            
            // Skip authentication for login endpoint
            if (original.url().encodedPath().contains("/auth/login")) {
                return chain.proceed(original);
            }
            
            // Add authentication header if available
            if (context != null) {
                AuthManager authManager = AuthManager.getInstance(context);
                String token = authManager.getAccessToken();
                
                if (token != null) {
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .method(original.method(), original.body());
                    
                    return chain.proceed(requestBuilder.build());
                }
            }
            
            return chain.proceed(original);
        }
    }
}
