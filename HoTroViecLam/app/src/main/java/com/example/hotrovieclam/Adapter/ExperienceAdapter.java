package com.example.hotrovieclam.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotrovieclam.Fragment.Child_Fragment.KiNangVaChungChiFragment;
import com.example.hotrovieclam.Fragment.Child_Fragment.KinhNghiemFragment;
import com.example.hotrovieclam.Model.Experience;
import com.example.hotrovieclam.Model.TruongHoc;
import com.example.hotrovieclam.databinding.ItemKinhnghiemRecycleviewBinding;
import com.example.hotrovieclam.databinding.ItemSchoolRecycleviewBinding;

import java.util.List;

public class ExperienceAdapter extends RecyclerView.Adapter<ExperienceAdapter.ExperienceViewHolder> {

    private final List<Experience> experienceList;
    private OnItemEditClickListener editClickListener;
    private final KinhNghiemFragment fragment;

    public interface OnItemEditClickListener {
        void onEditClick(String kinhNghiemID); // Phương thức để truyền ID
    }

    public void setOnItemEditClickListener(OnItemEditClickListener listener) {
        this.editClickListener = listener;
    }

    // Constructor cho adapter, nhận vào danh sách các đối tượng TruongHoc
    public ExperienceAdapter(List<Experience> experienceList,KinhNghiemFragment fragment) {
        this.experienceList = experienceList;
        this.fragment = fragment;
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

        Experience kinhNghiem = experienceList.get(position);

        // Gán các giá trị vào các view bằng ViewBinding
        holder.binding.tvOrganization.setText(kinhNghiem.getName_organization());
        holder.binding.tvMission.setText(kinhNghiem.getPosition());
        holder.binding.tvTime.setText(kinhNghiem.getTime_start() + " - " + kinhNghiem.getTime_end());
        holder.binding.imgEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editClickListener != null) {
                    editClickListener.onEditClick(kinhNghiem.getIdExperiences()); // Truyền ID qua interface
                }
            }
        });
        holder.binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DeleteClick", "Experience ID: " + kinhNghiem.getIdExperiences());

                // Hiển thị dialog xác nhận xóa
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa kỹ năng này không?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Gọi hàm delete khi người dùng xác nhận xóa

                                fragment.deleteExperience(kinhNghiem.getIdExperiences());
                                //fragment.deleteExperience(kinhNghiem.getId_uid());
                            }
                        })
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // Đóng dialog nếu người dùng chọn "Hủy"
                            }
                        })
                        .show();
            }
        });

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
            binding.imgEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TL", "onClick: llllllllllllllllllllllll");
                }
            });
            binding.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TL", "onClick: llllllllllllllllllllllll");
                }
            });
        }
    }
}
