package com.example.hotrovieclam.Fragment.Child_Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Activity.Navigation;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentDialogAvatarBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class DialogFragmentAvatar extends DialogFragment {
    private FragmentDialogAvatarBinding binding;
    UserSessionManager userSessionManager = new UserSessionManager();
    String uid = null;
    private Uri selectedImageUri;  // Lưu Uri ảnh đã chọn


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDialogAvatarBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Khi bấm vào hình ảnh để chọn ảnh
        binding.previewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick: ggggggg");
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                mGetContent.launch(intent);
            }
        });

        // Khi bấm nút Cập Nhật
        binding.btnCapNhat.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                binding.loadding.setVisibility(View.VISIBLE);
                binding.btnCapNhat.setVisibility(View.GONE);

                // Kiểm tra nếu selectedImageUri không phải là null thì tiến hành upload
                if (selectedImageUri != null) {
                    uploadImageToStorage(selectedImageUri);

                } else {
                    binding.loadding.setVisibility(View.GONE);
                    Log.d("TAG", "No image selected");
                }
            }
        });
        uid = userSessionManager.getUserUid();
        if (uid != null) {
            LoadAnh(uid);
        }
        return view;
    }

    // Khai báo ActivityResultLauncher để chọn ảnh
    private ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // Cập nhật biến toàn cục selectedImageUri
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            // Hiển thị ảnh lên ShapeableImageView
                            Glide.with(DialogFragmentAvatar.this)
                                    .load(selectedImageUri)
                                    .into(binding.previewAvatar);
                            Log.d("TAG", "Selected Image URI: " + selectedImageUri);
                        }
                    }
                }
            });

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

    // Tải ảnh lên Firebase Storage
    private void uploadImageToStorage(Uri imageUri) {
        // Tạo reference của Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("avatars/" + System.currentTimeMillis() + ".jpg");

        // Upload ảnh
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Khi upload thành công, lấy URL của ảnh
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Cập nhật URL vào Firestore
                        updateAvatarInFirestore(uri);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("TAG", "Upload failed", e);
                    binding.loadding.setVisibility(View.GONE); // Ẩn loading nếu tải ảnh thất bại
                });
    }

    // Cập nhật URL avatar vào Firestore
// Cập nhật URL avatar vào Firestore
    private void updateAvatarInFirestore(Uri newImageUri) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        // Lấy URL ảnh cũ từ Firestore
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String oldAvatarUrl = documentSnapshot.getString("avatar");

                // Cập nhật URL avatar trong Firestore
                userRef.update("avatar", newImageUri.toString())
                        .addOnSuccessListener(aVoid -> {
                            Log.d("TAG", "Avatar updated successfully");
                            binding.loadding.setVisibility(View.GONE);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("update", true);
                            getParentFragmentManager().setFragmentResult("updateSuccess", bundle);
                            getParentFragmentManager().popBackStack();
                            Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            // Xóa ảnh cũ khỏi Firebase Storage
                            if (oldAvatarUrl != null) {
                                FirebaseStorage.getInstance().getReferenceFromUrl(oldAvatarUrl)
                                        .delete()
                                        .addOnSuccessListener(aVoid1 -> Log.d("TAG", "Old avatar deleted successfully"))
                                        .addOnFailureListener(e -> Log.e("TAG", "Failed to delete old avatar", e));
                            }

                            dismiss(); // Đóng DialogFragment
                        })
                        .addOnFailureListener(e -> {
                            Log.e("TAG", "Failed to update avatar", e);
                            binding.loadding.setVisibility(View.GONE);
                        });
            }
        });
    }

    public void LoadAnh(String uid) {


        // Dùng UID để truy vấn Firestore hoặc hiển thị thông tin người dùng
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {

                        String avatarUrl = document.getString("avatar");
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            // Dùng Glide để tải ảnh từ URL và hiển thị vào ImageView
                            Glide.with(getContext())
                                    .load(avatarUrl)
                                    .centerCrop()
                                    .into(binding.previewAvatar); // imageView là ImageView của bạn
                            Log.d("ii", "onComplete: lay dc anh vs uid" + uid);
                        }

                    } else {
                        Log.d("Firestore", "Không tìm thấy dữ liệu người dùng.");
                    }
                } else {
                    Log.d("Firestore", "Lỗi khi truy vấn dữ liệu.", task.getException());
                }
            }
        });

    }
}
