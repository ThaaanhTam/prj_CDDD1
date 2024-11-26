package com.example.hotrovieclam.Model;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.CustomToastSuccessBinding;

public class HieuUngThongBao {
    // Hàm hiển thị Toast thành công
    public static void showSuccessToast(Context context, String message) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_toast_success, null);
        CustomToastSuccessBinding binding = CustomToastSuccessBinding.bind(view);

        Toast toast = new Toast(context);
        toast.setView(view);
        binding.messs.setText(message);
        view.setBackgroundResource(R.drawable.custom_toast_success);

        // Cập nhật Gravity cho Toast
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, context.getResources().getDisplayMetrics().heightPixels / 6);
        toast.setDuration(Toast.LENGTH_SHORT);

        // Hiển thị Toast
        toast.show();

        // Hiệu ứng di chuyển lên
        view.postDelayed(() -> {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0, -300);
            animator.setDuration(500);
            animator.start();
        }, 1000);
    }

    // Hàm hiển thị Toast thất bại
    public static void showErrorToast(Context context, String message) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_toast_success, null);
        CustomToastSuccessBinding binding = CustomToastSuccessBinding.bind(view);

        Toast toast = new Toast(context);
        toast.setView(view);
        binding.messs.setText(message);
        view.setBackgroundResource(R.drawable.custom_toast_error);
        binding.sgv.setImageResource(R.drawable.error_svgrepo_com);

        // Cập nhật Gravity cho Toast
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, context.getResources().getDisplayMetrics().heightPixels / 6);
        toast.setDuration(Toast.LENGTH_SHORT);

        // Hiển thị Toast
        toast.show();

        // Hiệu ứng di chuyển lên
        view.postDelayed(() -> {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0, -300);
            animator.setDuration(500);
            animator.start();
        }, 1000);
    }

    // Hàm hiệu ứng quay vòng nhanh
    public static void startLoadingAnimation(View loadingView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(loadingView, "rotation", 0f, 360f);
        animator.setDuration(300);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();
    }
}

