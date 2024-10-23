package com.example.hotrovieclam.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotrovieclam.Fragment.Child_Fragment.MyProFileFragment;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentAcountBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class AcountFragment extends Fragment {
    private FragmentAcountBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAcountBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView bottomNav = getActivity().findViewById(R.id.nav_buttom);
                if (bottomNav != null) {
                    bottomNav.setVisibility(View.GONE);
                }
                MyProFileFragment myProFileFragment = new MyProFileFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, myProFileFragment).addToBackStack(null).commit();
            }
        });
        return view;
    }
}