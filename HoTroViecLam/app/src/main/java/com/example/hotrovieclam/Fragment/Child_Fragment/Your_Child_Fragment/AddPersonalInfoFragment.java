package com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentAddPersonalInfoBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPersonalInfoFragment extends Fragment {
    private FragmentAddPersonalInfoBinding binding;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        binding = FragmentAddPersonalInfoBinding.inflate(inflater, container, false);
        firestore = FirebaseFirestore.getInstance();
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        Bundle bundle = getArguments();
        String aa = null;
        if (bundle != null) {
            aa = bundle.getString("USER_UID");
            Log.d("III", "onCreateView: " + aa); // Sử dụng UID cho mục đích của bạn, ví dụ: binding.someTextView.setText(uid);
        }
        String getUid = aa;
        GetData(getUid);
        binding.btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            private boolean isBottonClicked = false;

            @Override
            public void onClick(View v) {
                if (!isBottonClicked) {
                    String title = binding.edtGioithieu.getText().toString().trim();
                    if (!title.isEmpty()) {
                        isBottonClicked = true;
                        addOrUpdateUserInfo(getUid, title);
                        Log.d("GGG", "onClick: " + getUid + title);
                        binding.btnUpdateProfile.setEnabled(false);
                        int grayColor = ContextCompat.getColor(getContext(), R.color.gray);
                        binding.btnUpdateProfile.setBackgroundColor(grayColor);
                        binding.loadding.setVisibility(View.VISIBLE);

                    } else {
                        Toast.makeText(getContext(), "Vui lòng điền đẩy đủ thông tin", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        //Lấy uid bên GioiThieuFragment
        //ham dem so chu nhap vao
        binding.edtGioithieu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length(); // Đếm số ký tự
                binding.countLenght.setText(String.valueOf(length)); // Hiển thị số lượng ký tự

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return binding.getRoot();
    }

    private void addOrUpdateUserInfo(String uid, String introduction) {
        // Tạo dữ liệu cho document trong subcollection "role/profile" của "users"
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("uid", uid);
        userInfo.put("introduction", introduction);

        // Thêm hoặc cập nhật document vào subcollection "role/profile" trong "users"
        firestore.collection("users").document(uid)
                .collection("role").document("candidate")
                .collection("introduction").document("introductdata").set(userInfo)
                .addOnSuccessListener(aVoid -> {
                    // Hiển thị Toast khi cập nhật thành công
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    binding.loadding.setVisibility(View.GONE);
                    binding.edtGioithieu.setText("");
                    Log.d("Firestore", "DocumentSnapshot successfully written!");

                    // Gửi kết quả cập nhật đến fragment trước đó
                    Bundle result = new Bundle();
                    result.putBoolean("isUpdated", true);
                    getParentFragmentManager().setFragmentResult("updateResult", result);

                    // Trở lại fragment trước đó
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    Log.d("Firestore", "Error writing document: ", e);
                    Toast.makeText(getContext(), "Lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show();
                });
    }


    private void GetData(String uid) {
        // Khởi tạo Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Tham chiếu đến document "introductdata" trong "users/{uid}/role/candidate/introduction"
        DocumentReference docRef = firestore.collection("users").document(uid)
                .collection("role").document("candidate")
                .collection("introduction").document("introductdata");

        // Lấy dữ liệu từ document
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Lấy dữ liệu từ trường "introduction"
                    String introduction = document.getString("introduction");
                    if (introduction != null && !introduction.isEmpty()) {
                        Log.d("Firestore", "Giới thiệu: " + introduction);

                        // Hiển thị dữ liệu lên TextView (ví dụ)
                        binding.edtGioithieu.setText(introduction);
                    } else {
                        Log.d("Firestore", "Không có data trong trường introduction");
                    }
                } else {
                    Log.d("Firestore", "Không có document 'introductdata' trong đường dẫn đã chỉ định");
                }
            } else {
                Log.d("Firestore", "Lỗi khi truy xuất document", task.getException());
            }
        });
    }

    public void back(){
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
            BottomNavigationView bottomNav = getActivity().findViewById(R.id.nav_buttom);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.GONE);
            }
        }
    }

}