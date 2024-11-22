package com.example.hotrovieclam.Fragment;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hotrovieclam.Activity.JobDetailMain;
import com.example.hotrovieclam.Adapter.AdapterListJobSave;
import com.example.hotrovieclam.Adapter.MyRecyclerViewAdapter;
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
    private Set<String> savedJobIds;
    private MyRecyclerViewAdapter myRecyclerViewAdapter ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSaveJobBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        listJob = new ArrayList<>();
        savedJobIds = new HashSet<>();
        adapter = new AdapterListJobSave(getActivity(), listJob);

        binding.listJobSave.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listJobSave.setAdapter(adapter);

        uid = user.getUserUid();
        getAllSavedJobIds(uid);

        adapter.setbamvaodexemchitietcongviecdaluu(idJob -> {
            // Xử lý xem chi tiết công việc
        });

        adapter.setOnItemDeleteClickListener((jobId, position) -> {
            confirmAndDeleteJob(jobId, position);
        });

        setRecycleClick();
        return view;
    }

    private void setRecycleClick(){
        myRecyclerViewAdapter.setRecycleClick(new MyRecyclerViewAdapter.OnItemClick() {
            @Override
            public void DetailClick(String SourceID, String jobID,Job job) {
                //     Log.d("Click", "DetailClick: " + "jodID" + jobID);
                //Detail_Job detailJob = new Detail_Job();
                Intent intent = new Intent(getContext(), JobDetailMain.class);

                intent.putExtra("jobID", jobID);
                intent.putExtra("sourceId", job.getSourceId());

                Log.d(TAG, "DetailClick: "+job);
                intent.putExtra("KEY_NAME", job);
                startActivity(intent);
            }
        });
    }

    private void getAllSavedJobIds(String uid) {
        db.collection("users")
                .document(uid)
                .collection("role")
                .document("candidate")
                .collection("saveJob")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            savedJobIds.add(document.getId());
                        }
                        fetchSavedJobsFromFirestore();
                    } else {
                        Log.d("JobId", "Failed with: ", task.getException());
                    }
                });
    }

    private void fetchSavedJobsFromFirestore() {
        db.collection("jobs").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String jobId = document.getId();
                    if (savedJobIds.contains(jobId)) {
                        Job job = document.toObject(Job.class);
                        job.setId(jobId);
                        listJob.add(job);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                Log.w("fetchJobsFromFirestore", "Error getting documents.", task.getException());
            }
        });
    }

    private void deleteSavedJob(String jobId, int position) {
        db.collection("users")
                .document(uid)
                .collection("role")
                .document("candidate")
                .collection("saveJob")
                .document(jobId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listJob.remove(position); // Xóa công việc khỏi danh sách hiển thị
                        adapter.notifyItemRemoved(position); // Cập nhật RecyclerView
                        Toast.makeText(getContext(), "Đã xóa công việc thành công", Toast.LENGTH_SHORT).show(); // Thông báo xóa
                        Log.d("DeleteJob", "Job deleted successfully: " + jobId);
                    } else {
                        Log.w("DeleteJob", "Error deleting job: ", task.getException());
                        Toast.makeText(getContext(), "Có lỗi xảy ra khi xóa công việc", Toast.LENGTH_SHORT).show(); // Thông báo lỗi
                    }
                });
    }

    private void confirmAndDeleteJob(String jobId, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa công việc này khỏi danh sách đã lưu không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteSavedJob(jobId, position); // Xóa công việc nếu người dùng xác nhận
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss()) // Đóng hộp thoại nếu hủy
                .show();
    }

}
