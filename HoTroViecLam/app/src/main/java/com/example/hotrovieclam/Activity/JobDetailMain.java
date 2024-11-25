package com.example.hotrovieclam.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.RadialGradient;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Fragment.RecruiterManagement.DetailinfoJob;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityJobDetailMainBinding;
import com.example.hotrovieclam.databinding.FragmentDetailinfoJobBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobDetailMain extends AppCompatActivity {

    private static final String ARG_JOB_ID = "jobID";
    private String jobID;
    private int sourceId;
    private FirebaseFirestore db;
    private ArrayList<Job> listJob = new ArrayList<>(); // Khởi tạo listJob
    private Job job;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ActivityJobDetailMainBinding binding;
    boolean isRbLibraryChecked = false;
    UserSessionManager userSessionManager = new UserSessionManager();
    String uid = userSessionManager.getUserUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJobDetailMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lấy jobID từ Intent
        Intent intent = getIntent();
        jobID = intent.getStringExtra(ARG_JOB_ID);
        sourceId = intent.getIntExtra("sourceId", 0);
        Intent i = getIntent();
        job = (Job) i.getSerializableExtra("KEY_NAME");

        Log.d("aaaaaaaaaaaaaaaaaaaaaaaaaaaa", job.toString());

        //Log.e("JobDetailMain", "Received jobIDdddddddddddddddddddddddddddddd: " + jobID);
        if (jobID == null) {
            Log.e("JobDetailMain", "jobID is null. Please provide a valid jobID.");
            finish(); // Đóng Activity nếu jobID không hợp lệ
            return;
        }
        binding.lvGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        db = FirebaseFirestore.getInstance();

        // Gọi phương thức để lấy chi tiết công việc
//        fỉebaseJobDetails();
        if (sourceId == 3) {
            firebaseJobDetails();
        } else {
            Web_APIJobDetails();

        }
        Uri uri = (job.getAvatar() != null && !job.getAvatar().isEmpty()) ? Uri.parse(job.getAvatar()) : Uri.parse("https://123job.vn/images/no_company.png");
        Glide.with(this).load(uri).into(binding.ivLogo);
        // Khởi tạo Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("luuu", "onClick: luuuuu" + jobID);
                Log.d("luuu", "onClick: luuuuu" + uid);

                if (jobID != null && uid != null) {
                    SaveJob(uid, jobID);
                }
            }
        });

        binding.msgImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserSessionManager userSessionManager = new UserSessionManager();
                String uid = userSessionManager.getUserUid();
