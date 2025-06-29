package com.example.shopmate.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.shopmate.R;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword, etConfirmPassword, etPhone, etAddress;
    private ImageView ivToggleRegisterPassword, ivToggleConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private static final String REGISTER_URL = "https://saleapp-mspd.onrender.com/v1/auth/register";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ view
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivToggleRegisterPassword = findViewById(R.id.ivToggleRegisterPassword);
        ivToggleConfirmPassword = findViewById(R.id.ivToggleConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        requestQueue = Volley.newRequestQueue(this);

        // Toggle ẩn/hiện mật khẩu
        ivToggleRegisterPassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggleRegisterPassword.setImageResource(R.drawable.ic_eye_off);
                isPasswordVisible = false;
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggleRegisterPassword.setImageResource(R.drawable.ic_eye);
                isPasswordVisible = true;
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        ivToggleConfirmPassword.setOnClickListener(v -> {
            if (isConfirmPasswordVisible) {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye_off);
                isConfirmPasswordVisible = false;
            } else {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye);
                isConfirmPasswordVisible = true;
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        btnRegister.setOnClickListener(v -> {
            clearErrors();

            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            boolean isValid = true;

            // Validate họ tên
            if (name.isEmpty()) {
                etName.setError("Vui lòng nhập họ và tên");
                isValid = false;
            }

            // Validate email
            if (email.isEmpty()) {
                etEmail.setError("Vui lòng nhập email");
                isValid = false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Email không hợp lệ");
                isValid = false;
            }

            // Validate số điện thoại (10 số, bắt đầu bằng 0)
            if (phone.isEmpty()) {
                etPhone.setError("Vui lòng nhập số điện thoại");
                isValid = false;
            } else if (!phone.matches("^0[0-9]{9}$")) {
                etPhone.setError("Số điện thoại Việt Nam phải đủ 10 số, bắt đầu bằng 0");
                isValid = false;
            }

            // Validate địa chỉ
            if (address.isEmpty()) {
                etAddress.setError("Vui lòng nhập địa chỉ");
                isValid = false;
            }

            // Validate mật khẩu
            if (password.isEmpty()) {
                etPassword.setError("Vui lòng nhập mật khẩu");
                isValid = false;
            } else if (password.length() < 6) {
                etPassword.setError("Mật khẩu phải từ 6 ký tự");
                isValid = false;
            }

            // Validate nhập lại mật khẩu
            if (confirmPassword.isEmpty()) {
                etConfirmPassword.setError("Vui lòng nhập lại mật khẩu");
                isValid = false;
            } else if (!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Mật khẩu không khớp");
                isValid = false;
            }

            if (!isValid) return;

            // Nếu hợp lệ, gọi API đăng ký
            doRegister(name, email, password, phone, address);
        });

        // Quay lại đăng nhập
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void doRegister(String name, String email, String password, String phone, String address) {
        JSONObject params = new JSONObject();
        try {
            params.put("name", name);
            params.put("email", email);
            params.put("password", password);
            params.put("phone", phone);
            params.put("address", address);
        } catch (JSONException e) {
            e.printStackTrace();
            showToast("Lỗi dữ liệu đầu vào!");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, REGISTER_URL, params,
                response -> {
                    // Xử lý kết quả trả về từ API
                    boolean success = false;
                    String message = "Đăng ký thất bại";
                    try {
                        if (response.has("success")) {
                            success = response.getBoolean("success");
                        }
                        if (response.has("message")) {
                            message = response.getString("message");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (success) {
                        showToast("Đăng ký thành công");
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        showToast(message);
                    }
                },
                error -> {
                    String errorMsg = "Đăng ký thất bại";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMsg = new String(error.networkResponse.data);
                    }
                    showToast(errorMsg);
                }
        );

        requestQueue.add(request);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void clearErrors() {
        etName.setError(null);
        etEmail.setError(null);
        etPhone.setError(null);
        etAddress.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
    }
}
