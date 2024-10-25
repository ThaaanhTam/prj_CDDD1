package com.example.hotrovieclam;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotrovieclam.Adapter.MessageAdapter;
import com.example.hotrovieclam.Model.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Messege extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList = new ArrayList<>();
    private EditText editTextMessage;
    private ImageButton imageButtonSend;

    // Firebase
    private DatabaseReference usersRef;
    private String currentUserId = "21sw8bnVlORGG6gdLVtZnBo8G4K2";  // ID của người dùng hiện tại
    private String recipientUserId = "DX7wNIO46KWi0OQUlLd1hKAtr552";  // ID của người nhận, bạn có thể thay đổi cho phù hợp

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messege);

        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        imageButtonSend = findViewById(R.id.imageButtonSend);

        // Cài đặt RecyclerView
        messageAdapter = new MessageAdapter(messageList, currentUserId); // Truyền currentUserId để xác định người gửi
        recyclerViewMessages.setAdapter(messageAdapter);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));

        // Tải tin nhắn khi ứng dụng khởi động
        loadMessages(currentUserId, recipientUserId);

        // Xử lý khi bấm nút gửi tin nhắn
        imageButtonSend.setOnClickListener(view -> {
            String content = editTextMessage.getText().toString().trim();
            if (!content.isEmpty()) {
                // Gửi tin nhắn đến Firebase
                sendMessage(currentUserId, recipientUserId, content);
                editTextMessage.setText(""); // Xóa nội dung sau khi gửi
            }
        });
    }

    // Hàm gửi tin nhắn
    private void sendMessage(String senderId, String receiverId, String content) {
        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("messages");

        // Tạo ID tin nhắn duy nhất
        String messageId = messageRef.push().getKey();

        // Tạo đối tượng tin nhắn
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("sender_id", senderId);
        messageData.put("receiver_id", receiverId);
        messageData.put("content", content);
        messageData.put("sent_at", System.currentTimeMillis()); // Lấy thời gian gửi hiện tại

        // Lưu tin nhắn vào Realtime Database
        messageRef.child(messageId).setValue(messageData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ChatApp", "Tin nhắn đã được gửi.");
                    } else {
                        Log.e("ChatApp", "Lỗi gửi tin nhắn: " + task.getException().getMessage());
                    }
                });
    }

    // Hàm tải tin nhắn
    private void loadMessages(String currentUserId, String recipientUserId) {
        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("messages");

        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageList.clear(); // Xóa danh sách tin nhắn cũ

                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String senderId = messageSnapshot.child("sender_id").getValue(String.class);
                    String receiverId = messageSnapshot.child("receiver_id").getValue(String.class);
                    String content = messageSnapshot.child("content").getValue(String.class);
                    Long sentAt = messageSnapshot.child("sent_at").getValue(Long.class);

                    // Kiểm tra nếu tin nhắn liên quan đến người dùng hiện tại và người nhận
                    if ((currentUserId.equals(senderId) && recipientUserId.equals(receiverId)) ||
                            (currentUserId.equals(receiverId) && recipientUserId.equals(senderId))) {
                        // Thêm tin nhắn vào danh sách
                        messageList.add(new Message(senderId, receiverId, content, sentAt.toString()));
                    }
                }

                // Cập nhật giao diện sau khi load tin nhắn
                messageAdapter.notifyDataSetChanged();
                recyclerViewMessages.scrollToPosition(messageList.size() - 1); // Tự động cuộn đến tin nhắn mới nhất
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ChatApp", "Lỗi tải tin nhắn: " + databaseError.getMessage());
            }
        });
    }
}
