package com.example.hotrovieclam.Fragment.Child_Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment.AddPersonalInfoFragment;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentGioiThieuBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GioiThieuFragment extends Fragment {
    private UserSessionManager userSessionManager;
    private FragmentGioiThieuBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGioiThieuBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        userSessionManager = new UserSessionManager();
        String i = userSessionManager.getUserUid();
        binding.gioithiebanthan.setText(i);
        binding.btnCapnhatProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPersonalInfoFragment addPersonalInfoFragment = new AddPersonalInfoFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, addPersonalInfoFragment).addToBackStack(null).commit();
            }
        });
        return view;
    }

    public void HienThiThongTin() {
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
                        String phoneNumber = document.getString("phoneNumber");

                        // Hiển thị thông tin người dùng

                        Log.d("PPPP", "onComplete: " + email + name);
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