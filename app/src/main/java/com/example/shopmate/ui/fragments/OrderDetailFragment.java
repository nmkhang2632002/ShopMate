package com.example.shopmate.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopmate.R;
import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.CartItem;
import com.example.shopmate.data.model.OrderDetail;
import com.example.shopmate.data.model.OrderDetailResponse;
import com.example.shopmate.data.model.Product;
import com.example.shopmate.data.network.RetrofitClient;
import com.example.shopmate.data.network.VNPayApi;
import com.example.shopmate.ui.adapters.CheckoutItemAdapter;
import com.example.shopmate.ui.fragments.CartFragment;
import com.example.shopmate.ui.fragments.ProductDetailFragment;
import com.example.shopmate.util.CurrencyUtils;
import com.example.shopmate.viewmodel.CartViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailFragment extends Fragment {
    
    private static final String ARG_ORDER_ID = "order_id";
    
    private int orderId;
    private boolean isAdminView = false; // Flag để phân biệt admin/customer view
    private VNPayApi vnPayApi;
    private CheckoutItemAdapter orderItemAdapter;
    private CartViewModel cartViewModel;
    private OrderDetailResponse currentOrderDetail;
    
    // Views
    private MaterialToolbar toolbar;
    private RecyclerView orderItemsRecyclerView;
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
    private TextView debugOrderItemsText;
    private MaterialButton buyAgainButton;
    private View loadingView;
    private View contentView;
    
    public static OrderDetailFragment newInstance(int orderId) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ORDER_ID, orderId);
        fragment.setArguments(args);
        return fragment;
    }
    
    // Method cho admin view - ẩn buy again button và các chức năng customer
    public static OrderDetailFragment newInstanceForAdmin(int orderId) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ORDER_ID, orderId);
        args.putBoolean("IS_ADMIN_VIEW", true);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderId = getArguments().getInt(ARG_ORDER_ID);
            isAdminView = getArguments().getBoolean("IS_ADMIN_VIEW", false);
        }
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);
        
        initViews(view);
        setupToolbar();
        setupRecyclerView();
        
        // Chỉ setup buy again button nếu không phải admin view
        if (!isAdminView) {
            setupBuyAgainButton();
        } else {
            // Ẩn buy again button cho admin
            if (buyAgainButton != null) {
                buyAgainButton.setVisibility(View.GONE);
            }
        }
        
        loadOrderDetail();
        
        return view;
    }
    
    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        orderItemsRecyclerView = view.findViewById(R.id.orderItemsRecyclerView);
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
        debugOrderItemsText = view.findViewById(R.id.debugOrderItemsText);
        buyAgainButton = view.findViewById(R.id.buyAgainButton);
        loadingView = view.findViewById(R.id.loadingView);
        contentView = view.findViewById(R.id.contentView);
        
        vnPayApi = RetrofitClient.getInstance().create(VNPayApi.class);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
    }
    
    private void setupToolbar() {
        toolbar.setTitle("Order #" + orderId);
        toolbar.setNavigationOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
    
    private void setupRecyclerView() {
        orderItemAdapter = new CheckoutItemAdapter();
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderItemsRecyclerView.setAdapter(orderItemAdapter);
        
        // Set item click listener để navigate đến ProductDetailFragment
        orderItemAdapter.setOnItemClickListener(cartItem -> {
            if (cartItem != null && cartItem.getProductID() > 0) {
                navigateToProductDetail(cartItem.getProductID(), cartItem.getProductName());
            }
        });
    }
    
    private void setupBuyAgainButton() {
        buyAgainButton.setOnClickListener(v -> {
            addAllItemsToCartAndNavigate();
        });
    }
    
    private void loadOrderDetail() {
        showLoading(true);
        
        vnPayApi.getOrderDetail(orderId).enqueue(new Callback<ApiResponse<OrderDetailResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderDetailResponse>> call, Response<ApiResponse<OrderDetailResponse>> response) {
                showLoading(false);
                
                android.util.Log.d("OrderDetail", "=== API Response Debug ===");
                android.util.Log.d("OrderDetail", "Response successful: " + response.isSuccessful());
                android.util.Log.d("OrderDetail", "Response code: " + response.code());
                
                // Log raw response
                if (response.raw() != null) {
                    android.util.Log.d("OrderDetail", "Raw response: " + response.raw().toString());
                }
                
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("OrderDetail", "Response body not null");
                    android.util.Log.d("OrderDetail", "API response success: " + response.body().isSuccessful());
                    android.util.Log.d("OrderDetail", "API response status: " + response.body().getStatus());
                    android.util.Log.d("OrderDetail", "API response message: " + response.body().getMessage());
                    
                    // Log the actual data object
                    OrderDetailResponse data = response.body().getData();
                    android.util.Log.d("OrderDetail", "Data object: " + (data != null ? "not null" : "null"));
                    
                    if (data != null) {
                        android.util.Log.d("OrderDetail", "Order ID: " + data.getId());
                        android.util.Log.d("OrderDetail", "Payment Method: " + data.getPaymentMethod());
                        android.util.Log.d("OrderDetail", "Payments: " + (data.getPayments() != null ? data.getPayments().size() + " items" : "null"));
                        android.util.Log.d("OrderDetail", "CartItems: " + (data.getCartItems() != null ? data.getCartItems().size() + " items" : "null"));
                        
                        if (data.getPayments() != null && !data.getPayments().isEmpty()) {
                            android.util.Log.d("OrderDetail", "First payment amount: " + data.getPayments().get(0).getAmount());
                        }
                    }
                    
                    if (response.body().isSuccessful()) {
                        OrderDetailResponse orderDetail = response.body().getData();
                        android.util.Log.d("OrderDetail", "OrderDetail data: " + (orderDetail != null ? "not null" : "null"));
                        if (orderDetail != null) {
                            displayOrderDetail(orderDetail);
                        }
                    } else {
                        android.util.Log.e("OrderDetail", "API response not successful");
                        Toast.makeText(getContext(), "Failed to load order details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    android.util.Log.e("OrderDetail", "Response not successful or body is null");
                    if (response.errorBody() != null) {
                        try {
                            String errorString = response.errorBody().string();
                            android.util.Log.e("OrderDetail", "Error body: " + errorString);
                        } catch (Exception e) {
                            android.util.Log.e("OrderDetail", "Failed to read error body", e);
                        }
                    }
                    Toast.makeText(getContext(), "Failed to load order details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderDetailResponse>> call, Throwable t) {
                showLoading(false);
                android.util.Log.e("OrderDetail", "API call failed", t);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void displayOrderDetail(OrderDetailResponse orderDetail) {
        android.util.Log.d("OrderDetail", "=== Starting displayOrderDetail ===");
        android.util.Log.d("OrderDetail", "OrderDetail object: " + (orderDetail != null ? "not null" : "null"));
        
        // Store current order detail for buy again functionality
        currentOrderDetail = orderDetail;
        
        orderIdText.setText("#" + orderDetail.getId());
        orderDateText.setText(orderDetail.getFormattedOrderDate());
        orderStatusText.setText(orderDetail.getOrderStatus());
        paymentMethodText.setText(orderDetail.getPaymentMethod());
        billingAddressText.setText(orderDetail.getBillingAddress());
        usernameText.setText(orderDetail.getUsername());
        phoneNumberText.setText(orderDetail.getPhoneNumber());
        
        // Calculate total amount from multiple sources
        BigDecimal totalAmount = orderDetail.getTotalAmount();
        
        // Priority 1: Use totalAmount from API if available
        if (totalAmount == null) {
            // Priority 2: Get from payments.amount
            if (orderDetail.getPayments() != null && !orderDetail.getPayments().isEmpty()) {
                // Convert from VNPay format (multiply by 100) back to VND
                double paymentAmount = orderDetail.getPayments().get(0).getAmount();
                totalAmount = BigDecimal.valueOf(paymentAmount);
                android.util.Log.d("OrderDetail", "Using payment amount: " + totalAmount);
            }
            // Priority 3: Calculate from cart items
            else if (orderDetail.getCartItems() != null && !orderDetail.getCartItems().isEmpty()) {
                totalAmount = BigDecimal.ZERO;
                for (CartItem item : orderDetail.getCartItems()) {
                    if (item.getSubtotal() != null) {
                        totalAmount = totalAmount.add(item.getSubtotal());
                    }
                }
                android.util.Log.d("OrderDetail", "Calculated totalAmount from cart items: " + totalAmount);
            }
        }
        
        if (totalAmount != null) {
            totalAmountText.setText(CurrencyUtils.formatVND(totalAmount));
        } else {
            totalAmountText.setText("0đ");
        }
        
        paymentStatusText.setText(orderDetail.getPaymentStatus());
        
        if (orderDetail.getLatestTransactionId() != null && !orderDetail.getLatestTransactionId().isEmpty()) {
            transactionIdText.setText(orderDetail.getLatestTransactionId());
            transactionIdText.setVisibility(View.VISIBLE);
        } else {
            transactionIdText.setVisibility(View.GONE);
        }
        
        // Debug cart items extensively
        android.util.Log.d("OrderDetail", "=== Cart Items Debug ===");
        android.util.Log.d("OrderDetail", "getCartItems() result: " + (orderDetail.getCartItems() != null ? "not null" : "null"));
        
        if (orderDetail.getCartItems() != null) {
            android.util.Log.d("OrderDetail", "Cart items size: " + orderDetail.getCartItems().size());
            if (!orderDetail.getCartItems().isEmpty()) {
                android.util.Log.d("OrderDetail", "Cart items found: " + orderDetail.getCartItems().size());
                for (int i = 0; i < orderDetail.getCartItems().size(); i++) {
                    CartItem item = orderDetail.getCartItems().get(i);
                    android.util.Log.d("OrderDetail", "Item " + i + ":");
                    android.util.Log.d("OrderDetail", "  - ID: " + item.getId());
                    android.util.Log.d("OrderDetail", "  - ProductName: " + item.getProductName());
                    android.util.Log.d("OrderDetail", "  - ProductImage: " + item.getProductImage());
                    android.util.Log.d("OrderDetail", "  - Quantity: " + item.getQuantity());
                    android.util.Log.d("OrderDetail", "  - Price: " + item.getPrice());
                }
                
                if (orderItemAdapter != null) {
                    android.util.Log.d("OrderDetail", "Adapter is not null, setting cart items");
                    orderItemAdapter.setCartItems(orderDetail.getCartItems());
                    
                    // Make sure RecyclerView is visible and update debug text
                    orderItemsRecyclerView.setVisibility(View.VISIBLE);
                    debugOrderItemsText.setText("Found " + orderDetail.getCartItems().size() + " items");
                    debugOrderItemsText.setVisibility(View.VISIBLE);
                    android.util.Log.d("OrderDetail", "RecyclerView set to visible, debug text updated");
                } else {
                    android.util.Log.e("OrderDetail", "Adapter is null!");
                    debugOrderItemsText.setText("Error: Adapter is null");
                    debugOrderItemsText.setVisibility(View.VISIBLE);
                }
            } else {
                android.util.Log.d("OrderDetail", "Cart items list is empty");
                debugOrderItemsText.setText("Cart items list is empty");
                debugOrderItemsText.setVisibility(View.VISIBLE);
                orderItemsRecyclerView.setVisibility(View.GONE);
            }
        } else {
            android.util.Log.d("OrderDetail", "Cart items is null");
            debugOrderItemsText.setText("Cart items is null");
            debugOrderItemsText.setVisibility(View.VISIBLE);
            orderItemsRecyclerView.setVisibility(View.GONE);
        }
        
        android.util.Log.d("OrderDetail", "=== End displayOrderDetail ===");
    }
    
    private void showLoading(boolean show) {
        loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        contentView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void navigateToProductDetail(int productId, String productName) {
        ProductDetailFragment productDetailFragment = ProductDetailFragment.newInstance(productId, productName);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, productDetailFragment)
                .addToBackStack(null)
                .commit();
    }
    
    private void addAllItemsToCartAndNavigate() {
        if (currentOrderDetail == null || currentOrderDetail.getCartItems() == null || currentOrderDetail.getCartItems().isEmpty()) {
            Toast.makeText(getContext(), "No items to add to cart", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading while adding items
        buyAgainButton.setEnabled(false);
        buyAgainButton.setText("Adding to cart...");

        // Observe loading state to know when to navigate
        cartViewModel.getIsLoading().observe(this, isLoading -> {
            if (!isLoading && !buyAgainButton.isEnabled()) {
                // Cart loading finished and button was disabled (meaning we just added items)
                buyAgainButton.setEnabled(true);
                buyAgainButton.setText("Buy Again");
                
                Toast.makeText(getContext(), "All items added to cart!", Toast.LENGTH_SHORT).show();
                
                // Navigate to cart fragment
                CartFragment cartFragment = new CartFragment();
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, cartFragment)
                        .addToBackStack(null)
                        .commit();
                
                // Remove the observer to prevent multiple navigations
                cartViewModel.getIsLoading().removeObservers(this);
            }
        });

        // Add all items to cart using the CartViewModel method
        for (CartItem orderItem : currentOrderDetail.getCartItems()) {
            if (orderItem != null) {
                // Use CartViewModel.addToCart(productId, quantity)
                cartViewModel.addToCart(orderItem.getProductID(), orderItem.getQuantity());
                
                Log.d("OrderDetail", "Added to cart: " + orderItem.getProductName() + " x" + orderItem.getQuantity());
            }
        }
    }
}
