package com.example.hotrovieclam.API;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.Model.JobDataAPI;
import com.example.hotrovieclam.OnDataLoadedCallback;
import com.google.gson.Gson;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APIconnect {
    private OkHttpClient okHttpClient = new OkHttpClient();
    private ExecutorService executorService = Executors.newFixedThreadPool(2);  // 2 luồng cho việc tải

    private Request requestAPI = new Request.Builder()
            .url("https://jobdataapi.com/api/jobs/?country_code=VN")
            .build();

    public void loadAPIsConcurrently(OnDataLoadedCallback callback) {
        executorService.execute(() -> {
            try {
                Response response = okHttpClient.newCall(requestAPI).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    // Sử dụng Gson để parse chuỗi JSON thành đối tượng JobDataAPI
                    Log.d("jsonResponse", jsonResponse);
                    Gson gson = new Gson();
                    JobDataAPI jobDataAPI = gson.fromJson(jsonResponse, JobDataAPI.class);

                    // Lấy danh sách công việc mới từ jobDataAPI
                    List<Job> newJobList = new ArrayList<>();
                    for (JobDataAPI.Job jobData : jobDataAPI.getResults()) {
                        Job job = new Job();
                        job.setId(jobData.getId());
                        job.setTitle(jobData.getTitle());
                        String rawDescription = jobData.getDescription();
                        String cleanDescription = Jsoup.parse(rawDescription).text(); // Loại bỏ thẻ HTML
                        job.setDescription(cleanDescription);
                        job.setLocation(jobData.getLocation());
                        job.setAgreement("Thoa thuan");
                        newJobList.add(job);
                    }

                    Log.d("newJobList", newJobList.size()+"");

                    // Cập nhật UI trên main thread
                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onDataLoaded(newJobList);  // Gọi callback để cập nhật giao diện
                    });
                } else {
                    Log.e("APIconnect", "Response not successful: " + response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Gọi callback với danh sách rỗng trong trường hợp có lỗi
                new Handler(Looper.getMainLooper()).post(() -> callback.onDataLoaded(new ArrayList<>()));
            }
        });
    }
}
