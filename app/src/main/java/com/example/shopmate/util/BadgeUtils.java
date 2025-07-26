package com.example.shopmate.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Handler;

import me.leolin.shortcutbadger.ShortcutBadger;

import java.util.List;

public class BadgeUtils {
    private static final String PREF_NAME = "badge_prefs";
    private static final String KEY_BADGE_COUNT = "badge_count";
    private static final String OPPO_LAUNCHER = "com.oppo.launcher";
    private static final String OPPO_LAUNCHER2 = "com.color.launcher";
    private static final String REALME_LAUNCHER = "com.color.launcher";
    private static final String VIVO_LAUNCHER = "com.vivo.launcher";
    private static final String XIAOMI_LAUNCHER = "com.miui.home";
    private static final String SAMSUNG_LAUNCHER = "com.sec.android.app.launcher";
    
    public static void updateBadge(Context context, int count) {
        saveBadgeCount(context, count);
        boolean success = ShortcutBadger.applyCount(context, count);
        
        if (!success) {
            String launcherPackage = getLauncherPackageName(context);
            if (launcherPackage != null) {
                if (launcherPackage.contains("oppo") || launcherPackage.contains("color") ||
                        launcherPackage.equals(OPPO_LAUNCHER) || launcherPackage.equals(OPPO_LAUNCHER2) ||
                        launcherPackage.equals(REALME_LAUNCHER)) {
                    applyBadgeForOppoRealme(context, count);
                } else if (launcherPackage.equals(VIVO_LAUNCHER)) {
                    applyBadgeForVivo(context, count);
                } else if (launcherPackage.equals(XIAOMI_LAUNCHER)) {
                    applyBadgeForXiaomi(context, count);
                } else if (launcherPackage.equals(SAMSUNG_LAUNCHER)) {
                    applyBadgeForSamsung(context, count);
                } else {
                    applyBadgeGeneric(context, count);
                }
            } else {
                applyBadgeGeneric(context, count);
            }
        }
    }
    
