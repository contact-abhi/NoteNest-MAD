package com.example.notenest;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.notenest.databinding.ActivityHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Home Activity with bottom navigation and fragment container
 */
public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentManager = getSupportFragmentManager();

        // Set up bottom navigation
        setupBottomNavigation();

        // Load home fragment by default
        if (savedInstanceState == null) {
            loadFragment(HomeFragment.newInstance());
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = binding.bottomNavigation;

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(HomeFragment.newInstance());
                return true;
            } else if (itemId == R.id.nav_sections) {
                loadFragment(SectionsFragment.newInstance());
                return true;
            } else if (itemId == R.id.nav_profile) {
                loadFragment(ProfileFragment.newInstance());
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
