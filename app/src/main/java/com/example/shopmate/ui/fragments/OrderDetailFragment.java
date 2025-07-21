package com.example.shopmate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.shopmate.R;
import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.OrderDetail;
import com.example.shopmate.data.network.RetrofitClient;
import com.example.shopmate.data.network.VNPayApi;
import com.example.shopmate.util.CurrencyUtils;
import com.google.android.material.appbar.MaterialToolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailFragment extends Fragment {
    
    private static final String ARG_ORDER_ID = "order_id";
    
    private int orderId;
    private VNPayApi vnPayApi;
    
    // Views
    private MaterialToolbar toolbar;
    private TextView orderIdText;
    private TextView orderDateText;
    private TextView orderStatusText;
    private TextView paymentMethodText;
    private TextView billingAddressText;
    private TextView usernameText;
    private TextView phoneNumberText;
    private TextView totalAmountText;
    private TextView paymentStatusText;
    private TextView transactionIdText;
    private View loadingView;
    private View contentView;
    
    public static OrderDetailFragment newInstance(int orderId) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ORDER_ID, orderId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderId = getArguments().getInt(ARG_ORDER_ID);
        }
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);
        
        initViews(view);
        setupToolbar();
        loadOrderDetail();
        
        return view;
    }
    
    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        orderIdText = view.findViewById(R.id.orderIdText);
        orderDateText = view.findViewById(R.id.orderDateText);
        orderStatusText = view.findViewById(R.id.orderStatusText);
        paymentMethodText = view.findViewById(R.id.paymentMethodText);
        billingAddressText = view.findViewById(R.id.billingAddressText);
        usernameText = view.findViewById(R.id.usernameText);
        phoneNumberText = view.findViewById(R.id.phoneNumberText);
        totalAmountText = view.findViewById(R.id.totalAmountText);
        paymentStatusText = view.findViewById(R.id.paymentStatusText);
        transactionIdText = view.findViewById(R.id.transactionIdText);
        loadingView = view.findViewById(R.id.loadingView);
        contentView = view.findViewById(R.id.contentView);
        
        vnPayApi = RetrofitClient.getInstance().create(VNPayApi.class);
    }
    
    private void setupToolbar() {
        toolbar.setTitle("Order #" + orderId);
        toolbar.setNavigationOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
    
    private void loadOrderDetail() {
        showLoading(true);
        
        vnPayApi.getOrderDetail(orderId).enqueue(new Callback<ApiResponse<OrderDetail>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderDetail>> call, Response<ApiResponse<OrderDetail>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccessful()) {
                    OrderDetail orderDetail = response.body().getData();
                    displayOrderDetail(orderDetail);
                } else {
                    Toast.makeText(getContext(), "Failed to load order details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderDetail>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void displayOrderDetail(OrderDetail orderDetail) {
        orderIdText.setText("#" + orderDetail.getId());
        orderDateText.setText(orderDetail.getFormattedOrderDate());
        orderStatusText.setText(orderDetail.getOrderStatus());
        paymentMethodText.setText(orderDetail.getPaymentMethod());
        billingAddressText.setText(orderDetail.getBillingAddress());
        usernameText.setText(orderDetail.getUsername());
        phoneNumberText.setText(orderDetail.getPhoneNumber());
        
        if (orderDetail.getTotalAmount() != null) {
            totalAmountText.setText(CurrencyUtils.formatVND(orderDetail.getTotalAmount()));
        }
        
        paymentStatusText.setText(orderDetail.getPaymentStatus());
        
        if (orderDetail.getLatestTransactionId() != null && !orderDetail.getLatestTransactionId().isEmpty()) {
            transactionIdText.setText(orderDetail.getLatestTransactionId());
            transactionIdText.setVisibility(View.VISIBLE);
        } else {
            transactionIdText.setVisibility(View.GONE);
        }
    }
    
    private void showLoading(boolean show) {
        loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        contentView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
