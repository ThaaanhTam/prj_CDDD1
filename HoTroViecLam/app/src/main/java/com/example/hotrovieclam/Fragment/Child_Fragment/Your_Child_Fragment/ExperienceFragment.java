package com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hotrovieclam.Model.Experience;
import com.example.hotrovieclam.Model.TruongHoc;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentEducationBinding;
import com.example.hotrovieclam.databinding.FragmentExperienceBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;


public class ExperienceFragment extends Fragment {

    private FragmentExperienceBinding binding;
    String id = null;
    String id_experience = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExperienceBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
//        UserSessionManager user = new UserSessionManager();
//        id=user.getUserUid();
        Bundle bundle = getArguments();
        Log.d("VV", "onCreateView: "+id);
        if (bundle != null) {
            id = bundle.getString("USER_ID");
            id_experience = bundle.getString("EXPERIENCE_ID");
            Log.d("AA", "onCreateView: " + id + "-----------------" + id_experience);
            if (id_experience!=null){
                loadData(id_experience);
            }
            //loadData(id,id_school);
            binding.btnUpdateExperience.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Log.d("VX", "onClick: " + id);
                    if (id != null) {
                        binding.loading.setVisibility(View.VISIBLE);
                        saveExperience();
                    } else if (id_experience != null) {
                        binding.loading.setVisibility(View.VISIBLE);
                        updateExperience(id_experience);
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

    private void saveExperience() {
        // Khởi tạo Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy các giá trị từ EditText và CheckBox
        String UID = id; // ID người dùng
        String name_organization = binding.nameToChuc.getText().toString().trim();
        String namejob = binding.editTextCongViec.getText().toString().trim();
        String start = binding.start.getText().toString().trim();
        String end = binding.end.getText().toString().trim();
        String description = binding.editTextDC.getText().toString().trim();
       // Integer type = binding.check.isChecked() ? 1 : 0; // 1 cho đang học, 0 cho không học

        // Tạo một đối tượng TruongHoc
        Experience experience = new Experience(description,end,start,namejob,name_organization,null,id);

        // Lưu dữ liệu vào Firestore
        db.collection("users").document(UID)
                .collection("role").document("candidate")
                .collection("experience")
                .add(experience)
                .addOnSuccessListener(documentReference -> {
                    experience.setIdExperiences(documentReference.getId());

                    documentReference.set(experience) // Cập nhật với ID mới
                            .addOnSuccessListener(aVoid -> {
                                // Gửi req khi quay lại màn hình trước đó (KinhNghiemFragment)
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("add", true);
                                getParentFragmentManager().setFragmentResult("addSucess", bundle);
                                getParentFragmentManager().popBackStack();
                                Log.d("Firestore", "Dữ liệu đã được lưu thành công với ID: " + documentReference.getId());
                                Toast.makeText(getContext(),"Lưu kinh nghiệm thành công", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi lưu dữ liệu", e);
                });
    }

    private void loadData( String id_experience) {
        // Khởi tạo Firestore
        UserSessionManager user =new UserSessionManager();
        String uid = user.getUserUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy dữ liệu từ Firestore
        db.collection("users").document(uid)
                .collection("role").document("candidate")
                .collection("experience").document(id_experience) // Truy cập đến document với id_school
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy dữ liệu từ document
                        Experience experience = documentSnapshot.toObject(Experience.class);

                        if (experience != null) {
                            // Cập nhật giao diện với dữ liệu từ Firestore
                            binding.nameToChuc.setText(experience.getName_organization());
                            binding.editTextCongViec.setText(experience.getPosition());
                            binding.start.setText(experience.getTime_start());
                            binding.end.setText(experience.getTime_end());
                            binding.editTextDC.setText(experience.getDescription());

                        } else {
                            Log.e("LoadData", "Không thể chuyển đổi dữ liệu thành Kinh nghiem");
                        }
                    } else {
                        Log.e("LoadData", "Tài liệu không tồn tại với ID: " + id_experience);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LoadData", "Lỗi khi lấy dữ liệu từ Firestore", e);
                });
    }


    private void updateExperience(String id_experience) {
        UserSessionManager sessionManager = new UserSessionManager();
        String uid = sessionManager.getUserUid();
        // Khởi tạo Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy các giá trị từ EditText và CheckBox
        String name_organization = binding.nameToChuc.getText().toString().trim();
        String namejob = binding.editTextCongViec.getText().toString().trim();
        String start = binding.start.getText().toString().trim();
        String end = binding.end.getText().toString().trim();
        String description = binding.editTextDC.getText().toString().trim();

        // Tạo một đối tượng kinh nghiệm với dữ liệu mới
        Experience experience = new Experience(description,end,start,namejob,name_organization,id_experience,uid);

        // Cập nhật dữ liệu vào Firestore
        db.collection("users").document(uid)
                .collection("role").document("candidate")
                .collection("experience").document(id_experience) // Truy cập vào document với id_experience
                .set(experience) // Sử dụng set() để cập nhật dữ liệu
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật thành công
                    Log.d("Firestore", "Dữ liệu đã được cập nhật thành công cho ID: " + id_experience);
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