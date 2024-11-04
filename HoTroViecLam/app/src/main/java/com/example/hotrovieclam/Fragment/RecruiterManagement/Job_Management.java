package com.example.hotrovieclam.Fragment.RecruiterManagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotrovieclam.Adapter.JobManagementAdapter;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.databinding.FragmentJobManagementBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class Job_Management extends Fragment {



    public Job_Management() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private FirebaseFirestore db;

    FragmentJobManagementBinding binding;
    JobManagementAdapter adapter;
    ArrayList<Job> jobs = new ArrayList<>();
    UserSessionManager userSessionManager = new UserSessionManager();
    String uid = userSessionManager.getUserUid();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentJobManagementBinding.inflate(inflater, container, false);
        //Job job = new Job();
       // job.setTitle("fffff");
       // jobs.add(job);
        adapter = new JobManagementAdapter((FragmentActivity) getContext(), jobs);
        binding.jobList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.jobList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        fetchJobsFromFirestore();


       binding.btnPostJob.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getContext(), PostJob.class);
               startActivity(intent);
           }
       });


adapter.setRecycleClick(new JobManagementAdapter.OnItemClick() {
    @Override
    public void DetailClick(View view, int position, String jobID) {
        Log.d("Click", "DetailClick: "+ position);
        //Detail_Job detailJob = new Detail_Job();
        Intent intent = new Intent(getContext(), Detail_Job.class);
        intent.putExtra("jobID", jobID);
        startActivity(intent);
    }

    @Override
    public void EditClick(View view, int position, String jobID) {
        Log.d("Click", "EditClick: "+ position);

    }

    @Override
    public void DeleteClick(View view, int position, String jobID) {

        Log.d("Click", "DeleteClick: "+ position);
    }
});


        return binding.getRoot();
    }
    private void fetchJobsFromFirestore() {
        db = FirebaseFirestore.getInstance();
        db.collection("jobs").addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                Log.w("HomeFragment", "Listen failed.", error);
                return;
            }

            if (snapshots != null && !snapshots.isEmpty()) {
                jobs.clear();
                for (QueryDocumentSnapshot document : snapshots) {

                    Job job = document.toObject(Job.class);
       //             Log.d("mm", job.getId());
//                    Log.d("nn", uid);
                    if (job.getEmployerId().equals(uid)) {
                        job.setId(document.getId());
                        jobs.add(job);

                    }

                }
                adapter.notifyDataSetChanged();
            } else {
                Log.d("HomeFragment", "No current data in the collection");
            }
        });
    }

}