//                List<Map<String, Object>> messagesArray = new ArrayList<>();
//
//                Map<String, Object> message1 = new HashMap<>();
//                message1.put("senderID", currentUserId);
//                message1.put("content", "Hello, đây là tin nhắn đầu tiên!");
//                message1.put("receiveID", receiverId);
//                messagesArray.add(message1);
//
//// Thêm tin nhắn thứ hai
//                Map<String, Object> message2 = new HashMap<>();
//                message2.put("senderID", receiverId);
//                message2.put("content", "Chào bạn, mình đã nhận được tin nhắn!");
//                message2.put("receiveID", currentUserId);
//                messagesArray.add(message2);
//
//// Thêm trường string bên ngoài (VD: tiêu đề cuộc hội thoại)
//                Map<String, Object> data = new HashMap<>();
//                data.put("conversationTitle", "Cuộc hội thoại giữa User1 và User2"); // Trường string bên ngoài
//                data.put("messages", messagesArray); // Mảng tin nhắn
// Dữ liệu ban đầu


                Map<String, Object> messageData = new HashMap<>();
                messageData.put("conversationTitle", "Cuộc hội thoại giữa User1 và User2");

                List<Map<String, Object>> messagesArray = new ArrayList<>();
                Map<String, Object> message = new HashMap<>();
                message.put("content", "Hello, đây là tin nhắn đầu tiên!");

                messagesArray.add(message);

                messageData.put("messages", messagesArray);

                db.collection("users").document(uid)
                        .collection("conversation")
                        .document(employerId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {

                                Log.d("Firestore", "Conversation đã tồn tại, không tạo lại.");
                            } else {
                                // Chưa tồn tại, tiến hành tạo mới
                                db.collection("Message")
                                        .add(messageData)
                                        .addOnSuccessListener(documentReference -> {
                                            String autoGeneratedId = documentReference.getId(); // Lấy ID tự động tạo
                                            Log.d("Firestore", "Document được tạo với ID: " + autoGeneratedId);

                                            // Tạo conversation trong bảng "users" với ID tin nhắn
                                            db.collection("users").document(uid)
                                                    .collection("conversation")
                                                    .document(employerId)
                                                    .set(Collections.singletonMap("messageID", autoGeneratedId))
                                                    .addOnSuccessListener(aVoid -> {
                                                        Log.d("Firestore", "Đã tạo conversation với ID tin nhắn: " + autoGeneratedId);
                                                    })
                                                    .addOnFailureListener(err -> {
                                                        Log.e("Firestore", "Lỗi khi tạo conversation: " + err.getMessage());
                                                    });
                                            db.collection("users").document(employerId)
                                                    .collection("conversation")
                                                    .document(uid)
                                                    .set(Collections.singletonMap("messageID", autoGeneratedId))
                                                    .addOnSuccessListener(aVoid -> {
                                                        Log.d("Firestore", "Đã tạo conversation với ID tin nhắn: " + autoGeneratedId);
                                                    })
                                                    .addOnFailureListener(err -> {
                                                        Log.e("Firestore", "Lỗi khi tạo conversation: " + err.getMessage());
                                                    });
                                        })
                                        .addOnFailureListener(err -> {
                                            Log.e("Firestore", "Lỗi khi tạo document: " + err.getMessage());
                                        });
                            }
                        })
                        .addOnFailureListener(err -> {
                            Log.e("Firestore", "Lỗi khi kiểm tra conversation: " + err.getMessage());
                        });
            }
        });
        if(job.getEmployerId()!=null) {
            DocumentReference employerDocRef = db.collection("users")
                    .document(job.getEmployerId())
                    .collection("role")
                    .document("employer");
            employerDocRef.get().addOnSuccessListener(document -> {
                if (document != null && document.exists()) {
                    // Lấy trường logo từ tài liệu employer
                    String logoUrl = document.getString("logo");
                    if (logoUrl != null) {
                        Log.d("logoUrl", logoUrl);
                        // Tải ảnh từ Firebase Storage nếu có URL của logo
                        loadImage(storage.getReference(), "images/" + logoUrl, binding.ivLogo);
                    } else {
                        Log.w("Firestore", "Logo field is null");
                    }
                } else {
                    Log.w("Firestore", "Document does not exist or role is not employer");
                }
            }).addOnFailureListener(e -> {
                // Xử lý lỗi nếu có
                Log.e("Firestore", "Error getting document", e);
            });
        }

    }

    private void loadImage(StorageReference storageReference, String path, ImageView imageView) {
        if (path != null && !path.isEmpty()) {
            StorageReference imageRef = storageReference.child(path);
            imageRef.getDownloadUrl()
                    .addOnSuccessListener(uri -> Glide.with(this).load(uri).into(imageView))
                    .addOnFailureListener(e -> Toast.makeText(this, "Không thể tải ảnh", Toast.LENGTH_SHORT).show());
        }
    }

String employerId = "";

