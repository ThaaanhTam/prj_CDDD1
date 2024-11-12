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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDialogAvatarBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Khi bấm vào hình ảnh để chọn ảnh
        binding.previewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick: chọn ảnh");
                try {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    mGetContent.launch(intent);
                } catch (Exception e) {
                    Log.e("TAG", "Lỗi khi mở thư viện ảnh", e);
                    Toast.makeText(getContext(), "Không thể mở thư viện ảnh", Toast.LENGTH_SHORT).show();
                }
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
                    Log.d("TAG", "Chưa chọn ảnh");
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
                    try {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            selectedImageUri = result.getData().getData();
                            if (selectedImageUri != null) {
                                // Hiển thị ảnh lên ShapeableImageView và hiện nút Cập Nhật
                                Glide.with(DialogFragmentAvatar.this)
                                        .load(selectedImageUri)
                                        .into(binding.previewAvatar);
                                binding.btnCapNhat.setVisibility(View.VISIBLE); // Hiển thị nút Cập Nhật
                                Log.d("TAG", "Đã chọn ảnh: " + selectedImageUri);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("TAG", "Lỗi khi chọn ảnh", e);
                        Toast.makeText(getContext(), "Có lỗi xảy ra khi chọn ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
    }

    // Tải ảnh lên Firebase Storage
    private void uploadImageToStorage(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("avatars/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> updateAvatarInFirestore(uri)))
                .addOnFailureListener(e -> {
                    Log.e("TAG", "Lỗi khi tải ảnh lên", e);
                    binding.loadding.setVisibility(View.GONE);
                });
    }

    // Cập nhật URL avatar vào Firestore
    private void updateAvatarInFirestore(Uri newImageUri) {
        try {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(uid);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String oldAvatarUrl = documentSnapshot.getString("avatar");

                    userRef.update("avatar", newImageUri.toString())
                            .addOnSuccessListener(aVoid -> {
                                Log.d("TAG", "Cập nhật avatar thành công");
                                binding.loadding.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();

                                // Xóa ảnh cũ khỏi Firebase Storage
                                if (oldAvatarUrl != null) {
                                    FirebaseStorage.getInstance().getReferenceFromUrl(oldAvatarUrl)
                                            .delete()
                                            .addOnSuccessListener(aVoid1 -> Log.d("TAG", "Xóa avatar cũ thành công"))
                                            .addOnFailureListener(e -> Log.e("TAG", "Lỗi khi xóa avatar cũ", e));
                                }

                                dismiss(); // Đóng DialogFragment
                            })
                            .addOnFailureListener(e -> {
                                Log.e("TAG", "Lỗi khi cập nhật avatar", e);
                                binding.loadding.setVisibility(View.GONE);
                            });
                }
            });
        } catch (Exception e) {
            Log.e("TAG", "Lỗi khi cập nhật Firestore", e);
        }
    }

    // Hàm tải ảnh từ Firestore
    public void LoadAnh(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                try {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        String avatarUrl = document.getString("avatar");

                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Glide.with(getContext())
                                    .load(avatarUrl)
                                    .centerCrop()
                                    .into(binding.previewAvatar);
                        }
                    } else {
                        Log.d("Firestore", "Không tìm thấy dữ liệu người dùng.");
                    }
                } catch (Exception e) {
                    Log.e("Firestore", "Lỗi khi tải ảnh từ Firestore", e);
                }
            }
        });
    }
}
