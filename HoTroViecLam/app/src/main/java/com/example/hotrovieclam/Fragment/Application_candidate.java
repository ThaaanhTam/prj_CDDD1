package com.example.hotrovieclam.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference jobsRef = db.collection("jobs");
    ActivityApplicationCandidateBinding binding;
    private AplicationAdapter adapter;
    private List<Candidate> candidateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityApplicationCandidateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Glide.with(this)
                .asGif()
                .load(R.drawable.angry)
                .override(500, 500)  // Điều chỉnh kích thước để đảm bảo không bị treo
                .into(binding.tvGif);
         binding.tvGif.setVisibility(View.VISIBLE);
        candidateList = new ArrayList<>();
        adapter = new AplicationAdapter(this,candidateList);
        binding.recyclerView.setAdapter(adapter);

        jobsRef.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.w("Firestore", "Error listening for job updates: ", e);
                return;
            }

            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                for (DocumentSnapshot jobDoc : querySnapshot.getDocuments()) {
                    String jobID = jobDoc.getId();
                    fetchApplications(jobID, jobDoc.getString("title"));
                }
            } else {
                Log.w("Firestore", "No jobs found");
            }
        });
    }

    UserSessionManager userSessionManager = new UserSessionManager();
    String uid = userSessionManager.getUserUid();

    private void fetchApplications(String jobID, String title) {
        CollectionReference applicationsRef = db.collection("jobs")
                .document(jobID)  // Đến công việc cụ thể
                .collection("application");

        applicationsRef.whereEqualTo("candidateId", uid)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.w("Firestore", "Error listening for application updates: ", e);
                        return;
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        List<Candidate> newCandidates = new ArrayList<>();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String applicationID = document.getId();
                            String applicantName = document.getString("candidateId");
                            Double status = document.getDouble("status");

                            // Create and set candidate data
                            Candidate candidate = new Candidate();
                            if (status != null) {
                                if (status.equals(0.0)) {
                                    candidate.setStatus("Từ chối");
                                } else if (status.equals(1.0)) {
                                    candidate.setStatus("Đã duyệt");
                                } else {
                                    candidate.setStatus("Đang xét duyệt");
                                }
                            } else {
                                candidate.setStatus("Đang xét duyệt");
                            }

                            candidate.setTitle(title);
                            candidate.setId(applicationID);
                            candidate.setJobID(jobID);

                            // Add the candidate to the new list
                            newCandidates.add(candidate);
                            binding.tvGif.setVisibility(View.GONE);
                        }

                        // Update candidate list and notify adapter once
                        candidateList.addAll(newCandidates);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w("Firestore", "No applications found for this job");
                    }
                });
    }
}
