package com.example.shopmate.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopmate.R;
import com.example.shopmate.data.model.Order;
import com.example.shopmate.util.CurrencyUtils;
import com.example.shopmate.util.DateUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.List;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {

    private List<Order> orders;
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onViewOrderDetails(Order order);
        void onUpdateOrderStatus(Order order);
        void onUpdatePaymentStatus(Order order);
    }

    public AdminOrderAdapter(List<Order> orders, OnOrderActionListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;
        private TextView orderId;
        private TextView customerName;
        private TextView orderDate;
        private TextView totalAmount;
        private TextView itemCount;
        private TextView paymentStatus;
        private Chip statusChip;
        private MaterialButton btnViewDetails;
        private MaterialButton btnUpdatePayment;
        private MaterialButton btnUpdateStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            orderId = itemView.findViewById(R.id.orderId);
            customerName = itemView.findViewById(R.id.customerName);
            orderDate = itemView.findViewById(R.id.orderDate);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            itemCount = itemView.findViewById(R.id.itemCount);
            paymentStatus = itemView.findViewById(R.id.paymentStatus);
            statusChip = itemView.findViewById(R.id.statusChip);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            btnUpdatePayment = itemView.findViewById(R.id.btnUpdatePayment);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
        }

        public void bind(Order order) {
            orderId.setText("Order #" + order.getId());
            customerName.setText(order.getUserName() != null ? order.getUserName() : "Unknown Customer");
            orderDate.setText(DateUtils.formatDate(order.getOrderDate()));
            totalAmount.setText(CurrencyUtils.formatPrice(order.getTotalAmount()));
            itemCount.setText(order.getTotalItems() + " items");

            // Set payment status
            if (order.getPayments() != null && !order.getPayments().isEmpty()) {
                Order.Payment payment = order.getPayments().get(0);
                String paymentStatusValue = payment.getPaymentStatus();
                paymentStatus.setText(paymentStatusValue);
                setPaymentStatusColor(paymentStatus, paymentStatusValue);
                
                // Disable payment update button if status is final
                boolean isPaymentFinal = "Paid".equalsIgnoreCase(paymentStatusValue) || 
                                       "Cancelled".equalsIgnoreCase(paymentStatusValue);
                btnUpdatePayment.setEnabled(!isPaymentFinal);
                btnUpdatePayment.setAlpha(isPaymentFinal ? 0.5f : 1.0f);
            } else {
                paymentStatus.setText("No Payment");
                paymentStatus.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
                // Enable button when no payment (can create payment)
                btnUpdatePayment.setEnabled(false);
                btnUpdatePayment.setAlpha(0.5f);
            }

            // Set status chip
            statusChip.setText(order.getStatus());
            setStatusChipStyle(statusChip, order.getStatus());

            // Set click listeners
            btnViewDetails.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewOrderDetails(order);
                }
            });

            btnUpdatePayment.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUpdatePaymentStatus(order);
                }
            });

            btnUpdateStatus.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUpdateOrderStatus(order);
                }
            });

            // Add card click for view details
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewOrderDetails(order);
                }
            });

            // Add subtle animation
            cardView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                        break;
                    case android.view.MotionEvent.ACTION_UP:
                    case android.view.MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                        break;
                }
                return false;
            });
        }

        private void setStatusChipStyle(Chip chip, String status) {
            switch (status.toLowerCase()) {
                case "pending":
                    chip.setChipBackgroundColorResource(R.color.status_pending);
                    chip.setTextColor(itemView.getContext().getColor(R.color.status_pending_text));
                    break;
                case "processing":
                    chip.setChipBackgroundColorResource(R.color.status_processing);
                    chip.setTextColor(itemView.getContext().getColor(R.color.status_processing_text));
                    break;
                case "delivered":
                    chip.setChipBackgroundColorResource(R.color.status_delivered);
                    chip.setTextColor(itemView.getContext().getColor(R.color.status_delivered_text));
                    break;
                case "cancelled":
                    chip.setChipBackgroundColorResource(R.color.status_cancelled);
                    chip.setTextColor(itemView.getContext().getColor(R.color.status_cancelled_text));
                    break;
                default:
                    chip.setChipBackgroundColorResource(R.color.chip_background);
                    chip.setTextColor(itemView.getContext().getColor(R.color.text_primary));
                    break;
            }
        }

        private void setPaymentStatusColor(TextView textView, String status) {
            switch (status.toLowerCase()) {
                case "paid":
                    textView.setTextColor(itemView.getContext().getColor(R.color.success));
                    break;
                case "pending":
                    textView.setTextColor(itemView.getContext().getColor(R.color.warning));
                    break;
                case "cancelled":
                case "failed":
                    textView.setTextColor(itemView.getContext().getColor(R.color.error));
                    break;
                default:
                    textView.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
                    break;
            }
        }
    }
}
