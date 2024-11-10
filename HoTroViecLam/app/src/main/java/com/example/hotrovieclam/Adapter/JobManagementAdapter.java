package com.example.hotrovieclam.Adapter;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.databinding.ListItemJobManagementBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class JobManagementAdapter extends RecyclerView.Adapter<JobManagementAdapter.MyViewHolder> {

    private FragmentActivity context;
    private ArrayList<Job> jobs;
    private OnItemClick recycleClick;

    public void setRecycleClick(OnItemClick recycleClick) {
        this.recycleClick = recycleClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(ListItemJobManagementBinding.inflate(context.getLayoutInflater(), parent, false));
    }

    public JobManagementAdapter(FragmentActivity context, ArrayList<Job> jobs) {
        this.context = context;
        this.jobs = jobs;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Job job = jobs.get(position);
        holder.binding.tvTitle.setText(job.getTitle());
    //    Timestamp timestamp = job.getCreatedAt();
       // if (timestamp != null) {
         //   Date date = timestamp.toDate();
       //     SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        //    String formattedDate = dateFormat.format(date);
         //   holder.binding.tvCreateAt.setText(formattedDate);  // Hiển thị chuỗi định dạng
//        } else {
//            holder.binding.tvCreateAt.setText("N/A");  // Nếu createdAt là null
//        }
        holder.binding.tvStartDate.setText(job.getStartTime());
        holder.binding.tvEndDate.setText(job.getEndTime());
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("jobs").document(jobs.get(position).getId()).collection("application")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.w("Firestore", "Listen failed.", error);
                        return;
                    }

                    if (snapshots != null) {
                        int count = snapshots.size(); // Số lượng phần tử trong sub-collection
                        holder.binding.tvCount.setText(count+"");
                    } else {

                    }
                });


        final int pos = position;
        holder.position = pos;
        holder.jobID = jobs.get(position).getId();


    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public int position;
        ListItemJobManagementBinding binding;
        public String jobID = "";
        public MyViewHolder(@NonNull ListItemJobManagementBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;


            binding.btnThongTin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recycleClick != null){
                        recycleClick.DetailClick(view, position, jobID );
                    }
                }
            });

            binding.btnSua.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recycleClick != null){
                        recycleClick.EditClick(view, position, jobID);
                    }
                }
            });


            binding.btnXoa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recycleClick != null){
                        recycleClick.DeleteClick(view, position, jobID);
                    }
                }
            });


        }
    }

    public void updateList(ArrayList<Job> newJobs) {
        jobs.clear(); // Xóa danh sách cũ
        jobs.addAll(newJobs); // Thêm danh sách mới
        notifyDataSetChanged(); // Cập nhật RecyclerView
    }
    public interface OnItemClick {
        void DetailClick(View view, int position, String jobID);
        void EditClick(View view, int position, String jobID);
        void DeleteClick(View view, int position, String jobID);

    }
}
