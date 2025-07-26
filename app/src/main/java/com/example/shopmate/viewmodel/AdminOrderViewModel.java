package com.example.shopmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.Order;
import com.example.shopmate.data.model.CartItem;
import com.example.shopmate.data.model.User;
import com.example.shopmate.data.model.UpdateOrderStatusRequest;
import com.example.shopmate.data.network.OrderApi;
import com.example.shopmate.data.network.UserApi;
import com.example.shopmate.data.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrderViewModel extends ViewModel {

    private OrderApi orderApi;
    private UserApi userApi;
    private List<Order> allOrders; // Danh sách đầy đủ từ API
    private List<Order> filteredOrders; // Danh sách sau khi filter

    private MutableLiveData<List<Order>> orders = new MutableLiveData<>();
    private MutableLiveData<Order> orderDetail = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();

    // Biến lưu trạng thái filter hiện tại
    private String currentSearchQuery = "";
    private String currentStatusFilter = "All";

    public AdminOrderViewModel() {
        orderApi = RetrofitClient.getInstance().create(OrderApi.class);
        userApi = RetrofitClient.getInstance().create(UserApi.class);
        // Khởi tạo danh sách rỗng
        allOrders = new ArrayList<>();
        filteredOrders = new ArrayList<>();
    }

    // Getters for LiveData
    public LiveData<List<Order>> getOrders() { return orders; }
    public LiveData<Order> getOrderDetail() { return orderDetail; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getOperationSuccess() { return operationSuccess; }

    // Load tất cả orders từ API /v1/orders
    public void loadAllOrders() {
        isLoading.setValue(true);
        errorMessage.setValue("");

        orderApi.getAllOrders(0, 100).enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Order>> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        allOrders = apiResponse.getData();
                        
                        // Process each order để tính toán total amount và items
                        for (Order order : allOrders) {
                            processOrderData(order);
                        }
                        
                        // Fetch usernames for all orders
                        fetchUsernamesForOrders(allOrders);
                        
                        filteredOrders = new ArrayList<>(allOrders);
                        orders.setValue(filteredOrders);
                    } else {
                        errorMessage.setValue("Lỗi: " + apiResponse.getMessage());
                        orders.setValue(new ArrayList<>());
                    }
                } else {
                    errorMessage.setValue("Lỗi mạng: " + response.code());
                    orders.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
                orders.setValue(new ArrayList<>());
            }
        });
    }

    // Load orders theo user ID - tận dụng lại method có sẵn
    public void loadOrdersByUserId(int userId) {
        isLoading.setValue(true);
        errorMessage.setValue("");

        orderApi.getOrdersByUserId(userId).enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Order>> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        allOrders = apiResponse.getData();
                        
                        // Process each order để tính toán total amount và items
                        for (Order order : allOrders) {
                            processOrderData(order);
                        }
                        
                        // Fetch usernames for all orders
                        fetchUsernamesForOrders(allOrders);
                        
                        filteredOrders = new ArrayList<>(allOrders);
                        orders.setValue(filteredOrders);
                    } else {
                        errorMessage.setValue("Lỗi: " + apiResponse.getMessage());
                        orders.setValue(new ArrayList<>());
                    }
                } else {
                    errorMessage.setValue("Lỗi mạng: " + response.code());
                    orders.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
                orders.setValue(new ArrayList<>());
            }
        });
    }

    public void loadOrders() {
        // Default load all orders
        loadAllOrders();
    }

    // Quick access method - search order by ID
    public void searchOrderById(int orderId) {
        getOrderDetail(orderId);
    }

    // Search theo tiêu đề order (ví dụ "Order 1")
    public void searchOrders(String query) {
        currentSearchQuery = query != null ? query.trim() : "";
        applyFilters();
    }

    // Filter theo trạng thái
    public void filterByStatus(String status) {
        currentStatusFilter = status != null ? status : "All";
        applyFilters();
    }

    // Áp dụng cả search và filter
    private void applyFilters() {
        isLoading.setValue(true);
        errorMessage.setValue("");

        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            isLoading.setValue(false);

            List<Order> result = new ArrayList<>(allOrders);

            // Áp dụng filter trạng thái trước
            if (!currentStatusFilter.equals("All") && !currentStatusFilter.isEmpty()) {
                result = result.stream()
                    .filter(order -> order.getOrderStatus() != null &&
                            order.getOrderStatus().equalsIgnoreCase(currentStatusFilter))
                    .collect(Collectors.toList());
            }

            // Sau đó áp dụng search theo ID order hoặc user info
            if (!currentSearchQuery.isEmpty()) {
                final String searchLower = currentSearchQuery.toLowerCase();
                result = result.stream()
                    .filter(order -> {
                        String orderId = String.valueOf(order.getId());
                        String billingAddress = order.getBillingAddress() != null ? order.getBillingAddress() : "";
                        String paymentMethod = order.getPaymentMethod() != null ? order.getPaymentMethod() : "";
                        
                        return orderId.contains(searchLower) ||
                               billingAddress.toLowerCase().contains(searchLower) ||
                               paymentMethod.toLowerCase().contains(searchLower);
                    })
                    .collect(Collectors.toList());
            }

            filteredOrders = result;

            if (filteredOrders.isEmpty()) {
                errorMessage.setValue("Không tìm thấy đơn hàng phù hợp");
                orders.setValue(new ArrayList<>());
            } else {
                orders.setValue(filteredOrders);
            }
        }, 300);
    }

    // Reset về trạng thái ban đầu
    public void resetFilters() {
        currentSearchQuery = "";
        currentStatusFilter = "All";
        loadOrders();
    }

    public void updateOrderStatus(int orderId, String newStatus) {
        isLoading.setValue(true);
        errorMessage.setValue("");

        Call<ApiResponse<Order>> call;
        
        // Use specific endpoints based on status
        switch (newStatus.toLowerCase()) {
            case "processing":
                call = orderApi.updateOrderToProcessing(orderId);
                break;
            case "delivered":
                call = orderApi.updateOrderToDelivered(orderId);
                break;
            case "cancelled":
                call = orderApi.updateOrderToCancelled(orderId);
                break;
            case "failed":
                call = orderApi.updateOrderToFailed(orderId, "Status updated by admin");
                break;
            default:
                // Fallback to legacy endpoint with request body
                UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(newStatus, "Status updated by admin");
                call = orderApi.updateOrderStatus(orderId, request);
                break;
        }

        call.enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Order> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        operationSuccess.setValue(true);
                    } else {
                        errorMessage.setValue(apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "Failed to update order status");
                    }
                } else {
                    errorMessage.setValue("Failed to update order status: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to update order status: " + t.getMessage());
            }
        });
    }

    public void getOrderDetail(int orderId) {
        isLoading.setValue(true);
        errorMessage.setValue("");

        orderApi.getOrderById(orderId).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Order> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Order order = apiResponse.getData();
                        processOrderData(order); // Process data trước khi set
                        
                        // Fetch username cho order detail
                        fetchUsernameForOrderDetail(order);
                    } else {
                        errorMessage.setValue(apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "Failed to load order detail");
                    }
                } else {
                    errorMessage.setValue("Failed to load order detail: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to load order detail: " + t.getMessage());
            }
        });
    }

    // Clear flags
    public void clearOperationSuccess() {
        operationSuccess.setValue(false);
    }

    public void clearError() {
        errorMessage.setValue("");
    }

    /**
     * Process order data để tính toán total amount, items count và customer name
     */
    private void processOrderData(Order order) {
        // 1. Tính total amount từ payments hoặc cart items
        double totalAmount = 0;
        if (order.getPayments() != null && !order.getPayments().isEmpty()) {
            // Lấy amount từ payment đầu tiên
            totalAmount = order.getPayments().get(0).getAmount();
        } else if (order.getCartItems() != null) {
            // Tính tổng từ cart items
            for (CartItem item : order.getCartItems()) {
                if (item.getSubtotal() != null) {
                    totalAmount += item.getSubtotal().doubleValue();
                }
            }
        }
        order.setTotalAmount(totalAmount);

        // 2. Tính total items từ cart items
        int totalItems = 0;
        if (order.getCartItems() != null) {
            for (CartItem item : order.getCartItems()) {
                totalItems += item.getQuantity();
            }
        }
        order.setTotalItems(totalItems);

        // 3. Set customer name - sẽ được update sau khi fetch username
        if (order.getUserName() == null || order.getUserName().isEmpty()) {
            order.setUserName("Loading..."); // Temporary placeholder
        }
    }

    /**
     * Fetch usernames cho tất cả orders để hiển thị tên thật thay vì ID
     */
    private void fetchUsernamesForOrders(List<Order> orderList) {
        for (Order order : orderList) {
            fetchUsernameForOrder(order);
        }
    }

    /**
     * Fetch username cho một order cụ thể
     */
    private void fetchUsernameForOrder(Order order) {
        userApi.getUserById(order.getUserId()).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        User user = apiResponse.getData();
                        String username = user.getUsername();
                        
                        // Update order với username thực
                        order.setUserName(username != null && !username.isEmpty() ? 
                                         username : "Customer #" + order.getUserId());
                        
                        // Trigger UI update
                        updateOrdersUI();
                    } else {
                        // Fallback to Customer ID if API fails
                        order.setUserName("Customer #" + order.getUserId());
                        updateOrdersUI();
                    }
                } else {
                    // Fallback to Customer ID if API fails
                    order.setUserName("Customer #" + order.getUserId());
                    updateOrdersUI();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                // Fallback to Customer ID if API fails
                order.setUserName("Customer #" + order.getUserId());
                updateOrdersUI();
            }
        });
    }

    /**
     * Update UI sau khi fetch username
     */
    private void updateOrdersUI() {
        // Trigger UI update với current filtered orders
        orders.postValue(new ArrayList<>(filteredOrders));
    }

    /**
     * Fetch username cho order detail
     */
    private void fetchUsernameForOrderDetail(Order order) {
        userApi.getUserById(order.getUserId()).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        User user = apiResponse.getData();
                        String username = user.getUsername();
                        
                        // Update order với username thực
                        order.setUserName(username != null && !username.isEmpty() ? 
                                         username : "Customer #" + order.getUserId());
                    } else {
                        // Fallback to Customer ID if API fails
                        order.setUserName("Customer #" + order.getUserId());
                    }
                } else {
                    // Fallback to Customer ID if API fails
                    order.setUserName("Customer #" + order.getUserId());
                }
                
                // Set order detail sau khi có username
                orderDetail.setValue(order);
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                // Fallback to Customer ID if API fails
                order.setUserName("Customer #" + order.getUserId());
                orderDetail.setValue(order);
            }
        });
    }

    public void updatePaymentStatus(int paymentId, String newStatus) {
        isLoading.setValue(true);
        
        // Create PaymentApi instance
        com.example.shopmate.data.network.PaymentApi paymentApi = 
            RetrofitClient.getInstance().create(com.example.shopmate.data.network.PaymentApi.class);
        
        // Create request body
        com.example.shopmate.data.model.UpdatePaymentStatusRequest request = 
            new com.example.shopmate.data.model.UpdatePaymentStatusRequest(newStatus);
        
        Call<ApiResponse<com.example.shopmate.data.model.Payment>> call = 
            paymentApi.updatePaymentStatus(paymentId, request);
        
        call.enqueue(new Callback<ApiResponse<com.example.shopmate.data.model.Payment>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.example.shopmate.data.model.Payment>> call, 
                                 Response<ApiResponse<com.example.shopmate.data.model.Payment>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 1000) {
                        operationSuccess.setValue(true);
                        // Reload orders to refresh payment status
                        loadOrders();
                    } else {
                        errorMessage.setValue(response.body().getMessage());
                    }
                } else {
                    errorMessage.setValue("Failed to update payment status");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.example.shopmate.data.model.Payment>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void searchAndFilterOrders(String searchQuery, String statusFilter, String sortFilter) {
        if (allOrders == null || allOrders.isEmpty()) {
            // Nếu chưa có data, load trước
            loadOrders();
            return;
        }

        List<Order> filtered = new ArrayList<>(allOrders);

        // Apply status filter
        if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equals("All Status")) {
            filtered = filtered.stream()
                    .filter(order -> order.getStatus().equalsIgnoreCase(statusFilter))
                    .collect(Collectors.toList());
        }

        // Apply search by customer name
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String query = searchQuery.trim().toLowerCase();
            filtered = filtered.stream()
                    .filter(order -> {
                        String customerName = order.getUserName();
                        return customerName != null && customerName.toLowerCase().contains(query);
                    })
                    .collect(Collectors.toList());
        }

        // Apply sorting
        if (sortFilter != null && !sortFilter.equals("Default")) {
            switch (sortFilter) {
                case "Total Amount (Low to High)":
                    filtered.sort((o1, o2) -> Double.compare(o1.getTotalAmount(), o2.getTotalAmount()));
                    break;
                case "Total Amount (High to Low)":
                    filtered.sort((o1, o2) -> Double.compare(o2.getTotalAmount(), o1.getTotalAmount()));
                    break;
                case "Order ID (Low to High)":
                    filtered.sort((o1, o2) -> Integer.compare(o1.getId(), o2.getId()));
                    break;
                case "Order ID (High to Low)":
                    filtered.sort((o1, o2) -> Integer.compare(o2.getId(), o1.getId()));
                    break;
            }
        }

        orders.setValue(filtered);
    }
}
