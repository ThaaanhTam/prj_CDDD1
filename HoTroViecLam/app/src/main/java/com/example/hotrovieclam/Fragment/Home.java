package com.example.hotrovieclam.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hotrovieclam.Adapter.MyRecyclerViewAdapter;
import com.example.hotrovieclam.Interface.UserSession;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class Home extends Fragment {
    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Job> listJob;
    private MyRecyclerViewAdapter adapter;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
UserSession userSession = new UserSessionManager();
String uid = userSession.getUserUid();
        Log.d("if", "onViewCreated: "+uid);
        // Khởi tạo danh sách công việc và adapter
        listJob = new ArrayList<>();
        adapter = new MyRecyclerViewAdapter(getActivity(), listJob);

        // Thiết lập RecyclerView
        binding.jobList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.jobList.setAdapter(adapter);

        // Lấy dữ liệu từ Firestore
        fetchJobsFromFirestore();

        // Thêm listener cho touch trên search bar
        binding.searchBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Kiểm tra nếu người dùng nhấn vào drawable bên trái
                    if (event.getRawX() <= (binding.searchBar.getCompoundDrawables()[0].getBounds().width())) {
                        // Người dùng đã nhấn vào drawable bên trái
                        String searchText = binding.searchBar.getText().toString();
                        Log.d("SearchInput", "Search text: " + searchText);
                        performSearch(searchText); // Gọi hàm tìm kiếm
                        return true; // Đã xử lý sự kiện
                    }
                }
                return false; // Không xử lý sự kiện
            }
        });
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo truy vấn Firestore
        db.collection("jobs") // Thay đổi tên collection nếu cần
                .orderBy("title") // Có thể thay đổi theo trường bạn muốn tìm kiếm
                .startAt(query) // Bắt đầu tìm kiếm từ chuỗi nhập
                .endAt(query + "\uf8ff") // Tìm kiếm mọi thứ kết thúc bằng query
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Job> jobList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Job job = document.toObject(Job.class); // Chuyển đổi tài liệu thành đối tượng Job
                            jobList.add(job);
                        }
                        // Cập nhật adapter với danh sách việc làm đã tìm thấy
                        adapter.updateList(jobList); // Cập nhật danh sách hiển thị trong RecyclerView
                    } else {
                        Log.d("SSS", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void fetchJobsFromFirestore() {
        db.collection("jobs")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Job job = document.toObject(Job.class); // Chuyển đổi document thành đối tượng Job
                            listJob.add(job); // Thêm vào danh sách
                        }
                        adapter.notifyDataSetChanged(); // Cập nhật adapter
                    } else {
                        Log.w("HomeFragment", "Error getting documents.", task.getException());
                    }
                });
    }
}