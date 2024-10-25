package com.example.hotrovieclam.FireBase;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;


import com.example.hotrovieclam.Interface.OnUriRetrievedListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Storage {
    private StorageReference mStorageRef;
    private Context mContext;

    public void getUri(final OnUriRetrievedListener listener, String path) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child(path);

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                listener.onUriRetrieved(uri); // Trả về Uri khi thành công
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Firebase", "Lỗi khi lấy URL", exception); // Xử lý lỗi
            }
        });
    }








}
