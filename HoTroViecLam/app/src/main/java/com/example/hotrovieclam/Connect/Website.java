package com.example.hotrovieclam.Connect;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.hotrovieclam.Model.Job;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.http.Url;

public class Website {
    private static final String TAG = "TopCVScraper";
    private static final String URL = "https://123job.vn/tuyen-dung";// 2 luồng cho việc tải
    private static final String URL2 = "https://careerviet.vn/viec-lam-noi-bat-trong-tuan-a30?utm_source=coccoc_context&utm_medium=CPC&utm_campaign=CCSP_DoiThu&utm_term=careerlink&utm_content=17899228&md=_05re50vdaa*Cr3MhoOtvxABYZwMXtuOsxfZFilEtJDJn3xwTnX7mmc7E0GaMWVsvJdMyZCfzeRDu4sW0R7W9v2sT3FwZLNexet*6rFFuaWu9ym9bQAjRbia8uVyC4at8QNR8.&ctm_event_id=2504129134";

    public List<Job> loadWebsitesConcurrently() {
        List<Job> newJobList = new ArrayList<>();
            try {
                Document document123job = Jsoup.connect(URL)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .timeout(90000)  // Tăng timeout lên 30 giây
                        .get();
                Document documentCareeviet = Jsoup.connect(URL2)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .timeout(90000)  // Tăng timeout lên 30 giây
                        .get();



                Elements jobElements123job = document123job.select("div.job__list-item");
                Elements jobElementsCareeviet = documentCareeviet.select("div.figure");

                for (Element jobElement : jobElementsCareeviet) {
                    String id = jobElement.select("div.job__list-item").attr("data-id");
                    Element jobTitleElement = jobElement.selectFirst(".lazy-img");
                    String jobUrll = jobTitleElement.attr("src");


                    String jobTitlee = jobTitleElement.attr("alt");

//                    Document document2 = Jsoup.connect(jobUrl)
//                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
//                            .timeout(90000)  // Tăng timeout lên 30 giây
//                            .get();
//
//                    String description =  document2.select(".content-group__content").text();
//                    String imgUrl = document2.select(".company-logo").attr("src");
//                    //  imgUrl = imgUrl.replace("this.src='", "").replace("'", "");
//                    if (imgUrl == null || imgUrl.isEmpty()) {
//
//                        imgUrl =  "https://123job.vn/images/no_company.png";
//
//                    }
//
//                    // Lấy tên công ty
//                    String companyName = jobElement.selectFirst("div.job__list-item-company span").text();
//
//                    // Lấy địa điểm làm việc
//                    String jobLocation = jobElement.selectFirst("div.address").text();
//
//                    // Lấy mức lương
//                    String jobSalary = jobElement.selectFirst("div.salary").text();
//
//                    Job job = new Job();
//                    job.setAvatar(imgUrl);
//                    job.setId(id);
//                    job.setTitle(jobTitle);
//                    job.setDescription(description);
//                    job.setLocation(jobLocation);
//                    job.setAgreement(jobSalary);
//                    newJobList.add(job);

                    // Lấy mô tả ngắn
                    Log.d("im", jobUrll);
                    Log.d("tt", jobTitlee);

                }




                for (Element jobElement : jobElements123job) {
                    String id = jobElement.select("div.job__list-item").attr("data-id");
                    Element jobTitleElement = jobElement.selectFirst("h2.job__list-item-title a");
                    String jobTitle = jobTitleElement.attr("title");
                    String jobUrl = jobTitleElement.attr("href");
                    Document document2 = Jsoup.connect(jobUrl)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                            .timeout(90000)  // Tăng timeout lên 30 giây
                            .get();

                    String description =  document2.select(".content-group__content").text();
                    String imgUrl = document2.select(".company-logo").attr("src");
                  //  imgUrl = imgUrl.replace("this.src='", "").replace("'", "");
                    if (imgUrl == null || imgUrl.isEmpty()) {

                        imgUrl =  "https://123job.vn/images/no_company.png";

                    }

                    // Lấy tên công ty
                    String companyName = jobElement.selectFirst("div.job__list-item-company span").text();

                    // Lấy địa điểm làm việc
                    String jobLocation = jobElement.selectFirst("div.address").text();

                    // Lấy mức lương
                    String jobSalary = jobElement.selectFirst("div.salary").text();

                    Job job = new Job();
                    job.setAvatar(imgUrl);
                    job.setId(id);
                    job.setTitle(jobTitle);
                    job.setDescription(description);
                    job.setLocation(jobLocation);
                    job.setAgreement(jobSalary);
                    job.setSourceId(2);
                    newJobList.add(job);

                    // Lấy mô tả ngắn





                    // In ra thông tin
                    Log.d("Job Title: " , jobTitle);
                    Log.d("Job URL: " , jobUrl);
                    Log.d("Company Name: " , companyName);
                    Log.d("Location: " , jobLocation);
                    Log.d("Salary: " , jobSalary);
                    Log.d("imgUrl: " , imgUrl);
                    Log.d("description: " , description);
                    Log.d("id: " , id);
                }

            } catch (IOException e) {
                // Xử lý lỗi nếu không tải được trang
                Log.e(TAG, "Error fetching data: " + e.getMessage());
            }
            return newJobList;
    }
}
