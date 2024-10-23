package com.example.hotrovieclam.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hotrovieclam.Connect.API;
import com.example.hotrovieclam.Adapter.MyRecyclerViewAdapter;
import com.example.hotrovieclam.Connect.Website;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.OnDataLoadedCallback;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    private ArrayList<Job> jobList; // Khai báo danh sách công việc

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

        // Gọi phương thức tải dữ liệu từ API
        API apiConnect = new API();
        apiConnect.loadAPIsConcurrently(new OnDataLoadedCallback() {
            @Override
            public void onDataLoaded(List<Job> newJobList) {
                // Thêm các công việc mới vào danh sách hiện tại
                jobList.addAll(newJobList);

                // Cập nhật UI từ luồng chính
                runOnUiThread(() -> myRecyclerViewAdapter.notifyDataSetChanged());
            }


        });
        Website website = new Website();
        website.loadWebsitesConcurrently();
    }
}