private void firebaseJobDetails() {
    // Thay "jobs" bằng tên của collection trong Firestore
    db.collection("jobs").document(jobID)
            .addSnapshotListener((documentSnapshot, e) -> {
                if (e != null) {
                    Log.e("JobDetailMain", "Listen failed.", e);
                    Log.d("JobDetailMainTitle", "dddd");
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    employerId = documentSnapshot.getString("employerId");
                    if (employerId != null) {

                        // Truy vấn người sử dụng từ employerId
                        db.collection("users").document(employerId)
                                .get()
                                .addOnSuccessListener(userSnapshot -> {
                                    if (userSnapshot.exists()) {
                                        // Lấy tên và số điện thoại của nhà tuyển dụng
                                        String email = userSnapshot.getString("email");
                                        String employerPhone = userSnapshot.getString("phoneNumber");

                                        // Hiển thị thông tin
                                        Log.d("EmployerDetails", "Name: " + email);
                                        Log.d("EmployerDetails", "Phone: " + employerPhone);

                                        // Cập nhật UI (ví dụ với TextView)
                                        binding.tvEmail.setText(email != null ? email : "N/A");
                                        binding.tvSDT.setText(employerPhone != null ? employerPhone : "N/A");
                                    } else {
                                        Log.w("EmployerDetails", "No such user document");
                                    }
                                    binding.dangkiungtuyen.setOnClickListener(v -> showUploadDialog());
                                })
                                .addOnFailureListener(es -> Log.e("EmployerDetails", "Error getting user details", e));
                    }

                    Log.d("JobDetailMainTitle", "jobTitle");

                    // Kiểm tra và hiển thị tiêu đề công việc
                    if (documentSnapshot.contains("title")) {
                        String jobTitle = documentSnapshot.getString("title");
                        Log.d("JobDetailMainTitle", jobTitle);
                        binding.tvTitle.setText(jobTitle != null ? jobTitle : "N/A");
                    } else {
                        Log.w("JobDetailMain", "Field 'title' does not exist");
                        binding.tvTitle.setText("N/A");
                    }
                    // Kiểm tra và hiển thị lĩnh vực
                    if (documentSnapshot.contains("major")) {
                        String jobMajor = documentSnapshot.getString("major");
                        binding.tvField.setText(jobMajor != null ? jobMajor : "N/A");
                    } else {
                        Log.w("JobDetailMain", "Field 'title' does not exist");
                        binding.tvField.setText("N/A");
                    }
                    // Kiểm tra và hiển thị địa điểm
                    if (documentSnapshot.contains("major")) {
                        String jobLocation = documentSnapshot.getString("location");
                        binding.tvLocation.setText(jobLocation != null ? jobLocation : "N/A");
                    } else {
                        Log.w("JobDetailMain", "Field 'title' does not exist");
                        binding.tvLocation.setText("N/A");
                    }

                    // Kiểm tra và hiển thị mô tả công việc
                    if (documentSnapshot.contains("description")) {
                        String description = documentSnapshot.getString("description");
                        binding.tvDescription.setText(description != null ? description : "N/A");
                    } else {
                        Log.w("JobDetailMain", "Field 'description' does not exist");
                        binding.tvDescription.setText("N/A");
                    }

                    // Kiểm tra và hiển thị mức lương tối thiểu
                    if (documentSnapshot.contains("salaryMin")) {
                        Double salaryMin = documentSnapshot.getDouble("salaryMin");
                        binding.tvSalaryMin.setText(salaryMin != null ? String.valueOf(salaryMin) : "N/A");
                    } else {
                        Log.w("JobDetailMain", "Field 'salaryMin' does not exist");
                        binding.tvSalaryMin.setText("N/A");
                    }

                    // Kiểm tra và hiển thị mức lương tối đa
                    if (documentSnapshot.contains("salaryMax")) {
                        Double salaryMax = documentSnapshot.getDouble("salaryMax");
                        binding.tvSalaryMax.setText(salaryMax != null ? String.valueOf(salaryMax) : "N/A");
                    } else {
                        Log.w("JobDetailMain", "Field 'salaryMax' does not exist");
                        binding.tvSalaryMax.setText("N/A");
                    }

                } else {
                    Log.d("JobDetailMainTitle", "No such document");
                }


            });
}

