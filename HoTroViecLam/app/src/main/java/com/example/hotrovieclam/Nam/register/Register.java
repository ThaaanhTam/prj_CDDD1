package com.example.hotrovieclam.Nam.register;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotrovieclam.Model.User;
import com.example.hotrovieclam.Nam.login.Login;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Xử lý sự kiện hiện/ẩn mật khẩu cho các trường password và passwordAgain
        setupPasswordToggle();

        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Vô hiệu hóa nút đăng ký
                binding.progressBarRes.setVisibility(View.VISIBLE);
                binding.buttonRegister.setEnabled(false);

                String name = binding.name.getText().toString().trim();
                String email = binding.email.getText().toString().trim();
                String sdt = binding.phone.getText().toString().trim();
                String pass = binding.password.getText().toString().trim();
                String passAgain = binding.passwordAgain.getText().toString().trim();

                // Kiểm tra trường rỗng
                if (name.isEmpty()) {
                    binding.progressBarRes.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
                    binding.name.requestFocus();
                    binding.buttonRegister.setEnabled(true); // Khôi phục nút khi có lỗi
                    return;
                }

                if (email.isEmpty()) {
                    binding.progressBarRes.setVisibility(View.GONE);

                    Toast.makeText(Register.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                    binding.email.requestFocus();
                    binding.buttonRegister.setEnabled(true); // Khôi phục nút khi có lỗi
                    return;
                }

                if (sdt.isEmpty()) {
                    binding.progressBarRes.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                    binding.phone.requestFocus();
                    binding.buttonRegister.setEnabled(true); // Khôi phục nút khi có lỗi
                    return;
                }

                if (pass.isEmpty()) {
                    binding.progressBarRes.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                    binding.password.requestFocus();
                    binding.buttonRegister.setEnabled(true); // Khôi phục nút khi có lỗi
                    return;
                }

                if (passAgain.isEmpty()) {
                    binding.progressBarRes.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Vui lòng nhập lại mật khẩu", Toast.LENGTH_SHORT).show();
                    binding.passwordAgain.requestFocus();
                    binding.buttonRegister.setEnabled(true); // Khôi phục nút khi có lỗi
                    return;
                }

                // Kiểm tra mật khẩu khớp
                if (!pass.equals(passAgain)) {
                    binding.progressBarRes.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                    binding.passwordAgain.requestFocus();
                    binding.buttonRegister.setEnabled(true); // Khôi phục nút khi có lỗi
                    return;
                }

                // Kiểm tra độ dài mật khẩu
                if (pass.length() < 6) {
                    binding.progressBarRes.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Mật khẩu phải tối thiểu 6 kí tự", Toast.LENGTH_SHORT).show();
                    binding.password.requestFocus();
                    binding.buttonRegister.setEnabled(true); // Khôi phục nút khi có lỗi
                    return;
                }

                if (!isValidEmail(email)) {
                    binding.progressBarRes.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Email phải đúng định dạng Abc@gmail.com", Toast.LENGTH_SHORT).show();
                    binding.email.requestFocus();
                    binding.buttonRegister.setEnabled(true); // Khôi phục nút khi có lỗi
                    return;
                }
                if (sdt.length() <= 10) {
                    binding.progressBarRes.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Số điện thoại phải là 10 số", Toast.LENGTH_SHORT).show();
                    binding.phone.requestFocus();
                    binding.buttonRegister.setEnabled(true); // Khôi phục nút khi có lỗi
                    return;
                }

                // Gọi phương thức tạo tài khoản
                createAccount(name, email, sdt, pass);
            }
        });
        binding.progressBarRes.setVisibility(View.GONE);
        binding.dangnhapngay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

    }

    public boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._%+-]+@gmail\\.com";
        return email.matches(emailPattern);
    }


    private void setupPasswordToggle() {
        binding.password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                togglePasswordVisibility(event, binding.password);
                return false;
            }
        });
        binding.passwordAgain.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                togglePasswordVisibility(event, binding.passwordAgain);
                return false;
            }
        });
    }

    private void togglePasswordVisibility(MotionEvent event, View view) {
        final int DRAWABLE_RIGHT = 2;
        Drawable drawableRight = ((android.widget.EditText) view).getCompoundDrawables()[DRAWABLE_RIGHT];
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (drawableRight != null && event.getRawX() >= (((android.widget.EditText) view).getRight() - drawableRight.getBounds().width())) {
                if (((android.widget.EditText) view).getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    ((android.widget.EditText) view).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    ((android.widget.EditText) view).setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.eyes, 0);
                } else {
                    ((android.widget.EditText) view).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ((android.widget.EditText) view).setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.eye, 0);
                }
                ((android.widget.EditText) view).setSelection(((android.widget.EditText) view).getText().length());
            }
        }
    }

    private void createAccount(String name, String email, String phone, String password) {
        // Kiểm tra xem email đã tồn tại trong Firestore chưa
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Nếu email đã tồn tại
                            Toast.makeText(Register.this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
                            binding.email.requestFocus();
                            binding.progressBarRes.setVisibility(View.GONE);

                        } else {
                            // Nếu email chưa tồn tại, tiếp tục tạo tài khoản
                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, authTask -> {
                                if (authTask.isSuccessful()) {
                                    // Đăng ký thành công, gửi email xác thực
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        // Tạo đối tượng User
                                        User user = new User(firebaseUser.getUid(), name, email, phone, 1, null, new Timestamp(new Date().getTime()), new Timestamp(new Date().getTime()));

                                        // Lưu thông tin người dùng vào Firestore
                                        saveUserToFirestore(user);

                                        // Gửi email xác thực
                                        firebaseUser.sendEmailVerification().addOnCompleteListener(verificationTask -> {
                                            if (verificationTask.isSuccessful()) {
                                                // Hiển thị AlertDialog thông báo người dùng kiểm tra email
                                                new AlertDialog.Builder(Register.this)
                                                        .setTitle("Xác nhận tài khoản")
                                                        .setMessage("Vui lòng kiểm tra email để xác nhận tài khoản của bạn!")
                                                        .setPositiveButton("OK", (dialog, which) -> {
                                                            clearInputFields(); // Xóa các trường nhập
                                                            mAuth.signOut(); // Đăng xuất sau khi gửi mail xác nhận
                                                            binding.progressBarRes.setVisibility(View.GONE);
                                                        })
                                                        .setCancelable(false) // Không cho phép đóng dialog khi bấm ngoài
                                                        .show();


                                            } else {
                                                // Gửi email xác thực thất bại.

                                                Toast.makeText(Register.this, "Lỗi khi gửi email xác thực: " + verificationTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                binding.progressBarRes.setVisibility(View.GONE);

                                            }
                                        });
                                    }
                                } else {
                                    // Đăng ký thất bại
                                    Toast.makeText(Register.this, "Đăng ký thất bại: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    binding.progressBarRes.setVisibility(View.GONE);

                                }
                            });
                        }
                    } else {
                        Toast.makeText(Register.this, "Lỗi khi kiểm tra email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        binding.progressBarRes.setVisibility(View.GONE);

                    }
                });
    }


    private void saveUserToFirestore(User user) {
        // Lưu thông tin người dùng vào Firestore
        db.collection("users").document(user.getId()).set(user).addOnSuccessListener(aVoid -> {
            //Toast.makeText(Register.this, "Thông tin người dùng đã được lưu vào Firestore", Toast.LENGTH_SHORT).show();
            binding.progressBarRes.setVisibility(View.GONE);

        }).addOnFailureListener(e -> {
            Toast.makeText(Register.this, "Lỗi khi lưu thông tin người dùng", Toast.LENGTH_SHORT).show();
            binding.progressBarRes.setVisibility(View.GONE);

        });
    }

    private void clearInputFields() {
        binding.buttonRegister.setEnabled(true);
        binding.name.setText("");
        binding.email.setText("");
        binding.phone.setText("");
        binding.password.setText("");
        binding.passwordAgain.setText("");
    }
}
