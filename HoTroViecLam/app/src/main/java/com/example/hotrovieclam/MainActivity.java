package com.example.hotrovieclam;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hotrovieclam.Connect.API;
import com.example.hotrovieclam.Adapter.MyRecyclerViewAdapter;
import com.example.hotrovieclam.Connect.Website;
import com.example.hotrovieclam.FireBase.Storage;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    // Khai báo thành phần giao diện và biến Firebase
    private Uri frontCCCDUri, backCCCDUri, companyCertUri;
    private EditText etRecruiterName, etPhoneNumber, etCompanyMail, etCompanyName, etLocation, etWebsite;
    private Button btnSubmit;
    private ImageView ImFrontID, ImBackID, ImBusinessLicense;
    private TextView tvFrontIDPath, tvBackIDPath, tvBusinessLicensePath;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private RegisterEmployerBinding binding;

    ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bật chế độ Edge to Edge và thiết lập giao diện
        EdgeToEdge.enable(this);
        binding = RegisterEmployerBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        // Khởi tạo Firebase Database và Storage
        databaseReference = FirebaseDatabase.getInstance().getReference("Companies");
        storageReference = FirebaseStorage.getInstance().getReference();

        // Ánh xạ các thành phần giao diện
        etRecruiterName = findViewById(R.id.etRecruiterName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etCompanyMail = findViewById(R.id.etCompanyMail);
        etCompanyName = findViewById(R.id.etCompanyName);
        etLocation = findViewById(R.id.etLocation);
        etWebsite = findViewById(R.id.etWebsite);

        setContentView(root); // Sử dụng root view từ binding

        // Thiết lập padding cho view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo danh sách công việc và adapter
        jobList = new ArrayList<>();
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(this, jobList);
        binding.rcListJob.setLayoutManager(new LinearLayoutManager(this));
        binding.rcListJob.setAdapter(myRecyclerViewAdapter);


       Runnable task1 = () -> {
            API api = new API();
            jobList.addAll(api.loadAPIsConcurrently());
               runOnUiThread(() -> myRecyclerViewAdapter.notifyDataSetChanged());
        };
        Runnable task2 = () -> {
            Website website = new Website();
            jobList.addAll(website.loadWebsitesConcurrently());
            runOnUiThread(() -> myRecyclerViewAdapter.notifyDataSetChanged());
        };
        executorService.submit(task1);
        executorService.submit(task2);


    }
    
}
