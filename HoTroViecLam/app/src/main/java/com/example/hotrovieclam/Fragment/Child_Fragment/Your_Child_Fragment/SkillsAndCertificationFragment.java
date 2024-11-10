package com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hotrovieclam.Model.KiNang;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentSkillsAndCertificationBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;


public class SkillsAndCertificationFragment extends Fragment {

    private FragmentSkillsAndCertificationBinding binding;
    String id = null;
    String id_skill;
    private KiNang kiNang;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSkillsAndCertificationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Bundle bundle = getArguments();

        if (bundle != null) {
            id = bundle.getString("USER_ID");
            id_skill = bundle.getString("ID_SKILL");
            Log.d("CAC", "onCreateView: " + id + "---------------------------" + id_skill);

        }
        Log.d("VV", "aiaiaiai: " + id);
        if (id_skill != null) {
            loaddata(id_skill);
        }
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                    BottomNavigationView bottomNav = getActivity().findViewById(R.id.nav_buttom);
                    if (bottomNav != null) {
                        bottomNav.setVisibility(View.GONE);
                    }// Quay lại Fragment trước đó
                }
            }
        });
        binding.btnUpdateSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameSkill = binding.nameskill.getText().toString().trim();
                String description = binding.mota.getText().toString().trim();
                if (nameSkill.isEmpty() || description.isEmpty()) {
                    if (nameSkill.isEmpty()) {
                        binding.nameskill.setError("Vui lòng nhập tên kỹ năng");
                    }
                    if (description.isEmpty()) {
                        binding.mota.setError("Vui lòng nhập mô tả");
                    }
                    return;
                }
                kiNang = new KiNang(null, id, nameSkill, description);
                if (id != null) {
                    binding.loading.setVisibility(View.VISIBLE);
                    saveSkill(id);
                } else if (id_skill != null) {
                    binding.loading.setVisibility(View.VISIBLE);
                    update(id_skill);
                }
            }
        });
        return view;

    }

    public void saveSkill(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(uid)
                .collection("role").document("candidate")
                .collection("skills")
                .add(kiNang)
                .addOnSuccessListener(documentReference -> {
                    kiNang.setId(documentReference.getId());

                    documentReference.set(kiNang) // Cập nhật với ID mới
                            .addOnSuccessListener(aVoid -> {
                                // Gửi req khi quay lại màn hình trước đó (HocVanFragment)
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("add", true);
                                getParentFragmentManager().setFragmentResult("addSucess", bundle);
                                getParentFragmentManager().popBackStack();
                                Log.d("Firestore", "Dữ liệu đã được lưu thành công với ID: " + documentReference.getId());
                                Toast.makeText(getContext(), "Lưu skill thành công", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi lưu dữ liệu", e);
                });

    }

    public void loaddata(String id_skill) {
        UserSessionManager user = new UserSessionManager();
        String uid = user.getUserUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy dữ liệu từ Firestore
        db.collection("users").document(uid)
                .collection("role").document("candidate")
                .collection("skills").document(id_skill) // Truy cập đến document với id_school
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy dữ liệu từ document
                        KiNang skill = documentSnapshot.toObject(KiNang.class);

                        if (skill != null) {
                            // Cập nhật giao diện với dữ liệu từ Firestore
                            binding.nameskill.setText(skill.getName());
                            binding.mota.setText(skill.getDescription());

                        } else {
                            Log.e("LoadData", "Không thể chuyển đổi dữ liệu thành TruongHoc");
                        }
                    } else {
                        Log.e("LoadData", "Tài liệu không tồn tại với ID: " + id_skill);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LoadData", "Lỗi khi lấy dữ liệu từ Firestore", e);
                });
    }

    private void update(String id_skill) {
        UserSessionManager userSession = new UserSessionManager();
        String uid = userSession.getUserUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String nameSkill = binding.nameskill.getText().toString().trim();
        String description = binding.mota.getText().toString().trim();
        // Tạo đối tượng KiNang với dữ liệu cập nhật
        KiNang kiNang = new KiNang(uid, id_skill, nameSkill, description); // Gán dữ liệu mới vào kiNang

        // Cập nhật tài liệu kỹ năng theo id_skill
        db.collection("users").document(uid)
                .collection("role").document("candidate")
                .collection("skills")
                .document(id_skill)  // Đảm bảo cập nhật đúng tài liệu bằng ID
                .set(kiNang)  // Cập nhật tài liệu với dữ liệu mới
                .addOnSuccessListener(aVoid -> {
                    // Gửi thông báo khi cập nhật thành công
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("add", true);
                    getParentFragmentManager().setFragmentResult("addSucess", bundle);
                    getParentFragmentManager().popBackStack();
                    Log.d("Firestore", "Dữ liệu đã được cập nhật thành công với ID: " + id_skill);
                    Toast.makeText(getContext(), "Cập nhật kỹ năng thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi cập nhật dữ liệu", e);
                    Toast.makeText(getContext(), "Lỗi khi cập nhật kỹ năng", Toast.LENGTH_SHORT).show();
                });
    }

}