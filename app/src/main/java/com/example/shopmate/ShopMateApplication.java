package com.example.shopmate;

import android.app.Application;
import android.os.Handler;

import com.example.shopmate.data.network.RetrofitClient;
import com.example.shopmate.util.AuthManager;
import com.example.shopmate.util.BadgeUtils;
import com.example.shopmate.util.NotificationUtils;

public class ShopMateApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize RetrofitClient with application context
        RetrofitClient.initialize(this);
        
        // Khởi tạo notification channel
        NotificationUtils.createNotificationChannel(this);
        
        // Kiểm tra trạng thái đăng nhập trước khi khôi phục badge
        AuthManager authManager = AuthManager.getInstance(this);
        if (!authManager.isLoggedIn()) {
            // Nếu chưa đăng nhập, xóa badge
            BadgeUtils.removeBadge(this);
            BadgeUtils.clearSavedBadgeCount(this);
            NotificationUtils.cancelBadgeNotification(this);
            return;
        }
        
        // Khôi phục badge từ SharedPreferences
        int savedBadgeCount = BadgeUtils.getSavedBadgeCount(this);
        if (savedBadgeCount > 0) {
            // Áp dụng badge ngay lập tức
            BadgeUtils.updateBadge(this, savedBadgeCount);
            
            // Hiển thị thông báo với badge
            NotificationUtils.showBadgeNotification(this, savedBadgeCount);
            
            // Thử lại sau 1 giây để đảm bảo badge hiển thị
            new Handler().postDelayed(() -> {
                BadgeUtils.updateBadge(this, savedBadgeCount);
                NotificationUtils.showBadgeNotification(this, savedBadgeCount);
            }, 1000);
            
            // Và thử lại sau 3 giây nữa (đôi khi launcher cần thời gian để khởi động hoàn toàn)
            new Handler().postDelayed(() -> {
                BadgeUtils.updateBadge(this, savedBadgeCount);
                NotificationUtils.showBadgeNotification(this, savedBadgeCount);
            }, 3000);
        }
    }
} 