package com.example.hotrovieclam.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.hotrovieclam.Fragment.RecruiterManagement.Information_management;
import com.example.hotrovieclam.Fragment.RecruiterManagement.Job_Management;

public class ViewPagerAdapter_recruiterManagement extends FragmentStateAdapter {
    public ViewPagerAdapter_recruiterManagement(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Information_management();
            case 1:
                return new Job_Management();
            default:
                return new Information_management();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}

