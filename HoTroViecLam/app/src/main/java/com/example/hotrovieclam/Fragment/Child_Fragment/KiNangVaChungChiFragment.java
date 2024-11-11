package com.example.hotrovieclam.Fragment.Child_Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hotrovieclam.Adapter.KiNangAdapter;
import com.example.hotrovieclam.Adapter.TruongHocAdapter;
import com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment.EducationFragment;
import com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment.SkillsAndCertificationFragment;
import com.example.hotrovieclam.Model.KiNang;
import com.example.hotrovieclam.Model.TruongHoc;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentKiNangVaChungChiBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class KiNangVaChungChiFragment extends Fragment {
    private FragmentKiNangVaChungChiBinding binding;
    private KiNangAdapter kiNangAdapter;
    private List<KiNang> kiNangArrayList;
    UserSessionManager userSessionManager = new UserSessionManager();
    String uid = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentKiNangVaChungChiBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //tao id uid
        uid = userSessionManager.getUserUid();

        kiNangArrayList = new ArrayList<>();
//        kiNangArrayList.add(new KiNang("33333", "3", "Laapj trinhs php", "hack nasa1"));
//        kiNangArrayList.add(new KiNang("33333", "3", "Laapj trinhs python", "hack nasa2"));

        SetUpRecycleView();
        Log.d("IUY", "onCreateView: "+uid);
        binding.themSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendIUDFragmenr(uid);
            }
        });
        binding.themkinang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIUDFragmenr(uid);
            }
        });
        getParentFragmentManager().setFragmentResultListener("addSucess", this, (requestKey, bundle) -> {
            boolean isUpdated = bundle.getBoolean("add");
            if (isUpdated) {
                loadSchoolData(uid); // Tải lại dữ liệu mới  khi cập nhật thành công
            }
        });
        kiNangAdapter.setOnItemEditClickListener(new KiNangAdapter.OnItemEditClickListener() {
            @Override
            public void onEditClick(String schoolId) {
                SkillsAndCertificationFragment skillsAndCertificationFragment = new SkillsAndCertificationFragment();                Bundle bundle = new Bundle();
                bundle.putString("ID_SKILL", schoolId);
                skillsAndCertificationFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, skillsAndCertificationFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        loadSchoolData(uid);
        return view;
    }

    public void SetUpRecycleView() {
        kiNangAdapter = new KiNangAdapter(kiNangArrayList,this);
        binding.ItemKiNang.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.ItemKiNang.setAdapter(kiNangAdapter);
    }

    private void sendIUDFragmenr(String uid) {
        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", uid);
        Log.d("fff", "onClick: " + uid);
        SkillsAndCertificationFragment skillsAndCertificationFragment = new SkillsAndCertificationFragment();
        skillsAndCertificationFragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, skillsAndCertificationFragment)
                .addToBackStack(null)
                .commit();
    }
    private void loadSchoolData(String uid) {
        // Khởi tạo Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy dữ liệu từ Firestore
        db.collection("users").document(uid)
                .collection("role").document("candidate")
                .collection("skills") // Truy cập vào collection "school"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        kiNangArrayList.clear(); // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Chuyển đổi dữ liệu từ Firestore thành đối tượng TruongHoc
                            KiNang kiNang = document.toObject(KiNang.class);
                            kiNangArrayList.add(kiNang);
                        }

                        if (kiNangArrayList.isEmpty()) {
                            // Nếu không có dữ liệu, hiển thị thông báo
                            Log.d("Firestore", "Bạn chưa có thông tin nào.");
                            binding.themkinang.setVisibility(View.VISIBLE);
                        } else {
                            // Nếu có dữ liệu, cập nhật RecyclerView
                            binding.gioithiebanthan.setVisibility(View.GONE);
                            binding.themkinang.setVisibility(View.GONE);
                            binding.themSkill.setVisibility(View.VISIBLE); // Ẩn thông báo nếu có dữ liệu
                            kiNangAdapter.notifyDataSetChanged(); // Cập nhật lại RecyclerView
                        }
                    } else {
                        Log.e("Firestore", "Lỗi khi lấy dữ liệu", task.getException());
                    }
                });
    }
    public void deleteSkill(String skillId) {
        UserSessionManager userSession = new UserSessionManager();
        String uid = userSession.getUserUid();

        if (uid != null && skillId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uid)
                    .collection("role").document("candidate")
                    .collection("skills").document(skillId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Xóa kỹ năng thành công"+skillId);
                        Toast.makeText(getContext(), "Đã xóa kỹ năng", Toast.LENGTH_SHORT).show();
                        // Cập nhật lại danh sách kỹ năng nếu cần
                        LoadLaiData();
                        removeSkillFromList(skillId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Lỗi khi xóa kỹ năng", e);
                    });
        } else {
            Log.e("DeleteSkill", "User ID hoặc Skill ID bị null");
        }
    }
    public void LoadLaiData(){
        kiNangAdapter.notifyDataSetChanged();
        loadSchoolData(uid);

    }
    private void removeSkillFromList(String skillId) {
        for (int i = 0; i < kiNangArrayList.size(); i++) {
            if (kiNangArrayList.get(i).getId().equals(skillId)) {
                kiNangArrayList.remove(i); // Xóa phần tử khỏi danh sách
                kiNangAdapter.notifyItemRemoved(i); // Cập nhật RecyclerView tại vị trí đã xóa
                break;
            }
        }
    }

}