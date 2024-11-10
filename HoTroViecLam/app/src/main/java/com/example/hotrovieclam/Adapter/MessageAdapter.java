package com.example.hotrovieclam.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotrovieclam.Model.Message;
import com.example.hotrovieclam.R;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private ArrayList<Message> messageList;
    private String currentUserId;

    public MessageAdapter(ArrayList<Message> messageList, String currentUserId) {
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

        if (message.getSender_id().equals(currentUserId)) {
            // Nếu người gửi là người dùng hiện tại, hiển thị tin nhắn bên phải
            holder.textViewMessageRight.setText(message.getContent());
            holder.textViewMessageRight.setVisibility(View.VISIBLE);
            holder.textViewMessageLeft.setVisibility(View.GONE);  // Ẩn tin nhắn bên trái
        } else {
            // Nếu người gửi là người khác, hiển thị tin nhắn bên trái
            holder.textViewMessageLeft.setText(message.getContent());
            holder.textViewMessageLeft.setVisibility(View.VISIBLE);
            holder.textViewMessageRight.setVisibility(View.GONE); // Ẩn tin nhắn bên phải
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessageRight, textViewMessageLeft;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessageRight = itemView.findViewById(R.id.textViewMessageRight);
            textViewMessageLeft = itemView.findViewById(R.id.textViewMessageLeft);
        }
    }
}
