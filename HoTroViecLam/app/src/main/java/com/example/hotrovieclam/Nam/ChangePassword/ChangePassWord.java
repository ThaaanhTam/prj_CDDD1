package com.example.hotrovieclam.Nam.ChangePassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.Nam.login.Login;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityChangePassWordBinding;
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
                                            Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(this, "Mật khẩu hiện tại không đúng.", Toast.LENGTH_SHORT).show();
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
}
