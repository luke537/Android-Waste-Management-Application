package com.example.fypapplication_waster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    new RecycleFragment()).commit();
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                    if (item.getItemId() != R.id.navMap) {
                        Fragment selectedFragment = null;

                        switch (item.getItemId()) {
                            case R.id.navNewRecycle:
                                selectedFragment = new RecycleFragment();
                                break;
                            case R.id.navMap:
                                selectedFragment = new AllBinsMapFragment();
                                break;
                            case R.id.navProfile:
                                selectedFragment = new ProfileFragment();
                                break;
                        }

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                                selectedFragment).commit();
//                    }

//                    else {
//                        MapFragment selectedFragment = new AllBinsMapFragment();
//                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
//                                selectedFragment).commit();
//
//                    }



                    return true;
                }
            };
}
