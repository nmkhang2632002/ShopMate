package com.example.shopmate.data.network;

import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.Payment;
import com.example.shopmate.data.model.UpdatePaymentStatusRequest;

import retrofit2.Call;
import retrofit2.http.*;

public interface PaymentApi {
    
    @PUT("payments/{paymentId}/status")
    Call<ApiResponse<Payment>> updatePaymentStatus(
            @Path("paymentId") int paymentId,
            @Body UpdatePaymentStatusRequest request
    );
}
