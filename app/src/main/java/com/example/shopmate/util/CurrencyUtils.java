package com.example.shopmate.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CurrencyUtils {
    
    private static final DecimalFormat VND_FORMAT;
    
    static {
        // Tạo DecimalFormatSymbols cho Việt Nam
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        
        // Tạo format với dấu phân cách hàng nghìn
        VND_FORMAT = new DecimalFormat("#,###", symbols);
    }
    
    /**
     * Format số tiền theo định dạng Việt Nam đồng
     * @param amount số tiền
     * @return chuỗi đã format (ví dụ: "15.999₫")
     */
    public static String formatVND(double amount) {
        return VND_FORMAT.format(amount) + "₫";
    }
    
    /**
     * Format số tiền từ string
     * @param amount số tiền dạng string
     * @return chuỗi đã format hoặc giá trị gốc nếu không parse được
     */
    public static String formatVND(String amount) {
        try {
            double value = Double.parseDouble(amount);
            return formatVND(value);
        } catch (NumberFormatException e) {
            return amount;
        }
    }
    
    /**
     * Format số tiền từ BigDecimal
     * @param amount số tiền dạng BigDecimal
     * @return chuỗi đã format
     */
    public static String formatVND(java.math.BigDecimal amount) {
        if (amount == null) return "0₫";
        return formatVND(amount.doubleValue());
    }
}
