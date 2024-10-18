package com.example.hotrovieclam.FireBase;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

    public void uploadImageFromUrl(String imageUrl, String fileName) {
        Picasso.get().load(imageUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // Chuyển đổi Bitmap thành Uri
                Uri fileUri = getImageUri(bitmap, fileName);
                uploadFile(fileUri);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.e("Storage", "Failed to load image: " + e.getMessage());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                // Không làm gì ở đây
            }
        });
    }

    private Uri getImageUri(Bitmap bitmap, String fileName) {
        File file = new File(context.getCacheDir(), fileName); // Lưu vào bộ nhớ cache
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // Lưu bitmap vào file
            return Uri.fromFile(file); // Trả về Uri của file
        } catch (Exception e) {
            Log.e("Storage", "Error saving bitmap to file: " + e.getMessage());
            return null;
        }
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
