package com.example.shopmate.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.shopmate.data.model.ChatMessage;
import com.example.shopmate.data.repository.ChatRepository;

import java.util.List;

public class ChatViewModel extends AndroidViewModel {
    private final ChatRepository chatRepository;
    private final LiveData<List<ChatMessage>> chatMessages;
    private final LiveData<Boolean> isLoading;
    private final LiveData<String> errorMessage;
    private final MediatorLiveData<Boolean> isSendingMessage = new MediatorLiveData<>();
    
    private boolean chatWithAdmin = false;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        chatRepository = ChatRepository.getInstance(application);
        chatMessages = chatRepository.getChatMessages();
        isLoading = chatRepository.getIsLoading();
        errorMessage = chatRepository.getErrorMessage();
        
        // Initialize chat history
        loadChatHistory();
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
    
    public LiveData<Boolean> getIsSendingMessage() {
        return isSendingMessage;
    }
    
    public boolean isChatWithAdmin() {
        return chatWithAdmin;
    }
    
    public void setChatWithAdmin(boolean chatWithAdmin) {
        this.chatWithAdmin = chatWithAdmin;
    }

    public void loadChatHistory() {
        chatRepository.loadChatHistory();
    }

    public void sendMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        
        isSendingMessage.setValue(true);
        chatRepository.sendMessage(message.trim(), chatWithAdmin);
        
        // Delay để đảm bảo message được gửi xong trước khi refresh
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Wait 1 second
                loadChatHistory();
                isSendingMessage.postValue(false);
            } catch (InterruptedException e) {
                isSendingMessage.postValue(false);
            }
        }).start();
    }
    
    public void switchToAI() {
        chatWithAdmin = false;
    }
    
    public void switchToAdmin() {
        chatWithAdmin = true;
    }
    
    public void refreshChatHistory() {
        loadChatHistory();
    }
} 