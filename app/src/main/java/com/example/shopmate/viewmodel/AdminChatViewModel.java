package com.example.shopmate.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shopmate.data.model.ChatCustomer;
import com.example.shopmate.data.model.ChatHistoryResponse;
import com.example.shopmate.data.model.ChatMessage;
import com.example.shopmate.data.network.ChatApi;
import com.example.shopmate.data.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminChatViewModel extends AndroidViewModel {
    
    private static final String TAG = "AdminChatViewModel";
    private final ChatApi chatApi;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<ChatCustomer>> customers = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ChatCustomer> selectedCustomer = new MutableLiveData<>();
    private final MutableLiveData<List<ChatMessage>> chatMessages = new MutableLiveData<>(new ArrayList<>());
    
    public AdminChatViewModel(@NonNull Application application) {
        super(application);
        chatApi = RetrofitClient.getInstance().create(ChatApi.class);
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<List<ChatCustomer>> getCustomers() {
        return customers;
    }
    
    public LiveData<ChatCustomer> getSelectedCustomer() {
        return selectedCustomer;
    }
    
    public void setSelectedCustomer(ChatCustomer customer) {
        selectedCustomer.setValue(customer);
        loadChatHistory(customer.getId());
    }
    
    public LiveData<List<ChatMessage>> getChatMessages() {
        return chatMessages;
    }
    
    public void loadCustomerList() {
        isLoading.setValue(true);
        chatApi.getCustomerChatList().enqueue(new Callback<List<ChatCustomer>>() {
            @Override
            public void onResponse(Call<List<ChatCustomer>> call, Response<List<ChatCustomer>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Customer list loaded successfully: " + response.body().size() + " customers");
                    customers.setValue(response.body());
                } else {
                    Log.e(TAG, "Error loading customer list: " + response.code() + " " + response.message());
                    errorMessage.setValue("Failed to load customer list: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<List<ChatCustomer>> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Network error loading customer list", t);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
    
    public void loadChatHistory(int customerId) {
        isLoading.setValue(true);
        // Use the standard chat history endpoint with the customer ID
        chatApi.getChatHistory(customerId).enqueue(new Callback<ChatHistoryResponse>() {
            @Override
            public void onResponse(Call<ChatHistoryResponse> call, Response<ChatHistoryResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ChatHistoryResponse historyResponse = response.body();
                    
                    // Combine AI and admin messages
                    List<ChatMessage> combinedMessages = new ArrayList<>();
                    if (historyResponse.getAiMessages() != null) {
                        combinedMessages.addAll(historyResponse.getAiMessages());
                    }
                    if (historyResponse.getAdminMessages() != null) {
                        combinedMessages.addAll(historyResponse.getAdminMessages());
                    }
                    
                    chatMessages.setValue(combinedMessages);
                } else {
                    errorMessage.setValue("Failed to load chat history");
                }
            }
            
            @Override
            public void onFailure(Call<ChatHistoryResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
    
    public void sendMessage(String messageText) {
        ChatCustomer customer = selectedCustomer.getValue();
        if (customer == null) {
            errorMessage.setValue("No customer selected");
            return;
        }
        
        try {
            // Admin ID is 40 (as per your system)
            ChatMessage message = new ChatMessage(40, customer.getId(), messageText);
            
            // Optimistically add message to UI
            List<ChatMessage> currentMessages = chatMessages.getValue();
            if (currentMessages != null) {
                List<ChatMessage> updatedMessages = new ArrayList<>(currentMessages);
                updatedMessages.add(message);
                chatMessages.setValue(updatedMessages);
            }
            
            // Use the standard send message endpoint
            chatApi.sendMessage(message).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Message sent successfully, refreshing chat history");
                        // Delay một chút trước khi refresh để đảm bảo server đã xử lý xong
                        new Thread(() -> {
                            try {
                                Thread.sleep(1000); // Wait 1 second
                                // Sử dụng Handler thay vì getMainExecutor() để tương thích với API level thấp hơn
                                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                                    loadChatHistory(customer.getId());
                                });
                            } catch (InterruptedException e) {
                                // Handle interruption
                            } catch (Exception e) {
                                Log.e(TAG, "Error in delayed refresh", e);
                            }
                        }).start();
                    } else {
                        Log.e(TAG, "Failed to send message: " + response.code() + " " + response.message());
                        errorMessage.setValue("Failed to send message");
                        // Refresh chat history để lấy trạng thái chính xác
                        loadChatHistory(customer.getId());
                    }
                }
                
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Network error sending message", t);
                    errorMessage.setValue("Network error: " + t.getMessage());
                    // Refresh chat history để lấy trạng thái chính xác
                    loadChatHistory(customer.getId());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in sendMessage", e);
            errorMessage.setValue("Error sending message: " + e.getMessage());
        }
    }
} 