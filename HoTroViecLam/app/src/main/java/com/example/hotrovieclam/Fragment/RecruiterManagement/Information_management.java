package com.example.hotrovieclam.Fragment.RecruiterManagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.databinding.FragmentInformationManagementBinding;

import java.util.ArrayList;



public class Information_management extends Fragment {


    FragmentInformationManagementBinding binding;
    Job_Management adapter;
    ArrayList<Job> jobs = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentInformationManagementBinding.inflate(inflater, container, false);


        return binding.getRoot();

    }
}