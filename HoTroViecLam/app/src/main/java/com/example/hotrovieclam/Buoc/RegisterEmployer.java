package com.example.hotrovieclam.Buoc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotrovieclam.Model.CompanyInfo;
import com.example.hotrovieclam.databinding.RegisterEmployerBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class RegisterEmployer extends AppCompatActivity {
    RegisterEmployerBinding binding;
    private StorageReference storageReference;
    private Uri frontCCCDUri, backCCCDUri, companyCertUri, logoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = RegisterEmployerBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        storageReference = FirebaseStorage.getInstance().getReference();

        binding.ImLogo.setOnClickListener(v -> pickImage(100));
        binding.ImFrontID.setOnClickListener(v -> pickImage(101));
        binding.ImBackID.setOnClickListener(v -> pickImage(102));
        binding.ImBusinessLicense.setOnClickListener(v -> pickImage(103));
        binding.btnSubmit.setOnClickListener(view -> {
            saveDataToFirebase(frontCCCDUri, backCCCDUri, companyCertUri, logoUri);
        });


        setContentView(root);
    }

    private void pickImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }


    private void saveDataToFirebase(Uri frontCCCDUri, Uri backCCCDUri, Uri companyCertUri, Uri logoUri) {
        if (frontCCCDUri != null && backCCCDUri != null && companyCertUri != null && logoUri != null) {
            String uniqueId = UUID.randomUUID().toString();

            uploadImage(logoUri, "logo_" + uniqueId, logo ->
                    uploadImage(frontCCCDUri, "front_cccd_" + uniqueId, frontUrl ->
                            uploadImage(backCCCDUri, "back_cccd_" + uniqueId, backUrl ->
                                    uploadImage(companyCertUri, "company_cert_" + uniqueId, certUrl -> {
                                        // Tách tên file từ URL
                                        String logoFileName = "logo_" + uniqueId;
                                        String frontFileName = "front_cccd_" + uniqueId;
                                        String backFileName = "back_cccd_" + uniqueId;
                                        String certFileName = "company_cert_" + uniqueId;

                                        // Gọi phương thức lưu vào Firestore với tên file
                                        saveToFirestore(logoFileName,frontFileName, backFileName, certFileName);
                                    })
                            )
                    )
            );
        } else {
            Toast.makeText(this, "Vui lòng tải đủ tất cả các ảnh", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveToFirestore(String logoFileName, String frontFileName, String backFileName, String certFileName) {
        String recruiterName = binding.etRecruiterName.getText().toString().trim();
        String phoneNumber = binding.etPhoneNumber.getText().toString().trim();
        String companyMail = binding.etCompanyMail.getText().toString().trim();
        String companyName = binding.etCompanyName.getText().toString().trim();
        String location = binding.etLocation.getText().toString().trim();
        String website = binding.etWebsite.getText().toString().trim();
        String status = "dang cho duyet";

        // Kiểm tra các trường không được để trống
        if (recruiterName.isEmpty() || phoneNumber.isEmpty() || companyMail.isEmpty() ||
                companyName.isEmpty() || location.isEmpty() || website.isEmpty()) {
            validateInputs();
            return;
        }

        CompanyInfo companyInfo = new CompanyInfo(recruiterName, phoneNumber, companyMail, companyName, location, website, frontFileName, backFileName, certFileName,logoFileName,status);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("employer")
                .add(companyInfo)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Lưu dữ liệu thành công", Toast.LENGTH_SHORT).show();
                    clearInputs(); // Xóa các trường nhập sau khi lưu thành công
                    Log.d("Firestore", "Dữ liệu đã được lưu: " + companyInfo.toString());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lưu dữ liệu thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void uploadImage(Uri uri, String path, OnSuccessListener<String> onSuccess) {
        StorageReference fileRef = storageReference.child("images/" + path);
        fileRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot ->
                        fileRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                            // Tạo tên file và trả về dưới dạng URL
                            String fileName = path; // Hoặc có thể thay đổi nếu cần
                            String fullUrl = downloadUrl.toString() + " (" + fileName + ")";
                            onSuccess.onSuccess(fullUrl);
                        }).addOnFailureListener(e -> {
                            Log.e("Firebase", "Không thể lấy URL tải xuống", e);
                            Toast.makeText(this, "Lỗi khi lấy URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                ).addOnFailureListener(e -> {
                    Log.e("Firebase", "Lỗi khi tải lên", e);
                    Toast.makeText(this, "Lỗi khi tải lên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            String imagePath = selectedImageUri.toString(); // Lấy đường dẫn ảnh

            switch (requestCode) {
                case 100:
                    logoUri = selectedImageUri;
                    //  binding.ImFrontID.setImageURI(frontCCCDUri);
                    binding.tvImLogo.setText(imagePath); // Hiển thị đường dẫn ảnh logo
                    break;
                case 101:
                    frontCCCDUri = selectedImageUri;
                    //  binding.ImFrontID.setImageURI(frontCCCDUri);
                    binding.tvFrontIDPath.setText(imagePath); // Hiển thị đường dẫn ảnh mặt trước
                    break;
                case 102:
                    backCCCDUri = selectedImageUri;
                    //binding.ImBackID.setImageURI(backCCCDUri);
                    binding.tvBackIDPath.setText(imagePath); // Hiển thị đường dẫn ảnh mặt sau
                    break;
                case 103:
                    companyCertUri = selectedImageUri;
                    // binding.ImBusinessLicense.setImageURI(companyCertUri);
                    binding.tvBusinessLicensePath.setText(imagePath); // Hiển thị đường dẫn giấy phép
                    break;
            }
        }
    }


    private boolean validateInputs() {
        if (binding.etRecruiterName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên người tuyển dụng", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.etPhoneNumber.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.etCompanyMail.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email công ty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.etCompanyName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên công ty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.etLocation.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.etWebsite.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập website", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void clearInputs() {
        binding.etRecruiterName.setText("");
        binding.etPhoneNumber.setText("");
        binding.etCompanyMail.setText("");
        binding.etCompanyName.setText("");
        binding.etLocation.setText("");
        binding.etWebsite.setText("");
        binding.ImLogo.setImageURI(null);
        binding.ImFrontID.setImageURI(null);
        binding.ImBackID.setImageURI(null);
        binding.ImBusinessLicense.setImageURI(null);
        binding.tvImLogo.setText("");
        binding.tvFrontIDPath.setText("");
        binding.tvBackIDPath.setText("");
        binding.tvBusinessLicensePath.setText("");
        frontCCCDUri = null;
        backCCCDUri = null;
        companyCertUri = null;
        logoUri = null;
    }
}