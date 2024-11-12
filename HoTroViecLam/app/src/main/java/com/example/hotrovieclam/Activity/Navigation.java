package com.example.hotrovieclam.Activity;

import static android.app.PendingIntent.getActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.hotrovieclam.Adapter.MyRecyclerViewAdapter;

import com.example.hotrovieclam.Fragment.AcountFragment;
import com.example.hotrovieclam.Fragment.ConvestationFrament;
import com.example.hotrovieclam.Fragment.Home;
import com.example.hotrovieclam.Fragment.RecruiterManagement.Recruiter_Management;
import com.example.hotrovieclam.Fragment.Save_job;
import com.example.hotrovieclam.Model.Job;
import com.example.hotrovieclam.Model.UserSessionManager;
import com.example.hotrovieclam.R;
import com.example.hotrovieclam.databinding.NavigationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Navigation extends AppCompatActivity {

    private NavigationBinding binding;
    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    private ArrayList<Job> jobList; // Khai báo danh sách công việc
    //luu fragmrnt hien tai
    private int currentFragmentId = -1;
    UserSessionManager user = new UserSessionManager();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle  savedInstanceState) {
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
        Log.d("YY", "onCreate: "+user.getUserUid());
        Log.d("YY", "Call");
        checkTypeUser(user.getUserUid());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
        }


        setupBottomNavigation();


    }
    private void setupBottomNavigation() {
        binding.navButtom.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                // Kiểm tra xem người dùng có nhấn vào mục đang hiển thị không
                if (id == currentFragmentId) {
                    return false; // Không thực hiện gì nếu Fragment đang hiển thị
                }

                // Cập nhật currentFragmentId với ID mới
                currentFragmentId = id;
                Fragment selectedFragment = null;

                if (id == R.id.home) {
                    selectedFragment = new Home();
                } else if (id == R.id.saved) {
                    selectedFragment = new Save_job();
                } else if (id == R.id.managerPost) {
                    selectedFragment = new Recruiter_Management();
                } else if (id == R.id.message) {
                    selectedFragment = new ConvestationFrament();
                } else if (id == R.id.accout) {
                    selectedFragment = new AcountFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });
    }
    public void checkTypeUser(String uid) {
        if (uid == null || uid.isEmpty()) {
            Log.e("checkTypeUser", "UID không hợp lệ");
            return;  // Dừng lại nếu UID không hợp lệ
        }

        DocumentReference docRef = db.collection("users").document(uid);

        // Lắng nghe thời gian thực
        docRef.addSnapshotListener((documentSnapshot, error) -> {
            if (error != null) {
                Log.w("Firestore", "Lỗi khi lắng nghe thay đổi dữ liệu", error);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                Long typeUser = documentSnapshot.getLong("userTypeId");
                Log.d("VANTAR", "UserType thay đổi: " + typeUser);

                if (binding.navButtom != null) {
                    MenuItem menuItem = binding.navButtom.getMenu().findItem(R.id.managerPost);

                    // Kiểm tra nếu `menuItem` không null trước khi gọi `setVisible`
                    if (menuItem != null) {
                        if (typeUser != null && typeUser == 1) {
                            menuItem.setVisible(false);
                        } else {
                            menuItem.setVisible(true);
                        }
                    } else {
                        Log.e("Menu", "MenuItem với ID 'managerPost' không tồn tại.");
                    }
                } else {
                    Log.e("NavBottom", "Navigation Bottom chưa được khởi tạo.");
                }
            } else {
                Log.d("Firestore", "Tài liệu không tồn tại.");
            }
        });
    }


}
