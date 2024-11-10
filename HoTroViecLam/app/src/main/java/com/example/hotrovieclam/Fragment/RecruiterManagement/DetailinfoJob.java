package com.example.hotrovieclam.Fragment.RecruiterManagement;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentDetailinfoJobBinding; // Import binding class
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailinfoJob extends Fragment {

    private static final String ARG_JOB_ID = "jobID";
    private String jobID;
    private FirebaseFirestore db;
    private FragmentDetailinfoJobBinding binding;

    public DetailinfoJob() {
    }

    public static DetailinfoJob newInstance(String jobID) {
        DetailinfoJob fragment = new DetailinfoJob();
        Bundle args = new Bundle();
        args.putString(ARG_JOB_ID, jobID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jobID = getArguments().getString("jobID");
            Log.d("DetailinfoJob", "Received jobID: " + jobID);
        }
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDetailinfoJobBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Call a method to fetch job details using jobID
        fetchJobDetails();

        return view;
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
                            binding.tvTitle.setText(jobTitle != null ? jobTitle : "N/A");
                        } else {
                            Log.w("DetailinfoJob", "Field 'title' does not exist");
                            binding.tvTitle.setText("N/A");
                        }

                        // Check and fetch job description
                        if (documentSnapshot.contains("description")) {
                            String description = documentSnapshot.getString("description");
                            binding.tvDescription.setText(description != null ? description : "N/A");
                        } else {
                            Log.w("DetailinfoJob", "Field 'description' does not exist");
                            binding.tvDescription.setText("N/A");
                        }

                        // Check and fetch salary min
                        if (documentSnapshot.contains("salaryMin")) {
                            String salaryMin = documentSnapshot.getDouble("salaryMin") + "";
                            binding.tvSalaryMin.setText(salaryMin != null ? salaryMin : "N/A");
                        } else {
                            Log.w("DetailinfoJob", "Field 'salaryMin' does not exist");
                            binding.tvSalaryMin.setText("N/A");
                        }

                        // Check and fetch salary max
                        if (documentSnapshot.contains("salaryMax")) {
                            String salaryMax = documentSnapshot.getDouble("salaryMax") + "";
                            binding.tvSalaryMax.setText(salaryMax != null ? salaryMax : "N/A");
                        } else {
                            Log.w("DetailinfoJob", "Field 'salaryMax' does not exist");
                            binding.tvSalaryMax.setText("N/A");
                        }

                    } else {
                        Log.d("DetailinfoJob", "No such document");
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
