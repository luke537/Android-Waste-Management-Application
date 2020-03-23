package com.example.fypapplication_waster.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public final class CameraUtils {
    private static final int REQUEST_TAKE_PHOTO = 1;
    private final static int MY_PERMISSIONS_REQUEST_CAMERA = 2;

    private Uri photoUri = null;

    private static boolean hasPermission(@NonNull Context context) {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private static void askForPermission(@NonNull Fragment fragment) {
        fragment.requestPermissions(
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                },
                MY_PERMISSIONS_REQUEST_CAMERA
        );
    }

    private static File createImageFile(Fragment fragment) throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = fragment.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    public static void onRequestPermissionsResult(Fragment fragment, int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntentAndGetUri(fragment);
                } else {
                    Toast.makeText(fragment.getActivity(), " Camera Permission Denied", Toast.LENGTH_LONG).show();
                }
        }
    }

    public static Uri dispatchTakePictureIntentAndGetUri(Fragment fragment) {
        if (!hasPermission(fragment.getContext())) {
            askForPermission(fragment);
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        Uri photoUri = null;

        if (takePictureIntent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(fragment);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(fragment.getContext(),
                        "com.example.fypapplication_waster.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                fragment.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
        return photoUri;
    }

    public static Bitmap onCameraActivityResult(Fragment fragment, int requestCode, int resultCode, Intent data, Uri photoUri) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap imageBitmap = null;
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(fragment.getActivity().getContentResolver(), photoUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return imageBitmap;
            }
        }
        return null;
    }
}
