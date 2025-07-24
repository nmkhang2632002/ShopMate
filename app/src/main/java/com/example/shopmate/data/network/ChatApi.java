package com.example.shopmate.data.network;

import com.example.shopmate.data.model.ChatHistoryResponse;
import com.example.shopmate.data.model.ChatMessage;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChatApi {
    @POST("chat/send")
    Call<Void> sendMessage(@Body ChatMessage message);
    
    @GET("chat/history")
    Call<ChatHistoryResponse> getChatHistory(@Query("userID") int userId);
} 