private void Web_APIJobDetails() {
    if (job != null) {
        Log.d("aaaaaaaaaaaaaaaaaaaaaaaaaaa", job.getJobURL());
        binding.dangkiungtuyen.setText("Truy cập trang web");
        binding.msgImg.setVisibility(View.GONE);
        binding.emailSDT.setVisibility(View.GONE);
        // Thiết lập tiêu đề công việc
        binding.tvTitle.setText(job.getTitle() != null ? job.getTitle() : "N/A");

        // Thiết lập lĩnh vực công việc
        binding.tvField.setText(job.getMajor() != null ? job.getMajor() : "N/A");

        // Thiết lập mô tả công việc
        binding.tvDescription.setText(job.getDescription() != null ? job.getDescription() : "N/A");
        binding.tvLocation.setText(job.getLocation() != null ? job.getLocation() : "N/A");
        // Thiết lập mức lương tối thiểu
        if (job.getSalaryMin() != -1.0f) {
            binding.tvSalaryMin.setText(String.valueOf(job.getSalaryMin()));
        } else {
            binding.tvSalaryMin.setText("Thỏa thuận");
        }

        // Thiết lập mức lương tối đa
        if (job.getSalaryMax() != -1.0f) {
            binding.tvSalaryMax.setText(String.valueOf(job.getSalaryMax() + "  triệu"));
        } else {
            binding.tvSalaryMax.setText("");
        }
        binding.dangkiungtuyen.setOnClickListener(v -> {
            String jobURL = job.getJobURL();
            if (jobURL != null && !jobURL.isEmpty()) {
                // Kiểm tra URL hợp lệ và mở trình duyệt
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(jobURL));
                startActivity(intent);
            } else {
                Log.e("Web_APIJobDetails", "Invalid job URL.");
                // Có thể hiển thị thông báo hoặc xử lý khi URL không hợp lệ
            }
        });
    } else {
        Log.e("Web_APIJobDetails", "Job object is null.");
        // Thiết lập các giá trị mặc định nếu job không tồn tại
        binding.tvTitle.setText("N/A");
        binding.tvField.setText("N/A");
        binding.tvDescription.setText("N/A");
        binding.tvSalaryMin.setText("Thỏa thuận");
        binding.tvSalaryMax.setText("N/A");
    }
}


private void showUploadDialog() {
    final Dialog dialog = new Dialog(this);
    //  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.upload_cv);
    // Thiết lập kích thước dialog
    Window window = dialog.getWindow();
    if (window != null) {
        //   window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    // Khởi tạo các view
    RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
    LinearLayout cvLibraryLayout = dialog.findViewById(R.id.cvLibraryLayout);
    LinearLayout uploadLayout = dialog.findViewById(R.id.uploadLayout);
    Button btnApply = dialog.findViewById(R.id.btnApply);
    RadioButton rbLibrary = dialog.findViewById(R.id.rbLibrary);
    RadioButton rbUpload = dialog.findViewById(R.id.rbUpload);
    LinearLayout file = dialog.findViewById(R.id.file);
    TextView tvname = dialog.findViewById(R.id.tvName);
    TextView tvemail = dialog.findViewById(R.id.tvEmail);
    TextView tvsdt = dialog.findViewById(R.id.tvSDT);
    TextView seeCV = dialog.findViewById(R.id.seeCV);


    uploadLayout.setVisibility(View.GONE);
    cvLibraryLayout.setVisibility(View.GONE);
    rbLibrary.setOnClickListener(v -> {
        rbLibrary.setChecked(true);
        rbUpload.setChecked(false);
        cvLibraryLayout.setVisibility(View.VISIBLE);
        uploadLayout.setVisibility(View.GONE);
        isRbLibraryChecked = true;
    });
    rbUpload.setOnClickListener(v -> {
        rbUpload.setChecked(true);
        rbLibrary.setChecked(false);
        uploadLayout.setVisibility(View.VISIBLE);
        cvLibraryLayout.setVisibility(View.GONE);
        isRbLibraryChecked = true;
    });

    UserSessionManager sessionManager = new UserSessionManager();
    String uid = sessionManager.getUserUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef = db.collection("users").document(uid);

// Lắng nghe sự thay đổi trong tài liệu người dùng
    docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(@Nullable DocumentSnapshot document, @Nullable FirebaseFirestoreException error) {
            if (error != null) {
                // Xử lý lỗi nếu có
                Log.d("Firestore", "Lỗi khi nghe thay đổi dữ liệu: ", error);
                return;
            }

            if (document != null && document.exists()) {
                // Lấy thông tin người dùng từ tài liệu
                String name = document.getString("name");
                String email = document.getString("email");
                String phonenumber = document.getString("phoneNumber");

                // Hiển thị thông tin người dùng trên giao diện
                tvname.setText(name);
                tvemail.setText(email);
                tvsdt.setText(phonenumber);

                // Ghi log thông tin người dùng để kiểm tra
                Log.d("PPPP", "onEvent: " + email + " " + name);
            } else {
                // Thông báo nếu không tìm thấy dữ liệu người dùng
                Log.d("Firestore", "Không tìm thấy dữ liệu người dùng.");
            }
        }
    });

