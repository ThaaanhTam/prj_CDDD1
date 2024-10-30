package com.example.hotrovieclam.Fragment.Child_Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment.ThongTinCaNhanFragment;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentInfoBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends Fragment {
    private FragmentInfoBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding  = FragmentInfoBinding.inflate(inflater,container,false);
       View view = binding.getRoot();
       binding.editTtcn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               ThongTinCaNhanFragment thongTinCaNhanFragment = new ThongTinCaNhanFragment();
               getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, thongTinCaNhanFragment).addToBackStack(null).commit();
           }
       });
       return  view;
    }

}