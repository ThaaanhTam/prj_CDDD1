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
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentHocVanBinding;

import java.util.ArrayList;
import java.util.List;

public class HocVanFragment extends Fragment {
    private FragmentHocVanBinding binding;
    private TruongHocAdapter truongHocAdapter;
    private List<TruongHoc> truongHocs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHocVanBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //set up va hien thi
        setupRecyclerView();
        addInitialData();
        binding.themhocvan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EducationFragment educationFragment = new EducationFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, educationFragment).addToBackStack(null).commit();
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
    private void addInitialData() {
        truongHocs.add(new TruongHoc("Đại học A", "Công nghệ thông tin", "2018", "2022", "Học tại thành phố X", 0));
        truongHocs.add(new TruongHoc("Đại học B", "Kinh tế", "2015", "2019", "Học tại thành phố Y", 1));

        // Notify the adapter that the data has changed
        truongHocAdapter.notifyDataSetChanged();
    }

    // Hàm để gọi lại bất cứ khi nào bạn cần cập nhật dữ liệu mới
    public void updateData(List<TruongHoc> newTruongHocs) {
        truongHocs.clear();  // Clear the old data
        truongHocs.addAll(newTruongHocs);  // Add the new data
        truongHocAdapter.notifyDataSetChanged();  // Notify the adapter of the change
    }
}
