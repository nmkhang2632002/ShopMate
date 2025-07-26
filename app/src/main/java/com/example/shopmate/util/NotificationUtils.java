package com.example.shopmate.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.shopmate.R;
import com.example.shopmate.ui.activities.MainActivity;

public class NotificationUtils {
    private static final String CHANNEL_ID = "cart_badge_channel";
    private static final int BADGE_NOTIFICATION_ID = 100;
    
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Cart Badge",
                    NotificationManager.IMPORTANCE_MIN);
            
            channel.setDescription("Channel for cart badge notifications");
            channel.setShowBadge(true);
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setSound(null, null);
            
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    public static void showBadgeNotification(Context context, int count) {
        if (count <= 0) {
            cancelBadgeNotification(context);
            return;
        }
        
        try {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_shopping_cart)
                    .setContentTitle("ShopMate Cart")
                    .setContentText(count + " item(s) in your cart")
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setNumber(count)
                    .setShowWhen(false)
                    .setSilent(true);
            
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(BADGE_NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {
            // Ignore permission exceptions
        }
    }
    
    public static void cancelBadgeNotification(Context context) {
        try {
            // Hủy thông báo
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(BADGE_NOTIFICATION_ID);
            
            // Thử hủy tất cả thông báo
            notificationManager.cancelAll();
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
} 