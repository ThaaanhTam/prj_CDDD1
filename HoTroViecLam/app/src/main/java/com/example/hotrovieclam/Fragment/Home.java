package com.example.hotrovieclam.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hotrovieclam.Adapter.MyRecyclerViewAdapter;
import com.example.hotrovieclam.Connect.API;
import com.example.hotrovieclam.Connect.Website;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Home extends Fragment {
    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Job> listJob;
    private MyRecyclerViewAdapter adapter;
    ExecutorService executorService = Executors.newFixedThreadPool(2);
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

        // Khởi tạo danh sách công việc và adapter
        listJob = new ArrayList<>();
        adapter = new MyRecyclerViewAdapter(getActivity(), listJob);

        // Thiết lập RecyclerView
        binding.jobList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.jobList.setAdapter(adapter);

        Runnable task1 = () -> {
            API api = new API();
            listJob.addAll(api.loadAPIsConcurrently());
            requireActivity().runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
            });
        };
        Runnable task2 = () -> {
            Website website = new Website();
            listJob.addAll(website.loadWebsitesConcurrently());

            requireActivity().runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
            });

        };
        executorService.submit(task1);
        executorService.submit(task2);


        // Lấy dữ liệu từ Firestore
        fetchJobsFromFirestore();




        // Thêm listener cho touch trên search bar
        binding.searchBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Kiểm tra nếu người dùng nhấn vào drawable bên trái
                    if (event.getRawX() <= (binding.searchBar.getCompoundDrawables()[0].getBounds().width())) {


                        String searchText = binding.searchBar.getText().toString();
                        Log.d("SearchInput", "Search text: " + searchText);

                        adapter = new MyRecyclerViewAdapter(getActivity(),  performSearch(searchText));
                        // Gọi hàm tìm kiếm

                        binding.jobList.setAdapter(adapter);
                        // Gọi hàm tìm kiếm
                        requireActivity().runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                        });
                        return true; // Đã xử lý sự kiện
                    }
                }
                return false; // Không xử lý sự kiện
            }
        });
    }

    private ArrayList<Job> performSearch(String query) {
        if (query.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            return listJob;  // Trả về toàn bộ danh sách nếu chuỗi tìm kiếm rỗng
        }

        ArrayList<Job> filteredList = new ArrayList<>();
        for (Job job : listJob) {
            if (job.getTitle().toUpperCase().contains(query.toUpperCase())) {
                filteredList.add(job);
            }
        }
        return filteredList;
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
