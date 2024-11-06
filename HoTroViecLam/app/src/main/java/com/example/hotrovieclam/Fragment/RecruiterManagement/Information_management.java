package com.example.hotrovieclam.Fragment.RecruiterManagement;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.databinding.FragmentInformationManagementBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Information_management extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    FragmentInformationManagementBinding binding;
    Job_Management adapter;
    ArrayList<Job> jobs = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance(); // Khởi tạo Firestore instance

    public Information_management() {
        // Required empty public constructor
    }

    public static Information_management newInstance(String param1, String param2) {
        Information_management fragment = new Information_management();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInformationManagementBinding.inflate(inflater, container, false);

        // Gọi hàm load thông tin công ty
        loadCompanyInformation();

        return binding.getRoot();
    }

    private void loadCompanyInformation() {
        // Lấy UID của người dùng hiện tại
        UserSessionManager userSessionManager = new UserSessionManager();
        String uid = userSessionManager.getUserUid();

        // Khởi tạo Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tham chiếu đến document "employer" của user
        DocumentReference docRef = db.collection("users").document(uid)
                .collection("roles").document("employer");

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Đọc và gán dữ liệu vào các TextView
                binding.tvCompanyName.setText(documentSnapshot.getString("companyName"));
                binding.tvContactPerson.setText(documentSnapshot.getString("contactPerson"));
                binding.tvPhoneNumber.setText(documentSnapshot.getString("companyPhone"));
                binding.tvCompanyEmail.setText(documentSnapshot.getString("companyEmail"));
                binding.tvAddress.setText(documentSnapshot.getString("address"));
                binding.tvWebsite.setText(documentSnapshot.getString("website"));
                binding.tvStatus.setText(documentSnapshot.getString("statusId"));

                // Hiển thị link tài liệu giấy tờ
                binding.tvLegalDocumentFront.setText(documentSnapshot.getString("legalDocumentFront"));
                binding.tvLegalDocumentBack.setText(documentSnapshot.getString("legalDocumentBack"));
                binding.tvCertificationDocument.setText(documentSnapshot.getString("certificationDocument"));

                // Lấy tên hoặc đường dẫn của logo từ Firestore
                String logoPath = "images/"+documentSnapshot.getString("logo");

                if (logoPath != null) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    // Tham chiếu đến ảnh trong Firebase Storage
                    StorageReference logoRef = storageRef.child(logoPath);

                    // Lấy URL của ảnh
                    logoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Tải ảnh lên ImageView với Glide
                        Glide.with(this)
                                .load(uri.toString())
                                .into(binding.companyLogo);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Không thể tải ảnh logo", Toast.LENGTH_SHORT).show();
                    });
                } else {gs://findwork-c1286.appspot.com/images/back_cccd_0a2da2d0-c748-438d-a5ea-2bdc4fb737f4
                    // Nếu không có ảnh logo, hiển thị ảnh mặc định

                    Glide.with(this)
                            .load("https://123job.vn/images/no_company.png")
                            .into(binding.companyLogo);
                }
            } else {
                Toast.makeText(getContext(), "Không tìm thấy dữ liệu công ty", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Lỗi khi đọc dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }






}
