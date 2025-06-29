package com.example.shopmate.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.shopmate.MainActivity;
import com.example.shopmate.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private ImageView ivTogglePassword;
    private Button btnLogin;
    private TextView tvRegister;
    private boolean isPasswordVisible = false;
    private RequestQueue requestQueue;

    private static final String LOGIN_URL = "https://saleapp-mspd.onrender.com/v1/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        requestQueue = Volley.newRequestQueue(this);

        ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        btnLogin.setOnClickListener(v -> handleLogin());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivTogglePassword.setImageResource(R.drawable.ic_eye_off);
            isPasswordVisible = false;
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivTogglePassword.setImageResource(R.drawable.ic_eye);
            isPasswordVisible = true;
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    private void handleLogin() {
        clearErrors();

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        boolean isValid = true;

        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            isValid = false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        } else if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải từ 6 ký tự");
            isValid = false;
        }

        if (!isValid) return;

        doLogin(email, password);
    }

    private void doLogin(String email, String password) {
        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            showToast("Lỗi dữ liệu đầu vào");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, LOGIN_URL, params,
                response -> {
                    boolean success = false;
                    String message = "Đăng nhập thất bại";
                    String token = "";
                    try {
                        if (response.has("success")) {
                            success = response.getBoolean("success");
                        }
                        if (response.has("message")) {
                            message = response.getString("message");
                        }
                        if (response.has("access_token")) {
                            token = response.getString("access_token");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (success || !token.isEmpty()) {
                        SharedPreferences prefs = getSharedPreferences("ShopMatePrefs", MODE_PRIVATE);
                        prefs.edit()
                                .putBoolean("isLoggedIn", true)
                                .putString("token", token)
                                .apply();

                        showToast("Đăng nhập thành công");
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Gợi ý: nếu message liên quan đến email hoặc password, có thể setError cho trường đó
                        if (message.toLowerCase().contains("email")) {
                            etEmail.setError(message);
                        } else if (message.toLowerCase().contains("mật khẩu") || message.toLowerCase().contains("password")) {
                            etPassword.setError(message);
                        } else {
                            showToast(message);
                        }
                    }
                },
                error -> {
                    String errorMsg = "Đăng nhập thất bại";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMsg = new String(error.networkResponse.data);
                    }
                    showToast(errorMsg);
                }
        );

        requestQueue.add(request);
    }

    private void clearErrors() {
        etEmail.setError(null);
        etPassword.setError(null);
    }

    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
