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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APIconnect {
    OkHttpClient okHttpClient = new OkHttpClient();
    ExecutorService executorService = Executors.newFixedThreadPool(2);  // 2 luồng cho việc tải

    Request requestAPI1 =  new Request.Builder()
            .url("https://findwork.dev/api/jobs/?format=json")
            .addHeader("Authorization", "Token 7eafb40d7ecace05f44b851f0d602568ce894d80")
            .build();

    Request requestAPI2 = new Request.Builder()
            .url("https://jobdataapi.com/api/jobs/")
            .build();

    // Cập nhật RecyclerView ngay khi tải xong từ mỗi API
    public void loadAPIsConcurrently(OnDataLoadedCallback callback) {
        // Khai báo danh sách công việc hiện tại
        List<JobDataAapi.Job> currentJobList = new ArrayList<>();

        // Luồng 1: Tải dữ liệu từ API 2
        executorService.execute(() -> {
            try {
                Response response = okHttpClient.newCall(requestAPI2).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    // Sử dụng Gson để parse chuỗi JSON thành đối tượng JobDataAapi
                    Gson gson = new Gson();
                    JobDataAapi jobData = gson.fromJson(jsonResponse, JobDataAapi.class);

                    // Lấy danh sách công việc mới từ jobData
                    List<JobDataAapi.Job> newJobList = jobData.getResults();

                    // Kiểm tra và thêm vào danh sách hiện tại
                    if (newJobList != null && !newJobList.isEmpty()) {
                        currentJobList.addAll(newJobList);  // Thêm các công việc mới vào danh sách hiện tại

                        // Cập nhật UI trên main thread
                        new Handler(Looper.getMainLooper()).post(() -> {
                            callback.onDataLoaded(currentJobList);  // Gọi callback để cập nhật giao diện
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Xử lý lỗi nếu cần
            }
        });
    }


//


        // Luồng 2: Tải dữ liệu từ API 2
//        executorService.execute(() -> {
//            try {
//                Response response2 = okHttpClient.newCall(requestAPI2).execute();
//                if (response2.isSuccessful()) {
//                    String responseData = response2.body().string();
//                    JSONObject jsonResponse = new JSONObject(responseData);
//                    JSONArray data = jsonResponse.getJSONArray("results");
//                    for (int i = 0; i < data.length(); i++) {
//                        String companyName = data.getJSONObject(i).getString("company_name");
//                        String location = data.getJSONObject(i).getString("location");
//                        String logo = data.getJSONObject(i).getString("company_logo");
//                        FindWork findWork = new FindWork(logo, companyName, location);
//                        combinedList.add(findWork);
//                        callback.onDataLoaded(findWork);  // Gọi callback cho từng phần tử
//                    }
//                }
//            } catch (IOException | JSONException e) {
//                e.printStackTrace();
//            }
//        });

}
