package com.example.hotrovieclam.Adapter;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.databinding.ItemJobSaveBinding;

import java.util.ArrayList;

public class AdapterListJobSave  extends RecyclerView.Adapter<AdapterListJobSave.MyViewHolder>{
    private Activity context;
    private ArrayList<Job> jobs;
    private OnItemEditClickListener bamvaodexemchitietcongviecdaluu;
    public interface OnItemEditClickListener {
        void onEditClick(String idJob); // Phương thức để truyền ID
    }

    public void setbamvaodexemchitietcongviecdaluu(AdapterListJobSave.OnItemEditClickListener listener) {
        this.bamvaodexemchitietcongviecdaluu = listener;
    }


    @NonNull
    @Override
    public AdapterListJobSave.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterListJobSave.MyViewHolder(ItemJobSaveBinding.inflate(context.getLayoutInflater(), parent, false));
    }

    public AdapterListJobSave(Activity context, ArrayList<Job> jobs) {
        this.context = context;
        this.jobs = jobs;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListJobSave.MyViewHolder holder, int position) {
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



        holder.jobID = jobs.get(position).getId();
        holder.job = jobs.get(position);
        holder.binding.btnThongTinMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                    if (recycleClick != null){
//                        recycleClick.DetailClick(SourceId, jobID,job );
//                    }
                Log.d("TAG", "onClick: chi tiet"+job.getId());
                if (bamvaodexemchitietcongviecdaluu != null) {
                    bamvaodexemchitietcongviecdaluu.onEditClick(job.getId()); // Truyền ID qua interface
                }
            }
        });
        holder.binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick: Xoa"+job.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public int position;
        ItemJobSaveBinding binding;
        public String jobID = "",SourceId = "";
        public Job job = new Job();
        public MyViewHolder(@NonNull ItemJobSaveBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;




        }
    }


}
