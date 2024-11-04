package com.example.hotrovieclam.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.hotrovieclam.Fragment.RecruiterManagement.Candidate_List;
import com.example.hotrovieclam.Fragment.RecruiterManagement.DetailinfoJob;

public class DetailAdapter extends FragmentStateAdapter {
    private String jobID; // Thêm thuộc tính để lưu jobID

    public DetailAdapter(@NonNull FragmentActivity fragmentActivity, String jobID) {
        super(fragmentActivity);
        this.jobID = jobID; // Lưu jobID vào adapter
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                // Tạo DetailinfoJob Fragment và truyền jobID
                return DetailinfoJob.newInstance(jobID);
            case 1:
                // Tạo Candidate_List Fragment và truyền jobID
                return Candidate_List.newInstance(jobID);
            default:
                return DetailinfoJob.newInstance(jobID); // Mặc định
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Số lượng Fragment
    }
}
