package com.example.shopmate.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopmate.R;
import com.example.shopmate.data.model.Product;
import com.example.shopmate.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    
    private List<Product> products;
    private OnProductClickListener clickListener;
    
    
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }
    
    public ProductAdapter() {
        this.products = new ArrayList<>();
    }
    
    
    public void setOnProductClickListener(OnProductClickListener listener) {
        this.clickListener = listener;
    }
    
    public void updateProducts(List<Product> newProducts) {
        if (newProducts == null) return;
        this.products = newProducts;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_featured, parent, false);
        return new ProductViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }
    
    @Override
    public int getItemCount() {
        return products.size();
    }
    
    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productDescription;
        private final TextView productPrice;
        private final TextView productSold;
        
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productDescription = itemView.findViewById(R.id.productDescription);
            productPrice = itemView.findViewById(R.id.productPrice);
            productSold = itemView.findViewById(R.id.productSold);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (clickListener != null) {
                        clickListener.onProductClick(products.get(position));
                    }
                }
            });
        }
        
        public void bind(Product product) {
            productName.setText(product.getProductName());
            productDescription.setText(product.getBriefDescription());
            productPrice.setText(product.getFormattedPrice());
            
            // Display total ordered (sold quantity)
            int totalSold = product.getTotalOrdered();
            productSold.setText("Sold: " + totalSold);
            
            String fullImageUrl = ImageUtils.getFullImageUrl(product.getImageURL());
            Glide.with(itemView.getContext())
                    .load(fullImageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(productImage);
        }
    }
} 