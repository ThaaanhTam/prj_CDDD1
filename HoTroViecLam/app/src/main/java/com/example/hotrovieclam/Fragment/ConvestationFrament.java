package com.example.hotrovieclam.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hotrovieclam.Adapter.ConversationAdapter;
import com.example.hotrovieclam.Model.ListMess;
import com.example.hotrovieclam.Model.User;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityConversationBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ConvestationFrament extends Fragment {

    private ActivityConversationBinding binding;
    private ArrayList<ListMess> conversationList = new ArrayList<>();
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
if(currentUserId!=null){
    fetchJobsFromFirestore();
}






        return binding.getRoot();
    }



    private void fetchJobsFromFirestore() {
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(currentUserId)
                .collection("conversation")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Lỗi khi lắng nghe dữ liệu conversation: " + error.getMessage());
                        return;
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String documentId = document.getId(); // ID của người dùng khác
                            String messageID = document.getString("messageID");
                            String status = document.getString("status");

                            // Lắng nghe thay đổi từ bảng "users"
                            db.collection("users").document(documentId)
                                    .addSnapshotListener((userSnapshot, userError) -> {
                                        if (userError != null) {
                                            Log.e("Firestore", "Lỗi khi lắng nghe dữ liệu user: " + userError.getMessage());
                                            return;
                                        }

                                        if (userSnapshot != null && userSnapshot.exists()) {
                                            String name = userSnapshot.getString("name");
                                            String avatar = userSnapshot.getString("avatar");

                                            db.collection("users").document(documentId)
                                                    .collection("role")
                                                    .document("employer")
                                                    .get()
                                                    .addOnSuccessListener(roleSnapshot -> {
                                                        String companyName = roleSnapshot != null && roleSnapshot.exists()
                                                                ? roleSnapshot.getString("companyName")
                                                                : " ";

                                                        // Lắng nghe thay đổi tin nhắn cuối cùng
                                                        db.collection("Message")
                                                                .document(messageID)
                                                                .collection("messages")
                                                                .orderBy("sent_at", Query.Direction.DESCENDING)
                                                                .limit(1)
                                                                .addSnapshotListener((messageSnapshot, messageError) -> {
                                                                    if (messageError != null) {
                                                                        Log.e("LastMessage", "Lỗi khi lắng nghe tin nhắn cuối: " + messageError.getMessage());
                                                                        return;
                                                                    }

                                                                    if (messageSnapshot != null && !messageSnapshot.isEmpty()) {
                                                                        DocumentSnapshot lastMessageDoc = messageSnapshot.getDocuments().get(0);
                                                                        String lastMessageContent = lastMessageDoc.getString("content");
                                                                        long timestamp = lastMessageDoc.getLong("sent_at");

                                                                        // Chuyển đổi từ timestamp sang chuỗi định dạng
                                                                        Date date = new Date(timestamp);
                                                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
                                                                        String formattedDate = sdf.format(date);

                                                                        Log.d("FormattedDate", "Thời gian định dạng: " + formattedDate);

                                                                        // Cập nhật danh sách hội thoại
                                                                        updateConversationList(
                                                                                documentId, messageID, status, name, avatar,
                                                                                lastMessageContent, formattedDate, companyName
                                                                        );

                                                                    }
                                                                });
                                                    })
                                                    .addOnFailureListener(roleError -> {
                                                        Log.e("Firestore", "Lỗi khi lấy role/employer: " + roleError.getMessage());
                                                    });
                                        } else {
                                            Log.d("Firestore", "Document user không tồn tại với ID: " + documentId);
                                        }
                                    });
                        }
                    } else {
                        Log.d("Firestore", "Collection 'conversation' trống hoặc không tồn tại.");
                    }
                });
    }


    private void updateConversationList(String documentId, String messageID, String status,
                                        String name, String avatar, String lastMessageContent,String formattedDate, String companyName) {
        boolean isUpdated = false;


        for (int i = 0; i < conversationList.size(); i++) {
            ListMess listMess = conversationList.get(i);
            if (listMess.getReicever_id().equals(documentId)) {
                // Cập nhật hội thoại đã tồn tại
                listMess.setMessID(messageID);
                listMess.setStatus(status);
                listMess.setName(name);
                listMess.setAvatar(avatar);
                listMess.setLastMes(lastMessageContent);
                listMess.setDate(formattedDate);
                listMess.setCompanyName(companyName);
                isUpdated = true;
                adapter.notifyDataSetChanged();

                conversationList.remove(i); // Xóa phần tử cũ
                conversationList.add(0, listMess);
                // Cập nhật giao diện
                adapter.notifyDataSetChanged();
                break;
            }
        }

        // Nếu chưa tồn tại, thêm mới vào danh sách
        if (!isUpdated) {
            ListMess newListMess = new ListMess();
            newListMess.setReicever_id(documentId);
            newListMess.setMessID(messageID);
            newListMess.setStatus(status);
            newListMess.setName(name);
            newListMess.setAvatar(avatar);
            newListMess.setLastMes(lastMessageContent);
            newListMess.setDate(formattedDate);
            newListMess.setCompanyName(companyName);
            conversationList.add(newListMess);
            adapter.notifyItemInserted(conversationList.size() - 1);
        }
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
