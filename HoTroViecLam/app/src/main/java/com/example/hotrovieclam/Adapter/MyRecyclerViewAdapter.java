package com.example.hotrovieclam.Adapter;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ListItemBinding;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private Activity context;
    private ArrayList<Job> jobs;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(ListItemBinding.inflate(context.getLayoutInflater(), parent, false));
    }

    public MyRecyclerViewAdapter(Activity context, ArrayList<Job> jobs) {
        this.context = context;
        this.jobs = jobs;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Job job = jobs.get(position);

        Uri uri;
        //String avatarUrl = job.getAvatar();

        if (job.getAvatar() != null && !job.getAvatar().isEmpty()) {
            // Nếu avatarUrl hợp lệ, tải ảnh bằng Glide
            uri = Uri.parse(job.getAvatar());
        }else {
            uri = Uri.parse("https://123job.vn/images/no_company.png");
        }
        Glide.with(context).load(uri).into(holder.binding.ivNameCompany);

        // Nếu avatarUrl không hợp lệ hoặc rỗng, không làm gì cả (không cần else)
        holder.binding.tvNameCompany.setText(job.getTitle());
        holder.binding.tvNameLocation.setText(job.getLocation());
        if(job.getSalaryMax() == -1.0f  || job.getSalaryMin() == 1.0f ){
            holder.binding.tvSalary.setText(job.getAgreement());
        }else {
            holder.binding.tvSalary.setText( Math.round(job.getSalaryMin())+ " - "+ Math.round(job.getSalaryMax())+ " triệu");
        }

//        if(job.getSourceId()==1){
//            holder.binding.backgroundItem.setBackgroundResource(R.color.API);
//        }
//        if(job.getSourceId()==2){
//            holder.binding.backgroundItem.setBackgroundResource(R.color.website);
//        }


        if (job.getSourceId() != 3) {
           // holder.binding.backgroundItem.setBackgroundResource(R.color.API);
            holder.binding.logoApp.setVisibility(ViewGroup.GONE);
        }
        else {
            holder.binding.logoApp.setVisibility(ViewGroup.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public int position;
        ListItemBinding binding;
        public MyViewHolder(@NonNull ListItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;

        }
    }

    public void updateList(ArrayList<Job> newJobs) {
        jobs.clear(); // Xóa danh sách cũ
        jobs.addAll(newJobs); // Thêm danh sách mới
        notifyDataSetChanged(); // Cập nhật RecyclerView
    }
}
