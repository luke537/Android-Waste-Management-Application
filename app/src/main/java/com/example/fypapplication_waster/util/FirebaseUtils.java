package com.example.fypapplication_waster.util;

import com.google.firebase.storage.FirebaseStorage;

public final class FirebaseUtils {
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();

    public static FirebaseStorage getFirebaseStorageInstance() {
        return storage;
    }
}
