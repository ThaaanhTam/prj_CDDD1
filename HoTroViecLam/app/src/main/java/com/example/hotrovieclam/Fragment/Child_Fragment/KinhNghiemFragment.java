package com.example.hotrovieclam.Fragment.Child_Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotrovieclam.Adapter.ExperienceAdapter;
import com.example.hotrovieclam.Adapter.TruongHocAdapter;
import com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment.EducationFragment;
import com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment.ExperienceFragment;
import com.example.hotrovieclam.Model.Experience;
import com.example.hotrovieclam.Model.TruongHoc;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentHocVanBinding;
import com.example.hotrovieclam.databinding.FragmentKinhNghiemBinding;

import java.util.ArrayList;
import java.util.List;


public class KinhNghiemFragment extends Fragment {
    private FragmentKinhNghiemBinding binding;
    private ExperienceAdapter experienceAdapter;
    private List<Experience> experiences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentKinhNghiemBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //set up va hien thi
        setupRecyclerView();
        addInitialData();
        binding.themkn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ExperienceFragment experienceFragment = new ExperienceFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, experienceFragment).addToBackStack(null).commit();
            }
        });
        return view;
    }


    // Hàm thiết lập RecyclerView và Adapter
    private void setupRecyclerView() {
        experiences = new ArrayList<>();
        experienceAdapter = new ExperienceAdapter(experiences);
        binding.recycelViewItemKinhnghiem.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycelViewItemKinhnghiem.setAdapter(experienceAdapter);
    }

    // Hàm thêm dữ liệu ban đầu
    private void addInitialData() {
//        truongHocs.add(new TruongHoc("Đại học A", "Công nghệ thông tin", "2018", "2022", "Học tại thành phố X", 0));
        experiences.add(new Experience());

        // Notify the adapter that the data has changed
        experienceAdapter.notifyDataSetChanged();
    }

    // Hàm để gọi lại bất cứ khi nào bạn cần cập nhật dữ liệu mới
    public void updateData(List<Experience> newExperiences) {
        experiences.clear();  // Clear the old data
        experiences.addAll(newExperiences);  // Add the new data
        experienceAdapter.notifyDataSetChanged();  // Notify the adapter of the change
    }
}
