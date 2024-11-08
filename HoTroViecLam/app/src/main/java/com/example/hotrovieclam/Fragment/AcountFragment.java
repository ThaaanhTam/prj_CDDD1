package com.example.hotrovieclam.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hotrovieclam.Activity.Navigation;
import com.example.hotrovieclam.Fragment.Child_Fragment.ChangPassWordFragment;
import com.example.hotrovieclam.Fragment.Child_Fragment.DialogFragmentAvatar;
import com.example.hotrovieclam.Fragment.Child_Fragment.MyProFileFragment;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.Nam.ChangePassword.ChangePassWord;
import com.example.hotrovieclam.Nam.login.Login;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentAcountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đăng xuất người dùng khỏi Firebase
                FirebaseAuth.getInstance().signOut();

                // Xóa UID khỏi SharedPreferences
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("user_uid");
                editor.apply();

                // Chuyển về màn hình đăng nhập
                Intent intent = new Intent(getActivity(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();

                Toast.makeText(getContext(), "log out ok l", Toast.LENGTH_SHORT).show();
            }
        });
        binding.doipassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangPassWordFragment changePassWord = new ChangPassWordFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, changePassWord).addToBackStack("null").commit();
                BottomNavigationView bottomNav = getActivity().findViewById(R.id.nav_buttom);
                if (bottomNav != null) {
                    bottomNav.setVisibility(View.GONE);
                }
            }
        });

        binding.avt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("000", "calll: ");
                DialogFragmentAvatar avatar= new DialogFragmentAvatar();
                avatar.show(getParentFragmentManager(),"ChangeAvatarDialog");
            }
        });
        getParentFragmentManager().setFragmentResultListener("updateSuccess",this,(req,keyvalue)->{
            boolean update = keyvalue.getBoolean("update");
            if (update){
                binding.avt.setVisibility(View.GONE);
                HienThiThongTin();

            }
        });
        binding.load.setVisibility(View.GONE);
        HienThiThongTin();
        return view;

    }

    public void HienThiThongTin() {
        UserSessionManager sessionManager = new UserSessionManager();
        String uid = sessionManager.getUserUid();
binding.load.setVisibility(View.VISIBLE);
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
                        Long typeuser = document.getLong("userTypeId");
                        if (typeuser == 2) {
                            binding.mucNhaTuynDung.setVisibility(View.VISIBLE);
                        }
                        // Hiển thị thông tin người dùng
                        binding.name.setText(name);
                        binding.email.setText(email);
                        binding.sdt.setText(phoneNumber);
                        String avatarUrl = document.getString("avatar");

                        if (avatarUrl != null && !avatarUrl.isEmpty()) {

                            // Dùng Glide để tải ảnh từ URL và hiển thị vào ImageView
                            Glide.with(getContext())
                                    .load(avatarUrl)
                                    .centerCrop()
                                    .into(binding.avt);
                            binding.avt.setVisibility(View.VISIBLE);
                            binding.load.setVisibility(View.GONE);
                            Log.d("ii", "onComplete: lay dc anh vs uid" + uid);
                        }

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