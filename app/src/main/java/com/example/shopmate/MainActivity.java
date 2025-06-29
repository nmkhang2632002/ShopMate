package com.example.shopmate;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopmate.view.PostAdapter;
import com.example.shopmate.viewmodel.PostViewModel;
import com.example.shopmate.view.LoginActivity;
public class MainActivity extends AppCompatActivity {

    private PostViewModel postViewModel;
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra trạng thái đăng nhập
        if (!isUserLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // Kết thúc MainActivity để người dùng không thể quay lại nếu chưa đăng nhập
            return;
        }

        // Nếu đã đăng nhập, tiếp tục khởi tạo màn hình chính
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setContentView(recyclerView);

        // Thiết lập RecyclerView với PostAdapter
        adapter = new PostAdapter();
        recyclerView.setAdapter(adapter);

        // Kết nối với ViewModel
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);

        // Quan sát LiveData để lấy danh sách bài viết và cập nhật adapter
        postViewModel.getPosts().observe(this, posts -> {
            adapter.setPosts(posts);
        });

        // Tải dữ liệu bài viết
        postViewModel.loadPosts();
    }

    // Hàm mô phỏng kiểm tra trạng thái đăng nhập của người dùng
    private boolean isUserLoggedIn() {
        // Đây là logic giả sử, bạn có thể thay thế bằng kiểm tra từ SharedPreferences hoặc Database
        return getSharedPreferences("ShopMatePrefs", MODE_PRIVATE).getBoolean("isLoggedIn", false);
    }
}
