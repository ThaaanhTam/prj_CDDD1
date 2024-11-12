package com.example.hotrovieclam.Fragment.Child_Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment.AddPersonalInfoFragment;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentGioiThieuBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class GioiThieuFragment extends Fragment {
    private UserSessionManager userSessionManager;
    private FragmentGioiThieuBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGioiThieuBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        userSessionManager = new UserSessionManager();
        String i = userSessionManager.getUserUid();
        //lấy kết quả trả về của Bunble bên kia
        getParentFragmentManager().setFragmentResultListener("updateResult", this, (requestKey, bundle) -> {
            boolean isUpdated = bundle.getBoolean("isUpdated");
            if (isUpdated) {
                ReadData(i); // Tải lại dữ liệu mới  khi cập nhật thành công
            }
        });
//tai lai du lieu khi truyen uid qua
        ReadData(i);
        fetchDataRealtime(i);

        binding.btnCapnhatProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPersonalInfoFragment addPersonalInfoFragment = new AddPersonalInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putString("USER_UID", i);
                addPersonalInfoFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, addPersonalInfoFragment).addToBackStack(null).commit();
            }
        });
        binding.editGioiThieu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPersonalInfoFragment addPersonalInfoFragment = new AddPersonalInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putString("USER_UID", i);
                addPersonalInfoFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, addPersonalInfoFragment).addToBackStack(null).commit();
            }
        });
        return view;
    }

    public void ReadData(String uid) {
        // Khởi tạo Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Tham chiếu đến document với UID trong bảng "Introduces"
        DocumentReference docRef = firestore.collection("users").document(uid)
                .collection("role").document("candidate")
                .collection("introduction").document("introductdata");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Kiểm tra xem trường "introduction" đã có data hay chưa
                    String introduction = document.getString("introduction");
                    if (introduction != null && !introduction.isEmpty()) {
                        binding.gioithiebanthan.setText(introduction);
                        //nếu có data thì ẩn nút thêm thông tin giới thiệu bản thân và hiện nút và ngược lại
                        binding.editGioiThieu.setVisibility(View.VISIBLE);
                        binding.btnCapnhatProfile.setVisibility(View.GONE);
                        Log.d("Firestore", "Đã có data: " + introduction);
                    }
                } else {
                    Log.d("Firestore", "Không có document với UID này trong bảng Introduces");
                    binding.btnCapnhatProfile.setVisibility(View.VISIBLE);
                    binding.editGioiThieu.setVisibility(View.GONE);
                }
            } else {
                // Xử lý khi bảng "Introduces" không tồn tại hoặc lỗi kết nối Firestore
                Exception e = task.getException();
                if (e instanceof FirebaseFirestoreException &&
                        ((FirebaseFirestoreException) e).getCode() == FirebaseFirestoreException.Code.NOT_FOUND) {
                    Log.d("Firestore", "Bảng Introduces không tồn tại.");
                } else {
                    Log.d("Firestore", "Lỗi khi truy xuất document", e);
                }
            }
        });
    }
    public void fetchDataRealtime(String uid) {
        // Khởi tạo Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Tham chiếu đến document với UID trong bảng "Introduces"
        DocumentReference docRef = firestore.collection("users").document(uid)
                .collection("role").document("candidate")
                .collection("introduction").document("introductdata");

        // Lắng nghe sự thay đổi của document này
        docRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                Log.e("Firestore", "Lỗi khi lắng nghe sự kiện thay đổi dữ liệu", error);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                // Kiểm tra xem trường "introduction" đã có data hay chưa
                String introduction = snapshot.getString("introduction");
                if (introduction != null && !introduction.isEmpty()) {
                    binding.gioithiebanthan.setText(introduction);
                    binding.editGioiThieu.setVisibility(View.VISIBLE);
                    binding.btnCapnhatProfile.setVisibility(View.GONE);
                    Log.d("Firestore", "Đã có data: " + introduction);
                } else {
                    binding.gioithiebanthan.setText("");
                    binding.btnCapnhatProfile.setVisibility(View.VISIBLE);
                    binding.editGioiThieu.setVisibility(View.GONE);
                    Log.d("Firestore", "Trường introduction trống.");
                }
            } else {
                // Trường hợp không có document nào hoặc document bị xóa
                binding.gioithiebanthan.setText("");
                binding.btnCapnhatProfile.setVisibility(View.VISIBLE);
                binding.editGioiThieu.setVisibility(View.GONE);
                Log.d("Firestore", "Document không tồn tại hoặc đã bị xóa.");
            }
        });
    }

}