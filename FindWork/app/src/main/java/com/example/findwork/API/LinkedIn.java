package com.example.findwork.API;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.findwork.Job.JobDataAapi;
import com.example.findwork.OnDataLoadedCallback;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LinkedIn {
    OkHttpClient client = new OkHttpClient();
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    Request request = new Request.Builder()
            .url("https://api.linkedin.com/v2/jobPosts")
            .addHeader("86w0dw8sjvphn7", "Bearer WPL_AP1.vRW7qOyGVFMRhmI3.xXY9wQ==")
            .build();

    public void loadAPIsConcurrently(OnDataLoadedCallback callback) {
        // Khai báo danh sách công việc hiện tại
        List<JobDataAapi.Job> currentJobList = new ArrayList<>();

        // Luồng 1: Tải dữ liệu từ API 2
        executorService.execute(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Log.d("ss", jsonResponse);
                    // Sử dụng Gson để parse chuỗi JSON thành đối tượng JobDataAapi
//                    Gson gson = new Gson();
//                    JobDataAapi jobData = gson.fromJson(jsonResponse, JobDataAapi.class);
//
//                    // Lấy danh sách công việc mới từ jobData
//                    List<JobDataAapi.Job> newJobList = jobData.getResults();
//
//                    // Kiểm tra và thêm vào danh sách hiện tại
//                    if (newJobList != null && !newJobList.isEmpty()) {
//                        currentJobList.addAll(newJobList);  // Thêm các công việc mới vào danh sách hiện tại
//
//                        // Cập nhật UI trên main thread
//                        new Handler(Looper.getMainLooper()).post(() -> {
//                            callback.onDataLoaded(currentJobList);  // Gọi callback để cập nhật giao diện
//                        });
//                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Error", "Failed to fetch job data", e);

            }
        });
    }

}
