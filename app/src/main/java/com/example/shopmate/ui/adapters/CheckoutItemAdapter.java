package com.example.shopmate.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.shopmate.R;
import com.example.shopmate.data.model.CartItem;
import com.example.shopmate.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class CheckoutItemAdapter extends RecyclerView.Adapter<CheckoutItemAdapter.CheckoutItemViewHolder> {

    private List<CartItem> cartItems = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(CartItem cartItem);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public CheckoutItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkout, parent, false);
        return new CheckoutItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutItemViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        android.util.Log.d("CheckoutAdapter", "Binding item at position " + position + ": " + item.getProductName());
        holder.bind(item);
        
        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void setCartItems(List<CartItem> cartItems) {
        android.util.Log.d("CheckoutAdapter", "setCartItems called with " + (cartItems != null ? cartItems.size() : "null") + " items");
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
        android.util.Log.d("CheckoutAdapter", "Adapter now has " + this.cartItems.size() + " items");
        notifyDataSetChanged();
        android.util.Log.d("CheckoutAdapter", "notifyDataSetChanged called");
    }

    static class CheckoutItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView productName;
        private final TextView productPrice;
        private final TextView quantityText;
        private final TextView subtotalValue;
        private final ImageView productImage;

        public CheckoutItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            subtotalValue = itemView.findViewById(R.id.subtotalValue);
        }

        public void bind(CartItem item) {
            productName.setText(item.getProductName());
            productPrice.setText(item.getFormattedPrice());
            quantityText.setText("Quantity: " + item.getQuantity());
            subtotalValue.setText(item.getFormattedSubtotal());
            
            // Load product image with Glide
            if (item.getProductImage() != null && !item.getProductImage().isEmpty()) {
                String fullImageUrl = ImageUtils.getFullImageUrl(item.getProductImage());
                Glide.with(productImage.getContext())
                    .load(fullImageUrl)
                    .apply(new RequestOptions()
                        .transform(new RoundedCorners(12))
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground))
                    .into(productImage);
            } else {
                productImage.setImageResource(R.drawable.ic_launcher_foreground);
            }
        }
    }
}