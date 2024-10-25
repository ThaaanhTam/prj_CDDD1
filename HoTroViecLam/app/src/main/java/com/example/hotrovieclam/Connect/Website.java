package com.example.hotrovieclam.Connect;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Website {
    private static final String TAG = "TopCVScraper";
    private static final String URL = "https://www.topcv.vn/";
    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    public void loadWebsitesConcurrently() {
        executorService.execute(() -> {
            try {
                Document document = Jsoup.connect("https://www.vietnamworks.com/viec-lam-goi-y")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                        .timeout(900000)
                        .get();

                // Lấy tiêu đề của trang
                String title = document.title();
                Elements jobElementss = document.select("div.sc-cKXybt.gCRvSm");
//                Log.d(TAG, "oooooooo: " + title);
//
//                // Lấy tất cả các công việc có thể nằm trong thẻ <h3> hoặc thẻ khác
//                Elements jobElements = document.select("strong");  // Ví dụ, chọn thẻ <h3>
//
//                // Duyệt qua từng thẻ để lấy tên công việc (hoặc các dữ liệu khác)
//                for (Element job : jobElements) {
//                    Log.d(TAG, "gg" + job.text());
//                }
                for (Element jobElement : jobElementss) {
                    // Lấy tiêu đề công việc
                    String jobTitle = jobElement.select("div.sc-dUWDJJ h2 a").text();
                    String jobUrl = jobElement.select("div.sc-dUWDJJ h2 a").attr("href");

                    // Lấy tên công ty
                    String companyName = jobElement.select("div.sc-iLWXdy a").text();
                    String companyUrl = jobElement.select("div.sc-iLWXdy a").attr("href");

                    // Lấy mức lương và địa điểm
                    String salary = jobElement.select("span.sc-enkILE").text();
                    String location = jobElement.select("span.sc-bcSKrn").text();

                    // Lấy ngày cập nhật
                    String updateDate = jobElement.select("div.sc-dBFDNq").text();

                    // Lấy các kỹ năng yêu cầu
                    Elements skillElements = jobElement.select("ul.sc-iZzKWI li label");
                    StringBuilder skills = new StringBuilder();
                    for (Element skill : skillElements) {
                        skills.append(skill.text()).append(", ");
                    }

                    // In ra thông tin công việc
                    Log.d(TAG, "Job Title: " + jobTitle);
                    Log.d(TAG, "Job URL: " + jobUrl);
                    Log.d(TAG, "Company: " + companyName + " (" + companyUrl + ")");
                    Log.d(TAG, "Salary: " + salary);
                    Log.d(TAG, "Location: " + location);
                    Log.d(TAG, "Update Date: " + updateDate);
                    Log.d(TAG, "Skills: " + skills.toString());
                }

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        });
    }

}
