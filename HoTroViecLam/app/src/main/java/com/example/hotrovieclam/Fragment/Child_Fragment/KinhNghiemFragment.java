package com.example.hotrovieclam.Fragment.Child_Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hotrovieclam.Adapter.ExperienceAdapter;
import com.example.hotrovieclam.Adapter.TruongHocAdapter;
import com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment.EducationFragment;
import com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment.ExperienceFragment;
import com.example.hotrovieclam.Model.Experience;
import com.example.hotrovieclam.Model.HieuUngThongBao;
import com.example.hotrovieclam.Model.TruongHoc;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentHocVanBinding;
import com.example.hotrovieclam.databinding.FragmentKinhNghiemBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class KinhNghiemFragment extends Fragment {
    private FragmentKinhNghiemBinding binding;
    private ExperienceAdapter experienceAdapter;
    private List<Experience> experiences;
    UserSessionManager userSessionManager = new UserSessionManager();
    private FirebaseFirestore db;
    String uid = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentKinhNghiemBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        db = FirebaseFirestore.getInstance();
         uid = userSessionManager.getUserUid();
        setupRecyclerView();
        //lay req va cap lại du lieuy
        getParentFragmentManager().setFragmentResultListener("addSucess", this, (requestKey, bundle) -> {
            boolean isUpdated = bundle.getBoolean("add");
            if (isUpdated) {
                loadexpErienceData(uid); // Tải lại dữ liệu mới  khi cập nhật thành công
            }
        });
        getParentFragmentManager().setFragmentResultListener("updateSuccess",this,(req,keyvalue)->{
            boolean update = keyvalue.getBoolean("update");
            if (update){
                loadexpErienceData(uid);
            }
        });
        loadexpErienceData(uid);


        binding.themkn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIUDFragmenr(uid);
            }
        });
        binding.btnCapnhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIUDFragmenr(uid);
            }
        });
        experienceAdapter.setOnItemEditClickListener(new ExperienceAdapter.OnItemEditClickListener() {
            @Override
            public void onEditClick(String experienceId) {
                ExperienceFragment fragment = new ExperienceFragment();
                Bundle bundle = new Bundle();
                bundle.putString("EXPERIENCE_ID", experienceId);
                fragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }


    // Hàm thiết lập RecyclerView và Adapter
    private void setupRecyclerView() {
        experiences = new ArrayList<>();
        experienceAdapter = new ExperienceAdapter(experiences,this);
        binding.recycelViewItemKinhnghiem.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycelViewItemKinhnghiem.setAdapter(experienceAdapter);
    }

    // Hàm thêm dữ liệu ban đầu
    private void loadexpErienceData(String uid) {
        // Khởi tạo Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy dữ liệu từ Firestore
        db.collection("users").document(uid)
                .collection("role").document("candidate")
                .collection("experience") // Truy cập vào collection "experience"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        experiences.clear(); // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Chuyển đổi dữ liệu từ Firestore thành đối tượng TruongHoc
                            Experience experience = document.toObject(Experience.class);
                            experiences.add(experience);
                        }

                        if (experiences.isEmpty()) {
                            // Nếu không có dữ liệu, hiển thị thông báo
                            Log.d("Firestore", "Bạn chưa có thông tin nào.");
                            binding.btnCapnhat.setVisibility(View.VISIBLE);
                            binding.themkn.setVisibility(View.GONE);
                        } else {
                            // Nếu có dữ liệu, cập nhật RecyclerView
                            binding.tvthem.setVisibility(View.GONE);
                            binding.btnCapnhat.setVisibility(View.GONE);
                            binding.themkn.setVisibility(View.VISIBLE); // Ẩn thông báo nếu có dữ liệu
                            experienceAdapter.notifyDataSetChanged(); // Cập nhật lại RecyclerView
                        }
                    } else {
                        Log.e("Firestore", "Lỗi khi lấy dữ liệu", task.getException());
                    }
                });
    }

    private void sendIUDFragmenr(String uid) {
        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", uid);
        Log.d("fff", "onClick: " + uid);
        ExperienceFragment experienceFragment = new ExperienceFragment();
        experienceFragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, experienceFragment)
                .addToBackStack(null)
                .commit();
    }



    private void loadExperienceData(String uid) {
        // Khởi tạo Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lắng nghe sự thay đổi trong Firestore theo thời gian thực
        db.collection("users").document(uid)
                .collection("role").document("candidate")
                .collection("experience") // Truy cập vào collection "experience"
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Lỗi khi lấy dữ liệu", error);
                        return;
                    }

                    if (value != null) {
                        experiences.clear(); // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                        for (QueryDocumentSnapshot document : value) {
                            // Chuyển đổi dữ liệu từ Firestore thành đối tượng Experience
                            Experience experience = document.toObject(Experience.class);
                            experiences.add(experience);
                        }

                        if (experiences.isEmpty()) {
                            // Nếu không có dữ liệu, hiển thị thông báo
                            Log.d("Firestore", "Bạn chưa có thông tin nào.");
                            binding.btnCapnhat.setVisibility(View.VISIBLE);
                            binding.themkn.setVisibility(View.GONE);
                        } else {
                            // Nếu có dữ liệu, cập nhật RecyclerView
                            binding.tvthem.setVisibility(View.GONE);
                            binding.btnCapnhat.setVisibility(View.GONE);
                            binding.themkn.setVisibility(View.VISIBLE); // Ẩn thông báo nếu có dữ liệu
                            experienceAdapter.notifyDataSetChanged(); // Cập nhật lại RecyclerView
                        }
                    }
                });
    }

    public void deleteExperience(String ex_id) {
        UserSessionManager userSession = new UserSessionManager();
        String uid = userSession.getUserUid();

        if (uid != null && ex_id != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uid)
                    .collection("role").document("candidate")
                    .collection("experience").document(ex_id)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Xóa kinh nghiem thành công"+ex_id);
                        HieuUngThongBao.showSuccessToast(requireContext(),"Xóa kinh nghiem thành công");
                        //Toast.makeText(getContext(), "Đã xóa kỹ năng", Toast.LENGTH_SHORT).show();
                        // Cập nhật lại danh sách kinh nghiem nếu cần

                        removeExperienceFromList(ex_id);
                        LoadLaiData();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Lỗi khi xóa kỹ năng", e);
                    });
        } else {
            Log.e("Delete", "User ID hoặc Skill ID bị null");
        }
    }
    public void LoadLaiData(){
        experienceAdapter.notifyDataSetChanged();
        loadExperienceData(uid);

    }
    private void removeExperienceFromList(String ex_id) {
        for (int i = 0; i < experiences.size(); i++) {
            if (experiences.get(i).getIdExperiences().equals(ex_id)) {
                experiences.remove(i); // Xóa phần tử khỏi danh sách
                experienceAdapter.notifyItemRemoved(i); // Cập nhật RecyclerView tại vị trí đã xóa
                break;
            }
        }
    }
}
