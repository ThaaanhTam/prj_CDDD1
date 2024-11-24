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
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    static String anhcccdTruoc = null;

    public Information_management() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInformationManagementBinding.inflate(inflater, container, false);

        // Gọi hàm load thông tin công ty trong onCreateView
        loadCompanyInformation();

        binding.tvLegalDocumentFront.setOnClickListener(v -> {
            CCCD_MatTruoc_Fragment cccdMatTruocFragment = new CCCD_MatTruoc_Fragment();
            cccdMatTruocFragment.show(getParentFragmentManager(), "cccdMatTruocFragment");
        });

        binding.tvLegalDocumentBack.setOnClickListener(v -> {
            CCCD_MatSau_Fragment cccdMatSauFragment = new CCCD_MatSau_Fragment();
            cccdMatSauFragment.show(getParentFragmentManager(), "cccdMatSauFragment");
        });

        binding.tvCertificationDocument.setOnClickListener(v -> {
            Giay_Phep_Kinh_Doanh giayPhepKinhDoanh = new Giay_Phep_Kinh_Doanh();
            giayPhepKinhDoanh.show(getParentFragmentManager(), "giayPhepKinhDoanh");
        });

        return binding.getRoot();
    }

    private void loadCompanyInformation() {
        UserSessionManager userSessionManager = new UserSessionManager();
        String uid = userSessionManager.getUserUid();

        if (uid == null || uid.isEmpty()) {
            Log.e("Information_management", "UID không hợp lệ.");
            return;
        }

        DocumentReference docRef = db.collection("users").document(uid)
                .collection("role").document("employer");

        docRef.addSnapshotListener((documentSnapshot, error) -> {
            if (error != null) {
                Toast.makeText(getContext(), "Lỗi khi nghe thay đổi dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                binding.tvCompanyName.setText(documentSnapshot.getString("companyName"));
                binding.tvContactPerson.setText(documentSnapshot.getString("contactPerson"));
                binding.tvPhoneNumber.setText(documentSnapshot.getString("companyPhone"));
                binding.tvCompanyEmail.setText(documentSnapshot.getString("companyEmail"));
                binding.tvAddress.setText(documentSnapshot.getString("address"));
                binding.tvWebsite.setText(documentSnapshot.getString("website"));
                binding.tvStatus.setText(documentSnapshot.getString("statusId"));

                anhcccdTruoc = documentSnapshot.getString("legalDocumentFront");
                binding.tvLegalDocumentFront.setText(anhcccdTruoc);
                binding.tvLegalDocumentFront.setPaintFlags(binding.tvLegalDocumentFront.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                binding.tvLegalDocumentBack.setText(documentSnapshot.getString("legalDocumentBack"));
                binding.tvLegalDocumentBack.setPaintFlags(binding.tvLegalDocumentBack.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                binding.tvCertificationDocument.setText(documentSnapshot.getString("certificationDocument"));
                binding.tvCertificationDocument.setPaintFlags(binding.tvCertificationDocument.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                String logoPath = documentSnapshot.getString("logo") != null ? "images/" + documentSnapshot.getString("logo") : null;
                if (logoPath != null) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference logoRef = storage.getReference().child(logoPath);

                    logoRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(this).load(uri).into(binding.companyLogo));
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
