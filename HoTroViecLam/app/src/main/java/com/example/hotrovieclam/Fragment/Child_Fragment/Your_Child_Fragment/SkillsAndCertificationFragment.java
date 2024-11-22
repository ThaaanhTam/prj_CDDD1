package com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hotrovieclam.Model.HieuUngThongBao;
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
        try {
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
                    try {
                        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                            getParentFragmentManager().popBackStack();
                            BottomNavigationView bottomNav = getActivity().findViewById(R.id.nav_buttom);
                            if (bottomNav != null) {
                                bottomNav.setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("BackNavigation", "Lỗi khi quay lại màn hình trước đó", e);
                    }
                }
            });

            //binding.btnUpdateSkill.setEnabled(false); // Vô hiệu hóa nút

            binding.btnUpdateSkill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.btnUpdateSkill.setVisibility(View.GONE); // Kích hoạt lại nút nếu lỗi nhập liệu
                    String nameSkill = binding.nameskill.getText().toString().trim();
                    String description = binding.mota.getText().toString().trim();
                    if (nameSkill.isEmpty() || description.isEmpty()) {
                        if (nameSkill.isEmpty()) {
                            binding.nameskill.setError("Vui lòng nhập tên kỹ năng");
                        }
                        if (description.isEmpty()) {
                            binding.mota.setError("Vui lòng nhập mô tả");
                        }
                        binding.btnUpdateSkill.setVisibility(View.VISIBLE); // Kích hoạt lại nút nếu lỗi nhập liệu
                        return;
                    }
                    kiNang = new KiNang(null, id, nameSkill, description);
                    if (id != null) {
                        binding.loading.setVisibility(View.VISIBLE);
                        saveSkill(id);
                    } else if (id_skill != null) {
                        binding.loading.setVisibility(View.VISIBLE);
                        Log.d("BOM", "onClick: "+id+"-------"+id_skill);
                        update(id_skill);
                    }
                }
            });

            return view;

        } catch (Exception e) {
            Log.e("onCreateView", "Lỗi khi tạo View", e);
            Toast.makeText(getContext(), "Đã xảy ra lỗi khi tạo giao diện", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void saveSkill(String uid) {
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(uid)
                    .collection("role").document("candidate")
                    .collection("skills")
                    .add(kiNang)
                    .addOnSuccessListener(documentReference -> {
                        try {
                            kiNang.setId(documentReference.getId());

                            documentReference.set(kiNang)
                                    .addOnSuccessListener(aVoid -> {
                                        try {
                                            // Kiểm tra Fragment có gắn với FragmentManager trước khi xử lý
                                            if (isAdded()) {
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean("add", true);
                                                getParentFragmentManager().setFragmentResult("addSucess", bundle);
                                                getParentFragmentManager().popBackStack();
                                                Log.d("Firestore", "Dữ liệu đã được lưu thành công với ID: " + documentReference.getId());
                                                HieuUngThongBao.showSuccessToast(requireContext(),"Lưu skill thành công");
                                                //Toast.makeText(getContext(), "Lưu skill thành công", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            Log.e("saveSkill", "Lỗi khi gửi kết quả hoặc cập nhật giao diện", e);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Lỗi khi cập nhật ID của kỹ năng", e);
                                        if (isAdded()) {
                                            Toast.makeText(getContext(), "Lỗi khi cập nhật ID của kỹ năng", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } catch (Exception e) {
                            Log.e("saveSkill", "Lỗi khi đặt ID cho kỹ năng", e);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Lỗi khi lưu dữ liệu", e);
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Lỗi khi lưu dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Log.e("saveSkill", "Lỗi khi thực hiện lưu kỹ năng", e);
        }
    }

    public void loaddata(String id_skill) {
        try {
            UserSessionManager user = new UserSessionManager();
            String uid = user.getUserUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(uid)
                    .collection("role").document("candidate")
                    .collection("skills").document(id_skill)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            KiNang skill = documentSnapshot.toObject(KiNang.class);
                            if (skill != null) {
                                binding.nameskill.setText(skill.getName());
                                binding.mota.setText(skill.getDescription());
                            } else {
                                Log.e("LoadData", "Không thể chuyển đổi dữ liệu thành KiNang");
                            }
                        } else {
                            Log.e("LoadData", "Tài liệu không tồn tại với ID: " + id_skill);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("LoadData", "Lỗi khi lấy dữ liệu từ Firestore", e);
                    });
        } catch (Exception e) {
            Log.e("loaddata", "Lỗi khi tải dữ liệu kỹ năng", e);
        }
    }

    private void update(String id_skill) {
        try {
            UserSessionManager userSession = new UserSessionManager();
             id = userSession.getUserUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String nameSkill = binding.nameskill.getText().toString().trim();
            String description = binding.mota.getText().toString().trim();

            KiNang kiNang = new KiNang(id_skill, id, nameSkill, description);

            db.collection("users").document(id)
                    .collection("role").document("candidate")
                    .collection("skills")
                    .document(id_skill)
                    .set(kiNang)
                    .addOnSuccessListener(aVoid -> {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("add", true);
                        getParentFragmentManager().setFragmentResult("addSucess", bundle);
                        getParentFragmentManager().popBackStack();
                        Log.d("Firestore", "Dữ liệu đã được cập nhật thành công với ID: " + id_skill);
                        HieuUngThongBao.showSuccessToast(requireContext(),"Cập nhật kỹ năng thành công");
                        //Toast.makeText(getContext(), "Cập nhật kỹ năng thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Lỗi khi cập nhật dữ liệu", e);
                        Toast.makeText(getContext(), "Lỗi khi cập nhật kỹ năng", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e("update", "Lỗi khi cập nhật kỹ năng", e);
        }
    }
}
