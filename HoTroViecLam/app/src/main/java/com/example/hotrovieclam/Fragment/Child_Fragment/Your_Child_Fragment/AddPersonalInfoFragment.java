package com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotrovieclam.Fragment.Child_Fragment.ChangPassWordFragment;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentAddPersonalInfoBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AddPersonalInfoFragment extends Fragment {
    private FragmentAddPersonalInfoBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        binding = FragmentAddPersonalInfoBinding.inflate(inflater, container, false);
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                    BottomNavigationView bottomNav = getActivity().findViewById(R.id.nav_buttom);
                    if (bottomNav != null) {
                        bottomNav.setVisibility(View.GONE);
                    }
                }
            }
        });
        return binding.getRoot();
    }
}