// Sự kiện khi nhấn vào button "seeCV"
    seeCV.setOnClickListener(v -> {
        Intent intent = new Intent(this, cv_template.class);
        startActivity(intent);
    });


    // Xử lý sự kiện khi click vào layout upload
    uploadLayout.setOnClickListener(v -> {
        openFilePicker();
    });


    btnApply.setOnClickListener(v -> {
        // Kiểm tra xem đã chọn phương thức ứng tuyển chưa
        if (!rbUpload.isChecked() && !rbLibrary.isChecked()) {
            new AlertDialog.Builder(this)
                    .setTitle("Thông báo")
                    .setMessage("Vui lòng chọn phương thức ứng tuyển")
                    .setPositiveButton("OK", (xdialog, which) -> dialog.dismiss())
                    .show();
            return;
        }

        UserSessionManager userSessionManager = new UserSessionManager();
        String uida = userSessionManager.getUserUid();

        // Kiểm tra đã ứng tuyển chưa
        db.collection("jobs")
                .document(jobID)
                .collection("application")
                .whereEqualTo("candidateId", uida)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Nếu đã có application, chỉ cập nhật CV
                        DocumentSnapshot application = querySnapshot.getDocuments().get(0);
                        String applicationId = application.getId();

                        if (rbUpload.isChecked()) {
                            if (selectedFileUri != null) {
                                uploadCV(applicationId);
                            } else {
                                Toast.makeText(this, "Vui lòng chọn CV để upload", Toast.LENGTH_SHORT).show();
                            }
                        } else if (rbLibrary.isChecked()) {
                            // Cập nhật CV từ thư viện
                            db.collection("jobs")
                                    .document(jobID)
                                    .collection("application")
                                    .document(applicationId)
                                    .update("cv_file", null) // selectedLibraryCV là URL CV từ thư viện
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Cập nhật CV thành công!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Lỗi khi cập nhật CV: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // Chưa ứng tuyển - Tạo application mới
                        Map<String, Object> newData = new HashMap<>();
                        newData.put("candidateId", uida);
                        newData.put("status", -1);
                        newData.put("cv_file", null);
                        newData.put("applicationDate", new Date());

                        db.collection("jobs")
                                .document(jobID)
                                .collection("application")
                                .add(newData)
                                .addOnSuccessListener(documentReference -> {
                                    if (rbUpload.isChecked()) {
                                        if (selectedFileUri != null) {
                                            uploadCV(documentReference.getId());
                                        } else {
                                            Toast.makeText(this, "Đã ứng tuyển! Vui lòng cập nhật CV sau",
                                                    Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    } else if (rbLibrary.isChecked()) {
                                        // Cập nhật CV từ thư viện cho application mới
                                        documentReference.update("cv_file", null)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(this, "Ứng tuyển thành công!",
                                                            Toast.LENGTH_SHORT).show();
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(this, "Lỗi khi cập nhật CV: " + e.getMessage(),
                                                            Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Lỗi khi ứng tuyển: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi kiểm tra ứng tuyển: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    });

// Hàm lưu đơn ứng tuyển với CV từ thư viện

    dialog.show();
}

private void uploadCV(String applicationId) {
    ProgressDialog progressDialog = new ProgressDialog(this);
    progressDialog.setTitle("Đang xử lý");
    progressDialog.setMessage("Đang tải CV lên...");
    progressDialog.setCancelable(false);
    progressDialog.show();

    String fileName = "cv_" + System.currentTimeMillis() + ".pdf";
    StorageReference fileRef = storage.getReference().child("cvs/" + fileName);

    fileRef.putFile(selectedFileUri)
            .addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Cập nhật URL của CV trong đơn ứng tuyển
                    db.collection("jobs")
                            .document(jobID)
                            .collection("application")
                            .document(applicationId)
                            .update("cv_file", uri.toString())
                            .addOnSuccessListener(aVoid -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Ứng tuyển thành công!",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Lỗi khi cập nhật URL CV: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                });
            })
            .addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Lỗi khi tải CV: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            })
            .addOnProgressListener(snapshot -> {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressDialog.setMessage("Đang tải CV lên: " + (int) progress + "%");
            });
}

private void SaveJob(String uid, String idJob) {
    try {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tham chiếu đến document của công việc
        db.collection("users")
                .document(uid)
                .collection("role")
                .document("candidate")
                .collection("saveJob")
                .document(idJob)
                .get()
                .addOnCompleteListener(task -> {
                    try {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Nếu document tồn tại, hiển thị thông báo rằng công việc đã được lưu
                                new AlertDialog.Builder(this)
                                        .setTitle("Thông báo")
                                        .setMessage("Công việc đã được lưu")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Xử lý khi nhấn nút OK
                                                dialog.dismiss(); // Đóng dialog
                                            }
                                        })
                                        .show();
                            } else {
                                // Nếu document chưa tồn tại, lưu công việc vào Firestore
                                Map<String, Object> jobData = new HashMap<>();
                                jobData.put("idJob", idJob);

                                db.collection("users")
                                        .document(uid)
                                        .collection("role")
                                        .document("candidate")
                                        .collection("saveJob")
                                        .document(idJob)
                                        .set(jobData)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("SaveJob", "Job saved successfully with ID: " + idJob);
                                            new AlertDialog.Builder(this)
                                                    .setTitle("Thông báo")
                                                    .setMessage("Lưu công việc thành công!")
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // Xử lý khi nhấn nút OK
                                                            dialog.dismiss(); // Đóng dialog
                                                        }
                                                    })
                                                    .show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("SaveJob", "Error saving job", e);
                                            Toast.makeText(this, "Lỗi khi lưu công việc", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            throw new Exception("Lỗi khi kiểm tra công việc: " + task.getException());
                        }
                    } catch (Exception e) {
                        Log.e("SaveJob", "Error in Firestore operation", e);
                        Toast.makeText(this, "Đã xảy ra lỗi khi lưu công việc", Toast.LENGTH_SHORT).show();
                    }
                });
    } catch (Exception e) {
        Log.e("SaveJob", "Unexpected error", e);
        Toast.makeText(this, "Đã xảy ra lỗi không mong muốn", Toast.LENGTH_SHORT).show();
    }
}

