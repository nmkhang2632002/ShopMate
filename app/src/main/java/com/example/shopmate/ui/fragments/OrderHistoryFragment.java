package com.example.shopmate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopmate.R;
import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.Order;
import com.example.shopmate.data.network.OrderApi;
import com.example.shopmate.data.network.RetrofitClient;
import com.example.shopmate.ui.adapters.OrderHistoryAdapter;
import com.example.shopmate.util.AuthManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private OrderHistoryAdapter adapter;
    private List<Order> orderList;
    private OrderApi orderApi;
    private AuthManager authManager;
    private View loadingView;
    private View emptyView;
    private MaterialToolbar toolbar;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        
        initViews(view);
        setupToolbar();
        setupRecyclerView();
        loadOrderHistory();
        
        return view;
    }
    
    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.recyclerViewOrders);
        loadingView = view.findViewById(R.id.loadingView);
        emptyView = view.findViewById(R.id.emptyView);
        
        // Initialize APIs
        orderApi = RetrofitClient.getInstance().create(OrderApi.class);
        authManager = AuthManager.getInstance(requireContext());
    }
    
    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> {
            // Handle back button click
            requireActivity().onBackPressed();
        });
    }
    
    private void setupRecyclerView() {
        orderList = new ArrayList<>();
        adapter = new OrderHistoryAdapter(orderList, this::onOrderClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
    
    private void loadOrderHistory() {
        showLoading(true);
        
        int userId = authManager.getUserId();
        android.util.Log.d("OrderHistory", "Loading orders for userId: " + userId);
        
        orderApi.getOrdersByUserId(userId).enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
                showLoading(false);
                android.util.Log.d("OrderHistory", "API response - success: " + response.isSuccessful() + 
                                  ", code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("OrderHistory", "Response body success: " + response.body().isSuccessful());
                    if (response.body().isSuccessful()) {
                        List<Order> orders = response.body().getData();
                        android.util.Log.d("OrderHistory", "Orders count: " + (orders != null ? orders.size() : "null"));
                        
                        if (orders != null && !orders.isEmpty()) {
                            orderList.clear();
                            // Sắp xếp theo ID giảm dần (order mới nhất lên đầu)
                            orders.sort((o1, o2) -> Integer.compare(o2.getId(), o1.getId()));
                            orderList.addAll(orders);
                            adapter.notifyDataSetChanged();
                            showEmptyView(false);
                        } else {
                            android.util.Log.d("OrderHistory", "No orders found");
                            showEmptyView(true);
                        }
                    } else {
                        android.util.Log.e("OrderHistory", "API response not successful: " + response.body().getMessage());
                        showEmptyView(true);
                    }
                } else {
                    android.util.Log.e("OrderHistory", "Response not successful or body is null");
                    Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
                    showEmptyView(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                showLoading(false);
                android.util.Log.e("OrderHistory", "Network error: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showEmptyView(true);
            }
        });
    }
    
    private void onOrderClick(Order order) {
        // Navigate to order detail
        OrderDetailFragment detailFragment = OrderDetailFragment.newInstance(order.getId());
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, detailFragment)
                .addToBackStack(null)
                .commit();
    }
    
    private void showLoading(boolean show) {
        if (loadingView != null) {
            loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void showEmptyView(boolean show) {
        if (emptyView != null) {
            emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
