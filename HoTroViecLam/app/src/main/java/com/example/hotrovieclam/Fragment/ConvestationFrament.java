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

import java.util.ArrayList;

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
                        conversationList.clear();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String documentId = document.getId();
                            String messageID = document.getString("messageID");
                            String status = document.getString("status");



                            // Lắng nghe thay đổi từ bảng "users" dựa vào documentId
                            db.collection("users").document(documentId)
                                    .addSnapshotListener((userSnapshot, userError) -> {
                                        if (userError != null) {
                                            Log.e("Firestore", "Lỗi khi lắng nghe dữ liệu user: " + userError.getMessage());
                                            return;
                                        }

                                        if (userSnapshot != null && userSnapshot.exists()) {
                                            // Lấy dữ liệu từ document "users"
                                            String name = userSnapshot.getString("name");
                                            String avatar = userSnapshot.getString("avatar");

                                            // Tạo đối tượng User
                                            ListMess listMess = new ListMess();
                                            listMess.setName(name);
                                            listMess.setAvatar(avatar);
                                            listMess.setMessID(messageID);
                                            listMess.setReicever_id(documentId);
                                            listMess.setStatus(status);

                                            conversationList.add(listMess);
                                            adapter.notifyDataSetChanged();


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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
