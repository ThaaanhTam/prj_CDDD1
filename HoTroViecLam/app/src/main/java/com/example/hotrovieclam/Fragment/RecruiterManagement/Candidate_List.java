package com.example.hotrovieclam.Fragment.RecruiterManagement;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hotrovieclam.Adapter.CandidateAdapter;
import com.example.hotrovieclam.Model.User;
import com.example.hotrovieclam.R;
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
    private String cv;
    public Candidate_List() {
        // Required empty public constructor
    }

    public static Candidate_List newInstance(String jobID) {
        Candidate_List fragment = new Candidate_List();
        Bundle args = new Bundle();
        args.putString("jobID", jobID);
        args.putString("cadidateIDD", jobID);
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
        getParentFragmentManager().setFragmentResultListener("deleteSuccess", this, (req, keyvalue) -> {
            // Lấy giá trị "delete" từ kết quả
            boolean update = keyvalue.getBoolean("delete");

            // Kiểm tra nếu có yêu cầu cập nhật
            if (update) {
                // Lấy lại dữ liệu từ Firestore
                users.clear();
                fetchCandidatesRealtime();
                Log.d("ff", "onCreateView: Cập nhật thành công");

                // Sau khi lấy lại dữ liệu, làm mới danh sách
                // (Lưu ý: Dữ liệu đã được cập nhật trong fetchCandidatesRealtime)
                adapter.notifyDataSetChanged();
            }
        });
        getParentFragmentManager().setFragmentResultListener("deleteSuccess", this, (req, keyvalue) -> {
            // Lấy giá trị "delete" từ kết quả
            boolean update = keyvalue.getBoolean("delete");

            // Kiểm tra nếu có yêu cầu cập nhật
            if (update) {
                // Lấy lại dữ liệu từ Firestore
                users.clear();
                fetchCandidatesRealtime();
                Log.d("ff", "onCreateView: Cập nhật thành công");

                adapter.notifyDataSetChanged();
            }
        });

        // Fetch candidates in real-time
        fetchCandidatesRealtime();
        adapter.setClickIem(new CandidateAdapter.OnItemClickListener() {
            @Override
            public void onClick(String id_candidate, Integer po) {
                // Tạo một instance của Fragment
                Profile_Candidate_Fragment profileCandidateFragment = new Profile_Candidate_Fragment();

                // Tạo Bundle để truyền dữ liệu
                Bundle bundle = new Bundle();
                bundle.putString("id_candidate", id_candidate);
                bundle.putString("candidate", users.get(po).getCv());
                bundle.putString("ID_JOB", jobID);



                // Thiết lập arguments cho Fragment
                profileCandidateFragment.setArguments(bundle);

                // Thay thế Fragment
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main, profileCandidateFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
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
                        // Clear the local users list before adding new data
                        users.clear();

                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String candidateId = document.getString("candidateId");
                             cv = document.getId();
                            Log.d("candidateIdd", cv);
                            if (candidateId != null) {
                                fetchUserDetails(candidateId, cv);  // Fetch user details for each candidate
                            }
                        }
                    } else {
                        Log.d("Candidate_List", "No applications found.");
                    }
                });
    }


    private void fetchUserDetails(String candidateId, String cv) {
        db.collection("users")
                .document(candidateId)
                .addSnapshotListener((userDocument, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "User details fetch failed", e);
                        return;
                    }

                    if (userDocument != null && userDocument.exists()) {
                        User user = new User();
                        user.setName(userDocument.getString("name"));
                        user.setAvatar(userDocument.getString("avatar"));
                        user.setEmail(userDocument.getString("email"));
                        user.setPhoneNumber(userDocument.getString("phoneNumber"));
                        user.setId(userDocument.getString("id"));
user.setCv(cv);
                        // Add the user to the list
                        users.add(user);
                        // Notify adapter to refresh the list
                        adapter.notifyDataSetChanged();
                    } else {

                        Log.d("Candidate_List", "No such user document for candidate ID: " + candidateId);
                    }
                });
    }
}
