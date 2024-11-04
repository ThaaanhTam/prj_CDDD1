package com.example.hotrovieclam.Fragment.RecruiterManagement;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityPostJobBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostJob extends AppCompatActivity {
    ActivityPostJobBinding binding;
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    UserSessionManager userSessionManager = new UserSessionManager();
    String uid = userSessionManager.getUserUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostJobBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.title));

        binding.lvGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.etDateStart.setOnClickListener(v -> showDatePickerDialog(binding.etDateStart, true));
        binding.etDateEnd.setOnClickListener(v -> showDatePickerDialog(binding.etDateEnd, false));
        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    pushDataToFirestore();
                }
            }
        });


    }
    private boolean validateInputs() {
        String title = binding.edtTitle.getText().toString().trim();
        String description = binding.edtDescription.getText().toString().trim();
        String salaryMinStr = binding.edtSalaryMin.getText().toString().trim();
        String salaryMaxStr = binding.edtSalaryMax.getText().toString().trim();
        String startTime = binding.etDateStart.getText().toString().trim();
        String endTime = binding.etDateEnd.getText().toString().trim();

        // Kiểm tra tất cả các trường không rỗng
        if (title.isEmpty() || description.isEmpty() || salaryMinStr.isEmpty() || salaryMaxStr.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Kiểm tra lương tối đa phải lớn hơn lương tối thiểu
        int salaryMin = Integer.parseInt(salaryMinStr);
        int salaryMax = Integer.parseInt(salaryMaxStr);
        if (salaryMax <= salaryMin) {
            Toast.makeText(this, "Lương tối đa phải lớn hơn lương tối thiểu", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true; // Tất cả các điều kiện hợp lệ
    }

    private void clearInputFields() {
        binding.edtTitle.setText("");
        binding.edtDescription.setText("");
        binding.edtSalaryMin.setText("");
        binding.edtSalaryMax.setText("");
        binding.etDateStart.setText("");
        binding.etDateEnd.setText("");
    }
    public void pushDataToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> dataToPush = new HashMap<>();

        dataToPush.put("salaryMax", Integer.parseInt(binding.edtSalaryMax.getText().toString()));
        dataToPush.put("salaryMin", Integer.parseInt(binding.edtSalaryMin.getText().toString()));
        dataToPush.put("createdAt", Timestamp.now());
        dataToPush.put("startTime", binding.etDateStart.getText().toString());
        dataToPush.put("endTime", binding.etDateEnd.getText().toString());
        dataToPush.put("employerId", uid);
        dataToPush.put("title", binding.edtTitle.getText().toString());
        dataToPush.put("description", binding.edtDescription.getText().toString());

        db.collection("jobs")
                .add(dataToPush)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Dữ liệu đã được đẩy lên thành công");
                    Toast.makeText(this, "Đăng tuyển thành công", Toast.LENGTH_SHORT).show();

                    // Xóa dữ liệu trong các trường nhập
                    clearInputFields();

                    finish(); // Quay lại màn hình trước đó
                })
                .addOnFailureListener(e -> {
                    Log.d("Firestore", "Đẩy dữ liệu thất bại: " + e.getMessage());
                });
    }
    private void showDatePickerDialog(final EditText dateField, final boolean isStartDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            dateField.setText(selectedDate);

            // Cập nhật ngày bắt đầu hoặc ngày kết thúc dựa trên lựa chọn
            if (isStartDate) {
                startDate.set(selectedYear, selectedMonth, selectedDay);
            } else {
                endDate.set(selectedYear, selectedMonth, selectedDay);
            }

            // Kiểm tra tính hợp lệ của ngày bắt đầu và ngày kết thúc
            if (startDate.after(endDate)) {
                Toast.makeText(this, "Ngày bắt đầu phải nhỏ hơn ngày kết thúc", Toast.LENGTH_SHORT).show();
                dateField.setText("");  // Xóa ngày không hợp lệ
            }

        }, year, month, day);

        datePickerDialog.show();
    }
}
