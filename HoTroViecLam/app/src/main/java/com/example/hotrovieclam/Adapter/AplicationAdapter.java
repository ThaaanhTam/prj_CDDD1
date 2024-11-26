package com.example.hotrovieclam.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotrovieclam.Model.Candidate;
import com.example.hotrovieclam.databinding.ApplicationCandidateItemBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AplicationAdapter extends RecyclerView.Adapter<AplicationAdapter.CandidateViewHolder> {

    private List<Candidate> candidateList;
    private Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Thêm context vào constructor
    public AplicationAdapter(Context context, List<Candidate> candidateList) {
        this.context = context; // Khởi tạo context
        this.candidateList = candidateList;
    }

    @NonNull
    @Override
    public CandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng ViewBinding để gắn layout vào ViewHolder
        ApplicationCandidateItemBinding binding = ApplicationCandidateItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CandidateViewHolder(binding);  // Trả về ViewHolder với binding
    }

    @Override
    public void onBindViewHolder(@NonNull CandidateViewHolder holder, int position) {
        Candidate candidate = candidateList.get(position);
        // Cập nhật dữ liệu vào các view
        holder.binding.tvTitleValue.setText(candidate.getTitle());
        holder.binding.tvSalaryValue.setText(candidate.getSalary());
        holder.binding.tvStatusValue.setText(candidate.getStatus());

        holder.binding.btnCancel.setOnClickListener(v -> {
            deleteCandidateFromFirestore(position, candidate);
        });
    }

    private void showDeleteConfirmationDialog(int position, Candidate candidate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa ứng viên này?");

        // Tùy chỉnh các nút hành động
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            // Xóa ứng viên khỏi Firestore
            deleteCandidateFromFirestore(position, candidate);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> {
            // Không làm gì, chỉ đóng hộp thoại
            dialog.dismiss();
        });

        // Tạo AlertDialog
        AlertDialog dialog = builder.create();

        // Tùy chỉnh giao diện của AlertDialog (ví dụ, thêm màu nền, viền)
        View dialogView = dialog.getWindow().getDecorView();
        dialogView.setBackgroundColor(context.getResources().getColor(android.R.color.white));

        // Tùy chỉnh viền và góc của AlertDialog
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(20);  // Góc bo tròn
        drawable.setStroke(5, context.getResources().getColor(android.R.color.holo_blue_light));  // Viền màu xanh, độ dày 5px
        dialogView.setBackground(drawable);

        // Tùy chỉnh màu chữ cho các nút
        TextView title = dialogView.findViewById(android.R.id.title);
        title.setTextColor(context.getResources().getColor(android.R.color.black));

        TextView message = dialogView.findViewById(android.R.id.message);
        message.setTextColor(context.getResources().getColor(android.R.color.darker_gray));

        dialog.show();
    }

    private void deleteCandidateFromFirestore(int position, Candidate candidate) {
        db.collection("jobs")
                .document(candidate.getJobID()) // Lấy đúng jobID từ candidate
                .collection("application")
                .document(candidate.getId()) // Lấy đúng applicationID từ candidate
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Hiển thị thông báo Toast khi xóa thành công
                    Toast.makeText(context, "Ứng viên đã được xóa thành công", Toast.LENGTH_SHORT).show();

                    // Xóa item trong danh sách và thông báo cho adapter
                    candidateList.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    // Hiển thị thông báo Toast khi xóa không thành công
                    Toast.makeText(context, "Lỗi khi xóa ứng viên", Toast.LENGTH_SHORT).show();
                    Log.w("Firestore", "Error deleting candidate", e);
                });
    }


    @Override
    public int getItemCount() {
        return candidateList.size();
    }

    public static class CandidateViewHolder extends RecyclerView.ViewHolder {

        ApplicationCandidateItemBinding binding;

        public CandidateViewHolder(ApplicationCandidateItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
