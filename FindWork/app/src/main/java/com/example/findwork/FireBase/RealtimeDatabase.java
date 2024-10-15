package com.example.findwork.FireBase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.findwork.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RealtimeDatabase {


  DatabaseReference  mDatabase = FirebaseDatabase.getInstance().getReference();





    public void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);
        // Ghi dữ liệu vào node "users/userId"
        mDatabase.child("users").child(userId).setValue(user);
    }
    public void readDataFromFirebase() {
        // Giả sử bạn có một node "users" trong database
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Dữ liệu đã thay đổi, cập nhật UI
                StringBuilder data = new StringBuilder();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String username = userSnapshot.child("name").getValue(String.class);
                    data.append(username).append("\n");
                    Log.w("Firebase", username);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi
                Log.w("Firebase", "loadUsers:onCancelled", databaseError.toException());
            }
        });
    }
}
