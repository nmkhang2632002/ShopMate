package com.example.shopmate.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopmate.R;
import com.example.shopmate.data.model.Banner;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    
    private List<Banner> banners = new ArrayList<>();
    private Context context;
    private OnBannerClickListener listener;
    
    public interface OnBannerClickListener {
        void onBannerClick(Banner banner);
    }
    
    public BannerAdapter(Context context) {
        this.context = context;
    }
    
    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Banner banner = banners.get(position);
        holder.bind(banner);
    }
    
    @Override
    public int getItemCount() {
        return banners.size();
    }
    
    public void setBanners(List<Banner> banners) {
        this.banners = banners;
        notifyDataSetChanged();
    }
    
    class BannerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView bannerImage;
        private final TextView bannerTitle;
        private final TextView bannerDescription;
        
        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.bannerImage);
            bannerTitle = itemView.findViewById(R.id.bannerTitle);
            bannerDescription = itemView.findViewById(R.id.bannerDescription);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onBannerClick(banners.get(position));
                }
            });
        }
        
        public void bind(Banner banner) {
            bannerTitle.setText(banner.getTitle());
            bannerDescription.setText(banner.getDescription());
            
            // Load image with Glide if URL is available
            if (banner.getImageUrl() != null && !banner.getImageUrl().isEmpty()) {
                Glide.with(context)
                    .load(banner.getImageUrl())
                    .placeholder(R.drawable.ic_home)
                    .error(R.drawable.ic_home)
                    .centerCrop()
                    .into(bannerImage);
            } else {
                // Use placeholder if no image URL
                bannerImage.setImageResource(R.drawable.ic_home);
            }
        }
    }
} 