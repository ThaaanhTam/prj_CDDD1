package com.example.hotrovieclam.Fragment.RecruiterManagement;

import android.app.Dialog;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Model.HieuUngThongBao;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentCCCDMatSauBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class CCCD_MatSau_Fragment extends DialogFragment {
private FragmentCCCDMatSauBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCCCDMatSauBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        loadCompanyInformation();
        return  view;


    }
    @Override
    public void onStart() {
        super.onStart();

        // Thay đổi kích thước của dialog
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                // Đặt chiều rộng và chiều cao cho dialog
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                // Bạn cũng có thể sử dụng chiều cao cố định hoặc tỷ lệ, ví dụ:
                // window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 600);
            }
        }
    }
    private void loadCompanyInformation() {
        HieuUngThongBao.startLoadingAnimation(binding.loadImage);
        binding.loadImage.setVisibility(View.VISIBLE);
        UserSessionManager user = new UserSessionManager();
        String uid = user.getUserUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                String anhcccdSau = "images/" + documentSnapshot.getString("legalDocumentBack");

                if (anhcccdSau != null) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference logoRef = storageRef.child(anhcccdSau);

                    // Tải ảnh logo từ Firebase Storage
                    logoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(this)
                                .load(uri.toString())
                                .into(binding.cccdMatSau);
                        Log.d("TAMM", "loadCompanyInformation: "+uri.toString());
                        //Toast.makeText(getContext(), "lay anh ok ma", Toast.LENGTH_SHORT).show();
                        binding.loadImage.setVisibility(View.GONE);
                    }).addOnFailureListener(e -> {
                        //Toast.makeText(getContext(), "Không thể tải ảnh logo", Toast.LENGTH_SHORT).show();
                        HieuUngThongBao.showErrorToast(requireContext(),"Không thể tải căn cước công dân mặt sau");

                        binding.loadImage.setVisibility(View.GONE);

                    });
                } else {
                    Glide.with(this)
                            .load("https://123job.vn/images/no_company.png")
                            .into(binding.cccdMatSau);
                }
            } else {
                Toast.makeText(getContext(), "Không tìm thấy dữ liệu công ty", Toast.LENGTH_SHORT).show();
            }
        });
    }
}