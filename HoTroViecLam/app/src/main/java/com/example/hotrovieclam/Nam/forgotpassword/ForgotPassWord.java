package com.example.hotrovieclam.Nam.forgotpassword;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotrovieclam.Nam.AuthencationPassWord.AuthenticationPassWord;
import com.example.hotrovieclam.Nam.login.Login;
import com.example.hotrovieclam.Nam.register.Register;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityForgotPassWordBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class ForgotPassWord extends AppCompatActivity {
private ActivityForgotPassWordBinding binding;
private FirebaseAuth mAuth;
private FirebaseFirestore db;
public Register register = new Register();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        binding = ActivityForgotPassWordBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.buttonForgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBarRes.setVisibility(View.VISIBLE);
                String email = binding.email.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(ForgotPassWord.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                    binding.progressBarRes.setVisibility(View.GONE);
                    return;
                }
                if (!register.isValidEmail(email)){
                    Toast.makeText(ForgotPassWord.this, "Email phải đúng định dạng @gmail.com", Toast.LENGTH_SHORT).show();
                    binding.email.requestFocus();
                    binding.progressBarRes.setVisibility(View.GONE);
                    return;
                }

                // Sử dụng Firebase để gửi email đặt lại mật khẩu
                checkEmailExistsAndSendResetEmail(email);
            }
        });
        binding.progressBarRes.setVisibility(View.GONE);
    }
    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showSuccessPopup(); // Hiện popup khi gửi thành công
                    } else {
                        Toast.makeText(ForgotPassWord.this, "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showSuccessPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Email đã được gửi")
                .setMessage("Vui lòng kiểm tra email của bạn để đặt lại mật khẩu.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Điều hướng đến màn hình đăng nhập
                        Intent intent = new Intent(ForgotPassWord.this, Login.class);
                        startActivity(intent);
                        finish(); // Kết thúc activity hiện tại nếu cần
                    }
                })
                .setNegativeButton("Gửi lại email", (dialog, which) -> {
                    // Gọi lại hàm gửi email
                    sendPasswordResetEmail(binding.email.getText().toString().trim());
                })
                .setCancelable(false) // Không cho phép hủy popup khi nhấn bên ngoài
                .show();
    }
    private void checkEmailExistsAndSendResetEmail(String email) {
        // Kiểm tra xem email đã tồn tại trong Firestore
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Nếu email đã tồn tại, gửi email đặt lại mật khẩu
                            sendPasswordResetEmail(email);
                        } else {
                            // Email không tồn tại
                            Toast.makeText(ForgotPassWord.this, "Email không tồn tại!", Toast.LENGTH_SHORT).show();
                            binding.progressBarRes.setVisibility(View.GONE);
                        }
                    } else {
                        // Lỗi khi kiểm tra email
                        Toast.makeText(ForgotPassWord.this, "Lỗi khi kiểm tra email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        binding.progressBarRes.setVisibility(View.GONE);
                    }
                });
    }


}