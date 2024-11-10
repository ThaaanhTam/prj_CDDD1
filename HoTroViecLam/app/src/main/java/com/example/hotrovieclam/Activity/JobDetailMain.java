package com.example.hotrovieclam.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class JobDetailMain extends AppCompatActivity {

    private static final String ARG_JOB_ID = "jobID";
    private String jobID;
    private int sourceId;
    private FirebaseFirestore db;
    private ArrayList<Job> listJob = new ArrayList<>(); // Khởi tạo listJob
    private Job job;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ActivityJobDetailMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJobDetailMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lấy jobID từ Intent
        Intent intent = getIntent();
        jobID = intent.getStringExtra(ARG_JOB_ID);
        sourceId = intent.getIntExtra("sourceId", 0);
        Intent i = getIntent();
        job = (Job) i.getSerializableExtra("KEY_NAME");

        Log.d("aaaaaaaaaaaaaaaaaaaaaaaaaaaa", job.toString());

        //Log.e("JobDetailMain", "Received jobIDdddddddddddddddddddddddddddddd: " + jobID);
        if (jobID == null) {
            Log.e("JobDetailMain", "jobID is null. Please provide a valid jobID.");
            finish(); // Đóng Activity nếu jobID không hợp lệ
            return;
        }
        binding.lvGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        db = FirebaseFirestore.getInstance();


        // Gọi phương thức để lấy chi tiết công việc
//        fỉebaseJobDetails();
        if (sourceId == 3) {
            fỉebaseJobDetails();
        } else {
            Web_APIJobDetails();

        }
        // Khởi tạo Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    private void fỉebaseJobDetails() {
        // Thay "jobs" bằng tên của collection trong Firestore
        db.collection("jobs").document(jobID)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.e("JobDetailMain", "Listen failed.", e);
                        Log.d("JobDetailMainTitle", "dddd");
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String employerId = documentSnapshot.getString("employerId");
                        if (employerId != null) {
                            // Truy vấn người sử dụng từ employerId
                            db.collection("users").document(employerId)
                                    .get()
                                    .addOnSuccessListener(userSnapshot -> {
                                        if (userSnapshot.exists()) {
                                            // Lấy tên và số điện thoại của nhà tuyển dụng
                                            String email = userSnapshot.getString("email");
                                            String employerPhone = userSnapshot.getString("phoneNumber");

                                            // Hiển thị thông tin
                                            Log.d("EmployerDetails", "Name: " + email);
                                            Log.d("EmployerDetails", "Phone: " + employerPhone);

                                            // Cập nhật UI (ví dụ với TextView)
                                            binding.tvEmail.setText(email != null ? email : "N/A");
                                            binding.tvSDT.setText(employerPhone != null ? employerPhone : "N/A");
                                        } else {
                                            Log.w("EmployerDetails", "No such user document");
                                        }
                                        binding.dangkiungtuyen.setOnClickListener(v -> showUploadDialog());
                                    })
                                    .addOnFailureListener(es -> Log.e("EmployerDetails", "Error getting user details", e));
                        }

                        Log.d("JobDetailMainTitle", "jobTitle");

                        // Kiểm tra và hiển thị tiêu đề công việc
                        if (documentSnapshot.contains("title")) {
                            String jobTitle = documentSnapshot.getString("title");
                            Log.d("JobDetailMainTitle", jobTitle);
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
                        // Kiểm tra và hiển thị địa điểm
                        if (documentSnapshot.contains("major")) {
                            String jobLocation = documentSnapshot.getString("location");
                            binding.tvLocation.setText(jobLocation != null ? jobLocation : "N/A");
                        } else {
                            Log.w("JobDetailMain", "Field 'title' does not exist");
                            binding.tvLocation.setText("N/A");
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
        if (job != null) {
            Log.d("aaaaaaaaaaaaaaaaaaaaaaaaaaa", job.getJobURL());
            binding.dangkiungtuyen.setText("Truy cập trang web");
            binding.msgImg.setVisibility(View.GONE);
            binding.emailSDT.setVisibility(View.GONE);
            // Thiết lập tiêu đề công việc
            binding.tvTitle.setText(job.getTitle() != null ? job.getTitle() : "N/A");

            // Thiết lập lĩnh vực công việc
            binding.tvField.setText(job.getMajor() != null ? job.getMajor() : "N/A");

            // Thiết lập mô tả công việc
            binding.tvDescription.setText(job.getDescription() != null ? job.getDescription() : "N/A");
            binding.tvLocation.setText(job.getLocation() != null ? job.getLocation() : "N/A");
            // Thiết lập mức lương tối thiểu
            if (job.getSalaryMin() != -1.0f) {
                binding.tvSalaryMin.setText(String.valueOf(job.getSalaryMin()));
            } else {
                binding.tvSalaryMin.setText("Thỏa thuận");
            }

            // Thiết lập mức lương tối đa
            if (job.getSalaryMax() != -1.0f) {
                binding.tvSalaryMax.setText(String.valueOf(job.getSalaryMax() + "  triệu"));
            } else {
                binding.tvSalaryMax.setText("");
            }
            binding.dangkiungtuyen.setOnClickListener(v -> {
                String jobURL = job.getJobURL();
                if (jobURL != null && !jobURL.isEmpty()) {
                    // Kiểm tra URL hợp lệ và mở trình duyệt
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(jobURL));
                    startActivity(intent);
                } else {
                    Log.e("Web_APIJobDetails", "Invalid job URL.");
                    // Có thể hiển thị thông báo hoặc xử lý khi URL không hợp lệ
                }
            });
        } else {
            Log.e("Web_APIJobDetails", "Job object is null.");
            // Thiết lập các giá trị mặc định nếu job không tồn tại
            binding.tvTitle.setText("N/A");
            binding.tvField.setText("N/A");
            binding.tvDescription.setText("N/A");
            binding.tvSalaryMin.setText("Thỏa thuận");
            binding.tvSalaryMax.setText("N/A");
        }
    }

    private final ActivityResultLauncher<Intent> pdfPicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri fileUri = result.getData().getData();
                    uploadFileToFirebase(fileUri);
                }
            }
    );

    private void showUploadDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.upload_cv);

        Button btnUseCvApp = dialog.findViewById(R.id.btnUseCvApp);
        Button btnUploadFile = dialog.findViewById(R.id.btnUploadFile);

        btnUseCvApp.setOnClickListener(v -> {
            // Xử lý khi chọn sử dụng CV trong app
            useAppCV();
            dialog.dismiss();
        });

        btnUploadFile.setOnClickListener(v -> {
            // Mở file picker để chọn file PDF
            openFilePicker();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void useAppCV() {
        // TODO: Xử lý logic sử dụng CV trong app
        Toast.makeText(this, "Đang sử dụng CV trong app", Toast.LENGTH_SHORT).show();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        pdfPicker.launch(intent);
    }

    private void uploadFileToFirebase(Uri fileUri) {
        if (fileUri != null) {
            // Tạo reference đến vị trí lưu trữ file
            String fileName = "cv_" + System.currentTimeMillis() + ".pdf";
            StorageReference fileRef = storageRef.child("cvs/" + fileName);

            // Upload file
            fileRef.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Lấy download URL
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            // TODO: Lưu URL vào database

                        });
                    })
                    .addOnFailureListener(e -> {

                    })
                    .addOnProgressListener(snapshot -> {
                        // Tính phần trăm quá trình upload
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        // TODO: Hiển thị progress bar nếu cần
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}