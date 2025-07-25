package com.example.shopmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.Order;
import com.example.shopmate.data.model.UpdateOrderStatusRequest;
import com.example.shopmate.data.network.OrderApi;
import com.example.shopmate.data.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrderViewModel extends ViewModel {

    private OrderApi orderApi;
    private List<Order> allMockOrders; // Danh sách đầy đủ mock data
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
        // Khởi tạo mock data
        allMockOrders = createMockOrders();
        filteredOrders = new ArrayList<>(allMockOrders);
    }

    // Getters for LiveData
    public LiveData<List<Order>> getOrders() { return orders; }
    public LiveData<Order> getOrderDetail() { return orderDetail; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getOperationSuccess() { return operationSuccess; }

    public void loadOrders() {
        isLoading.setValue(true);
        errorMessage.setValue("");

        // Sử dụng mock data thay vì API call
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            isLoading.setValue(false);
            // Reset filter và hiển thị tất cả orders
            currentSearchQuery = "";
            currentStatusFilter = "All";
            filteredOrders = new ArrayList<>(allMockOrders);
            orders.setValue(filteredOrders);
        }, 500);
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

            List<Order> result = new ArrayList<>(allMockOrders);

            // Áp dụng filter trạng thái trước
            if (!currentStatusFilter.equals("All") && !currentStatusFilter.isEmpty()) {
                result = result.stream()
                    .filter(order -> order.getOrderStatus() != null &&
                            order.getOrderStatus().equalsIgnoreCase(currentStatusFilter))
                    .collect(Collectors.toList());
            }

            // Sau đó áp dụng search theo tiêu đề order
            if (!currentSearchQuery.isEmpty()) {
                final String searchLower = currentSearchQuery.toLowerCase();
                result = result.stream()
                    .filter(order -> {
                        String orderTitle = "Order " + order.getId();
                        return orderTitle.toLowerCase().contains(searchLower);
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

    // Tạo mock orders cho admin
    private List<Order> createMockOrders() {
        List<Order> mockOrders = new ArrayList<>();

        // Order 1 - PENDING
        Order order1 = new Order();
        order1.setId(1);
        order1.setUserId(101);
        order1.setUserName("Nguyễn Văn A");
        order1.setUserEmail("nguyenvana@gmail.com");
        order1.setPaymentMethod("COD");
        order1.setBillingAddress("123 Nguyễn Huệ, Q1, TP.HCM");
        order1.setOrderStatus("PENDING");
        order1.setStatus("PENDING");
        order1.setTotalAmount(1500000);
        order1.setTotalItems(3);
        order1.setOrderDate(new java.util.Date());
        order1.setNote("Giao hàng giờ hành chính");
        mockOrders.add(order1);

        // Order 2 - CONFIRMED
        Order order2 = new Order();
        order2.setId(2);
        order2.setUserId(102);
        order2.setUserName("Trần Thị B");
        order2.setUserEmail("tranthib@gmail.com");
        order2.setPaymentMethod("CARD");
        order2.setBillingAddress("456 Lê Lợi, Q3, TP.HCM");
        order2.setOrderStatus("CONFIRMED");
        order2.setStatus("CONFIRMED");
        order2.setTotalAmount(2800000);
        order2.setTotalItems(2);
        order2.setOrderDate(new java.util.Date(System.currentTimeMillis() - 86400000)); // 1 day ago
        order2.setNote("Giao hàng cuối tuần");
        mockOrders.add(order2);

        // Order 3 - SHIPPED
        Order order3 = new Order();
        order3.setId(3);
        order3.setUserId(103);
        order3.setUserName("Lê Văn C");
        order3.setUserEmail("levanc@gmail.com");
        order3.setPaymentMethod("MOMO");
        order3.setBillingAddress("789 Võ Văn Tần, Q10, TP.HCM");
        order3.setOrderStatus("SHIPPED");
        order3.setStatus("SHIPPED");
        order3.setTotalAmount(950000);
        order3.setTotalItems(1);
        order3.setOrderDate(new java.util.Date(System.currentTimeMillis() - 172800000)); // 2 days ago
        order3.setNote("Gọi trước khi giao");
        mockOrders.add(order3);

        // Order 4 - DELIVERED
        Order order4 = new Order();
        order4.setId(4);
        order4.setUserId(104);
        order4.setUserName("Phạm Thị D");
        order4.setUserEmail("phamthid@gmail.com");
        order4.setPaymentMethod("COD");
        order4.setBillingAddress("321 Cách Mạng Tháng 8, Q3, TP.HCM");
        order4.setOrderStatus("DELIVERED");
        order4.setStatus("DELIVERED");
        order4.setTotalAmount(3200000);
        order4.setTotalItems(4);
        order4.setOrderDate(new java.util.Date(System.currentTimeMillis() - 259200000)); // 3 days ago
        order4.setNote("Đã giao thành công");
        mockOrders.add(order4);

        // Order 5 - CANCELLED
        Order order5 = new Order();
        order5.setId(5);
        order5.setUserId(105);
        order5.setUserName("Hoàng Văn E");
        order5.setUserEmail("hoangvane@gmail.com");
        order5.setPaymentMethod("CARD");
        order5.setBillingAddress("654 Hai Bà Trưng, Q1, TP.HCM");
        order5.setOrderStatus("CANCELLED");
        order5.setStatus("CANCELLED");
        order5.setTotalAmount(750000);
        order5.setTotalItems(1);
        order5.setOrderDate(new java.util.Date(System.currentTimeMillis() - 345600000)); // 4 days ago
        order5.setNote("Khách hàng hủy đơn");
        mockOrders.add(order5);

        return mockOrders;
    }

    public void updateOrderStatus(int orderId, String newStatus) {
        isLoading.setValue(true);
        errorMessage.setValue("");

        // Truyền đủ 2 tham số cho UpdateOrderStatusRequest (status và note)
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(newStatus, "Status updated by admin");

        orderApi.updateOrderStatus(orderId, request).enqueue(new Callback<ApiResponse<Order>>() {
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
                        orderDetail.setValue(apiResponse.getData());
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
}
