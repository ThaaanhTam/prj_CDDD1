package com.example.hotrovieclam;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.hotrovieclam.Adapter.ConversationAdapter;
import com.example.hotrovieclam.Model.Conversation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import com.example.hotrovieclam.databinding.ActivityConversationBinding;
public class ConvestationFrament extends Fragment {

    private ActivityConversationBinding binding;
    private List<Conversation> conversationList = new ArrayList<>();
    private ConversationAdapter conversationAdapter;
    private String currentUserId = "7yRXcjnLPhfDr3OJG8FpEdSsWv83"; // ID người dùng hiện tại
    private DatabaseReference conversationRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityConversationBinding.inflate(inflater, container, false);

        // Kết nối đến Firebase Realtime Database
        conversationRef = FirebaseDatabase.getInstance().getReference("conversations");
        setupRecyclerView();
        loadConversations();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.recyclerViewConversations.setLayoutManager(new LinearLayoutManager(requireContext()));
        conversationAdapter = new ConversationAdapter(conversationList, requireContext());
        binding.recyclerViewConversations.setAdapter(conversationAdapter);
    }

    private void loadConversations() {
        // Lấy các cuộc hội thoại của người dùng hiện tại
        conversationRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conversationList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot receiverSnapshot : snapshot.getChildren()) {
                        String receiverId = receiverSnapshot.getKey();
                        String userName = receiverSnapshot.child("userName").getValue(String.class);
                        String avatar = receiverSnapshot.child("avatar").getValue(String.class);
                        String content = receiverSnapshot.child("content").getValue(String.class);
                        Long sentAt = receiverSnapshot.child("sentAt").getValue(Long.class);
                        Boolean seen = receiverSnapshot.child("seen").getValue(Boolean.class);

                        if (receiverId != null && content != null && sentAt != null) {
                            Conversation conversation = new Conversation(
                                    receiverId,
                                    currentUserId,
                                    content,
                                    sentAt,
                                    seen != null && seen,
                                    userName,
                                    avatar
                            );
                            conversationList.add(conversation);
                        } else {
                            Log.w("ConversationFragment", "Thiếu dữ liệu trong cuộc hội thoại: " + receiverId);
                        }
                    }
                } else {
                    Log.w("ConversationFragment", "Không có cuộc hội thoại nào.");
                }
                conversationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ConversationFragment", "Lỗi khi tải cuộc hội thoại: " + error.getMessage());
            }
        });
    }

    private void addConversation(String receiverId, String userName, String avatar, String content) {
        Long sentAt = System.currentTimeMillis();
        boolean seen = false;

        DatabaseReference newConversationRef = conversationRef.child(currentUserId).child(receiverId);
        Conversation conversation = new Conversation(receiverId, currentUserId, content, sentAt, seen, userName, avatar);

        newConversationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    newConversationRef.setValue(conversation).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("ConversationFragment", "Thêm cuộc hội thoại thành công");
                        } else {
                            Log.e("ConversationFragment", "Lỗi khi thêm cuộc hội thoại: " + task.getException().getMessage());
                        }
                    });
                } else {
                    Log.w("ConversationFragment", "Cuộc hội thoại đã tồn tại với ID: " + receiverId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ConversationFragment", "Lỗi khi kiểm tra cuộc hội thoại: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Giải phóng binding khi view bị hủy
    }
}
