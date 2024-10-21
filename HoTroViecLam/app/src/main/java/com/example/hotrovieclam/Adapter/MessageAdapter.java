package com.example.hotrovieclam.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotrovieclam.Model.Message;
import com.example.hotrovieclam.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;
    private String currentUserId;

    public MessageAdapter(List<Message> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (message.getSenderId().equals(currentUserId)) {
            // Nếu là tin nhắn do người dùng hiện tại gửi, hiển thị tin nhắn bên phải
            holder.textViewMessageRight.setText(message.getContent());
            holder.textViewMessageRight.setVisibility(View.VISIBLE);
            holder.textViewMessageLeft.setVisibility(View.GONE);
        } else {
            // Nếu là tin nhắn của người khác, hiển thị tin nhắn bên trái
            holder.textViewMessageLeft.setText(message.getContent());
            holder.textViewMessageLeft.setVisibility(View.VISIBLE);
            holder.textViewMessageRight.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMessageLeft;
        public TextView textViewMessageRight;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessageLeft = itemView.findViewById(R.id.textViewMessageLeft);
            textViewMessageRight = itemView.findViewById(R.id.textViewMessageRight);
        }
    }
}
