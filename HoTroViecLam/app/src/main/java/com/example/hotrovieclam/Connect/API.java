package com.example.hotrovieclam.Connect;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.hotrovieclam.Adapter.AdapterListJobSave;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.Model.JobDataAPI;
import com.example.hotrovieclam.Adapter.MyRecyclerViewAdapter;
import com.google.gson.Gson;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class API {
    private static final String TAG = "API";
    private OkHttpClient okHttpClient = new OkHttpClient();

    private Request requestAPI = new Request.Builder()
            .url("https://jobdataapi.com/api/jobs/?country_code=VN")
            .build();

    public void loadAPIsConcurrently(MyRecyclerViewAdapter adapter, List<Job> jobList) {
        new Thread(() -> {
            try {
                Response response = okHttpClient.newCall(requestAPI).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Log.d("jsonResponse", jsonResponse);
                    Gson gson = new Gson();
                    JobDataAPI jobDataAPI = gson.fromJson(jsonResponse, JobDataAPI.class);

                    for (JobDataAPI.Job jobData : jobDataAPI.getResults()) {

                        Job job = new Job();
                        job.setId(jobData.getId());
                        Log.d(TAG,  jobData.getApplication_url());

                        job.setTitle(jobData.getTitle());
                        String rawDescription = jobData.getDescription();
                        String cleanDescription = Jsoup.parse(rawDescription).text();
                        job.setDescription(cleanDescription);
                        job.setLocation(jobData.getLocation());
                        job.setAvatar(jobData.getCompany().getLogo());
                        job.setJobURL(jobData.getApplication_url());
                        job.setSourceId(1);

                        job.setAgreement("Thỏa thuận");

                        jobList.add(job);
                    }
                    // Cập nhật toàn bộ dữ liệu sau khi thêm tất cả các job
                    new Handler(Looper.getMainLooper()).post(adapter::notifyDataSetChanged);
              //      Log.d("newJobList", jobList.size() + "");
                } else {
                    Log.e(TAG, "Response not successful: " + response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void loadAPIsConcurrentlysave(AdapterListJobSave adapter, List<Job> jobList) {
        new Thread(() -> {
            try {
                Response response = okHttpClient.newCall(requestAPI).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Log.d("jsonResponse", jsonResponse);
                    Gson gson = new Gson();
                    JobDataAPI jobDataAPI = gson.fromJson(jsonResponse, JobDataAPI.class);

                    for (JobDataAPI.Job jobData : jobDataAPI.getResults()) {

                        Job job = new Job();
                        job.setId(jobData.getId());
                        Log.d(TAG,  jobData.getApplication_url());

                        job.setTitle(jobData.getTitle());
                        String rawDescription = jobData.getDescription();
                        String cleanDescription = Jsoup.parse(rawDescription).text();
                        job.setDescription(cleanDescription);
                        job.setLocation(jobData.getLocation());
                        job.setAvatar(jobData.getCompany().getLogo());
                        job.setJobURL(jobData.getApplication_url());
                        job.setSourceId(1);

                        job.setAgreement("Thỏa thuận");

                        jobList.add(job);
                    }
                    // Cập nhật toàn bộ dữ liệu sau khi thêm tất cả các job
                    new Handler(Looper.getMainLooper()).post(adapter::notifyDataSetChanged);
                    //      Log.d("newJobList", jobList.size() + "");
                } else {
                    Log.e(TAG, "Response not successful: " + response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
