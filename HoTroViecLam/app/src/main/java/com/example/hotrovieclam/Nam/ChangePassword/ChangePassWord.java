package com.example.hotrovieclam.Nam.ChangePassword;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotrovieclam.Model.HieuUngThongBao;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.Nam.login.Login;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityChangePassWordBinding;
import com.example.hotrovieclam.databinding.CustomToastSuccessBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

public class ChangePassWord extends AppCompatActivity {
    private ActivityChangePassWordBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityChangePassWordBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        UserSessionManager sessionManager = new UserSessionManager();
        String uid = sessionManager.getUserUid();

        binding.buttonChangpass.setOnClickListener(v -> {
            String currentPassword = binding.password.getText().toString().trim();
            String newPassword = binding.passwordNew.getText().toString().trim();
            String confirmPassword = binding.passwordAgain.getText().toString().trim();

            // Kiểm tra tính hợp lệ của các trường
            if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Vui lòng điền tất cả các trường", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            changePassword(currentPassword, newPassword);
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void changePassword(String currentPassword, String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            binding.progressBar.setVisibility(View.VISIBLE);
            // Xác thực lại người dùng với mật khẩu hiện tại
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Nếu xác thực thành công, đổi mật khẩu
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Toast toast = new Toast(ChangePassWord.this);
                                            ThongBaoThanhCong(toast,"Đổi mật khẩu thành côngk!");
                                            //Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                                            // Chuyển về màn hình đăng nhập sau khi đổi mật khẩu
                                            Intent intent = new Intent(ChangePassWord.this, Login.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Không thể đổi mật khẩu. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast toast = new Toast(ChangePassWord.this);
                         ThongBaoThatBai(toast,"Mật khẩu hiện tại không đúngl.");

                            //Toast.makeText(this, "Mật khẩu hiện tại không đúng.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Có lỗi xảy ra. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            // Chuyển người dùng về màn hình đăng nhập nếu không tìm thấy user
            Intent intent = new Intent(ChangePassWord.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
    public void LoadingNhanhHon() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.progressBar, "rotation", 0f, 360f);
        animator.setDuration(300);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();
    }

    public void ThongBaoThanhCong(Toast toast, String nameTB) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_toast_success, null);
        CustomToastSuccessBinding binding = CustomToastSuccessBinding.bind(view);

        toast.setView(view);
        binding.messs.setText(nameTB);
        view.setBackgroundResource(R.drawable.custom_toast_success); // Màu xanh cho thành công

        // Cập nhật toast để hiển thị Toast giữa Gravity.TOP và Gravity.CENTER
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, getResources().getDisplayMetrics().heightPixels / 6);
        toast.setDuration(Toast.LENGTH_SHORT);  // Dài hơn một chút để Toast không biến mất quá nhanh

        // Hiển thị toast
        toast.show();
        view.bringToFront();


        // Thêm hiệu ứng di chuyển lên sau khi Toast hiển thị
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0, -300);
                animator.setDuration(500);
                animator.start();
            }
        }, 1000);
    }
    public void ThongBaoThatBai(Toast toast, String nameTB) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_toast_success, null);
        CustomToastSuccessBinding binding = CustomToastSuccessBinding.bind(view);

        toast.setView(view);
        binding.messs.setText(nameTB);
        view.setBackgroundResource(R.drawable.custom_toast_error); // Màu xanh cho thành công
        binding.sgv.setImageResource(R.drawable.error_svgrepo_com);
        // Cập nhật toast để hiển thị Toast giữa Gravity.TOP và Gravity.CENTER
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, getResources().getDisplayMetrics().heightPixels / 6);
        toast.setDuration(Toast.LENGTH_SHORT);  // Dài hơn một chút để Toast không biến mất quá nhanh

        // Hiển thị toast
        toast.show();
        view.bringToFront();


        // Thêm hiệu ứng di chuyển lên sau khi Toast hiển thị
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0, -300);
                animator.setDuration(500);
                animator.start();
            }
        }, 1000);
    }
}
