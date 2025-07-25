package com.example.shopmate.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {

    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    private static final NumberFormat vndFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public static String formatPrice(double price) {
        return currencyFormatter.format(price);
    }

    public static String formatPriceWithSymbol(double price) {
        return "$" + String.format("%.2f", price);
    }

    public static String formatVND(double price) {
        return vndFormatter.format(price);
    }

    // Overload method for BigDecimal
    public static String formatVND(BigDecimal price) {
        if (price == null) return "0 VNĐ";
        return vndFormatter.format(price);
    }

    public static String formatVNDWithSymbol(double price) {
        return String.format("%,.0f VNĐ", price);
    }

    // Overload method for BigDecimal
    public static String formatVNDWithSymbol(BigDecimal price) {
        if (price == null) return "0 VNĐ";
        return String.format("%,.0f VNĐ", price.doubleValue());
    }

    public static double parsePrice(String priceString) {
        try {
            // Remove currency symbols and parse
            String cleanPrice = priceString.replaceAll("[^\\d.]", "");
            return Double.parseDouble(cleanPrice);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
