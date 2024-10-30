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
        ReadData(i);
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
        DocumentReference docRef = firestore.collection("Introduces").document(uid);
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
                        Log.d("Firestore", "Đã có data: " + introduction);
                    }
                } else {
                    Log.d("Firestore", "Không có document với UID này trong bảng Introduces");
                    binding.btnCapnhatProfile.setVisibility(View.VISIBLE);
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
}