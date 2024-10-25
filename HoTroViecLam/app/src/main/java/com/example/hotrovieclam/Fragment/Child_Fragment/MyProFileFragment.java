package com.example.hotrovieclam.Fragment.Child_Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotrovieclam.Adapter.TabLayoutAdapter;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentMyProFileBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayoutMediator;

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
       return view;
    }
}