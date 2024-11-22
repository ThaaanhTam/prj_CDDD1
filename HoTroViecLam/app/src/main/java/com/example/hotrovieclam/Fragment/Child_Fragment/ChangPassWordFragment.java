package com.example.hotrovieclam.Fragment.Child_Fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hotrovieclam.Model.HieuUngThongBao;
import com.example.hotrovieclam.Nam.ChangePassword.ChangePassWord;
import com.example.hotrovieclam.Nam.login.Login;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.FragmentChangPassWordBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ChangPassWordFragment extends Fragment {
private FragmentChangPassWordBinding binding;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChangPassWordBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        mAuth = FirebaseAuth.getInstance();
        setupPasswordToggle(binding.password, R.drawable.lock, R.drawable.eye, R.drawable.eyes);
        setupPasswordToggle(binding.passwordNew, R.drawable.lock, R.drawable.eye, R.drawable.eyes);
        setupPasswordToggle(binding.passwordAgain, R.drawable.lock, R.drawable.eye, R.drawable.eyes);
        getDataEDT();
        quayLaiManHinhTruocDo();
        return  view;
    }
    public void quayLaiManHinhTruocDo(){
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                }
            }
        });
    }
    private void changePassword(String currentPassword, String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            HieuUngThongBao.startLoadingAnimation(binding.progressBar);
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
                                            HieuUngThongBao.showSuccessToast(requireContext(),"Đổi mật khẩu thành công!");

                                            //Toast.makeText(getContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                                            // Chuyển về màn hình đăng nhập sau khi đổi mật khẩu
                                            Intent intent = new Intent(getActivity(), Login.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            (getActivity()).finish();
                                        } else {
                                            Toast.makeText(getContext(), "Không thể đổi mật khẩu. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            HieuUngThongBao.showErrorToast(requireContext(),"Mật khẩu hiện tại không đúng");

                            //Toast.makeText(getContext(), "Mật khẩu hiện tại không đúng.", Toast.LENGTH_SHORT).show();
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            HieuUngThongBao.showErrorToast(requireContext(),"Có lỗi xảy ra. Vui lòng đăng nhập lại");

            //Toast.makeText(getContext(), "Có lỗi xảy ra. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            // Chuyển người dùng về màn hình đăng nhập nếu không tìm thấy user
            Intent intent = new Intent(getActivity(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            (getActivity()).finish();
        }
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
    public void getDataEDT(){
        binding.buttonChangpass.setOnClickListener(v -> {
            String currentPassword = binding.password.getText().toString().trim();
            String newPassword = binding.passwordNew.getText().toString().trim();
            String confirmPassword = binding.passwordAgain.getText().toString().trim();

            // Kiểm tra tính hợp lệ của các trường
            if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(getContext(), "Vui lòng điền tất cả các trường", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                HieuUngThongBao.showErrorToast(requireContext(),"Mật khẩu mới không khớp");
                //Toast.makeText(getContext(), "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
                return;
            }

            changePassword(currentPassword, newPassword);
        });
    }

}