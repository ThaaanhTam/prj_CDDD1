package com.example.hotrovieclam.Adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.databinding.ListItemJobManagementBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

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
        holder.binding.tvCreateAt.setText(job.getCreatedAt());
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
                        int count = snapshots.size();
                        holder.binding.tvCount.setText(count + "");
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
                    if (recycleClick != null) {
                        recycleClick.DetailClick(view, position, jobID);
                    }
                }
            });

            binding.btnSua.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recycleClick != null) {
                        recycleClick.EditClick(view, position, jobID);
                    }
                }
            });


            binding.btnXoa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recycleClick != null) {
                        recycleClick.DeleteClick(view, position, jobID);
                    }
                }
            });


        }
    }

    public void updateList(ArrayList<Job> newJobs) {
        jobs.clear();
        jobs.addAll(newJobs);
        notifyDataSetChanged();
    }

    public interface OnItemClick {
        void DetailClick(View view, int position, String jobID);

        void EditClick(View view, int position, String jobID);

        void DeleteClick(View view, int position, String jobID);

    }
}
