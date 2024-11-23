package com.example.hotrovieclam.Fragment.Child_Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Activity.Navigation;
import com.example.hotrovieclam.Model.HieuUngThongBao;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DialogFragmentAvatar extends DialogFragment {
    private FragmentDialogAvatarBinding binding;
    private UserSessionManager userSessionManager = new UserSessionManager();
    private String uid;
    private Uri selectedImageUri;
    private Uri cameraImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDialogAvatarBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Lấy UID người dùng
        uid = userSessionManager.getUserUid();
        if (uid != null) {
            loadAvatar(uid);
        }

        // Xử lý khi bấm vào hình ảnh
        binding.previewAvatar.setOnClickListener(v -> showImageSourceDialog());

        // Xử lý khi bấm nút Cập Nhật
        binding.btnCapNhat.setOnClickListener(v -> {
            HieuUngThongBao.startLoadingAnimation(binding.loadding);
            binding.loadding.setVisibility(View.VISIBLE);
            binding.btnCapNhat.setVisibility(View.GONE);

            if (selectedImageUri != null) {
                uploadImageToStorage(selectedImageUri);
            } else {
                binding.loadding.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Vui lòng chọn ảnh trước khi cập nhật", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showImageSourceDialog() {
        CharSequence[] options = {"Thư viện", "Camera", "Xem ảnh hiện tại"};
        new AlertDialog.Builder(getContext()).setTitle("Chọn ảnh từ").setItems(options, (dialog, which) -> {
            if (which == 0) { // Thư viện
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                mGetContent.launch(intent);
            } else if (which == 1) { // Camera
                openCamera();
            } else if (which == 2) { // Xem ảnh hiện tại
                showCurrentAvatar();
            }
        }).show();
    }

    private void showCurrentAvatar() {
        if (binding.previewAvatar.getDrawable() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Ảnh hiện tại");

            ImageView imageView = new ImageView(getContext());

// Chuyển đổi giá trị dp sang pixel
            int widthInDp = 200; // Giá trị chiều rộng bạn muốn (ví dụ: 200dp)
            int heightInDp = 150; // Giá trị chiều cao bạn muốn (ví dụ: 150dp)

// Sử dụng công thức chuyển đổi dp sang px
            int widthInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp, getResources().getDisplayMetrics());
            int heightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());

// Đặt LayoutParams với giá trị px đã tính
            imageView.setLayoutParams(new ViewGroup.LayoutParams(widthInPx, heightInPx));
            imageView.setAdjustViewBounds(true);

            Glide.with(this).load(selectedImageUri != null ? selectedImageUri : binding.previewAvatar.getDrawable()).into(imageView);

            builder.setView(imageView);
            builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());
            builder.show();
        } else {
            HieuUngThongBao.showErrorToast(requireContext(),"Không có ảnh để xem");
            //Toast.makeText(getContext(), "Không có ảnh để xem", Toast.LENGTH_SHORT).show();
        }
    }

    private ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            selectedImageUri = result.getData().getData();
            Glide.with(this).load(selectedImageUri).into(binding.previewAvatar);
            binding.btnCapNhat.setVisibility(View.VISIBLE);
        }
    });

    private ActivityResultLauncher<Intent> mTakePhoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && cameraImageUri != null) {
            selectedImageUri = cameraImageUri;
            Glide.with(this).load(selectedImageUri).into(binding.previewAvatar);
            binding.btnCapNhat.setVisibility(View.VISIBLE);
        }
    });

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            try {
                File photoFile = createImageFile();
                cameraImageUri = FileProvider.getUriForFile(getContext(), "com.example.hotrovieclam.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                mTakePhoto.launch(intent);
            } catch (IOException ex) {
                Log.e("TAG", "Lỗi khi tạo file ảnh", ex);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void uploadImageToStorage(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("avatars/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(this::updateAvatarInFirestore)).addOnFailureListener(e -> {
            Log.e("TAG", "Lỗi khi tải ảnh lên", e);
            binding.loadding.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Không thể tải ảnh lên", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateAvatarInFirestore(Uri newImageUri) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String oldAvatarUrl = documentSnapshot.getString("avatar");

                userRef.update("avatar", newImageUri.toString()).addOnSuccessListener(aVoid -> {
                    binding.loadding.setVisibility(View.GONE);
                    //Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();

                    if (oldAvatarUrl != null) {
                        FirebaseStorage.getInstance().getReferenceFromUrl(oldAvatarUrl).delete();
                    }

                    dismiss();
                }).addOnFailureListener(e -> {
                    Log.e("TAG", "Lỗi khi cập nhật avatar", e);
                    binding.loadding.setVisibility(View.GONE);
                });
            }
        });
    }

    private void loadAvatar(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uid);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String avatarUrl = task.getResult().getString("avatar");

                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Glide.with(this).load(avatarUrl).centerCrop().into(binding.previewAvatar);
                }
            }
        });
    }
}
