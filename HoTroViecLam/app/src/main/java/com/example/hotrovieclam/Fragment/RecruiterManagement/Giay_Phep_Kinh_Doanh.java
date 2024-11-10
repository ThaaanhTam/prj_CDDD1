package com.example.hotrovieclam.Fragment.RecruiterManagement;

import androidx.fragment.app.DialogFragment;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.databinding.FragmentGiayPhepKinhDoanhBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class Giay_Phep_Kinh_Doanh extends DialogFragment {
private FragmentGiayPhepKinhDoanhBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentGiayPhepKinhDoanhBinding.inflate(inflater,container,false);
        View view =binding.getRoot();
        loadCompanyInformation();
        return view;
    }
    private void loadCompanyInformation() {
        UserSessionManager user = new UserSessionManager();
        String uid = user.getUserUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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


                // Hiển thị link tài liệu giấy tờ
                String ankkd = "images/" + documentSnapshot.getString("certificationDocument");

                if (ankkd != null) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference logoRef = storageRef.child(ankkd);

                    // Tải ảnh logo từ Firebase Storage
                    logoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(this)
                                .load(uri.toString())
                                .into(binding.giayphepkd);
                        Log.d("TAMM", "loadCompanyInformation: "+uri.toString());
                        Toast.makeText(getContext(), "lay anh ok ma", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Không thể tải ảnh logo", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Glide.with(this)
                            .load("https://123job.vn/images/no_company.png")
                            .into(binding.giayphepkd);
                }
            } else {
                Toast.makeText(getContext(), "Không tìm thấy dữ liệu công ty", Toast.LENGTH_SHORT).show();
            }
        });
    }

}