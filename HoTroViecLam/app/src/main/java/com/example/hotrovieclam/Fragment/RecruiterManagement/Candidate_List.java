package com.example.hotrovieclam.Fragment.RecruiterManagement;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hotrovieclam.Adapter.CandidateAdapter;
import com.example.hotrovieclam.Model.User;
import com.example.hotrovieclam.databinding.FragmentCandidateListBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class Candidate_List extends Fragment {

    private String jobID;
    private FirebaseFirestore db;
    private FragmentCandidateListBinding binding;
    private CandidateAdapter adapter;
    private ArrayList<User> users = new ArrayList<>();

    public Candidate_List() {
        // Required empty public constructor
    }

    public static Candidate_List newInstance(String jobID) {
        Candidate_List fragment = new Candidate_List();
        Bundle args = new Bundle();
        args.putString("jobID", jobID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jobID = getArguments().getString("jobID");
        }
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCandidateListBinding.inflate(inflater, container, false);

        // Initialize RecyclerView and Adapter
        adapter = new CandidateAdapter(getActivity(), users);
        binding.candidateList.setLayoutManager(new GridLayoutManager(getContext(), 2));

        binding.candidateList.setAdapter(adapter);

        // Fetch candidates in real-time
        fetchCandidatesRealtime();

        return binding.getRoot();
    }

    private void fetchCandidatesRealtime() {
        if (jobID == null) {
            Log.e("Candidate_List", "Job ID is null, cannot fetch candidates.");
            return;
        }

        db.collection("jobs")
                .document(jobID)
                .collection("application")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Listen failed.", e);
                        return;
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        users.clear();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String candidateId = document.getString("candidateId");

                            if (candidateId != null) {
                                fetchUserDetails(candidateId);
                            }
                        }
                    } else {
                        Log.d("Candidate_List", "No applications found.");
                    }
                });
    }

    private void fetchUserDetails(String candidateId) {
        db.collection("users")
                .document(candidateId)
                .addSnapshotListener((userDocument, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "User details fetch failed", e);
                        return;
                    }

                    if (userDocument != null && userDocument.exists()) {
                        User user = new User();
                       // if (user != null) {
                            user.setName(userDocument.getString("name"));
                            user.setAvatar(userDocument.getString("avatar"));
                            users.add(user);
                            adapter.notifyDataSetChanged();
                     //   }
                    } else {

                        Log.d("Candidate_List", "No such user document for candidate ID: " + candidateId);
                    }
                });
    }
}
