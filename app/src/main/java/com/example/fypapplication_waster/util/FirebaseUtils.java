package com.example.fypapplication_waster.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public final class FirebaseUtils {
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();

    public static FirebaseStorage getFirebaseStorageInstance() {
        return storage;
    }

    public static StorageReference encodeBitmapAndUploadToFirebase(DialogFragment fragment, Bitmap bitmap, Uri bitmapUri) {
        // Encode Bitmap to Byte Stream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] encodedBitmap = baos.toByteArray();

        // Get reference to the root directory of Firebase storage
        StorageReference storageRef = getFirebaseStorageInstance().getReference();

        // Get reference to the target directory of the image we are uploading
        StorageReference binImageRef = storageRef.child("images/" + bitmapUri.getLastPathSegment());
        UploadTask uploadTask = binImageRef.putBytes(encodedBitmap);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(fragment.getTag(), "Failed image upload to Firebase");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(fragment.getTag(), "Successful image upload to Firebase");
            }
        });

        return binImageRef;
    }

    public static void downloadEncodedBitmapFromFirebase(DialogFragment fragment, StorageReference binImageRef, ImageView imageView) {
        // TODO Show taken image above take picture button, with progress bar overlay
        final long TWENTY_MEGABYTES = 20*(1024 * 1024);

        binImageRef.getBytes(TWENTY_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d(fragment.getTag(), "Successful image download from Firebase");
                Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(decodedByte);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.e(fragment.getTag(), "Failed image download from Firebase");
            }
        });
    }
}
