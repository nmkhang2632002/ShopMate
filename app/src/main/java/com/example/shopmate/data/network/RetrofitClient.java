package com.example.shopmate.data.network;

import android.content.Context;

import com.example.shopmate.util.AuthManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://saleapp-mspd.onrender.com/v1/";
    private static Retrofit retrofit;
    private static Context context;

    private RetrofitClient() {}
    
    public static void initialize(Context appContext) {
        context = appContext.getApplicationContext();
        retrofit = null; // Reset retrofit instance when context changes
    }
    
    public static Retrofit getInstance() {
        if (retrofit == null) {
            // Optional: add logging
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .addInterceptor(interceptor);

            // Add authentication interceptor if context is available
            if (context != null) {
                clientBuilder.addInterceptor(new AuthInterceptor());
            }

            OkHttpClient client = clientBuilder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
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
