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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotrovieclam.Adapter.MessageAdapter;
import com.example.hotrovieclam.Model.Message;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messageList = new ArrayList<>();
    private EditText editTextMessage;
    private ImageButton imageButtonSend;

    private String currentUserId;
    private String messageID;
    private String receiverID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messege);
        messageID = getIntent().getStringExtra("messageID");
        receiverID = getIntent().getStringExtra("receiverID");
        UserSessionManager sessionManager = new UserSessionManager();
        currentUserId = sessionManager.getUserUid();
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        imageButtonSend = findViewById(R.id.imageButtonSend);
        Log.d("ii", messageID);
        setupRecyclerView();
        loadMessages();


        // Xử lý sự kiện gửi tin nhắn
        imageButtonSend.setOnClickListener(view -> {
            String content = editTextMessage.getText().toString().trim();
            if (!content.isEmpty()) {
                sendMessage(content);
                editTextMessage.setText("");
            }
        });
    }

    private void setupRecyclerView() {
        messageAdapter = new MessageAdapter(messageList, currentUserId);
        recyclerViewMessages.setAdapter(messageAdapter);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
    }

    private void sendMessage(String content) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tạo ID cho tin nhắn mới

        // Tạo Map chứa dữ liệu tin nhắn
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("sender_id", currentUserId);  // Gửi tin nhắn từ người dùng hiện tại
        messageData.put("content", content);
        messageData.put("sent_at", System.currentTimeMillis());

        db.collection("Message").document(messageID)
                .collection("messages")
                .add(messageData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ChatApp", "Tin nhắn đã được gửi.");
                        loadMessages();  // Tải lại danh sách tin nhắn
                    } else {
                        Log.e("ChatApp", "Lỗi gửi tin nhắn: " + task.getException().getMessage());
                    }
                });


//        Map<String, Object> status = new HashMap<>();
//        status.put("status", "0");
//
//        db.collection("users").document(receiverID)
//                .collection("conversation")
//                .document(currentUserId)
//                .set(status) // Giữ lại các trường hiện có
//                .addOnSuccessListener(aVoid -> {
//                    Log.d("Firestore", "Thêm trường mới thành công!");
//                })
//                .addOnFailureListener(err -> {
//                    Log.e("Firestore", "Lỗi khi thêm trường mới: " + err.getMessage());
//                });
    }


    private void loadMessages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        Map<String, Object> status = new HashMap<>();
//        status.put("status", "1");
//
//        db.collection("users").document(currentUserId)
//                .collection("conversation")
//                .document(receiverID)
//                .set(status) // Giữ lại các trường hiện có
//                .addOnSuccessListener(aVoid -> {
//                    Log.d("Firestore", "Thêm trường mới thành công!");
//                })
//                .addOnFailureListener(err -> {
//                    Log.e("Firestore", "Lỗi khi thêm trường mới: " + err.getMessage());
//                });

        db.collection("Message").document(messageID)
                .collection("messages")
                .orderBy("sent_at")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("ChatApp", "Lỗi tải tin nhắn: " + e.getMessage());
                        return;
                    }
                    if (querySnapshot != null) {
                        messageList.clear();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String msgSenderId = document.getString("sender_id");
                            String content = document.getString("content");
                            Date sentAt = null;
                            Object sentAtObject = document.get("sent_at");
                            if (sentAtObject instanceof com.google.firebase.Timestamp) {
                                sentAt = ((com.google.firebase.Timestamp) sentAtObject).toDate();
                            } else if (sentAtObject instanceof Long) {
                                sentAt = new Date((Long) sentAtObject);
                            }
                            Message message = new Message();
                            message.setContent(content);
                            message.setSender_id(msgSenderId);
                            message.setSent_at(sentAt);

                            messageList.add(message);
                        }
                        messageAdapter.notifyDataSetChanged();
                        recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

}