package com.example.shopmate.debug;

public class DebugOrderDetail {
    public static void testCartItems() {
        System.out.println("Testing OrderDetail with CartItems...");
        
        // Test data similar to API response
        String jsonResponse = "{\n" +
            "  \"status\": 1000,\n" +
            "  \"message\": \"Order detail retrieved\",\n" +
            "  \"data\": {\n" +
            "    \"id\": 77,\n" +
            "    \"cartID\": 49,\n" +
            "    \"userID\": 1,\n" +
            "    \"paymentMethod\": \"VNPAY\",\n" +
            "    \"billingAddress\": \"Quan 12\",\n" +
            "    \"orderStatus\": \"Processing\",\n" +
            "    \"orderDate\": \"2025-07-23T08:41:57Z\",\n" +
            "    \"cartItems\": [\n" +
            "      {\n" +
            "        \"id\": 95,\n" +
            "        \"productID\": 1,\n" +
            "        \"productName\": \"iPhone 15\",\n" +
            "        \"productImage\": \"https://res.cloudinary.com/ds9f2jnnj/image/upload/v1751177209/products/wa37mrhql3qqnj4v8fqh.webp\",\n" +
            "        \"quantity\": 1,\n" +
            "        \"price\": 24999000,\n" +
            "        \"subtotal\": 24999000\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";
        
        System.out.println("JSON Response: " + jsonResponse);
    }
}
