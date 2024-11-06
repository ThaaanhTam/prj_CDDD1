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
import java.util.List;

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
            // Hiển thị tin nhắn bên phải cho người dùng hiện tại
            holder.textViewMessageRight.setText(message.getContent());
            holder.textViewMessageRight.setVisibility(View.VISIBLE);
            holder.textViewMessageLeft.setVisibility(View.GONE);  // Ẩn tin nhắn bên trái
        } else {
            // Hiển thị tin nhắn bên trái cho tin nhắn của người khác
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
