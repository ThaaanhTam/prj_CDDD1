package com.example.hotrovieclam.Fragment.Child_Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment.AddPersonalInfoFragment;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentGioiThieuBinding;

public class GioiThieuFragment extends Fragment {
private FragmentGioiThieuBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      binding = FragmentGioiThieuBinding.inflate(inflater,container,false);
      View view = binding.getRoot();
binding.btnCapnhatProfile.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        AddPersonalInfoFragment  addPersonalInfoFragment = new AddPersonalInfoFragment();
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, addPersonalInfoFragment).addToBackStack(null).commit();
    }
});
      return  view;
    }
}