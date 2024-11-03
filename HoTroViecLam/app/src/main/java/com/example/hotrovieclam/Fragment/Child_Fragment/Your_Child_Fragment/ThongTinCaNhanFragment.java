package com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hotrovieclam.Model.Profile;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentThongTinCaNhanBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class ThongTinCaNhanFragment extends Fragment {

    private FragmentThongTinCaNhanBinding binding;
    String a = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentThongTinCaNhanBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Bundle bundle = getArguments();
        if (bundle != null) {
            a = bundle.getString("uid");
            Log.d("TTT", "onCreateView: " + a);
        }
        HienThiThongTin(a);
        binding.back.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
                BottomNavigationView bottomNav = getActivity().findViewById(R.id.nav_buttom);
                if (bottomNav != null) {
                    bottomNav.setVisibility(View.GONE);
                }
            }
        });

        binding.editTextDate.setOnClickListener(v -> showDatePickerDialog()); // Sử dụng onClick thay vì onTouch
        binding.btnUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("uid", "onClick: " + a);
                String ngaysinh = binding.editTextDate.getText().toString().trim();
                String diachi = binding.editTextDC.getText().toString().trim();
                int gioitinh = 0; // Khởi tạo biến giới tính

                if (binding.rdNam.isChecked()) {
                    gioitinh = 1; // Gán "Nam"
                } else if (binding.rdNu.isChecked()) {
                    gioitinh = 2; // Gán "Nữ"
                } else {
                    Log.d("tttt", "onClick: Không có giới tính được chọn");
                }

                Log.d("tttt", "onClick: " + ngaysinh + ", " + diachi + ", Giới tính: " + gioitinh);
                Profile profile = new Profile(ngaysinh, diachi, gioitinh);

                // Tạo đối tượng Profile với dữ liệu
                addProfile(a,profile);

            }
        });


        return view;
    }

    public void showDatePickerDialog() {
        // Lấy ngày hiện tại
        final Calendar calendar = Calendar.getInstance();

        // Lấy năm, tháng, ngày hiện tại
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Hiển thị DatePickerDialog với kiểu Spinner
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar, // Sử dụng style Spinner
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Khi người dùng chọn ngày, hiển thị ngày đã chọn vào EditText
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    binding.editTextDate.setText(selectedDate);
                }, year, month, day);

        // Xử lý sự kiện khi người dùng bấm nút Cancel
        datePickerDialog.setOnCancelListener(dialog -> {
            // Xử lý khi người dùng bấm hủy
            Toast.makeText(getContext(), "Bạn đã hủy chọn ngày", Toast.LENGTH_SHORT).show();
        });

        // Hiển thị DatePickerDialog
        datePickerDialog.show();
    }

    public void HienThiThongTin(String uid) {

        // Dùng UID để truy vấn Firestore hoặc hiển thị thông tin người dùng
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String email = document.getString("email");
                        String phone = document.getString("phoneNumber");
                        // Hiển thị thông tin người dùng
                        binding.editTextEmail.setText(email);
                        binding.editTextsdt.setText(phone);

                        Log.d("PPPP", "onComplete: " + email);
                        DocumentReference profileRef = db.collection("users").document(uid)
                                .collection("role").document("candidate")
                                .collection("profile").document(uid);
                        profileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot profileDocument = task.getResult();
                                    if (profileDocument.exists()) {
                                        String ngaysinh = profileDocument.getString("birthday"); // Lấy ngày sinh
                                        String diachi = profileDocument.getString("address"); // Lấy địa chỉ
                                        int gioitinh = profileDocument.getLong("gioitinh").intValue(); // Lấy giới tính dưới dạng số

                                        // Hiển thị thông tin profile
                                        binding.editTextDate.setText(ngaysinh); // Hiển thị ngày sinh
                                        binding.editTextDC.setText(diachi); // Hiển thị địa chỉ

                                        // Kiểm tra giới tính và cập nhật trạng thái RadioButton
                                        if (gioitinh == 1) {
                                            binding.rdNam.setChecked(true); // Kiểm tra giới tính Nam
                                        } else if (gioitinh == 2) {
                                            binding.rdNu.setChecked(true); // Kiểm tra giới tính Nữ
                                        }
                                    } else {
                                        Log.d("Firestore", "Không tìm thấy dữ liệu profile.");
                                    }
                                } else {
                                    Log.d("Firestore", "Lỗi khi truy vấn dữ liệu profile.", task.getException());
                                }
                            }
                        });
                    } else {
                        Log.d("Firestore", "Không tìm thấy dữ liệu người dùng.");
                    }
                } else {
                    Log.d("Firestore", "Lỗi khi truy vấn dữ liệu.", task.getException());
                }
            }
        });
    }

    public void addProfile(String uid,Profile profile) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
       // Giả sử 'a' là UID của người dùng

        // Lưu dữ liệu vào Firestore với ID cụ thể
        db.collection("users").document(uid)
                .collection("role").document("candidate")
                .collection("profile")
                .document(uid) // Sử dụng UID làm ID tài liệu
                .set(profile) // Sử dụng set() để lưu dữ liệu
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật thành công
                    Log.d("Firestore", "Dữ liệu đã được lưu thành công với ID: " + uid);
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi khi lưu dữ liệu
                    Log.e("Firestore", "Lỗi khi lưu dữ liệu", e);
                    Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                });
    }
}
