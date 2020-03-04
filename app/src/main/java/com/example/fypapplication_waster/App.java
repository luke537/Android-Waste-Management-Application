package com.example.fypapplication_waster;

import android.app.Application;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class App extends Application {
    private static final String CLOUDINARY_CLOUD_NAME = "wasterimgdb";

    @Override
    public void onCreate() {
        super.onCreate();
        Map config = new HashMap();
        config.put("cloud_name", CLOUDINARY_CLOUD_NAME);
        config.put("api_key", "928464184197124");
        config.put("api_secret", "Ax9-B9bxCgiy5LgGzuSiUF1iZ7c");
        MediaManager.init(this, config);
    }
}
