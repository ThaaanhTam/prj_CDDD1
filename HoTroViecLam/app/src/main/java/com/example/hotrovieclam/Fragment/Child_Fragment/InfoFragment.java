package com.example.hotrovieclam.Fragment.Child_Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotrovieclam.Fragment.Child_Fragment.Your_Child_Fragment.ThongTinCaNhanFragment;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentInfoBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class InfoFragment extends Fragment {
    private FragmentInfoBinding binding;



    public InfoFragment() {
        // Required empty public constructor
    }



    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding  = FragmentInfoBinding.inflate(inflater,container,false);
       View view = binding.getRoot();
//        UserSessionManager uid = new UserSessionManager();
//        String aididu = uid.getUserUid();
//        Log.d("HH", "onCreateView: "+aididu);
       binding.editTtcn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               UserSessionManager sessionManager = new UserSessionManager();
               String uid = sessionManager.getUserUid();
               Bundle bundle =new Bundle();
               bundle.putString("uid",uid);
               ThongTinCaNhanFragment thongTinCaNhanFragment = new ThongTinCaNhanFragment();
               thongTinCaNhanFragment.setArguments(bundle);
               getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, thongTinCaNhanFragment).addToBackStack(null).commit();
           }
       });
     getParentFragmentManager().setFragmentResultListener("isupdate",this,(requestKey, result) -> {
         boolean key = result.getBoolean("update");
         if (key){
             HienThiThongTin();
         }
     });

        HienThiThongTin();
       return  view;
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
                        String email = document.getString("email");
                        String phoneNumber = document.getString("phoneNumber");

                        // Hiển thị thông tin người dùng

                        binding.email.setText(email);
                        binding.phone.setText(phoneNumber);
                        Log.d("PPPP", "onComplete: "+email);
                        DocumentReference profileRef = db.collection("users").document(uid)
                                .collection("role").document("candidate")
                                .collection("profile").document(uid);
                        profileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot profileDocument = task.getResult();
                                    if (profileDocument.exists()) {
                                        String ngaysinh = profileDocument.getString("birthday"); // Lấy ngày sinh
                                        String diachi = profileDocument.getString("address"); // Lấy địa chỉ
                                        int gioitinh = profileDocument.getLong("gioitinh").intValue(); // Lấy giới tính dưới dạng số

                                        // Hiển thị thông tin profile
                                        binding.ngaysinh.setText(ngaysinh); // Hiển thị ngày sinh
                                        binding.adress.setText(diachi);
                                        binding.ngaysinh.setTextColor(getResources().getColor(R.color.black));// Hiển thị tên giới tính
                                        binding.adress.setTextColor(getResources().getColor(R.color.black));// Hiển thị tên giới tính
// Hiển thị địa chỉ

                                        // Truy vấn bảng genders để lấy tên giới tính
                                        db.collection("genders").document(String.valueOf(gioitinh))
                                                .get()
                                                .addOnCompleteListener(genderTask -> {
                                                    if (genderTask.isSuccessful()) {
                                                        DocumentSnapshot genderDocument = genderTask.getResult();
                                                        if (genderDocument.exists()) {
                                                            String genderName = genderDocument.getString("name");
                                                            binding.gioitinh.setText(genderName);
                                                            binding.gioitinh.setTextColor(getResources().getColor(R.color.black));// Hiển thị tên giới tính
                                                        } else {
                                                            Log.d("Firestore", "Không tìm thấy giới tính cho ID: " + gioitinh);
                                                        }
                                                    } else {
                                                        Log.e("Firestore", "Lỗi khi truy vấn bảng genders.", genderTask.getException());
                                                    }
                                                });
                                    } else {
                                        Log.d("Firestore", "Không tìm thấy dữ liệu profile.");
                                    }
                                } else {
                                    Log.d("Firestore", "Lỗi khi truy vấn dữ liệu profile.", task.getException());
                                }
                            }
                        });
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