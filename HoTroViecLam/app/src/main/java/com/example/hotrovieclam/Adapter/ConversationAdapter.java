package com.example.hotrovieclam.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Model.Conversation;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.Message; // Đổi thành MessageActivity nếu cần

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private List<Conversation> conversationList;
    private Context context;

    public ConversationAdapter(List<Conversation> conversationList, Context context) {
        this.conversationList = conversationList;
        this.context = context;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        Conversation conversation = conversationList.get(position);

        holder.userName.setText(conversation.getUserName());
        holder.lastMessage.setText(conversation.getContent());

        // Sử dụng Glide để load avatar
        Glide.with(context)
                .load(conversation.getAvatar())
                .placeholder(R.drawable.ic_default_avatar)
                .circleCrop()
                .into(holder.avatar);

        // Hiển thị trạng thái tin nhắn đã xem hoặc chưa
        holder.status.setImageResource(conversation.isSeen() ? R.drawable.ic_seen : R.drawable.ic_sent);

        // Khi click vào cuộc hội thoại
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Message.class); // Đổi thành MessageActivity nếu cần
            intent.putExtra("receiverId", conversation.getReceiverId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar, status;
        TextView userName, lastMessage;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.imageViewAvatar);
            userName = itemView.findViewById(R.id.textViewUserName);
            lastMessage = itemView.findViewById(R.id.textViewLastMessage);
            status = itemView.findViewById(R.id.imageViewStatus);
        }
    }
}
