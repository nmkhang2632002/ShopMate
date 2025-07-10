package com.example.shopmate.repository;

import com.example.shopmate.model.Post;
import com.example.shopmate.network.PostApi;
import com.example.shopmate.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class PostRepository {
   private final PostApi postApi;

   public PostRepository() {
       postApi = RetrofitClient.getInstance().create(PostApi.class);
   }

   public void fetchPosts(Callback<List<Post>> callback) {
        postApi.getPosts().enqueue(callback);
   }
}
