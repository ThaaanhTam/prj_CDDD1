package com.example.hotrovieclam.FireBase;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import androidx.annotation.NonNull;


import com.example.hotrovieclam.Interface.OnUriRetrievedListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Storage {
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Context context;

    public Storage(Context context) {
        this.context = context; // Lưu context vào biến thành viên
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }


    public void uploadFile(Uri fileUri) {
        if (fileUri != null) {
            StorageReference data = storageRef.child("images/" + fileUri.getLastPathSegment());
            data.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("Storage", "Upload successful: " + fileUri.getLastPathSegment());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Storage", "Upload failed: " + e.getMessage());
                        }
                    });
        }
    }
}
