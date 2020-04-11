package com.example.fypapplication_waster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    final RecycleFragment recycleFragment = new RecycleFragment();
    final AllBinsMapFragment allBinsMapFragment = new AllBinsMapFragment();
    final ProfileFragment profileFragment = new ProfileFragment();

    Fragment activeFragment = recycleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, recycleFragment).commit();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.navNewRecycle);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) { // Check which tab is selected
                        case R.id.navNewRecycle: // If Recycle tab is selected
                            if (recycleFragment.isAdded()) { // Check if an instance of this Fragment exists on the backstack already
                                getSupportFragmentManager().beginTransaction().hide(activeFragment).show(recycleFragment).commit();
                            }
                            else { // Add Fragment to the backstack if it is not present on the stack already
                                getSupportFragmentManager().beginTransaction()
                                        .add(R.id.fragmentContainer, recycleFragment, "recycleFragment")
                                        .hide(activeFragment).commit();
                            }

                            activeFragment = recycleFragment; // Make this the active Fragment
                            break;

                        case R.id.navMap: // If map with all bins tab is selected
                            if (allBinsMapFragment.isAdded()) { // Check if an instance of this Fragment exists on the backstack already
                                getSupportFragmentManager().beginTransaction().hide(activeFragment).show(allBinsMapFragment).commit();
                            }
                            else { // Add Fragment to the backstack if it is not present on the stack already
                                getSupportFragmentManager().beginTransaction()
                                        .add(R.id.fragmentContainer, allBinsMapFragment, "allBinsMapFragment")
                                        .hide(activeFragment).commit();
                            }

                            activeFragment = allBinsMapFragment; // Make this the active Fragment
                            break;

                        case R.id.navProfile: // If Profile tab is selected
                            if (profileFragment.isAdded()) { // Check if an instance of this Fragment exists on the backstack already
                                getSupportFragmentManager().beginTransaction().hide(activeFragment).show(profileFragment).commit();
                            }
                            else { // Add Fragment to the backstack if it is not present on the stack already
                                getSupportFragmentManager().beginTransaction()
                                        .add(R.id.fragmentContainer, profileFragment, "profileFragment")
                                        .hide(activeFragment).commit();
                            }

                            activeFragment = profileFragment; // Make this the active Fragment
                            break;
                    }

                    return true;
                }
            };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == AllBinsMapFragment.MY_PERMISSIONS_REQUEST_LOCATION){
            allBinsMapFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        else if (requestCode == RecycleFragment.MY_PERMISSIONS_REQUEST_LOCATION){
            recycleFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
