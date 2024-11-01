package com.example.hotrovieclam.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotrovieclam.Model.TruongHoc;
import com.example.hotrovieclam.databinding.ItemSchoolRecycleviewBinding;

import java.util.List;

public class TruongHocAdapter extends RecyclerView.Adapter<TruongHocAdapter.TruongHocViewHolder> {

    private final List<TruongHoc> truongHocList;

    // Constructor cho adapter, nhận vào danh sách các đối tượng TruongHoc
    public TruongHocAdapter(List<TruongHoc> truongHocList) {
        this.truongHocList = truongHocList;
    }

    @NonNull
    @Override
    public TruongHocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng ViewBinding để inflate layout
        ItemSchoolRecycleviewBinding binding = ItemSchoolRecycleviewBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TruongHocViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TruongHocViewHolder holder, int position) {
        // Lấy dữ liệu TruongHoc tại vị trí "position"
        TruongHoc truongHoc = truongHocList.get(position);

        // Gán các giá trị vào các view bằng ViewBinding
        holder.binding.tvSchoolName.setText(truongHoc.getNameSchool());
        holder.binding.tvNganhHoc.setText(truongHoc.getNganhHoc());
        holder.binding.tvTime.setText(truongHoc.getTimeStart() + " - " + truongHoc.getTimeEnd());


    }

    @Override
    public int getItemCount() {
        // Trả về số lượng item trong danh sách truongHocList
        return truongHocList.size();
    }

    public static class TruongHocViewHolder extends RecyclerView.ViewHolder {
        private final ItemSchoolRecycleviewBinding binding;

        public TruongHocViewHolder(@NonNull ItemSchoolRecycleviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
