package com.example.hotrovieclam.Connect;

import android.util.Log;

import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.Model.JobDataAPI;
import com.google.gson.Gson;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class API {
    private OkHttpClient okHttpClient = new OkHttpClient();


    private Request requestAPI = new Request.Builder()
            .url("https://jobdataapi.com/api/jobs/?country_code=VN")
            .build();

    public List<Job> loadAPIsConcurrently() {

            List<Job> newJobList = new ArrayList<>();
            try {
                Response response = okHttpClient.newCall(requestAPI).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    // Sử dụng Gson để parse chuỗi JSON thành đối tượng JobDataAPI
                    Log.d("jsonResponse", jsonResponse);
                    Gson gson = new Gson();
                    JobDataAPI jobDataAPI = gson.fromJson(jsonResponse, JobDataAPI.class);

                    // Lấy danh sách công việc mới từ jobDataAPI
                    for (JobDataAPI.Job jobData : jobDataAPI.getResults()) {
                        Job job = new Job();
                        job.setId(jobData.getId());
                        job.setTitle(jobData.getTitle());
                        String rawDescription = jobData.getDescription();
                        String cleanDescription = Jsoup.parse(rawDescription).text();
                        job.setDescription(cleanDescription);
                        job.setLocation(jobData.getLocation());
                        job.setAvatar(jobData.getCompany().getLogo());
                        job.setAgreement("Thoa thuan");
                        newJobList.add(job);
                    }
                    Log.d("newJobList", newJobList.size()+"");
                } else {
                    Log.e("APIconnect", "Response not successful: " + response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newJobList;
    }
}