    private static void applyBadgeForOppoRealme(Context context, int count) {
        try {
            Intent intent = new Intent("com.oppo.unsettledevent");
            intent.putExtra("packageName", context.getPackageName());
            intent.putExtra("number", count);
            intent.putExtra("upgradeNumber", count);
            context.sendBroadcast(intent);
            
            intent = new Intent("com.color.badge");
            intent.putExtra("package_name", context.getPackageName());
            intent.putExtra("app_badge_count", count);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
    
    private static void applyBadgeForVivo(Context context, int count) {
        try {
            Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
            intent.putExtra("packageName", context.getPackageName());
            intent.putExtra("className", getLauncherClassName(context));
            intent.putExtra("notificationNum", count);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
    
    private static void applyBadgeForXiaomi(Context context, int count) {
        try {
            Intent intent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
            intent.putExtra("android.intent.extra.update_application_component_name", 
                    context.getPackageName() + "/" + getLauncherClassName(context));
            intent.putExtra("android.intent.extra.update_application_message_text", String.valueOf(count));
            context.sendBroadcast(intent);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
    
    private static void applyBadgeForSamsung(Context context, int count) {
        try {
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", count);
            intent.putExtra("badge_count_package_name", context.getPackageName());
            intent.putExtra("badge_count_class_name", getLauncherClassName(context));
            context.sendBroadcast(intent);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
    
    private static void applyBadgeGeneric(Context context, int count) {
        try {
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", count);
            intent.putExtra("badge_count_package_name", context.getPackageName());
            intent.putExtra("badge_count_class_name", getLauncherClassName(context));
            context.sendBroadcast(intent);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
    
    public static void removeBadge(Context context) {
        // Xóa badge từ SharedPreferences
        saveBadgeCount(context, 0);
        
        // Thử xóa badge bằng ShortcutBadger
        ShortcutBadger.removeCount(context);
        
        // Thử xóa badge bằng cách gửi số lượng 0
        updateBadge(context, 0);
        
        // Thử xóa badge bằng cách gửi broadcast trực tiếp cho các launcher phổ biến
        try {
            // OPPO/Realme
            Intent intentOppo = new Intent("com.oppo.unsettledevent");
            intentOppo.putExtra("packageName", context.getPackageName());
            intentOppo.putExtra("number", 0);
            intentOppo.putExtra("upgradeNumber", 0);
            context.sendBroadcast(intentOppo);
            
            Intent intentColor = new Intent("com.color.badge");
            intentColor.putExtra("package_name", context.getPackageName());
            intentColor.putExtra("app_badge_count", 0);
            context.sendBroadcast(intentColor);
            
            // Samsung
            Intent intentSamsung = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intentSamsung.putExtra("badge_count", 0);
            intentSamsung.putExtra("badge_count_package_name", context.getPackageName());
            intentSamsung.putExtra("badge_count_class_name", getLauncherClassName(context));
            context.sendBroadcast(intentSamsung);
            
            // Xiaomi
            Intent intentXiaomi = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
            intentXiaomi.putExtra("android.intent.extra.update_application_component_name", 
                    context.getPackageName() + "/" + getLauncherClassName(context));
            intentXiaomi.putExtra("android.intent.extra.update_application_message_text", "0");
            context.sendBroadcast(intentXiaomi);
            
            // Vivo
            Intent intentVivo = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
            intentVivo.putExtra("packageName", context.getPackageName());
            intentVivo.putExtra("className", getLauncherClassName(context));
            intentVivo.putExtra("notificationNum", 0);
            context.sendBroadcast(intentVivo);
            
            // Generic
            Intent intentGeneric = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intentGeneric.putExtra("badge_count", 0);
            intentGeneric.putExtra("badge_count_package_name", context.getPackageName());
            intentGeneric.putExtra("badge_count_class_name", getLauncherClassName(context));
            context.sendBroadcast(intentGeneric);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
    
    /**
     * Xóa badge bằng mọi cách có thể, bao gồm cả thử lại nhiều lần
     */
    public static void forceRemoveBadge(Context context) {
        // Xóa badge ngay lập tức
        removeBadge(context);
        clearSavedBadgeCount(context);
        
        // Thử thêm một số cách xóa badge khác
        try {
            // Thử cách khác cho OPPO/Realme
            Intent intentOppo1 = new Intent("com.oppo.unsettledevent");
            intentOppo1.putExtra("packageName", context.getPackageName());
            intentOppo1.putExtra("badgenumber", 0);
            context.sendBroadcast(intentOppo1);
            
            Intent intentOppo2 = new Intent("com.oppo.badge");
            intentOppo2.putExtra("app_packagename", context.getPackageName());
            intentOppo2.putExtra("app_badge_count", 0);
            context.sendBroadcast(intentOppo2);
            
            Intent intentColor = new Intent("com.color.badge.update");
            intentColor.putExtra("package_name", context.getPackageName());
            intentColor.putExtra("class_name", getLauncherClassName(context));
            intentColor.putExtra("badge_count", 0);
            context.sendBroadcast(intentColor);
            
            // Thử cách khác cho Xiaomi
            Intent intentXiaomi = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
            intentXiaomi.putExtra("android.intent.extra.update_application_component_name", 
                    context.getPackageName());
            intentXiaomi.putExtra("android.intent.extra.update_application_message_text", "0");
            context.sendBroadcast(intentXiaomi);
            
            // Thử cách khác cho HTC
            Intent intentHTC = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
            intentHTC.putExtra("packagename", context.getPackageName());
            intentHTC.putExtra("count", 0);
            context.sendBroadcast(intentHTC);
            
            // Thử cách khác cho Sony
            Intent intentSony = new Intent("com.sonyericsson.home.action.UPDATE_BADGE");
            intentSony.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());
            intentSony.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", getLauncherClassName(context));
            intentSony.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", "0");
            intentSony.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", false);
            context.sendBroadcast(intentSony);
        } catch (Exception e) {
            // Ignore exceptions
        }
        
        // Thử lại sau một khoảng thời gian
        new Handler().postDelayed(() -> {
            removeBadge(context);
            
            // Thử lại ShortcutBadger một lần nữa
            ShortcutBadger.removeCount(context);
            ShortcutBadger.applyCount(context, 0);
        }, 200);
    }
    
    public static int getSavedBadgeCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_BADGE_COUNT, 0);
    }
    
    private static void saveBadgeCount(Context context, int count) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_BADGE_COUNT, count).apply();
    }
    
    public static void clearSavedBadgeCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_BADGE_COUNT).apply();
    }
    
    private static String getLauncherPackageName(Context context) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        
        PackageManager pm = context.getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        
        if (resolveInfo != null && resolveInfo.activityInfo != null && 
                resolveInfo.activityInfo.packageName != null) {
            return resolveInfo.activityInfo.packageName;
        }
        return null;
    }
    
    private static String getLauncherClassName(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(context.getPackageName());
        
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        if (resolveInfos != null && resolveInfos.size() > 0) {
            return resolveInfos.get(0).activityInfo.name;
        }
        return "";
    }
} 