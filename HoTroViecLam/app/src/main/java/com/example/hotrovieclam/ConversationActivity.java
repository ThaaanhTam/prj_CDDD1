package com.example.hotrovieclam;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.hotrovieclam.Adapter.ConversationAdapter;
import com.example.hotrovieclam.Model.Conversation;
import com.example.hotrovieclam.databinding.ActivityConversationBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ConversationActivity extends AppCompatActivity {

    private ActivityConversationBinding binding;
    private List<Conversation> conversationList = new ArrayList<>();
    private ConversationAdapter conversationAdapter;
    private String currentUserId = "7yRXcjnLPhfDr3OJG8FpEdSsWv83"; // ID người dùng hiện tại
    private DatabaseReference conversationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Kết nối đến Firebase Realtime Database
        conversationRef = FirebaseDatabase.getInstance().getReference("conversations");
        setupRecyclerView();
        loadConversations();

        // Gọi addConversation để thêm cuộc hội thoại (thay đổi ID và nội dung theo nhu cầu)
        String receiverId = "someReceiverId"; // ID của người nhận
        String userName = "Tên người nhận"; // Tên người nhận
        String avatar = "URL_đến_avatar"; // URL của avatar
        String content = "Nội dung tin nhắn"; // Nội dung tin nhắn

        // Gọi addConversation để thêm cuộc hội thoại
        addConversation(receiverId, userName, avatar, content);
    }

    private void setupRecyclerView() {
        binding.recyclerViewConversations.setLayoutManager(new LinearLayoutManager(this));
        conversationAdapter = new ConversationAdapter(conversationList, this);
        binding.recyclerViewConversations.setAdapter(conversationAdapter);
    }

    private void loadConversations() {
        // Lấy các cuộc hội thoại của người dùng hiện tại
        conversationRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conversationList.clear();
                // Kiểm tra nếu có dữ liệu trong snapshot
                if (snapshot.exists()) {
                    for (DataSnapshot receiverSnapshot : snapshot.getChildren()) {
                        String receiverId = receiverSnapshot.getKey(); // Nhận ID người nhận từ khóa
                        String userName = receiverSnapshot.child("userName").getValue(String.class);
                        String avatar = receiverSnapshot.child("avatar").getValue(String.class);
                        String content = receiverSnapshot.child("content").getValue(String.class);
                        Long sentAt = receiverSnapshot.child("sentAt").getValue(Long.class);
                        Boolean seen = receiverSnapshot.child("seen").getValue(Boolean.class);

                        // Kiểm tra và tạo cuộc hội thoại
                        if (receiverId != null && content != null && sentAt != null) {
                            Conversation conversation = new Conversation(
                                    receiverId,
                                    currentUserId,
                                    content,
                                    sentAt,
                                    seen != null && seen, // Nếu seen không có giá trị, mặc định là false
                                    userName,
                                    avatar
                            );
                            conversationList.add(conversation);
                        } else {
                            Log.w("ConversationActivity", "Thiếu dữ liệu trong cuộc hội thoại: " + receiverId);
                        }
                    }
                } else {
                    Log.w("ConversationActivity", "Không có cuộc hội thoại nào.");
                }
                conversationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ConversationActivity", "Lỗi khi tải cuộc hội thoại: " + error.getMessage());
            }
        });
    }

    private void addConversation(String receiverId, String userName, String avatar, String content) {
        Long sentAt = System.currentTimeMillis(); // Thời gian gửi
        boolean seen = false; // Mặc định là false

        DatabaseReference newConversationRef = conversationRef.child(currentUserId).child(receiverId); // Đảm bảo bạn cập nhật đúng vị trí
        Conversation conversation = new Conversation(receiverId, currentUserId, content, sentAt, seen, userName, avatar);

        // Kiểm tra xem cuộc hội thoại đã tồn tại chưa
        newConversationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    newConversationRef.setValue(conversation).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("ConversationActivity", "Thêm cuộc hội thoại thành công");
                        } else {
                            Log.e("ConversationActivity", "Lỗi khi thêm cuộc hội thoại: " + task.getException().getMessage());
                        }
                    });
                } else {
                    Log.w("ConversationActivity", "Cuộc hội thoại đã tồn tại với ID: " + receiverId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ConversationActivity", "Lỗi khi kiểm tra cuộc hội thoại: " + databaseError.getMessage());
            }
        });
    }
}
