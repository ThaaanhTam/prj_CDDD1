package com.example.hotrovieclam.Buoc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotrovieclam.Model.CompanyInfo;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.databinding.RegisterEmployerBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterEmployer extends AppCompatActivity {
    RegisterEmployerBinding binding;
    private StorageReference storageReference;
    private Uri frontCCCDUri, backCCCDUri, companyCertUri, logoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo binding và set layout
        binding = RegisterEmployerBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        HienThiThongTin();
        // Khởi tạo Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference();

        // Gán sự kiện click cho các trường ảnh
        binding.ImLogo.setOnClickListener(v -> pickImage(100));
        binding.ImFrontID.setOnClickListener(v -> pickImage(101));
        binding.ImBackID.setOnClickListener(v -> pickImage(102));
        binding.ImBusinessLicense.setOnClickListener(v -> pickImage(103));

        // Thiết lập kiểm tra dữ liệu khi mất focus
        setupFieldValidation();


        // Khi người dùng nhấn nút Submit
        binding.btnSubmit.setOnClickListener(view -> {
            if (validateInputs()) {
                showLoadingState(true);
                saveDataToFirebase(frontCCCDUri, backCCCDUri, companyCertUri, logoUri);
            } else {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            }
        });

        // Thiết lập sự kiện cho nút "Quay lại"
        binding.back.setOnClickListener(v -> finish());



    }
    // Hiển thị trạng thái tải
    private void showLoadingState(boolean isLoading) {
        if (isLoading) {
            binding.btnSubmit.setEnabled(false);  // Vô hiệu hóa nút
            binding.btnSubmit.animate().alpha(0f).setDuration(200).start(); // Làm mờ nút
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.progressBar.animate().alpha(1f).setDuration(200).start(); // Hiện vòng tròn tải
        } else {
            binding.progressBar.animate().alpha(0f).setDuration(200).start(); // Làm mờ vòng tròn tải
            binding.progressBar.setVisibility(View.GONE);
            binding.btnSubmit.setEnabled(true);
            binding.btnSubmit.animate().alpha(1f).setDuration(200).start(); // Hiện lại nút
        }
    }


    // Phương thức mở thư viện ảnh và chọn ảnh
    private void pickImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    // Thiết lập kiểm tra từng trường khi mất focus
    private void setupFieldValidation() {
        setupFieldFocusListener(binding.etRecruiterName, "Vui lòng nhập tên nhà tuyển dụng");
        setupFieldFocusListener(binding.etPhoneNumber, "Vui lòng nhập số điện thoại");
        setupFieldFocusListener(binding.etCompanyMail, "Vui lòng nhập email công ty");
        setupFieldFocusListener(binding.etCompanyName, "Vui lòng nhập tên công ty");
        setupFieldFocusListener(binding.etLocation, "Vui lòng nhập địa chỉ");
        setupFieldFocusListener(binding.etWebsite, "Vui lòng nhập website");
    }

    // Thiết lập sự kiện khi trường mất focus
    private void setupFieldFocusListener(EditText editText, String errorMessage) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && editText.getText().toString().trim().isEmpty()) {
                editText.setError(errorMessage); // Hiển thị lỗi nếu bỏ trống
            }
        });
    }

    // Kiểm tra xem tất cả các trường có được điền đầy đủ không
    private boolean validateInputs() {
        boolean isValid = true;

        // Kiểm tra các trường không được để trống
        if (binding.etRecruiterName.getText().toString().trim().isEmpty()) {
            binding.etRecruiterName.setError("Vui lòng nhập tên nhà tuyển dụng");
            isValid = false;
        }
        if (binding.etPhoneNumber.getText().toString().trim().isEmpty()) {
            binding.etPhoneNumber.setError("Vui lòng nhập số điện thoại");
            isValid = false;
        } else if (!isPhoneNumberValid(binding.etPhoneNumber.getText().toString().trim())) {
            binding.etPhoneNumber.setError("Số điện thoại phải có 10 chữ số");
            isValid = false;
        }
        if (binding.etCompanyMail.getText().toString().trim().isEmpty()) {
            binding.etCompanyMail.setError("Vui lòng nhập email công ty");
            isValid = false;
        } else if (!isEmailValid(binding.etCompanyMail.getText().toString().trim())) {
            binding.etCompanyMail.setError("Vui lòng nhập email hợp lệ");
            isValid = false;
        }
        if (binding.etCompanyName.getText().toString().trim().isEmpty()) {
            binding.etCompanyName.setError("Vui lòng nhập tên công ty");
            isValid = false;
        }
        if (binding.etLocation.getText().toString().trim().isEmpty()) {
            binding.etLocation.setError("Vui lòng nhập địa chỉ");
            isValid = false;
        }
        if (binding.etWebsite.getText().toString().trim().isEmpty()) {
            binding.etWebsite.setError("Vui lòng nhập website");
            isValid = false;
        }

        return isValid;
    }


    // Lưu dữ liệu vào Firebase
    private void saveDataToFirebase(Uri frontCCCDUri, Uri backCCCDUri, Uri companyCertUri, Uri logoUri) {
        if (frontCCCDUri != null && backCCCDUri != null && companyCertUri != null && logoUri != null) {
            String uniqueId = UUID.randomUUID().toString();

            uploadImage(logoUri, "logo_" + uniqueId, logo ->
                    uploadImage(frontCCCDUri, "front_cccd_" + uniqueId, frontUrl ->
                            uploadImage(backCCCDUri, "back_cccd_" + uniqueId, backUrl ->
                                    uploadImage(companyCertUri, "company_cert_" + uniqueId, certUrl -> {
                                        saveToFirestore(
                                                "logo_" + uniqueId,
                                                "front_cccd_" + uniqueId,
                                                "back_cccd_" + uniqueId,
                                                "company_cert_" + uniqueId
                                        );
                                    })
                            )
                    )
            );
        } else {
            Toast.makeText(this, "Vui lòng tải đủ tất cả các ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    // Lưu thông tin vào Firestore
        private void saveToFirestore(String logoFileName, String frontFileName, String backFileName, String certFileName) {
            UserSessionManager userSessionManager = new UserSessionManager();
            String uid = userSessionManager.getUserUid();

            // Tạo đối tượng thông tin công ty
            Map<String, Object> companyInfo = new HashMap<>();
            companyInfo.put("companyName", binding.etCompanyName.getText().toString().trim());
            companyInfo.put("contactPerson", binding.etRecruiterName.getText().toString().trim());
            companyInfo.put("companyPhone", binding.etPhoneNumber.getText().toString().trim());
            companyInfo.put("companyEmail", binding.etCompanyMail.getText().toString().trim());
            companyInfo.put("address", binding.etLocation.getText().toString().trim());
            companyInfo.put("website", binding.etWebsite.getText().toString().trim());
            companyInfo.put("logo", logoFileName);
            companyInfo.put("legalDocumentFront", frontFileName);
            companyInfo.put("legalDocumentBack", backFileName);
            companyInfo.put("certificationDocument", certFileName);
            companyInfo.put("statusId", "1");
            companyInfo.put("createdAt", FieldValue.serverTimestamp());
            companyInfo.put("updatedAt", FieldValue.serverTimestamp());

            FirebaseFirestore db = FirebaseFirestore.getInstance();


            db.collection("users").document(uid)
                    .collection("role").document("employer")
                    .set(companyInfo)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Lưu dữ liệu thành công", Toast.LENGTH_SHORT).show();
                        clearInputs(); // Xóa các input sau khi lưu thành công
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lưu dữ liệu thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

    // Tải ảnh lên Firebase Storage
    private void uploadImage(Uri uri, String path, OnSuccessListener<String> onSuccess) {
        StorageReference fileRef = storageReference.child("images/" + path);
        fileRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot ->
                        fileRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                            onSuccess.onSuccess(downloadUrl.toString());
                        })
                ).addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi khi tải lên: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            switch (requestCode) {
                case 100:
                    logoUri = selectedImageUri;
                    binding.tvImLogo.setText(selectedImageUri.toString());
                    break;
                case 101:
                    frontCCCDUri = selectedImageUri;
                    binding.tvFrontIDPath.setText(selectedImageUri.toString());
                    break;
                case 102:
                    backCCCDUri = selectedImageUri;
                    binding.tvBackIDPath.setText(selectedImageUri.toString());
                    break;
                case 103:
                    companyCertUri = selectedImageUri;
                    binding.tvBusinessLicensePath.setText(selectedImageUri.toString());
                    break;
            }
        }
    }

    // Xóa dữ liệu sau khi gửi thành công
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
        binding.progressBar.setVisibility(View.GONE);
       // binding.btnSubmit.setVisibility(View.VISIBLE);

    }
    // Kiểm tra xem số điện thoại có phải 10 chữ số không
    private boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.matches("^\\d{10}$");
    }

    // Kiểm tra định dạng email
    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void populateDefaultUserInfo() {
        UserSessionManager userSessionManager = new UserSessionManager();
        String name = userSessionManager.getName();
        String phoneNumber = userSessionManager.getPhoneNumber();
        String email = userSessionManager.getEmail();

        Log.d("UserInfo", "Name: " + name + ", Phone: " + phoneNumber + ", Email: " + email);

        binding.etRecruiterName.setText(name);
        binding.etPhoneNumber.setText(phoneNumber);
        binding.etCompanyMail.setText(email);
    }


    public void HienThiThongTin() {
        // Tạo session để lấy UID người dùng hiện tại.
        UserSessionManager sessionManager = new UserSessionManager();
        String uid = sessionManager.getUserUid();

        // Lấy tham chiếu đến tài liệu người dùng trong Firestore dựa trên UID.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uid);

        // Truy vấn thông tin người dùng và xử lý kết quả.
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) { // Kiểm tra nếu truy vấn thành công.
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) { // Kiểm tra nếu tài liệu tồn tại.
                        // Lấy thông tin người dùng từ tài liệu.
                        String name = document.getString("name");
                        String email = document.getString("email");
                        String phonenumber = document.getString("phoneNumber");

                        // Hiển thị thông tin người dùng trên giao diện.
                        binding.etRecruiterName.setText(name);
                        binding.etPhoneNumber.setText(phonenumber);
                        binding.etCompanyMail.setText(email);

                        // Ghi log thông tin người dùng để kiểm tra.
                        Log.d("PPPP", "onComplete: " + email + name);
                    } else {
                        // Thông báo nếu không tìm thấy dữ liệu người dùng.
                        Log.d("Firestore", "Không tìm thấy dữ liệu người dùng.");
                    }
                } else {
                    // Thông báo nếu có lỗi khi truy vấn Firestore.
                    Log.d("Firestore", "Lỗi khi truy vấn dữ liệu.", task.getException());
                }
            }
        });
    }
}
