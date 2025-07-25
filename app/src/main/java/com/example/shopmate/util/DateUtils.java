package com.example.shopmate.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    private static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);
    private static final SimpleDateFormat shortDateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

    public static String formatDate(Date date) {
        if (date == null) return "N/A";
        return dateFormatter.format(date);
    }

    public static String formatDate(String dateString) {
        try {
            // Assuming dateString is in ISO format or parseable format
            SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = inputFormatter.parse(dateString);
            return formatDate(date);
        } catch (Exception e) {
            return dateString; // Return original if parsing fails
        }
    }

    public static String formatDateTime(Date date) {
        if (date == null) return "N/A";
        return dateTimeFormatter.format(date);
    }

    public static String formatShortDate(Date date) {
        if (date == null) return "N/A";
        return shortDateFormatter.format(date);
    }
}
