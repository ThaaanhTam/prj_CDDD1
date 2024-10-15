package com.example.findwork.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.findwork.API.APIconnect;
import com.example.findwork.Adapter.MyRecyclerViewAdapter;
import com.example.findwork.Job.JobDataAapi;
import com.example.findwork.OnDataLoadedCallback;
import com.example.findwork.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    MyRecyclerViewAdapter myRecyclerViewAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        APIconnect apiConnect = new APIconnect();
        ArrayList<JobDataAapi.Job> jobList = new ArrayList<>();  // Sửa đổi kiểu dữ liệu
        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter((Activity) getContext(), jobList);
        binding.rcListJob.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcListJob.setAdapter(myRecyclerViewAdapter);

// Gọi phương thức tải dữ liệu từ API
        apiConnect.loadAPIsConcurrently(new OnDataLoadedCallback() {
            @Override
            public void onDataLoaded(List<JobDataAapi.Job> newJobList) {  // Sửa kiểu dữ liệu
                // Thêm các công việc mới vào danh sách hiện tại
                jobList.addAll(newJobList);  // Thêm công việc mới vào danh sách
                getActivity().runOnUiThread(() -> {
                    myRecyclerViewAdapter.notifyDataSetChanged();  // Thông báo adapter cập nhật dữ liệu
                });
            }
        });






        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}