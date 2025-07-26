package com.example.shopmate.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopmate.R;
import com.example.shopmate.data.model.ChatMessage;
import com.example.shopmate.data.repository.ChatRepository;
import com.example.shopmate.util.AuthManager;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChatAdapter extends ListAdapter<ChatMessage, ChatAdapter.MessageViewHolder> {
    private final int currentUserId;
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    
    // Constants for AI and Admin IDs
    public static final int AI_ID = 23;
    public static final int ADMIN_ID = 40;

    /**
     * Constructor that gets the current user ID from AuthManager
     */
    public ChatAdapter(Context context) {
        super(new MessageDiffCallback());
        this.currentUserId = AuthManager.getInstance(context).getUserId();
    }
    
    /**
     * Constructor that accepts a user ID directly (useful for admin chat)
     */
    public ChatAdapter(int userId) {
        super(new MessageDiffCallback());
        this.currentUserId = userId;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = getItem(position);
        if (message.getUserId() == currentUserId) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = getItem(position);
        holder.bind(message, getItemViewType(position));
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewMessage;
        private final TextView textViewTime;
        private final TextView textViewSender;
        private final CardView cardViewMessage;
        private final ConstraintLayout constraintLayout;
        private final Context context;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewSender = itemView.findViewById(R.id.textViewSender);
            cardViewMessage = itemView.findViewById(R.id.cardViewMessage);
            constraintLayout = (ConstraintLayout) itemView;
            context = itemView.getContext();
        }

        public void bind(ChatMessage message, int viewType) {
            textViewMessage.setText(message.getMessage());
            
            // Format time
            if (message.getSentAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
                textViewTime.setText(sdf.format(message.getSentAt()));
            } else {
                textViewTime.setText("Sending...");
            }

            // Configure layout based on message type
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            
            if (viewType == VIEW_TYPE_SENT) {
                // User's message (right side)
                cardViewMessage.setCardBackgroundColor(context.getResources().getColor(R.color.primary, null));
                textViewMessage.setTextColor(context.getResources().getColor(android.R.color.white, null));
                textViewTime.setTextColor(context.getResources().getColor(android.R.color.white, null));
                
                constraintSet.clear(R.id.cardViewMessage, ConstraintSet.START);
                constraintSet.connect(R.id.cardViewMessage, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 8);
                textViewSender.setVisibility(View.GONE);
            } else {
                // AI or Admin message (left side)
                cardViewMessage.setCardBackgroundColor(context.getResources().getColor(R.color.white, null));
                textViewMessage.setTextColor(context.getResources().getColor(android.R.color.black, null));
                textViewTime.setTextColor(context.getResources().getColor(android.R.color.darker_gray, null));
                
                constraintSet.clear(R.id.cardViewMessage, ConstraintSet.END);
                constraintSet.connect(R.id.cardViewMessage, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 8);
                
                // Show sender name for received messages
                textViewSender.setVisibility(View.VISIBLE);
                if (message.getUserId() == AI_ID || message.isFromAI()) {
                    textViewSender.setText(R.string.ai_assistant);
                } else if (message.getUserId() == ADMIN_ID) {
                    textViewSender.setText(R.string.customer_support);
                } else if (currentUserId == ADMIN_ID) {
                    // If current user is admin, show customer name
                    textViewSender.setText("Customer");
                } else {
                    textViewSender.setVisibility(View.GONE);
                }
            }
            
            constraintSet.applyTo(constraintLayout);
        }
    }

    private static class MessageDiffCallback extends DiffUtil.ItemCallback<ChatMessage> {
        @Override
        public boolean areItemsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            // Assuming messages with same user, receiver and timestamp are the same
            return oldItem.getUserId() == newItem.getUserId() 
                    && oldItem.getReceiverId() == newItem.getReceiverId()
                    && oldItem.getSentAt() != null 
                    && oldItem.getSentAt().equals(newItem.getSentAt());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            return oldItem.getMessage().equals(newItem.getMessage());
        }
    }
} 