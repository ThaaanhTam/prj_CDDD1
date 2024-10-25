package com.example.hotrovieclam.Model.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotrovieclam.Fragment.Child_Fragment.MyProFileFragment;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentAcountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class AcountFragment extends Fragment {

    
    private FragmentAcountBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BottomNavigationView bottomNav = getActivity().findViewById(R.id.nav_buttom);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.VISIBLE);
        }
        // Inflate the layout for this fragment
        binding = FragmentAcountBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyProFileFragment myProFileFragment = new MyProFileFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, myProFileFragment).addToBackStack("null").commit();
                BottomNavigationView bottomNav = getActivity().findViewById(R.id.nav_buttom);
                if (bottomNav != null) {
                    bottomNav.setVisibility(View.GONE);
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
                        String email = document.getString("email");
                        // Hiển thị thông tin người dùng
                        binding.name.setText(name);
                        binding.email.setText(email);
                        Log.d("PPPP", "onComplete: "+email+name);
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