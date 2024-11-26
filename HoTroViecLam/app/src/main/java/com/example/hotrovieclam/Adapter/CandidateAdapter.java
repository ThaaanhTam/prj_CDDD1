package com.example.hotrovieclam.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Model.HieuUngThongBao;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.Model.User;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ListItemBinding;
import com.example.hotrovieclam.databinding.ListItemCadidateBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.MyViewHolder> {

    private Activity context;
    private ArrayList<User> users;
    private OnItemClickListener editListener;

    public interface OnItemClickListener {
        void onClick(String id_candidate, Integer po);
    }

    public void setClickIem(OnItemClickListener listener) {
        this.editListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(ListItemCadidateBinding.inflate(context.getLayoutInflater(), parent, false));
    }

    public CandidateAdapter(Activity context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User user = users.get(position);

        Uri uri;
        //String avatarUrl = job.getAvatar();

        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            uri = Uri.parse(user.getAvatar());
        } else {
            uri = Uri.parse("https://123job.vn/images/no_company.png");
        }
        Glide.with(context).load(uri).into(holder.binding.ivAvatar);


        holder.binding.tvNameCadidate.setText(user.getName());


        holder.binding.backgroundItem.setOnClickListener(v -> {
            if (editListener != null) {
                Log.d("UU", "onBindViewHolder: "+user.getId());
                editListener.onClick(user.getId(), position);
            }
        });
        Log.d("StatusCheck", "Status for " + user.getName() + ": " + user.getStatus());
        if (user.getStatus() != null && user.getStatus().intValue() == 0) {
            int color = ContextCompat.getColor(context, R.color.mauvien);
            holder.binding.status.setText("Đã từ chối");
            holder.binding.backgroundItem.setBackgroundColor(color);
            holder.binding.status.setVisibility(View.VISIBLE);
            holder.binding.backgroundItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Log thông tin khi người dùng long click
                    Log.d("DDD", "onBindViewHolder: " + user.getIdJob());
                    if (user.getIdJob() != null && user.getId() != null) {
                        String id_Job = user.getIdJob();
                        String id_candidate = user.getId();

                        //deleteCandidate(user.getIdJob(), user.getId());
                        new AlertDialog.Builder(context)
                                .setTitle("Xác nhận xóa")
                                .setMessage("Bạn có chắc chắn muốn xóa ứng viên này không?")
                                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Nếu người dùng chọn "Xóa", tiến hành xóa ứng viên và tài liệu người dùng
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                                        // Bước 1: Truy vấn và lấy tài liệu ứng viên trong collection "application"
                                        db.collection("jobs")
                                                .document(id_Job)  // ID của công việc
                                                .collection("application")
                                                .whereEqualTo("candidateId", id_candidate)  // Tìm ứng viên theo candidateId
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            QuerySnapshot querySnapshot = task.getResult();

                                                            // Kiểm tra nếu có tài liệu ứng viên cần xóa
                                                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                                                // Lấy id của tài liệu trong collection "application"
                                                                for (QueryDocumentSnapshot document : querySnapshot) {
                                                                    String documentId = document.getId(); // Lấy id của tài liệu trong application

                                                                    // Xóa tài liệu ứng viên dựa trên documentId
                                                                    db.collection("jobs")
                                                                            .document(id_Job)
                                                                            .collection("application")
                                                                            .document(documentId)
                                                                            .delete()
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    // Xóa thành công tài liệu ứng viên
                                                                                    HieuUngThongBao.showSuccessToast(context, "Xóa ứng viên thành công!");

                                                                                    // Xóa ứng viên khỏi danh sách và cập nhật RecyclerViewifif(ifif(


//                                                                                    if(user!= null) {
//                                                                                        users.remove(position);
//                                                                                    }

                                                                                    if (position >= 0 && position < users.size()) {
                                                                                        users.remove(position);
                                                                                        notifyItemRemoved(position);
                                                                                        // Nếu muốn cập nhật lại các vị trí còn lại
                                                                                        notifyItemRangeChanged(position, users.size());
                                                                                    }
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    // Hiển thị thông báo lỗi khi không xóa thành công
                                                                                    HieuUngThongBao.showErrorToast(context, "Lỗi khi xóa ứng viên: " + e.getMessage());
                                                                                }
                                                                            });
                                                                }
                                                            } else {
                                                                Toast.makeText(context, "Không tìm thấy ứng viên với id: " + id_candidate, Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            // Trường hợp lỗi khi truy vấn
                                                            Toast.makeText(context, "Lỗi khi truy vấn Firestore: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                })
                                .setNegativeButton("Hủy", null) // Nếu chọn "Hủy", không làm gì cả
                                .show();
                    }

                    return false;
                }


            });
        }else if (user.getStatus() != null && user.getStatus().intValue() == 1){
            holder.binding.status.setText("Đã chấp nhận");
            holder.binding.status.setBackgroundColor(R.color.title);
            holder.binding.status.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public int position;
        ListItemCadidateBinding binding;

        public MyViewHolder(@NonNull ListItemCadidateBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;

        }
    }

    public void updateList(ArrayList<User> users) {
        users.clear(); // Xóa danh sách cũ
        users.addAll(users); // Thêm danh sách mới
        notifyDataSetChanged(); // Cập nhật RecyclerView
    }


}
