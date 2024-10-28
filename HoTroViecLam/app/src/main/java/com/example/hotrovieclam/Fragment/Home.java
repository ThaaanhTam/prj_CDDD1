package com.example.hotrovieclam.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
    Spinner spinner;

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
        binding.sourceSpinner.setSelection(3);
        binding.line1.setVisibility(View.GONE);
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




        Website websiteLoader = new Website();
        websiteLoader.loadWebsitesConcurrentlySequentially(adapter, listJob);
        API apiLoader = new API();
        apiLoader.loadAPIsConcurrently(adapter, listJob);
        // Lấy dữ liệu từ Firestore
        fetchJobsFromFirestore();

        binding.sourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("ii", "onItemSelected: " + position);

                if (position == 0) {
                    ArrayList<Job> jobAPI = new ArrayList<>();
                    for (Job job : listJob) {
                        if (job.getSourceId() == 1) {
                            jobAPI.add(job);
                        }
                    }
                    adapter = new MyRecyclerViewAdapter(getActivity(), jobAPI);
                    // Gọi hàm tìm kiếm

                    binding.jobList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Log.d("ii", jobAPI.toString());

                } else if (position == 1) {
                        ArrayList<Job> jobWeb = new ArrayList<>();
                        for (Job job : listJob) {
                            if (job.getSourceId() == 2) {
                                jobWeb.add(job);
                            }
                        }
                        adapter = new MyRecyclerViewAdapter(getActivity(), jobWeb);
                        // Gọi hàm tìm kiếm
                        binding.jobList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                } else if (position == 2) {
                    ArrayList<Job> jobFire = new ArrayList<>();
                    for (Job job : listJob) {
                        if (job.getSourceId() == 3) {
                            jobFire.add(job);
                        }
                    }
                    adapter = new MyRecyclerViewAdapter(getActivity(), jobFire);
                    // Gọi hàm tìm kiếm

                    binding.jobList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                else {
                    adapter = new MyRecyclerViewAdapter(getActivity(), listJob);
                    // Gọi hàm tìm kiếm
                    binding.jobList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Lấy dữ liệu từ Firestore
        // Thêm listener cho touch trên search bar
        binding.btnTim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.line1.setVisibility(View.VISIBLE);
                String searchText = binding.searchBar.getText().toString();
                Log.d("SearchInput", "Search text: " + searchText);
                adapter = new MyRecyclerViewAdapter(getActivity(), performSearch(searchText));
                binding.jobList.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                for (Job job : listJob) {
                    // Lấy location của mỗi job
                    String location = job.getLocation();
                    if (location != null) {
                        // Hiển thị location trong Logcat
                        Log.d("JobLocation", "Location: " + location);
                    } else {
                        Log.d("JobLocation", "Location: Không có thông tin địa điểm");
                    }
                }
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
                            Job job = document.toObject(Job.class);
                          job.setSourceId(3);
                           Log.d("test",job.toString());
                            // Chuyển đổi document thành đối tượng Job
                            listJob.add(job); // Thêm vào danh sách
                        }
                        adapter.notifyDataSetChanged(); // Cập nhật adapter
                    } else {
                        Log.w("HomeFragment", "Error getting documents.", task.getException());
                    }
                });
    }
}
