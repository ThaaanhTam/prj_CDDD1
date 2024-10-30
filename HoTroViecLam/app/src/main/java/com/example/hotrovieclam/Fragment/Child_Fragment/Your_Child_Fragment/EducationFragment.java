package com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hotrovieclam.Model.TruongHoc;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentEducationBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class EducationFragment extends Fragment {
    private FragmentEducationBinding binding;
    String id = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEducationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Bundle bundle = getArguments();

        if (bundle != null) {
            id = bundle.getString("USER_ID");
            Log.d("AA", "onCreateView: " + id);
        }

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                    BottomNavigationView bottomNav = getActivity().findViewById(R.id.nav_buttom);
                    if (bottomNav != null) {
                        bottomNav.setVisibility(View.GONE);
                    }// Quay lại Fragment trước đó
                }
            }
        });
        binding.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogStart();
            }
        });
        binding.end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogEnd();
            }
        });
        binding.btnUpdateEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("VX", "onClick: " + id);
                saveSchool();
            }
        });
        return view;
    }

    public void showDatePickerDialogStart() {
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
                    binding.start.setText(selectedDate);
                }, year, month, day);

        // Xử lý sự kiện khi người dùng bấm nút Cancel
        datePickerDialog.setOnCancelListener(dialog -> {
            // Xử lý khi người dùng bấm hủy
            Toast.makeText(getContext(), "Bạn đã hủy chọn ngày", Toast.LENGTH_SHORT).show();
        });

        // Hiển thị DatePickerDialog
        datePickerDialog.show();
    }

    public void showDatePickerDialogEnd() {
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
                    binding.end.setText(selectedDate);
                }, year, month, day);

        // Xử lý sự kiện khi người dùng bấm nút Cancel
        datePickerDialog.setOnCancelListener(dialog -> {
            // Xử lý khi người dùng bấm hủy
            Toast.makeText(getContext(), "Bạn đã hủy chọn ngày", Toast.LENGTH_SHORT).show();
        });

        // Hiển thị DatePickerDialog
        datePickerDialog.show();
    }

    private void saveSchool() {
        // Khởi tạo Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy các giá trị từ EditText và CheckBox
        String UID = id; // ID người dùng hoặc bất kỳ thông tin nào bạn muốn lưu
        String nameSchool = binding.nameSchool.getText().toString().trim();
        String nameMajor = binding.nameMajor.getText().toString().trim();
        String start = binding.start.getText().toString().trim();
        String end = binding.end.getText().toString().trim();
        String description = binding.editTextDC.getText().toString().trim();
        Integer type = binding.check.isChecked() ? 1 : 0; // 1 cho đang học, 0 cho không học

        // Tạo một đối tượng TruongHoc
        TruongHoc truongHoc = new TruongHoc(null,UID, nameSchool, nameMajor, start, end, description, type);

        // Lưu dữ liệu vào Firestore
        db.collection("School")
                .add(truongHoc) // Sử dụng add() để tạo ID tự động
                .addOnSuccessListener(documentReference -> {
                    // Cập nhật id_School vào đối tượng sau khi lưu thành công
                    truongHoc.setId_Shool(documentReference.getId());
                    documentReference.set(truongHoc); // Cập nhật với ID mới

                    Log.d("Firestore", "Dữ liệu đã được lưu thành công với ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi lưu dữ liệu", e);
                });
    }
}