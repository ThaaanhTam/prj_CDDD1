package com.example.hotrovieclam.Adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.Model.User;
import com.example.hotrovieclam.databinding.ListItemBinding;
import com.example.hotrovieclam.databinding.ListItemCadidateBinding;

import java.util.ArrayList;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.MyViewHolder> {

    private Activity context;
    private ArrayList<User> users;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(ListItemCadidateBinding.inflate(context.getLayoutInflater(), parent, false));
    }

    public CandidateAdapter(Activity context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = users.get(position);

        Uri uri;
        //String avatarUrl = job.getAvatar();

        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            uri = Uri.parse(user.getAvatar());
        }else {
            uri = Uri.parse("https://123job.vn/images/no_company.png");
        }
        Glide.with(context).load(uri).into(holder.binding.ivAvatar);


        holder.binding.tvNameCadidate.setText(user.getName());




    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
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
