package com.example.hotrovieclam.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Activity.JobDetailMain;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.databinding.ItemJobSaveBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterListJobSave  extends RecyclerView.Adapter<AdapterListJobSave.MyViewHolder>{
    private Activity context;
    private ArrayList<Job> jobs;
    private OnItemEditClickListener bamvaodexemchitietcongviecdaluu;
    private OnItemDeleteClickListener deleteClickListener;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    public interface OnItemEditClickListener {
        void onEditClick(String idJob);
    }

    public interface OnItemDeleteClickListener {
        void onDeleteClick(String jobId, int position);
    }

    public void setbamvaodexemchitietcongviecdaluu(OnItemEditClickListener listener) {
        this.bamvaodexemchitietcongviecdaluu = listener;
    }

    public void setOnItemDeleteClickListener(OnItemDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    @NonNull
    @Override
    public AdapterListJobSave.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(ItemJobSaveBinding.inflate(context.getLayoutInflater(), parent, false));
    }

    public AdapterListJobSave(Activity context, ArrayList<Job> jobs) {
        this.context = context;
        this.jobs = jobs;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListJobSave.MyViewHolder holder, int position) {
        Job job = jobs.get(position);
        Uri uri = (job.getAvatar() != null && !job.getAvatar().isEmpty()) ? Uri.parse(job.getAvatar()) : Uri.parse("https://123job.vn/images/no_company.png");

        Glide.with(context).load(uri).into(holder.binding.ivNameCompany);

        holder.binding.tvNameCompany.setText(job.getTitle());
        holder.binding.tvNameLocation.setText(job.getLocation());
        if (job.getSalaryMax() == -1.0f || job.getSalaryMin() == 1.0f) {
            holder.binding.tvSalary.setText(job.getAgreement());
        } else {
            holder.binding.tvSalary.setText(Math.round(job.getSalaryMin()) + " - " + Math.round(job.getSalaryMax()) + " triệu");
        }

        holder.jobID = job.getId();
        holder.job = job;

        holder.binding.btnThongTinMain.setOnClickListener(v -> {
            Intent intent = new Intent(context, JobDetailMain.class);
            Log.d("hh", job.getId());
            intent.putExtra("jobID", job.getId()); // Truyền ID công việc
            intent.putExtra("sourceId", job.getSourceId());
            intent.putExtra("KEY_NAME", job);
            context.startActivity(intent);
        });

        holder.binding.delete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(job.getId(), holder.getAdapterPosition());
            }
        });

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
        return jobs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public int position;
        ItemJobSaveBinding binding;
        public String jobID = "", SourceId = "";
        public Job job = new Job();

        public MyViewHolder(@NonNull ItemJobSaveBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;

        }
    }
    public interface OnItemClick {
        void DetailClick(String SourceId, String jobID, Job job);
    }
}
