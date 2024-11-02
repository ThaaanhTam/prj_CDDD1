package com.example.hotrovieclam.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hotrovieclam.Activity.Navigation;
import com.example.hotrovieclam.Fragment.Child_Fragment.ChangPassWordFragment;
import com.example.hotrovieclam.Fragment.Child_Fragment.MyProFileFragment;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.Nam.ChangePassword.ChangePassWord;
import com.example.hotrovieclam.Nam.login.Login;

import com.example.hotrovieclam.Buoc.RegisterEmployer;
import com.example.hotrovieclam.Fragment.Child_Fragment.MyProFileFragment;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.Nam.register.Register;
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

    // Binding giúp liên kết các view với mã Java.
    private FragmentAcountBinding binding;

    // Phương thức này được gọi để tạo giao diện cho Fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Tìm và hiển thị thanh điều hướng dưới cùng (nếu tồn tại).
        BottomNavigationView bottomNav = getActivity().findViewById(R.id.nav_buttom);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.VISIBLE);
        }

        // Khởi tạo binding và liên kết giao diện.
        binding = FragmentAcountBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Xử lý sự kiện khi người dùng nhấn vào nút "Profile".
        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang `MyProFileFragment`.
                MyProFileFragment myProFileFragment = new MyProFileFragment();
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, myProFileFragment)
                        .addToBackStack("null")
                        .commit();

                // Ẩn thanh điều hướng dưới cùng.
                BottomNavigationView bottomNav = getActivity().findViewById(R.id.nav_buttom);
                if (bottomNav != null) {
                    bottomNav.setVisibility(View.GONE);
                }
            }
        });

        // Xử lý sự kiện khi người dùng nhấn vào nút "Acount".
        binding.acount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chuyển sang màn hình đăng ký nhà tuyển dụng.
                Intent i = new Intent(getActivity(), RegisterEmployer.class);
                startActivity(i);
            }
        });

        // Gọi hàm hiển thị thông tin người dùng.
        HienThiThongTin();
        return view;
    }

    /**
     * Phương thức HienThiThongTin() dùng để lấy và hiển thị thông tin người dùng từ Firestore.
     */
    public void HienThiThongTin() {
        // Tạo session để lấy UID người dùng hiện tại.
        UserSessionManager sessionManager = new UserSessionManager();
        String uid = sessionManager.getUserUid();

        // Lấy tham chiếu đến tài liệu người dùng trong Firestore dựa trên UID.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uid);

        // Truy vấn thông tin người dùng và xử lý kết quả.
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) { // Kiểm tra nếu truy vấn thành công.
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) { // Kiểm tra nếu tài liệu tồn tại.
                        // Lấy thông tin người dùng từ tài liệu.
                        String name = document.getString("name");
                        String email = document.getString("email");
                        String phonenumber = document.getString("phoneNumber");

                        // Hiển thị thông tin người dùng trên giao diện.
                        binding.name.setText(name);
                        binding.email.setText(email);
                        binding.sdt.setText(phonenumber);

                        // Ghi log thông tin người dùng để kiểm tra.
                        Log.d("PPPP", "onComplete: " + email + name);
                    } else {
                        // Thông báo nếu không tìm thấy dữ liệu người dùng.
                        Log.d("Firestore", "Không tìm thấy dữ liệu người dùng.");
                    }
                } else {
                    // Thông báo nếu có lỗi khi truy vấn Firestore.
                    Log.d("Firestore", "Lỗi khi truy vấn dữ liệu.", task.getException());
                }
            }
        });
    }
}