private Uri selectedFileUri; // Biến để lưu URI của file được chọn

// Mở file picker khi click vào nút chọn CV
private void openFilePicker() {
    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("*/*");
    String[] mimeTypes = {
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    };
    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
    intent.addCategory(Intent.CATEGORY_OPENABLE);

    try {
        pdfPicker.launch(Intent.createChooser(intent, "Chọn CV"));
    } catch (ActivityNotFoundException ex) {
        Toast.makeText(this, "Vui lòng cài đặt File Manager", Toast.LENGTH_SHORT).show();
    }
}

// Xử lý kết quả chọn file
private final ActivityResultLauncher<Intent> pdfPicker = registerForActivityResult(

        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                    final Dialog dialog = new Dialog(this);
//                    TextView tvSelectedFile = dialog.findViewById(R.id.tvSelectedFile);
                selectedFileUri = result.getData().getData(); // Chỉ lưu URI, chưa upload
                // Có thể hiện tên file đã chọn
                String fileName = getFileName(selectedFileUri);
                //  tvSelectedFile.setText("File đã chọn: " + fileName);
            }
        }
);

// Hàm lấy tên file từ URI
private String getFileName(Uri uri) {
    String result = null;
    if (uri.getScheme().equals("content")) {
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index != -1) {
                    result = cursor.getString(index);
                }
            }
        }
    }
    if (result == null) {
        result = uri.getPath();
        int cut = result.lastIndexOf('/');
        if (cut != -1) {
            result = result.substring(cut + 1);
        }
    }
    return result;
}

private String cvDownloadUrl;

// Hàm lưu thông tin ứng tuyển vào Firestore

@Override
protected void onDestroy() {
    super.onDestroy();
    binding = null;
}
}