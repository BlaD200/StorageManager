package com.example.storagemanager.viewmodels;

import android.content.SharedPreferences;

import androidx.lifecycle.ViewModel;

import com.example.storagemanager.entities.LoginEntity;

public class LoginViewModel extends ViewModel {

    private final SharedPreferences mPreferences;
    private LoginEntity mLoginEntity;

    public LoginViewModel(SharedPreferences preferences) {
        mPreferences = preferences;
    }

    public boolean authenticate(LoginEntity loginEntity) {
        // TODO authenticate
        return loginEntity.getLogin().equals("login") &&
                loginEntity.getPassword().equals("password");
    }

    public boolean isAuthenticated() {
        if (mLoginEntity == null) {
            String login = mPreferences.getString(LOGIN, null);
            String password = mPreferences.getString(PASSWORD, null);

            if (login == null || password == null)
                return false;

            mLoginEntity = new LoginEntity(login, password);
        }

        return true;
    }

    public void save(LoginEntity loginEntity) {
        SharedPreferences.Editor editor = mPreferences.edit();

        editor.putString(LOGIN, loginEntity.getLogin());
        editor.putString(PASSWORD, loginEntity.getPassword());

        editor.apply();
    }

    public void logout() {
        mPreferences.edit().clear().apply();
        mLoginEntity = null;
    }

    private static final String LOGIN = "LOGIN";
    private static final String PASSWORD = "PASSWORD";
}
