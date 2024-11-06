package com.example.hotrovieclam.Adapter;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotrovieclam.Model.Message;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.Fragment.MessageFrament; // Đổi thành MessageActivity nếu cần
import com.example.hotrovieclam.databinding.ItemConversationBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private ArrayList<Message> conversationList;
    private FragmentActivity context;
    private  FirebaseFirestore db = FirebaseFirestore.getInstance();
    UserSessionManager sessionManager = new UserSessionManager();
    String currentUserId = sessionManager.getUserUid();
    public ConversationAdapter(ArrayList<Message> conversationList, FragmentActivity context) {
        this.conversationList = conversationList;
        this.context = context;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return   new ConversationViewHolder(ItemConversationBinding.inflate(context.getLayoutInflater(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        Message message = conversationList.get(position);
        String rec = message.getReceiver_id();
        if (currentUserId.equals(rec)){
            rec = message.getSender_id();
        }


        // Set an OnClickListener on the itemView to handle click events
        holder.itemView.setOnClickListener(v -> {
            // Lấy senderId và receiverId
            String senderId = message.getSender_id();
            String receiverId = message.getReceiver_id();

            // Tạo Bundle để truyền dữ liệu
            Bundle bundle = new Bundle();
            bundle.putString("senderId", senderId);
            bundle.putString("receiverId", receiverId);

            // Tạo MessageFragment mới và truyền dữ liệu qua Bundle
            MessageFrament messageFragment = new MessageFrament();
            messageFragment.setArguments(bundle);

            // Chuyển đến MessageFragment
            if (context != null) {
                FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, messageFragment);  // Chuyển sang MessageFragment
                transaction.addToBackStack(null);  // Cho phép quay lại fragment cũ
                transaction.commit();
            }
        });

     //   holder.binding.textViewUserName.setText(documentSnapshot.getString("name"));
        db.collection("users").document(rec)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                       holder.binding.textViewUserName.setText(documentSnapshot.getString("name"));
                    } else {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public class ConversationViewHolder extends RecyclerView.ViewHolder {
        public int position;
        ItemConversationBinding binding;
        public ConversationViewHolder(@NonNull ItemConversationBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;


        }
    }

}
