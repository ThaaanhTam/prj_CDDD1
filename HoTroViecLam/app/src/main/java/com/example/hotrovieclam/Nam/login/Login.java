package com.example.hotrovieclam.Nam.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotrovieclam.Activity.MainActivity;
import com.example.hotrovieclam.Activity.Navigation;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.Nam.forgotpassword.ForgotPassWord;
import com.example.hotrovieclam.Nam.register.Register;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class Login extends AppCompatActivity {
    private ActivityLoginBinding binding;
    FirebaseAuth mAuth;
    Register register = new Register();
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        binding.editTextPassword.setText("777777");
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String id = sharedPreferences.getString("user_uid", null);
        Log.d("KK", "onCreate: " + id);

        if (id != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(id);

            ((DocumentReference) userRef).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Lấy thông tin của user từ document
                        String name = document.getString("name");
                        String email = document.getString("email");
                        binding.editTextEmail.setText(email);
                        binding.editTextPassword.requestFocus();
                        // Log ra thông tin user
                        Log.d("KK", "User Name: " + name);
                        Log.d("KK", "User Email: " + email);
                    } else {
                        Log.d("KK", "User not found.");
                    }
                } else {
                    Log.e("KK", "Error getting user info: ", task.getException());
                }
            });
        }
        setupPasswordToggle(binding.editTextPassword, R.drawable.lock, R.drawable.eye, R.drawable.eyes);


        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        binding.textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reister();
            }
        });
        binding.textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetpassWord();
            }
        });


    }

    private void login() {

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

    private void forgetpassWord() {
        Intent intent = new Intent(Login.this, ForgotPassWord.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void reister() {
        Intent intent = new Intent(Login.this, Register.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void signIn(String email, String password) {
        binding.progressBarLogin.setVisibility(View.VISIBLE);
        // Kiểm tra đăng nhập với Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Lấy thông tin người dùng sau khi đăng nhập thành công
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user_uid", user.getUid());
                            editor.apply();  // Lưu thay đổi
                            Toast.makeText(Login.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                            String uid = user.getUid();
                            // Lưu UID vào UserSessionManager
                            UserSessionManager sessionManager = new UserSessionManager();
                            sessionManager.setUserUid(uid);  // Lưu UID vào session
                            // Chuyển đến MainActivity
                            Intent intent = new Intent(Login.this, Navigation.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Email chưa được xác thực
                            Toast.makeText(Login.this, "Vui lòng xác thực email trước khi đăng nhập!", Toast.LENGTH_SHORT).show();
                            binding.progressBarLogin.setVisibility(View.GONE);
                            mAuth.signOut(); // Đăng xuất người dùng chưa xác thực
                        }
                    } else {
                        // Nếu đăng nhập thất bại
                        Toast.makeText(Login.this, "Tài khoản hoặc mật khẩu không chính xác: ", Toast.LENGTH_SHORT).show();
                        binding.progressBarLogin.setVisibility(View.GONE);

                    }
                });
    }

    private void setupPasswordToggle(final EditText editText, int lockDrawable, int eyeDrawableHidden, int eyeDrawableVisible) {
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                Drawable drawableRight = editText.getCompoundDrawables()[DRAWABLE_RIGHT];

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (drawableRight != null && event.getRawX() >= (editText.getRight() - drawableRight.getBounds().width())) {
                        // Icon drawableRight đã được nhấn
                        Log.d("EditText", "Icon hiện/ẩn mật khẩu đã được nhấn");

                        // Kiểm tra inputType và thay đổi giữa hiển thị mật khẩu và mật khẩu ẩn
                        if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                            // Hiện mật khẩu
                            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            editText.setCompoundDrawablesWithIntrinsicBounds(lockDrawable, 0, eyeDrawableVisible, 0); // Biểu tượng hiển thị ở bên phải
                        } else {
                            // Ẩn mật khẩu
                            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            editText.setCompoundDrawablesWithIntrinsicBounds(lockDrawable, 0, eyeDrawableHidden, 0); // Biểu tượng ẩn ở bên phải
                        }

                        editText.setSelection(editText.getText().length()); // Đặt con trỏ ở cuối
                        return true;
                    }
                }
                return false;
            }
        });
    }


}