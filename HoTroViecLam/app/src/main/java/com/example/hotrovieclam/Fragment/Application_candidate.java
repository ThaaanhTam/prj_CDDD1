package com.example.hotrovieclam.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Adapter.AplicationAdapter;
import com.example.hotrovieclam.Model.Candidate;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityApplicationCandidateBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Application_candidate extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference jobsRef = db.collection("jobs");
    private ActivityApplicationCandidateBinding binding;
    private AplicationAdapter adapter;
    private List<Candidate> candidateList;
    private UserSessionManager userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Thiết lập giao diện
        EdgeToEdge.enable(this);
        binding = ActivityApplicationCandidateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Cài đặt hệ thống thanh trạng thái
        setupSystemBars();

        // Khởi tạo quản lý phiên người dùng
       // userSessionManager = new UserSessionManager();

        // Cài đặt RecyclerView
        setupRecyclerView();

        // Lắng nghe sự kiện jobs
        //listenToJobUpdates();
    }

    private void setupSystemBars() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.title));

        // Nút quay lại
        binding.lvGoBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        // Cấu hình RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Cấu hình GIF loading
        Glide.with(this)
                .asGif()
                .load(R.drawable.angry)
                .override(500, 500)
                .into(binding.tvGif);
        binding.tvGif.setVisibility(View.VISIBLE);

        // Khởi tạo danh sách và adapter
        candidateList = new ArrayList<>();
        adapter = new AplicationAdapter(this, candidateList);
        binding.recyclerView.setAdapter(adapter);
    }

    private void listenToJobUpdates() {
        jobsRef.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Lỗi lắng nghe cập nhật việc làm", e);
                showErrorState();
                return;
            }

            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                for (DocumentSnapshot jobDoc : querySnapshot.getDocuments()) {
                    String jobID = jobDoc.getId();
                    fetchApplications(jobID, jobDoc.getString("title"));
                }
            } else {
                Log.w("Firestore", "Không tìm thấy việc làm");
                showEmptyState();
            }
        });
    }

    private void fetchApplications(String jobID, String title) {
        String uid = userSessionManager.getUserUid();

        // Kiểm tra uid
        if (TextUtils.isEmpty(uid)) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem ứng tuyển", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tham chiếu đến collection application
        CollectionReference applicationsRef = db.collection("jobs")
                .document(jobID)
                .collection("application");

        applicationsRef.whereEqualTo("candidateId", uid)
                .addSnapshotListener((querySnapshot, e) -> {
                    // Xử lý lỗi
                    if (e != null) {
                        Log.e("Firestore", "Lỗi tải dữ liệu ứng dụng", e);
                        showErrorState();
                        return;
                    }

                    // Xóa danh sách cũ
                    candidateList.clear();

                    // Xử lý dữ liệu
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            try {
                                Candidate candidate = createCandidateFromDocument(document, title, jobID);
                                candidateList.add(candidate);
                                Log.d("ac", candidateList.size()+"");
                            } catch (Exception ex) {
                                Log.e("Firestore", "Lỗi chuyển đổi dữ liệu", ex);
                            }
                        }
                    }

                    updateUIAfterFetch();
                });
    }

    private Candidate createCandidateFromDocument(DocumentSnapshot document, String title, String jobID) {
        String applicationID = document.getId();
        Double status = document.getDouble("status");

        Candidate candidate = new Candidate();
        candidate.setId(applicationID);
        candidate.setJobID(jobID);
        candidate.setTitle(title);
        candidate.setStatus(determineStatus(status));

        return candidate;
    }

    private String determineStatus(Double status) {
        if (status == null) return "Đang xét duyệt";

        switch (status.intValue()) {
            case 0: return "Từ chối";
            case 1: return "Đã duyệt";
            default: return "Đang xét duyệt";
        }
    }

    private void updateUIAfterFetch() {


        // Điều chỉnh hiển thị
        if (candidateList.isEmpty()) {
            binding.tvGif.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Không có đơn ứng tuyển", Toast.LENGTH_SHORT).show();
        } else {
            binding.tvGif.setVisibility(View.GONE);
        }
    }

    private void showErrorState() {
        candidateList.clear();
        adapter.notifyDataSetChanged();
        binding.tvGif.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Lỗi tải dữ liệu. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
    }

    private void showEmptyState() {
        candidateList.clear();
        adapter.notifyDataSetChanged();
        binding.tvGif.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Không có việc làm", Toast.LENGTH_SHORT).show();
    }
}