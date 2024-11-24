package com.example.hotrovieclam.Fragment.RecruiterManagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.hotrovieclam.Adapter.JobManagementAdapter;
import com.example.hotrovieclam.Adapter.ViewPagerAdapter_recruiterManagement;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentRecuiterManagementBinding;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;


public class Recruiter_Management extends Fragment {



    FragmentRecuiterManagementBinding binding;
    private ArrayList<Job> listJob;
    private JobManagementAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRecuiterManagementBinding.inflate(inflater, container, false);

        ViewPagerAdapter_recruiterManagement adapter = new ViewPagerAdapter_recruiterManagement((FragmentActivity) getContext());
        if (getActivity() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.title));
        }
        binding.viewPager.setAdapter(adapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Thông tin");
                            break;
                        case 1:
                            tab.setText("Quản lý");
                            break;
                    }
                }).attach();







        return binding.getRoot();
    }
}