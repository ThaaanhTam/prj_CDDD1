package com.example.hotrovieclam.Connect;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.hotrovieclam.Adapter.AdapterListJobSave;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.Adapter.MyRecyclerViewAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Website {
    private static final String TAG = "TopCVScraper";
    private static final String URL1 = "https://123job.vn/tuyen-dung";
    private static final String URL2 = "https://careerviet.vn/viec-lam-noi-bat-trong-tuan-a3";

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    public void loadWebsitesConcurrentlySequentially(MyRecyclerViewAdapter adapter, List<Job> jobList) {
        executorService.submit(() -> {
            try {
                Document documentCareerviet = Jsoup.connect(URL2)
                        .userAgent("Mozilla/5.0")
                        .timeout(90000)
                        .get();
                Elements jobElementsCareerviet = documentCareerviet.select("div.job-item");

                for (Element jobElement : jobElementsCareerviet) {
                    String id = jobElement.attr("id");
                    String image = jobElement.select("img.lazy-img").attr("data-src");
                    String title = jobElement.select(".job_link").text();
                    String salary = jobElement.select(".salary").text();
                    String jobUrl = jobElement.select(".job_link").attr("href");
                    Document descriptionDoc = Jsoup.connect(jobElement.select(".job_link").attr("href")).get();
                    Log.d("mm", jobUrl);
                    String description = descriptionDoc.select("div.detail-row").text();
                    String location = descriptionDoc.select("div.place-name").text();

                    Job job = new Job();
                    job.setAvatar(image);
                    job.setId(id);
                    job.setTitle(title);
                    job.setDescription(description);
                    job.setLocation(location);
                 //   job.setAgreement(salary);
                    job.setJobURL(jobUrl);
                    job.setSourceId(2);
                    try {
                        String[] salaryParts = salary.split("-");
                        if (salaryParts.length == 2) {
                            int salaryMin = Integer.parseInt(salaryParts[0].replaceAll("[^\\d]", "").trim());
                            int salaryMax = Integer.parseInt(salaryParts[1].replaceAll("[^\\d]", "").trim());
                            //return new int[]{salaryMin, salaryMax};
                            job.setSalaryMax(salaryMax);
                            job.setSalaryMin(salaryMin);
                        }
                        else {
                            job.setAgreement(salary);
                        }

                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error parsing salary range: " + e.getMessage());
                    }
                    jobList.add(job);

                    new Handler(Looper.getMainLooper()).post(() -> {
                        adapter.notifyItemInserted(jobList.size() - 1);
                    });
                }
            } catch (IOException e) {
                Log.e(TAG, "Error fetching data from URL2: " + e.getMessage());
            }
        });
        executorService.submit(() -> {
            try {
                Document document123job = Jsoup.connect(URL1)
                        .userAgent("Mozilla/5.0")
                        .timeout(90000)
                        .get();
                Elements jobElements123job = document123job.select("div.job__list-item");

                for (Element jobElement : jobElements123job) {
                    String id = jobElement.attr("data-id");
                    Element jobTitleElement = jobElement.selectFirst("h2.job__list-item-title a");
                    String jobTitle = jobTitleElement.attr("title");
                    String jobUrl = jobTitleElement.attr("href");
                    Document jobDoc = Jsoup.connect(jobUrl).get();

                    String description = jobDoc.select(".content-group__content").text();
                    String imgUrl = jobDoc.select(".company-logo").attr("src");
                    String location = jobElement.selectFirst("div.address").text();
                    String salary = jobElement.selectFirst("div.salary").text();

                    if (imgUrl == null || imgUrl.isEmpty()) {
                        imgUrl = "https://123job.vn/images/no_company.png";
                    }

                    Job job = new Job();
                    job.setId(id);
                    job.setTitle(jobTitle);
                    job.setDescription(description);
                    job.setAvatar(imgUrl);
                    job.setLocation(location);
                    job.setJobURL(jobUrl);
                    job.setSourceId(2);

                    //    job.setAgreement(salary);
                    try {
                        String[] salaryParts = salary.split("-");
                        if (salaryParts.length == 2) {
                            int salaryMin = Integer.parseInt(salaryParts[0].replaceAll("[^\\d]", "").trim());
                            int salaryMax = Integer.parseInt(salaryParts[1].replaceAll("[^\\d]", "").trim());
                            //return new int[]{salaryMin, salaryMax};
                            job.setSalaryMax(salaryMax);
                            job.setSalaryMin(salaryMin);
                        }   else {
                            job.setAgreement(salary);
                        }

                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error parsing salary range: " + e.getMessage());
                    }
                    jobList.add(job);

                    new Handler(Looper.getMainLooper()).post(() -> {
                        adapter.notifyItemInserted(jobList.size() - 1);
                    });
                }
            } catch (IOException e) {
                Log.e(TAG, "Error fetching data from URL1: " + e.getMessage());
            }
        });


    }
    public void loadWebsitesConcurrentlySequentiallysave(AdapterListJobSave adapter, List<Job> jobList) {
        executorService.submit(() -> {
            try {
                Document documentCareerviet = Jsoup.connect(URL2)
                        .userAgent("Mozilla/5.0")
                        .timeout(90000)
                        .get();
                Elements jobElementsCareerviet = documentCareerviet.select("div.job-item");

                for (Element jobElement : jobElementsCareerviet) {
                    String id = jobElement.attr("id");
                    String image = jobElement.select("img.lazy-img").attr("data-src");
                    String title = jobElement.select(".job_link").text();
                    String salary = jobElement.select(".salary").text();
                    String jobUrl = jobElement.select(".job_link").attr("href");
                    Document descriptionDoc = Jsoup.connect(jobElement.select(".job_link").attr("href")).get();
                    Log.d("mm", jobUrl);
                    String description = descriptionDoc.select("div.detail-row").text();
                    String location = descriptionDoc.select("div.place-name").text();

                    Job job = new Job();
                    job.setAvatar(image);
                    job.setId(id);
                    job.setTitle(title);
                    job.setDescription(description);
                    job.setLocation(location);
                    //   job.setAgreement(salary);
                    job.setJobURL(jobUrl);
                    job.setSourceId(2);
                    try {
                        String[] salaryParts = salary.split("-");
                        if (salaryParts.length == 2) {
                            int salaryMin = Integer.parseInt(salaryParts[0].replaceAll("[^\\d]", "").trim());
                            int salaryMax = Integer.parseInt(salaryParts[1].replaceAll("[^\\d]", "").trim());
                            //return new int[]{salaryMin, salaryMax};
                            job.setSalaryMax(salaryMax);
                            job.setSalaryMin(salaryMin);
                        }
                        else {
                            job.setAgreement(salary);
                        }

                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error parsing salary range: " + e.getMessage());
                    }
                    jobList.add(job);

                    new Handler(Looper.getMainLooper()).post(() -> {
                        adapter.notifyItemInserted(jobList.size() - 1);
                    });
                }
            } catch (IOException e) {
                Log.e(TAG, "Error fetching data from URL2: " + e.getMessage());
            }
        });
        executorService.submit(() -> {
            try {
                Document document123job = Jsoup.connect(URL1)
                        .userAgent("Mozilla/5.0")
                        .timeout(90000)
                        .get();
                Elements jobElements123job = document123job.select("div.job__list-item");

                for (Element jobElement : jobElements123job) {
                    String id = jobElement.attr("data-id");
                    Element jobTitleElement = jobElement.selectFirst("h2.job__list-item-title a");
                    String jobTitle = jobTitleElement.attr("title");
                    String jobUrl = jobTitleElement.attr("href");
                    Document jobDoc = Jsoup.connect(jobUrl).get();

                    String description = jobDoc.select(".content-group__content").text();
                    String imgUrl = jobDoc.select(".company-logo").attr("src");
                    String location = jobElement.selectFirst("div.address").text();
                    String salary = jobElement.selectFirst("div.salary").text();

                    if (imgUrl == null || imgUrl.isEmpty()) {
                        imgUrl = "https://123job.vn/images/no_company.png";
                    }

                    Job job = new Job();
                    job.setId(id);
                    job.setTitle(jobTitle);
                    job.setDescription(description);
                    job.setAvatar(imgUrl);
                    job.setLocation(location);
                    job.setJobURL(jobUrl);
                    job.setSourceId(2);

                    //    job.setAgreement(salary);
                    try {
                        String[] salaryParts = salary.split("-");
                        if (salaryParts.length == 2) {
                            int salaryMin = Integer.parseInt(salaryParts[0].replaceAll("[^\\d]", "").trim());
                            int salaryMax = Integer.parseInt(salaryParts[1].replaceAll("[^\\d]", "").trim());
                            //return new int[]{salaryMin, salaryMax};
                            job.setSalaryMax(salaryMax);
                            job.setSalaryMin(salaryMin);
                        }   else {
                            job.setAgreement(salary);
                        }

                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error parsing salary range: " + e.getMessage());
                    }
                    jobList.add(job);

                    new Handler(Looper.getMainLooper()).post(() -> {
                        adapter.notifyItemInserted(jobList.size() - 1);
                    });
                }
            } catch (IOException e) {
                Log.e(TAG, "Error fetching data from URL1: " + e.getMessage());
            }
        });


    }
}

