package com.example.hotrovieclam;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hotrovieclam.Connect.API;
import com.example.hotrovieclam.Adapter.MyRecyclerViewAdapter;
import com.example.hotrovieclam.Connect.Website;
import com.example.hotrovieclam.FireBase.Storage;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    private ArrayList<Job> jobList; // Khai báo danh sách công việc

    ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Bật chế độ Edge to Edge
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root); // Sử dụng root view từ binding

        // Thiết lập padding cho view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo danh sách công việc và adapter
        jobList = new ArrayList<>();
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(this, jobList);
        binding.rcListJob.setLayoutManager(new LinearLayoutManager(this));
        binding.rcListJob.setAdapter(myRecyclerViewAdapter);


       Runnable task1 = () -> {
            API api = new API();
            jobList.addAll(api.loadAPIsConcurrently());
               runOnUiThread(() -> myRecyclerViewAdapter.notifyDataSetChanged());
        };
        Runnable task2 = () -> {
            Website website = new Website();
            jobList.addAll(website.loadWebsitesConcurrently());
            runOnUiThread(() -> myRecyclerViewAdapter.notifyDataSetChanged());
        };
        executorService.submit(task1);
        executorService.submit(task2);


    }
    
}
