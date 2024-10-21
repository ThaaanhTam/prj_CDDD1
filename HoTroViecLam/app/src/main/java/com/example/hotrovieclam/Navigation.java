package com.example.hotrovieclam;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.hotrovieclam.Fragment.AcountFragment;
import com.example.hotrovieclam.Fragment.Home;
import com.example.hotrovieclam.Fragment.ManagerPostFragment;
import com.example.hotrovieclam.Fragment.MessageFragment;
import com.example.hotrovieclam.Fragment.Save_job;
import com.example.hotrovieclam.databinding.NavigationBinding;
import com.google.android.material.navigation.NavigationBarView;

public class Navigation extends AppCompatActivity {

    private NavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = NavigationBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        khởi chạy màn hình home đầu tiên
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new Home())
                    .commit();
        }
        binding.navButtom.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                khuyến khích dùng if else dùng swith case sex bị lỗi R.id......
                Fragment selectedFragment = null;
                int id = item.getItemId();
                if (id == R.id.home) {
                    selectedFragment = new Home();
                } else if (id == R.id.saved) {
                    selectedFragment = new Save_job();
                } else if (id == R.id.managerPost) {
                    selectedFragment = new ManagerPostFragment();

                } else if (id == R.id.message) {
                    selectedFragment = new MessageFragment();
                } else if (id == R.id.accout) {
                    selectedFragment = new AcountFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
                return false;
            }
        });
    }
}
