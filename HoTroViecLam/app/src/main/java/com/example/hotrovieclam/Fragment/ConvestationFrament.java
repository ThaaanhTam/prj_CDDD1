package com.example.hotrovieclam.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotrovieclam.Adapter.ConversationAdapter;
import com.example.hotrovieclam.Model.Message;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityConversationBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConvestationFrament extends Fragment {

    private ActivityConversationBinding binding;
    private ArrayList<Message> conversationList = new ArrayList<>();
    private String currentUserId;
    private FirebaseFirestore db;
    private ConversationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityConversationBinding.inflate(inflater, container, false);

        UserSessionManager sessionManager = new UserSessionManager();
        currentUserId = sessionManager.getUserUid();

        adapter = new ConversationAdapter(conversationList, (FragmentActivity) getContext());
        binding.recyclerViewConversations.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewConversations.setAdapter(adapter);

        // Gọi hàm để tải các tin nhắn từ Firestore
        fetchJobsFromFirestore();

        // Lắng nghe sự kiện nhấn vào các mục trong RecyclerView
        binding.recyclerViewConversations.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                View childView = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (childView != null && motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    int position = recyclerView.getChildAdapterPosition(childView);
                    Message message = conversationList.get(position);

                    String senderId;
                    String receiverId;

                    // Kiểm tra để xác định đúng vai trò của senderId và receiverId
                    if (message.getSender_id().equals(currentUserId)) {
                        senderId = currentUserId;
                        receiverId = message.getReceiver_id();
                    } else {
                        senderId = message.getSender_id();
                        receiverId = currentUserId;
                    }

                    // Chuyển sang MessageFragment với senderId và receiverId đã xác định đúng
                    openMessageFragment(senderId, receiverId);
                    Log.d("Conversation", "Sender ID: " + senderId + " | Receiver ID: " + receiverId);
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                // Không cần xử lý ở đây
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                // Không cần xử lý ở đây
            }
        });


        return binding.getRoot();
    }

    private void openMessageFragment(String senderId, String receiverId) {
        MessageFrament messageFragment = new MessageFrament();
        Bundle bundle = new Bundle();
        bundle.putString("senderId", senderId);
        bundle.putString("receiverId", receiverId);
        messageFragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, messageFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void fetchJobsFromFirestore() {
        db = FirebaseFirestore.getInstance();
        db.collection("messages").addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                Log.w("zz", "Listen failed.", error);
                return;
            }

            if (snapshots != null && !snapshots.isEmpty()) {
                conversationList.clear();

                for (QueryDocumentSnapshot document : snapshots) {
                    Message message = document.toObject(Message.class);

                    // Kiểm tra nếu người dùng hiện tại là người gửi hoặc người nhận
                    if ((message.getSender_id() != null && message.getSender_id().equals(currentUserId)) ||
                            (message.getReceiver_id() != null && message.getReceiver_id().equals(currentUserId))) {
                        Log.d("zz", "Listen x.");
                        // Xác định ID của đối tác trò chuyện
                        String chatPartnerId = message.getSender_id().equals(currentUserId)
                                ? message.getReceiver_id() : message.getSender_id();

                        // Kiểm tra xem conversationList đã chứa cuộc hội thoại với đối tác này chưa
                        boolean exists = false;
                        for (Message existingMessage : conversationList) {
                            String existingPartnerId = existingMessage.getSender_id().equals(currentUserId)
                                    ? existingMessage.getReceiver_id() : existingMessage.getSender_id();
                            if (existingPartnerId.equals(chatPartnerId)) {
                                exists = true;
                                break;
                            }
                        }

                        // Nếu chưa có, thêm vào danh sách conversationList
                        if (!exists) {
                            conversationList.add(message);
                        }
                    }
                }

                // Cập nhật adapter
                adapter.notifyDataSetChanged();
            } else {
                Log.d("zz", "No current data in the collection");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
