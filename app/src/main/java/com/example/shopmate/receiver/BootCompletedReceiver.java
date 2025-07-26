package com.example.shopmate.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.example.shopmate.util.AuthManager;
import com.example.shopmate.util.BadgeUtils;
import com.example.shopmate.util.NotificationUtils;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || 
                Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            
            // Kiểm tra trạng thái đăng nhập trước khi khôi phục badge
            AuthManager authManager = AuthManager.getInstance(context);
            if (!authManager.isLoggedIn()) {
                // Nếu chưa đăng nhập, xóa badge
                BadgeUtils.removeBadge(context);
                BadgeUtils.clearSavedBadgeCount(context);
                NotificationUtils.cancelBadgeNotification(context);
                return;
            }
            
            // Khôi phục badge từ SharedPreferences
            int savedBadgeCount = BadgeUtils.getSavedBadgeCount(context);
            if (savedBadgeCount > 0) {
                
                // Đợi một chút để đảm bảo launcher đã khởi động
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    // Kiểm tra lại trạng thái đăng nhập
                    if (authManager.isLoggedIn()) {
                        BadgeUtils.updateBadge(context, savedBadgeCount);
                        NotificationUtils.showBadgeNotification(context, savedBadgeCount);
                        
                        // Thử lại sau 5 giây
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            // Kiểm tra lại trạng thái đăng nhập
                            if (authManager.isLoggedIn()) {
                                BadgeUtils.updateBadge(context, savedBadgeCount);
                                NotificationUtils.showBadgeNotification(context, savedBadgeCount);
                            }
                        }, 5000);
                    }
                }, 3000);
            }
        }
    }
} 