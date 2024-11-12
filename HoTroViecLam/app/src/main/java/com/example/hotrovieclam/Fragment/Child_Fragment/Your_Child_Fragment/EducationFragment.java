package com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EducationFragment extends Fragment {
    private FragmentEducationBinding binding;
    String id = null;
    String id_school = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEducationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
//        UserSessionManager user = new UserSessionManager();
//        id=user.getUserUid();
        Bundle bundle = getArguments();
        Log.d("VV", "onCreateView: " + id);
        if (bundle != null) {
            id = bundle.getString("USER_ID");
            id_school = bundle.getString("SCHOOL_ID");
            Log.d("AA", "onCreateView: " + id + "-----------------" + id_school);
            if (id_school != null) {
                loadData(id_school);
            }
            binding.end.setVisibility(binding.check.isChecked() ? View.GONE : View.VISIBLE);

            // Thêm listener để cập nhật khi trạng thái của `CheckBox` thay đổi
            binding.check.setOnCheckedChangeListener((buttonView, isChecked) -> {
                binding.end.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            });
            //loadData(id,id_school);
            binding.btnUpdateEducation.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {
                    binding.btnUpdateEducation.setVisibility(View.GONE);
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, // Sử dụng style Spinner
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, // Sử dụng style Spinner
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
        try {
            // Khởi tạo Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Lấy các giá trị từ EditText và CheckBox
            String UID = id; // ID người dùng
            String nameSchool = binding.nameSchool.getText().toString().trim();
            String nameMajor = binding.nameMajor.getText().toString().trim();
            String start = binding.start.getText().toString().trim();
            String end = null; // Đặt giá trị mặc định cho end là null
            String description = binding.editTextDC.getText().toString().trim();
            Integer type = binding.check.isChecked() ? 1 : 0; // 1 cho đang học, 0 cho không học

            // Kiểm tra nếu `CheckBox` được chọn, đặt `end` là null và bỏ qua kiểm tra
            boolean hasError = false;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            if (!binding.check.isChecked()) {
                // Nếu `CheckBox` không được chọn, lấy giá trị từ `EditText` của `end`
                end = binding.end.getText().toString().trim();

                // Kiểm tra tính hợp lệ của ngày bắt đầu và ngày kết thúc
                try {
                    // Chuyển `start` và `end` sang đối tượng `Date`
                    Date startDate = dateFormat.parse(start);
                    Date endDate = dateFormat.parse(end);

                    // Kiểm tra ngày bắt đầu có lớn hơn ngày kết thúc không
                    if (startDate != null && endDate != null) {
                        if (startDate.after(endDate)) {
                            Snackbar.make(binding.getRoot(), "Ngày bắt đầu không được lớn hơn ngày kết thúc", Snackbar.LENGTH_SHORT).show();
                            binding.start.setError("Thời gian bắt đầu không được lớn hơn thời gian kết thúc");
                            hasError = true;
                        }
                    }
                } catch (ParseException e) {
                    Log.e("Ngày", "Lỗi khi phân tích ngày", e);
                    hasError = true;
                }

                // Kiểm tra nếu `end` trống khi `CheckBox` không được chọn
                if (end.isEmpty()) {
                    binding.end.setError("Thời gian kết thúc không được để trống");
                    hasError = true;
                }
            }

            if (UID == null || UID.isEmpty()) {
                Toast.makeText(getContext(), "Lỗi ID người dùng", Toast.LENGTH_SHORT).show();
                binding.loading.setVisibility(View.GONE);
                return;
            }

            if (nameSchool.isEmpty()) {
                binding.nameSchool.setError("Tên trường không được để trống");
                hasError = true;
            }

            if (nameMajor.isEmpty()) {
                binding.nameMajor.setError("Tên chuyên ngành không được để trống");
                hasError = true;
            }

            if (start.isEmpty()) {
                binding.start.setError("Thời gian bắt đầu không được để trống");
                hasError = true;
            }

            // Nếu có lỗi, dừng việc lưu lại
            if (hasError) {
                binding.loading.setVisibility(View.GONE);
                binding.btnUpdateEducation.setVisibility(View.VISIBLE);
                return;
            }

            // Tạo một đối tượng TruongHoc
            TruongHoc truongHoc = new TruongHoc(null, UID, nameSchool, nameMajor, start, end, description, type);

            // Lưu dữ liệu vào Firestore
            db.collection("users").document(UID).collection("role").document("candidate").collection("school")
                    .add(truongHoc)
                    .addOnSuccessListener(documentReference -> {
                        try {
                            truongHoc.setId_Shool(documentReference.getId());

                            documentReference.set(truongHoc) // Cập nhật với ID mới
                                    .addOnSuccessListener(aVoid -> {
                                        // Gửi req khi quay lại màn hình trước đó (HocVanFragment)
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean("add", true);
                                        getParentFragmentManager().setFragmentResult("addSchool", bundle);
                                        getParentFragmentManager().popBackStack();
                                        Log.d("Firestore", "Dữ liệu đã được lưu thành công với ID: " + documentReference.getId());
                                        Toast.makeText(getContext(), "Lưu Trường học thành công", Toast.LENGTH_SHORT).show();
                                    });
                        } catch (Exception e) {
                            Log.e("Firestore", "Lỗi khi cập nhật ID của trường học", e);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Lỗi khi lưu dữ liệu", e);
                        Toast.makeText(getContext(), "Lỗi khi lưu dữ liệu", Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            Log.e("saveSchool", "Lỗi trong quá trình lưu thông tin trường học", e);
            Toast.makeText(getContext(), "Đã xảy ra lỗi khi lưu thông tin", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadData(String id_school) {
        // Khởi tạo Firestore
        UserSessionManager user = new UserSessionManager();
        String uid = user.getUserUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy dữ liệu từ Firestore
        db.collection("users").document(uid).collection("role").document("candidate").collection("school").document(id_school) // Truy cập đến document với id_school
                .get().addOnSuccessListener(documentSnapshot -> {
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
                }).addOnFailureListener(e -> {
                    Log.e("LoadData", "Lỗi khi lấy dữ liệu từ Firestore", e);
                });
    }

    private void updateSchool(String id_school) {
        try {
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

            // Kiểm tra dữ liệu không được để trống và thiết lập lỗi
            boolean hasError = false;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            try {
                // Lấy chuỗi từ EditText và chuyển sang đối tượng Date
                Date startDate = dateFormat.parse(start);
                Date endDate = dateFormat.parse(end);

                // Kiểm tra ngày bắt đầu có nhỏ hơn ngày kết thúc không
                if (startDate != null && endDate != null) {
                    if (startDate.after(endDate)) {
                        // Nếu ngày bắt đầu lớn hơn ngày kết thúc, báo lỗi
                        //Toast.makeText(getContext(), "Ngày bắt đầu không được lớn hơn ngày kết thúc", Toast.LENGTH_SHORT).show();
                         Snackbar.make(binding.getRoot(), "Ngày bắt đầu không được lớn hơn ngày kết thúc", Snackbar.LENGTH_SHORT).show();
                        binding.start.setError("Thời gian bắt đầu không được lớn hơn thời gian kết thúc");

                        hasError = true;
                    } else {
                        // Xử lý khi ngày bắt đầu hợp lệ
                        Log.d("Ngày", "Ngày bắt đầu nhỏ hơn hoặc bằng ngày kết thúc");
                    }
                }
            } catch (ParseException e) {
                Log.e("Ngày", "Lỗi khi phân tích ngày", e);
                Toast.makeText(getContext(), "Định dạng ngày không hợp lệ", Toast.LENGTH_SHORT).show();
                hasError = true;
            }

            if (uid == null || uid.isEmpty()) {
                Toast.makeText(getContext(), "Lỗi ID người dùng", Toast.LENGTH_SHORT).show();
                return; // UID là thông tin quan trọng, nên cần kiểm tra và thoát sớm nếu null
            }

            if (nameSchool.isEmpty()) {
                binding.nameSchool.setError("Tên trường không được để trống");
                hasError = true;
            }

            if (nameMajor.isEmpty()) {
                binding.nameMajor.setError("Tên chuyên ngành không được để trống");
                hasError = true;
            }

            if (start.isEmpty()) {
                binding.start.setError("Thời gian bắt đầu không được để trống");
                hasError = true;
            }

            if (end.isEmpty()) {
                binding.end.setError("Thời gian kết thúc không được để trống");
                hasError = true;
            }

            // Nếu có lỗi, dừng việc cập nhật
            if (hasError) {
                binding.loading.setVisibility(View.GONE);
                binding.btnUpdateEducation.setVisibility(View.VISIBLE);
                return;
            }

            // Tạo một đối tượng TruongHoc với dữ liệu mới
            TruongHoc truongHoc = new TruongHoc(id_school, uid, nameSchool, nameMajor, start, end, description, type);

            // Cập nhật dữ liệu vào Firestore
            db.collection("users").document(uid).collection("role").document("candidate").collection("school").document(id_school) // Truy cập vào document với id_school
                    .set(truongHoc) // Sử dụng set() để cập nhật dữ liệu
                    .addOnSuccessListener(aVoid -> {
                        // Cập nhật thành công
                        Log.d("Firestore", "Dữ liệu đã được cập nhật thành công cho ID: " + id_school);
                        Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("update", true);
                        getParentFragmentManager().setFragmentResult("updateTruongHoc", bundle);
                        getParentFragmentManager().popBackStack();
                    }).addOnFailureListener(e -> {
                        // Xử lý lỗi khi cập nhật
                        Log.e("Firestore", "Lỗi khi cập nhật dữ liệu", e);
                        Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e("updateSchool", "Lỗi trong quá trình cập nhật thông tin trường học", e);
            Toast.makeText(getContext(), "Đã xảy ra lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show();
        }
    }
public void HienThongBao(String namethongbao){

}

}