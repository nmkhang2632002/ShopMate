package com.example.shopmate.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shopmate.data.model.ChatHistoryResponse;
import com.example.shopmate.data.model.ChatMessage;
import com.example.shopmate.data.network.ChatApi;
import com.example.shopmate.data.network.RetrofitClient;
import com.example.shopmate.util.AuthManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository {
    private static final String TAG = "ChatRepository";
    private static ChatRepository instance;
    private final ChatApi chatApi;
    private final Context context;
    private final MutableLiveData<List<ChatMessage>> chatMessages = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    // Constants for AI and Admin IDs
    public static final int AI_ID = 23;
    public static final int ADMIN_ID = 40;

    private ChatRepository(Context context) {
        this.context = context.getApplicationContext();
        chatApi = RetrofitClient.getInstance().create(ChatApi.class);
    }

    public static synchronized ChatRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ChatRepository(context);
        }
        return instance;
    }

    public LiveData<List<ChatMessage>> getChatMessages() {
        return chatMessages;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadChatHistory() {
        isLoading.setValue(true);
        
        int userId = AuthManager.getInstance(context).getUserId();
        
        chatApi.getChatHistory(userId).enqueue(new Callback<ChatHistoryResponse>() {
            @Override
            public void onResponse(Call<ChatHistoryResponse> call, Response<ChatHistoryResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ChatHistoryResponse chatHistory = response.body();
                    List<ChatMessage> allMessages = new ArrayList<>();
                    
                    // Add AI messages - these are direct conversations with the AI
                    if (chatHistory.getAiMessages() != null) {
                        allMessages.addAll(chatHistory.getAiMessages());
                    }
                    
                    // Add admin messages - these are direct conversations with admin support
                    if (chatHistory.getAdminMessages() != null) {
                        allMessages.addAll(chatHistory.getAdminMessages());
                    }
                    
                    // Sort messages by time
                    Collections.sort(allMessages, (m1, m2) -> m1.getSentAt().compareTo(m2.getSentAt()));
                    
                    chatMessages.setValue(allMessages);
                } else {
                    errorMessage.setValue("Failed to load chat history");
                    Log.e(TAG, "Error loading chat history: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ChatHistoryResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Network error loading chat history", t);
            }
        });
    }
   
    public void sendMessage(String messageText, boolean toAdmin) {
        int userId = AuthManager.getInstance(context).getUserId();
        int receiverId = toAdmin ? ADMIN_ID : AI_ID;
        
        ChatMessage message = new ChatMessage(userId, receiverId, messageText);
        
        // Optimistically add message to UI
        List<ChatMessage> currentMessages = chatMessages.getValue();
        if (currentMessages != null) {
            List<ChatMessage> updatedMessages = new ArrayList<>(currentMessages);
            updatedMessages.add(message);
            chatMessages.setValue(updatedMessages);
        }
        
        chatApi.sendMessage(message).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    errorMessage.setValue("Failed to send message");
                    Log.e(TAG, "Error sending message: " + response.message());
                }
                // Refresh chat history to get correct state
                loadChatHistory();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Network error sending message", t);
                
                // Refresh chat history to get correct state
                loadChatHistory();
            }
        });
    }
} 