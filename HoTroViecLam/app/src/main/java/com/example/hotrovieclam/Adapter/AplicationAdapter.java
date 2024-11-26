package com.example.hotrovieclam.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotrovieclam.Model.Candidate;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ApplicationCandidateItemBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AplicationAdapter extends RecyclerView.Adapter<AplicationAdapter.CandidateViewHolder> {

    private List<Candidate> candidateList;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Constructor
    public AplicationAdapter(Context context, List<Candidate> candidateList) {
        this.context = context;
        this.candidateList = candidateList;
    }

    @NonNull
    @Override
    public CandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ApplicationCandidateItemBinding binding = ApplicationCandidateItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CandidateViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidateViewHolder holder, int position) {
        Candidate candidate = candidateList.get(position);

        // Set job title
        holder.binding.tvTitleValue.setText(candidate.getTitle());

        // Set salary (if available)
        String salary = candidate.getSalary();
        holder.binding.tvSalaryValue.setText(salary != null ? salary : "Chưa cập nhật");

        // Set status with color coding
        String status = candidate.getStatus();
        holder.binding.tvStatusValue.setText(status);

        // Color code status
        int statusColor;
        switch (status) {
            case "Đã duyệt":
                statusColor = ContextCompat.getColor(context, android.R.color.holo_green_dark);
                break;
            case "Từ chối":
                statusColor = ContextCompat.getColor(context, android.R.color.holo_red_dark);
                break;
            default:
                statusColor = ContextCompat.getColor(context, android.R.color.holo_orange_dark);
        }
        holder.binding.tvStatusValue.setTextColor(statusColor);

        // Delete button click listener
        holder.binding.btnCancel.setOnClickListener(v ->
                showDeleteConfirmationDialog(position, candidate)
        );
    }

    private void showDeleteConfirmationDialog(int position, Candidate candidate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa ứng viên này?")
                .setPositiveButton("Xóa", (dialog, which) ->
                        deleteCandidateFromFirestore(position, candidate)
                )
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void deleteCandidateFromFirestore(int position, Candidate candidate) {
        db.collection("jobs")
                .document(candidate.getJobID())
                .collection("application")
                .document(candidate.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Thông báo thành công
                    Snackbar.make(
                            ((Activity)context).findViewById(android.R.id.content),
                            "Ứng viên đã được xóa thành công",
                            Snackbar.LENGTH_SHORT
                    ).show();

                    // Xóa khỏi danh sách
                    if (position >= 0 && position < candidateList.size()) {
                        candidateList.remove(position);
                        notifyItemRemoved(position);
                    } else {
                        Log.e("AplicationAdapter", "Vị trí không hợp lệ: " + position);
                    }
                })
                .addOnFailureListener(e -> {
                    // Thông báo lỗi
                    Toast.makeText(
                            context,
                            "Lỗi khi xóa ứng viên: " + e.getLocalizedMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                    Log.w("Firestore", "Lỗi xóa ứng viên", e);
                });
    }

    @Override
    public int getItemCount() {
        return candidateList.size();
    }

    public static class CandidateViewHolder extends RecyclerView.ViewHolder {
        ApplicationCandidateItemBinding binding;

        public CandidateViewHolder(ApplicationCandidateItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}