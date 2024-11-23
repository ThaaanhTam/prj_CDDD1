package com.example.hotrovieclam.Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Activity.MessageActivity;
import com.example.hotrovieclam.Model.ListMess;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ItemConversationBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private ArrayList<ListMess> conversationList;
    private FragmentActivity context;
    private  FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();;
    UserSessionManager sessionManager = new UserSessionManager();
    String currentUserId = sessionManager.getUserUid();
    public ConversationAdapter(ArrayList<ListMess> conversationList, FragmentActivity context) {
        this.conversationList = conversationList;
        this.context = context;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return   new ConversationViewHolder(ItemConversationBinding.inflate(context.getLayoutInflater(), parent, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        ListMess listMess = conversationList.get(position);
        if (listMess.getName()!=null) {
            holder.binding.textViewUserName.setText(listMess.getName());
        }
        if(listMess.getAvatar()!=null){
            Log.d("ff", listMess.getAvatar());
            Glide.with(context)
                    .load(listMess.getAvatar())
                    .circleCrop() // Bo tròn hình ảnh
                    .placeholder(R.drawable.user_solid) // Ảnh thay thế trong khi tải
                    .error(R.drawable.user_solid) // Ảnh lỗi nếu tải thất bại
                    .into(holder.binding.imageViewAvatar);

        }
        holder.binding.textViewLastMessage.setText(listMess.getLastMes());

        if(listMess.getDate() != null){
            Log.d("date",listMess.getDate() );
        holder.binding.textViewTime.setText(listMess.getDate());}
        holder.binding.body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("messageID", listMess.getMessID());
                intent.putExtra("receiverID", listMess.getReicever_id());
                context.startActivity(intent);

            }
        });

        if (listMess.getStatus()!=null) {
            if (listMess.getStatus().equals("0")) {
                Log.d("vvv", listMess.getStatus());
                holder.binding.textViewLastMessage.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.black)
                );
            } else {
                holder.binding.textViewLastMessage.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.gray_light)
                );
            }
        }





    }
    private void loadImage(StorageReference storageReference, String path, ImageView imageView) {
        if (path != null && !path.isEmpty()) {
            StorageReference imageRef = storageReference.child(path);
            imageRef.getDownloadUrl()
                    .addOnSuccessListener(uri -> Glide.with(context).load(uri).into(imageView))
                    .addOnFailureListener(e -> Toast.makeText(context, "Không thể tải ảnh", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public class ConversationViewHolder extends RecyclerView.ViewHolder {
        public int position;
        ItemConversationBinding binding;
        public ConversationViewHolder(@NonNull ItemConversationBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;


        }
    }

}
