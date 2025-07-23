package com.example.shopmate.util;

import com.example.shopmate.data.model.CartItem;
import com.example.shopmate.data.model.OrderDetailResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailResponseDeserializer implements JsonDeserializer<OrderDetailResponse> {
    
    @Override
    public OrderDetailResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        
        try {
            // Use default Gson for basic deserialization
            Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .create();
            
            OrderDetailResponse response = gson.fromJson(json, OrderDetailResponse.class);
            
            // Custom handling for cartItems if needed
            if (json.getAsJsonObject().has("cartItems")) {
                JsonElement cartItemsElement = json.getAsJsonObject().get("cartItems");
                if (cartItemsElement.isJsonArray()) {
                    List<CartItem> cartItems = new ArrayList<>();
                    for (JsonElement itemElement : cartItemsElement.getAsJsonArray()) {
                        CartItem item = new CartItem();
                        
                        if (itemElement.getAsJsonObject().has("id")) {
                            item.setId(itemElement.getAsJsonObject().get("id").getAsInt());
                        }
                        if (itemElement.getAsJsonObject().has("productID")) {
                            item.setProductID(itemElement.getAsJsonObject().get("productID").getAsInt());
                        }
                        if (itemElement.getAsJsonObject().has("productName")) {
                            item.setProductName(itemElement.getAsJsonObject().get("productName").getAsString());
                        }
                        if (itemElement.getAsJsonObject().has("productImage")) {
                            item.setProductImage(itemElement.getAsJsonObject().get("productImage").getAsString());
                        }
                        if (itemElement.getAsJsonObject().has("quantity")) {
                            item.setQuantity(itemElement.getAsJsonObject().get("quantity").getAsInt());
                        }
                        if (itemElement.getAsJsonObject().has("price")) {
                            BigDecimal price = new BigDecimal(itemElement.getAsJsonObject().get("price").getAsString());
                            item.setPrice(price);
                        }
                        if (itemElement.getAsJsonObject().has("subtotal")) {
                            BigDecimal subtotal = new BigDecimal(itemElement.getAsJsonObject().get("subtotal").getAsString());
                            item.setSubtotal(subtotal);
                        }
                        
                        cartItems.add(item);
                    }
                    response.setCartItems(cartItems);
                }
            }
            
            return response;
            
        } catch (Exception e) {
            android.util.Log.e("OrderDetailDeserializer", "Failed to deserialize OrderDetailResponse", e);
            throw new JsonParseException("Failed to deserialize OrderDetailResponse", e);
        }
    }
}
