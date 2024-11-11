package com.example.hotrovieclam.Fragment.RecruiterManagement;

import android.graphics.Paint;
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


    FragmentInformationManagementBinding binding;
    Job_Management adapter;
    ArrayList<Job> jobs = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance(); // Khởi tạo Firestore instance
    static String anhcccdTruoc = null;

    public Information_management() {
        // Required empty public constructor
    }

    public static Information_management newInstance(String param1, String param2) {
        Information_management fragment = new Information_management();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadCompanyInformation();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInformationManagementBinding.inflate(inflater, container, false);

        // Gọi hàm load thông tin công ty
        loadCompanyInformation();
        binding.tvLegalDocumentFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CCCD_MatTruoc_Fragment cccd_matTruoc_fragment = new CCCD_MatTruoc_Fragment();
                cccd_matTruoc_fragment.show(getParentFragmentManager(), "cccdMatSauFragment");
            }
        });
        binding.tvLegalDocumentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CCCD_MatSau_Fragment cccdMatSauFragment = new CCCD_MatSau_Fragment();
                cccdMatSauFragment.show(getParentFragmentManager(), "cccdMatSauFragment");
            }
        });
        binding.tvCertificationDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Giay_Phep_Kinh_Doanh giayPhepKinhDoanh = new Giay_Phep_Kinh_Doanh();
                giayPhepKinhDoanh.show(getParentFragmentManager(),"giayPhepKinhDoanh");
            }
        });
        return binding.getRoot();
    }

    private void loadCompanyInformation() {
        // Lấy UID của người dùng hiện tại
        UserSessionManager userSessionManager = new UserSessionManager();
        String uid = userSessionManager.getUserUid();

        // Tham chiếu đến document "employer" của user
        DocumentReference docRef = db.collection("users").document(uid)
                .collection("roles").document("employer");

        // Thêm snapshot listener để lắng nghe thay đổi
        docRef.addSnapshotListener((documentSnapshot, error) -> {
            if (error != null) {
                Toast.makeText(getContext(), "Lỗi khi nghe thay đổi dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                // Cập nhật dữ liệu vào TextView khi có thay đổi
                binding.tvCompanyName.setText(documentSnapshot.getString("companyName"));
                binding.tvContactPerson.setText(documentSnapshot.getString("contactPerson"));
                binding.tvPhoneNumber.setText(documentSnapshot.getString("companyPhone"));
                binding.tvCompanyEmail.setText(documentSnapshot.getString("companyEmail"));
                binding.tvAddress.setText(documentSnapshot.getString("address"));
                binding.tvWebsite.setText(documentSnapshot.getString("website"));
                binding.tvStatus.setText(documentSnapshot.getString("statusId"));

                // Hiển thị link tài liệu giấy tờ
                anhcccdTruoc = documentSnapshot.getString("legalDocumentFront");
                binding.tvLegalDocumentFront.setText(anhcccdTruoc);
                binding.tvLegalDocumentFront.setPaintFlags(binding.tvLegalDocumentFront.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                binding.tvLegalDocumentBack.setText(documentSnapshot.getString("legalDocumentBack"));
                binding.tvLegalDocumentBack.setPaintFlags(binding.tvLegalDocumentFront.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                binding.tvCertificationDocument.setText(documentSnapshot.getString("certificationDocument"));
                binding.tvCertificationDocument.setPaintFlags(binding.tvLegalDocumentFront.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                // Tải và hiển thị logo công ty
                String logoPath = "images/" + documentSnapshot.getString("logo");

                if (logoPath != null) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference logoRef = storageRef.child(logoPath);

                    // Tải ảnh logo từ Firebase Storage
                    logoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(this)
                                .load(uri.toString())
                                .into(binding.companyLogo);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Không thể tải ảnh logo", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Glide.with(this)
                            .load("https://123job.vn/images/no_company.png")
                            .into(binding.companyLogo);
                }
            } else {
                Toast.makeText(getContext(), "Không tìm thấy dữ liệu công ty", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
