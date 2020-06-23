package com.example.storagemanager.viewmodels.factories;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.storagemanager.viewmodels.LoginViewModel;

public class LoginVMFactory implements ViewModelProvider.Factory {

    private final SharedPreferences mPreferences;

    public LoginVMFactory(final SharedPreferences preferences) {
        mPreferences = preferences;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LoginViewModel(mPreferences);
    }
}

