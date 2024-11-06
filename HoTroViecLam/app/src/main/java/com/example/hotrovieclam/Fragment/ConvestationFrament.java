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
import java.util.HashSet;
import java.util.Set;

public class ConvestationFrament extends Fragment {


    private ActivityConversationBinding binding;
    private ArrayList<Message> conversationList = new ArrayList<>();
    private String currentUserId;
   private  FirebaseFirestore db;
    ConversationAdapter adapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityConversationBinding.inflate(inflater, container, false);

        UserSessionManager sessionManager = new UserSessionManager();
        currentUserId = sessionManager.getUserUid();


        adapter = new ConversationAdapter(conversationList, (FragmentActivity) getContext());
        binding.recyclerViewConversations.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewConversations.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        fetchJobsFromFirestore();


        // Lắng nghe sự kiện nhấn vào các mục trong RecyclerView
        binding.recyclerViewConversations.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                View childView = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (childView != null && motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    int position = recyclerView.getChildAdapterPosition(childView);
                    Message message = conversationList.get(position);

                    // Lấy ID người gửi và người nhận
                    String senderId = currentUserId;  // ID người dùng hiện tại
                    String receiverId = message.getReceiver_id();  // ID người nhận từ cuộc hội thoại

                    // Chuyển sang MessageFragment
                    openMessageFragment(senderId, receiverId);
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                // Không cần xử lý gì ở đây
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                // Không cần xử lý nếu không cần tắt sự kiện
            }
        });
        return binding.getRoot();

    }


    private void openMessageFragment(String senderId, String receiverId) {
        // Tạo MessageFragment và truyền dữ liệu vào
        MessageFrament messageFragment = new MessageFrament();

        // Sử dụng Bundle để truyền dữ liệu
        Bundle bundle = new Bundle();
        bundle.putString("senderId", senderId);
        bundle.putString("receiverId", receiverId);
        messageFragment.setArguments(bundle);

        // Chuyển fragment bằng FragmentTransaction
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, messageFragment); // R.id.fragment_container là nơi chứa các fragment
        transaction.addToBackStack(null);  // Thêm vào back stack nếu muốn quay lại
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
                Log.w("zz", "Ldd");
                conversationList.clear();

                // Tạo một Set để theo dõi các receiver_id đã tồn tại trong conversationList
                Set<String> existingReceiverIds = new HashSet<>();
                Set<String> existingSenderIds = new HashSet<>();
                for (QueryDocumentSnapshot document : snapshots) {
                    Message message = document.toObject(Message.class);
//


                    if (message.getSender_id() != null && message.getSender_id().equals(currentUserId)||message.getReceiver_id() != null && message.getReceiver_id().equals(currentUserId)) {
                        String receiverId = message.getReceiver_id();
                        String senderId = message.getSender_id();
                        // Kiểm tra receiver_id không null và chưa tồn tại trong Set
                        if (senderId != null && receiverId != null && !existingReceiverIds.contains(receiverId)&&!existingSenderIds.contains(senderId)) {
                            conversationList.add(message);
                            existingReceiverIds.add(receiverId);
                            existingSenderIds.add(senderId);
                        }
                    }
                }

// Chỉ gọi notifyDataSetChanged một lần sau khi xử lý tất cả các tin nhắn
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
