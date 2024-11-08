package com.example.hotrovieclam.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotrovieclam.Fragment.RecruiterManagement.DetailinfoJob;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityJobDetailMainBinding;
import com.example.hotrovieclam.databinding.FragmentDetailinfoJobBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class JobDetailMain extends AppCompatActivity {

    private static final String ARG_JOB_ID = "jobID";
    private String jobID;
    private int sourceId;
    private FirebaseFirestore db;
    private ArrayList<Job> listJob = new ArrayList<>(); // Khởi tạo listJob


    private ActivityJobDetailMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJobDetailMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lấy jobID từ Intent
        Intent intent = getIntent();
        jobID = intent.getStringExtra(ARG_JOB_ID);
        sourceId = intent.getIntExtra("sourceId",0);
        Intent i = getIntent();
        Job job = (Job) i.getSerializableExtra("KEY_NAME");

       Log.d("aaaaaaaaaaaaaaaaaaaaaaaaaaaa",job.toString());

        //Log.e("JobDetailMain", "Received jobIDdddddddddddddddddddddddddddddd: " + jobID);
        if (jobID == null) {
            Log.e("JobDetailMain", "jobID is null. Please provide a valid jobID.");
            finish(); // Đóng Activity nếu jobID không hợp lệ
            return;
        }
        binding.lvGoBack.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                finish();
            }
        });
        db = FirebaseFirestore.getInstance();


        // Gọi phương thức để lấy chi tiết công việc
        fỉebaseJobDetails();
        if (sourceId==1){
            Log.d("sourceIdddddddddddd","1");
        }else if (sourceId==2){
            Log.d("sourceIdddddddddddd","2");
        }else {
            Log.d("sourceIdddddddddddd","3");

        }
       //displayJobDetails();
    }

    private void fỉebaseJobDetails() {
        binding.tvDescription.setText(jobID != null ? jobID : "N/A");
        // Thay "jobs" bằng tên của collection trong Firestore
        db.collection("jobs").document(jobID)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.e("JobDetailMain", "Listen failed.", e);
                        Log.d("JobDetailMainTitle","dddd");

                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Log.d("JobDetailMainTitle","jobTitle");

                        // Kiểm tra và hiển thị tiêu đề công việc
                        if (documentSnapshot.contains("title")) {
                            String jobTitle = documentSnapshot.getString("title");
                            Log.d("JobDetailMainTitle",jobTitle);
                            binding.tvTitle.setText(jobTitle != null ? jobTitle : "N/A");
                        } else {
                            Log.w("JobDetailMain", "Field 'title' does not exist");
                            binding.tvTitle.setText("N/A");
                        }
                        // Kiểm tra và hiển thị lĩnh vực
                        if (documentSnapshot.contains("major")) {
                            String jobMajor = documentSnapshot.getString("major");
                            binding.tvField.setText(jobMajor != null ? jobMajor : "N/A");
                        } else {
                            Log.w("JobDetailMain", "Field 'title' does not exist");
                            binding.tvField.setText("N/A");
                        }

                        // Kiểm tra và hiển thị mô tả công việc
                        if (documentSnapshot.contains("description")) {
                            String description = documentSnapshot.getString("description");
                            binding.tvDescription.setText(description != null ? description : "N/A");
                        } else {
                            Log.w("JobDetailMain", "Field 'description' does not exist");
                            binding.tvDescription.setText("N/A");
                        }

                        // Kiểm tra và hiển thị mức lương tối thiểu
                        if (documentSnapshot.contains("salaryMin")) {
                            Double salaryMin = documentSnapshot.getDouble("salaryMin");
                            binding.tvSalaryMin.setText(salaryMin != null ? String.valueOf(salaryMin) : "N/A");
                        } else {
                            Log.w("JobDetailMain", "Field 'salaryMin' does not exist");
                            binding.tvSalaryMin.setText("N/A");
                        }

                        // Kiểm tra và hiển thị mức lương tối đa
                        if (documentSnapshot.contains("salaryMax")) {
                            Double salaryMax = documentSnapshot.getDouble("salaryMax");
                            binding.tvSalaryMax.setText(salaryMax != null ? String.valueOf(salaryMax) : "N/A");
                        } else {
                            Log.w("JobDetailMain", "Field 'salaryMax' does not exist");
                            binding.tvSalaryMax.setText("N/A");
                        }

                    } else {
                        Log.d("JobDetailMainTitle", "No such document");
                    }


                });
    }
    private void Web_APIJobDetails() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}