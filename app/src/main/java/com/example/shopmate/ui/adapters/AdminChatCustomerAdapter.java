package com.example.shopmate.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopmate.R;
import com.example.shopmate.data.model.ChatCustomer;

public class AdminChatCustomerAdapter extends ListAdapter<ChatCustomer, AdminChatCustomerAdapter.CustomerViewHolder> {

    private final OnCustomerClickListener listener;

    public interface OnCustomerClickListener {
        void onCustomerClick(ChatCustomer customer);
    }

    public AdminChatCustomerAdapter(OnCustomerClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<ChatCustomer> DIFF_CALLBACK = new DiffUtil.ItemCallback<ChatCustomer>() {
        @Override
        public boolean areItemsTheSame(@NonNull ChatCustomer oldItem, @NonNull ChatCustomer newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatCustomer oldItem, @NonNull ChatCustomer newItem) {
            return oldItem.getFullName().equals(newItem.getFullName()) &&
                   oldItem.getEmail().equals(newItem.getEmail());
        }
    };

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        ChatCustomer customer = getItem(position);
        holder.bind(customer, listener);
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivCustomerAvatar;
        private final TextView tvCustomerName;
        private final TextView tvCustomerEmail;

        CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCustomerAvatar = itemView.findViewById(R.id.ivCustomerAvatar);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerEmail = itemView.findViewById(R.id.tvCustomerEmail);
        }

        void bind(ChatCustomer customer, OnCustomerClickListener listener) {
            tvCustomerName.setText(customer.getFullName());
            tvCustomerEmail.setText(customer.getEmail());
            
            // Handle click event
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCustomerClick(customer);
                }
            });
        }
    }
} 