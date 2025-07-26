package com.example.shopmate.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopmate.R;
import com.example.shopmate.data.model.ChatMessage;
import com.example.shopmate.data.repository.ChatRepository;
import com.example.shopmate.ui.adapters.ChatAdapter;
import com.example.shopmate.viewmodel.ChatViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class ChatFragment extends Fragment {
    private ChatViewModel viewModel;
    private ChatAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textViewEmpty;
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private TabLayout tabLayout;
    
    private boolean showingAdminChat = false;
    private Timer autoRefreshTimer;
    private static final long AUTO_REFRESH_INTERVAL = 5000; // 5 seconds

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        progressBar = view.findViewById(R.id.progressBar);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);
        tabLayout = view.findViewById(R.id.tabLayout);
        
        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        
        adapter = new ChatAdapter(requireContext());
        recyclerView.setAdapter(adapter);
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        
        // Setup observers
        setupObservers();
        
        // Setup click listeners
        setupClickListeners();
        
        // Setup tab listener
        setupTabListener();
    }

    private void setupObservers() {
        viewModel.getChatMessages().observe(getViewLifecycleOwner(), messages -> {
            List<ChatMessage> filteredMessages;
            
            if (showingAdminChat) {
                // Show only admin messages
                filteredMessages = messages.stream()
                        .filter(msg -> msg.getUserId() == ChatRepository.ADMIN_ID || 
                                msg.getReceiverId() == ChatRepository.ADMIN_ID)
                        .collect(Collectors.toList());
            } else {
                // Show only AI messages
                filteredMessages = messages.stream()
                        .filter(msg -> msg.getUserId() == ChatRepository.AI_ID || 
                                msg.getReceiverId() == ChatRepository.AI_ID || 
                                msg.isFromAI())
                        .collect(Collectors.toList());
            }
            
            // Lưu số lượng message hiện tại trước khi update
            int previousCount = adapter.getItemCount();
            
            adapter.submitList(new ArrayList<>(filteredMessages), () -> {
                // Callback được gọi sau khi adapter đã update xong
                if (filteredMessages.isEmpty()) {
                    textViewEmpty.setVisibility(View.VISIBLE);
                } else {
                    textViewEmpty.setVisibility(View.GONE);
                    
                    // Tự động scroll xuống cuối khi có message mới
                    int newCount = filteredMessages.size();
                    if (newCount > 0) {
                        // Sử dụng post để đảm bảo RecyclerView đã layout xong
                        recyclerView.post(() -> {
                            recyclerView.smoothScrollToPosition(newCount - 1);
                        });
                    }
                }
            });
        });
        
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupClickListeners() {
        buttonSend.setOnClickListener(v -> {
            String message = editTextMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                viewModel.sendMessage(message);
                editTextMessage.setText("");
            }
        });
    }
    
    private void setupTabListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Tab 0 is AI, Tab 1 is Admin
                showingAdminChat = tab.getPosition() == 1;
                viewModel.setChatWithAdmin(showingAdminChat);
                
                // Refresh the message list to filter based on selected tab
                List<ChatMessage> messages = viewModel.getChatMessages().getValue();
                if (messages != null) {
                    List<ChatMessage> filteredMessages;
                    
                    if (showingAdminChat) {
                        // Show only admin messages
                        filteredMessages = messages.stream()
                                .filter(msg -> msg.getUserId() == ChatRepository.ADMIN_ID || 
                                        msg.getReceiverId() == ChatRepository.ADMIN_ID)
                                .collect(Collectors.toList());
                    } else {
                        // Show only AI messages
                        filteredMessages = messages.stream()
                                .filter(msg -> msg.getUserId() == ChatRepository.AI_ID || 
                                        msg.getReceiverId() == ChatRepository.AI_ID || 
                                        msg.isFromAI())
                                .collect(Collectors.toList());
                    }
                    
                    adapter.submitList(new ArrayList<>(filteredMessages), () -> {
                        // Callback được gọi sau khi adapter đã update xong
                        if (filteredMessages.isEmpty()) {
                            textViewEmpty.setVisibility(View.VISIBLE);
                        } else {
                            textViewEmpty.setVisibility(View.GONE);
                            // Tự động scroll xuống cuối khi chuyển tab
                            recyclerView.post(() -> {
                                recyclerView.smoothScrollToPosition(filteredMessages.size() - 1);
                            });
                        }
                    });
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed
            }
        });
    }
    
    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadChatHistory();
        startAutoRefresh();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        stopAutoRefresh();
    }
    
    private void startAutoRefresh() {
        stopAutoRefresh(); // Stop any existing timer
        autoRefreshTimer = new Timer();
        autoRefreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        viewModel.loadChatHistory();
                    });
                }
            }
        }, AUTO_REFRESH_INTERVAL, AUTO_REFRESH_INTERVAL);
    }
    
    private void stopAutoRefresh() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.cancel();
            autoRefreshTimer = null;
        }
    }
} 