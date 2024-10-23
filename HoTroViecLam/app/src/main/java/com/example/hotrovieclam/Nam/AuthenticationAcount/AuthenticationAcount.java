package com.example.hotrovieclam.Nam.AuthenticationAcount;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotrovieclam.Nam.login.Login;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.ActivityAuthenticationAcountBinding;


public class AuthenticationAcount extends AppCompatActivity {
    private ActivityAuthenticationAcountBinding binding;
    private EditText[] otpInput;
    String otpdemo = "12121";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityAuthenticationAcountBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        otpInput = new EditText[]{binding.otpId1, binding.otpId2, binding.otpId3, binding.otpId4, binding.otpId5};

        for (int i = 0; i < otpInput.length; i++) {
            final int index = i;

            // Thêm TextWatcher để quản lý hành vi khi người dùng nhập ký tự
            otpInput[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1) {
                        // Nếu nhập 1 ký tự, focus chuyển sang ô tiếp theo
                        if (index < otpInput.length - 1) {
                            otpInput[index + 1].requestFocus();
                        } else {
                            // Ẩn bàn phím nếu đã nhập đủ
                            hideKeyboard(otpInput[index]);
                        }
                    } else if (s.length() == 0 && index > 0) {
                        // Nếu xóa ký tự, focus quay lại ô trước đó
                        otpInput[index - 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }

                // Hàm ẩn bàn phím
                private void hideKeyboard(EditText editText) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                }
            });

            // Thêm OnFocusChangeListener để xoá nội dung khi ô được focus
            otpInput[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus && !TextUtils.isEmpty(((EditText) v).getText())) {
                        ((EditText) v).setText(""); // Xoá số cũ nếu đã nhập
                        binding.thongbao.setVisibility(View.GONE);
                    }
                }
            });
            binding.btnXacNhanOTP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String getotpString = layOTPtrongTextInputVaChuyenThanhChuoi();
                    if (getotpString.equals(otpdemo)){
                        Intent intent =  new Intent(AuthenticationAcount.this, Login.class);
                        startActivity(intent);
                    }
                    else {
                        binding.thongbao.setText("OTP Không chính xác");
                        binding.thongbao.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private String layOTPtrongTextInputVaChuyenThanhChuoi() {
        StringBuilder otp = new StringBuilder();
        for (EditText editText : otpInput) {
            otp.append(editText.getText().toString().trim());
        }
        return otp.toString();
    }
}
