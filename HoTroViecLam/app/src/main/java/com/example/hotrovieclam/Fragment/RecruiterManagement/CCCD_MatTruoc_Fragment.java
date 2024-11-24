package com.example.hotrovieclam.Fragment.RecruiterManagement;

import androidx.fragment.app.DialogFragment;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Model.HieuUngThongBao;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentCCCDMatTruocBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class CCCD_MatTruoc_Fragment extends DialogFragment {
    private FragmentCCCDMatTruocBinding binding;

    FirebaseFirestore db = FirebaseFirestore.getInstance(); // Khởi tạo Firestore instance



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCCCDMatTruocBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        loadCompanyInformation();
        return view;
    }
    private void loadCompanyInformation() {
        HieuUngThongBao.startLoadingAnimation(binding.loadImage);
        binding.loadImage.setVisibility(View.VISIBLE);
        UserSessionManager user = new UserSessionManager();
        String uid = user.getUserUid();
        // Tham chiếu đến document "employer" của user
        DocumentReference docRef = db.collection("users").document(uid)
                .collection("role").document("employer");

        // Thêm snapshot listener để lắng nghe thay đổi
        docRef.addSnapshotListener((documentSnapshot, error) -> {
            if (error != null) {
                Toast.makeText(getContext(), "Lỗi khi nghe thay đổi dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                binding.loadImage.setVisibility(View.GONE);

                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                // Cập nhật dữ liệu vào TextView khi có thay đổi


                // Hiển thị link tài liệu giấy tờ
                String anhcccdTruoc = "images/" + documentSnapshot.getString("legalDocumentFront");

                if (anhcccdTruoc != null) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference logoRef = storageRef.child(anhcccdTruoc);

                    // Tải ảnh logo từ Firebase Storage
                    logoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(this)
                                .load(uri.toString())
                                .into(binding.cccdMatTruoc);
                        binding.loadImage.setVisibility(View.GONE);
                        //Toast.makeText(getContext(), "lay anh ok ma", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        HieuUngThongBao.showErrorToast(requireContext(),"Không thể tải căn cước công dân mặt trước");
                        binding.loadImage.setVisibility(View.GONE);
                        //Toast.makeText(getContext(), "Không thể tải ảnh logo", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Glide.with(this)
                            .load("https://123job.vn/images/no_company.png")
                            .into(binding.cccdMatTruoc);
                }
            } else {
                Toast.makeText(getContext(), "Không tìm thấy dữ liệu công ty", Toast.LENGTH_SHORT).show();
                binding.loadImage.setVisibility(View.GONE);

            }
        });
    }
}