package com.example.hotrovieclam.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Model.Experience;
import com.example.hotrovieclam.Model.KiNang;
import com.example.hotrovieclam.Model.TruongHoc;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityCvTemplateBinding;
import com.example.hotrovieclam.databinding.ActivityJobDetailMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class cv_template extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ActivityCvTemplateBinding binding;
    ArrayList<TruongHoc> truonghoc = new ArrayList<>();
    ArrayList<Experience> experiences = new ArrayList<>();
    ArrayList<KiNang>kiNangs= new ArrayList<>();
    ArrayAdapter<TruongHoc> truongHocAdapter;
    ArrayAdapter<Experience> kinhnghiemAdater;
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
        kinhnghiemAdater = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, experiences);
        binding.kinhnghiem.setAdapter(kinhnghiemAdater);

        kiNangArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,kiNangs);
        binding.kinang.setAdapter(kiNangArrayAdapter);

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
                    String avatarUrl = documentSnapshot.getString("avatar");
                    String ns = documentSnapshot.getString("birthday");
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
                    binding.tvGioiTinh.setText("......");
                    binding.tvGioiTinh.setTextColor(getResources().getColor(R.color.navigation));
                    binding.tvNgaySinh.setText(".......");
                    binding.tvNgaySinh.setTextColor(getResources().getColor(R.color.navigation));
                }
                DocumentReference doc = db.collection("users").document(userId)
                        .collection("role").document("candidate")
                        .collection("introduction").document("introductdata");

                doc.addSnapshotListener((documentSnapshota, e) -> {
                    if (e != null) {
                        // Xử lý lỗi nếu có
                        Log.e("Firestore", "Error listening to document", e);
                        return;
                    }

                    if (documentSnapshota != null && documentSnapshota.exists()) {
                        // Kiểm tra xem trường "introduction" đã có data hay chưa
                        String introduction = documentSnapshota.getString("introduction");
                        if (introduction != null && !introduction.isEmpty()) {
                            binding.gioithieu.setText(introduction);
                            // Nếu có data thì ẩn nút thêm thông tin giới thiệu bản thân và hiện nút và ngược lại
                            Log.d("Firestore", "Đã có data: " + introduction);
                        } else {
                            binding.gioithieu.setText("Chưa cập nhật");
                            binding.gioithieu.setTextColor(getResources().getColor(R.color.chuacapnhat));
                        }
                    } else {
                        Log.d("Firestore", "Không có document với UID này trong bảng Introduces");
                        binding.gioithieu.setText("Chưa cập nhật");
                        binding.gioithieu.setTextColor(getResources().getColor(R.color.chuacapnhat));
                    }
                });
// Lắng nghe thay đổi cho Collection "school"
                CollectionReference docSchool = db.collection("users")
                        .document(userId)
                        .collection("role")
                        .document("candidate")
                        .collection("school");

                docSchool.addSnapshotListener((value, serror) -> {
                    if (serror != null) {
                        Log.d("Firestore", "Error listening for school data changes: ", serror);
                        return;
                    }

                    truonghoc.clear(); // Xóa dữ liệu cũ
                    if (value != null && value.isEmpty()) {
                        truonghoc.add(new TruongHoc(null, null, "Chưa cập nhật trường học", "", "", "", null, null));
                    } else if (value != null) {
                        for (DocumentSnapshot document : value) {
                            String nameSchool = document.getString("nameSchool");
                            String nganhHoc = document.getString("nganhHoc");
                            String timeStart = document.getString("timeStart");
                            String timeEnd = document.getString("timeEnd");

                            TruongHoc truongHoc = new TruongHoc(null, null, nameSchool, nganhHoc, timeStart, timeEnd, null, null);
                            truonghoc.add(truongHoc); // Thêm vào danh sách
                        }
                    }
                    truongHocAdapter.notifyDataSetChanged(); // Cập nhật adapter
                });

// Lắng nghe thay đổi cho Collection "experience"
                CollectionReference getKinhNghiem = db.collection("users")
                        .document(userId)
                        .collection("role")
                        .document("candidate")
                        .collection("experience");

                getKinhNghiem.addSnapshotListener((value, aerror) -> {
                    if (aerror != null) {
                        Log.d("Firestore", "Error listening for experience data changes: ", aerror);
                        return;
                    }

                    experiences.clear(); // Xóa dữ liệu cũ
                    if (value != null && value.isEmpty()) {
                        experiences.add(new Experience(null, "", "", "", "Chưa cập nhật kinh nghiệm", "", null));
                    } else if (value != null) {
                        for (DocumentSnapshot document : value) {
                            String name_organization = document.getString("name_organization");
                            String position = document.getString("position");
                            String timeStart = document.getString("time_start");
                            String timeEnd = document.getString("time_end");

                            Experience experie = new Experience(null, timeEnd, timeStart, position, name_organization, null, null);
                            experiences.add(experie); // Thêm vào danh sách
                        }
                    }
                    kinhnghiemAdater.notifyDataSetChanged(); // Cập nhật adapter
                });

// Lắng nghe thay đổi cho Collection "skills"
                CollectionReference getSkill = db.collection("users")
                        .document(userId)
                        .collection("role")
                        .document("candidate")
                        .collection("skills");

                getSkill.addSnapshotListener((value, berror) -> {
                    if (berror != null) {
                        Log.d("Firestore", "Error listening for skill data changes: ", berror);
                        return;
                    }

                    kiNangs.clear(); // Xóa dữ liệu cũ
                    if (value != null && value.isEmpty()) {
                        kiNangs.add(new KiNang(null, null, "Chưa cập nhật kĩ năng", null));
                    } else if (value != null) {
                        for (DocumentSnapshot document : value) {
                            String name = document.getString("name");
                            KiNang kiNang = new KiNang(null, null, name, null);
                            kiNangs.add(kiNang); // Thêm vào danh sách
                        }
                    }
                    kiNangArrayAdapter.notifyDataSetChanged(); // Cập nhật adapter
                });


            });
        } else {
            Log.d("UserInfo", "Người dùng chưa đăng nhập.");
        }


    }
}