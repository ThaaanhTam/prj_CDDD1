package com.example.hotrovieclam.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Model.Experiences;
import com.example.hotrovieclam.Model.KiNang;
import com.example.hotrovieclam.Model.TruongHoc;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityCvTemplateBinding;
import com.example.hotrovieclam.databinding.ActivityJobDetailMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class cv_template extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ActivityCvTemplateBinding binding;
    ArrayList<TruongHoc> truonghoc = new ArrayList<>();
    ArrayList<Experiences> experiences = new ArrayList<>();
    ArrayList<KiNang>kiNangs= new ArrayList<>();
    ArrayAdapter<TruongHoc> truongHocAdapter;
    ArrayAdapter<Experiences> kinhnghiemAdater;
    ArrayAdapter<KiNang>kiNangArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCvTemplateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Khởi tạo FirebaseAuth và Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        truongHocAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, truonghoc);
        binding.lisviewHocVan.setAdapter(truongHocAdapter);

        // Gọi hàm để lấy thông tin người dùng
        getUserInfo();
    }

    private void getUserInfo() {
        // Kiểm tra người dùng đã đăng nhập chưa
        if (mAuth.getCurrentUser() != null) {
            // Lấy UID của người dùng hiện tại
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference userRef = db.collection("users").document(userId);

            userRef.addSnapshotListener((documentSnapshot, error) -> {
                if (error != null) {
                    Log.d("UserInfo", "Lỗi khi nghe thay đổi: ", error);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Lấy thông tin từ document
                    String email = documentSnapshot.getString("email"); // Trường email
                    String name = documentSnapshot.getString("name"); // Trường name
                    String sdt = documentSnapshot.getString("phoneNumber"); // Trường số điện thoại
                    String avatarUrl = documentSnapshot.getString("avatar"); //
                    String ns = documentSnapshot.getString("birthday"); // Lấy ngày sinh
                    Long genderLong = documentSnapshot.getLong("gioitinh"); // Lấy giới tính (kiểu Long)

                    // Hiển thị thông tin lên UI
                    binding.tvEmail.setText(email);
                    binding.tvSdt.setText(sdt);
                    binding.tvName.setText(name);

                    // Kiểm tra ngày sinh có null không
                    if (ns != null && !ns.isEmpty()) {
                        binding.tvNgaySinh.setText(ns);
                        binding.tvNgaySinh.setTextColor(getResources().getColor(R.color.black)); // Màu sắc khi có ngày sinh
                    } else {
                        binding.tvNgaySinh.setText("Chưa cập nhật");
                        binding.tvNgaySinh.setTextColor(getResources().getColor(R.color.chuacapnhat)); // Màu sắc khi ngày sinh chưa cập nhật
                    }

                    Glide.with(this)
                            .load(avatarUrl)
                            .centerCrop()
                            .into(binding.ivAvatar);

                    // Kiểm tra giới tính
                    if (genderLong != null) {  // Kiểm tra nếu giá trị giới tính không null
                        int gt = genderLong.intValue(); // Chuyển đổi Long sang int
                        db.collection("genders").document(String.valueOf(gt))
                                .get()
                                .addOnCompleteListener(genderTask -> {
                                    if (genderTask.isSuccessful()) {
                                        DocumentSnapshot genderDocument = genderTask.getResult();
                                        if (genderDocument.exists()) {
                                            String genderName = genderDocument.getString("name");
                                            binding.tvGioiTinh.setText(genderName);
                                            binding.tvGioiTinh.setTextColor(getResources().getColor(R.color.black)); // Hiển thị tên giới tính
                                        } else {
                                            Log.d("Firestore", "Không tìm thấy giới tính cho ID: " + gt);
                                        }
                                    } else {
                                        Log.e("Firestore", "Lỗi khi truy vấn bảng genders.", genderTask.getException());
                                    }
                                });
                    } else {
                        // Nếu giới tính là null, hiển thị thông báo "Chưa cập nhật"
                        binding.tvGioiTinh.setText("Chưa cập nhật");
                        binding.tvGioiTinh.setTextColor(getResources().getColor(R.color.chuacapnhat));
                    }
                } else {
                    // Nếu tài liệu không tồn tại
                    binding.tvGioiTinh.setText("Chưa cập nhật");
                    binding.tvGioiTinh.setTextColor(getResources().getColor(R.color.chuacapnhat));
                    binding.tvNgaySinh.setText("Chưa cập nhật");
                    binding.tvNgaySinh.setTextColor(getResources().getColor(R.color.chuacapnhat));
                }

            });
        } else {
            Log.d("UserInfo", "Người dùng chưa đăng nhập.");
        }
        
    }
}