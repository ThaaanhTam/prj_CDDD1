package com.example.hotrovieclam.Nam.register;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotrovieclam.Nam.AuthenticationAcount.AuthenticationAcount;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityRegisterBinding;


public class Register extends AppCompatActivity {
private ActivityRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                Drawable drawableRight = binding.password.getCompoundDrawables()[DRAWABLE_RIGHT];

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (drawableRight != null && event.getRawX() >= (binding.password.getRight() - drawableRight.getBounds().width())) {
                        // Icon drawableRight đã được nhấn
                        Log.d("EditText", "Icon hiện/ẩn mật khẩu đã được nhấn");

                        // Kiểm tra inputType và thay đổi giữa hiển thị mật khẩu và mật khẩu ẩn
                        if (binding.password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                            // Hiện mật khẩu
                            binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            binding.password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.eyes, 0); // Biểu tượng hiển thị ở bên phải
                        } else {
                            // Ẩn mật khẩu
                            binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            binding.password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.eye, 0); // Biểu tượng ẩn ở bên phải
                        }

                        binding.password.setSelection(binding.password.getText().length()); // Đặt con trỏ ở cuối
                        return true;
                    }
                }
                return false;
            }
        });
        binding.fogetpass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                Drawable drawableRight = binding.fogetpass.getCompoundDrawables()[DRAWABLE_RIGHT];

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (drawableRight != null && event.getRawX() >= (binding.fogetpass.getRight() - drawableRight.getBounds().width())) {
                        // Icon drawableRight đã được nhấn
                        Log.d("EditText", "Icon hiện/ẩn mật khẩu đã được nhấn");

                        // Kiểm tra inputType và thay đổi giữa hiển thị mật khẩu và mật khẩu ẩn
                        if (binding.fogetpass.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                            // Hiện mật khẩu
                            binding.fogetpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            binding.fogetpass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.eyes, 0); // Biểu tượng hiển thị ở bên phải
                        } else {
                            // Ẩn mật khẩu
                            binding.fogetpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            binding.fogetpass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.eye, 0); // Biểu tượng ẩn ở bên phải
                        }

                        binding.password.setSelection(binding.password.getText().length()); // Đặt con trỏ ở cuối
                        return true;
                    }
                }
                return false;
            }
        });
        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, AuthenticationAcount.class);
                startActivity(intent);
            }
        });
    }
}