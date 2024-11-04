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
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentEducationBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class EducationFragment extends Fragment {
    private FragmentEducationBinding binding;
    String id = null;
    String id_school = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEducationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
//        UserSessionManager user = new UserSessionManager();
//        id=user.getUserUid();
        Bundle bundle = getArguments();
        Log.d("VV", "onCreateView: "+id);
        if (bundle != null) {
            id = bundle.getString("USER_ID");
            id_school = bundle.getString("SCHOOL_ID");
            Log.d("AA", "onCreateView: " + id + "-----------------" + id_school);
            if (id_school!=null){
                loadData(id_school);
            }
            //loadData(id,id_school);
            binding.btnUpdateEducation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("VX", "onClick: " + id);
                    if (id != null) {
                        binding.loading.setVisibility(View.VISIBLE);
                        saveSchool();
                    } else if (id_school != null) {
                        binding.loading.setVisibility(View.VISIBLE);
                        updateSchool(id_school);
                    }

                }
            });
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
        String UID = id; // ID người dùng
        String nameSchool = binding.nameSchool.getText().toString().trim();
        String nameMajor = binding.nameMajor.getText().toString().trim();
        String start = binding.start.getText().toString().trim();
        String end = binding.end.getText().toString().trim();
        String description = binding.editTextDC.getText().toString().trim();
        Integer type = binding.check.isChecked() ? 1 : 0; // 1 cho đang học, 0 cho không học

        // Tạo một đối tượng TruongHoc
        TruongHoc truongHoc = new TruongHoc(null, UID, nameSchool, nameMajor, start, end, description, type);

        // Lưu dữ liệu vào Firestore
        db.collection("users").document(UID)
                .collection("role").document("candidate")
                .collection("school")
                .add(truongHoc)
                .addOnSuccessListener(documentReference -> {
                    truongHoc.setId_Shool(documentReference.getId());

                    documentReference.set(truongHoc) // Cập nhật với ID mới
                            .addOnSuccessListener(aVoid -> {
                                // Gửi req khi quay lại màn hình trước đó (HocVanFragment)
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("add", true);
                                getParentFragmentManager().setFragmentResult("addSucess", bundle);
                                getParentFragmentManager().popBackStack();
                                Log.d("Firestore", "Dữ liệu đã được lưu thành công với ID: " + documentReference.getId());
                                Toast.makeText(getContext(),"Lưu Trường học thành công", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi lưu dữ liệu", e);
                });
    }

    private void loadData( String id_school) {
        // Khởi tạo Firestore
        UserSessionManager user =new UserSessionManager();
        String uid = user.getUserUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy dữ liệu từ Firestore
        db.collection("users").document(uid)
                .collection("role").document("candidate")
                .collection("school").document(id_school) // Truy cập đến document với id_school
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy dữ liệu từ document
                        TruongHoc truongHoc = documentSnapshot.toObject(TruongHoc.class);

                        if (truongHoc != null) {
                            // Cập nhật giao diện với dữ liệu từ Firestore
                            binding.nameSchool.setText(truongHoc.getNameSchool());
                            binding.nameMajor.setText(truongHoc.getNganhHoc());
                            binding.start.setText(truongHoc.getTimeStart());
                            binding.end.setText(truongHoc.getTimeEnd());
                            binding.editTextDC.setText(truongHoc.getDeltail());
                            binding.check.setChecked(truongHoc.getType() == 1); // Nếu type == 1 thì đang học
                        } else {
                            Log.e("LoadData", "Không thể chuyển đổi dữ liệu thành TruongHoc");
                        }
                    } else {
                        Log.e("LoadData", "Tài liệu không tồn tại với ID: " + id_school);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LoadData", "Lỗi khi lấy dữ liệu từ Firestore", e);
                });
    }


    private void updateSchool(String id_school) {
        UserSessionManager sessionManager = new UserSessionManager();
        String uid = sessionManager.getUserUid();
        // Khởi tạo Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy các giá trị từ EditText và CheckBox
        String nameSchool = binding.nameSchool.getText().toString().trim();
        String nameMajor = binding.nameMajor.getText().toString().trim();
        String start = binding.start.getText().toString().trim();
        String end = binding.end.getText().toString().trim();
        String description = binding.editTextDC.getText().toString().trim();
        Integer type = binding.check.isChecked() ? 1 : 0; // 1 cho đang học, 0 cho không học

        // Tạo một đối tượng TruongHoc với dữ liệu mới
        TruongHoc truongHoc = new TruongHoc(id_school, uid, nameSchool, nameMajor, start, end, description, type);

        // Cập nhật dữ liệu vào Firestore
        db.collection("users").document(uid)
                .collection("role").document("candidate")
                .collection("school").document(id_school) // Truy cập vào document với id_school
                .set(truongHoc) // Sử dụng set() để cập nhật dữ liệu
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật thành công
                    Log.d("Firestore", "Dữ liệu đã được cập nhật thành công cho ID: " + id_school);
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("update", true);
                    getParentFragmentManager().setFragmentResult("updateSuccess", bundle);
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi khi cập nhật
                    Log.e("Firestore", "Lỗi khi cập nhật dữ liệu", e);
                    Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                });
    }


}