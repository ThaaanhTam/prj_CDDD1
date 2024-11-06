package com.example.hotrovieclam.Fragment;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    private DatabaseReference messagesRef;
    private String currentUserId;
    private String senderId;
    private String receiverId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_messege, container, false);

        // Lấy dữ liệu senderId và receiverId từ Bundle
        if (getArguments() != null) {
            senderId = getArguments().getString("senderId");
            receiverId = getArguments().getString("receiverId");
        }

        UserSessionManager sessionManager = new UserSessionManager();
        currentUserId = sessionManager.getUserUid();

        recyclerViewMessages = view.findViewById(R.id.recyclerViewMessages);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        imageButtonSend = view.findViewById(R.id.imageButtonSend);

        setupRecyclerView();
        loadMessages(senderId, receiverId);

        imageButtonSend.setOnClickListener(view1 -> {
            String content = editTextMessage.getText().toString().trim();
            if (!content.isEmpty()) {
                sendMessage(senderId, receiverId, content);
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

    private void sendMessage(String senderId, String receiverId, String content) {
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
                    } else {
                        Log.e("ChatApp", "Lỗi gửi tin nhắn: " + task.getException().getMessage());
                    }
                });
    }


    private void loadMessages(String senderId, String receiverId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Truy vấn tin nhắn từ Firestore giữa hai người dùng và lắng nghe thay đổi theo thời gian thực
        db.collection("messages")
                .whereIn("sender_id", Arrays.asList(senderId, receiverId))
                .whereIn("receiver_id", Arrays.asList(senderId, receiverId))
                .orderBy("sent_at")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("ChatApp", "Lỗi tải tin nhắn: " + e.getMessage());
                        return;
                    }

                    if (querySnapshot != null) {
                        messageList.clear();
                        boolean isInitialLoad = messageList.isEmpty();// Xóa danh sách cũ để cập nhật lại từ đầu
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String msgSenderId = document.getString("sender_id");
                            String msgReceiverId = document.getString("receiver_id");
                            String content = document.getString("content");
                           // long sentAt = document.getLong("sent_at");

                            // Thêm tin nhắn vào danh sách
                            Message message = new Message();
                            message.setContent(content);
                            message.setReceiver_id(msgReceiverId);
                            message.setSender_id(msgSenderId);

                            // Kiểm tra tin nhắn mới dựa trên thời gian gửi
                            if (!messageList.contains(message)) {
                                messageList.add(message); // Thêm tin nhắn mới vào danh sách
                            }
                        }
                        messageAdapter.notifyDataSetChanged();
                        // Chỉ cuộn xuống cuối nếu đang tải lần đầu hoặc có tin nhắn mới
                        if (isInitialLoad || querySnapshot.size() > messageList.size()) {
                            recyclerViewMessages.scrollToPosition(messageList.size() - 1); // Cuộn xuống tin nhắn mới nhất
                        }
                    }
                });
    }



}
