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
        UserSessionManager user = new UserSessionManager();
        String uid = user.getUserUid();
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

                // Lấy đường dẫn ảnh từ Firestore
                String anhcccdTruoc = "images/" + documentSnapshot.getString("legalDocumentFront");

                if (anhcccdTruoc != null) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference logoRef = storageRef.child(anhcccdTruoc);

                    // Tải ảnh logo từ Firebase Storage
                    logoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Kiểm tra và in URL tải ảnh ra để kiểm tra
                        String imageUrl = uri.toString();
                        //Log.d("Firebase Image", "URL tải ảnh: " + imageUrl);

                        // Sử dụng Glide để tải ảnh
                        Glide.with(this)
                                .load(imageUrl)
                                .diskCacheStrategy(DiskCacheStrategy.NONE) // Không sử dụng cache
                                .skipMemoryCache(true) // Bỏ qua bộ nhớ đệm
                                .into(binding.cccdMatTruoc);
                        Toast.makeText(getContext(), "Ảnh tải thành công", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        // Lỗi khi tải ảnh
                        Toast.makeText(getContext(), "Không thể tải ảnh từ Firebase Storage", Toast.LENGTH_SHORT).show();
                        //Log.e("Firebase Image", "Lỗi tải ảnh: " + e.getMessage());
                    });
                } else {
                    // Nếu không có ảnh, sử dụng ảnh mặc định
                    Glide.with(this)
                            .load("https://123job.vn/images/no_company.png")
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(binding.cccdMatTruoc);
                    Toast.makeText(getContext(), "Không có ảnh hợp lệ, dùng ảnh mặc định", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Không tìm thấy dữ liệu công ty", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
