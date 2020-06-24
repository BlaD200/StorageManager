package com.example.storagemanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.storagemanager.backend.client.StoreClientTCP;
import com.example.storagemanager.databinding.ActivityMainBinding;
import com.example.storagemanager.viewmodels.LoginViewModel;
import com.example.storagemanager.viewmodels.factories.LoginVMFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

// TODO move all data to view models
public class MainActivity extends AppCompatActivity {

    private LoginViewModel mLoginViewModel;
    private BottomNavigationView mBottomNav;
    private NavController mNavController;
    private AppBarConfiguration mAppBarConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil
                .setContentView(this, R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences(USER_DATA_KEY, MODE_PRIVATE);
        mLoginViewModel = new ViewModelProvider(this,
                new LoginVMFactory(preferences)
        ).get(LoginViewModel.class);

        Toolbar toolbar = binding.toolbar;
        mBottomNav = binding.bottomNavigationView;
        mNavController = Navigation.findNavController(this, R.id.navHostFragment);
        mAppBarConfig = new AppBarConfiguration
                .Builder(R.id.goodsFragment, R.id.groupsFragment)
                .build();

        setSupportActionBar(binding.toolbar);

        NavigationUI.setupWithNavController(toolbar, mNavController, mAppBarConfig);
        NavigationUI.setupWithNavController(mBottomNav, mNavController);

        setupDestinations();

        if (!mLoginViewModel.isAuthenticated())
            navigateToLogin();

        new Thread(this::setupConnection).start();
    }

    private void setupConnection() {
        StoreClientTCP storeClientTCP = new StoreClientTCP();
        int connectionAttempts = 10;
        while (connectionAttempts >= 0) {
            try {
                if (storeClientTCP.connect())
                    try {
                        connectionAttempts = 10;
                        storeClientTCP.conversation();
                    } catch (IOException e) {
                        System.err.println("CONNECTION LOST. TRYING RECONNECT.");
                    }
            } catch (IOException e) {
                System.err.println("COULD NOT ESTABLISH CONNECTION(" + (10 - connectionAttempts) + ").");
            } finally {
                storeClientTCP.disconnect();
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
            --connectionAttempts;
        }
        System.err.println("Closing org.vsynytsyn.client.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            mLoginViewModel.logout();
            navigateToLogin();
            return true;
        }

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

    private void navigateToLogin() {
        NavOptions navOptions = new NavOptions
                .Builder()
                .setPopUpTo(R.id.goodsFragment, true)
                .build();
        mNavController.navigate(R.id.loginFragment, null, navOptions);
    }

    private static final String USER_DATA_KEY = "com.example.storagemanager.userdata";
}