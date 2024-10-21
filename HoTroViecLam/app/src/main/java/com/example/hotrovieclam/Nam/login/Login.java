package com.example.hotrovieclam.Nam.login;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotrovieclam.MainActivity;
import com.example.hotrovieclam.Nam.forgotpassword.ForgotPassWord;
import com.example.hotrovieclam.Nam.register.Register;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {
    private ActivityLoginBinding binding;
    FirebaseAuth mAuth;
    Register register = new Register();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.editTextEmail.getText().toString().trim();
                String pass = binding.editTextPassword.getText().toString().trim();

                // Kiểm tra nếu email hoặc password trống
                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(Login.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return; // Dừng lại nếu chưa nhập đầy đủ
                }

                // Kiểm tra định dạng email
                if (!register.isValidEmail(email)) {
                    Toast.makeText(Login.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tiếp tục thực hiện đăng nhập nếu tất cả điều kiện thỏa mãn

             signIn(email, pass);

            }
        });

        binding.editTextPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                Drawable drawableRight = binding.editTextPassword.getCompoundDrawables()[DRAWABLE_RIGHT];

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (drawableRight != null && event.getRawX() >= (binding.editTextPassword.getRight() - drawableRight.getBounds().width())) {
                        // Icon drawableRight đã được nhấn
                        Log.d("EditText", "Icon hiện/ẩn mật khẩu đã được nhấn");

                        // Kiểm tra inputType và thay đổi giữa hiển thị mật khẩu và mật khẩu ẩn
                        if (binding.editTextPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                            // Hiện mật khẩu
                            binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            binding.editTextPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.eyes, 0); // Biểu tượng hiển thị ở bên phải
                        } else {
                            // Ẩn mật khẩu
                            binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            binding.editTextPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.eye, 0); // Biểu tượng ẩn ở bên phải
                        }

                        binding.editTextPassword.setSelection(binding.editTextPassword.getText().length()); // Đặt con trỏ ở cuối
                        return true;
                    }
                }
                return false;
            }
        });
        binding.textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(Login.this, Register.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
              startActivity(intent);
            }
        });
        binding.textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassWord.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });


    }
    private void signIn(String email, String password) {
        // Kiểm tra đăng nhập với Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Lấy thông tin người dùng sau khi đăng nhập thành công
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            // Kiểm tra email đã xác thực
                            Toast.makeText(Login.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            // Chuyển đến MainActivity
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Email chưa được xác thực
                            Toast.makeText(Login.this, "Vui lòng xác thực email trước khi đăng nhập!", Toast.LENGTH_SHORT).show();
                            mAuth.signOut(); // Đăng xuất người dùng chưa xác thực
                        }
                    } else {
                        // Nếu đăng nhập thất bại
                        Toast.makeText(Login.this, "Tài khoản hoặc mật khẩu không chính xác: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}