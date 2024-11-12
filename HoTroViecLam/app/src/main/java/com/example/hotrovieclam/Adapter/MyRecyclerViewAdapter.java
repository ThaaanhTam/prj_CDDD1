package com.example.hotrovieclam.Adapter;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ListItemBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private Activity context;
    private ArrayList<Job> jobs;
    private OnItemClick recycleClick;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    public void setRecycleClick(OnItemClick recycleClick) {
        this.recycleClick = recycleClick;
    }

    public MyRecyclerViewAdapter(Activity context, ArrayList<Job> jobs) {
        this.context = context;
        this.jobs = jobs;
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(ListItemBinding.inflate(context.getLayoutInflater(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Job job = jobs.get(position);

        // Load avatar
        Uri uri = (job.getAvatar() != null && !job.getAvatar().isEmpty()) ? Uri.parse(job.getAvatar()) : Uri.parse("https://123job.vn/images/no_company.png");
        Glide.with(context).load(uri).into(holder.binding.ivNameCompany);

        holder.binding.tvNameCompany.setText(job.getTitle());
        holder.binding.tvNameLocation.setText(job.getLocation());

        if (job.getSalaryMax() == -1.0f || job.getSalaryMin() == 1.0f) {
            holder.binding.tvSalary.setText(job.getAgreement());
        } else {
            holder.binding.tvSalary.setText(Math.round(job.getSalaryMin()) + " - " + Math.round(job.getSalaryMax()) + " triệu");
        }

        // Set visibility of logo based on sourceId
        if (job.getSourceId() != 3) {
            holder.binding.logoApp.setVisibility(ViewGroup.GONE);
        } else {
            holder.binding.logoApp.setVisibility(ViewGroup.VISIBLE);
        }

        if (job.getEmployerId() != null) {
            // Lấy tham chiếu tới tài liệu "employer" trong subcollection "role" của người dùng
            DocumentReference employerDocRef = db.collection("users")
                    .document(job.getEmployerId())
                    .collection("role")
                    .document("employer");

            // Đọc dữ liệu từ tài liệu "employer"
            employerDocRef.get().addOnSuccessListener(document -> {
                if (document != null && document.exists()) {
                    // Lấy trường logo từ tài liệu employer
                    String logoUrl = document.getString("logo");
                    if (logoUrl != null) {
                        Log.d("logoUrl", logoUrl);
                        // Tải ảnh từ Firebase Storage nếu có URL của logo
                        loadImage(storage.getReference(), "images/" + logoUrl, holder.binding.ivNameCompany);
                    } else {
                        Log.w("Firestore", "Logo field is null");
                    }
                } else {
                    Log.w("Firestore", "Document does not exist or role is not employer");
                }
            }).addOnFailureListener(e -> {
                // Xử lý lỗi nếu có
                Log.e("Firestore", "Error getting document", e);
            });
        }



        // Set job data in ViewHolder for onClick event
        holder.jobID = job.getId();
        holder.job = job;
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
        ListItemBinding binding;
        public String jobID = "", SourceId = "";
        public Job job = new Job();
        public ListenerRegistration listenerRegistration;

        public MyViewHolder(@NonNull ListItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;

            binding.btnThongTinMain.setOnClickListener(view -> {
                if (recycleClick != null) {
                    recycleClick.DetailClick(SourceId, jobID, job);
                }
            });
        }

        // Unregister listener when the item is recycled
        public void clearListener() {
            if (listenerRegistration != null) {
                listenerRegistration.remove();
            }
        }
    }

    // Update list and notify the RecyclerView
    public void updateList(ArrayList<Job> newJobs) {
        jobs.clear();
        jobs.addAll(newJobs);
        notifyDataSetChanged();
    }

    public interface OnItemClick {
        void DetailClick(String SourceId, String jobID, Job job);
    }
}
