package com.example.hotrovieclam.Activity;

import static android.app.PendingIntent.getActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
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
import com.example.hotrovieclam.Model.RunBackGround;
import com.example.hotrovieclam.Model.StatusThongBao;
import com.example.hotrovieclam.Model.ThongBaoAndroid;
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
    private StatusThongBao status ;

    @Override
    protected void onCreate(Bundle  savedInstanceState) {
        binding = NavigationBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
         status = new StatusThongBao(this);

        // Kiểm tra quyền POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ////
        Intent serviceIntent = new Intent(this, RunBackGround.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }


//        khởi chạy màn hình home đầu tiên
        Log.d("YY", "onCreate: " + user.getUserUid());
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
                return; // Dừng lại nếu UID không hợp lệ
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

                    // Kiểm tra nếu typeUser thay đổi và thông báo chưa được gửi
                    if (typeUser != null) {
                        Log.d("CCC", "checkTypeUser: "+status.isNotificationSent());
                        if (status.isNotificationSent()) { // Kiểm tra nếu chưa gửi thông báo
                            if (typeUser == 2) {
                                ThongBaoAndroid.sendNotification(
                                        this,
                                        "Bạn đã trở thành nhà tuyển dụng",
                                        "Bạn có thể vào app để đăng công việc bạn ứng tuyển"
                                );
                                Log.d("Notification", "Thông báo: Bạn đã trở thành nhà tuyển dụng");
                            } else if (typeUser == 1) {
                                ThongBaoAndroid.sendNotification(
                                        this,
                                        "Bạn bị hủy với tư cách nhà ứng tuyển",
                                        "Chúng tôi nhận thấy bạn vi phạm chính sách bảo mật của chúng tôi nên tài khoản của bạn sẽ bị khóa"
                                );
                                Log.d("Notification", "Thông báo: Bạn bị hủy với tư cách nhà ứng tuyển");
                            }

                            // Sau khi gửi thông báo, lưu trạng thái đã gửi
                            status.setNotificationSent(true);
                            Log.d("NotificationStatus", "Đã lưu trạng thái thông báo: true");
                        } else {
                            Log.d("NotificationStatus", "Thông báo đã được gửi trước đó.");
                        }
                    }

                    // Cập nhật giao diện menu theo loại user
                    if (binding.navButtom != null) {
                        MenuItem menuItem = binding.navButtom.getMenu().findItem(R.id.managerPost);
                        if (menuItem != null) {
                            menuItem.setVisible(typeUser != null && typeUser != 1); // Ẩn với userType 1
                        }
                    }

                    Log.d("FirestoreSnapshot", "Dữ liệu: " + documentSnapshot.getData());
                } else {
                    Log.d("Firestore", "Tài liệu không tồn tại.");
                }
            });
        }


}
