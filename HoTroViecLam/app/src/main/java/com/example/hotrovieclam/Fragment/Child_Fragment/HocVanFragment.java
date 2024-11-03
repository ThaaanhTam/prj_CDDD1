package com.example.hotrovieclam.Fragment.Child_Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotrovieclam.Adapter.TruongHocAdapter;
import com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment.AddPersonalInfoFragment;
import com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment.EducationFragment;
import com.example.hotrovieclam.Model.TruongHoc;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentHocVanBinding;
import com.example.hotrovieclam.databinding.ItemSchoolRecycleviewBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HocVanFragment extends Fragment {
    private FragmentHocVanBinding binding;
    private TruongHocAdapter truongHocAdapter;
    private List<TruongHoc> truongHocs;
    UserSessionManager userSessionManager = new UserSessionManager();
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHocVanBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        db = FirebaseFirestore.getInstance();
        String uid = userSessionManager.getUserUid();
        setupRecyclerView();
        //lay req va cap lại du lieu
        getParentFragmentManager().setFragmentResultListener("addSucess", this, (requestKey, bundle) -> {
            boolean isUpdated = bundle.getBoolean("add");
            if (isUpdated) {
                loadSchoolData(uid); // Tải lại dữ liệu mới  khi cập nhật thành công
            }
        });
        getParentFragmentManager().setFragmentResultListener("updateSuccess",this,(req,keyvalue)->{
            boolean update = keyvalue.getBoolean("update");
            if (update){
                loadSchoolData(uid);
            }
        });
        loadSchoolData(uid);


        binding.themhocvan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIUDFragmenr(uid);
            }
        });
        binding.btnCapnhatProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIUDFragmenr(uid);
            }
        });
        truongHocAdapter.setOnItemEditClickListener(new TruongHocAdapter.OnItemEditClickListener() {
            @Override
            public void onEditClick(String schoolId) {
                EducationFragment fragment = new EducationFragment();
                Bundle bundle = new Bundle();
                bundle.putString("SCHOOL_ID", schoolId);
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
        truongHocs = new ArrayList<>();
        truongHocAdapter = new TruongHocAdapter(truongHocs);
        binding.recycelViewItemTruongHoc.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycelViewItemTruongHoc.setAdapter(truongHocAdapter);
    }

    // Hàm thêm dữ liệu ban đầu
    private void loadSchoolData(String uid) {
        // Khởi tạo Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy dữ liệu từ Firestore
        db.collection("users").document(uid)
                .collection("role").document("candidate")
                .collection("school") // Truy cập vào collection "school"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        truongHocs.clear(); // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Chuyển đổi dữ liệu từ Firestore thành đối tượng TruongHoc
                            TruongHoc truongHoc = document.toObject(TruongHoc.class);
                            truongHocs.add(truongHoc);
                        }

                        if (truongHocs.isEmpty()) {
                            // Nếu không có dữ liệu, hiển thị thông báo
                            Log.d("Firestore", "Bạn chưa có thông tin nào.");
                            binding.btnCapnhatProfile.setVisibility(View.VISIBLE);
                        } else {
                            // Nếu có dữ liệu, cập nhật RecyclerView
                            binding.gioithiebanthan.setVisibility(View.GONE);
                            binding.btnCapnhatProfile.setVisibility(View.GONE);
                            binding.themhocvan.setVisibility(View.VISIBLE); // Ẩn thông báo nếu có dữ liệu
                            truongHocAdapter.notifyDataSetChanged(); // Cập nhật lại RecyclerView
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
        EducationFragment educationFragment = new EducationFragment();
        educationFragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, educationFragment)
                .addToBackStack(null)
                .commit();
    }


}
