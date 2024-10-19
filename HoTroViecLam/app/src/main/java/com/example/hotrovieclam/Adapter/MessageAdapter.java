package com.example.tvl;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_APPLICANT = 1;
    private static final int VIEW_TYPE_RECRUITER = 2;

    private List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        // Phân loại dựa trên người gửi
        if (message.isFromApplicant()) {
            return VIEW_TYPE_APPLICANT;
        } else {
            return VIEW_TYPE_RECRUITER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_APPLICANT) {
            //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            //return new ApplicantMessageViewHolder(view);

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            return new RecruiterMessageViewHolder(view);
        } else {
            //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            //return new RecruiterMessageViewHolder(view);

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            return new ApplicantMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder instanceof ApplicantMessageViewHolder) {
            ((ApplicantMessageViewHolder) holder).bind(message);
        } else {
            ((RecruiterMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // ViewHolder cho ứng viên
    public static class ApplicantMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        public ApplicantMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textViewMessageLeft);
        }

        public void bind(Message message) {
            messageText.setText(message.getContent());
        }
    }

    // ViewHolder cho nhà tuyển dụng
    public static class RecruiterMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        public RecruiterMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textViewMessageRight);
        }

        public void bind(Message message) {
            messageText.setText(message.getContent());
        }
    }
}

