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

import com.example.hotrovieclam.Fragment.Child_Fragment.HocVanFragment;
import com.example.hotrovieclam.Model.TruongHoc;
import com.example.hotrovieclam.databinding.ItemSchoolRecycleviewBinding;

import java.util.List;

public class TruongHocAdapter extends RecyclerView.Adapter<TruongHocAdapter.TruongHocViewHolder> {

    private final List<TruongHoc> truongHocList;
    private OnItemEditClickListener editClickListener;
    private HocVanFragment hocvan;

    public interface OnItemEditClickListener {
        void onEditClick(String schoolId); // Phương thức để truyền ID
    }

    public void setOnItemEditClickListener(OnItemEditClickListener listener) {
        this.editClickListener = listener;
    }

    // Constructor cho adapter, nhận vào danh sách các đối tượng TruongHoc
    public TruongHocAdapter(List<TruongHoc> truongHocList ,HocVanFragment hocVanFragment) {
        this.truongHocList = truongHocList;
        this.hocvan= hocVanFragment;
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
        // Kiểm tra type để hiển thị "Đang học" hoặc thời gian
        if (truongHoc.getType() == 1) {
            holder.binding.tvTime.setText("Đang học");
        } else {
            holder.binding.tvTime.setText(truongHoc.getTimeEnd() + " - " + truongHoc.getTimeStart());
        }
        holder.binding.imgEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("EditClick", "School ID: " + truongHoc.getId_Shool());
                if (editClickListener != null) {
                    editClickListener.onEditClick(truongHoc.getId_Shool()); // Truyền ID qua interface
                }
            }
        });
        holder.binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "iiii", Toast.LENGTH_SHORT).show();
                Log.d("GGGG", "onClick: "+truongHoc.getId_Shool());
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa trường học này không?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Gọi hàm delete khi người dùng xác nhận xóa

                                hocvan.deleteScool(truongHoc.getId_Shool());
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
        return truongHocList.size();
    }

    public static class TruongHocViewHolder extends RecyclerView.ViewHolder {
        private final ItemSchoolRecycleviewBinding binding;

        public TruongHocViewHolder(@NonNull ItemSchoolRecycleviewBinding binding) {
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
