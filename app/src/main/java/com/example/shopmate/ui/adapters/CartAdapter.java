package com.example.shopmate.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.shopmate.R;
import com.example.shopmate.data.model.CartItem;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartItemViewHolder> {

    private List<CartItem> cartItems = new ArrayList<>();
    private final CartItemActionListener listener;

    public interface CartItemActionListener {
        void onQuantityChanged(int itemId, int newQuantity);
        void onRemoveItem(int itemId);
    }

    public CartAdapter(CartItemActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView productName;
        private final TextView productPrice;
        private final TextView quantityText;
        private final TextView subtotalValue;
        private final MaterialButton decreaseQuantityBtn;
        private final MaterialButton increaseQuantityBtn;
        private final ImageButton removeItemButton;
        private final ImageView productImage;
        private int currentQuantity = 1;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            subtotalValue = itemView.findViewById(R.id.subtotalValue);
            decreaseQuantityBtn = itemView.findViewById(R.id.decreaseQuantityBtn);
            increaseQuantityBtn = itemView.findViewById(R.id.increaseQuantityBtn);
            removeItemButton = itemView.findViewById(R.id.removeItemButton);
            productImage = itemView.findViewById(R.id.productImage);
        }

        public void bind(CartItem item) {
            productName.setText(item.getProductName());
            productPrice.setText(item.getFormattedPrice());
            currentQuantity = item.getQuantity();  // ✅ Luôn sync với data từ server
            updateQuantityDisplay();
            subtotalValue.setText(item.getFormattedSubtotal());  // ✅ Subtotal từ API

            // Load product image with Glide
            if (item.getProductImage() != null && !item.getProductImage().isEmpty()) {
                Glide.with(productImage.getContext())
                    .load(item.getProductImage())
                    .apply(new RequestOptions()
                        .transform(new RoundedCorners(16))
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground))
                    .into(productImage);
            } else {
                productImage.setImageResource(R.drawable.ic_launcher_foreground);
            }

            // ✅ Chỉ call API, không update local UI
            decreaseQuantityBtn.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {  // ✅ Dùng quantity từ item
                    if (listener != null) {
                        listener.onQuantityChanged(item.getId(), item.getQuantity() - 1);
                    }
                }
            });

            increaseQuantityBtn.setOnClickListener(v -> {
                if (item.getQuantity() < 10) {  // ✅ Dùng quantity từ item
                    if (listener != null) {
                        listener.onQuantityChanged(item.getId(), item.getQuantity() + 1);
                    }
                }
            });

            removeItemButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveItem(item.getId());
                }
            });
        }

        private void updateQuantityDisplay() {
            quantityText.setText(String.valueOf(currentQuantity));
            decreaseQuantityBtn.setEnabled(currentQuantity > 1);
            increaseQuantityBtn.setEnabled(currentQuantity < 10);
        }
    }
} 
