package com.example.hotrovieclam.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotrovieclam.Adapter.MessageAdapter;
import com.example.hotrovieclam.Model.Message;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageFrament extends Fragment {

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messageList = new ArrayList<>();
    private EditText editTextMessage;
    private ImageButton imageButtonSend;

    private String currentUserId;
    private String senderId ;
    private String receiverId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_messege, container, false);

        // Lấy dữ liệu senderId và receiverId từ Bundle
        Bundle arguments = getArguments();
        if (arguments != null) {
            senderId = arguments.getString("senderId");
            receiverId = arguments.getString("receiverId");
        }


        UserSessionManager sessionManager = new UserSessionManager();
        currentUserId = sessionManager.getUserUid();

        recyclerViewMessages = view.findViewById(R.id.recyclerViewMessages);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        imageButtonSend = view.findViewById(R.id.imageButtonSend);

        setupRecyclerView();
        loadMessages();

        imageButtonSend.setOnClickListener(view1 -> {
            String content = editTextMessage.getText().toString().trim();
            if (!content.isEmpty()) {
                sendMessage(content);
                editTextMessage.setText("");
            }
        });

        return view;
    }

    private void setupRecyclerView() {
        messageAdapter = new MessageAdapter(messageList, currentUserId);
        recyclerViewMessages.setAdapter(messageAdapter);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void sendMessage(String content) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tạo ID cho tin nhắn mới
        String messageId = db.collection("messages").document().getId();

        // Tạo Map chứa dữ liệu tin nhắn
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("sender_id", senderId);
        messageData.put("receiver_id", receiverId);
        messageData.put("content", content);
        messageData.put("sent_at", System.currentTimeMillis());
        messageData.put("read", false); // Bạn có thể theo dõi trạng thái tin nhắn đã đọc

        // Lưu tin nhắn vào Firestore
        db.collection("messages").document(messageId)
                .set(messageData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ChatApp", "Tin nhắn đã được gửi.");
                        loadMessages();
                    } else {
                        Log.e("ChatApp", "Lỗi gửi tin nhắn: " + task.getException().getMessage());
                    }
                });


    }

    private void loadMessages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lắng nghe tin nhắn gửi từ sender đến receiver
        db.collection("messages")
                .whereEqualTo("sender_id", senderId)
                .whereEqualTo("receiver_id", receiverId)
                .orderBy("sent_at")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("ChatApp", "Lỗi tải tin nhắn: " + e.getMessage());
                        return;
                    }

                    if (querySnapshot != null) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            addMessageToList(document);
                        }
                    }
                });

        // Lắng nghe tin nhắn gửi từ receiver đến sender
        db.collection("messages")
                .whereEqualTo("sender_id", receiverId)
                .whereEqualTo("receiver_id", senderId)
                .orderBy("sent_at")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("ChatApp", "Lỗi tải tin nhắn: " + e.getMessage());
                        return;
                    }
                    if (querySnapshot != null) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            addMessageToList(document);
                        }
                    }
                });
    }

    private void addMessageToList(QueryDocumentSnapshot document) {
        String msgSenderId = document.getString("sender_id");
        String msgReceiverId = document.getString("receiver_id");
        String content = document.getString("content");
        Long sentAt = document.getLong("sent_at");

        Message message = new Message();
        message.setContent(content);
        message.setReceiver_id(msgReceiverId);
        message.setSender_id(msgSenderId);
        message.setSent_at(sentAt);

        // Chỉ thêm tin nhắn mới vào danh sách nếu chưa tồn tại
        if (!messageList.contains(message)) {
            messageList.add(message);
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerViewMessages.scrollToPosition(messageList.size() - 1);
        }
    }

}