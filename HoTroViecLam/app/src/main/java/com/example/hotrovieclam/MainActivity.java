package com.example.hotrovieclam;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotrovieclam.Model.CompanyInfo;
import com.example.hotrovieclam.databinding.RegisterEmployerBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

        ImFrontID = findViewById(R.id.ImFrontID);
        ImBackID = findViewById(R.id.ImBackID);
        ImBusinessLicense = findViewById(R.id.ImBusinessLicense);

        tvFrontIDPath = findViewById(R.id.tvFrontIDPath);
        tvBackIDPath = findViewById(R.id.tvBackIDPath);
        tvBusinessLicensePath = findViewById(R.id.tvBusinessLicensePath);

        btnSubmit = findViewById(R.id.btnSubmit);

        // Gán sự kiện chọn ảnh cho các ImageView
        ImFrontID.setOnClickListener(v -> pickImage(101));
        ImBackID.setOnClickListener(v -> pickImage(102));
        ImBusinessLicense.setOnClickListener(v -> pickImage(103));

        // Gán sự kiện cho nút Submit
        binding.btnSubmit.setOnClickListener(view -> {
            if (validateInputs()) { // Kiểm tra các trường hợp nhập
                saveDataToFirebase();
            }
        });
    }

    // Phương thức mở Intent để chọn ảnh
    private void pickImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    // Xử lý kết quả trả về sau khi chọn ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            String imagePath = selectedImageUri.toString(); // Lấy đường dẫn ảnh

            switch (requestCode) {
                case 101:
                    frontCCCDUri = selectedImageUri;
                    ImFrontID.setImageURI(frontCCCDUri);
                    tvFrontIDPath.setText(imagePath); // Hiển thị đường dẫn ảnh mặt trước
                    break;
                case 102:
                    backCCCDUri = selectedImageUri;
                    ImBackID.setImageURI(backCCCDUri);
                    tvBackIDPath.setText(imagePath); // Hiển thị đường dẫn ảnh mặt sau
                    break;
                case 103:
                    companyCertUri = selectedImageUri;
                    ImBusinessLicense.setImageURI(companyCertUri);
                    tvBusinessLicensePath.setText(imagePath); // Hiển thị đường dẫn giấy phép
                    break;
            }
        }
    }

    // Phương thức lưu dữ liệu lên Firebase
    private void saveDataToFirebase() {
        if (frontCCCDUri != null && backCCCDUri != null && companyCertUri != null) {
            uploadImage(frontCCCDUri, "front_cccd", frontUrl ->
                    uploadImage(backCCCDUri, "back_cccd", backUrl ->
                            uploadImage(companyCertUri, "company_cert", certUrl ->
                                    saveToDatabase(frontUrl, backUrl, certUrl)
                            )
                    )
            );
        } else {
            Toast.makeText(this, "Vui lòng tải đủ tất cả các ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    // Phương thức tải ảnh lên Firebase Storage và lấy URL
    private void uploadImage(Uri uri, String path, OnSuccessListener<String> onSuccess) {
        StorageReference fileRef = storageReference.child("images/" + path);
        fileRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot ->
                        fileRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                            onSuccess.onSuccess(downloadUrl.toString());
                        }).addOnFailureListener(e -> {
                            Log.e("Firebase", "Không thể lấy URL tải xuống", e);
                            Toast.makeText(MainActivity.this, "Lỗi khi lấy URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                ).addOnFailureListener(e -> {
                    Log.e("Firebase", "Lỗi khi tải lên", e);
                    Toast.makeText(MainActivity.this, "Lỗi khi tải lên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    // Phương thức lưu thông tin công ty vào Firebase Database
    private void saveToDatabase(String frontUrl, String backUrl, String certUrl) {
        CompanyInfo companyInfo = new CompanyInfo(
                binding.etRecruiterName.getText().toString().trim(),
                binding.etPhoneNumber.getText().toString().trim(),
                binding.etCompanyMail.getText().toString().trim(),
                binding.etCompanyName.getText().toString().trim(),
                binding.etLocation.getText().toString().trim(),
                binding.etWebsite.getText().toString().trim(),
                frontUrl, backUrl, certUrl
        );

        databaseReference.push().setValue(companyInfo).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Lưu dữ liệu thành công", Toast.LENGTH_SHORT).show();
                clearInputs(); // Xóa các trường nhập sau khi lưu thành công
                Log.d("Firebase", "Dữ liệu đã được lưu: " + companyInfo.toString());
            } else {
                Toast.makeText(this, "Lưu dữ liệu thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Kiểm tra xem tất cả các trường nhập có hợp lệ không
    private boolean validateInputs() {
        if (etRecruiterName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên người tuyển dụng", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPhoneNumber.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etCompanyMail.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email công ty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etCompanyName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên công ty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etLocation.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etWebsite.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập website", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Phương thức xóa các trường nhập
    private void clearInputs() {
        etRecruiterName.setText("");
        etPhoneNumber.setText("");
        etCompanyMail.setText("");
        etCompanyName.setText("");
        etLocation.setText("");
        etWebsite.setText("");
        ImFrontID.setImageURI(null);
        ImBackID.setImageURI(null);
        ImBusinessLicense.setImageURI(null);
        tvFrontIDPath.setText("");
        tvBackIDPath.setText("");
        tvBusinessLicensePath.setText("");
        frontCCCDUri = null;
        backCCCDUri = null;
        companyCertUri = null;
    }
}
