package com.example.storagemanager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.storagemanager.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNav;
    private NavController mNavController;
    private AppBarConfiguration mAppBarConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil
                .setContentView(this, R.layout.activity_main);

        Toolbar toolbar = binding.toolbar;
        mBottomNav = binding.bottomNavigationView;
        mNavController = Navigation.findNavController(this, R.id.navHostFragment);
        mAppBarConfig = new AppBarConfiguration
                .Builder(R.id.allGoodsFragment, R.id.groupsFragment)
                .build();

        setSupportActionBar(binding.toolbar);

        NavigationUI.setupWithNavController(toolbar, mNavController, mAppBarConfig);
        NavigationUI.setupWithNavController(mBottomNav, mNavController);

        setupDestinations();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) ;
        // TODO logout

        return super.onOptionsItemSelected(item);
    }

    private void setupDestinations() {
        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (mAppBarConfig.getTopLevelDestinations().contains(destination.getId()))
                mBottomNav.setVisibility(View.VISIBLE);
            else
                mBottomNav.setVisibility(View.GONE);
        });
    }
}