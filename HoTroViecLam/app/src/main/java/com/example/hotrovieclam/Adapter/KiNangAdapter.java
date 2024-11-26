package com.example.hotrovieclam.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Fragment.Child_Fragment.KiNangVaChungChiFragment;
import com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment.SkillsAndCertificationFragment;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.Model.KiNang;
import com.example.hotrovieclam.Model.TruongHoc;
import com.example.hotrovieclam.databinding.ItemKinangvachungchiBinding;

import java.util.ArrayList;
import java.util.List;

public class KiNangAdapter extends RecyclerView.Adapter<KiNangAdapter.KiNangViewHolder> {
    private final List<KiNang> kiNangList;
    private OnItemEditClickListener editClickListener;
    private final  KiNangVaChungChiFragment fragment;

    public interface OnItemEditClickListener {
        void onEditClick(String schoolId); // Phương thức để truyền ID
    }

    public void setOnItemEditClickListener(KiNangAdapter.OnItemEditClickListener listener) {
        this.editClickListener = listener;
    }


    public KiNangAdapter(List<KiNang> kiNangList, KiNangVaChungChiFragment fragment) {
        this.kiNangList = kiNangList;
        this.fragment = fragment;

    }

    @NonNull
    @Override
    public KiNangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemKinangvachungchiBinding binding = ItemKinangvachungchiBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new KiNangViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull KiNangAdapter.KiNangViewHolder holder, int position) {
        KiNang kiNang = kiNangList.get(position);
        Glide.with(fragment)
                .load(kiNang.getImageUrl())
                .into(holder.binding.imageDetail);
        holder.binding.tenkinang.setText(kiNang.getName());
        holder.binding.mota.setText(kiNang.getDescription());
        holder.binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("EditClick", "School ID: " + kiNang.getId());
                if (editClickListener != null) {
                    editClickListener.onEditClick(kiNang.getId()); // Truyền ID qua interface
                }
            }
        });
        holder.binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DeleteClick", "Skill ID: " + kiNang.getId());

                // Hiển thị dialog xác nhận xóa
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa kỹ năng này không?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Gọi hàm delete khi người dùng xác nhận xóa

                                fragment.deleteSkill(kiNang.getUid());
                                fragment.deleteSkill(kiNang.getId());
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
        return kiNangList.size();
    }

    public class KiNangViewHolder extends RecyclerView.ViewHolder {
        private final ItemKinangvachungchiBinding binding;


        public KiNangViewHolder(ItemKinangvachungchiBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
