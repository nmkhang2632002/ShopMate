package com.example.shopmate.util;

import android.util.Log;

import com.example.shopmate.config.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Utility class để xử lý timezone cho VNPay payment
 * Đảm bảo consistency với backend khi deploy
 */
public class TimeZoneHelper {
    private static final String TAG = "TimeZoneHelper";
    
    /**
     * Lấy TimeZone Việt Nam
     */
    public static TimeZone getVietnamTimeZone() {
        return TimeZone.getTimeZone(Constants.VIETNAM_TIMEZONE_ID);
    }
    
    /**
     * Tạo SimpleDateFormat với timezone Việt Nam
     */
    public static SimpleDateFormat getVNPayDateFormatter() {
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.VNPAY_DATE_FORMAT, Locale.US);
        formatter.setTimeZone(getVietnamTimeZone());
        return formatter;
    }
    
    /**
     * Lấy thời gian hiện tại theo timezone Việt Nam
     */
    public static String getCurrentTimeVietnam() {
        return getVNPayDateFormatter().format(new Date());
    }
    
    /**
     * Lấy expire time cho VNPay (hiện tại + timeout)
     */
    public static String getVNPayExpireTime() {
        Calendar calendar = Calendar.getInstance(getVietnamTimeZone());
        calendar.add(Calendar.MINUTE, Constants.VNPAY_TIMEOUT_MINUTES);
        return getVNPayDateFormatter().format(calendar.getTime());
    }
    
    /**
     * Debug timezone information
     */
    public static void debugTimezoneInfo(String context) {
        TimeZone systemTz = TimeZone.getDefault();
        TimeZone vietnamTz = getVietnamTimeZone();
        
        Log.d(TAG, "=== TIMEZONE DEBUG [" + context + "] ===");
        Log.d(TAG, "System Timezone: " + systemTz.getID());
        Log.d(TAG, "Vietnam Timezone: " + vietnamTz.getID());
        Log.d(TAG, "Current Time (Vietnam): " + getCurrentTimeVietnam());
        Log.d(TAG, "VNPay Expire Time: " + getVNPayExpireTime());
        Log.d(TAG, "VNPay Timeout: " + Constants.VNPAY_TIMEOUT_MINUTES + " minutes");
        
        // Hiển thị offset
        long currentTime = System.currentTimeMillis();
        int systemOffset = systemTz.getOffset(currentTime) / (1000 * 60 * 60);
        int vietnamOffset = vietnamTz.getOffset(currentTime) / (1000 * 60 * 60);
        
        Log.d(TAG, "System GMT Offset: UTC" + (systemOffset >= 0 ? "+" : "") + systemOffset);
        Log.d(TAG, "Vietnam GMT Offset: UTC" + (vietnamOffset >= 0 ? "+" : "") + vietnamOffset);
        Log.d(TAG, "======================================");
    }
    
    /**
     * Parse VNPay date string thành Date object
     */
    public static Date parseVNPayDate(String vnpayDateString) {
        try {
            return getVNPayDateFormatter().parse(vnpayDateString);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing VNPay date: " + vnpayDateString, e);
            return null;
        }
    }
    
    /**
     * Format Date thành VNPay date string
     */
    public static String formatToVNPayDate(Date date) {
        return getVNPayDateFormatter().format(date);
    }
    
    /**
     * Format VNPay date string thành human readable format
     */
    public static String formatVNPayDateToHuman(String vnpayDateString) {
        try {
            Date date = parseVNPayDate(vnpayDateString);
            if (date != null) {
                SimpleDateFormat humanFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
                humanFormatter.setTimeZone(getVietnamTimeZone());
                return humanFormatter.format(date);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error formatting VNPay date to human format: " + vnpayDateString, e);
        }
        return vnpayDateString; // Fallback
    }
}
