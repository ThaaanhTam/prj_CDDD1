package com.example.hotrovieclam.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityEditJobBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditJob extends AppCompatActivity {
    ActivityEditJobBinding binding;
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    UserSessionManager userSessionManager = new UserSessionManager();
    ArrayList<String> fieldNames = new ArrayList<>();
    String uid = userSessionManager.getUserUid();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private String jobID;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditJobBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.title));
        setupFieldSpinner();
        jobID = getIntent().getStringExtra("jobID");
        db = FirebaseFirestore.getInstance();
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
//        } else {
//            getUserLocation();
//        }


        binding.lvGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fetchJobDetails();
        binding.etDateStart.setOnClickListener(v -> showDatePickerDialog(binding.etDateStart, true));
        binding.etDateEnd.setOnClickListener(v -> showDatePickerDialog(binding.etDateEnd, false));
        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    confirmEdit();
                }
            }
        });


    }


    public void confirmEdit() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận sửa")
                .setMessage("Bạn có chắc chắn muốn sửa công vệc này không?")
                .setPositiveButton("Sửa", (dialog, which) -> {
                    // Người dùng nhấn "Xóa"
                    pushDataToFirestore();
                })
                .setNegativeButton("Hủy", (dialog, which) -> {

                    dialog.dismiss();
                })
                .show();
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        getAddressFromLocation(latitude, longitude);
                    }
                });
    }


    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String fullAddress = address.getAddressLine(0); // Địa chỉ đầy đủ
                Log.d("Địa chỉ: ", fullAddress);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.edtLocation.setText(fullAddress);
                    }
                });

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {

            }
        }
    }

    private boolean validateInputs() {
        String title = binding.edtTitle.getText().toString().trim();
        String description = binding.edtDescription.getText().toString().trim();
        String salaryMinStr = binding.edtSalaryMin.getText().toString().trim();
        String salaryMaxStr = binding.edtSalaryMax.getText().toString().trim();
        String startTime = binding.etDateStart.getText().toString().trim();
        String endTime = binding.etDateEnd.getText().toString().trim();
        if (binding.edtTitle.getText().toString().isEmpty()) {

            binding.textInputLayoutTitle.setError("Tiêu đề không được bỏ trống!");

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.textInputLayoutTitle.getLayoutParams();
            layoutParams.topMargin = 10;
            binding.textInputLayoutTitle.setLayoutParams(layoutParams);
            return false;
        } else {

            binding.textInputLayoutTitle.setError(null);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.textInputLayoutTitle.getLayoutParams();
            layoutParams.topMargin = 0;  // Không thay đổi khoảng cách viền
            binding.textInputLayoutTitle.setLayoutParams(layoutParams);
        }


        if (binding.edtLocation.getText().toString().isEmpty()) {

            binding.textInputLayoutLocation.setError("Địa chỉ không được bỏ trống!");

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.textInputLayoutLocation.getLayoutParams();
            layoutParams.topMargin = 10;
            binding.textInputLayoutLocation.setLayoutParams(layoutParams);
            return false;
        } else {

            binding.textInputLayoutLocation.setError(null);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.textInputLayoutLocation.getLayoutParams();
            layoutParams.topMargin = 0;  // Không thay đổi khoảng cách viền
            binding.textInputLayoutLocation.setLayoutParams(layoutParams);
        }


        if (binding.edtDescription.getText().toString().isEmpty()) {

            binding.textInputLayoutDes.setError("Mô tả không được bỏ trống!");

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.textInputLayoutDes.getLayoutParams();
            layoutParams.topMargin = 20;
            binding.textInputLayoutDes.setLayoutParams(layoutParams);
            return false;
        } else {

            binding.textInputLayoutDes.setError(null);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.textInputLayoutDes.getLayoutParams();
            layoutParams.topMargin = 0;  // Không thay đổi khoảng cách viền
            binding.textInputLayoutDes.setLayoutParams(layoutParams);
        }


        if (title.isEmpty() || description.isEmpty() || salaryMinStr.isEmpty() || salaryMaxStr.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }

        Double salaryMin = Double.parseDouble(salaryMinStr);
        Double salaryMax = Double.parseDouble(salaryMaxStr);
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

    private void setupFieldSpinner() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                fieldNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtField.setAdapter(adapter);

        // Fetch data from Firestore
        db.collection("majors")
                .orderBy("name")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    fieldNames.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        if (name != null) {
                            fieldNames.add(name);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không thể tải danh sách lĩnh vực", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error loading fields: " + e.getMessage());
                });

        // Handle item selection
        binding.edtField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedField = fieldNames.get(position);
                // Do something with the selected field
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where nothing is selected
            }
        });
    }
    public String convertTimestampToString(Timestamp timestamp) {
        if (timestamp != null) {

            Date date = timestamp.toDate();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            return format.format(date);
        }
        return "N/A";  // Trả về "N/A" nếu timestamp null
    }
    public void pushDataToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> dataToPush = new HashMap<>();

        // Sử dụng ID tài liệu đã có


        dataToPush.put("id", jobID);
        dataToPush.put("salaryMax", Double.parseDouble(binding.edtSalaryMax.getText().toString()));
        dataToPush.put("salaryMin", Double.parseDouble(binding.edtSalaryMin.getText().toString()));
        Timestamp timestamp = Timestamp.now();
        String timestampString = convertTimestampToString(timestamp);
        dataToPush.put("createdAt", timestampString);
        dataToPush.put("startTime", binding.etDateStart.getText().toString());
        dataToPush.put("endTime", binding.etDateEnd.getText().toString());
        dataToPush.put("location", binding.edtLocation.getText().toString());
        dataToPush.put("employerId", uid);
        dataToPush.put("title", binding.edtTitle.getText().toString());
        dataToPush.put("description", binding.edtDescription.getText().toString());
        dataToPush.put("major", binding.edtField.getSelectedItem().toString());

        db.collection("jobs")
                .document(jobID)
                .set(dataToPush)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Dữ liệu đã được cập nhật thành công");
                    Toast.makeText(this, "Cập nhật tuyển dụng thành công", Toast.LENGTH_SHORT).show();
                    clearInputFields();

                })
                .addOnFailureListener(e -> {
                    Log.d("Firestore", "Cập nhật dữ liệu thất bại: " + e.getMessage());
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
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

    private void fetchJobDetails() {
        // Replace "jobs" with the name of your Firestore collection
        db.collection("jobs").document(jobID)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.e("DetailinfoJob", "Listen failed.", e);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {

                        if (documentSnapshot.contains("title")) {
                            String jobTitle = documentSnapshot.getString("title");
                            binding.edtTitle.setText(jobTitle != null ? jobTitle : "");
                        } else {
                            binding.edtTitle.setText("");
                        }

                        if (documentSnapshot.contains("location")) {
                            String jobLocation = documentSnapshot.getString("location");
                            binding.edtLocation.setText(jobLocation != null ? jobLocation : "");
                        } else {
                            binding.edtLocation.setText("");
                        }

                        // Check and fetch job description
                        if (documentSnapshot.contains("description")) {
                            String description = documentSnapshot.getString("description");
                            binding.edtDescription.setText(description != null ? description : "");
                        } else {
                            binding.edtDescription.setText("");
                        }

                        // Check and fetch salary min
                        if (documentSnapshot.contains("salaryMin")) {
                            String salaryMin = documentSnapshot.getDouble("salaryMin") + "";
                            binding.edtSalaryMin.setText(salaryMin != null ? salaryMin : "");
                        } else {
                            binding.edtSalaryMin.setText("");
                        }

                        // Check and fetch salary max
                        if (documentSnapshot.contains("salaryMax")) {
                            String salaryMax = documentSnapshot.getDouble("salaryMax") + "";
                            binding.edtSalaryMax.setText(salaryMax != null ? salaryMax : "");
                        } else {

                            binding.edtSalaryMax.setText("");
                        }


                        if (documentSnapshot.contains("startTime")) {
                            binding.etDateStart.setText(documentSnapshot.getString("startTime"));
                        } else {
                            binding.etDateStart.setText("");
                        }


                        if (documentSnapshot.contains("endTime")) {
                            binding.etDateEnd.setText(documentSnapshot.getString("endTime"));
                        } else {
                            binding.etDateEnd.setText("");
                        }


                        if (documentSnapshot.contains("major")) {
                            String major = documentSnapshot.getString("major");
                            //     String major = ma.toString();
                            ArrayAdapter<String> adapter = (ArrayAdapter<String>) binding.edtField.getAdapter();
                            int position = adapter.getPosition(major);
                            if (position >= 0) {
                                binding.edtField.setSelection(position);
                            } else {
                                major = "khác";
                                int positionn = adapter.getPosition(major);
                                binding.edtField.setSelection(positionn);
                            }
                        } else {

                            binding.etDateEnd.setText("N/A");
                        }


                    } else {
                    }
                });
    }


}
