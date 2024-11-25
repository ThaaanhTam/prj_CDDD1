package com.example.hotrovieclam.Fragment;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotrovieclam.Activity.JobDetailMain;
import com.example.hotrovieclam.Adapter.AdapterListJobSave;
import com.example.hotrovieclam.Adapter.MyRecyclerViewAdapter;
import com.example.hotrovieclam.Connect.API;
import com.example.hotrovieclam.Connect.Website;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentSaveJobBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Save_job extends Fragment {
    private FragmentSaveJobBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Job> listJob;
    private AdapterListJobSave adapter;
    private UserSessionManager user = new UserSessionManager();
    private String uid = null;
    private Set<String> savedJobIds; // Khởi tạo Set lưu các jobId đã lưu
    private MyRecyclerViewAdapter adapterr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSaveJobBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Khởi tạo Firestore, ArrayList, Set và Adapter
        db = FirebaseFirestore.getInstance();
        listJob = new ArrayList<>();
        savedJobIds = new HashSet<>(); // Khởi tạo Set để chứa các jobId đã lưu
        adapter = new AdapterListJobSave(getActivity(), listJob);

        // Thiết lập RecyclerView
        binding.listJobSave.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listJobSave.setAdapter(adapter);

        Website websiteLoader = new Website();
        websiteLoader.loadWebsitesConcurrentlySequentiallysave(adapter, listJob);
        API apiLoader = new API();
        apiLoader.loadAPIsConcurrentlysave(adapter, listJob);

        // Lấy UID từ UserSessionManager
        uid = user.getUserUid();

        // Lấy danh sách jobId đã lưu và sau đó lấy dữ liệu job tương ứng từ Firestore
        getAllSavedJobIds(uid);


        return view;
    }

//    private void setRecycleClick(){
//        adapter.setRecycleClick(new MyRecyclerViewAdapter.OnItemClick() {
//            @Override
//            public void DetailClick(String SourceID, String jobID,Job job) {
//                //     Log.d("Click", "DetailClick: " + "jodID" + jobID);
//                //Detail_Job detailJob = new Detail_Job();
//                Intent intent = new Intent(getContext(), JobDetailMain.class);
//
//                intent.putExtra("jobID", jobID);
//                intent.putExtra("sourceId", job.getSourceId());
//
//                Log.d(TAG, "DetailClick: "+job);
//                intent.putExtra("KEY_NAME", job);
//                startActivity(intent);
//            }
//        });
//    }
    // Hàm để lấy tất cả các jobId đã lưu
    public void getAllSavedJobIds(String uid) {
        try {
            db.collection("users")
                    .document(uid)
                    .collection("role")
                    .document("candidate")
                    .collection("saveJob")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String jobId = document.getId();
                                savedJobIds.add(jobId); // Thêm jobId vào Set
                                Log.d("JobId", "Job ID: " + jobId);
                            }
                            fetchSavedJobsFromFirestore(); // Lấy job tương ứng sau khi có danh sách jobId
                        } else {
                            Log.d("JobId", "Failed with: ", task.getException());
                        }
                    });
        } catch (Exception e) {
            Log.e("getAllSavedJobIds", "Error fetching saved job IDs", e);
        }
    }

    // Hàm để lấy các Job từ Firestore dựa trên các jobId đã lưu
    private void fetchSavedJobsFromFirestore() {
        try {
            db.collection("jobs").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String jobId = document.getId();
                        if (savedJobIds.contains(jobId)) { // Kiểm tra nếu jobId thuộc các job đã lưu
                            Job job = document.toObject(Job.class);
                            job.setId(jobId); // Gán jobId cho Job object
                            listJob.add(job); // Thêm job vào danh sách hiển thị
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.w("fetchJobsFromFirestore", "Error getting documents.", task.getException());
                }
            });
        } catch (Exception e) {
            Log.e("fetchJobsFromFirestore", "Error fetching saved jobs", e);
        }
    }
}