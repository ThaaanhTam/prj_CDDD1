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
import com.example.hotrovieclam.Model.HieuUngThongBao;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentExperienceBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class ExperienceFragment extends Fragment {

    private FragmentExperienceBinding binding;
    private String userId = null;
    private String experienceId = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExperienceBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Nhận dữ liệu từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString("USER_ID");
            experienceId = bundle.getString("EXPERIENCE_ID");

            if (experienceId != null) {
                loadData(experienceId);
            }
        }

        // Cấu hình các sự kiện click
        setupEventHandlers();

        return view;
    }

    private void setupEventHandlers() {
        // Nút quay lại
        binding.back.setOnClickListener(v -> handleBackNavigation());

        // Nút chọn ngày bắt đầu
        binding.start.setOnClickListener(v -> showDatePickerDialog(binding.start));

        // Nút chọn ngày kết thúc
        binding.end.setOnClickListener(v -> showDatePickerDialog(binding.end));

        // Nút lưu hoặc cập nhật kinh nghiệm
        binding.btnUpdateExperience.setOnClickListener(v -> {
            binding.loading.setVisibility(View.VISIBLE);
            if (experienceId != null) {
                updateExperience(experienceId);
            } else if (userId != null) {
                saveExperience();
            }
        });
    }

    private void handleBackNavigation() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
            BottomNavigationView bottomNav = getActivity().findViewById(R.id.nav_buttom);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.GONE);
            }
        }
    }

    private void showDatePickerDialog(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                (dateView, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    ((android.widget.TextView) view).setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.setOnCancelListener(dialog ->
                Toast.makeText(getContext(), "Bạn đã hủy chọn ngày", Toast.LENGTH_SHORT).show()
        );

        datePickerDialog.show();
    }

    private void saveExperience() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy dữ liệu từ form
        Experience experience = getExperienceFromInput();
        if (experience == null) {
            binding.loading.setVisibility(View.GONE);
            return;
        }

        db.collection("users").document(userId)
                .collection("role").document("candidate")
                .collection("experience")
                .add(experience)
                .addOnSuccessListener(documentReference -> {
                    experience.setIdExperiences(documentReference.getId());
                    documentReference.set(experience)
                            .addOnSuccessListener(aVoid -> {
                                binding.loading.setVisibility(View.GONE);
                                HieuUngThongBao.showSuccessToast(requireContext(), "Lưu kinh nghiệm thành công");
                                getParentFragmentManager().popBackStack();
                            });
                })
                .addOnFailureListener(e -> {
                    binding.loading.setVisibility(View.GONE);
                    HieuUngThongBao.showErrorToast(requireContext(), "Lỗi khi lưu kinh nghiệm");
                    Log.e("Firestore", "Lỗi khi lưu dữ liệu", e);
                });
    }

    private void updateExperience(String experienceId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Experience experience = getExperienceFromInput();
        if (experience == null) {
            binding.loading.setVisibility(View.GONE);
            return;
        }

        db.collection("users").document(userId)
                .collection("role").document("candidate")
                .collection("experience").document(experienceId)
                .set(experience)
                .addOnSuccessListener(aVoid -> {
                    binding.loading.setVisibility(View.GONE);
                    HieuUngThongBao.showSuccessToast(requireContext(), "Cập nhật kinh nghiệm thành công");
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    binding.loading.setVisibility(View.GONE);
                    HieuUngThongBao.showErrorToast(requireContext(), "Cập nhật kinh nghiệm thất bại");
                    Log.e("Firestore", "Lỗi khi cập nhật dữ liệu", e);
                });
    }

    private Experience getExperienceFromInput() {
        String nameOrganization = binding.nameToChuc.getText().toString().trim();
        String position = binding.editTextCongViec.getText().toString().trim();
        String start = binding.start.getText().toString().trim();
        String end = binding.end.getText().toString().trim();
        String description = binding.editTextDC.getText().toString().trim();

        // Kiểm tra dữ liệu
        if (nameOrganization.isEmpty()) {
            binding.nameToChuc.setError("Vui lòng nhập tên tổ chức");
            binding.nameToChuc.requestFocus();
            return null;
        }
        if (position.isEmpty()) {
            binding.editTextCongViec.setError("Vui lòng nhập tên công việc");
            binding.editTextCongViec.requestFocus();
            return null;
        }
        if (start.isEmpty()) {
            binding.start.setError("Vui lòng chọn ngày bắt đầu");
            binding.start.requestFocus();
            return null;
        }
        if (end.isEmpty()) {
            binding.end.setError("Vui lòng chọn ngày kết thúc");
            binding.end.requestFocus();
            return null;
        }
        if (description.isEmpty()) {
            binding.editTextDC.setError("Vui lòng nhập mô tả công việc");
            binding.editTextDC.requestFocus();
            return null;
        }

        return new Experience(description, end, start, position, nameOrganization, experienceId, userId);
    }

    private void loadData(String experienceId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
                .collection("role").document("candidate")
                .collection("experience").document(experienceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Experience experience = documentSnapshot.toObject(Experience.class);
                        if (experience != null) {
                            binding.nameToChuc.setText(experience.getName_organization());
                            binding.editTextCongViec.setText(experience.getPosition());
                            binding.start.setText(experience.getTime_start());
                            binding.end.setText(experience.getTime_end());
                            binding.editTextDC.setText(experience.getDescription());
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("LoadData", "Lỗi khi lấy dữ liệu từ Firestore", e));
    }
}
