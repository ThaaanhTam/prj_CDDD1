package com.example.hotrovieclam.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotrovieclam.Model.Experience;
import com.example.hotrovieclam.Model.TruongHoc;
import com.example.hotrovieclam.databinding.ItemKinhnghiemRecycleviewBinding;
import com.example.hotrovieclam.databinding.ItemSchoolRecycleviewBinding;

import java.util.List;

public class ExperienceAdapter extends RecyclerView.Adapter<ExperienceAdapter.ExperienceViewHolder> {

    private final List<Experience> experienceList;

    // Constructor cho adapter, nhận vào danh sách các đối tượng TruongHoc
    public ExperienceAdapter(List<Experience> experienceList) {
        this.experienceList = experienceList;
    }

    @NonNull
    @Override
    public ExperienceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng ViewBinding để inflate layout
        ItemKinhnghiemRecycleviewBinding binding = ItemKinhnghiemRecycleviewBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ExperienceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExperienceViewHolder holder, int position) {

        Experience kinhNghiem = new Experience();

        // Gán các giá trị vào các view bằng ViewBinding
        holder.binding.tvOrganization.setText(kinhNghiem.getName_organization());
        holder.binding.tvMission.setText(kinhNghiem.getPosition());
        holder.binding.tvTime.setText(kinhNghiem.getTime_start() + " - " + kinhNghiem.getTime_end());


    }

    @Override
    public int getItemCount() {
        // Trả về số lượng item trong danh sách truongHocList
        return experienceList.size();
    }

    public static class ExperienceViewHolder extends RecyclerView.ViewHolder {
        private final ItemKinhnghiemRecycleviewBinding binding;

        public ExperienceViewHolder(@NonNull ItemKinhnghiemRecycleviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
