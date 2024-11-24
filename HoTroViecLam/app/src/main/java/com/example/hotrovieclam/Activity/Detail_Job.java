package com.example.hotrovieclam.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.hotrovieclam.Adapter.DetailAdapter;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityDetailJobBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class Detail_Job extends AppCompatActivity {


    ActivityDetailJobBinding binding;
    private DetailAdapter adapter;
    private String jobID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailJobBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.title));

        jobID = getIntent().getStringExtra("jobID");

         adapter = new DetailAdapter(this,jobID);

        binding.viewPager.setAdapter(adapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Chi tiết công việc");
                            break;
                        case 1:
                            tab.setText("Danh sách ứng tuyển");
                            break;
                    }
                }).attach();
binding.lvGoBack.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        finish();
    }
});


    }
}