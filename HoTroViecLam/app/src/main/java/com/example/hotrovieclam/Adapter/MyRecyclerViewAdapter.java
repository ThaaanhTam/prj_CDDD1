package com.example.hotrovieclam.Adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Model.Job;
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


        Uri uri = Uri.parse(job.getAvatar());

        Glide.with(context).load(uri).into(holder.binding.ivNameCompany);
        holder.binding.tvNameCompany.setText(job.getTitle());
        holder.binding.tvNameLocation.setText(job.getAgreement());
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
}
