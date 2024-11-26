package com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment;

import static android.app.Activity.RESULT_OK;

//import static com.example.hotrovieclam.Activity.MainActivity.PICK_IMAGE_REQUEST;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.hotrovieclam.Model.KiNang;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentSkillsAndCertificationBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SkillsAndCertificationFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private FragmentSkillsAndCertificationBinding binding;
    List<KiNang> kiNangs = new ArrayList<>();
    String id = null;
    String id_skill;
    private KiNang kiNang;
    static String imagePath = "";

    static String imageUrl ="";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            binding = FragmentSkillsAndCertificationBinding.inflate(inflater, container, false);
            View view = binding.getRoot();
            Bundle bundle = getArguments();



            if (bundle != null) {
                id = bundle.getString("USER_ID");
                id_skill = bundle.getString("ID_SKILL");
            }

            if (id_skill != null) {
                loaddata(id_skill);
            }
            binding.ImLogo.setOnClickListener(v -> pickImage());
            binding.back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                            getParentFragmentManager().popBackStack();
                            BottomNavigationView bottomNav = getActivity().findViewById(R.id.nav_buttom);
                            if (bottomNav != null) {
                                bottomNav.setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("BackNavigation", "Lỗi khi quay lại màn hình trước đó", e);
                    }
                }
            });

            //binding.btnUpdateSkill.setEnabled(false);

            binding.btnUpdateSkill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Ẩn nút khi bắt đầu xử lý
                    binding.btnUpdateSkill.setVisibility(View.GONE);
                    String nameSkill = binding.nameskill.getText().toString().trim();
                    String description = binding.mota.getText().toString().trim();

                    // Kiểm tra lỗi nhập liệu
                    if (nameSkill.isEmpty() || description.isEmpty()) {
                        if (nameSkill.isEmpty()) {
                            binding.nameskill.setError("Vui lòng nhập tên kỹ năng");
                        }
                        if (description.isEmpty()) {
                            binding.mota.setError("Vui lòng nhập mô tả");
                        }
                        binding.btnUpdateSkill.setVisibility(View.VISIBLE);
                        return;
                    }


                    binding.loading.setVisibility(View.VISIBLE);
                    kiNang = new KiNang(id_skill, id, binding.nameskill.getText().toString(),binding.mota.getText().toString(),imageUrl);
                    if (id_skill != null) {
                        update(id_skill);
                    } else {
                        saveSkill(id);
                    }
                }
            });



            return view;

        } catch (Exception e) {
            Log.e("onCreateView", "Lỗi khi tạo View", e);
            Toast.makeText(getContext(), "Đã xảy ra lỗi khi tạo giao diện", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData(); // Lấy URI của hình ảnh đã chọn

            // In ra URI để kiểm tra
            Log.d("Image URI", imageUri.toString());

            // Bạn có thể chuyển URI thành đường dẫn tệp hoặc sử dụng URI trực tiếp
             imagePath = getRealPathFromURI(imageUri);  // Nếu cần đường dẫn tệp
            Log.d("Image Path", imagePath);
            binding.tvImLogo.setText(imagePath);

            uploadImageToFirebase(imageUri);
        }
    }
    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri == null) {
            Log.e("Firebase", "Không có ảnh để tải lên");
            return;
        }

        // Khởi tạo FirebaseStorage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Tạo tên ảnh duy nhất sử dụng timestamp
        String fileName = "images/" + System.currentTimeMillis() + ".jpg";

        // Lưu ảnh vào Firebase Storage
        StorageReference imageRef = storage.getReference().child(fileName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Lấy URL của ảnh vừa tải lên
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                         imageUrl = uri.toString();
                        Log.d("cc", "Ảnh đã tải lên thành công: " + imageUrl);
                        saveImageUrlToFirestore(imageUrl);
                        Log.d("Firebase", "Ảnh đã tải lên thành công: " + imageUrl);
                    }).addOnFailureListener(e -> {
                        Log.e("Firebase", "Lỗi khi lấy URL ảnh", e);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Lỗi khi tải ảnh lên Firebase", e);
                });
    }


    private void saveImageUrlToFirestore(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            Log.e("Firestore", "URL ảnh không hợp lệ.");
            return;
        }
        if (id_skill == null || id_skill.isEmpty()) {
            Log.e("Firestore", "ID kỹ năng không hợp lệ");
            return;  // Exit the method if the ID is invalid
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserSessionManager sessionManager = new UserSessionManager();
        String currentUserId = sessionManager.getUserUid();

        if (currentUserId != null && !currentUserId.isEmpty()) {
            if (id_skill != null && !id_skill.isEmpty()) {
                db.collection("users")
                        .document(currentUserId)
                        .collection("role")
                        .document("candidate")
                        .collection("skills")
                        .document(id_skill)  // Ensure this ID is valid
                        .update("imageUrl", imageUrl)  // Only update the imageUrl field
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firestore", "URL ảnh đã được lưu thành công.");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firestore", "Lỗi khi lưu URL ảnh vào Firestore", e);
                        });
            } else {
                Log.e("Firestore", "ID kỹ năng không hợp lệ");
            }
        } else {
            Log.e("Firestore", "ID người dùng không hợp lệ");
        }
    }




    private String getRealPathFromURI(Uri uri) {
        // Đảm bảo sử dụng ContentResolver để lấy đường dẫn file
        String filePath = null;

        // Kiểm tra loại URI
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                if (columnIndex != -1) {
                    cursor.moveToFirst();
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
            }
        } else if (uri.getScheme().equals("file")) {
            // Nếu URI là kiểu file (trong trường hợp sử dụng đường dẫn file trực tiếp)
            filePath = uri.getPath();
        }

        // Trả về đường dẫn file nếu có
        return filePath;
    }





    // Phương thức mở thư viện ảnh và chọn ảnh
    private void pickImage() {
        // Tạo một Intent để chọn hình ảnh từ bộ sưu tập
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // Chỉ cho phép chọn hình ảnh
        startActivityForResult(intent, PICK_IMAGE_REQUEST); // Khởi chạy Intent
    }

    public void saveSkill(String uid) {
        try {

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(uid)
                    .collection("role").document("candidate")
                    .collection("skills")
                    .add(kiNang)
                    .addOnSuccessListener(documentReference -> {
                        try {
                            kiNang.setId(documentReference.getId());

                            documentReference.set(kiNang)
                                    .addOnSuccessListener(aVoid -> {
                                        try {
                                            // Kiểm tra Fragment có gắn với FragmentManager trước khi xử lý
                                            if (isAdded()) {
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean("add", true);
                                                getParentFragmentManager().setFragmentResult("addSucess", bundle);
                                                getParentFragmentManager().popBackStack();
                                                Log.d("Firestore", "Dữ liệu đã được lưu thành công với ID: " + documentReference.getId());
                                                Toast.makeText(getContext(), "Lưu skill thành công", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            Log.e("saveSkill", "Lỗi khi gửi kết quả hoặc cập nhật giao diện", e);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Lỗi khi cập nhật ID của kỹ năng", e);
                                        if (isAdded()) {
                                            Toast.makeText(getContext(), "Lỗi khi cập nhật ID của kỹ năng", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } catch (Exception e) {
                            Log.e("saveSkill", "Lỗi khi đặt ID cho kỹ năng", e);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Lỗi khi lưu dữ liệu", e);
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Lỗi khi lưu dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Log.e("saveSkill", "Lỗi khi thực hiện lưu kỹ năng", e);
        }
    }

    public void loaddata(String id_skill) {
        try {
            UserSessionManager user = new UserSessionManager();
            String uid = user.getUserUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(uid)
                    .collection("role").document("candidate")
                    .collection("skills").document(id_skill)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            KiNang skill = documentSnapshot.toObject(KiNang.class);
                            if (skill != null) {
                                binding.nameskill.setText(skill.getName());
                                binding.mota.setText(skill.getDescription());
                            } else {
                                Log.e("LoadData", "Không thể chuyển đổi dữ liệu thành KiNang");
                            }
                        } else {
                            Log.e("LoadData", "Tài liệu không tồn tại với ID: " + id_skill);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("LoadData", "Lỗi khi lấy dữ liệu từ Firestore", e);
                    });
        } catch (Exception e) {
            Log.e("loaddata", "Lỗi khi tải dữ liệu kỹ năng", e);
        }
    }

    private void update(String id_skill) {
        try {
            UserSessionManager userSession = new UserSessionManager();
            id = userSession.getUserUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String nameSkill = binding.nameskill.getText().toString().trim();
            String description = binding.mota.getText().toString().trim();
            kiNang.setImageUrl(imageUrl);
            db.collection("users").document(id)
                    .collection("role").document("candidate")
                    .collection("skills")
                    .document(id_skill)
                    .set(kiNang)
                    .addOnSuccessListener(aVoid -> {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("add", true);
                        getParentFragmentManager().setFragmentResult("addSucess", bundle);
                        getParentFragmentManager().popBackStack();
                        Log.d("Firestore", "Dữ liệu đã được cập nhật thành công với ID: " + id_skill);
                        Toast.makeText(getContext(), "Cập nhật kỹ năng thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Lỗi khi cập nhật dữ liệu", e);
                        Toast.makeText(getContext(), "Lỗi khi cập nhật kỹ năng", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e("update", "Lỗi khi cập nhật kỹ năng", e);
        }
    }
}