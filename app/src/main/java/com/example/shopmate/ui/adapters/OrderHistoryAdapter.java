package com.example.shopmate.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopmate.R;
import com.example.shopmate.data.model.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {
    
    private List<Order> orderList;
    private OnOrderClickListener listener;
    
    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }
    
    public OrderHistoryAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }
    
    @Override
    public int getItemCount() {
        return orderList.size();
    }
    
    class OrderViewHolder extends RecyclerView.ViewHolder {
        
        private TextView orderIdText;
        private TextView orderDateText;
        private TextView orderStatusText;
        private TextView paymentMethodText;
        private TextView totalAmountText;
        private TextView paymentStatusText;
        private TextView billingAddressText;
        
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            
            orderIdText = itemView.findViewById(R.id.orderIdText);
            orderDateText = itemView.findViewById(R.id.orderDateText);
            orderStatusText = itemView.findViewById(R.id.orderStatusText);
            paymentMethodText = itemView.findViewById(R.id.paymentMethodText);
            totalAmountText = itemView.findViewById(R.id.totalAmountText);
            paymentStatusText = itemView.findViewById(R.id.paymentStatusText);
            billingAddressText = itemView.findViewById(R.id.billingAddressText);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(orderList.get(getAdapterPosition()));
                }
            });
        }
        
        public void bind(Order order) {
            // Order ID
            orderIdText.setText("#" + order.getId());
            
            // Order Date
            orderDateText.setText(formatDate(order.getOrderDate()));
            
            // Order Status
            orderStatusText.setText(order.getOrderStatus());
            setStatusColor(orderStatusText, order.getOrderStatus());
            
            // Payment Method
            paymentMethodText.setText(order.getPaymentMethod());
            
            // Billing Address
            billingAddressText.setText(order.getBillingAddress());
            
            // Total Amount and Payment Status
            if (order.getPayments() != null && !order.getPayments().isEmpty()) {
                Order.Payment payment = order.getPayments().get(0);
                totalAmountText.setText(formatCurrency(payment.getAmount()));
                paymentStatusText.setText(payment.getPaymentStatus());
                setPaymentStatusColor(paymentStatusText, payment.getPaymentStatus());
            } else {
                totalAmountText.setText("N/A");
                paymentStatusText.setText("No Payment");
                paymentStatusText.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
            }
        }
        
        private String formatDate(String dateString) {
            try {
                // Parse ISO date format
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                return outputFormat.format(date);
            } catch (Exception e) {
                return dateString;
            }
        }
        
        private String formatCurrency(double amount) {
            return String.format("₫%.0f", amount);
        }
        
        private void setStatusColor(TextView textView, String status) {
            int colorRes;
            switch (status.toLowerCase()) {
                case "confirmed":
                case "processing":
                    colorRes = R.color.primary;
                    break;
                case "pending":
                    colorRes = R.color.warning;
                    break;
                case "completed":
                    colorRes = R.color.success;
                    break;
                case "cancelled":
                    colorRes = R.color.error;
                    break;
                default:
                    colorRes = R.color.text_secondary;
                    break;
            }
            textView.setTextColor(itemView.getContext().getColor(colorRes));
        }
        
        private void setPaymentStatusColor(TextView textView, String status) {
            int colorRes;
            switch (status.toLowerCase()) {
                case "paid":
                    colorRes = R.color.success;
                    break;
                case "pending":
                    colorRes = R.color.warning;
                    break;
                case "failed":
                    colorRes = R.color.error;
                    break;
                default:
                    colorRes = R.color.text_secondary;
                    break;
            }
            textView.setTextColor(itemView.getContext().getColor(colorRes));
        }
    }
}
