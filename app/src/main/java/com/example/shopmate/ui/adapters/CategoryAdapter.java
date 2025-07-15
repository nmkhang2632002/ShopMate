package com.example.shopmate.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopmate.R;
import com.example.shopmate.data.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    
    private List<Category> categories;
    private OnCategoryClickListener listener;
    
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
    
    public CategoryAdapter() {
        this.categories = new ArrayList<>();
    }
    
    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }
    
    public void updateCategories(List<Category> newCategories) {
        if (newCategories == null) return;
        this.categories = newCategories;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }
    
    @Override
    public int getItemCount() {
        return categories.size();
    }
    
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryLetter;
        private final TextView categoryName;
        
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryLetter = itemView.findViewById(R.id.categoryLetter);
            categoryName = itemView.findViewById(R.id.categoryName);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onCategoryClick(categories.get(position));
                }
            });
        }
        
        public void bind(Category category) {
            categoryName.setText(category.getCategoryName());
            
            // Set first letter of category name as the letter
            String name = category.getCategoryName();
            if (name != null && !name.isEmpty()) {
                categoryLetter.setText(String.valueOf(name.charAt(0)));
            } else {
                categoryLetter.setText("C");
            }
        }
    }
} 