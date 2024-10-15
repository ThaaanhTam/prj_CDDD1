package com.example.findwork.Adapter;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.findwork.FireBase.Storage;
import com.example.findwork.Job.JobDataAapi;
import com.example.findwork.OnUriRetrievedListener;
import com.example.findwork.databinding.ListItemBinding;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private Activity context;
    private ArrayList<JobDataAapi.Job> jobDataAPIs;



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(ListItemBinding.inflate(context.getLayoutInflater(), parent, false));
    }

    public MyRecyclerViewAdapter(Activity context, ArrayList<JobDataAapi.Job> jobDataAPIs) {
        this.context = context;
        this.jobDataAPIs = jobDataAPIs;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        JobDataAapi.Job jobDataAapi = jobDataAPIs.get(position);
//        Storage storageHelper = new Storage();
//
//        storageHelper.getUri(new OnUriRetrievedListener() {
//            @Override
//            public void onUriRetrieved(Uri uri) {
//                Log.d("Firebase", "Download URL: " + uri.toString());
//                Glide.with(context).load(uri).into(holder.binding.ivNameCompany);
//
//            }
//        },jobDataAapi.getCompany().getLogo());

        Uri uri = Uri.parse(jobDataAapi.getCompany().getLogo());

        // Sử dụng Glide để tải hình ảnh từ Uri
        Glide.with(context).load(uri).into(holder.binding.ivNameCompany);


        holder.binding.tvNameCompany.setText(jobDataAapi.getCompany().getName());
        holder.binding.tvNameLocation.setText(jobDataAapi.getLocation());
    }

    @Override
    public int getItemCount() {
        return jobDataAPIs.size();
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
