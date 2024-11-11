package com.example.hotrovieclam.Fragment.Child_Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Adapter.TabLayoutAdapter;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentMyProFileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyProFileFragment extends Fragment {
private FragmentMyProFileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding = FragmentMyProFileBinding.inflate(inflater,container,false);
               View view =binding.getRoot();

        // Thiết lập lại adapter sử dụng FragmentStateAdapter
        TabLayoutAdapter tabLayoutAdapter = new TabLayoutAdapter(getActivity());

// Thêm các fragment
        tabLayoutAdapter.addFragment(new GioiThieuFragment(), "Giới Thiệu");
        tabLayoutAdapter.addFragment(new HocVanFragment(), "Học Vấn");
        tabLayoutAdapter.addFragment(new KinhNghiemFragment(), "Kinh Nghiệm");
        tabLayoutAdapter.addFragment(new KiNangVaChungChiFragment(), "Kĩ Năng Và Chứng Chỉ");
        tabLayoutAdapter.addFragment(new InfoFragment(), "Thông Tin Cá Nhân");

// Thiết lập adapter cho ViewPager2
        binding.viewpager.setAdapter(tabLayoutAdapter);

// Thiết lập TabLayoutMediator để kết nối TabLayout và ViewPager2
        new TabLayoutMediator(binding.tabLayout, binding.viewpager,
                (tab, position) -> tab.setText(tabLayoutAdapter.getPageTitle(position))
        ).attach();

// Thiết lập item hiện tại nếu cần (optional)
        binding.viewpager.setCurrentItem(0);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                    BottomNavigationView bottomNav = getActivity().findViewById(R.id.nav_buttom);
                    if (bottomNav != null) {
                        bottomNav.setVisibility(View.VISIBLE);
                    }// Quay lại Fragment trước đó
                }
            }
        });
        HienThiThongTin();
       return view;
    }
    public void HienThiThongTin(){
        UserSessionManager sessionManager = new UserSessionManager();
        String uid = sessionManager.getUserUid();

        // Dùng UID để truy vấn Firestore hoặc hiển thị thông tin người dùng
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name = document.getString("name");


                        // Hiển thị thông tin người dùng
                        binding.name.setText(name);
                        String avatarUrl = document.getString("avatar");
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            // Dùng Glide để tải ảnh từ URL và hiển thị vào ImageView
                            Glide.with(getContext())
                                    .load(avatarUrl)
                                    .centerCrop()
                                    .into(binding.avt); // imageView là ImageView của bạn
                            Log.d("ii", "onComplete: lay dc anh vs uid"+uid );
                        }
                        Log.d("PPPP", "onComplete: "+name);
                    } else {
                        Log.d("Firestore", "Không tìm thấy dữ liệu người dùng.");
                    }
                } else {
                    Log.d("Firestore", "Lỗi khi truy vấn dữ liệu.", task.getException());
                }
            }
        });
    }
}